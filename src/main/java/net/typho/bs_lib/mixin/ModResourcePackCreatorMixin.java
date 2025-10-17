package net.typho.bs_lib.mixin;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePackProfile;
import net.typho.bs_lib.BigShotLibClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ModResourcePackCreator.class)
public class ModResourcePackCreatorMixin {
    @Inject(
            method = "register",
            at = @At("TAIL")
    )
    private void register(Consumer<ResourcePackProfile> consumer, CallbackInfo ci) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            consumer.accept(BigShotLibClient.HOT_RELOAD_PACK_PROFILE);
        }
    }
}
