package net.typho.bs_lib;

import org.joml.*;

public interface BSUniform {
    int getLocation();

    String name();

    void set1f(float f1);

    void set2f(float f1, float f2);

    void set3f(float f1, float f2, float f3);

    void set4f(float f1, float f2, float f3, float f4);

    void set1fv(float[] floats);

    void set2fv(float[] floats);

    void set3fv(float[] floats);

    void set4fv(float[] floats);

    void set1i(int f1);

    void set2i(int f1, int f2);

    void set3i(int f1, int f2, int f3);

    void set4i(int f1, int f2, int f3, int f4);

    void set1iv(int[] ints);

    void set2iv(int[] ints);

    void set3iv(int[] ints);

    void set4iv(int[] ints);

    void setM2f(Matrix2f mat);

    void setM3f(Matrix3f mat);

    void setM4f(Matrix4f mat);
}
