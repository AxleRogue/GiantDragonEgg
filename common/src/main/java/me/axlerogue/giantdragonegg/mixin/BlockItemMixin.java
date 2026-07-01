package me.axlerogue.giantdragonegg.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "canPlace", at = @At("HEAD"), cancellable = true)
    private void overrideDragonEggPlacement(BlockPlaceContext context, net.minecraft.world.level.block.state.BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(Blocks.DRAGON_EGG)) {
            // Check if placing as giant (not on bedrock)
            boolean isEnd = context.getLevel().dimension() == net.minecraft.world.level.Level.END;
            boolean isOnBedrock = context.getLevel().getBlockState(context.getClickedPos().below()).is(Blocks.BEDROCK);
            if (!(isEnd && isOnBedrock)) {
                // Allow placement, overriding collision check for the larger shape
                cir.setReturnValue(true);
            }
        }
    }
}

