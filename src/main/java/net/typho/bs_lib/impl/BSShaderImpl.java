package net.typho.bs_lib.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.typho.bs_lib.*;
import net.typho.bs_lib.error.ShaderCompileException;
import net.typho.bs_lib.wrap.GLUniformWrapper;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class BSShaderImpl implements BSShader {
    public final ShaderKey key;
    public final int vertex, fragment;
    public final Peer peer;
    protected final Map<String, BSUniform> uniforms = new LinkedHashMap<>();

    public BSShaderImpl(Identifier id, int glId, String vertexCode, String fragmentCode) {
        key = new ShaderKey(glId, id);
        vertex = glCreateShader(GL_VERTEX_SHADER);
        fragment = glCreateShader(GL_FRAGMENT_SHADER);

        upload(vertex, vertexCode);
        upload(fragment, fragmentCode);

        glAttachShader(glId, vertex);
        glAttachShader(glId, fragment);

        glLinkProgram(glId);

        if (glGetProgrami(glId, GL_LINK_STATUS) == GL_FALSE) {
            throw new ShaderCompileException(id, glGetProgramInfoLog(glId).trim());
        }

        try {
            peer = new Peer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BSShaderImpl(Identifier id, String vertexCode, String fragmentCode) {
        this(id, glCreateProgram(), vertexCode, fragmentCode);
    }

    protected void upload(int id, String source) {
        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new ShaderCompileException(key.id(), glGetShaderInfoLog(id).trim());
        }
    }

    @Override
    public void bind() {
        glUseProgram(key.gl());
    }

    @Override
    public ShaderKey getKey() {
        return key;
    }

    @Override
    public ShaderProgram mcPeer() {
        return peer;
    }

    @Override
    public BSUniform getUniform(String name) {
        return uniforms.computeIfAbsent(name, key -> new BSUniformImpl(glGetUniformLocation(this.key.gl(), key), key));
    }

    @Override
    public void free() {
        glDetachShader(key.gl(), vertex);
        glDetachShader(key.gl(), fragment);
        glDeleteShader(vertex);
        glDeleteShader(fragment);
        glDeleteProgram(key.gl());
    }

    public class Peer extends ShaderProgram {
        protected final Map<String, GLUniformWrapper> wrappedUniforms = new LinkedHashMap<>();

        public Peer() throws IOException {
            super(
                    id -> Optional.of(new net.minecraft.resource.Resource(null, () -> new ByteArrayInputStream("{\"vertex\": \"none\",\"fragment\":\"none\"}".getBytes()))),
                    "",
                    VertexFormat.builder().build()
            );
        }

        @Override
        public void bind() {
            BSShaderImpl.this.bind();
        }

        @Override
        public void close() {
            free();
        }

        @Override
        public @Nullable GlUniform getUniform(String name) {
            return wrappedUniforms.computeIfAbsent(name, k -> new GLUniformWrapper(BSShaderImpl.this.getUniform(k)));
        }
    }

    public static class Resource extends BSShaderImpl {
        public Resource(Identifier id, int glId, JsonObject json, ResourceManager manager) {
            super(id, glId, getCode(json, "vertex", VERTEX_RESOLVER, manager), getCode(json, "fragment", FRAGMENT_RESOLVER, manager));
        }

        public Resource(Identifier id, JsonObject json, ResourceManager manager) {
            super(id, getCode(json, "vertex", VERTEX_RESOLVER, manager), getCode(json, "fragment", FRAGMENT_RESOLVER, manager));
        }

        public static String readCode(Identifier id, ResourceManager manager) {
            try (BufferedReader reader = manager.getResource(id).orElseThrow(() -> new NullPointerException("Couldn't find " + id)).getReader()) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static String getCode(JsonObject object, String key, ResourceResolver resolver, ResourceManager manager) {
            StringBuilder code = new StringBuilder(readCode(resolver.toResourcePath(Identifier.of(object.get(key).getAsString())), manager));
            JsonArray includeArray = object.getAsJsonArray("include");

            if (includeArray != null) {
                for (JsonElement include : includeArray) {
                    code.insert(0, readCode(INCLUDE_RESOLVER.toResourcePath(Identifier.of(include.getAsString())), manager) + "\n");
                }
            }

            return code.toString();
        }
    }
}
