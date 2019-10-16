package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.CraftingStationContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class CraftingStationScreen extends AbstractContainerScreen<CraftingStationContainer> {

    private static final Identifier BG_TEX = new Identifier("textures/gui/container/crafting_table.png");

    public CraftingStationScreen(CraftingStationContainer container, PlayerInventory playerInv) {
        super(container, playerInv, new TranslatableText("container.craftingstation.craftingstation"));
    }

    @Override
    protected void drawBackground(float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bindTexture(BG_TEX);
        }
        int int_3 = this.left;
        int int_4 = (this.height - this.containerHeight) / 2;
        this.blit(int_3, int_4, 0, 0, this.containerWidth, this.containerHeight);
    }

    @Override
    protected void drawForeground(int int_1, int int_2) {
        this.font.draw(this.title.asFormattedString(), 28.0F, 6.0F, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
    }

    @Override
    public void render(int int_1, int int_2, float float_1) {
        super.render(int_1, int_2, float_1);
        this.drawMouseoverTooltip(int_1, int_2);
    }

}
