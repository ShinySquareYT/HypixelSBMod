package net.shinysquare.hsbr;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.shinysquare.hsbr.gui.CustomHudRenderer;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(HSBR.MODID)
public class HSBR {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "hsbr";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public HSBR(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (HSBR) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerGuiLayers);
        // Register the item to a creative tab

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
    }

    public void registerGuiLayers(RegisterGuiLayersEvent event) {

        // --- 1. REGISTER THE CUSTOM OVERLAY ---
        // Register the CustomHudRenderer, placing it just above the Hotbar layer.
        event.registerAbove(
                VanillaGuiLayers.HOTBAR, // ID of the layer to place our custom HUD above
                ResourceLocation.fromNamespaceAndPath(MODID, "custom_hud"), // A unique identifier for our overlay
                new CustomHudRenderer()  // The instance of our renderer class
        );

        // --- 2. DISABLE CONFLICTING VANILLA OVERLAYS ---
        // Since there's no replace method, we'll just let our custom renderer handle everything
        // Make sure your CustomHudRenderer doesn't call the super methods that would render vanilla elements

        // Note: If vanilla overlays are still showing, you may need to use mixins to disable them
        // or ensure your CustomHudRenderer properly overrides the rendering
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }
}