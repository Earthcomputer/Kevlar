package com.notvanilla.kevlar.block.entity;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;

public class BlockBreakerBlockEntity extends LootableContainerBlockEntity {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public BlockBreakerBlockEntity() {
        super(KevlarBlockEntities.BLOCK_BREAKER);
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
        return new TranslatableText("container.kevlar.block_breaker");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory) {
        return new Generic3x3Container(syncId, playerInventory, this);
    }

    @Override
    public int getInvSize() {
        return 9;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        items = DefaultedList.ofSize(getInvSize(), ItemStack.EMPTY);
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
