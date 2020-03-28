package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.block.entity.BlockBreakerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockBreakerBlock extends Block implements BlockEntityProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public BlockBreakerBlock(Settings settings) {
        super(settings);
        setDefaultState(
                stateManager.getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(TRIGGERED, false)
        );
    }

    @Override
    public ActionResult onUse(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        if (world.isClient)
            return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BlockBreakerBlockEntity) {
            player.openContainer((BlockBreakerBlockEntity) be);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 4;
    }

    @Override
    public void neighborUpdate(
            BlockState state,
            World world,
            BlockPos pos,
            Block block,
            BlockPos neighborPos,
            boolean moved
    ) {
        boolean isTriggered = state.get(TRIGGERED);
        boolean isPowered = world.isReceivingRedstonePower(pos)
                || world.isReceivingRedstonePower(pos.up());
        if (isTriggered && !isPowered) {
            world.setBlockState(pos, state.with(TRIGGERED, false), 4);
        } else if (!isTriggered && isPowered) {
            world.getBlockTickScheduler().schedule(pos, this, getTickRate(world));
            world.setBlockState(pos, state.with(TRIGGERED, true), 4);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos posInFront = pos.offset(state.get(FACING));
        BlockState stateInFront = world.getBlockState(posInFront);
        if (stateInFront.isAir())
            return;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BlockBreakerBlockEntity) {
            BlockBreakerBlockEntity blockBreaker = (BlockBreakerBlockEntity) be;

            List<Integer> validSlots = new ArrayList<>(blockBreaker.getInvSize());
            for (int i = 0; i < blockBreaker.getInvSize(); i++) {
                ItemStack stack = blockBreaker.getInvStack(i);
                if (stack.isEffectiveOn(stateInFront)) {
                    validSlots.add(i);
                }
            }
            if (validSlots.isEmpty()) {
                for (int i = 0; i < blockBreaker.getInvSize(); i++) {
                    ItemStack stack = blockBreaker.getInvStack(i);
                    if (stack.getItem() instanceof ToolItem) {
                        validSlots.add(i);
                    }
                }
                if (validSlots.isEmpty())
                    return;
            }

            int slot = validSlots.get(random.nextInt(validSlots.size()));
            ItemStack stack = blockBreaker.getInvStack(slot);

            if (stack.damage(1, random, null)) {
                blockBreaker.setInvStack(slot, ItemStack.EMPTY);
            }

            world.breakBlock(posInFront, true);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new BlockBreakerBlockEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
}
