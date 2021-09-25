package me.adneths.bottle_bee;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.adneths.bottle_bee.block.KillerBeehiveBlock;
import me.adneths.bottle_bee.block.dispenser.KillerBeehiveDispenseBehavior;
import me.adneths.bottle_bee.client.renderer.entity.BottleBeeRenderer;
import me.adneths.bottle_bee.client.renderer.entity.KillerBeeRenderer;
import me.adneths.bottle_bee.effect.CustomEffect;
import me.adneths.bottle_bee.entity.BottleBeeEntity;
import me.adneths.bottle_bee.entity.KillerBeeEntity;
import me.adneths.bottle_bee.init.ModArmorMaterial;
import me.adneths.bottle_bee.init.ModContents;
import me.adneths.bottle_bee.init.ModFeatures;
import me.adneths.bottle_bee.init.ModTreeDecoratorType;
import me.adneths.bottle_bee.inventory.ItemGroups;
import me.adneths.bottle_bee.item.BeeArmorItem;
import me.adneths.bottle_bee.item.BottleBeeItem;
import me.adneths.bottle_bee.item.KillerBeeHoneyBottleItem;
import me.adneths.bottle_bee.tile.KillerBeehiveTile;
import me.adneths.bottle_bee.util.WorldGenUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BottleOfBee.MODID)
public class BottleOfBee {
	public static final String MODID = "bottle_bee";

	private static final Logger LOGGER = LogManager.getLogger();

