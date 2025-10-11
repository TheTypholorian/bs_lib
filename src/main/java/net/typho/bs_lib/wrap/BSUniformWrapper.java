package net.typho.bs_lib.wrap;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Uniform;
import net.typho.bs_lib.BSUniform;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BSUniformWrapper implements BSUniform {
    public final GlUniform wrapped;

    public BSUniformWrapper(GlUniform wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int getLocation() {
        return wrapped.getLocation();
    }

    @Override
    public String name() {
        return wrapped.getName();
    }

    @Override
    public void set1f(float f1) {
        wrapped.set(f1);
    }

    @Override
    public void set2f(float f1, float f2) {
        wrapped.set(f1, f2);
    }

    @Override
    public void set3f(float f1, float f2, float f3) {
        wrapped.set(f1, f2, f3);
    }

    @Override
    public void set4f(float f1, float f2, float f3, float f4) {
        wrapped.set(f1, f2, f3, f4);
    }

    @Override
    public void set1fv(float[] floats) {
        wrapped.set(floats);
    }

    @Override
    public void set2fv(float[] floats) {
        wrapped.set(floats);
    }

    @Override
    public void set3fv(float[] floats) {
        wrapped.set(floats);
    }

    @Override
    public void set4fv(float[] floats) {
        wrapped.set(floats);
    }

    @Override
    public void set1i(int f1) {
        wrapped.set(f1);
    }

    @Override
    public void set2i(int f1, int f2) {
        wrapped.set(f1, f2);
    }

    @Override
    public void set3i(int f1, int f2, int f3) {
        wrapped.set(f1, f2, f3);
    }

    @Override
    public void set4i(int f1, int f2, int f3, int f4) {
        wrapped.set(f1, f2, f3, f4);
    }

    @Override
    public void set1iv(int[] ints) {
        float[] f = new float[ints.length];

        for (int i = 0; i < f.length; i++) {
            f[i] = ints[i];
        }

        wrapped.set(f);
    }

    @Override
    public void set2iv(int[] ints) {
        float[] f = new float[ints.length];

        for (int i = 0; i < f.length; i++) {
            f[i] = ints[i];
        }

        wrapped.set(f);
    }

    @Override
    public void set3iv(int[] ints) {
        float[] f = new float[ints.length];

        for (int i = 0; i < f.length; i++) {
            f[i] = ints[i];
        }

        wrapped.set(f);
    }

    @Override
    public void set4iv(int[] ints) {
        float[] f = new float[ints.length];

        for (int i = 0; i < f.length; i++) {
            f[i] = ints[i];
        }

        wrapped.set(f);
    }

    @Override
    public void setM2f(Matrix2f mat) {
        wrapped.set(mat.get(new float[4]));
    }

    @Override
    public void setM3f(Matrix3f mat) {
        wrapped.set(mat);
    }

    @Override
    public void setM4f(Matrix4f mat) {
        wrapped.set(mat);
    }
}
