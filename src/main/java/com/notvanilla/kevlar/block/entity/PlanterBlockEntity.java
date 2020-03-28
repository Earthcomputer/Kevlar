package com.notvanilla.kevlar.block.entity;

import com.notvanilla.kevlar.block.PlanterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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

public class PlanterBlockEntity extends LootableContainerBlockEntity   {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public PlanterBlockEntity() {
        super(KevlarBlockEntities.PLANTER);
    }


    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return items;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.items = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
        if (!deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.items);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        if(!serializeLootTable(tag)) {
            Inventories.toTag(tag, items);
        }

        return tag;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        items = list;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.kevlar.planter");
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerInventory) {
        return  new Generic3x3Container(i, playerInventory, this);
    }

    @Override
    public int getInvSize() {
        return 9;
    }

    public boolean isEmpty() {
        for(int i = 0; i < getInvSize(); i++) {
            if (!getInvStack(i).isEmpty()) return false;
        }

        return true;
    }

    @Override
    public boolean isValidInvStack(int slot, ItemStack stack) {
        return PlanterBlock.itemToBlockMap.containsKey(stack.getItem());
    }
}
