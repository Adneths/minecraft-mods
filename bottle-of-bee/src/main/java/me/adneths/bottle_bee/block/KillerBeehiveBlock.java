package me.adneths.bottle_bee.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import me.adneths.bottle_bee.init.ModContents;
import me.adneths.bottle_bee.tile.KillerBeehiveTile;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
public class KillerBeehiveBlock extends ContainerBlock {

	private static final Direction[] GENERATE_DIRECTIONS = new Direction[] { Direction.WEST, Direction.EAST,
			Direction.SOUTH };

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

	public static final IntegerProperty HONEY_LEVEL = BlockStateProperties.HONEY_LEVEL;

	public KillerBeehiveBlock(AbstractBlock.Properties properties) {
		super(properties);
		setDefaultState(this.stateContainer.getBaseState().with(HONEY_LEVEL, 0).with(FACING, Direction.NORTH));
	}

	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return (blockState.get(HONEY_LEVEL)).intValue();
	}

	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
			@Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, state, te, stack);
		if (!worldIn.isRemote && te instanceof KillerBeehiveTile) {
			KillerBeehiveTile beehive = (KillerBeehiveTile) te;
			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
				beehive.angerBees(player, state, KillerBeehiveTile.State.EMERGENCY);
				worldIn.updateComparatorOutputLevel(pos, this);
				angerNearbyBees(worldIn, pos);
			}
			CriteriaTriggers.BEE_NEST_DESTROYED.test((ServerPlayerEntity) player, state.getBlock(), stack,
					beehive.getBeeCount());
		}
	}

	private void angerNearbyBees(World world, BlockPos pos) {
		List<BeeEntity> list = world.getEntitiesWithinAABB(BeeEntity.class,
				(new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
		if (!list.isEmpty()) {
			List<PlayerEntity> list1 = world.getEntitiesWithinAABB(PlayerEntity.class,
					(new AxisAlignedBB(pos)).grow(8.0D, 6.0D, 8.0D));
			int i = list1.size();
			for (BeeEntity beeentity : list) {
				if (beeentity.getAttackTarget() == null)
					beeentity.setAttackTarget((LivingEntity) list1.get(world.rand.nextInt(i)));
			}
		}
	}

	public static void dropHoneyComb(World world, BlockPos pos) {
		spawnAsEntity(world, pos, new ItemStack(Items.HONEYCOMB, 6));
	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemstack = player.getHeldItem(handIn);
		int i = state.get(HONEY_LEVEL);
		boolean flag = false;
		if (i >= 5) {
			if (itemstack.getItem() == Items.SHEARS) {
				worldIn.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
						SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				dropHoneyComb(worldIn, pos);
				itemstack.damageItem(1, player, (playerEntity) -> {
					playerEntity.sendBreakAnimation(handIn);
				});
				flag = true;
			} else if (itemstack.getItem() == Items.GLASS_BOTTLE) {
				itemstack.shrink(1);
				worldIn.playSound(player, player.getPosX(), player.getPosY(), player.getPosZ(),
						SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				ItemStack stack;
				if(Math.random() < 0.02)
					stack = new ItemStack(ModContents.Items.VENOM);
				else
					stack = new ItemStack(ModContents.Items.KILLER_BEE_HONEY_BOTTLE);
				if (itemstack.isEmpty()) {
					player.setHeldItem(handIn, stack);
				} else if (!player.inventory
						.addItemStackToInventory(stack)) {
					player.dropItem(stack, false);
				}

				flag = true;
			}
		}

		if (flag) {
			if (!CampfireBlock.isSmokingBlockAt(worldIn, pos)) {
				if (this.hasBees(worldIn, pos)) {
					this.angerNearbyBees(worldIn, pos);
				}

				this.takeHoney(worldIn, state, pos, player, KillerBeehiveTile.State.EMERGENCY);
			} else {
				this.takeHoney(worldIn, state, pos);
			}

			return ActionResultType.func_233537_a_(worldIn.isRemote);
		} else {
			return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		}
	}

	private boolean hasBees(World world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof KillerBeehiveTile) {
			KillerBeehiveTile beehive = (KillerBeehiveTile) tileentity;
			return !beehive.hasNoBees();
		}
		return false;
	}

	public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player,
			KillerBeehiveTile.State tileState) {
		takeHoney(world, state, pos);
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof KillerBeehiveTile) {
			KillerBeehiveTile beehive = (KillerBeehiveTile) tileentity;
			beehive.angerBees(player, state, tileState);
		}
	}

	public void takeHoney(World world, BlockState state, BlockPos pos) {
		world.setBlockState(pos, (BlockState) state.with(HONEY_LEVEL, 0), 3);
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (((Integer) stateIn.get(HONEY_LEVEL)).intValue() >= 5)
			for (int i = 0; i < rand.nextInt(1) + 1; i++)
				addHoneyParticle(worldIn, pos, stateIn);
	}

	@OnlyIn(Dist.CLIENT)
	private void addHoneyParticle(World world, BlockPos pos, BlockState state) {
		if (state.getFluidState().isEmpty() && world.rand.nextFloat() >= 0.3F) {
			VoxelShape voxelshape = state.getCollisionShape((IBlockReader) world, pos);
			double d0 = voxelshape.getEnd(Direction.Axis.Y);
			if (d0 >= 1.0D && !state.isIn((ITag<Block>) BlockTags.IMPERMEABLE)) {
				double d1 = voxelshape.getStart(Direction.Axis.Y);
				if (d1 > 0.0D) {
					addHoneyParticle(world, pos, voxelshape, pos.getY() + d1 - 0.05D);
				} else {
					BlockPos blockpos = pos.down();
					BlockState blockstate = world.getBlockState(blockpos);
					VoxelShape voxelshape1 = blockstate.getCollisionShape((IBlockReader) world, blockpos);
					double d2 = voxelshape1.getEnd(Direction.Axis.Y);
					if ((d2 < 1.0D || !blockstate.hasOpaqueCollisionShape((IBlockReader) world, blockpos))
							&& blockstate.getFluidState().isEmpty())
						addHoneyParticle(world, pos, voxelshape, pos.getY() - 0.05D);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double y) {
		addHoneyParticle(world, pos.getX() + shape.getStart(Direction.Axis.X),
				pos.getX() + shape.getEnd(Direction.Axis.X), pos.getZ() + shape.getStart(Direction.Axis.Z),
				pos.getZ() + shape.getEnd(Direction.Axis.Z), y);
	}

	@OnlyIn(Dist.CLIENT)
	private void addHoneyParticle(World particleData, double x1, double x2, double z1, double z2, double y) {
		particleData.addParticle((IParticleData) ParticleTypes.DRIPPING_HONEY,
				MathHelper.lerp(particleData.rand.nextDouble(), x1, x2), y,
				MathHelper.lerp(particleData.rand.nextDouble(), z1, z2), 0.0D, 0.0D, 0.0D);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return (BlockState) getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(new Property[] { HONEY_LEVEL, FACING });
	}

	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new KillerBeehiveTile();
	}

	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof KillerBeehiveTile) {
				KillerBeehiveTile beehive = (KillerBeehiveTile) tileentity;
				ItemStack itemstack = new ItemStack(this);
				int i = ((Integer) state.get(HONEY_LEVEL)).intValue();
				boolean flag = !beehive.hasNoBees();
				if (!flag && i == 0)
					return;
				if (flag) {
					CompoundNBT compoundnbt = new CompoundNBT();
					compoundnbt.put("Bees", (INBT) beehive.getBees());
					itemstack.setTagInfo("BlockEntityTag", (INBT) compoundnbt);
				}
				CompoundNBT compoundnbt1 = new CompoundNBT();
				compoundnbt1.putInt("honey_level", i);
				itemstack.setTagInfo("BlockStateTag", (INBT) compoundnbt1);
				ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
				itementity.setDefaultPickupDelay();
				worldIn.addEntity((Entity) itementity);
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		Entity entity = (Entity) builder.get(LootParameters.THIS_ENTITY);
		if (entity instanceof net.minecraft.entity.item.TNTEntity
				|| entity instanceof net.minecraft.entity.monster.CreeperEntity
				|| entity instanceof net.minecraft.entity.projectile.WitherSkullEntity
				|| entity instanceof net.minecraft.entity.boss.WitherEntity
				|| entity instanceof net.minecraft.entity.item.minecart.TNTMinecartEntity) {
			TileEntity tileentity = (TileEntity) builder.get(LootParameters.BLOCK_ENTITY);
			if (tileentity instanceof KillerBeehiveTile) {
				KillerBeehiveTile beehive = (KillerBeehiveTile) tileentity;
				beehive.angerBees((PlayerEntity) null, state, KillerBeehiveTile.State.EMERGENCY);
			}
		}
		return super.getDrops(state, builder);
	}

	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		if (worldIn.getBlockState(facingPos).getBlock() instanceof FireBlock) {
			TileEntity tileentity = worldIn.getTileEntity(currentPos);
			if (tileentity instanceof KillerBeehiveTile) {
				KillerBeehiveTile beehive = (KillerBeehiveTile) tileentity;
				beehive.angerBees((PlayerEntity) null, stateIn, KillerBeehiveTile.State.EMERGENCY);
			}
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	public static Direction getGenerationDirection(Random rand) {
		return (Direction) Util.getRandomObject((Object[]) GENERATE_DIRECTIONS, rand);
	}
}
