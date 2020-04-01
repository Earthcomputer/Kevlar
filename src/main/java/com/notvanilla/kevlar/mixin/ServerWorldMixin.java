package com.notvanilla.kevlar.mixin;

import com.notvanilla.kevlar.ducks.IServerWorld;
import com.notvanilla.kevlar.wireless.WorldNetworks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.function.BiFunction;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements IServerWorld {

    @Shadow @Final private WorldSaveHandler worldSaveHandler;

    @Unique
    private WorldNetworks networks = new WorldNetworks();

    protected ServerWorldMixin(LevelProperties levelProperties, DimensionType dimensionType, BiFunction<World, Dimension, ChunkManager> chunkManagerProvider, Profiler profiler, boolean isClient) {
        super(levelProperties, dimensionType, chunkManagerProvider, profiler, isClient);
    }

    @Override
    public WorldNetworks getKevlarNetworks() {
        return networks;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onLoadLevel(CallbackInfo ci) {
        File kevlarDir = new File(dimension.getType().getSaveDirectory(worldSaveHandler.getWorldDir()), "kevlar");
        File networksFile = new File(kevlarDir, "networks.dat");
        if (networksFile.isFile()) {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(networksFile))) {
                networks.fromTag(NbtIo.readCompressed(in));
            } catch (IOException e) {
                LOGGER.error("Failed to read Kevlar wireless networks", e);
            }
        }
    }

    @Inject(method = "saveLevel", at = @At("RETURN"))
    private void onSaveLevel(CallbackInfo ci) {
        File kevlarDir = new File(dimension.getType().getSaveDirectory(worldSaveHandler.getWorldDir()), "kevlar");
        if (!kevlarDir.exists() && !kevlarDir.mkdirs()) {
            LOGGER.error("Failed to create Kevlar directory");
            return;
        }
        File networksFile = new File(kevlarDir, "networks.dat");
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(networksFile))) {
            NbtIo.writeCompressed(networks.toTag(new CompoundTag()), out);
        } catch (IOException e) {
            LOGGER.error("Failed to write Kevlar wireless networks", e);
        }
    }
}
