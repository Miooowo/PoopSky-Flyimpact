package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.client.CustomTitleLogo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public abstract class LogoRendererMixin {
    @Shadow
    @Final
    private boolean keepLogoThroughFade;

    @Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", at = @At("HEAD"), cancellable = true)
    private void flyimpact$renderCustomTitle(GuiGraphics graphics, int screenWidth, float transparency, int height, CallbackInfo ci) {
        if (!CustomTitleLogo.isActive()) {
            return;
        }
        CustomTitleLogo.render(graphics, screenWidth, this.keepLogoThroughFade ? 1.0F : transparency, height);
        ci.cancel();
    }
}
