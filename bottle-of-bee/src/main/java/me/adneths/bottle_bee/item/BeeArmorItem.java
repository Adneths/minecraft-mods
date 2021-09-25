package me.adneths.bottle_bee.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import me.adneths.bottle_bee.init.ModContents;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;

public class BeeArmorItem extends ArmorItem {
	private static final UUID[] ARMOR_MODIFIERS = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };

	private Multimap<Attribute, AttributeModifier> attributeModifiers;
	
	public BeeArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Item.Properties builderIn) {
		super(materialIn, slot, builderIn.defaultMaxDamage(materialIn.getDurability(slot)));
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
		builder.putAll(super.getAttributeModifiers(slot));
		builder.put(ModContents.Attributes.STING_RESISTANCE,
				new AttributeModifier(uuid, "Sting resistance", .25, AttributeModifier.Operation.ADDITION));
		attributeModifiers = (Multimap<Attribute, AttributeModifier>) builder.build();
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		return (equipmentSlot == this.slot) ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
	}

}
