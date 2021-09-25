package me.adneths.bottle_bee.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import me.adneths.bottle_bee.init.ModContents;
import me.adneths.bottle_bee.tile.KillerBeehiveTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
public class KillerBeeEntity extends AnimalEntity implements IAngerable, IFlyingAnimal {
	private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.createKey(KillerBeeEntity.class,
			DataSerializers.BYTE);

	private static final DataParameter<Integer> ANGER_TIME = EntityDataManager.createKey(KillerBeeEntity.class,
			DataSerializers.VARINT);

	private static final RangedInteger field_234180_bw_ = TickRangeConverter.convertRange(20, 39);

	private UUID lastHurtBy;

	private float rollAmount;

	private float rollAmountO;

	private int timeSinceSting;

	private int ticksWithoutNectarSinceExitingHive;

	private int stayOutOfHiveCountdown;

	private int numCropsGrownSincePollination;

	private int remainingCooldownBeforeLocatingNewHive = 0;

	private int remainingCooldownBeforeLocatingNewFlower = 0;

	@Nullable
	private BlockPos savedFlowerPos = null;

	@Nullable
	private BlockPos hivePos = null;

	private PollinateGoal pollinateGoal;

	private FindBeehiveGoal findBeehiveGoal;

	private FindFlowerGoal findFlowerGoal;

	private int underWaterTicks;

