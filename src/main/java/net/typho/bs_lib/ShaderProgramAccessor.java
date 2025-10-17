package net.typho.bs_lib;

import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.VertexFormat;

public interface ShaderProgramAccessor {
    void setGlRef(int i);

    void setName(String name);

    void setVertex(ShaderStage vertex);

    void setFragment(ShaderStage fragment);

    void setFormat(VertexFormat format);

    void init(boolean loadUniforms);
}
