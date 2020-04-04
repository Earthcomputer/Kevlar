package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.ducks.IServerWorld;
import com.notvanilla.kevlar.wireless.NodeType;
import com.notvanilla.kevlar.wireless.RedstoneNetwork;
import com.notvanilla.kevlar.wireless.RedstoneNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WirelessRedstoneBlock extends WirelessBlock<RedstoneNode> {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public WirelessRedstoneBlock(NodeType nodeType, Settings settings) {
        super(nodeType, settings);
        setDefaultState(stateManager.getDefaultState().with(COLOR, DyeColor.WHITE).with(POWERED, false));
    }

    @Override
    public int getTickRate(WorldView world) {
        return 2;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        RedstoneNetwork network = getNetwork(world);
        RedstoneNode node = network.getNode(pos);
        if (node != null) {
            if (receiveSignal(network, node, world, pos, state)) {
                boolean powered = false;
                for (BlockPos powerSource : node.getPowerSources()) {
                    if (node.getPowerSourceRef(powerSource).getPower() > 0) {
                        powered = true;
                        break;
                    }
                }
                if (powered != state.get(POWERED)) {
                    world.setBlockState(pos, state.with(POWERED, powered));
                }

                onPowerChanged(world, pos);

                propagateSignal(network, world, pos, state.get(COLOR));
            }
        }
    }

    protected boolean receiveSignal(RedstoneNetwork network, RedstoneNode node, ServerWorld world, BlockPos pos, BlockState state) {
        MutableBoolean changed = new MutableBoolean(false);
        Set<BlockPos> sourcesToRemove = new HashSet<>(node.getPowerSources());

        // If this is a transmitter, don't remove its own power source (and don't update based on it)
        // Leave self-powering logic to subclasses
        sourcesToRemove.remove(pos);

        network.getNodesWithinDistance(pos)
                .filter(n -> n.getColor() == node.getColor())
                .forEach(n -> {
                    for (BlockPos powerSource : n.getPowerSources()) {
                        RedstoneNode.PowerSourceRef otherRef = n.getPowerSourceRef(powerSource);
                        RedstoneNode.PowerSourceRef thisRef = node.getPowerSourceRef(powerSource);
                        if (thisRef == null) {
                            thisRef = new RedstoneNode.PowerSourceRef(
                                    otherRef.getPower(),
                                    network.getDistance(node, n1 -> n1.getPos().equals(powerSource), n1 -> n1.getColor() == node.getColor())
                            );
                            node.setPowerSourceRef(powerSource, thisRef);
                            changed.setTrue();
                        } else if (isCloser(n, node, otherRef, thisRef)) {
                            sourcesToRemove.remove(powerSource);
                            if (thisRef.getPower() != otherRef.getPower()) {
                                thisRef.setPower(otherRef.getPower());
                                changed.setTrue();
                            }
                        }
                    }
                });

        sourcesToRemove.forEach(source -> node.setPowerSourceRef(source, null));
        if (!sourcesToRemove.isEmpty())
            changed.setTrue();

        return changed.booleanValue();
    }

    protected void propagateSignal(RedstoneNetwork network, ServerWorld world, BlockPos pos, DyeColor color) {
        network.getNodesWithinDistance(pos)
                .filter(node -> node.getColor() == color)
                .forEach(node -> loadAndSchedule(world, node.getPos()));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (shouldUpdate(oldState, state)) {
            super.onBlockAdded(state, world, pos, oldState, moved);
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                RedstoneNetwork network = getNetwork(serverWorld);
                network.recalculateDistances(); // TODO do something smarter than recalculate everything
                loadAndSchedule(serverWorld, pos);
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (shouldUpdate(state, newState)) {
            super.onBlockRemoved(state, world, pos, newState, moved);
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                RedstoneNetwork network = getNetwork(serverWorld);
                network.recalculateDistances();
                propagateSignal(network, serverWorld, pos, state.get(COLOR));
            }
        }
    }

    private void loadAndSchedule(ServerWorld world, BlockPos pos) {
        world.getChunkManager().addTicket(Kevlar.REDSTONE_3TICK_TICKET, new ChunkPos(pos), 1, pos);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof WirelessRedstoneBlock) {
            world.getBlockTickScheduler().schedule(pos, block, getTickRate(world));
        }
    }

    private boolean shouldUpdate(BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock() || oldState.get(COLOR) != newState.get(COLOR);
    }

    private boolean isCloser(RedstoneNode a, RedstoneNode b, RedstoneNode.PowerSourceRef sourceA, RedstoneNode.PowerSourceRef sourceB) {
        if (sourceA.getDistance() < sourceB.getDistance()) {
            return true;
        } else if (sourceA.getDistance() > sourceB.getDistance()) {
            return false;
        } else {
            return a.getPos().compareTo(b.getPos()) < 0;
        }
    }

    protected void onPowerChanged(World world, BlockPos pos) {
    }

    @Override
    protected RedstoneNetwork getNetwork(ServerWorld world) {
        return ((IServerWorld) world).getKevlarNetworks().redstone;
    }

    @Override
    protected RedstoneNode createNode(World world, BlockPos pos, BlockState state) {
        return new RedstoneNode(pos, nodeType, state.get(COLOR));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR, POWERED);
    }
}
