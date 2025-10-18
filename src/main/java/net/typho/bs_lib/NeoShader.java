package net.typho.bs_lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.typho.bs_lib.error.ShaderCompileException;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.NativeResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class NeoShader implements NativeResource {
    public static final ResourceResolver INCLUDE_RESOLVER = new ResourceResolver("bs/shaders/include", "");
    public static final ResourceResolver RESOLVER = ResourceResolver.json("bs/shaders");
    public static final ResourceResolver VERTEX_RESOLVER = new ResourceResolver(RESOLVER.directoryName, ".vsh");
    public static final ResourceResolver FRAGMENT_RESOLVER = new ResourceResolver(RESOLVER.directoryName, ".fsh");
    @ApiStatus.Internal
    public static final Map<ShaderKey, NeoShader> SHADER_LOOKUP = new HashMap<>();

    public static NeoShader get(int id) {
        return SHADER_LOOKUP.get(new ShaderKey(id));
    }

    public static NeoShader get(Identifier id) {
        return SHADER_LOOKUP.get(new ShaderKey(id));
    }

    public static NeoShader getOrCreate(int gl, Identifier id, Function<ShaderKey, NeoShader> func) {
        return SHADER_LOOKUP.computeIfAbsent(new ShaderKey(gl, id), func);
    }

    public static NeoShader put(NeoShader shader) {
        NeoShader old = SHADER_LOOKUP.put(shader.getKey(), shader);

        if (old != null && shader != old) {
            old.free();
        }

        return old;
    }

    public final ShaderKey key;
    public final ShaderStage vertex, fragment;
    public final ShaderProgram program;
    protected final Map<String, BigUniform> uniforms = new LinkedHashMap<>();

    public NeoShader(Identifier id, VertexFormat format, int glId, String vertexCode, String fragmentCode) {
        key = new ShaderKey(glId, id);
        vertex = new ShaderStage(ShaderStage.Type.VERTEX, glCreateShader(GL_VERTEX_SHADER), id + "-vertex");
        fragment = new ShaderStage(ShaderStage.Type.FRAGMENT, glCreateShader(GL_FRAGMENT_SHADER), id + "-fragment");

        ShaderStage.Type.VERTEX.getLoadedShaders().put(vertex.getName(), vertex);
        ShaderStage.Type.FRAGMENT.getLoadedShaders().put(fragment.getName(), fragment);

        upload(vertex.getGlRef(), vertexCode);
        upload(fragment.getGlRef(), fragmentCode);

        glAttachShader(glId, vertex.getGlRef());
        glAttachShader(glId, fragment.getGlRef());

        glLinkProgram(glId);

        if (glGetProgrami(glId, GL_LINK_STATUS) == GL_FALSE) {
            throw new ShaderCompileException(id, glGetProgramInfoLog(glId).trim());
        }

        try {
            program = BigShotClient.createProgram(id.toString(), format, glId, vertex, fragment);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public NeoShader(Identifier id, VertexFormat format, String vertexCode, String fragmentCode) {
        this(id, format, glCreateProgram(), vertexCode, fragmentCode);
    }

    protected void upload(int id, String source) {
        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new ShaderCompileException(key.id(), glGetShaderInfoLog(id).trim());
        }
    }

    public void bind() {
        RenderSystem.setShader(() -> program);
    }

    public ShaderKey getKey() {
        return key;
    }

    public BigUniform getUniform(String name) {
        return uniforms.computeIfAbsent(name, key -> new BigUniform(glGetUniformLocation(this.key.gl(), key), key));
    }

    @Override
    public void free() {
        vertex.release();
        fragment.release();
        glDeleteProgram(key.gl());
    }

    public static class Resource extends NeoShader {
        public Resource(Identifier id, int glId, JsonObject json, ResourceManager manager) {
            super(id, getFormat(json.get("format")), glId, getCode(json, "vertex", VERTEX_RESOLVER, manager), getCode(json, "fragment", FRAGMENT_RESOLVER, manager));
        }

        public Resource(Identifier id, JsonObject json, ResourceManager manager) {
            super(id, getFormat(json.get("format")), getCode(json, "vertex", VERTEX_RESOLVER, manager), getCode(json, "fragment", FRAGMENT_RESOLVER, manager));
        }

        public static VertexFormat getFormat(JsonElement json) {
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();

                VertexFormat.Builder builder = VertexFormat.builder();

                for (JsonElement element : array) {
                    String s = element.getAsString();

                    builder.add(s, switch (s) {
                        case "Position" -> VertexFormatElement.POSITION;
                        case "Color" -> VertexFormatElement.COLOR;
                        case "UV0" -> VertexFormatElement.UV_0;
                        case "UV1" -> VertexFormatElement.UV_1;
                        case "UV2" -> VertexFormatElement.UV_2;
                        case "Normal" -> VertexFormatElement.NORMAL;
                        default -> throw new IllegalStateException("Unexpected value: " + s);
                    });
                }

                return builder.build();
            } else {
                String s = json.getAsString();

                return switch (s.toUpperCase()) {
                    case "POSITION_COLOR_TEXTURE_LIGHT_NORMAL" -> VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
                    case "POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL" -> VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
                    case "POSITION_TEXTURE_COLOR_LIGHT" -> VertexFormats.POSITION_TEXTURE_COLOR_LIGHT;
                    case "POSITION" -> VertexFormats.POSITION;
                    case "POSITION_COLOR" -> VertexFormats.POSITION_COLOR;
                    case "LINES" -> VertexFormats.LINES;
                    case "POSITION_COLOR_LIGHT" -> VertexFormats.POSITION_COLOR_LIGHT;
                    case "POSITION_TEXTURE" -> VertexFormats.POSITION_TEXTURE;
                    case "POSITION_TEXTURE_COLOR" -> VertexFormats.POSITION_TEXTURE_COLOR;
                    case "POSITION_COLOR_TEXTURE_LIGHT" -> VertexFormats.POSITION_COLOR_TEXTURE_LIGHT;
                    case "POSITION_TEXTURE_LIGHT_COLOR" -> VertexFormats.POSITION_TEXTURE_LIGHT_COLOR;
                    case "POSITION_TEXTURE_COLOR_NORMAL" -> VertexFormats.POSITION_TEXTURE_COLOR_NORMAL;
                    default -> throw new IllegalStateException("Unexpected value: " + s);
                };
            }
        }

        public static String readCode(Identifier id, ResourceManager manager) {
            try (BufferedReader reader = manager.getResource(id).orElseThrow(() -> new NullPointerException("Couldn't find " + id)).getReader()) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static String getCode(JsonObject object, String key, ResourceResolver resolver, ResourceManager manager) {
            StringBuilder codeBuilder = new StringBuilder(readCode(resolver.toResourcePath(Identifier.of(object.get(key).getAsString())), manager));
            int version = 150;

            JsonArray includeArray = object.getAsJsonArray("include");

            if (includeArray != null) {
                for (JsonElement include : includeArray) {
                    codeBuilder.insert(0, readCode(INCLUDE_RESOLVER.toResourcePath(Identifier.of(include.getAsString())), manager) + "\n");
                }
            }

            JsonArray mojIncludeArray = object.getAsJsonArray("moj_include");

            if (mojIncludeArray != null) {
                for (JsonElement include : mojIncludeArray) {
                    codeBuilder.insert(0, readCode(Identifier.of("shaders/include/" + include.getAsString()), manager) + "\n");
                }
            }

            String code = codeBuilder.toString();

            while (true) {
                int i = code.indexOf("#version ");

                if (i == -1) {
                    break;
                }

                int nl = code.indexOf('\n', i);
                version = Math.max(version, Integer.parseInt(code.substring(i + "#version ".length(), nl)));
                code = code.substring(0, i) + code.substring(nl);
            }

            code = "#version " + version + "\n" + code;

            return code;
        }
    }
}
