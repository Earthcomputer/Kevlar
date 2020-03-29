package com.notvanilla.kevlar.block;

import com.google.common.collect.ImmutableMap;
import com.notvanilla.kevlar.block.entity.PlanterBlockEntity;
import com.notvanilla.kevlar.item.KevlarItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlanterBlock extends Block implements BlockEntityProvider{

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if(itemStack.hasCustomName()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof PlanterBlockEntity) {
                ( (PlanterBlockEntity) be).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {

        if(state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);

            if(be instanceof PlanterBlockEntity) {
                ItemScatterer.spawn(world, pos, (PlanterBlockEntity)be);

            }
            world.updateHorizontalAdjacent(pos, this);
            super.onBlockRemoved(state, world, pos, newState, moved);
        }

    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Container.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public static final Lazy<Map<Item, Block>> itemToBlockMap = new Lazy<>(() -> ImmutableMap.<Item, Block>builder()
            .put(Items.WHEAT_SEEDS, Blocks.WHEAT)
            .put(Items.MELON_SEEDS, Blocks.MELON_STEM)
            .put(Items.PUMPKIN_SEEDS, Blocks.PUMPKIN_STEM)
            .put(Items.BEETROOT_SEEDS, Blocks.BEETROOTS)
            .put(Items.CARROT, Blocks.CARROTS)
            .put(Items.POTATO, Blocks.POTATOES)
            .put(Items.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM)
            .put(Items.RED_MUSHROOM, Blocks.RED_MUSHROOM)
            .put(Items.NETHER_WART, Blocks.NETHER_WART)
            .put(Items.CHORUS_FLOWER, Blocks.CHORUS_FLOWER)
            .put(Items.COCOA_BEANS, Blocks.COCOA)
            .put(Items.OAK_SAPLING, Blocks.OAK_SAPLING)
            .put(Items.SPRUCE_SAPLING, Blocks.SPRUCE_SAPLING)
            .put(Items.BIRCH_SAPLING, Blocks.BIRCH_SAPLING)
            .put(Items.JUNGLE_SAPLING, Blocks.JUNGLE_SAPLING)
            .put(Items.ACACIA_SAPLING, Blocks.ACACIA_SAPLING)
            .put(Items.DARK_OAK_SAPLING, Blocks.DARK_OAK_SAPLING)
            .put(Items.SUGAR_CANE, Blocks.SUGAR_CANE)
            .put(KevlarItems.ALFALFA_SEEDS, KevlarBlocks.ALFALFA)
            .build());

    public PlanterBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TRIGGERED, false)
        );

    }


    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new PlanterBlockEntity();

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void neighborUpdate(BlockState state,
                               World world,
                               BlockPos pos,
                               Block block,
                               BlockPos neighborPos,
                               boolean moved) {
        boolean isTriggered = state.get(TRIGGERED);
        boolean isPowered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());

        if(isTriggered && !isPowered)
            world.setBlockState(pos, state.with(TRIGGERED, false), 4);
        else if(!isTriggered && isPowered) {
            world.setBlockState(pos, state.with(TRIGGERED, true), 4);
            world.getBlockTickScheduler().schedule(pos, this, getTickRate(world));
        }

    }


    @Override
    public int getTickRate(WorldView worldView) {
        return 4;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient)
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof PlanterBlockEntity) {
            player.openContainer((PlanterBlockEntity) blockEntity);

        }

        return ActionResult.SUCCESS;
    }
    private void plant(BlockState state, World world, PlanterBlockEntity inventory, BlockPos posToPlant, Random random) {

            //Pick full slot randomly
            ItemStack stackToUse = ItemStack.EMPTY;

            while(stackToUse.isEmpty()) {
                stackToUse = (inventory.getInvStack(random.nextInt(9)));
            }


            Item itemToPlant = stackToUse.getItem();

            //Figure out how to know if you can place it and get the block from the item

            Block blockToPlant = itemToBlockMap.get().get(itemToPlant); // :thonk:

            if(!(blockToPlant == null)) {
                BlockState blockStateToPlant = blockToPlant.getDefaultState();


                if (blockStateToPlant.canPlaceAt(world, posToPlant)) {
                    stackToUse.decrement(1);
                    world.setBlockState(posToPlant, blockStateToPlant);
                    return;
                }

                if(blockToPlant == Blocks.COCOA) {
                    //Horrible (now nice code :)) code ahead :)

                    for(Direction dir : Direction.Type.HORIZONTAL) {
                        BlockState cocoaState = Blocks.COCOA.getDefaultState().with(FACING, dir);
                        if(cocoaState.canPlaceAt(world, posToPlant)) {
                            stackToUse.decrement(1);
                            world.setBlockState(posToPlant, cocoaState);
                            return;
                        }
                    }

                }
            }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Direction facing = state.get(FACING);

        BlockPos posInfront = pos.offset(facing);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if(!(blockEntity instanceof PlanterBlockEntity) )
            return;

        PlanterBlockEntity planterBlockEntity = (PlanterBlockEntity) blockEntity;

        if(world.getBlockState(posInfront).isAir() && !planterBlockEntity.isEmpty()) {

            plant(state, world, planterBlockEntity, posInfront, random);


            BlockSoundGroup wheatSoundGroup = Blocks.WHEAT.getDefaultState().getSoundGroup();//TODO(Dan) fix this so its less disgustingly ugly

            world.playSound(null, pos, wheatSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, wheatSoundGroup.volume, wheatSoundGroup.pitch);
            //TODO(Dan) Add particles here maybe
        } else {
            world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1f, 1.2f);
            world.playLevelEvent(2000, pos, facing.getId());//This spawns partiles identical to in a dispenser
        }


    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
    }
}
