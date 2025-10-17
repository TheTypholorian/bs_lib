package net.typho.bs_lib.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramSetupView;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.InvalidHierarchicalFileException;
import net.typho.bs_lib.ShaderProgramAccessor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.*;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

@Mixin(ShaderProgram.class)
@Implements(@Interface(iface = ShaderProgramAccessor.class, prefix = "bs_lib$"))
public abstract class ShaderProgramMixin {
    @Shadow
    @Final
    @Mutable
    private int glRef;

    @Shadow
    @Final
    @Mutable
    private String name;

    @Shadow
    @Final
    @Mutable
    private ShaderStage vertexShader;

    @Shadow
    @Final
    @Mutable
    private ShaderStage fragmentShader;

    @Shadow
    @Final
    @Mutable
    private VertexFormat format;

    @Shadow
    @Final
    @Mutable
    private Map<String, Object> samplers;

    @Shadow
    @Final
    @Mutable
    private List<String> samplerNames;

    @Shadow
    @Final
    @Mutable
    private List<Integer> loadedSamplerIds;

    @Shadow
    @Final
    @Mutable
    private List<GlUniform> uniforms;

    @Shadow
    @Final
    @Mutable
    private List<Integer> loadedUniformIds;

    @Shadow
    @Final
    @Mutable
    private Map<String, GlUniform> loadedUniforms;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform modelViewMat;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform projectionMat;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform textureMat;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform screenSize;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform colorModulator;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform light0Direction;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform light1Direction;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform glintAlpha;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform fogStart;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform fogEnd;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform fogColor;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform fogShape;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform lineWidth;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform gameTime;

    @Shadow
    @Final
    @Nullable
    @Mutable
    public GlUniform chunkOffset;

    @Shadow
    public abstract @Nullable GlUniform getUniform(String name);

    @Shadow
    protected abstract void loadReferences();

    @Shadow
    public abstract void markUniformsDirty();

    @Shadow
    public abstract void readSampler(JsonElement json);

    @Shadow
    public abstract void addUniform(JsonElement json) throws InvalidHierarchicalFileException;

    @Unique
    public void bs_lib$setGlRef(int i) {
        glRef = i;
    }

    @Unique
    public void bs_lib$setName(String name) {
        this.name = name;
    }

    @Unique
    public void bs_lib$setVertex(ShaderStage vertex) {
        vertexShader = vertex;
    }

    @Unique
    public void bs_lib$setFragment(ShaderStage fragment) {
        fragmentShader = fragment;
    }

    @Unique
    public void bs_lib$setFormat(VertexFormat format) {
        this.format = format;
    }

    @Unique
    public void bs_lib$init(boolean loadUniforms) {
        samplers = Maps.newHashMap();
        samplerNames = Lists.newArrayList();
        loadedSamplerIds = Lists.newArrayList();
        uniforms = Lists.newArrayList();
        loadedUniformIds = Lists.newArrayList();
        loadedUniforms = Maps.newHashMap();

        if (loadUniforms) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer sizeBuf = stack.mallocInt(1);
                IntBuffer typeBuf = stack.mallocInt(1);
                int numUniforms = glGetProgrami(glRef, GL_ACTIVE_UNIFORMS);

                for (int i = 0; i < numUniforms; i++) {
                    String name = glGetActiveUniform(glRef, i, sizeBuf, typeBuf);
                    int type = typeBuf.get(0);

                    switch (type) {
                        case GL_SAMPLER_2D,
                             GL_SAMPLER_3D,
                             GL_SAMPLER_CUBE,
                             GL_SAMPLER_2D_SHADOW,
                             GL_INT_SAMPLER_2D,
                             GL_UNSIGNED_INT_SAMPLER_2D,
                             GL_SAMPLER_2D_ARRAY,
                             GL_SAMPLER_CUBE_SHADOW,
                             GL_SAMPLER_2D_ARRAY_SHADOW: {
                            samplers.put(name, null);
                            samplerNames.add(name);
                            break;
                        }
                        default: {
                            int index = switch (type) {
                                case GL_INT_VEC2, GL_INT_VEC3, GL_INT_VEC4 -> 0;
                                case GL_FLOAT_VEC2, GL_FLOAT_VEC3, GL_FLOAT_VEC4 -> 4;
                                case GL_FLOAT_MAT2 -> 8;
                                case GL_FLOAT_MAT3 -> 9;
                                case GL_FLOAT_MAT4 -> 10;
                                default -> -1;
                            };
                            int count = switch (type) {
                                case GL_INT_VEC2, GL_FLOAT_VEC2 -> 2;
                                case GL_INT_VEC3, GL_FLOAT_VEC3 -> 3;
                                case GL_INT_VEC4, GL_FLOAT_VEC4, GL_FLOAT_MAT2 -> 4;
                                case GL_FLOAT_MAT3 -> 9;
                                case GL_FLOAT_MAT4 -> 16;
                                default -> -1;
                            };
                            uniforms.add(new GlUniform(name, index + (count > 1 && count <= 4 && index < 8 ? count - 1 : 0), count, (ShaderProgramSetupView) this));
                        }
                    }
                }
            }
        }

        int j = 0;

        for (String string3 : format.getAttributeNames()) {
            GlUniform.bindAttribLocation(glRef, j, string3);
            j++;
        }

        loadReferences();
        markUniformsDirty();

        modelViewMat = getUniform("ModelViewMat");
        projectionMat = getUniform("ProjMat");
        textureMat = getUniform("TextureMat");
        screenSize = getUniform("ScreenSize");
        colorModulator = getUniform("ColorModulator");
        light0Direction = getUniform("Light0_Direction");
        light1Direction = getUniform("Light1_Direction");
        glintAlpha = getUniform("GlintAlpha");
        fogStart = getUniform("FogStart");
        fogEnd = getUniform("FogEnd");
        fogColor = getUniform("FogColor");
        fogShape = getUniform("FogShape");
        lineWidth = getUniform("LineWidth");
        gameTime = getUniform("GameTime");
        chunkOffset = getUniform("ChunkOffset");
    }
}
