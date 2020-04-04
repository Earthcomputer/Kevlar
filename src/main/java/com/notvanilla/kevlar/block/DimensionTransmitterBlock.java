package com.notvanilla.kevlar.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class DimensionTransmitterBlock extends Block {



    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;//used to see whether the neighbor update has either powered or unpowered block
    public final DimensionType dimension;
    private boolean isTriggering = false;

    public DimensionTransmitterBlock(Settings settings, DimensionType dimension) {
        super(settings.nonOpaque());
        this.dimension = dimension;
        setDefaultState(
                stateManager.getDefaultState().with(TRIGGERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
       updateBlock(world, pos, state);
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        updateBlock(world,pos, state);

    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        DimensionType currentDimension = world.getDimension().getType();
        if(isTriggering) {
            sendPulseToDimension(pos, world.getServer().getWorld(dimension), currentDimension);
            world.setBlockState(pos, state.with(TRIGGERED, false));
        }
    }

    private void updateBlock(World world, BlockPos pos, BlockState state) {
        if(!world.isClient) {

            boolean isTriggered = state.get(TRIGGERED);
            boolean isPowered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());

            if(!isTriggered && isPowered) {
                world.setBlockState(pos, state.with(TRIGGERED, true));
                isTriggering = true;
                world.getBlockTickScheduler().schedule(pos, this, getTickRate(world));


                System.err.println("Turning on");
            }

        }
    }

    private void sendPulseToDimension(BlockPos pos, ServerWorld world, DimensionType currentDimension) {

        if(dimension != currentDimension) {

            BlockPos posInNewDimension = pos;
            if(dimension == DimensionType.THE_NETHER) {
                int newX = pos.getX() / 8;
                int newZ = pos.getZ() / 8;
                posInNewDimension = new BlockPos(newX, pos.getY(), newZ);
            }

            if(dimension == DimensionType.OVERWORLD && currentDimension == DimensionType.THE_NETHER) {
                int newX = pos.getX() * 8;
                int newZ = pos.getZ() * 8;
                posInNewDimension = new BlockPos(newX, pos.getY(), newZ);
            }


            PointOfInterest poi = getClosestPOI(posInNewDimension, world);

            if (poi != null) {
                world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(pos), 3, pos);



                if (dimension == DimensionType.THE_NETHER) {
                    world.setBlockState(
                            poi.getPos(),
                            KevlarBlocks.NETHER_RECEIVER.getDefaultState().with(DimensionReceiverBlock.POWERED, true));
                    world.getBlockTickScheduler().schedule(poi.getPos(), KevlarBlocks.NETHER_RECEIVER, 2);
                }

                if (dimension == DimensionType.THE_END) {
                    world.setBlockState(
                            poi.getPos(),
                            KevlarBlocks.END_RECEIVER.getDefaultState().with(DimensionReceiverBlock.POWERED, true));
                    world.getBlockTickScheduler().schedule(poi.getPos(), KevlarBlocks.END_RECEIVER, 2);
                }

                if(dimension == DimensionType.OVERWORLD) {
                    world.setBlockState(
                            poi.getPos(),
                            KevlarBlocks.OVERWORLD_RECEIVER.getDefaultState().with(DimensionReceiverBlock.POWERED, true));
                    world.getBlockTickScheduler().schedule(poi.getPos(), KevlarBlocks.OVERWORLD_RECEIVER, 2);
                }

            }
        }

    }

    private PointOfInterest getClosestPOI(BlockPos pos, ServerWorld world) {
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        pointOfInterestStorage.method_22439(world, pos, 128);

        List<PointOfInterest> pointsOfInterest = pointOfInterestStorage.method_22383(
                poiType-> poiType == KevlarPointOfInterestTypes.DIMENSION_RECEIVER,
                pos,
                5,
                PointOfInterestStorage.OccupationStatus.ANY
        ).collect(Collectors.toList());



        PointOfInterest poi = null;
        double sqrDistance = Double.POSITIVE_INFINITY;
        for(PointOfInterest p: pointsOfInterest) {
            double tempSqrDist = p.getPos().getSquaredDistance(pos);
            Block blockAtPOI = world.getBlockState(p.getPos()).getBlock();
            if(tempSqrDist < sqrDistance
                    && blockAtPOI instanceof DimensionReceiverBlock
                    && ((DimensionReceiverBlock) blockAtPOI).getDimensionType() == dimension) { //TODO(Dan) Make sure this doesnt just stay as nether
                sqrDistance = tempSqrDist;
                poi = p;
            }
        }


        return poi;
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 4;
    }
}
