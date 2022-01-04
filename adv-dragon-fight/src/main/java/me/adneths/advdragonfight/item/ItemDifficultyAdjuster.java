package me.adneths.advdragonfight.item;

import me.adneths.advdragonfight.AdvDragonFight;
import me.adneths.advdragonfight.capability.DragonDifficultyProvider;
import me.adneths.advdragonfight.capability.IDragonDifficulty;
import me.adneths.advdragonfight.network.DragonDifficultyPacket;
import me.adneths.advdragonfight.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemDifficultyAdjuster extends Item
{
	public ItemDifficultyAdjuster()
	{
		this.setRegistryName(new ResourceLocation(AdvDragonFight.MODID, "diff_adjuster"));
		this.setUnlocalizedName(this.getRegistryName().toString());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if (!worldIn.isRemote)
		{
			IDragonDifficulty diff = playerIn.getCapability(DragonDifficultyProvider.dragonDifficulty, null);
			if (playerIn.isSneaking())
			{
				diff.setDifficulty(diff.getDifficulty() - 1);
			}
			else
			{
				diff.setDifficulty(diff.getDifficulty() + 1);
			}
			playerIn.sendMessage(new TextComponentString("Your difficulty is now " + diff.getDifficulty()));
			PacketHandler.INSTANCE.sendTo(new DragonDifficultyPacket(diff.getDifficulty()), (EntityPlayerMP) playerIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
