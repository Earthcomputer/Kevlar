package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.item.KevlarItems;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FeederBlock extends Block implements InventoryProvider {



    public static final IntProperty LEVEL = Properties.LEVEL_8;
    public static final EnumProperty<FeedType> FEED_TYPE = KevlarProperties.FEED_TYPE;
    private static final int INCREASE_PROBABILITY = 8; //1/8 is chance of increase

    public FeederBlock(Settings settings) {
        super(settings.nonOpaque());
        setDefaultState(
                getStateManager().getDefaultState().with(LEVEL, 0).with(FEED_TYPE, FeedType.EMPTY)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL).add(FEED_TYPE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stackInHand = player.getStackInHand(hand);
        Item itemInHand = stackInHand.getItem();

        if(state.get(LEVEL) == 0)
            world.setBlockState(pos, state.with(FEED_TYPE, FeedType.EMPTY));

        if (state.get(FEED_TYPE) == FeedType.EMPTY) {
            if (itemInHand == Items.WHEAT)
                world.setBlockState(pos, state.with(FEED_TYPE, FeedType.WHEAT));

            if (itemInHand == KevlarItems.ALFALFA)
                world.setBlockState(pos, state.with(FEED_TYPE, FeedType.ALFALFA));
        }


        if (itemInHand == world.getBlockState(pos).get(FEED_TYPE).getCrop()) {
            int level = state.get(LEVEL);
            if (level < 8 && !world.isClient) { //Is it okay to use the world random here like this?
                if (!player.abilities.creativeMode)
                    stackInHand.decrement(1);

                //increase level

                if(world.getRandom().nextInt(INCREASE_PROBABILITY) == 0)
                    world.setBlockState(pos, world.getBlockState(pos).with(LEVEL, level + 1));

                return ActionResult.SUCCESS;
            }

        }

        return ActionResult.PASS;

    }



    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(LEVEL);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateHorizontalAdjacent(pos, this);
    }

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return new FeederInventory(state, world, pos);
    }

    public Item getFilledItem(BlockState state) {
        return state.get(FEED_TYPE).getCrop();
    }

    public static void eat(World world, BlockState state, BlockPos pos) {
        int level = state.get(LEVEL);

        if (level != 0 && world.getRandom().nextInt(INCREASE_PROBABILITY) == 0)
            world.setBlockState(pos, state.with(LEVEL, level - 1));

    }

    public static void addToFeeder(IWorld world, BlockPos pos) {
        int level = world.getBlockState(pos).get(LEVEL);
        if(level < 8 &&world.getRandom().nextInt(INCREASE_PROBABILITY) == 0)
            world.setBlockState(pos, world.getBlockState(pos).with(LEVEL, level + 1),3 );

    }




    static class FeederInventory extends BasicInventory implements SidedInventory {
        private final BlockState state;
        private final IWorld world;
        private final BlockPos pos;
        private boolean dirty;

        public FeederInventory(BlockState state, IWorld world, BlockPos pos) {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        public int getInvMaxStackAmount() {
            return 1;
        }

        public int[] getInvAvailableSlots(Direction side) {
            return side == Direction.UP ? new int[]{0} : new int[0];
        }

        public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {

            if(!this.dirty && world.getBlockState(pos).get(LEVEL) < 8 && (stack.getItem() == KevlarItems.ALFALFA || stack.getItem() == Items.WHEAT)) {
                if(world.getBlockState(pos).get(FEED_TYPE) == FeedType.EMPTY    ) {
                    if(stack.getItem() == KevlarItems.ALFALFA)
                        world.setBlockState(pos, world.getBlockState(pos).with(FEED_TYPE, FeedType.ALFALFA), 3);

                    if(stack.getItem() == Items.WHEAT)
                        world.setBlockState(pos, world.getBlockState(pos).with(FEED_TYPE, FeedType.WHEAT), 3);


                    return true;
                } else {
                    return world.getBlockState(pos).get(FEED_TYPE).getCrop() == stack.getItem();
                }
            }

            return false;

        }

        public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
            return false;
        }

        public void markDirty() {
            ItemStack itemStack = this.getInvStack(0);
            if (!itemStack.isEmpty()) {
                this.dirty = true;
                addToFeeder(world, pos);
                this.removeInvStack(0);
            }

        }
    }

}
