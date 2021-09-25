package me.adneths.burnout.init;

import me.adneths.burnout.Burnout;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Burnout.MODID)
public class ModContents {

	public static class Items {

		@ObjectHolder("burnout:torch")
		public static final Item torch = null;
		
	}
	
	public static class Blocks {
	
		@ObjectHolder("burnout:torch")
		public static final Block torch = null;
		@ObjectHolder("burnout:wall_torch")
		public static final Block wallTorch = null;
		@ObjectHolder("burnout:soul_torch")
		public static final Block soulTorch = null;
		@ObjectHolder("burnout:soul_wall_torch")
		public static final Block soulWallTorch = null;
		
	
		@ObjectHolder("burnout:glowstone")
		public static final Block glowstone = null;
		@ObjectHolder("burnout:sea_lantern")
		public static final Block seaLantern = null;
		@ObjectHolder("burnout:shroomlight")
		public static final Block shroomLight = null;
		
		@ObjectHolder("burnout:lantern")
		public static final Block lantern = null;

		@ObjectHolder("burnout:soul_lantern")
		public static final Block soulLantern = null;

		@ObjectHolder("burnout:end_rod")
		public static final Block endRod = null;

		@ObjectHolder("burnout:jack_o_lantern")
		public static final Block jackOLantern = null;
		
		@ObjectHolder("burnout:redstone_lamp")
		public static final Block redstoneLamp = null;
		
		@ObjectHolder("burnout:campfire")
		public static final Block campfire = null;
		
		@ObjectHolder("burnout:soul_campfire")
		public static final Block soulCampfire = null;
		
		public static Block[] allBlocks()
		{
			return new Block[] {torch, wallTorch, soulTorch, soulWallTorch, glowstone, seaLantern, 
					shroomLight, lantern, soulLantern, endRod, jackOLantern, redstoneLamp,
					campfire, soulCampfire};
		}
		
		public static Block[] allBlocksNoCampfire()
		{
			return new Block[] {torch, wallTorch, soulTorch, soulWallTorch, glowstone, seaLantern, 
					shroomLight, lantern, soulLantern, endRod, jackOLantern, redstoneLamp};
		}
	}
	
}
