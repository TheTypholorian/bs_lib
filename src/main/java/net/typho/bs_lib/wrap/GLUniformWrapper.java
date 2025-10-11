package net.typho.bs_lib.wrap;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgramSetupView;
import net.typho.bs_lib.BSUniform;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GLUniformWrapper extends GlUniform {
    public final BSUniform uniform;

    public GLUniformWrapper(BSUniform uniform) {
        super(uniform.name(), 0, 0, null);
        this.uniform = uniform;
    }

    @Override
    public void set(int index, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(float value1) {
        uniform.set1f(value1);
    }

    @Override
    public void set(int value) {
        uniform.set1i(value);
    }

    @Override
    public void set(Vector4f vec) {
        uniform.set4f(vec.x, vec.y, vec.z, vec.w);
    }

    @Override
    public void set(float[] values) {
        switch (values.length) {
            case 1 -> uniform.set1fv(values);
            case 2 -> uniform.set2fv(values);
            case 3 -> uniform.set3fv(values);
            case 4 -> uniform.set4fv(values);
        }
    }

    @Override
    public void set(Matrix3f values) {
        uniform.setM3f(values);
    }

    @Override
    public void set(Matrix4f values) {
        uniform.setM4f(values);
    }

    @Override
    public void set(Vector3f vector) {
        uniform.set3f(vector.x, vector.y, vector.z);
    }

    @Override
    public void set(int value1, int value2) {
        uniform.set2i(value1, value2);
    }

    @Override
    public void set(float value1, float value2) {
        uniform.set2f(value1, value2);
    }

    @Override
    public void set(int value1, int value2, int value3) {
        uniform.set3i(value1, value2, value3);
    }

    @Override
    public void set(float value1, float value2, float value3) {
        uniform.set3f(value1, value2, value3);
    }

    @Override
    public void set(int value1, int value2, int value3, int value4) {
        uniform.set4i(value1, value2, value3, value4);
    }

    @Override
    public void set(float value1, float value2, float value3, float value4) {
        uniform.set4f(value1, value2, value3, value4);
    }

    @Override
    public void setForDataType(int value1, int value2, int value3, int value4) {
        set(value1, value2, value3, value4);
    }

    @Override
    public void setForDataType(float value1, float value2, float value3, float value4) {
        set(value1, value2, value3, value4);
    }

    @Override
    public int getLocation() {
        return uniform.getLocation();
    }

    @Override
    public void setLocation(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void upload() {
    }

    @Override
    public void close() {
    }
}
