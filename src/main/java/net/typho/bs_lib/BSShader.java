package net.typho.bs_lib;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.NativeResource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface BSShader extends NativeResource {
    ResourceResolver INCLUDE_RESOLVER = new ResourceResolver("bs/shaders/include", "");
    ResourceResolver RESOLVER = ResourceResolver.json("bs/shaders");
    ResourceResolver VERTEX_RESOLVER = new ResourceResolver(RESOLVER.directoryName, ".vsh");
    ResourceResolver FRAGMENT_RESOLVER = new ResourceResolver(RESOLVER.directoryName, ".fsh");
    @ApiStatus.Internal
    Map<ShaderKey, BSShader> SHADER_LOOKUP = new HashMap<>();

    static BSShader get(int id) {
        return SHADER_LOOKUP.get(new ShaderKey(id));
    }

    static BSShader get(Identifier id) {
        return SHADER_LOOKUP.get(new ShaderKey(id));
    }

    static BSShader getOrCreate(int gl, Identifier id, Function<ShaderKey, BSShader> func) {
        return SHADER_LOOKUP.computeIfAbsent(new ShaderKey(gl, id), func);
    }

    static BSShader put(BSShader shader) {
        BSShader old = SHADER_LOOKUP.put(shader.getKey(), shader);

        if (old != null && shader != old) {
            old.free();
        }

        return old;
    }

    ShaderKey getKey();

    void bind();

    ShaderProgram mcPeer();

    BSUniform getUniform(String name);
}
