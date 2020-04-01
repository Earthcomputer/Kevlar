package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.block.entity.WirelessTeleportTransmitterBlockEntity;
import com.notvanilla.kevlar.ducks.IServerWorld;
import com.notvanilla.kevlar.wireless.KevlarNetwork;
import com.notvanilla.kevlar.wireless.NodeType;
import com.notvanilla.kevlar.wireless.TeleportationNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class WirelessTeleportTransmitterBlock extends WirelessBlock<TeleportationNode> implements BlockEntityProvider {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<DyeColor> COLOR = KevlarProperties.COLOR;

    public WirelessTeleportTransmitterBlock(Settings settings) {
        super(settings);
        setDefaultState(
                stateManager.getDefaultState()
                        .with(POWERED, false)
                        .with(COLOR, DyeColor.WHITE)
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERED, false));
    }

    @Override
    protected KevlarNetwork<TeleportationNode> getNetwork(ServerWorld world) {
        return ((IServerWorld) world).getKevlarNetworks().teleportation;
    }

    @Override
    protected TeleportationNode createNode(World world, BlockPos pos, BlockState state) {
        return new TeleportationNode(pos, NodeType.TRANSMITTER, state.get(COLOR));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new WirelessTeleportTransmitterBlockEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, COLOR);
    }
}
