package me.axlerogue.giantdragonegg;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class DummyDragonEggBlock extends Block {

    public DummyDragonEggBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (level instanceof Level) {
            BlockPos mainPos = getMainEggPos((Level)level, currentPos);
            if (mainPos != null) {
                level.scheduleTick(mainPos, level.getBlockState(mainPos).getBlock(), 2);
            }
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        BlockPos mainPos = getMainEggPos(level, pos);
        if (mainPos != null) {
            level.neighborChanged(mainPos, block, fromPos);
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockPos mainPos = getMainEggPos(level, pos);
        if (mainPos != null) {
            double offsetX = (mainPos.getX() - pos.getX()) * 16.0D;
            double offsetY = (mainPos.getY() - pos.getY()) * 16.0D;
            double offsetZ = (mainPos.getZ() - pos.getZ()) * 16.0D;
            return Block.box(offsetX, offsetY, offsetZ, offsetX + 32.0D, offsetY + 32.0D, offsetZ + 32.0D);
        }
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    private BlockPos getMainEggPos(BlockGetter level, BlockPos dummyPos) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dy = -1; dy <= 0; dy++) {
                for (int dz = -1; dz <= 0; dz++) {
                    BlockPos p = dummyPos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(p);
                    if (state.is(Blocks.DRAGON_EGG)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        BlockPos mainPos = getMainEggPos(level, pos);
        if (mainPos != null) {
            level.getBlockState(mainPos).attack(level, mainPos, player);
        }
    }

    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos mainPos = getMainEggPos((Level)level, pos);
        if (mainPos != null) {
            return level.getBlockState(mainPos).useItemOn(stack, level, player, hand, new BlockHitResult(hit.getLocation(), hit.getDirection(), mainPos, hit.isInside()));
        }
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos mainPos = getMainEggPos((Level)level, pos);
        if (mainPos != null) {
            return level.getBlockState(mainPos).useWithoutItem(level, player, new BlockHitResult(hit.getLocation(), hit.getDirection(), mainPos, hit.isInside()));
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos mainPos = getMainEggPos((Level)level, pos);
        if (mainPos != null) {
            level.destroyBlock(mainPos, false, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