	public KillerBeeEntity(EntityType<? extends KillerBeeEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController((MobEntity)this, 20, true);
		this.lookController = new BeeLookController((MobEntity)this);
		setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
		setPathPriority(PathNodeType.WATER, -1.0F);
		setPathPriority(PathNodeType.WATER_BORDER, 16.0F);
		setPathPriority(PathNodeType.COCOA, -1.0F);
		setPathPriority(PathNodeType.FENCE, -1.0F);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(DATA_FLAGS_ID, (byte) 0);
		this.dataManager.register(ANGER_TIME, 0);
	}

	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new StingGoal((CreatureEntity) this, 1.399999976158142D, true));
		this.goalSelector.addGoal(1, new EnterBeehiveGoal());
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal((CreatureEntity) this, 1.25D, Ingredient.fromTag(ItemTags.FLOWERS), false));
		
		this.pollinateGoal = new PollinateGoal();
		this.goalSelector.addGoal(4, this.pollinateGoal);
		
		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
		this.goalSelector.addGoal(5, new UpdateBeehiveGoal());
		this.findBeehiveGoal = new FindBeehiveGoal();
		this.goalSelector.addGoal(5, this.findBeehiveGoal);
		this.findFlowerGoal = new FindFlowerGoal();
		this.goalSelector.addGoal(6, this.findFlowerGoal);
		this.goalSelector.addGoal(7, new FindPollinationTargetGoal());
		this.goalSelector.addGoal(8, new WanderGoal());
		this.goalSelector.addGoal(9, new SwimGoal(this));
		
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 0, true, false, living -> {return !((PlayerEntity)living).isCreative() && !((PlayerEntity)living).isSpectator();}));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, false, living -> {return !(living instanceof KillerBeeEntity);}));
		this.targetSelector.addGoal(3, (new AngerGoal(this)).setCallsForHelp(new Class[0]));
		this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		if (this.hasHive()) {
			compound.put("HivePos", NBTUtil.writeBlockPos(this.getHivePos()));
		}

		if (this.hasFlower()) {
			compound.put("FlowerPos", NBTUtil.writeBlockPos(this.getFlowerPos()));
		}

		compound.putBoolean("HasNectar", this.hasNectar());
		compound.putBoolean("HasStung", this.hasStung());
		compound.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
		compound.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
		compound.putInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
		this.writeAngerNBT(compound);
	}

	public void readAdditional(CompoundNBT compound) {
		this.hivePos = null;
		if (compound.contains("HivePos")) {
			this.hivePos = NBTUtil.readBlockPos(compound.getCompound("HivePos"));
		}

		this.savedFlowerPos = null;
		if (compound.contains("FlowerPos")) {
			this.savedFlowerPos = NBTUtil.readBlockPos(compound.getCompound("FlowerPos"));
		}

		super.readAdditional(compound);
		this.setHasNectar(compound.getBoolean("HasNectar"));
		this.setHasStung(compound.getBoolean("HasStung"));
		this.ticksWithoutNectarSinceExitingHive = compound.getInt("TicksSincePollination");
		this.stayOutOfHiveCountdown = compound.getInt("CannotEnterHiveTicks");
		this.numCropsGrownSincePollination = compound.getInt("CropsGrownSincePollination");
		this.readAngerNBT((ServerWorld) this.world, compound);
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		if(entityIn instanceof LivingEntity)
		{
			LivingEntity living = (LivingEntity) entityIn;
			if(living.getAttributeManager().hasAttributeInstance(ModContents.Attributes.STING_RESISTANCE))
			{
				double resistance = living.getAttributeValue(ModContents.Attributes.STING_RESISTANCE);
				if(resistance > Math.random())
				{
					for(ItemStack stack : entityIn.getArmorInventoryList())
					{
						stack.damageItem(1, living, entity -> entity.sendBreakAnimation(((ArmorItem)stack.getItem()).getEquipmentSlot()));
					}
					func_241356_K__();
					playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
					return false;
				}
			}
		}
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeBeeStingDamage((LivingEntity) this),
				(int) getAttributeValue(Attributes.ATTACK_DAMAGE));
		if (flag) {
			applyEnchantments((LivingEntity) this, entityIn);
			if (!this.hasStung())
				if (entityIn instanceof LivingEntity) {
					LivingEntity living = (LivingEntity) entityIn;
					living.setBeeStingCount(((LivingEntity) entityIn).getBeeStingCount() + 1);
					int i = 0;
					if (this.world.getDifficulty() == Difficulty.NORMAL) {
						i = 10;
					} else if (this.world.getDifficulty() == Difficulty.HARD) {
						i = 18;
					}

					if (i > 0) {
						living.addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 1));
						living.addPotionEffect(new EffectInstance(Effects.NAUSEA, i * 20, 1));
						living.addPotionEffect(new EffectInstance(Effects.HUNGER, i * 10, 0));
						if(Math.random() < 0.05)
							((LivingEntity) entityIn).addPotionEffect(new EffectInstance(ModContents.Effects.PARALYSIS, i * 400, 0));
					}
				}
			if (entityIn instanceof PlayerEntity)
				this.setHasStung(true);
			func_241356_K__();
			playSound(SoundEvents.ENTITY_BEE_STING, 1.0F, 1.0F);
		}
		return flag;
	}

	public void tick() {
		super.tick();
		if (hasNectar() && getCropsGrownSincePollination() < 10 && this.rand.nextFloat() < 0.05F)
			for (int i = 0; i < this.rand.nextInt(2) + 1; i++)
				addParticle(this.world, getPosX() - 0.30000001192092896D, getPosX() + 0.30000001192092896D,
						getPosZ() - 0.30000001192092896D, getPosZ() + 0.30000001192092896D, getPosYHeight(0.5D),
						(IParticleData) ParticleTypes.FALLING_NECTAR);
		updateBodyPitch();
	}

	private void addParticle(World worldIn, double p_226397_2_, double p_226397_4_, double p_226397_6_,
			double p_226397_8_, double posY, IParticleData particleData) {
		worldIn.addParticle(particleData, MathHelper.lerp(worldIn.rand.nextDouble(), p_226397_2_, p_226397_4_), posY,
				MathHelper.lerp(worldIn.rand.nextDouble(), p_226397_6_, p_226397_8_), 0.0D, 0.0D, 0.0D);
	}

	private void startMovingTo(BlockPos pos) {
		Vector3d vector3d = Vector3d.copyCenteredHorizontally((Vector3i) pos);
		int i = 0;
		BlockPos blockpos = getPosition();
		int j = (int) vector3d.y - blockpos.getY();
		if (j > 2) {
			i = 4;
		} else if (j < -2) {
			i = -4;
		}
		int k = 6;
		int l = 8;
		int i1 = blockpos.manhattanDistance((Vector3i) pos);
		if (i1 < 15) {
			k = i1 / 2;
			l = i1 / 2;
		}
		Vector3d vector3d1 = RandomPositionGenerator.func_226344_b_((CreatureEntity) this, k, l, i, vector3d,
				0.3141592741012573D);
		if (vector3d1 != null) {
			this.navigator.setRangeMultiplier(0.5F);
			this.navigator.tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, 1.0D);
		}
	}

	@Nullable
	public BlockPos getFlowerPos() {
		return this.savedFlowerPos;
	}

	public boolean hasFlower() {
		return (this.savedFlowerPos != null);
	}

	public void setFlowerPos(BlockPos pos) {
		this.savedFlowerPos = pos;
	}

	private boolean failedPollinatingTooLong() {
		return (this.ticksWithoutNectarSinceExitingHive > 3600);
	}

	private boolean canEnterHive() {
		if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isRunning() && !hasStung()
				&& getAttackTarget() == null) {
			boolean flag = (failedPollinatingTooLong() || this.world.isRaining() || this.world.isNightTime()
					|| hasNectar());
			return (flag && !isHiveNearFire());
		}
		return false;
	}

	public void setStayOutOfHiveCountdown(int p_226450_1_) {
		this.stayOutOfHiveCountdown = p_226450_1_;
	}

	@OnlyIn(Dist.CLIENT)
	public float getBodyPitch(float p_226455_1_) {
		return MathHelper.lerp(p_226455_1_, this.rollAmountO, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.rollAmountO = this.rollAmount;
		if (isNearTarget()) {
			this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
		} else {
			this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
		}
	}

	protected void updateAITasks() {
		boolean flag = hasStung();
		if (isInWaterOrBubbleColumn()) {
			this.underWaterTicks++;
		} else {
			this.underWaterTicks = 0;
		}
		if (this.underWaterTicks > 20)
			attackEntityFrom(DamageSource.DROWN, 1.0F);
		if (flag) {
			this.timeSinceSting++;
			if (this.timeSinceSting % 5 == 0
					&& this.rand.nextInt(MathHelper.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0)
				attackEntityFrom(DamageSource.GENERIC, getHealth());
		}
		if (!hasNectar())
			this.ticksWithoutNectarSinceExitingHive++;
		if (!this.world.isRemote)
			func_241359_a_((ServerWorld) this.world, false);
	}

	public void resetTicksWithoutNectar() {
		this.ticksWithoutNectarSinceExitingHive = 0;
	}

	private boolean isHiveNearFire() {
		if (this.hivePos == null)
			return false;
		TileEntity tileentity = this.world.getTileEntity(this.hivePos);
		return (tileentity instanceof KillerBeehiveTile && ((KillerBeehiveTile) tileentity).isNearFire());
	}

	public int getAngerTime() {
		return ((Integer) this.dataManager.get(ANGER_TIME)).intValue();
	}

	public void setAngerTime(int time) {
		this.dataManager.set(ANGER_TIME, Integer.valueOf(time));
	}

	public UUID getAngerTarget() {
		return this.lastHurtBy;
	}

	public void setAngerTarget(@Nullable UUID target) {
		this.lastHurtBy = target;
	}

	public void func_230258_H__() {
		setAngerTime(field_234180_bw_.getRandomWithinRange(this.rand));
	}

	private boolean doesHiveHaveSpace(BlockPos pos) {
		TileEntity tileentity = this.world.getTileEntity(pos);
		if (tileentity instanceof KillerBeehiveTile)
			return !((KillerBeehiveTile) tileentity).isFullOfBees();
		return false;
	}

	public boolean hasHive() {
		return (this.hivePos != null);
	}

	@Nullable
	public BlockPos getHivePos() {
		return this.hivePos;
	}

	private int getCropsGrownSincePollination() {
		return this.numCropsGrownSincePollination;
	}

	private void resetCropCounter() {
		this.numCropsGrownSincePollination = 0;
	}

	private void addCropCounter() {
		this.numCropsGrownSincePollination++;
	}

	public void livingTick() {
		super.livingTick();
		if (!this.world.isRemote) {
			if (this.stayOutOfHiveCountdown > 0)
				this.stayOutOfHiveCountdown--;
			if (this.remainingCooldownBeforeLocatingNewHive > 0)
				this.remainingCooldownBeforeLocatingNewHive--;
			if (this.remainingCooldownBeforeLocatingNewFlower > 0)
				this.remainingCooldownBeforeLocatingNewFlower--;
			boolean flag = (func_233678_J__() && !hasStung() && getAttackTarget() != null
					&& getAttackTarget().getDistanceSq((Entity) this) < 4.0D);
			setNearTarget(flag);
			if (this.ticksExisted % 20 == 0 && !isHiveValid())
				this.hivePos = null;
		}
	}

	private boolean isHiveValid() {
		if (!hasHive())
			return false;
		TileEntity tileentity = this.world.getTileEntity(this.hivePos);
		return tileentity instanceof KillerBeehiveTile;
	}

	public boolean hasNectar() {
		return getBeeFlag(8);
	}

	private void setHasNectar(boolean p_226447_1_) {
		if (p_226447_1_)
			resetTicksWithoutNectar();
		setBeeFlag(8, p_226447_1_);
	}

	public boolean hasStung() {
		return getBeeFlag(4);
	}

	private void setHasStung(boolean p_226449_1_) {
		setBeeFlag(4, p_226449_1_);
	}

	private boolean isNearTarget() {
		return getBeeFlag(2);
	}

	private void setNearTarget(boolean p_226452_1_) {
		setBeeFlag(2, p_226452_1_);
	}

	private boolean isTooFar(BlockPos pos) {
		return !isWithinDistance(pos, 32);
	}

	private void setBeeFlag(int flagId, boolean p_226404_2_) {
		if (p_226404_2_) {
			this.dataManager.set(DATA_FLAGS_ID,
					Byte.valueOf((byte) (((Byte) this.dataManager.get(DATA_FLAGS_ID)).byteValue() | flagId)));
		} else {
			this.dataManager.set(DATA_FLAGS_ID, Byte.valueOf(
					(byte) (((Byte) this.dataManager.get(DATA_FLAGS_ID)).byteValue() & (flagId ^ 0xFFFFFFFF))));
		}
	}

	private boolean getBeeFlag(int flagId) {
		return ((((Byte) this.dataManager.get(DATA_FLAGS_ID)).byteValue() & flagId) != 0);
	}

	public static AttributeModifierMap.MutableAttribute func_234182_eX_() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D)
				.createMutableAttribute(Attributes.FLYING_SPEED, 3.00000003576278685D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.30000001788139344D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D);
	}

	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator((MobEntity) this, worldIn) {
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}

			public void tick() {
				if (!KillerBeeEntity.this.pollinateGoal.isRunning())
					super.tick();
			}
		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return (PathNavigator) flyingpathnavigator;
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem().isIn(ItemTags.FLOWERS);
	}

	private boolean isFlowers(BlockPos pos) {
		return (this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(BlockTags.FLOWERS));
	}

	protected void playStepSound(BlockPos pos, BlockState blockIn) {
	}

	protected SoundEvent getAmbientSound() {
		return null;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public KillerBeeEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		return (KillerBeeEntity) ModContents.Entities.KILLER_BEE.create((World) p_241840_1_);
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return isChild() ? (sizeIn.height * 0.5F) : (sizeIn.height * 0.5F);
	}

	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	protected boolean makeFlySound() {
		return true;
	}

	public void onHoneyDelivered() {
		setHasNectar(false);
		resetCropCounter();
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isInvulnerableTo(source))
			return false;
		if (!this.world.isRemote)
			this.pollinateGoal.cancel();
		return super.attackEntityFrom(source, amount);
	}

	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	protected void handleFluidJump(ITag<Fluid> fluidTag) {
		setMotion(getMotion().add(0.0D, 0.01D, 0.0D));
	}

	@OnlyIn(Dist.CLIENT)
	public Vector3d func_241205_ce_() {
		return new Vector3d(0.0D, (0.5F * getEyeHeight()), (getWidth() * 0.2F));
	}

	private boolean isWithinDistance(BlockPos pos, int distance) {
		return pos.withinDistance((Vector3i) getPosition(), distance);
	}

	class AngerGoal extends HurtByTargetGoal {
		AngerGoal(KillerBeeEntity beeIn) {
			super((CreatureEntity) beeIn, new Class[0]);
		}

		public boolean shouldContinueExecuting() {
			return (KillerBeeEntity.this.func_233678_J__() && super.shouldContinueExecuting());
		}

		protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
			if (mobIn instanceof KillerBeeEntity && this.goalOwner.canEntityBeSeen((Entity) targetIn))
				mobIn.setAttackTarget(targetIn);
		}
	}

	class BeeLookController extends LookController {
		BeeLookController(MobEntity beeIn) {
			super(beeIn);
		}

		public void tick() {
			if (!KillerBeeEntity.this.func_233678_J__())
				super.tick();
		}

		protected boolean shouldResetPitch() {
			return !KillerBeeEntity.this.pollinateGoal.isRunning();
		}
	}

	class EnterBeehiveGoal extends PassiveGoal {
		private EnterBeehiveGoal() {
		}

		public boolean canBeeStart() {
			if (KillerBeeEntity.this.hasHive() && KillerBeeEntity.this.canEnterHive() && KillerBeeEntity.this.hivePos
					.withinDistance(KillerBeeEntity.this.getPositionVec(), 2.0D)) {
				TileEntity tileentity = KillerBeeEntity.this.world.getTileEntity(KillerBeeEntity.this.hivePos);
				if (tileentity instanceof KillerBeehiveTile) {
					KillerBeehiveTile beehive = (KillerBeehiveTile) tileentity;
					if (!beehive.isFullOfBees())
						return true;
					KillerBeeEntity.this.hivePos = null;
				}
			}
			return false;
		}

		public boolean canBeeContinue() {
			return false;
		}

		public void startExecuting() {
			TileEntity tileentity = KillerBeeEntity.this.world.getTileEntity(KillerBeeEntity.this.hivePos);
			if (tileentity instanceof KillerBeehiveTile) {
				KillerBeehiveTile KillerBeehiveTile = (KillerBeehiveTile) tileentity;
				KillerBeehiveTile.tryEnterHive((Entity) KillerBeeEntity.this, KillerBeeEntity.this.hasNectar());
			}
		}
	}

	public class FindBeehiveGoal extends PassiveGoal {
		private int ticks = KillerBeeEntity.this.world.rand.nextInt(10);

		private List<BlockPos> possibleHives = Lists.newArrayList();

		@Nullable
		private Path path = null;

		private int field_234183_f_;

		FindBeehiveGoal() {
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canBeeStart() {
			return (KillerBeeEntity.this.hivePos != null && !KillerBeeEntity.this.detachHome()
					&& KillerBeeEntity.this.canEnterHive() && !isCloseEnough(KillerBeeEntity.this.hivePos)
					&& KillerBeeEntity.this.world.getBlockState(KillerBeeEntity.this.hivePos).getBlock().equals(ModContents.Blocks.KILLER_BEE_NEST));
		}

		public boolean canBeeContinue() {
			return canBeeStart();
		}

		public void startExecuting() {
			this.ticks = 0;
			this.field_234183_f_ = 0;
			super.startExecuting();
		}

		public void resetTask() {
			this.ticks = 0;
			this.field_234183_f_ = 0;
			KillerBeeEntity.this.navigator.clearPath();
			KillerBeeEntity.this.navigator.resetRangeMultiplier();
		}

		public void tick() {
			if (KillerBeeEntity.this.hivePos != null) {
				this.ticks++;
				if (this.ticks > 600) {
					makeChosenHivePossibleHive();
				} else if (!KillerBeeEntity.this.navigator.hasPath()) {
					if (!KillerBeeEntity.this.isWithinDistance(KillerBeeEntity.this.hivePos, 16)) {
						if (KillerBeeEntity.this.isTooFar(KillerBeeEntity.this.hivePos)) {
							reset();
						} else {
							KillerBeeEntity.this.startMovingTo(KillerBeeEntity.this.hivePos);
						}
					} else {
						boolean flag = startMovingToFar(KillerBeeEntity.this.hivePos);
						if (!flag) {
							makeChosenHivePossibleHive();
						} else if (this.path != null
								&& KillerBeeEntity.this.navigator.getPath().isSamePath(this.path)) {
							this.field_234183_f_++;
							if (this.field_234183_f_ > 60) {
								reset();
								this.field_234183_f_ = 0;
							}
						} else {
							this.path = KillerBeeEntity.this.navigator.getPath();
						}
					}
				}
			}
		}

		private boolean startMovingToFar(BlockPos pos) {
			KillerBeeEntity.this.navigator.setRangeMultiplier(10.0F);
			KillerBeeEntity.this.navigator.tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
			return (KillerBeeEntity.this.navigator.getPath() != null
					&& KillerBeeEntity.this.navigator.getPath().reachesTarget());
		}

		private boolean isPossibleHive(BlockPos pos) {
			return this.possibleHives.contains(pos);
		}

		private void addPossibleHives(BlockPos pos) {
			this.possibleHives.add(pos);
			while (this.possibleHives.size() > 3)
				this.possibleHives.remove(0);
		}

		private void clearPossibleHives() {
			this.possibleHives.clear();
		}

		private void makeChosenHivePossibleHive() {
			if (KillerBeeEntity.this.hivePos != null)
				addPossibleHives(KillerBeeEntity.this.hivePos);
			reset();
		}

		private void reset() {
			KillerBeeEntity.this.hivePos = null;
			KillerBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 200;
		}

		private boolean isCloseEnough(BlockPos pos) {
			if (KillerBeeEntity.this.isWithinDistance(pos, 2))
				return true;
			Path path = KillerBeeEntity.this.navigator.getPath();
			return (path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished());
		}
	}

	public class FindFlowerGoal extends PassiveGoal {
		private int ticks = KillerBeeEntity.this.world.rand.nextInt(10);

		FindFlowerGoal() {
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canBeeStart() {
			return (KillerBeeEntity.this.savedFlowerPos != null && !KillerBeeEntity.this.detachHome()
					&& shouldMoveToFlower() && KillerBeeEntity.this.isFlowers(KillerBeeEntity.this.savedFlowerPos)
					&& !KillerBeeEntity.this.isWithinDistance(KillerBeeEntity.this.savedFlowerPos, 2));
		}

		public boolean canBeeContinue() {
			return canBeeStart();
		}

		public void startExecuting() {
			this.ticks = 0;
			super.startExecuting();
		}

		public void resetTask() {
			this.ticks = 0;
			KillerBeeEntity.this.navigator.clearPath();
			KillerBeeEntity.this.navigator.resetRangeMultiplier();
		}

		public void tick() {
			if (KillerBeeEntity.this.savedFlowerPos != null) {
				this.ticks++;
				if (this.ticks > 600) {
					KillerBeeEntity.this.savedFlowerPos = null;
				} else if (!KillerBeeEntity.this.navigator.hasPath()) {
					if (KillerBeeEntity.this.isTooFar(KillerBeeEntity.this.savedFlowerPos)) {
						KillerBeeEntity.this.savedFlowerPos = null;
					} else {
						KillerBeeEntity.this.startMovingTo(KillerBeeEntity.this.savedFlowerPos);
					}
				}
			}
		}

		private boolean shouldMoveToFlower() {
			return (KillerBeeEntity.this.ticksWithoutNectarSinceExitingHive > 2400);
		}
	}

	class FindPollinationTargetGoal extends PassiveGoal {
		private FindPollinationTargetGoal() {
		}

		public boolean canBeeStart() {
			if (KillerBeeEntity.this.getCropsGrownSincePollination() >= 10)
				return false;
			if (KillerBeeEntity.this.rand.nextFloat() < 0.3F)
				return false;
			return (KillerBeeEntity.this.hasNectar() && KillerBeeEntity.this.isHiveValid());
		}

		public boolean canBeeContinue() {
			return canBeeStart();
		}

		public void tick() {
			if (KillerBeeEntity.this.rand.nextInt(30) == 0)
				for (int i = 1; i <= 2; i++) {
					BlockPos blockpos = KillerBeeEntity.this.getPosition().down(i);
					BlockState blockstate = KillerBeeEntity.this.world.getBlockState(blockpos);
					Block block = blockstate.getBlock();
					boolean flag = false;
					IntegerProperty integerproperty = null;
					if (block.isIn(BlockTags.BEE_GROWABLES)) {
						if (block instanceof CropsBlock) {
							CropsBlock cropsblock = (CropsBlock) block;
							if (!cropsblock.isMaxAge(blockstate)) {
								flag = true;
								integerproperty = cropsblock.getAgeProperty();
							}
						} else if (block instanceof StemBlock) {
							int j = ((Integer) blockstate.get(StemBlock.AGE)).intValue();
							if (j < 7) {
								flag = true;
								integerproperty = StemBlock.AGE;
							}
						} else if (block == Blocks.SWEET_BERRY_BUSH) {
							int k = ((Integer) blockstate.get(SweetBerryBushBlock.AGE)).intValue();
							if (k < 3) {
								flag = true;
								integerproperty = SweetBerryBushBlock.AGE;
							}
						}
						if (flag) {
							KillerBeeEntity.this.world.playEvent(2005, blockpos, 0);
							KillerBeeEntity.this.world.setBlockState(blockpos,
									(BlockState) blockstate.with(integerproperty,
											Integer.valueOf((blockstate.get(integerproperty)).intValue() + 1)));
							KillerBeeEntity.this.addCropCounter();
						}
					}
				}
		}
	}

	abstract class PassiveGoal extends Goal {
		private PassiveGoal() {
		}

		public boolean shouldExecute() {
			return (canBeeStart() && !KillerBeeEntity.this.func_233678_J__());
		}

		public boolean shouldContinueExecuting() {
			return (canBeeContinue() && !KillerBeeEntity.this.func_233678_J__());
		}

		public abstract boolean canBeeStart();

		public abstract boolean canBeeContinue();
	}

	class PollinateGoal extends PassiveGoal {
		private final Predicate<BlockState> flowerPredicate;

		private int pollinationTicks;

		private int lastPollinationTick;

		private boolean running;

		private Vector3d nextTarget;

		private int ticks;

		PollinateGoal() {
			this.flowerPredicate = (p_226499_0_ -> p_226499_0_.isIn(BlockTags.TALL_FLOWERS)
					? (p_226499_0_.isIn(Blocks.SUNFLOWER)
							? ((p_226499_0_.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER))
							: true)
					: p_226499_0_.isIn(BlockTags.SMALL_FLOWERS));
			this.pollinationTicks = 0;
			this.lastPollinationTick = 0;
			this.ticks = 0;
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean canBeeStart() {
			if (KillerBeeEntity.this.remainingCooldownBeforeLocatingNewFlower > 0)
				return false;
			if (KillerBeeEntity.this.hasNectar())
				return false;
			if (KillerBeeEntity.this.world.isRaining())
				return false;
			if (KillerBeeEntity.this.rand.nextFloat() < 0.7F)
				return false;
			Optional<BlockPos> optional = getFlower();
			if (optional.isPresent()) {
				KillerBeeEntity.this.savedFlowerPos = optional.get();
				KillerBeeEntity.this.navigator.tryMoveToXYZ(KillerBeeEntity.this.savedFlowerPos.getX() + 0.5D,
						KillerBeeEntity.this.savedFlowerPos.getY() + 0.5D,
						KillerBeeEntity.this.savedFlowerPos.getZ() + 0.5D, 1.2000000476837158D);
				return true;
			}
			return false;
		}

		public boolean canBeeContinue() {
			if (!this.running)
				return false;
			if (!KillerBeeEntity.this.hasFlower())
				return false;
			if (KillerBeeEntity.this.world.isRaining())
				return false;
			if (completedPollination())
				return (KillerBeeEntity.this.rand.nextFloat() < 0.2F);
			if (KillerBeeEntity.this.ticksExisted % 20 == 0
					&& !KillerBeeEntity.this.isFlowers(KillerBeeEntity.this.savedFlowerPos)) {
				KillerBeeEntity.this.savedFlowerPos = null;
				return false;
			}
			return true;
		}

		private boolean completedPollination() {
			return (this.pollinationTicks > 400);
		}

		private boolean isRunning() {
			return this.running;
		}

		private void cancel() {
			this.running = false;
		}

		public void startExecuting() {
			this.pollinationTicks = 0;
			this.ticks = 0;
			this.lastPollinationTick = 0;
			this.running = true;
			KillerBeeEntity.this.resetTicksWithoutNectar();
		}

		public void resetTask() {
			if (completedPollination())
				KillerBeeEntity.this.setHasNectar(true);
			this.running = false;
			KillerBeeEntity.this.navigator.clearPath();
			KillerBeeEntity.this.remainingCooldownBeforeLocatingNewFlower = 200;
		}

		public void tick() {
			this.ticks++;
			if (this.ticks > 600) {
				KillerBeeEntity.this.savedFlowerPos = null;
			} else {
				Vector3d vector3d = Vector3d.copyCenteredHorizontally((Vector3i) KillerBeeEntity.this.savedFlowerPos)
						.add(0.0D, 0.6000000238418579D, 0.0D);
				if (vector3d.distanceTo(KillerBeeEntity.this.getPositionVec()) > 1.0D) {
					this.nextTarget = vector3d;
					moveToNextTarget();
				} else {
					if (this.nextTarget == null)
						this.nextTarget = vector3d;
					boolean flag = (KillerBeeEntity.this.getPositionVec().distanceTo(this.nextTarget) <= 0.1D);
					boolean flag1 = true;
					if (!flag && this.ticks > 600) {
						KillerBeeEntity.this.savedFlowerPos = null;
					} else {
						if (flag) {
							boolean flag2 = (KillerBeeEntity.this.rand.nextInt(25) == 0);
							if (flag2) {
								this.nextTarget = new Vector3d(vector3d.getX() + getRandomOffset(), vector3d.getY(),
										vector3d.getZ() + getRandomOffset());
								KillerBeeEntity.this.navigator.clearPath();
							} else {
								flag1 = false;
							}
							KillerBeeEntity.this.getLookController().setLookPosition(vector3d.getX(), vector3d.getY(),
									vector3d.getZ());
						}
						if (flag1)
							moveToNextTarget();
						this.pollinationTicks++;
						if (KillerBeeEntity.this.rand.nextFloat() < 0.05F
								&& this.pollinationTicks > this.lastPollinationTick + 60) {
							this.lastPollinationTick = this.pollinationTicks;
							KillerBeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
						}
					}
				}
			}
		}

		private void moveToNextTarget() {
			KillerBeeEntity.this.getMoveHelper().setMoveTo(this.nextTarget.getX(), this.nextTarget.getY(),
					this.nextTarget.getZ(), 0.3499999940395355D);
		}

		private float getRandomOffset() {
			return (KillerBeeEntity.this.rand.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
		}

		private Optional<BlockPos> getFlower() {
			return findFlower(this.flowerPredicate, 5.0D);
		}

		private Optional<BlockPos> findFlower(Predicate<BlockState> p_226500_1_, double distance) {
			BlockPos blockpos = KillerBeeEntity.this.getPosition();
			BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
			int i;
			for (i = 0; i <= distance; i = (i > 0) ? -i : (1 - i)) {
				for (int j = 0; j < distance; j++) {
					int k;
					for (k = 0; k <= j; k = (k > 0) ? -k : (1 - k)) {
						int l;
						for (l = (k < j && k > -j) ? j : 0; l <= j; l = (l > 0) ? -l : (1 - l)) {
							blockpos$mutable.setAndOffset((Vector3i) blockpos, k, i - 1, l);
							if (blockpos.withinDistance((Vector3i) blockpos$mutable, distance) && p_226500_1_
									.test(KillerBeeEntity.this.world.getBlockState((BlockPos) blockpos$mutable)))
								return Optional.of(blockpos$mutable);
						}
					}
				}
			}
			return Optional.empty();
		}
	}

	class StingGoal extends MeleeAttackGoal {
		StingGoal(CreatureEntity creatureIn, double speedIn, boolean useLongMemory) {
			super(creatureIn, speedIn, useLongMemory);
		}

		public boolean shouldExecute() {
			return (super.shouldExecute() && KillerBeeEntity.this.func_233678_J__());
		}

		public boolean shouldContinueExecuting() {
			return (super.shouldContinueExecuting() && KillerBeeEntity.this.func_233678_J__());
		}
	}

	class UpdateBeehiveGoal extends PassiveGoal {
		private UpdateBeehiveGoal() {
		}

		public boolean canBeeStart() {
			return (KillerBeeEntity.this.remainingCooldownBeforeLocatingNewHive == 0 && !KillerBeeEntity.this.hasHive()
					&& KillerBeeEntity.this.canEnterHive());
		}

		public boolean canBeeContinue() {
			return false;
		}

		public void startExecuting() {
			KillerBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 200;
			List<BlockPos> list = getNearbyFreeHives();
			if (!list.isEmpty()) {
				for (BlockPos blockpos : list) {
					if (!KillerBeeEntity.this.findBeehiveGoal.isPossibleHive(blockpos)) {
						KillerBeeEntity.this.hivePos = blockpos;
						return;
					}
				}
				KillerBeeEntity.this.findBeehiveGoal.clearPossibleHives();
				KillerBeeEntity.this.hivePos = list.get(0);
			}
		}

		private List<BlockPos> getNearbyFreeHives() {
			BlockPos blockpos = KillerBeeEntity.this.getPosition();
			PointOfInterestManager pointofinterestmanager = ((ServerWorld) KillerBeeEntity.this.world)
					.getPointOfInterestManager();
			Stream<PointOfInterest> stream = pointofinterestmanager.func_219146_b(
					poi -> poi.equals(ModContents.PointOfInterestTypes.KILLER_BEE_NEST),
					blockpos, 20, PointOfInterestManager.Status.ANY);
			return (List<BlockPos>) stream.map(PointOfInterest::getPos)
					.filter(pos -> KillerBeeEntity.this.doesHiveHaveSpace(pos))
					.sorted(Comparator.comparingDouble(pos -> pos.distanceSq((Vector3i) blockpos)))
					.collect(Collectors.toList());
		}
	}

	class WanderGoal extends Goal {
		WanderGoal() {
			setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean shouldExecute() {
			return (KillerBeeEntity.this.navigator.noPath() && KillerBeeEntity.this.rand.nextInt(10) == 0);
		}

		public boolean shouldContinueExecuting() {
			return KillerBeeEntity.this.navigator.hasPath();
		}

		public void startExecuting() {
			Vector3d vector3d = getRandomLocation();
			if (vector3d != null)
				KillerBeeEntity.this.navigator
						.setPath(KillerBeeEntity.this.navigator.getPathToPos(new BlockPos(vector3d), 1), 1.0D);
		}

		@Nullable
		private Vector3d getRandomLocation() {
			Vector3d vector3d;
			if (KillerBeeEntity.this.isHiveValid()
					&& !KillerBeeEntity.this.isWithinDistance(KillerBeeEntity.this.hivePos, 22)) {
				Vector3d vector3d1 = Vector3d.copyCentered((Vector3i) KillerBeeEntity.this.hivePos);
				vector3d = vector3d1.subtract(KillerBeeEntity.this.getPositionVec()).normalize();
			} else {
				vector3d = KillerBeeEntity.this.getLook(0.0F);
			}
			Vector3d vector3d2 = RandomPositionGenerator.findAirTarget((CreatureEntity) KillerBeeEntity.this, 8, 7,
					vector3d, 1.5707964F, 2, 1);
			return (vector3d2 != null) ? vector3d2
					: RandomPositionGenerator.findGroundTarget((CreatureEntity) KillerBeeEntity.this, 8, 4, -2,
							vector3d, 1.5707963705062866D);
		}
	}
}
