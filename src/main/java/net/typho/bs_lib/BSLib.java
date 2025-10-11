package net.typho.bs_lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.typho.bs_lib.impl.BSShaderImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class BSLib implements ClientModInitializer {
    public static final String MOD_ID = "bs_lib";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final DynamicResourcePack HOT_RELOAD_PACK = new DynamicResourcePack(new ResourcePackInfo("bs_lib_hot_reload", Text.translatable("bs_lib.hot_reload"), ResourcePackSource.BUILTIN, Optional.empty()));
    public static final ResourcePackProfile HOT_RELOAD_PACK_PROFILE = new ResourcePackProfile(
            HOT_RELOAD_PACK.getInfo(),
            new ResourcePackProfile.PackFactory() {
                @Override
                public ResourcePack open(ResourcePackInfo info) {
                    return HOT_RELOAD_PACK;
                }

                @Override
                public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                    return HOT_RELOAD_PACK;
                }
            },
            new ResourcePackProfile.Metadata(Text.translatable("bs_lib.hot_reload.desc"), ResourcePackCompatibility.COMPATIBLE, FeatureSet.empty(), List.of()),
            new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, false)
    );

    @Override
    public void onInitializeClient() {
        SimpleSynchronousResourceReloadListener reload = new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return id("client");
            }

            @Override
            public void reload(ResourceManager manager) {
                BSShader.RESOLVER.findResources(manager).forEach((id, resource) -> {
                    Identifier resId = BSShader.RESOLVER.toResourceId(id);

                    try (BufferedReader reader = resource.getReader()) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                        RenderSystem.recordRenderCall(() -> BSShader.put(new BSShaderImpl.Resource(resId, json, manager)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        };

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(reload);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            FileWatcherThread.HOT_RELOADERS.addLast((path, contents, manager) -> {
                reload.reload(manager);
                return true;
            });

            new FileWatcherThread(Path.of(System.getProperty("user.dir")).toAbsolutePath().getParent().resolve("src/main/resources")).start();
        }
    }
}
