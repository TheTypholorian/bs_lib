package net.typho.bs_lib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.typho.bs_lib.BSLib;
import net.typho.bs_lib.BSShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @WrapOperation(
            method = "drawTexturedQuad(Lnet/minecraft/util/Identifier;IIIIIFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"
            )
    )
    private void setShader(Supplier<ShaderProgram> program, Operation<Void> original) {
        BSShader shader = BSShader.get(BSLib.id("title"));

        if (shader != null) {
            shader.bind();
        } else {
            original.call(program);
        }
    }
}
