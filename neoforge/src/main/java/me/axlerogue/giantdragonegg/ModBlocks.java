package me.axlerogue.giantdragonegg;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);

    public static final DeferredBlock<Block> DUMMY_EGG = BLOCKS.register("dummy_egg", 
        () -> new DummyDragonEggBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DRAGON_EGG).noOcclusion().noLootTable().pushReaction(net.minecraft.world.level.material.PushReaction.IGNORE)));
}