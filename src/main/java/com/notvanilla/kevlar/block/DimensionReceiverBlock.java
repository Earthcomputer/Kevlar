package com.notvanilla.kevlar.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

import java.util.Random;

public class DimensionReceiverBlock extends Block {


    public static final BooleanProperty POWERED = Properties.POWERED;
    public final DimensionType dimension;

    public DimensionReceiverBlock(Settings settings, DimensionType dimension) {
        super(settings.nonOpaque());
        setDefaultState(
                stateManager.getDefaultState().with(POWERED, false)
        );
        this.dimension = dimension;
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        if(state.get(POWERED) == true)
            return 15;

        return 0;
    }

    public DimensionType getDimensionType() {
        return dimension;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED) == true) {
            if (dimension == DimensionType.THE_NETHER)
                world.setBlockState(pos, KevlarBlocks.NETHER_RECEIVER.getDefaultState().with(POWERED, false));

            if (dimension == DimensionType.THE_END)
                world.setBlockState(pos, KevlarBlocks.END_RECEIVER.getDefaultState().with(POWERED, false));

            if (dimension == DimensionType.OVERWORLD)
                world.setBlockState(pos, KevlarBlocks.OVERWORLD_RECEIVER.getDefaultState().with(POWERED, false));
        }
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 2;
    }
}
