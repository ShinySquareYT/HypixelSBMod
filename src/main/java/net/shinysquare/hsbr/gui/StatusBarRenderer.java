package net.shinysquare.hsbr.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = "hsbr", value = Dist.CLIENT)
public class StatusBarRenderer {

    private static CustomStatusBar healthBar;
    private static CustomStatusBar hungerBar;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) return;

        // Cancel vanilla health rendering
        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {
            event.setCanceled(true);
        }

        // Cancel vanilla food rendering
        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        GuiGraphics graphics = event.getGuiGraphics();

        if (player == null || mc.options.hideGui) return;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // Initialize bars if needed
        if (healthBar == null) {
            // Position health bar above hotbar on the left
            int barWidth = 91;
            int x = screenWidth / 2 - 91 - 2;
            int y = screenHeight - 39 - 10;
            healthBar = new CustomStatusBar(CustomStatusBar.BarType.HEALTH, x, y, barWidth);
        }

        if (hungerBar == null) {
            // Position hunger bar above hotbar on the right
            int barWidth = 91;
            int x = screenWidth / 2 + 2;
            int y = screenHeight - 39 - 10;
            hungerBar = new CustomStatusBar(CustomStatusBar.BarType.HUNGER, x, y, barWidth);
        }

        // Update positions in case window was resized
        healthBar.setPosition(screenWidth / 2 - 91 - 2, screenHeight - 39 - 10);
        hungerBar.setPosition(screenWidth / 2 + 2, screenHeight - 39 - 10);

        // Render the bars
        healthBar.render(graphics, player);
        hungerBar.render(graphics, player);
    }
}