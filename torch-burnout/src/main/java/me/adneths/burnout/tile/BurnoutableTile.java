package me.adneths.burnout.tile;

import me.adneths.burnout.Burnout;
import me.adneths.burnout.init.ModTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class BurnoutableTile extends TileEntity implements ITickableTileEntity {

	private int fuel;

	protected static final Property<Boolean> LIT = BlockStateProperties.LIT;
	
	public BurnoutableTile()
	{
		this(ModTileEntity.BURNOUTABLE);
		fuel = 0;
	}
	
	public BurnoutableTile(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		this.fuel = 0;
	}
	
	public void setFuel(int fuel)
	{
		this.fuel = fuel;
		try {
			this.getWorld().setBlockState(this.getPos(), this.getBlockState().with(LIT, true));
		}
		catch (NullPointerException e)
		{	}
	}
	
	public void addFuel(int fuel, int max)
	{
		this.fuel = Math.min(this.fuel + fuel, max);
		try {
			this.getWorld().setBlockState(this.getPos(), this.getBlockState().with(LIT, true));
		}
		catch (NullPointerException e)
		{	}
	}
	
	public int getFuel()
	{
		return fuel;
	}

	@Override
	public void tick() {
		if(!this.getBlockState().get(LIT))
			return;
		fuel = Math.max(fuel-1, 0);
		if(fuel%20==0)
			this.markDirty();
		if(fuel==0)
			this.getWorld().setBlockState(this.getPos(), this.getBlockState().with(BlockStateProperties.LIT, false));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		super.write(nbt);
		nbt.putInt(Burnout.MODID+":fuel", fuel);
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt)
	{
		super.read(state, nbt);
		this.fuel = nbt.getInt(Burnout.MODID+":fuel");
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt(Burnout.MODID+":fuel", fuel);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt)
	{
		super.handleUpdateTag(state, nbt);
		this.fuel = nbt.getInt(Burnout.MODID+":fuel");
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket(){
	    CompoundNBT nbt = this.getUpdateTag();
	    return new SUpdateTileEntityPacket(getPos(), -1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
	    CompoundNBT nbt = pkt.getNbtCompound();
		this.fuel = nbt.getInt(Burnout.MODID+":fuel");
	}
	
}
