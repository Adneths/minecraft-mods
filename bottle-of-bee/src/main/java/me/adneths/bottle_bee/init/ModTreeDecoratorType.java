package me.adneths.bottle_bee.init;

import java.lang.reflect.InvocationTargetException;

import com.mojang.serialization.Codec;

import me.adneths.bottle_bee.BottleOfBee;
import me.adneths.bottle_bee.world.gen.KillerBeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTreeDecoratorType {
	
	public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, BottleOfBee.MODID); 
	
	public static final TreeDecoratorType<KillerBeehiveTreeDecorator> KILLER_BEEHIVE = register("killer_beehive_tree_awega_decorator", KillerBeehiveTreeDecorator.field_236863_a_);
	
	@SuppressWarnings("unchecked")
	private static <P extends TreeDecorator> TreeDecoratorType<P> register(String name, Codec<P> codec) {
		TreeDecoratorType<P> ret;
		try {
			ret = (TreeDecoratorType<P>)ObfuscationReflectionHelper.findConstructor(TreeDecoratorType.class, Codec.class).newInstance(codec);
			TREE_DECORATOR_TYPES.register(name, () -> ret);
			return ret;	
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
