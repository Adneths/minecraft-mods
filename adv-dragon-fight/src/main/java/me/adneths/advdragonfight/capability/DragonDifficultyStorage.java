package me.adneths.advdragonfight.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DragonDifficultyStorage implements Capability.IStorage<IDragonDifficulty>
{

	@Override
	public NBTBase writeNBT(Capability<IDragonDifficulty> capability, IDragonDifficulty instance, EnumFacing side)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dragonDifficulty", instance.getDifficulty());
		return nbt;
	}

	@Override
	public void readNBT(Capability<IDragonDifficulty> capability, IDragonDifficulty instance, EnumFacing side,
			NBTBase nbt)
	{
		NBTTagCompound tag = (NBTTagCompound) nbt;
		instance.setDifficulty(tag.getInteger("dragonDifficulty"));
	}

}
