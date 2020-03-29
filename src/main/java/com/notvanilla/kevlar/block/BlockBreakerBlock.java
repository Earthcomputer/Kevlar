package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.block.entity.BlockBreakerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

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
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomName()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BlockBreakerBlockEntity) {
                ((BlockBreakerBlockEntity) be).setCustomName(stack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BlockBreakerBlockEntity) {
                BlockBreakerBlockEntity blockBreaker = (BlockBreakerBlockEntity) be;
                if (blockBreaker.isMining())
                    blockBreaker.abortMining();
                ItemScatterer.spawn(world, pos, blockBreaker);
                world.updateHorizontalAdjacent(pos, this);
            }

            super.onBlockRemoved(state, world, pos, newState, moved);
        }
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
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BlockBreakerBlockEntity) {
            BlockBreakerBlockEntity blockBreaker = (BlockBreakerBlockEntity) be;

            if (blockBreaker.isMining())
                return;

            BlockPos posInFront = pos.offset(state.get(FACING));
            BlockState stateInFront = world.getBlockState(posInFront);

            if (stateInFront.getBlock() == Blocks.FIRE) {
                boolean hasItem = false;
                for (int i = 0; i < blockBreaker.getInvSize(); i++) {
                    if (!blockBreaker.getInvStack(i).isEmpty()) {
                        hasItem = true;
                        break;
                    }
                }
                if (!hasItem)
                    return;
                world.extinguishFire(null, pos, state.get(FACING));
            }

            if (stateInFront.isAir())
                return;
            if (stateInFront.getHardness(world, posInFront) == -1)
                return;

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
            float initialProgress = calcBlockBreakingDelta(world, posInFront, stack, stateInFront);
            if (initialProgress >= 1) {
                // insta-mine
                blockBreaker.startMining(slot, 0);
                blockBreaker.finishMining();
            } else {
                blockBreaker.startMining(slot, initialProgress);
            }
        }
    }

    public static float calcBlockBreakingDelta(BlockView world, BlockPos pos, ItemStack stack, BlockState state) {
        // swords on bamboo is an exception
        if (stack.getItem() instanceof SwordItem) {
            if (state.getBlock() instanceof BambooBlock || state.getBlock() instanceof BambooSaplingBlock) {
                return 1;
            }
        }

        float hardness = state.getHardness(world, pos);
        if (hardness == -1) // unbreakable
            return 0;

        boolean effectiveTool = state.getMaterial().canBreakByHand() || stack.isEffectiveOn(state);
        int penalty = effectiveTool ? 30 : 100;
        float miningSpeed = stack.getMiningSpeed(state);
        if (miningSpeed > 1) {
            // check efficiency for tools
            int efficiencyLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
            if (efficiencyLevel > 0) {
                miningSpeed += efficiencyLevel * efficiencyLevel + 1;
            }
        }

        return miningSpeed / penalty / hardness;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Container.calculateComparatorOutput(world.getBlockEntity(pos));
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
