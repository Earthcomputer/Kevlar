package com.notvanilla.kevlar;

import com.notvanilla.kevlar.block.*;
import com.notvanilla.kevlar.ducks.IAnimalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.notvanilla.kevlar.block.FeederBlock.FEED_TYPE;

public class FeederGoal extends Goal {

    private AnimalEntity mob;
    private Item cropType;
    private int cooldown = 0;
    private BlockPos closestFeederPos;
    private boolean active;
    private final int COOLDOWN_TICKS = 1000;
    private double speed;

    public FeederGoal(AnimalEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));

        cropType = FeedingUtil.animalToFastBreedingPlant.get(mob);

    }

    @Override
    public boolean canStart() {



        if (mob.getBreedingAge() > 0 ||  cooldown > 0) {
            cooldown --;
            return false;
        } else {
            ServerWorld serverWorld = mob.getEntityWorld().getServer().getWorld(mob.dimension);
            cropType = FeedingUtil.animalToFastBreedingPlant.get(mob.getType());


            if (cropType != null) {
                PointOfInterest feederPOI = getClosestPOI(mob.getBlockPos(), serverWorld);


                if (feederPOI == null) {
                    return false;
                } else {
                    closestFeederPos = feederPOI.getPos();

                    return true;
                }
            }
            return false;
        }


    }


    @Override
    public boolean shouldContinue() {
       if (mob.getBreedingAge() > 0 && cooldown > 0) {
           cooldown--;
           return false;
       }
       return true;
    }

    @Override
    public boolean canStop() {
        return cooldown != 0;
    }



    @Override
    public void start() {
        active = true;
    }

    @Override
    public void stop() {
        active = false;
        cooldown = COOLDOWN_TICKS;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if(cooldown == 0) {
            World world = mob.world;
            BlockPos mobPos = mob.getBlockPos();
            double sqrDist = mobPos.getSquaredDistance(closestFeederPos);
            boolean isFeederEmpty = world.getBlockState(closestFeederPos).get(FeederBlock.LEVEL) == 0;
            if (sqrDist < 4) {
                mob.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(closestFeederPos));
                if (mob.getBreedingAge() == 0 && mob.canEat() && !isFeederEmpty) {

                    mob.lovePlayer(null);
                    FeederBlock.eat(mob.getEntityWorld(), world.getBlockState(closestFeederPos), closestFeederPos);
                    cooldown = COOLDOWN_TICKS;
                    if(world.getBlockState(closestFeederPos).get(FEED_TYPE) == FeedType.ALFALFA)
                        ((IAnimalEntity) mob).kevlarSetFastBreeding(true);


                } else if (mob.isBaby() && !isFeederEmpty) {
                    cooldown = COOLDOWN_TICKS;
                    mob.growUp((int) ((float) (-mob.getBreedingAge() / 20) * 0.1F), true);
                    FeederBlock.eat(mob.getEntityWorld(), world.getBlockState(closestFeederPos), closestFeederPos);
                }
            } else if (sqrDist < 400) {
                mob.getNavigation().startMovingTo(closestFeederPos.getX(), closestFeederPos.getY() - 1, closestFeederPos.getZ(), speed);
            } else {
                mob.getNavigation().stop();
            }
        }
    }



    private PointOfInterest getClosestPOI(BlockPos pos, ServerWorld world) {
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        pointOfInterestStorage.method_22439(world, pos, 128);

        List<PointOfInterest> pointsOfInterest = pointOfInterestStorage.method_22383(
                poiType-> poiType == KevlarPointOfInterestTypes.FEEDER,
                pos,
                20,
                PointOfInterestStorage.OccupationStatus.ANY
        ).collect(Collectors.toList());



        PointOfInterest poi = null;
        double sqrDistance = Double.POSITIVE_INFINITY;
        for(PointOfInterest p: pointsOfInterest) {
            double tempSqrDist = p.getPos().getSquaredDistance(pos);
            BlockState blockStateAtPOI = world.getBlockState(p.getPos());
            if(
                    tempSqrDist < sqrDistance
                    && blockStateAtPOI.getBlock() == KevlarBlocks.FEEDER
                    && blockStateAtPOI.get(FEED_TYPE) != FeedType.EMPTY //This needs to be changed to support other crops at some point
            ) {
                sqrDistance = tempSqrDist;
                poi = p;
            }
        }



        return poi;
    }
}
