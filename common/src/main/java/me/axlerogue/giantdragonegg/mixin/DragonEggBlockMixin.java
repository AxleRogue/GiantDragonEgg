package me.axlerogue.giantdragonegg.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DragonEggBlock.class)
public abstract class DragonEggBlockMixin extends Block {

    @Shadow
    protected abstract int getDelayAfterPlace();

    @Unique
    private static final BooleanProperty GIANT = BooleanProperty.create("giant");

    @Unique
    private static final VoxelShape GIANT_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 32.0D, 32.0D, 32.0D);

    public DragonEggBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initMixin(Properties properties, CallbackInfo ci) {
        this.registerDefaultState(this.stateDefinition.any().setValue(GIANT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(GIANT);
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void onGetShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.hasProperty(GIANT) && state.getValue(GIANT)) {
            cir.setReturnValue(GIANT_SHAPE);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        boolean isEnd = context.getLevel().dimension() == net.minecraft.world.level.Level.END;
        boolean isOnBedrock = context.getLevel().getBlockState(pos.below()).is(Blocks.BEDROCK);
        return this.defaultBlockState().setValue(GIANT, !(isEnd && isOnBedrock));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (state.hasProperty(GIANT)) {
            boolean isEnd = level.dimension() == net.minecraft.world.level.Level.END;
            boolean isOnBedrock = level.getBlockState(pos.below()).is(Blocks.BEDROCK);
            boolean shouldBeGiant = !(isEnd && isOnBedrock);
            
            if (state.getValue(GIANT) != shouldBeGiant) {
                level.setBlock(pos, state.setValue(GIANT, shouldBeGiant), 3);
                return;
            }
        }
        
        if (state.hasProperty(GIANT) && state.getValue(GIANT) && !state.is(oldState.getBlock())) {
            for (int dx = 0; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = 0; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos target = pos.offset(dx, dy, dz);
                        BlockState targetState = level.getBlockState(target);
                        if (targetState.canBeReplaced() || targetState.isAir()) {
                            level.setBlock(target, net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse("giantdragonegg:dummy_egg")).defaultBlockState(), 3);
                        } else {
                            level.destroyBlock(target, true);
                            level.setBlock(target, net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse("giantdragonegg:dummy_egg")).defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasProperty(GIANT) && state.getValue(GIANT) && !state.is(newState.getBlock())) {
            for (int dx = 0; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = 0; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos target = pos.offset(dx, dy, dz);
                        if (level.getBlockState(target).is(net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse("giantdragonegg:dummy_egg")))) {
                            level.removeBlock(target, false);
                        }
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.hasProperty(GIANT) && state.getValue(GIANT)) {
            boolean allFree = true;
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    BlockPos under = pos.offset(dx, -1, dz);
                    if (!FallingBlock.isFree(level.getBlockState(under))) {
                        allFree = false;
                        break;
                    }
                }
            }
                if (allFree) {
                    for (int dx = 0; dx <= 1; dx++) {
                        for (int dy = 0; dy <= 1; dy++) {
                            for (int dz = 0; dz <= 1; dz++) {
                                if (dx == 0 && dy == 0 && dz == 0) continue;
                                BlockPos target = pos.offset(dx, dy, dz);
                                if (level.getBlockState(target).is(net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse("giantdragonegg:dummy_egg")))) {
                                    level.removeBlock(target, false);
                                }
                            }
                        }
                    }
                    FallingBlockEntity.fall(level, pos, state);
                }
        } else {
            super.tick(state, level, pos, random);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.hasProperty(GIANT) && state.getValue(GIANT)) {
            if (neighborPos.getY() == currentPos.getY() - 1 &&
                neighborPos.getX() >= currentPos.getX() && neighborPos.getX() <= currentPos.getX() + 1 &&
                neighborPos.getZ() >= currentPos.getZ() && neighborPos.getZ() <= currentPos.getZ() + 1) {
                level.scheduleTick(currentPos, this, this.getDelayAfterPlace());
            }
        } else {
            return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    private void onTeleport(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (state.hasProperty(GIANT) && state.getValue(GIANT)) {
            ci.cancel();
            for (int i = 0; i < 1000; ++i) {
                BlockPos targetPos = pos.offset(level.random.nextInt(16) - level.random.nextInt(16), level.random.nextInt(8) - level.random.nextInt(8), level.random.nextInt(16) - level.random.nextInt(16));
                
                boolean free = true;
                for (int dx = 0; dx <= 1; dx++) {
                    for (int dy = 0; dy <= 1; dy++) {
                        for (int dz = 0; dz <= 1; dz++) {
                            if (targetPos.getY() + dy < level.getMinBuildHeight() || targetPos.getY() + dy >= level.getMaxBuildHeight()) {
                                free = false;
                                break;
                            }
                            if (!level.getBlockState(targetPos.offset(dx, dy, dz)).isAir()) {
                                free = false;
                                break;
                            }
                        }
                    }
                }
                if (free) {
                    if (level.isClientSide) {
                        for (int j = 0; j < 128; ++j) {
                            double d0 = level.random.nextDouble();
                            float f = (level.random.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (level.random.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (level.random.nextFloat() - 0.5F) * 0.2F;
                            double d1 = Mth.lerp(d0, (double)targetPos.getX(), (double)pos.getX()) + (level.random.nextDouble() - 0.5D) + 0.5D;
                            double d2 = Mth.lerp(d0, (double)targetPos.getY(), (double)pos.getY()) + level.random.nextDouble() - 0.5D;
                            double d3 = Mth.lerp(d0, (double)targetPos.getZ(), (double)pos.getZ()) + (level.random.nextDouble() - 0.5D) + 0.5D;
                            level.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
                        }
                    } else {
                        level.setBlock(targetPos, state, 2);
                        level.removeBlock(pos, false);
                    }
                    return;
                }
            }
        }
    }
}



