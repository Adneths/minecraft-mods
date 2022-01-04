package me.adneths.advdragonfight.network;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.capability.DragonDifficultyProvider;
import me.adneths.advdragonfight.capability.IDragonDifficulty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	private static int id = 0;
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AdvDragonFight.MODID);

	public static void register()
	{
		INSTANCE.registerMessage(DifficultyPacketHandler.class, DragonDifficultyPacket.class, id++, Side.CLIENT);
	}

	public static class DifficultyPacketHandler implements IMessageHandler<DragonDifficultyPacket, IMessage>
	{
		public DifficultyPacketHandler()
		{
		}

		@Override
		public IMessage onMessage(DragonDifficultyPacket packet, MessageContext context)
		{
			EntityPlayer player = AdvDragonFight.proxy.getClientPlayer();

			Minecraft.getMinecraft().addScheduledTask(() -> {
				IDragonDifficulty instance = player.getCapability(DragonDifficultyProvider.dragonDifficulty, null);
				instance.setDifficulty(packet.difficulty);
			});

			return null;
		}
	}
}
