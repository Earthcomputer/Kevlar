package com.notvanilla.kevlar.block.entity;

import com.notvanilla.kevlar.container.WirelessTeleportReceiverContainer;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

public class WirelessTeleportReceiverBlockEntity extends LootableContainerBlockEntity {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public WirelessTeleportReceiverBlockEntity() {
        super(KevlarBlockEntities.WIRELESS_TELEPORT_RECEIVER);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return items;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.items = list;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.kevlar.wireless_teleport_receiver");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInv) {
        return new WirelessTeleportReceiverContainer(syncId, playerInv, this);
    }

    @Override
    public int getInvSize() {
        return items.size();
    }

    @Override
    public boolean isValidInvStack(int slot, ItemStack stack) {
        return stack.getItem() == Items.ENDER_PEARL;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (!deserializeLootTable(tag)) {
            Inventories.fromTag(tag, items);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!serializeLootTable(tag)) {
            Inventories.toTag(tag, items);
        }
        return tag;
    }
}
