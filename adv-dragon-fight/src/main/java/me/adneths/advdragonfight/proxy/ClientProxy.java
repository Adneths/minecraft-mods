package me.adneths.advdragonfight.proxy;

import me.adneths.advdragonfight.client.renderer.RenderBabyDragon;
import me.adneths.advdragonfight.client.renderer.RenderDelaySpawn;
import me.adneths.advdragonfight.client.renderer.RenderDragonEffectBall;
import me.adneths.advdragonfight.client.renderer.RenderFloatingItem;
import me.adneths.advdragonfight.client.renderer.RenderHealthedEnderCrystal;
import me.adneths.advdragonfight.client.renderer.RenderScale;
import me.adneths.advdragonfight.client.renderer.RenderSmallDragonFireball;
import me.adneths.advdragonfight.entity.EntityDelaySpawn;
import me.adneths.advdragonfight.entity.EntityDivingBabyDragon;
import me.adneths.advdragonfight.entity.EntityDragonEffectBall;
import me.adneths.advdragonfight.entity.EntityEnderGuardian;
import me.adneths.advdragonfight.entity.EntityFireBabyDragon;
import me.adneths.advdragonfight.entity.EntityFloatingItem;
import me.adneths.advdragonfight.entity.EntityHardenedEndermite;
import me.adneths.advdragonfight.entity.EntityHealingBabyDragon;
import me.adneths.advdragonfight.entity.EntityHealthedEnderCrystal;
import me.adneths.advdragonfight.entity.EntityScale;
import me.adneths.advdragonfight.entity.EntityScaledDragon;
import me.adneths.advdragonfight.entity.EntitySmallDragonFireball;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderEndermite;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy {

	private final Minecraft minecraft = Minecraft.getMinecraft();
	
	@Override
	public void preInit() {
		super.preInit();
	}

	@Override
	public void init() {
		super.init();
		minecraft.getRenderManager().entityRenderMap.put(EntityHealthedEnderCrystal.class, new RenderHealthedEnderCrystal(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityHardenedEndermite.class, new RenderEndermite(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityScale.class, new RenderScale(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityDivingBabyDragon.class, new RenderBabyDragon(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityHealingBabyDragon.class, new RenderBabyDragon(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityFireBabyDragon.class, new RenderBabyDragon(minecraft.getRenderManager()));		
		minecraft.getRenderManager().entityRenderMap.put(EntitySmallDragonFireball.class, new RenderSmallDragonFireball(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityDragonEffectBall.class, new RenderDragonEffectBall(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityScaledDragon.class, new RenderDragon(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityDelaySpawn.class, new RenderDelaySpawn(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityFloatingItem.class, new RenderFloatingItem(minecraft.getRenderManager(),minecraft.getRenderItem()));
		minecraft.getRenderManager().entityRenderMap.put(EntityEnderGuardian.class, new RenderEnderman(minecraft.getRenderManager()));
		minecraft.getRenderManager().entityRenderMap.put(EntityDragonEffectBall.class, new RenderDragonEffectBall(minecraft.getRenderManager()));
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return minecraft.player;
	}
	
	@Override
	public boolean isServer()
	{
		return false;
	}

}
