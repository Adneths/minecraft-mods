package me.adneths.bottle_bee.event;

import me.adneths.bottle_bee.init.ModContents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BeeEvents {

	@SubscribeEvent
	public static void onBottleBee(PlayerInteractEvent.EntityInteract event)
	{
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getHeldItem(event.getHand());
		if(event.getTarget().getType().equals(EntityType.BEE))
		{
			BeeEntity bee = (BeeEntity) event.getTarget();
			if(bee.getGrowingAge() > -1 && stack.getItem().equals(Items.HONEY_BOTTLE))
			{
				if(!player.abilities.isCreativeMode)
					stack.shrink(1);
				if(!player.addItemStackToInventory(new ItemStack(ModContents.Items.BOTTLE_OF_BEE)))
					player.dropItem(new ItemStack(ModContents.Items.BOTTLE_OF_BEE), false);
				event.getTarget().remove();
			}
		}
	}
	
	@SubscribeEvent
	public static void onBeeSting(LivingAttackEvent event)
	{
		if(event.getSource().getDamageType().equals("sting"))
		{
			ModifiableAttributeInstance mai = event.getEntityLiving().getAttribute(ModContents.Attributes.STING_RESISTANCE);
			if(mai != null)
				if(Math.random() < mai.getValue())
				{
					event.setCanceled(true);
					for(ItemStack stack : event.getEntityLiving().getArmorInventoryList())
						if(stack.getAttributeModifiers(stack.getEquipmentSlot()).containsKey(ModContents.Attributes.STING_RESISTANCE))
							stack.damageItem(1, event.getEntityLiving(), entity -> entity.sendBreakAnimation(((ArmorItem)stack.getItem()).getEquipmentSlot()));
				}
		}
	}
	
	@SubscribeEvent
	public static void onEntityJump(LivingEvent.LivingJumpEvent event)
	{
		LivingEntity living = event.getEntityLiving();
		EffectInstance effect = living.getActivePotionEffect(ModContents.Effects.PARALYSIS);
		if(effect != null)
		{
			Vector3d v = living.getMotion();
			living.setMotion(v.x, v.y/(effect.getAmplifier()+2), v.z);
		}
	}
	
}