	public BottleOfBee() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);

		MinecraftForge.EVENT_BUS.register(this);

		ModTreeDecoratorType.TREE_DECORATOR_TYPES.register(modEventBus);
	}

	
	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Loading [Bottle of Bee]");

		for (Biome biome : ForgeRegistries.BIOMES) {
			WorldGenUtil.overrideFeatureInBiome(biome, Features.FOREST_FLOWER_TREES, ModFeatures.FOREST_FLOWER_TREES,
					Features.BIRCH_TALL, ModFeatures.BIRCH_TALL, Features.TREES_BIRCH, ModFeatures.TREES_BIRCH,
					Features.BIRCH_OTHER, ModFeatures.BIRCH_OTHER, Features.PLAIN_VEGETATION,
					ModFeatures.PLAIN_VEGETATION);
		}
		
		GlobalEntityTypeAttributes.put(EntityType.PLAYER, PlayerEntity.func_234570_el_().createMutableAttribute(ModContents.Attributes.STING_RESISTANCE).create());

		Method addMix = ObfuscationReflectionHelper.findMethod(PotionBrewing.class, "func_193357_a", Potion.class,
				Item.class, Potion.class);
		try {
			addMix.invoke(null, Potions.AWKWARD, ModContents.Items.VENOM, ModContents.Potions.VENOM);
			addMix.invoke(null, ModContents.Potions.VENOM, Items.GLOWSTONE_DUST, ModContents.Potions.STRONG_VENOM);
			addMix.invoke(null, ModContents.Potions.VENOM, Items.REDSTONE, ModContents.Potions.LONG_VENOM);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		Method getBehavior = ObfuscationReflectionHelper.findMethod(DispenserBlock.class, "func_149940_a",
				ItemStack.class);
		try {
			IDispenseItemBehavior behavior = (IDispenseItemBehavior) getBehavior.invoke(Blocks.DISPENSER,
					new ItemStack(Items.GLASS_BOTTLE));
			DispenserBlock.registerDispenseBehavior(() -> Items.GLASS_BOTTLE, new OptionalDispenseBehavior() {
				private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();
				
				private ItemStack glassBottleFill(IBlockSource source, ItemStack empty, ItemStack filled) {
					empty.shrink(1);
					if (empty.isEmpty())
						return filled.copy();
					if (((DispenserTileEntity) source.<DispenserTileEntity>getBlockTileEntity())
							.addItemStack(filled.copy()) < 0)
						this.defaultBehaviour.dispense(source, filled.copy());
					return empty;
				}

				@Override
				public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
					setSuccessful(false);
					ServerWorld serverworld = source.getWorld();
					BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
					BlockState blockstate = serverworld.getBlockState(blockpos);
					if (blockstate.getBlock().equals(ModContents.Blocks.KILLER_BEE_NEST)
							&& blockstate.get(KillerBeehiveBlock.HONEY_LEVEL) == 5) {
						((KillerBeehiveBlock) blockstate.getBlock()).takeHoney(serverworld, blockstate, blockpos, null,
								KillerBeehiveTile.State.BEE_RELEASED);
						setSuccessful(true);
						return glassBottleFill(source, stack, new ItemStack(Math.random() < 0.02 ? ModContents.Items.VENOM : ModContents.Items.KILLER_BEE_HONEY_BOTTLE));
					}
					return behavior.dispense(source, stack);
				}
			});
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		DispenserBlock.registerDispenseBehavior(() -> Items.SHEARS, new KillerBeehiveDispenseBehavior());
		DispenserBlock.registerDispenseBehavior(() -> ModContents.Items.BOTTLE_OF_BEE,
				new ProjectileDispenseBehavior() {
					protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position,
							ItemStack stackIn) {
						return Util.make(
								new BottleBeeEntity(position.getX(), position.getY(), position.getZ(), worldIn),
								bottle -> {
									bottle.setItem(stackIn);
									bottle.setNumberOfBees(1);
									bottle.setIsKillerBees(false);
								});
					}
				});
		DispenserBlock.registerDispenseBehavior(() -> ModContents.Items.BUNDLE_OF_BEES,
				new ProjectileDispenseBehavior() {
					protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position,
							ItemStack stackIn) {
						return Util.make(
								new BottleBeeEntity(position.getX(), position.getY(), position.getZ(), worldIn),
								bottle -> {
									bottle.setItem(stackIn);
									bottle.setNumberOfBees(8);
									bottle.setIsKillerBees(false);
								});
					}
				});
		DispenserBlock.registerDispenseBehavior(() -> ModContents.Items.BOTTLE_OF_KILLER_BEE,
				new ProjectileDispenseBehavior() {
					protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position,
							ItemStack stackIn) {
						return Util.make(
								new BottleBeeEntity(position.getX(), position.getY(), position.getZ(), worldIn),
								bottle -> {
									bottle.setItem(stackIn);
									bottle.setNumberOfBees(1);
									bottle.setIsKillerBees(true);
								});
					}
				});
		DispenserBlock.registerDispenseBehavior(() -> ModContents.Items.BUNDLE_OF_KILLER_BEES,
				new ProjectileDispenseBehavior() {
					protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position,
							ItemStack stackIn) {
						return Util.make(
								new BottleBeeEntity(position.getX(), position.getY(), position.getZ(), worldIn),
								bottle -> {
									bottle.setItem(stackIn);
									bottle.setNumberOfBees(8);
									bottle.setIsKillerBees(true);
								});
					}
				});
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ModContents.Entities.BOTTLE_OF_BEE,
				renderManager -> new BottleBeeRenderer(renderManager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(ModContents.Entities.KILLER_BEE,
				renderManager -> new KillerBeeRenderer(renderManager));
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {

	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		
		@SubscribeEvent
		public static void onAttributeRegistry(final RegistryEvent.Register<Attribute> event) {
			event.getRegistry().register(ModContents.Attributes.STING_RESISTANCE);
		}
		
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
			event.getRegistry().register(new KillerBeehiveBlock(AbstractBlock.Properties
					.create(Material.WOOD, MaterialColor.YELLOW).hardnessAndResistance(0.3F).sound(SoundType.WOOD))
							.setRegistryName(BottleOfBee.MODID, "killer_bee_nest"));
		}

		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
			event.getRegistry()
					.register(new BottleBeeItem(1, false).setRegistryName(BottleOfBee.MODID, "bottle_of_bee"));
			event.getRegistry()
					.register(new BottleBeeItem(8, false).setRegistryName(BottleOfBee.MODID, "bundle_of_bees"));
			event.getRegistry()
					.register(new BottleBeeItem(1, true).setRegistryName(BottleOfBee.MODID, "bottle_of_killer_bee"));
			event.getRegistry()
					.register(new BottleBeeItem(8, true).setRegistryName(BottleOfBee.MODID, "bundle_of_killer_bees"));
			event.getRegistry().register(
					new KillerBeeHoneyBottleItem().setRegistryName(BottleOfBee.MODID, "killer_bee_honey_bottle"));

			event.getRegistry().register(
					new BlockItem(ModContents.Blocks.KILLER_BEE_NEST, new Item.Properties().group(ItemGroups.MAIN))
							.setRegistryName(BottleOfBee.MODID, "killer_bee_nest"));
			event.getRegistry().register(new Item(new Item.Properties().group(ItemGroups.MAIN).maxStackSize(1))
					.setRegistryName(BottleOfBee.MODID, "venom"));

			event.getRegistry()
					.register(new BeeArmorItem(ModArmorMaterial.BEE_SUIT, EquipmentSlotType.HEAD,
							new Item.Properties().group(ItemGroups.MAIN)).setRegistryName(BottleOfBee.MODID,
									"bee_suit_helmet"));
			event.getRegistry()
					.register(new BeeArmorItem(ModArmorMaterial.BEE_SUIT, EquipmentSlotType.CHEST,
							new Item.Properties().group(ItemGroups.MAIN)).setRegistryName(BottleOfBee.MODID,
									"bee_suit_chestplate"));
			event.getRegistry()
					.register(new BeeArmorItem(ModArmorMaterial.BEE_SUIT, EquipmentSlotType.LEGS,
							new Item.Properties().group(ItemGroups.MAIN)).setRegistryName(BottleOfBee.MODID,
									"bee_suit_leggings"));
			event.getRegistry()
					.register(new BeeArmorItem(ModArmorMaterial.BEE_SUIT, EquipmentSlotType.FEET,
							new Item.Properties().group(ItemGroups.MAIN)).setRegistryName(BottleOfBee.MODID,
									"bee_suit_boots"));
		}

		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
			event.getRegistry()
					.register(TileEntityType.Builder.create(KillerBeehiveTile::new, ModContents.Blocks.KILLER_BEE_NEST)
							.build(null).setRegistryName(BottleOfBee.MODID, "tile.kill_bee_hive"));
		}

		@SubscribeEvent
		public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
			event.getRegistry().register(EntityType.Builder
					.<BottleBeeEntity>create(BottleBeeEntity::new, EntityClassification.MISC).size(0.25F, 0.25F)
					.setCustomClientFactory(BottleBeeEntity::new).trackingRange(4).func_233608_b_(10)
					.build(BottleOfBee.MODID + ":bottle_of_bee").setRegistryName(BottleOfBee.MODID, "bottle_of_bee"));
			@SuppressWarnings("unchecked")
			EntityType<? extends LivingEntity> type = (EntityType<? extends LivingEntity>) EntityType.Builder
					.<KillerBeeEntity>create(KillerBeeEntity::new, EntityClassification.CREATURE).size(0.7F, 0.6F)
					.trackingRange(8).build(BottleOfBee.MODID + ":killer_bee")
					.setRegistryName(BottleOfBee.MODID, "killer_bee");
			event.getRegistry().register(type);
			GlobalEntityTypeAttributes.put(type, KillerBeeEntity.func_234182_eX_().create());
		}

		@SubscribeEvent
		public static void onEffectRegistry(final RegistryEvent.Register<Effect> event) {
			event.getRegistry()
					.register(new CustomEffect(EffectType.HARMFUL, 0xdddddd)
							.addAttributesModifier(Attributes.MOVEMENT_SPEED, "E17242EF-E8FB-4CC5-BE46-820F3D569886",
									-0.300000003973642986D, AttributeModifier.Operation.MULTIPLY_TOTAL)
							.addAttributesModifier(Attributes.FLYING_SPEED, "5F46E11A-6CB7-419D-A390-799D0EB722E8",
									-0.300000003973642986D, AttributeModifier.Operation.MULTIPLY_TOTAL)
							.addAttributesModifier(Attributes.ATTACK_SPEED, "0914E3CE-56E8-4702-9B20-03B3D01AFBF7",
									-0.500000003973642986D, AttributeModifier.Operation.MULTIPLY_TOTAL)
							.setRegistryName(BottleOfBee.MODID, "paralysis"));
		}

		@SubscribeEvent
		public static void onPotionRegistry(final RegistryEvent.Register<Potion> event) {
			event.getRegistry()
					.register(new Potion(new EffectInstance(Effects.POISON, 400, 1),
							new EffectInstance(Effects.NAUSEA, 900, 1), new EffectInstance(Effects.HUNGER, 300, 0),
							new EffectInstance(ModContents.Effects.PARALYSIS, 500, 0))
									.setRegistryName(BottleOfBee.MODID, "venom"));
			event.getRegistry()
					.register(new Potion(new EffectInstance(Effects.POISON, 200, 3),
							new EffectInstance(Effects.NAUSEA, 400, 3), new EffectInstance(Effects.HUNGER, 200, 1),
							new EffectInstance(ModContents.Effects.PARALYSIS, 200, 1))
									.setRegistryName(BottleOfBee.MODID, "strong_venom"));
			event.getRegistry()
					.register(new Potion(new EffectInstance(Effects.POISON, 600, 1),
							new EffectInstance(Effects.NAUSEA, 1800, 1), new EffectInstance(Effects.HUNGER, 1200, 0),
							new EffectInstance(ModContents.Effects.PARALYSIS, 1200, 0))
									.setRegistryName(BottleOfBee.MODID, "long_venom"));
		}
		
		@SubscribeEvent
		public static void onPointOfInterestTypeRegistry(final RegistryEvent.Register<PointOfInterestType> event)
		{
			PointOfInterestType poit;
			event.getRegistry().register(poit = new PointOfInterestType("killer_bee_nest", PointOfInterestType.getAllStates(ModContents.Blocks.KILLER_BEE_NEST), 1, 0).setRegistryName(BottleOfBee.MODID, "killer_bee_nest"));
			try {
				Method func_221052_a = ObfuscationReflectionHelper.findMethod(PointOfInterestType.class, "func_221052_a", PointOfInterestType.class);
				func_221052_a.invoke(null, poit);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
