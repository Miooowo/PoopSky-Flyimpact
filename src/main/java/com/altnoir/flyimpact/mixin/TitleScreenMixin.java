package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.client.CustomSplashes;
import com.altnoir.flyimpact.client.CustomTitleLogo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Shadow
    @Nullable
    private SplashRenderer splash;

    @Unique
    private boolean flyimpact$useCustomTitle;

    @Unique
    private boolean flyimpact$splashApplied;

    @Inject(
            method = "<init>(ZLnet/minecraft/client/gui/components/LogoRenderer;)V",
            at = @At("TAIL")
    )
    private void flyimpact$rollCustomTitle(boolean fading, LogoRenderer logoRenderer, CallbackInfo ci) {
        this.flyimpact$useCustomTitle = RandomSource.create().nextFloat() < CustomTitleLogo.CHANCE;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void flyimpact$applyCustomSplash(CallbackInfo ci) {
        if (this.flyimpact$useCustomTitle && !this.flyimpact$splashApplied) {
            this.splash = CustomSplashes.randomRenderer();
            this.flyimpact$splashApplied = true;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void flyimpact$activateCustomTitle(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        CustomTitleLogo.setActive(this.flyimpact$useCustomTitle);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void flyimpact$deactivateCustomTitle(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        CustomTitleLogo.setActive(false);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"
            )
    )
    private void flyimpact$offsetSplash(SplashRenderer instance, GuiGraphics graphics, int screenWidth, Font font, int color) {
        if (this.flyimpact$useCustomTitle) {
            graphics.pose().pushPose();
            graphics.pose().translate(CustomTitleLogo.SPLASH_X_OFFSET, CustomTitleLogo.SPLASH_Y_OFFSET, 0.0F);
            instance.render(graphics, screenWidth, font, color);
            graphics.pose().popPose();
        } else {
            instance.render(graphics, screenWidth, font, color);
        }
    }
}
