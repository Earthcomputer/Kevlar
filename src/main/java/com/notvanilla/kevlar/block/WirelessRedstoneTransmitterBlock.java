package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.wireless.NodeType;
import com.notvanilla.kevlar.wireless.RedstoneNetwork;
import com.notvanilla.kevlar.wireless.RedstoneNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WirelessRedstoneTransmitterBlock extends WirelessRedstoneBlock {

    public WirelessRedstoneTransmitterBlock(Settings settings) {
        super(NodeType.TRANSMITTER, settings);
    }

    @Override
    protected boolean receiveSignal(RedstoneNetwork network, RedstoneNode node, ServerWorld world, BlockPos pos, BlockState state) {
        boolean changed = super.receiveSignal(network, node, world, pos, state);

        RedstoneNode.PowerSourceRef selfRef = node.getPowerSourceRef(pos);
        if (selfRef == null) {
            selfRef = new RedstoneNode.PowerSourceRef(0, 0);
            node.setPowerSourceRef(pos, selfRef);
        }

        int oldPower = selfRef.getPower();
        int newPower = world.getReceivedRedstonePower(pos);
        if (oldPower != newPower) {
            selfRef.setPower(newPower);
            changed = true;
        }

        return changed;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        super.neighborUpdate(state, world, pos, block, neighborPos, moved);
        world.getBlockTickScheduler().schedule(pos, this, getTickRate(world));
    }
}
