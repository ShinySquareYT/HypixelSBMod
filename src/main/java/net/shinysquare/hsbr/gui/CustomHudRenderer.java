package net.shinysquare.hsbr.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class CustomHudRenderer implements Layer {
    // --- HEALTH BAR DIMENSIONS (120x7) ---
    private static final int HEALTH_BAR_WIDTH = 120;
    private static final int HEALTH_BAR_HEIGHT = 7;
    // The texture total height is 2 * BAR_HEIGHT = 14 pixels
    private static final int HEALTH_TEXTURE_HEIGHT = HEALTH_BAR_HEIGHT * 2;

    // --- HUNGER BAR DIMENSIONS (NEW: 120x7) ---
    private static final int HUNGER_BAR_WIDTH = 120;
    private static final int HUNGER_BAR_HEIGHT = 7;
    // The texture total height is 3 * BAR_HEIGHT = 21 pixels
    private static final int HUNGER_TEXTURE_HEIGHT = HUNGER_BAR_HEIGHT * 3;

    // Textures
    private static final ResourceLocation HEALTH_BAR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("hsbr", "textures/gui/health_bar.png");
    private static final ResourceLocation HUNGER_BAR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("hsbr", "textures/gui/hunger_bar.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Render the Health Bar
        renderHealthBar(guiGraphics, mc.player);

        // Render the Hunger Bar
        renderHungerBar(guiGraphics, mc.player);
    }

    private void renderHealthBar(GuiGraphics guiGraphics, Player player) {
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();

        if (maxHealth <= 0) return;

        float healthRatio = currentHealth / maxHealth;
        int filledWidth = (int) (HEALTH_BAR_WIDTH * healthRatio);

        // Position: Bottom Left (Adjusted y position for 7-pixel bar)
        int x = 10;
        int y = guiGraphics.guiHeight() - 44;

        // 1. Determine Color Tint based on Status Effects (HP)
        float r = 1.0F, g = 1.0F, b = 1.0F;

        if (player.hasEffect(MobEffects.POISON)) {
            r = 0.5F; g = 1.0F; b = 0.5F;
        }
        else if (player.hasEffect(MobEffects.WITHER)) {
            r = 0.7F; g = 0.5F; b = 0.7F;
        }

        // --- DRAWING ORDER SWAPPED FOR CORRECT LAYERING ---

        // 2. Draw the Health Fill (V=0) FIRST
        RenderSystem.setShaderColor(r, g, b, 1.0F);
        if (filledWidth > 0) {
            int fillV = 0; // Now using V=0 for the actual fill
            // Dimensions: (120, 7)
            guiGraphics.blit(HEALTH_BAR_TEXTURE, x, y, 0, fillV, filledWidth, HEALTH_BAR_HEIGHT, HEALTH_BAR_WIDTH, HEALTH_TEXTURE_HEIGHT);
        }

        // 3. Draw the Frame/Outline (V=7) LAST (to sit on top)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset color for the static frame
        int frameV = HEALTH_BAR_HEIGHT; // V=7 (Start of the second layer)
        // Dimensions: (120, 7)
        guiGraphics.blit(HEALTH_BAR_TEXTURE, x, y, 0, frameV, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, HEALTH_BAR_WIDTH, HEALTH_TEXTURE_HEIGHT);

        // 4. Draw HP Text
        String hpText = String.format("%d / %d", (int)currentHealth, (int)maxHealth);
        guiGraphics.drawString(Minecraft.getInstance().font, hpText, x + HEALTH_BAR_WIDTH + 5, y + 1, 0xFFFFFF, true);
    }

    private void renderHungerBar(GuiGraphics guiGraphics, Player player) {
        int currentHunger = player.getFoodData().getFoodLevel();
        float currentSaturation = player.getFoodData().getSaturationLevel();
        int maxHunger = 20;

        float hungerRatio = (float)currentHunger / (float)maxHunger;
        int hungerFilledWidth = (int) (HUNGER_BAR_WIDTH * hungerRatio);
        float saturationRatio = currentSaturation / (float)maxHunger;
        int saturationFilledWidth = (int) (HUNGER_BAR_WIDTH * saturationRatio);

        // Position: Bottom Right (Uses new 120 width, adjusted y for 7-pixel bar)
        int screenWidth = guiGraphics.guiWidth();
        int x = screenWidth - HUNGER_BAR_WIDTH - 10;
        int y = guiGraphics.guiHeight() - 44; // Same Y as Health Bar

        // 1. Determine Color Tint based on Status Effects (Hunger)
        float r = 1.0F, g = 1.0F, b = 1.0F;

        if (player.hasEffect(MobEffects.WITHER)) {
            r = 0.7F; g = 0.7F; b = 0.7F;
        }
        else if (player.hasEffect(MobEffects.POISON)) {
            r = 0.8F; g = 1.0F; b = 0.8F;
        }

        // --- DRAWING ORDER SWAPPED FOR CORRECT LAYERING ---

        // 2. Draw the Hunger Fill (V=0) FIRST
        RenderSystem.setShaderColor(r, g, b, 1.0F);
        if (hungerFilledWidth > 0) {
            int hungerFillV = 0;
            guiGraphics.blit(HUNGER_BAR_TEXTURE, x, y, 0, hungerFillV, hungerFilledWidth, HUNGER_BAR_HEIGHT, HUNGER_BAR_WIDTH, HUNGER_TEXTURE_HEIGHT);
        }

        // 3. Draw the Saturation Fill (V=7) SECOND
        if (saturationFilledWidth > 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset color
            int saturationFillV = HUNGER_BAR_HEIGHT; // V=7
            guiGraphics.blit(HUNGER_BAR_TEXTURE, x, y, 0, saturationFillV, saturationFilledWidth, HUNGER_BAR_HEIGHT, HUNGER_BAR_WIDTH, HUNGER_TEXTURE_HEIGHT);
        }

        // 4. Draw the Frame/Outline (V=14) LAST (to sit on top)
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset color
        int frameV = HUNGER_BAR_HEIGHT * 2; // V=14
        guiGraphics.blit(HUNGER_BAR_TEXTURE, x, y, 0, frameV, HUNGER_BAR_WIDTH, HUNGER_BAR_HEIGHT, HUNGER_BAR_WIDTH, HUNGER_TEXTURE_HEIGHT);

        // 5. Draw Hunger/Saturation Text
        String foodText = String.format("%d (%.1f)", currentHunger, currentSaturation);
        int textX = x - Minecraft.getInstance().font.width(foodText) - 5;
        guiGraphics.drawString(Minecraft.getInstance().font, foodText, textX, y + 1, 0xFFFFFF, true);
    }
}