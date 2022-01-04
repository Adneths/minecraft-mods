package me.adneths.advdragonfight.proxy;

import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return null;
	}
	
	@Override
	public boolean isServer()
	{
		return true;
	}

}
