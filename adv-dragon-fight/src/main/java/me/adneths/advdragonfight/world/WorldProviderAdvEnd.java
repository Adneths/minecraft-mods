package me.adneths.advdragonfight.world;

import javax.annotation.Nullable;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.world.end.AdvDragonFightManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.end.DragonFightManager;

public class WorldProviderAdvEnd extends WorldProviderEnd
{
	private DragonFightManager dragonFightManager;

	@Override
	public void init()
	{
		this.biomeProvider = new BiomeProviderSingle(Biomes.SKY);
		NBTTagCompound nbttagcompound = this.world.getWorldInfo().getDimensionData(this.world.provider.getDimension());
		this.dragonFightManager = (this.world instanceof WorldServer)
				? new AdvDragonFightManager((WorldServer) this.world, nbttagcompound.getCompoundTag("DragonFight"))
				: null;
	}

	@Override
	public DimensionType getDimensionType()
	{
		return AdvDragonFight.ADV_THE_END;
	}

	@Override
	public void onWorldSave()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (this.dragonFightManager != null)
			nbttagcompound.setTag("DragonFight", (NBTBase) this.dragonFightManager.getCompound());
		this.world.getWorldInfo().setDimensionData(this.world.provider.getDimension(), nbttagcompound);
	}

	@Override
	public void onWorldUpdateEntities()
	{
		if (this.dragonFightManager != null)
			this.dragonFightManager.tick();
	}

	@Override
	@Nullable
	public DragonFightManager getDragonFightManager()
	{
		return this.dragonFightManager;
	}

	@Override
	public void onPlayerAdded(EntityPlayerMP player)
	{
		if (this.dragonFightManager != null)
			this.dragonFightManager.addPlayer(player);
	}

	@Override
	public void onPlayerRemoved(EntityPlayerMP player)
	{
		if (this.dragonFightManager != null)
			this.dragonFightManager.removePlayer(player);
	}
}
