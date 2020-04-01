package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.block.entity.WirelessTeleportTransmitterBlockEntity;
import com.notvanilla.kevlar.wireless.KevlarNetwork;
import com.notvanilla.kevlar.wireless.NodeType;
import com.notvanilla.kevlar.wireless.TeleportationNode;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class WirelessTeleportTransmitterBlock extends WirelessTeleportBlock implements BlockEntityProvider {

    public WirelessTeleportTransmitterBlock(Settings settings) {
        super(NodeType.TRANSMITTER, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (super.onUse(state, world, pos, player, hand, hit) == ActionResult.SUCCESS) {
                return ActionResult.SUCCESS;
            }

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof WirelessTeleportTransmitterBlockEntity) {
                WirelessTeleportTransmitterBlockEntity teleportTransmitter = (WirelessTeleportTransmitterBlockEntity) be;
                if (teleportTransmitter.hasPath())
                    return ActionResult.SUCCESS;

                KevlarNetwork<TeleportationNode> network = getNetwork((ServerWorld) world);
                TeleportationNode nodeHere = network.getNode(pos);
                if (nodeHere == null)
                    return ActionResult.SUCCESS;

                DyeColor colorHere = state.get(COLOR);
                List<TeleportationNode> path = network.shortestPath(nodeHere, node -> node.getType() == NodeType.RECEIVER, node -> node.getColor() == colorHere);
                if (path == null)
                    return ActionResult.SUCCESS;

                teleportTransmitter.startTeleport(player, path);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new WirelessTeleportTransmitterBlockEntity();
    }
}
