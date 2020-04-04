package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.wireless.KevlarNetwork;
import com.notvanilla.kevlar.wireless.KevlarNode;
import com.notvanilla.kevlar.wireless.NodeType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WirelessBlock<N extends KevlarNode> extends Block {

    public static final EnumProperty<DyeColor> COLOR = KevlarProperties.COLOR;

    protected final NodeType nodeType;

    public WirelessBlock(NodeType nodeType, Settings settings) {
        super(settings);
        this.nodeType = nodeType;
    }

    protected abstract KevlarNetwork<N> getNetwork(ServerWorld world);

    protected abstract N createNode(World world, BlockPos pos, BlockState state);

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof DyeItem) {
            DyeColor color = ((DyeItem) stack.getItem()).getColor();
            if (color != state.get(COLOR)) {
                world.setBlockState(pos, state.with(COLOR, color));
                if (!player.abilities.creativeMode) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        if (!world.isClient) {
            getNetwork((ServerWorld) world).addNode(createNode(world, pos, state));
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient) {
            getNetwork((ServerWorld) world).removeNode(pos);
        }
    }
}
