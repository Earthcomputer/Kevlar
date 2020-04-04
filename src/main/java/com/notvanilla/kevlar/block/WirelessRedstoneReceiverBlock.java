package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.wireless.NodeType;
import com.notvanilla.kevlar.wireless.RedstoneNetwork;
import com.notvanilla.kevlar.wireless.RedstoneNode;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WirelessRedstoneReceiverBlock extends WirelessRedstoneBlock {

    public WirelessRedstoneReceiverBlock(Settings settings) {
        super(NodeType.RECEIVER, settings);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction facing) {
        if (!(world instanceof ServerWorld))
            return 0;

        RedstoneNetwork network = getNetwork((ServerWorld) world);
        RedstoneNode node = network.getNode(pos);

        int power = 0;
        for (BlockPos powerSource : node.getPowerSources()) {
            power = Math.max(power, node.getPowerSourceRef(powerSource).getPower());
        }

        return power;
    }

    @Override
    protected void onPowerChanged(World world, BlockPos pos) {
        world.updateNeighbors(pos, this);
    }
}
