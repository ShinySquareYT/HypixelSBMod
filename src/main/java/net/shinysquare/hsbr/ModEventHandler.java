package net.shinysquare.hsbr;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.shinysquare.hsbr.gui.CustomBarsOverlay;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ModEventHandler {

    @EventBusSubscriber(modid = HSBR.MODID, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
            // Register our custom overlay
            event.registerAboveAll(
                    ResourceLocation.parse("custom_bars"),
                    (LayeredDraw.Layer) new CustomBarsOverlay(Minecraft.getInstance())
            );
        }
    }

    @EventBusSubscriber(modid = HSBR.MODID, value = Dist.CLIENT)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
            // Cancel vanilla health and food bars only (keep armor and air)
            if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH) ||
                    event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {
                event.setCanceled(true);
            }
        }
    }
}