package net.typho.bs_lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class BigShotLibClient implements ClientModInitializer {
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
    private static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }

    public static ShaderProgram createProgram(String name, VertexFormat format, int id, ShaderStage vertex, ShaderStage fragment) throws InstantiationException {
        ShaderProgram program = (ShaderProgram) UNSAFE.allocateInstance(ShaderProgram.class);
        ShaderProgramAccessor accessor = (ShaderProgramAccessor) program;

        accessor.setName(name);
        accessor.setFormat(format);
        accessor.setGlRef(id);
        accessor.setVertex(vertex);
        accessor.setFragment(fragment);
        accessor.init(true);

        return program;
    }

    @Override
    public void onInitializeClient() {
        SimpleSynchronousResourceReloadListener reload = new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return id("client");
            }

            @Override
            public void reload(ResourceManager manager) {
                BigShader.RESOLVER.findResources(manager).forEach((id, resource) -> {
                    Identifier resId = BigShader.RESOLVER.toResourceId(id);

                    try (BufferedReader reader = resource.getReader()) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                        RenderSystem.recordRenderCall(() -> {
                            System.out.println("Loading shader " + resId);
                            BigShader.put(new BigShader.Resource(resId, json, manager));
                        });
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
