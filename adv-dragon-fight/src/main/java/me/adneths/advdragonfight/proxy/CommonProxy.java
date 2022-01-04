package me.adneths.advdragonfight.proxy;

import java.util.concurrent.Callable;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.capability.DragonDifficulty;
import me.adneths.advdragonfight.capability.DragonDifficultyStorage;
import me.adneths.advdragonfight.capability.IDragonDifficulty;
import me.adneths.advdragonfight.network.PacketHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.CapabilityManager;

public abstract class CommonProxy implements IProxy {
	
	@Override
	public void preInit() {
		DimensionManager.unregisterDimension(1);
		DimensionManager.registerDimension(1, AdvDragonFight.ADV_THE_END);
	}

	@Override
	public void init() {
		PacketHandler.register();
		CapabilityManager.INSTANCE.register(IDragonDifficulty.class, new DragonDifficultyStorage(), new Callable<IDragonDifficulty>() {
			@Override
			public DragonDifficulty call() throws Exception {
				return new DragonDifficulty();
			}});
	}

	@Override
	public void postInit() {
		
	}

}
