package net.shinysquare.hsbr.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.awt.Color;

public class CustomStatusBar {

    private static final ResourceLocation BAR_FILL = ResourceLocation.fromNamespaceAndPath("hsbr", "textures/gui/bar_fill.png");
    private static final ResourceLocation BAR_BACK = ResourceLocation.fromNamespaceAndPath("hsbr", "textures/gui/bar_back.png");

    public static final int BAR_HEIGHT = 7;
    public static final int BAR_BACK_TEXTURE_WIDTH = 120;
    public static final int BAR_BACK_TEXTURE_HEIGHT = 7;
    public static final int BAR_FILL_TEXTURE_WIDTH = 120;
    public static final int BAR_FILL_TEXTURE_HEIGHT = 5;

    private final BarType type;
    private final Color[] colors;

    private int x;
    private int y;
    private int width;

    private float fill = 0f;

    public CustomStatusBar(BarType type, int x, int y, int width) {
        this.type = type;
        this.colors = type.getColors();
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void render(GuiGraphics graphics, Player player) {
        if (width <= 0) return;

        // Update fill percentage based on type
        updateFill(player);

        // Get the current color based on player effects (used for both bar and text)
        Color currentColor = getCurrentColor(player);

        // Enable scissor test to ensure we render the full width including borders
        graphics.pose().pushPose();

        // Render bar background (stretch the 120x7 texture to fit width)
        graphics.blit(BAR_BACK, x, y, 0, 0, width, BAR_HEIGHT, BAR_BACK_TEXTURE_WIDTH, BAR_BACK_TEXTURE_HEIGHT);

        // Draw fill (stretch to match current fill percentage) - offset by 1 pixel to fit inside border
        int fillWidth = (int) (width * fill);
        if (fillWidth > 0) {
            RenderSystem.setShaderColor(
                    currentColor.getRed() / 255f,
                    currentColor.getGreen() / 255f,
                    currentColor.getBlue() / 255f,
                    1.0f
            );
            // Render fill 1 pixel down and with 5px height to fit inside the 7px background
            graphics.blit(BAR_FILL, x, y + 1, 0, 0, fillWidth, BAR_FILL_TEXTURE_HEIGHT, BAR_FILL_TEXTURE_WIDTH, BAR_FILL_TEXTURE_HEIGHT);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        graphics.pose().popPose();

        // Render text centered on bar with the same color as the bar
        String text = getDisplayText(player);
        int textWidth = Minecraft.getInstance().font.width(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y - 2; // Position above the bar with space for shadow

        // Draw text with shadow using the same color as the bar
        graphics.drawString(Minecraft.getInstance().font, text, textX, textY, currentColor.getRGB(), true);
    }

    private void updateFill(Player player) {
        switch (type) {
            case HEALTH:
                fill = player.getHealth() / player.getMaxHealth();
                break;
            case HUNGER:
                fill = player.getFoodData().getFoodLevel() / 20f;
                break;
        }
        fill = Math.max(0f, Math.min(1f, fill));
    }

    private Color getCurrentColor(Player player) {
        switch (type) {
            case HEALTH:
                // Poison - green
                if (player.hasEffect(net.minecraft.world.effect.MobEffects.POISON)) {
                    return new Color(78, 147, 49);
                }
                // Wither - dark gray/black
                if (player.hasEffect(net.minecraft.world.effect.MobEffects.WITHER)) {
                    return new Color(40, 40, 40);
                }
                // Freezing - light blue/cyan
                if (player.isFullyFrozen() || player.getTicksFrozen() > 0) {
                    return new Color(107, 206, 241);
                }
                // Default red
                return colors[0];

            case HUNGER:
                // Hunger effect - dark green
                if (player.hasEffect(net.minecraft.world.effect.MobEffects.HUNGER)) {
                    return new Color(88, 118, 83);
                }
                // Saturation - red/orange
                if (player.hasEffect(net.minecraft.world.effect.MobEffects.SATURATION)) {
                    return new Color(248, 230, 36);
                }
                // Default brown
                return colors[0];

            default:
                return colors[0];
        }
    }

    private String getDisplayText(Player player) {
        switch (type) {
            case HEALTH:
                return String.format("%.1f", player.getHealth());
            case HUNGER:
                return String.valueOf(player.getFoodData().getFoodLevel());
            default:
                return "";
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return BAR_HEIGHT;
    }

    public enum BarType {
        HEALTH(new Color[]{new Color(255, 0, 0)}),
        HUNGER(new Color[]{new Color(213, 107, 52)});

        private final Color[] colors;

        BarType(Color[] colors) {
            this.colors = colors;
        }

        public Color[] getColors() {
            return colors;
        }
    }
}