package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.wireless.KevlarNetwork;
import com.notvanilla.kevlar.wireless.KevlarNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WirelessBlock<N extends KevlarNode> extends Block {
    public WirelessBlock(Settings settings) {
        super(settings);
    }

    protected abstract KevlarNetwork<N> getNetwork(ServerWorld world);

    protected abstract N createNode(World world, BlockPos pos, BlockState state);

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (!world.isClient) {
            getNetwork((ServerWorld) world).addNode(createNode(world, pos, state));
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient) {
            getNetwork((ServerWorld) world).removeNode(pos);
        }
    }
}
