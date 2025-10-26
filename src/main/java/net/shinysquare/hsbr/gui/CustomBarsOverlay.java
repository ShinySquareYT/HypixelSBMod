package net.shinysquare.hsbr.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomBarsOverlay implements LayeredDraw.Layer {

    private static final ResourceLocation HEALTH_TEXTURE = ResourceLocation.fromNamespaceAndPath("hsbr", "textures/gui/health_bar.png");
    private static final ResourceLocation HUNGER_TEXTURE = ResourceLocation.fromNamespaceAndPath("hsbr", "textures/gui/hunger_bar.png");

    private final Minecraft minecraft;

    public CustomBarsOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (minecraft.options.hideGui || minecraft.player == null) {
            return;
        }

        Player player = minecraft.player;
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        // Position bars centered above hotbar
        int healthBarX = (screenWidth / 2) - 91; // Align with hotbar left edge
        int healthBarY = screenHeight - 49; // Above hotbar
        int hungerBarY = screenHeight - 39; // Below health bar

        renderHealthBar(guiGraphics, player, healthBarX, healthBarY);
        renderHungerBar(guiGraphics, player, healthBarX, hungerBarY);
    }

    private void renderHealthBar(GuiGraphics guiGraphics, Player player, int x, int y) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = Math.min(health / maxHealth, 1.0f);

        int barWidth = 120;
        int barHeight = 7;
        int filledWidth = (int)(barWidth * healthPercent);

        // Determine bar color based on effects (priority order)
        float r = 1.0f, g = 1.0f, b = 1.0f;

        if (player.getTicksFrozen() > 0) {
            // Freezing - cyan
            r = 0.0f; g = 1.0f; b = 1.0f;
        } else if (player.hasEffect(MobEffects.WITHER)) {
            // Withered - dark gray
            r = 0.25f; g = 0.25f; b = 0.25f;
        } else if (player.hasEffect(MobEffects.POISON)) {
            // Poisoned - green
            r = 0.0f; g = 1.0f; b = 0.0f;
        } else if (player.getAbsorptionAmount() > 0) {
            // Absorption - yellow
            r = 1.0f; g = 1.0f; b = 0.0f;
        }
        // else stays white (red bar shows through)

        RenderSystem.enableBlend();

        // FIRST: Render filled portion with color tint (Y: 0-6)
        if (filledWidth > 0) {
            RenderSystem.setShaderColor(r, g, b, 1.0f);
            guiGraphics.blit(
                    HEALTH_TEXTURE,
                    x, y,           // screen position
                    0.0f, 0.0f,     // texture UV start (Y=0 for filled bar)
                    filledWidth, barHeight,  // size to draw
                    120, 14         // total texture size
            );
        }

        // SECOND: Render outline ON TOP (Y: 7-13)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.blit(
                HEALTH_TEXTURE,
                x, y,           // screen position
                0.0f, 7.0f,     // texture UV start (Y=7 for outline overlay)
                barWidth, barHeight,  // size to draw
                120, 14         // total texture size
        );

        RenderSystem.disableBlend();

        // Render HP numbers to the left with shadow
        String hpText = String.format("%.1f", health);
        int textX = x - minecraft.font.width(hpText) - 3;
        guiGraphics.drawString(
                minecraft.font,
                hpText,
                textX,
                y - 1,
                0xFFFFFF,
                true
        );
    }

    private void renderHungerBar(GuiGraphics guiGraphics, Player player, int x, int y) {
        FoodData foodData = player.getFoodData();
        int foodLevel = foodData.getFoodLevel();
        float saturation = foodData.getSaturationLevel();

        int maxFood = 20;
        float hungerPercent = Math.min((float)foodLevel / maxFood, 1.0f);
        float saturationPercent = Math.min(saturation / maxFood, 1.0f);

        int barWidth = 120;
        int barHeight = 7;
        int hungerWidth = (int)(barWidth * hungerPercent);
        int saturationWidth = (int)(barWidth * saturationPercent);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // FIRST: Render hunger bar (Y: 0-6)
        if (hungerWidth > 0) {
            guiGraphics.blit(
                    HUNGER_TEXTURE,
                    x, y,           // screen position
                    0.0f, 0.0f,     // texture UV start (Y=0 for hunger fill)
                    hungerWidth, barHeight,  // size to draw
                    120, 21         // total texture size
            );
        }

        // SECOND: Render saturation bar on top (Y: 7-13)
        if (saturationWidth > 0) {
            guiGraphics.blit(
                    HUNGER_TEXTURE,
                    x, y,           // screen position
                    0.0f, 7.0f,     // texture UV start (Y=7 for saturation fill)
                    saturationWidth, barHeight,  // size to draw
                    120, 21         // total texture size
            );
        }

        // THIRD: Render outline ON TOP (Y: 14-20)
        guiGraphics.blit(
                HUNGER_TEXTURE,
                x, y,           // screen position
                0.0f, 14.0f,    // texture UV start (Y=14 for outline overlay)
                barWidth, barHeight,  // size to draw
                120, 21         // total texture size
        );

        RenderSystem.disableBlend();

        // Render hunger numbers to the right with shadow
        String hungerText = String.valueOf(foodLevel);
        int textX = x + barWidth + 3;
        guiGraphics.drawString(
                minecraft.font,
                hungerText,
                textX,
                y - 1,
                0xFFFFFF,
                true
        );
    }
}