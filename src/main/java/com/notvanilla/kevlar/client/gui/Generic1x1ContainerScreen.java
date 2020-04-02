package com.notvanilla.kevlar.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.notvanilla.kevlar.Kevlar;
import com.notvanilla.kevlar.container.Generic1x1Container;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Generic1x1ContainerScreen extends ContainerScreen<Generic1x1Container> {

    private static final Identifier TEXTURE = new Identifier(Kevlar.MOD_ID, "textures/gui/container/generic_11.png");

    public Generic1x1ContainerScreen(Generic1x1Container container, PlayerInventory playerInventory, Text name) {
        super(container, playerInventory, name);
        containerHeight = 133;
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        font.draw(title.asFormattedString(), 8, 6, 0x404040);
        font.draw(playerInventory.getDisplayName().asFormattedString(), 8, containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        assert minecraft != null;
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURE);
        int left = (width - containerWidth) / 2;
        int top = (height - containerHeight) / 2;
        blit(left, top, 0, 0, containerWidth, containerHeight);
    }
}
