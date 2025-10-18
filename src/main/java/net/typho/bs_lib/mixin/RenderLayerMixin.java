package net.typho.bs_lib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.RenderLayer;
import net.typho.bs_lib.RenderLayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {
    @Inject(
            method = "of(Ljava/lang/String;Lnet/minecraft/client/render/VertexFormat;Lnet/minecraft/client/render/VertexFormat$DrawMode;IZZLnet/minecraft/client/render/RenderLayer$MultiPhaseParameters;)Lnet/minecraft/client/render/RenderLayer$MultiPhase;",
            at = @At("HEAD")
    )
    private static void of(CallbackInfoReturnable<RenderLayer.MultiPhase> cir, @Local(argsOnly = true) String name, @Local(argsOnly = true) LocalIntRef bufferSize, @Local(ordinal = 0, argsOnly = true) LocalBooleanRef hasCrumbling, @Local(ordinal = 1, argsOnly = true) LocalBooleanRef translucent, @Local(argsOnly = true) LocalRef<RenderLayer.MultiPhaseParameters> phases) {
        RenderLayerEvent.Builder builder = new RenderLayerEvent.Builder(bufferSize.get(), hasCrumbling.get(), translucent.get(), phases.get().outlineMode, phases.get());

        RenderLayerEvent.MODIFY.invoker().modify(name, builder);

        bufferSize.set(builder.bufferSize);
        hasCrumbling.set(builder.hasCrumbling);
        translucent.set(builder.translucent);
        phases.set(builder.build());
    }
}
