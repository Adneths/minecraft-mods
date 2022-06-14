package me.adneths.bottle_bee.item;

import me.adneths.bottle_bee.entity.BottleBeeEntity;
import me.adneths.bottle_bee.inventory.ItemGroups;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class BottleBeeItem extends Item {

	private int num;
	private boolean isKiller;
	
	public BottleBeeItem(int num, boolean isKiller) {
		super(new Item.Properties().maxStackSize(16).group(ItemGroups.MAIN));
		this.num = num;
		this.isKiller = isKiller;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		if (!worldIn.isRemote) {
			BottleBeeEntity bottleBee = new BottleBeeEntity(playerIn.getPosX(), playerIn.getPosY()+playerIn.getEyeHeight(), playerIn.getPosZ(), worldIn);
			bottleBee.setNumberOfBees(num);
			bottleBee.setIsKillerBees(isKiller);
			bottleBee.setItem(stack);
			bottleBee.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 0.9F, 1.0F);
			worldIn.addEntity(bottleBee);
		} 
		if(!playerIn.abilities.isCreativeMode)
			stack.shrink(1);
		return ActionResult.func_233538_a_(stack, worldIn.isRemote);
	}
	
}
