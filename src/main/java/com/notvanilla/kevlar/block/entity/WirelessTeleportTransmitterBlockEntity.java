package com.notvanilla.kevlar.block.entity;

import com.google.common.collect.ImmutableSet;
import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.block.KevlarBlocks;
import com.notvanilla.kevlar.wireless.TeleportationNode;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WirelessTeleportTransmitterBlockEntity extends BlockEntity implements Tickable {

    private List<TeleportationNode> path = null;
    private UUID teleportee = null;
    private int warmup;
    private int cost;

    public WirelessTeleportTransmitterBlockEntity() {
        super(KevlarBlockEntities.WIRELESS_TELEPORT_TRANSMITTER);
    }

    public void startTeleport(PlayerEntity player, List<TeleportationNode> path) {
        this.path = path;
        this.teleportee = player.getUuid();
        warmup = 0;
        cost = 0;
    }

    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    @Override
    public void tick() {
        if (!hasPath() || world == null || world.isClient)
            return;

        ServerWorld world = (ServerWorld) this.world;

        warmup++;
        if (warmup >= 10) {
            warmup = 0;
            cost++;
            TeleportationNode node = path.remove(0);
            world.getChunkManager().addTicket(Kevlar.REDSTONE_11TICK_TICKET, new ChunkPos(node.getPos()), 1, node.getPos());
            BlockState state = world.getBlockState(node.getPos());
            Block block = state.getBlock();
            if (block == KevlarBlocks.WIRELESS_TELEPORT_REPEATER
                    || block == KevlarBlocks.WIRELESS_TELEPORT_TRANSMITTER
                    || block == KevlarBlocks.WIRELESS_TELEPORT_RECEIVER) {
                world.setBlockState(node.getPos(), state.with(Properties.POWERED, true));
                world.getBlockTickScheduler().schedule(node.getPos(), block, 10);
                if (path.isEmpty()) {
                    teleport(node.getPos());
                    path = null;
                }
            }
        }
    }

    private void teleport(BlockPos destination) {
        assert world != null;

        if (teleportee == null)
            return;
        PlayerEntity player = world.getPlayerByUuid(teleportee);
        if (player == null)
            return;

        ((ServerWorld) world).getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos(destination), 1, player.getEntityId());

        BlockEntity be = world.getBlockEntity(destination);
        if (be instanceof WirelessTeleportReceiverBlockEntity) {
            WirelessTeleportReceiverBlockEntity receiver = (WirelessTeleportReceiverBlockEntity) be;
            if (receiver.getInvStack(0).getCount() < cost)
                return;
            receiver.getInvStack(0).decrement(cost);
        }

        world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1, false);

        player.stopRiding();
        if (player.isSleeping())
            player.wakeUp(true, true);

        // TODO smart search for position to teleport to
        ((ServerPlayerEntity) player).networkHandler.teleportRequest(
                destination.getX() + 0.5,
                destination.getY() + 1,
                destination.getZ() + 0.5,
                player.yaw,
                player.pitch,
                ImmutableSet.of(PlayerPositionLookS2CPacket.Flag.X, PlayerPositionLookS2CPacket.Flag.Y, PlayerPositionLookS2CPacket.Flag.Z)
        );

        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        path = null;
        if (tag.contains("Path", NbtType.LIST)) {
            ListTag pathNbt = tag.getList("Path", NbtType.COMPOUND);
            path = new ArrayList<>(pathNbt.size());
            for (int i = 0; i < pathNbt.size(); i++) {
                TeleportationNode node = new TeleportationNode();
                node.fromTag(pathNbt.getCompound(i));
                path.add(node);
            }
        }

        teleportee = null;
        if (tag.containsUuid("Teleportee")) {
            teleportee = tag.getUuid("Teleportee");
        }

        warmup = tag.getByte("Warmup");
        cost = tag.getInt("Cost");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        if (path != null) {
            ListTag pathNbt = new ListTag();
            for (TeleportationNode node : path) {
                pathNbt.add(node.toTag(new CompoundTag()));
            }
            tag.put("Path", pathNbt);
        }

        if (teleportee != null) {
            tag.putUuid("Teleportee", teleportee);
        }

        tag.putByte("Warmup", (byte) warmup);
        tag.putInt("Cost", cost);

        return tag;
    }
}
