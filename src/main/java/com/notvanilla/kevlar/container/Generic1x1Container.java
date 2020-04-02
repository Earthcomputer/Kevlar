package com.notvanilla.kevlar.container;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class Generic1x1Container extends Container {

    private final Inventory inventory;

    public Generic1x1Container(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(null, syncId);
        this.inventory = inv;
        checkContainerSize(inv, 1);
        inv.onInvOpen(playerInv.player);

        // 1x1 inv
        addSlot(createTheSlot(inv, 80, 20));

        // player main inv
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, y * 18 + 51));
            }
        }

        // player hotbar
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInv, x, 8 + x * 18, 109));
        }
    }

    protected Slot createTheSlot(Inventory inv, int x, int y) {
        return new Slot(inv, 0, x, y);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUseInv(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot == 0) {
                if (!insertItem(originalStack, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!insertItem(originalStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}
