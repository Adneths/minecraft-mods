package me.adneths.burnout.init;

import me.adneths.burnout.Burnout;
import me.adneths.burnout.tile.BurnoutCampfireTile;
import me.adneths.burnout.tile.BurnoutableTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Burnout.MODID)
public class ModTileEntity {

	@ObjectHolder(Burnout.MODID+":tile.burnoutable")
	public static final TileEntityType<BurnoutableTile> BURNOUTABLE = null;
	
	@ObjectHolder(Burnout.MODID+":tile.burnout_campfire")
	public static final TileEntityType<BurnoutCampfireTile> CAMPFIRE = null;
	
}
