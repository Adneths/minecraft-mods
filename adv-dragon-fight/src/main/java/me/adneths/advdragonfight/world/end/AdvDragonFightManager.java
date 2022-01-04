package me.adneths.advdragonfight.world.end;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.adneths.advdragonfight.capability.DragonDifficultyProvider;
import me.adneths.advdragonfight.capability.IDragonDifficulty;
import me.adneths.advdragonfight.network.DragonDifficultyPacket;
import me.adneths.advdragonfight.network.PacketHandler;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.feature.WorldGenEndPodium;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AdvDragonFightManager extends DragonFightManager
{
	private final WorldServer world;
	private boolean generatedPortal = false;

	private List<EntityPlayer> fightingPlayers;

	public AdvDragonFightManager(WorldServer worldIn, NBTTagCompound compound)
	{
		super(worldIn, removeDragon(compound));
		this.world = worldIn;
		if (compound.hasKey("GeneratedPortal"))
			generatedPortal = compound.getBoolean("GeneratedPortal");
		this.fightingPlayers = new ArrayList<EntityPlayer>();
	}

	@Override
	public NBTTagCompound getCompound()
	{
		NBTTagCompound nbt = super.getCompound();
		nbt.setBoolean("GeneratedPortal", generatedPortal);
		return nbt;
	}

	@Override
	public void tick()
	{
		super.tick();
		if (!this.generatedPortal)
		{
			this.respawnDragon();
			if (this.getCompound().hasKey("ExitPortalLocation", 10))
			{
				this.generatedPortal = true;
				this.generatePortal();
			}
		}
	}

	public void despawnDragon(EntityDragon dragon)
	{
		generatePortal();
		BossInfoServer bossInfo = ObfuscationReflectionHelper.getPrivateValue(DragonFightManager.class, this,
				"field_186109_c");
		bossInfo.setPercent(0.0F);
		bossInfo.setVisible(false);
		ObfuscationReflectionHelper.setPrivateValue(DragonFightManager.class, this, true, "field_186117_k");
	}

	private void generatePortal()
	{
		WorldGenEndPodium worldgenendpodium = new WorldGenEndPodium(true);
		BlockPos exitPortalLocation = NBTUtil.getPosFromTag(this.getCompound().getCompoundTag("ExitPortalLocation"));
		worldgenendpodium.generate(this.world, new Random(), exitPortalLocation);
	}

	@Override
	public void processDragonDeath(EntityDragon dragon)
	{
		super.processDragonDeath(dragon);
		this.world.setBlockState(this.world.getHeight(WorldGenEndPodium.END_PODIUM_LOCATION),
				Blocks.DRAGON_EGG.getDefaultState());
		if (!this.world.isRemote)
		{
			for (EntityPlayer p : this.fightingPlayers)
			{
				IDragonDifficulty diff = p.getCapability(DragonDifficultyProvider.dragonDifficulty, null);
				diff.addDifficulty(1);
				PacketHandler.INSTANCE.sendTo(new DragonDifficultyPacket(diff.getDifficulty()), (EntityPlayerMP) p);
			}
			this.getFightingPlayers().clear();
		}
	}

	@Override
	public void dragonUpdate(EntityDragon dragonIn)
	{
		super.dragonUpdate(dragonIn);
	}

	@Override
	public void removePlayer(EntityPlayerMP player)
	{
		super.removePlayer(player);
		this.fightingPlayers.remove(player);
	}

	private static NBTTagCompound removeDragon(NBTTagCompound compound)
	{
		compound.setBoolean("DragonKilled", true);
		compound.setBoolean("PreviouslyKilled", true);
		compound.setBoolean("LegacyScanPerformed", true);
		compound.setBoolean("IsRespawning", false);
		return compound;
	}

	public boolean hasDragonBeenKilled()
	{
		for (int i = -8; i <= 8; i++)
		{
			for (int j = -8; j <= 8; j++)
			{
				Chunk chunk = this.world.getChunkFromChunkCoords(i, j);
				for (TileEntity tileentity : chunk.getTileEntityMap().values())
				{
					if (tileentity instanceof net.minecraft.tileentity.TileEntityEndPortal)
						return true;
				}
			}
		}
		return false;
	}

	public boolean noFightingPlayersLeft()
	{
		return fightingPlayers.isEmpty();
	}

	public boolean containsFightingPlayer(EntityPlayer p)
	{
		return fightingPlayers.contains(p);
	}

	public void addFightingPlayer(EntityPlayer p)
	{
		this.fightingPlayers.add(p);
	}

	public List<EntityPlayer> getFightingPlayers()
	{
		return this.fightingPlayers;
	}

}
