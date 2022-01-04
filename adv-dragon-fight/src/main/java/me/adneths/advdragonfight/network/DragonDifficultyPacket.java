package me.adneths.advdragonfight.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class DragonDifficultyPacket implements IMessage {
	
	protected int difficulty;
	
	public DragonDifficultyPacket()
	{
		this(1);
	}
	
	public DragonDifficultyPacket(int difficulty)
	{
		this.difficulty = difficulty;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.difficulty);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.difficulty = buf.readInt();
	}
}
