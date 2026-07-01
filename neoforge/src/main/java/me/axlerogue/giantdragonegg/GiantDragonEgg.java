package me.axlerogue.giantdragonegg;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class GiantDragonEgg {

    public GiantDragonEgg(IEventBus eventBus) {
        CommonClass.init();
        
        ModBlocks.BLOCKS.register(eventBus);
    }
}
