package me.adneths.burnout.debug;

import me.adneths.burnout.Burnout;
import me.adneths.burnout.inventory.BurnoutGroup;
import me.adneths.burnout.tile.BurnoutableTile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;

public class DrainFuelItem extends Item {

	public DrainFuelItem() {
		super(new Item.Properties().maxStackSize(1).group(BurnoutGroup.burnout));
		this.setRegistryName(Burnout.MODID, "drain_fuel_tool");
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		TileEntity tile = context.getWorld().getTileEntity(context.getPos());
		if(tile != null && tile instanceof BurnoutableTile)
		{
			((BurnoutableTile)tile).setFuel(200);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public String getTranslationKey()
	{
		return String.format("%s:%s", Burnout.MODID, "drain_fuel_tool");
	}
	
}
