package me.adneths.burnout.event;

import me.adneths.burnout.blocks.BurnoutCampfireBlock;
import me.adneths.burnout.init.ModContents;
import me.adneths.burnout.tile.BurnoutableTile;
import me.adneths.burnout.util.FuelDurations;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BlockEvents {
	
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
	{
		BlockState state = event.getPlacedBlock();
		Block block = state.getBlock();
		int fuel = 0;
		if(block.equals(Blocks.TORCH))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.torch.getDefaultState(), 2);
			fuel = FuelDurations.Max.TORCH;
		}
		else if(block.equals(Blocks.WALL_TORCH))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.wallTorch.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, state.get(HorizontalBlock.HORIZONTAL_FACING)), 2);
			fuel = FuelDurations.Max.TORCH;
		}
		else if(block.equals(Blocks.SOUL_TORCH))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.soulTorch.getDefaultState(), 2);
			fuel = FuelDurations.Max.TORCH;
		}
		else if(block.equals(Blocks.SOUL_WALL_TORCH))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.soulWallTorch.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, state.get(HorizontalBlock.HORIZONTAL_FACING)), 2);
			fuel = FuelDurations.Max.TORCH;
		}
		else if(block.equals(Blocks.GLOWSTONE))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.glowstone.getDefaultState(), 2);
			fuel = FuelDurations.Max.GLOWSTONE;
		}
		else if(block.equals(Blocks.SEA_LANTERN))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.seaLantern.getDefaultState(), 2);
			fuel = FuelDurations.Max.SEALANTERN;
		}
		else if(block.equals(Blocks.SHROOMLIGHT))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.shroomLight.getDefaultState(), 2);
			fuel = FuelDurations.Max.SHROOMLIGHT;
		}
		else if(block.equals(Blocks.LANTERN))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.lantern.getDefaultState(), 2);
			fuel = FuelDurations.Max.LANTERN;
		}
		else if(block.equals(Blocks.SOUL_LANTERN))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.soulLantern.getDefaultState(), 2);
			fuel = FuelDurations.Max.LANTERN;
		}
		else if(block.equals(Blocks.JACK_O_LANTERN))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.jackOLantern.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, state.get(HorizontalBlock.HORIZONTAL_FACING)), 2);
			fuel = FuelDurations.Max.TORCH;
		}
		else if(block.equals(Blocks.END_ROD))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.endRod.getDefaultState().with(DirectionalBlock.FACING, state.get(DirectionalBlock.FACING)), 2);
			fuel = FuelDurations.Max.ENDROD;
		}
		else if(block.equals(Blocks.REDSTONE_LAMP))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.redstoneLamp.getDefaultState(), 2);
			fuel = FuelDurations.Max.GLOWSTONE;
		}
		else if(block.equals(Blocks.CAMPFIRE))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.campfire.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, state.get(HorizontalBlock.HORIZONTAL_FACING)), 2);
			fuel = FuelDurations.Max.CAMPFIRE;
		}
		else if(block.equals(Blocks.SOUL_CAMPFIRE))
		{
			event.getWorld().setBlockState(event.getPos(), ModContents.Blocks.soulCampfire.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, state.get(HorizontalBlock.HORIZONTAL_FACING)), 2);
			fuel = FuelDurations.Max.CAMPFIRE;
		}
		if(fuel!=0)
		{
			BurnoutableTile tile = (BurnoutableTile) event.getWorld().getTileEntity(event.getPos());
			tile.setFuel(fuel);
		}
	}
	
	@SubscribeEvent
	public static void lightCampfire(PlayerInteractEvent.RightClickBlock event)
	{
		BlockState state = event.getWorld().getBlockState(event.getPos());
		if(state.getBlock() instanceof BurnoutCampfireBlock)
		{
			if(BurnoutCampfireBlock.canBeLit(state))
			{
				if(!event.getPlayer().isCreative())
					if(event.getItemStack().getItem().equals(Items.FLINT_AND_STEEL))
					{
						event.getItemStack().damageItem(1, event.getPlayer(), player -> player.sendBreakAnimation(event.getHand())); 
						event.getWorld().setBlockState(event.getPos(), state.with(BlockStateProperties.LIT, true), 3);
					}
					else if(event.getItemStack().getItem().equals(Items.FIRE_CHARGE))
					{
						event.getItemStack().shrink(1);
						event.getWorld().setBlockState(event.getPos(), state.with(BlockStateProperties.LIT, true), 3);
					}
			}
		}
	}
	
	@SubscribeEvent
	public static void lightCampfire(ProjectileImpactEvent event)
	{
		EntityType<?> type = event.getEntity().getType();
		if(type.equals(EntityType.SMALL_FIREBALL) || type.equals(EntityType.FIREBALL) || (type.equals(EntityType.ARROW) && event.getEntity().getFireTimer() > 0))
		{
			System.out.println(event.getRayTraceResult().getHitVec());
		}
	}
	
}
