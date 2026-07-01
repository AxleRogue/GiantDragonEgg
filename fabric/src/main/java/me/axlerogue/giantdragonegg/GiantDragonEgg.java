package me.axlerogue.giantdragonegg;

import net.fabricmc.api.ModInitializer;

public class GiantDragonEgg implements ModInitializer {

    @Override
    public void onInitialize() {

        CommonClass.init();
        
        ModBlocks.register();
    }
}
