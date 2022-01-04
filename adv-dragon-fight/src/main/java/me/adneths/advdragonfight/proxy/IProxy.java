package me.adneths.advdragonfight.proxy;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

public interface IProxy {
	void preInit();

	void init();

	void postInit();
	
	boolean isServer();

	@Nullable
	EntityPlayer getClientPlayer();
}