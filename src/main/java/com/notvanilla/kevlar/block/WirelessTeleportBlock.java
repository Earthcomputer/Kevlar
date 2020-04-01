package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.ducks.IServerWorld;
import com.notvanilla.kevlar.wireless.KevlarNetwork;
import com.notvanilla.kevlar.wireless.NodeType;
import com.notvanilla.kevlar.wireless.TeleportationNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.World;

import java.util.Random;

public class WirelessTeleportBlock extends WirelessBlock<TeleportationNode> {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final EnumProperty<DyeColor> COLOR = KevlarProperties.COLOR;

    private final NodeType nodeType;

    public WirelessTeleportBlock(NodeType nodeType, Settings settings) {
        super(settings);
        this.nodeType = nodeType;
        setDefaultState(
                stateManager.getDefaultState()
                        .with(POWERED, false)
                        .with(COLOR, DyeColor.WHITE)
        );
    }

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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERED, false));
    }

    @Override
    protected KevlarNetwork<TeleportationNode> getNetwork(ServerWorld world) {
        return ((IServerWorld) world).getKevlarNetworks().teleportation;
    }

    @Override
    protected TeleportationNode createNode(World world, BlockPos pos, BlockState state) {
        return new TeleportationNode(pos, nodeType, state.get(COLOR));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, COLOR);
    }

}
