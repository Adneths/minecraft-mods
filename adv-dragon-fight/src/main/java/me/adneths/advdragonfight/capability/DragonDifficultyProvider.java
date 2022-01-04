package me.adneths.advdragonfight.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class DragonDifficultyProvider implements ICapabilitySerializable<NBTBase>
{

	@CapabilityInject(IDragonDifficulty.class)
	public static final Capability<IDragonDifficulty> dragonDifficulty = null;
	
	private IDragonDifficulty instance = dragonDifficulty.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == dragonDifficulty;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == dragonDifficulty ? dragonDifficulty.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT()
	{
		return dragonDifficulty.getStorage().writeNBT(dragonDifficulty, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt)
	{
		dragonDifficulty.getStorage().readNBT(dragonDifficulty, this.instance, null, nbt);
	}

}
