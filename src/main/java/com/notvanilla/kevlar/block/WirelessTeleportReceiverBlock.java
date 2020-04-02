package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.block.entity.WirelessTeleportReceiverBlockEntity;
import com.notvanilla.kevlar.container.KevlarContainers;
import com.notvanilla.kevlar.wireless.NodeType;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WirelessTeleportReceiverBlock extends WirelessTeleportBlock implements BlockEntityProvider {

    public WirelessTeleportReceiverBlock(Settings settings) {
        super(NodeType.RECEIVER, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient)
            return ActionResult.SUCCESS;

        if (super.onUse(state, world, pos, player, hand, hit) == ActionResult.SUCCESS) {
            return ActionResult.SUCCESS;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof WirelessTeleportReceiverBlockEntity) {
            KevlarContainers.open(KevlarContainers.WIRELESS_TELEPORT_RECEIVER, player, pos, ((WirelessTeleportReceiverBlockEntity) be).getName());
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return Container.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new WirelessTeleportReceiverBlockEntity();
    }

    @Override
    public NameableContainerFactory createContainerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof NameableContainerFactory ? (NameableContainerFactory) be : null;
    }
}
