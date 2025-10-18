package net.typho.bs_lib;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.RenderLayer;

public interface RenderLayerEvent {
    Event<RenderLayerEvent> MODIFY = EventFactory.createArrayBacked(RenderLayerEvent.class, arr -> (name, builder) -> {
        for (RenderLayerEvent event : arr) {
            event.modify(name, builder);
        }
    });

    void modify(String name, Builder builder);

    class Builder extends RenderLayer.MultiPhaseParameters.Builder {
        public int bufferSize;
        public boolean hasCrumbling, translucent;
        public RenderLayer.OutlineMode outlineMode;

        public Builder(int bufferSize, boolean hasCrumbling, boolean translucent, RenderLayer.OutlineMode outlineMode, RenderLayer.MultiPhaseParameters copy) {
            this.bufferSize = bufferSize;
            this.hasCrumbling = hasCrumbling;
            this.translucent = translucent;
            this.outlineMode = outlineMode;

            texture(copy.texture);
            program(copy.program);
            transparency(copy.transparency);
            depthTest(copy.depthTest);
            cull(copy.cull);
            lightmap(copy.lightmap);
            overlay(copy.overlay);
            layering(copy.layering);
            target(copy.target);
            texturing(copy.texturing);
            writeMaskState(copy.writeMaskState);
            lineWidth(copy.lineWidth);
            colorLogic(copy.colorLogic);
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder crumbling(boolean hasCrumbling) {
            this.hasCrumbling = hasCrumbling;
            return this;
        }

        public Builder translucent(boolean translucent) {
            this.translucent = translucent;
            return this;
        }

        public Builder outlineMode(RenderLayer.OutlineMode outlineMode) {
            this.outlineMode = outlineMode;
            return this;
        }

        public RenderLayer.MultiPhaseParameters build() {
            return build(outlineMode);
        }
    }
}
