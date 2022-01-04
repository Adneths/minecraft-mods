package me.adneths.advdragonfight.event;

import java.util.List;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.block.ModBlocks;
import me.adneths.advdragonfight.capability.DragonDifficultyProvider;
import me.adneths.advdragonfight.capability.IDragonDifficulty;
import me.adneths.advdragonfight.entity.EntityBabyDragonBase;
import me.adneths.advdragonfight.entity.EntityDivingBabyDragon;
import me.adneths.advdragonfight.entity.EntityDivingBabyDragon.AttackPhase;
import me.adneths.advdragonfight.entity.EntityHealthedEnderCrystal;
import me.adneths.advdragonfight.entity.EntityScale;
import me.adneths.advdragonfight.entity.EntityScaledDragon;
import me.adneths.advdragonfight.item.ModItems;
import me.adneths.advdragonfight.potion.ModPotions;
import me.adneths.advdragonfight.world.end.AdvDragonFightManager;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.phase.PhaseList;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = AdvDragonFight.MODID)
public class DragonFightListener
{

	@SubscribeEvent
	public static void onDragonDamamge(LivingDamageEvent e)
	{
		if (e.getEntityLiving() instanceof EntityBabyDragonBase)
		{
			if (e.getSource().getTrueSource() instanceof EntityDragon)
				e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onScaleClick(PlayerInteractEvent.EntityInteract e)
	{
		if (!e.getTarget().isDead && e.getTarget() instanceof EntityScale
				&& e.getEntityPlayer().getHeldItem(e.getHand()).isEmpty())
		{
			e.getEntityPlayer().playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F,
					(float) (1.2F / (Math.random() * 0.2F + 0.9F)));
			e.getEntityPlayer().inventory.addItemStackToInventory(new ItemStack(ModItems.dragonScale));
			e.getTarget().setDead();
		}
	}

	@SubscribeEvent
	public static void onFireBreak(BlockEvent.BreakEvent e)
	{
		if (e.getState().getBlock() == ModBlocks.dragonFire && e.getPlayer() != null)
		{
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onDismountBabyDragon(EntityMountEvent e)
	{
		if (e.isDismounting() && e.getEntityBeingMounted() instanceof EntityDivingBabyDragon)
			if (!e.getEntityBeingMounted().isDead
					&& ((EntityDivingBabyDragon) e.getEntityBeingMounted()).isPhase(AttackPhase.CARRY))
			{
				e.setCanceled(true);
			}
	}

	@SubscribeEvent
	public static void onEnderPearl(EnderTeleportEvent e)
	{
		if (e.getEntityLiving() instanceof EntityPlayer)
		{
			if (e.getEntityLiving().world.provider.getDimensionType().equals(AdvDragonFight.ADV_THE_END))
			{
				AxisAlignedBB aabb = new AxisAlignedBB(e.getTargetX() - 24, e.getTargetY() - 24, e.getTargetZ() - 24,
						e.getTargetX() + 24, e.getTargetY() + 24, e.getTargetZ() + 24);
				for (EntityEnderman enderman : e.getEntityLiving().world.getEntitiesWithinAABB(EntityEnderman.class,
						aabb))
					enderman.setRevengeTarget(e.getEntityLiving());
			}
		}
	}

	@SubscribeEvent
	public static void onMobJoin(EntityJoinWorldEvent e)
	{
		if (e.getEntity() instanceof EntityEnderCrystal && !(e.getEntity() instanceof EntityHealthedEnderCrystal)
				&& e.getWorld().provider.getDimensionType().equals(AdvDragonFight.ADV_THE_END))
		{
			if (!e.getWorld().isRemote)
			{
				e.getWorld().spawnEntity(new EntityHealthedEnderCrystal(e.getWorld(), e.getEntity().posX,
						e.getEntity().posY, e.getEntity().posZ));
			}
			e.setCanceled(true);
		}
		else if (e.getEntity() instanceof EntityDragon && !(e.getEntity() instanceof EntityScaledDragon))
		{
			if (!e.getWorld().isRemote)
			{
				EntityScaledDragon dragon = new EntityScaledDragon(e.getWorld(), 0, 128, 0, 1, 1);
				AdvDragonFightManager manager = (AdvDragonFightManager) dragon.getFightManager();
				int sumDiff = 0;
				float pc = 0;
				for (EntityPlayer p : e.getWorld().playerEntities)
				{
					if (p.getPositionVector().squareDistanceTo(0, 0, 0) < 160000
							&& p.world.provider.getDimension() == 1)
					{
						IDragonDifficulty diff = p.getCapability(DragonDifficultyProvider.dragonDifficulty, null);
						if (diff != null)
							sumDiff += diff.getDifficulty();
						pc++;
						manager.addFightingPlayer(p);
						p.addPotionEffect(new PotionEffect(ModPotions.curse, 10000000));
					}
				}
				if (pc != 0)
				{
					dragon.setSumDifficulty(sumDiff);
					dragon.setAverageDifficulty(sumDiff / pc);
					dragon.updateDifficulty();

					dragon.setPosition(0, 128, 0);
					dragon.getPhaseManager().setPhase(PhaseList.HOLDING_PATTERN);
					e.getWorld().spawnEntity(dragon);
					for (EntityHealthedEnderCrystal c : e.getWorld().getEntitiesWithinAABB(
							EntityHealthedEnderCrystal.class, new AxisAlignedBB(-100, 0, -100, 100, 256, 100)))
					{
						c.setSumDifficulty(sumDiff);
						c.setAverageDifficulty(sumDiff / pc);
						c.updateDifficulty();
					}
					ObfuscationReflectionHelper.setPrivateValue(DragonFightManager.class, dragon.getFightManager(),
							dragon.getUniqueID(), "field_186119_m");
					dragon.getFightManager().dragonUpdate(dragon);
					dragon.setUniqueId(e.getEntity().getUniqueID());
				}
			}
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onTick(TickEvent.PlayerTickEvent e)
	{
		if (!e.player.world.isRemote)
			if (e.player.world.provider.getDimension() == 1)
			{
				if (e.player.ticksExisted % 20 == 0 && e.phase == TickEvent.Phase.END)
				{
					Vec3d vec = e.player.getPositionVector();
					double dist = vec.x * vec.x + vec.z + vec.z;
					if (dist > 160000 && dist < 250000)
					{
						vec = vec.normalize().scale(-40);
						e.player.motionX = vec.x;
						e.player.motionZ = vec.z;
					}
					else if (dist > 250000 && dist < 360000)
					{
						vec = vec.normalize().scale(40);
						e.player.motionX = vec.x;
						e.player.motionZ = vec.z;
					}
					else
					{
						List<EntityScaledDragon> list = e.player.world.getEntities(EntityScaledDragon.class,
								(dragon) -> {
									return dragon.getPositionVector().squareDistanceTo(0, 0, 0) < 160000;
								});
						if (!list.isEmpty())
						{
							AdvDragonFightManager manager = (AdvDragonFightManager) list.get(0).getFightManager();
							if (manager.noFightingPlayersLeft())
							{
								manager.despawnDragon(list.get(0));
								list.get(0).setDead();
							}
							else
							{
								if (!manager.containsFightingPlayer(e.player)
										&& e.player.getPositionVector().squareDistanceTo(0, 0, 0) < 160000)
									e.player.changeDimension(1);
							}
						}
					}
				}
			}
	}

	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent e)
	{
		if (e.getEntityLiving().isPotionActive(ModPotions.intimidate))
			e.getEntityLiving().motionY /= 3;
	}

	@SubscribeEvent
	public static void onDamage(LivingDamageEvent e)
	{
		if (e.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			if (p.dimension == 1 && !p.isPotionActive(ModPotions.curse))
			{
				AdvDragonFightManager manager = (AdvDragonFightManager) ((WorldProviderEnd) p.world.provider)
						.getDragonFightManager();
				if (manager.containsFightingPlayer(p))
					e.setAmount(e.getAmount() * 1.5f);
			}
		}
		else
		{
			if (e.getSource().getTrueSource() instanceof EntityPlayer)
			{
				EntityPlayer p = (EntityPlayer) e.getSource().getTrueSource();
				if (p.isPotionActive(ModPotions.curse))
				{
					e.setAmount(e.getAmount() / 2);
				}
			}
		}
	}

}
