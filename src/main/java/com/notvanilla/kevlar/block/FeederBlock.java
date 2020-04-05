package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.item.KevlarItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FeederBlock extends Block implements InventoryProvider {



    public static final IntProperty LEVEL = Properties.LEVEL_8;
    public static final EnumProperty<FeedType> FEED_TYPE = KevlarProperties.FEED_TYPE;
    private static final int INCREASE_PROBABILITY = 8; //1/8 is chance of increase

    public FeederBlock(Settings settings) {
        super(settings);
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


        if (itemInHand == state.get(FEED_TYPE).getCrop()) {
            int level = state.get(LEVEL);
            if (level < 8 && !world.isClient) { //Is it okay to use the world random here like this?
                if (!player.abilities.creativeMode)
                    stackInHand.decrement(1);

                //increase level

                if(world.getRandom().nextInt(INCREASE_PROBABILITY) == 0)
                    world.setBlockState(pos, state.with(LEVEL, level + 1));

                return ActionResult.SUCCESS;
            }

        }

        return ActionResult.PASS;

    }

    @Override
    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return null;
    }

    public Item getFilledItem(BlockState state) {
        return state.get(FEED_TYPE).getCrop();
    }

    public static void eat(World world, BlockState state, BlockPos pos) {
        int level = state.get(LEVEL);

        if (level != 0 && world.getRandom().nextInt(INCREASE_PROBABILITY) == 0)
            world.setBlockState(pos, state.with(LEVEL, level - 1));

    }


}
