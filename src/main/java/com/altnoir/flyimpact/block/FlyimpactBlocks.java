package com.altnoir.flyimpact.block;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.barrel.BarrelTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlyimpactBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Flyimpact.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Flyimpact.MOD_ID);

    public static final DeferredHolder<Block, TieredFlyBarrelBlock> COPPER_FLY_BARREL = barrel("copper_fly_barrel", BarrelTier.COPPER);
    public static final DeferredHolder<Block, TieredFlyBarrelBlock> IRON_FLY_BARREL = barrel("iron_fly_barrel", BarrelTier.IRON);
    public static final DeferredHolder<Block, SimpleBreedingChestBlock> SIMPLE_BREEDING_CHEST =
            BLOCKS.register("simple_breeding_chest", () -> new SimpleBreedingChestBlock(copyBreedingChestProperties()));

    public static final DeferredHolder<Item, BlockItem> COPPER_FLY_BARREL_ITEM = blockItem("copper_fly_barrel", COPPER_FLY_BARREL);
    public static final DeferredHolder<Item, BlockItem> IRON_FLY_BARREL_ITEM = blockItem("iron_fly_barrel", IRON_FLY_BARREL);
    public static final DeferredHolder<Item, BlockItem> SIMPLE_BREEDING_CHEST_ITEM = blockItem("simple_breeding_chest", SIMPLE_BREEDING_CHEST);

    private FlyimpactBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    private static DeferredHolder<Block, TieredFlyBarrelBlock> barrel(String name, BarrelTier tier) {
        return BLOCKS.register(name, () -> new TieredFlyBarrelBlock(copyFlyBarrelProperties(), tier));
    }

    private static DeferredHolder<Item, BlockItem> blockItem(String name, DeferredHolder<Block, ? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static BlockBehaviour.Properties copyFlyBarrelProperties() {
        Block flyBarrel = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("poopsky", "fly_barrel"));
        if (flyBarrel == Blocks.AIR) {
            flyBarrel = Blocks.BARREL;
        }
        return BlockBehaviour.Properties.ofFullCopy(flyBarrel);
    }

    private static BlockBehaviour.Properties copyBreedingChestProperties() {
        Block breedingChest = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("poopsky", "breeding_chest"));
        if (breedingChest == Blocks.AIR) {
            breedingChest = Blocks.CHEST;
        }
        return BlockBehaviour.Properties.ofFullCopy(breedingChest);
    }
}
