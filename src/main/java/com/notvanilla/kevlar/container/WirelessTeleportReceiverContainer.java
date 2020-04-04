package com.notvanilla.kevlar.container;

import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class WirelessTeleportReceiverContainer extends Generic1x1Container {
    public WirelessTeleportReceiverContainer(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(syncId, playerInv, inv);
    }

    @Override
    protected Slot createTheSlot(Inventory inv, int x, int y) {
        return new Slot(inv, 0, x, y) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.ENDER_EYE;
            }
        };
    }
}
