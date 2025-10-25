package net.shinysquare.hsbr.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    // This should cancel the health rendering
    @Inject(
            method = "renderPlayerHealth",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void cancelVanillaHealth(CallbackInfo ci) {
        ci.cancel();
    }

    // This cancels the food/hunger rendering
    @Inject(
            method = "renderFood",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelVanillaFood(GuiGraphics guiGraphics, Player player, int i, int j, CallbackInfo ci) {
        ci.cancel();
    }
}