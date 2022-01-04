package me.adneths.advdragonfight.recipe;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

import me.adneths.advdragonfight.item.ModItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ArmorUpgradeRecipe extends ShapelessRecipes
{

	public ArmorUpgradeRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients)
	{
		super(group, output, ingredients);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn)
	{
		boolean armor = false;
		int scale = 0;
		for (int i = 0; i < 9; i++)
		{
			ItemStack stack = inv.getStackInRowAndColumn(i / 3, i % 3);
			if (stack.getItem() instanceof ItemArmor)
			{
				if(armor && stack.getTagCompound().hasKey("DragonScalePlated") && stack.getTagCompound().getBoolean("DragonScalePlated"))
					return false;
				armor = true;
			}
			else if (stack.getItem() == ModItems.scaledSkin)
			{
				scale++;
			}
			else if(!stack.isEmpty())
				return false;
		}
		return armor && scale == 8;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		ItemStack stack = null;
		for (int i = 0; i < 9; i++)
		{
			stack = inv.getStackInRowAndColumn(i / 3, i % 3).copy();
			if (stack.getItem() instanceof ItemArmor)
				break;
		}
		ItemArmor armor = (ItemArmor) stack.getItem();
		Multimap<String, AttributeModifier> map = stack.getAttributeModifiers(armor.armorType);
		for(String name : map.keySet())
		{
			Collection<AttributeModifier> coll = map.get(name);
			if (!coll.isEmpty())
			{
				for (AttributeModifier mod : coll)
				{
					int c = 0;
					if(mod.getOperation() == 0)
					{
						if(name.equals(SharedMonsterAttributes.ARMOR.getName()))
							c = 4;
						else if(name.equals(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName()))
							c = 6;
					}
					stack.addAttributeModifier(name, new AttributeModifier(mod.getID(), mod.getName(), mod.getAmount()+c, mod.getOperation()), armor.armorType);
				}
			}
		}
		stack.setStackDisplayName(TextFormatting.RESET + "Dragon Plated " + stack.getItem().getItemStackDisplayName(stack));
		NBTTagCompound tag = stack.getTagCompound();
		tag.setBoolean("DragonScalePlated", true);
		stack.setTagCompound(tag);
		return stack;
	}

	public static class Factory implements IRecipeFactory
	{
		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json)
		{
			final String group = JsonUtils.getString(json, "group", "");

			return new ArmorUpgradeRecipe(group.isEmpty() ? "" : group, new ItemStack(Items.DIAMOND_CHESTPLATE), NonNullList.create());
		}
	}

}
