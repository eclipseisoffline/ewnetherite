package xyz.eclipseisoffline.ewnetherite;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import org.jetbrains.annotations.NotNull;

public class EwNetherite implements ModInitializer {
    public static final LootItemFunctionType NO_NETHERITE_FUNCTION = Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE,
            ResourceLocation.tryBuild("ewnetherite", "no_netherite"), new LootItemFunctionType(new NoNetheriteFunction.FunctionSerializer()));

    @Override
    public void onInitialize() {
        LootTableEvents.MODIFY.register((resourceManager, lootDataManager,
                                         id, builder, source) -> builder.modifyPools(pool -> pool.apply(new NoNetheriteFunction())));

        BiomeModifications.create(ResourceLocation.tryBuild("ewnetherite", "no_netherite"))
                .add(ModificationPhase.REMOVALS, context -> context.hasFeature(OreFeatures.ORE_ANCIENT_DEBRIS_LARGE) || context.hasFeature(OreFeatures.ORE_ANCIENT_DEBRIS_SMALL),
                        context -> {
                    context.getGenerationSettings().removeFeature(OrePlacements.ORE_ANCIENT_DEBRIS_LARGE);
                    context.getGenerationSettings().removeFeature(OrePlacements.ORE_ANCIENT_DEBRIS_SMALL);
                });
    }

    private static class NoNetheriteFunction implements LootItemFunction {

        @Override
        public @NotNull LootItemFunctionType getType() {
            return NO_NETHERITE_FUNCTION;
        }

        @Override
        public ItemStack apply(ItemStack stack, LootContext lootContext) {
            if (stack.is(Items.NETHERITE_INGOT) || stack.is(Items.NETHERITE_SCRAP)
                    || stack.is(Items.ANCIENT_DEBRIS) || stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)) {
                return ItemStack.EMPTY;
            }
            return stack;
        }

        private static class FunctionSerializer implements Serializer<LootItemFunction> {

            @Override
            public void serialize(JsonObject jsonObject, LootItemFunction object, JsonSerializationContext jsonSerializationContext) {}

            @Override
            public @NotNull LootItemFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
                return new NoNetheriteFunction();
            }
        }
    }
}
