package me.axlerogue.giantdragonegg;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final Block DUMMY_EGG = new DummyDragonEggBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DRAGON_EGG).noOcclusion().noLootTable().pushReaction(net.minecraft.world.level.material.PushReaction.IGNORE));

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "dummy_egg"), DUMMY_EGG);
    }
}