package me.adneths.bottle_bee.init;

import me.adneths.bottle_bee.BottleOfBee;
import me.adneths.bottle_bee.entity.BottleBeeEntity;
import me.adneths.bottle_bee.entity.KillerBeeEntity;
import me.adneths.bottle_bee.tile.KillerBeehiveTile;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BottleOfBee.MODID)
public class ModContents {
	
	public static class Items {
		
		@ObjectHolder("bottle_bee:bottle_of_bee")
		public static final Item BOTTLE_OF_BEE = null;
		
		@ObjectHolder("bottle_bee:bundle_of_bees")
		public static final Item BUNDLE_OF_BEES = null;
		
		@ObjectHolder("bottle_bee:bottle_of_killer_bee")
		public static final Item BOTTLE_OF_KILLER_BEE = null;
		
		@ObjectHolder("bottle_bee:bundle_of_killer_bees")
		public static final Item BUNDLE_OF_KILLER_BEES = null;
		
		@ObjectHolder("bottle_bee:killer_bee_honey_bottle")
		public static final Item KILLER_BEE_HONEY_BOTTLE = null;
		
		@ObjectHolder("bottle_bee:venom")
		public static final Item VENOM = null;
		
	}
	
	public static class Blocks {
		
		@ObjectHolder("bottle_bee:killer_bee_nest")
		public static final Block KILLER_BEE_NEST = null;
		
	}
	
	public static class Entities {

		@ObjectHolder("bottle_bee:bottle_of_bee")
		public static final EntityType<BottleBeeEntity> BOTTLE_OF_BEE = null;
	
		@ObjectHolder("bottle_bee:killer_bee")
		public static final EntityType<KillerBeeEntity> KILLER_BEE = null;
		
	}
	
	public static class TileEntities {

		@ObjectHolder(BottleOfBee.MODID+":tile.kill_bee_hive")
		public static final TileEntityType<KillerBeehiveTile> KILLER_BEEHIVE = null;
		
	}
	
	public static class Effects {
		
		@ObjectHolder("bottle_bee:paralysis")
		public static final Effect PARALYSIS = null;
		
	}
	
	public static class Potions {
		
		@ObjectHolder("bottle_bee:venom")
		public static final Potion VENOM = null;

		@ObjectHolder("bottle_bee:long_venom")
		public static final Potion LONG_VENOM = null;

		@ObjectHolder("bottle_bee:strong_venom")
		public static final Potion STRONG_VENOM = null;
		
	}
	
	public static class Attributes {
		
		@ObjectHolder("bottle_bee:generic.sting_resistance")
		public static final Attribute STING_RESISTANCE = new RangedAttribute("attribute.name.generic.sting_resistance", 0.0, 0.0, 1.0)
				.setRegistryName(BottleOfBee.MODID, "generic.sting_resistance");
		
	}
	
	public static class PointOfInterestTypes {
			
		@ObjectHolder("bottle_bee:killer_bee_nest")
		public static final PointOfInterestType KILLER_BEE_NEST = null;
		
	}
	
}
