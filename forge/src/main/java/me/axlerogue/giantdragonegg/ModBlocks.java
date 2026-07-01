package me.axlerogue.giantdragonegg;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final RegistryObject<Block> DUMMY_EGG = BLOCKS.register("dummy_egg", 
        () -> new DummyDragonEggBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DRAGON_EGG).noOcclusion().noLootTable().pushReaction(net.minecraft.world.level.material.PushReaction.IGNORE)));
}
