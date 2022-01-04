package me.adneths.advdragonfight;

import org.apache.logging.log4j.Logger;

import me.adneths.advdragonfight.proxy.IProxy;
import me.adneths.advdragonfight.world.WorldProviderAdvEnd;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AdvDragonFight.MODID, name = AdvDragonFight.NAME, version = AdvDragonFight.VERSION)
public class AdvDragonFight
{
    public static final String MODID = "advdragonfight";
    public static final String NAME = "AdvcancedDragonFight";
    public static final String VERSION = "1.12.2-1.0.0";

    private static Logger logger;
    
    @SidedProxy(clientSide = "me.adneths.advdragonfight.proxy.ClientProxy", serverSide = "me.adneths.advdragonfight.proxy.ServerProxy")
    public static IProxy proxy;
    
    public static DimensionType ADV_THE_END = DimensionType.register("the_end", "_end", 1, WorldProviderAdvEnd.class, false);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        logger.info("[AdvDragonFight]: Begin Pre-init");
        proxy.preInit();
        logger.info("[AdvDragonFight]: Finish Pre-init");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("[AdvDragonFight]: Begin Init");
    	proxy.init();
        logger.info("[AdvDragonFight]: Finish Init");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        logger.info("[AdvDragonFight]: Begin Post-init");
    	proxy.postInit();
        logger.info("[AdvDragonFight]: Finish Post-init");
    }
}
