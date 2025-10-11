package net.typho.bs_lib;

import net.minecraft.util.Identifier;

import java.util.Objects;

public record ShaderKey(int gl, Identifier id) {
    public ShaderKey(int gl) {
        this(gl, null);
    }

    public ShaderKey(Identifier id) {
        this(-1, id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShaderKey shaderKey = (ShaderKey) o;
        return gl == shaderKey.gl || Objects.equals(id, shaderKey.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
