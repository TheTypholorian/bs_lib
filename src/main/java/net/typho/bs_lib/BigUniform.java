package net.typho.bs_lib;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class BigUniform {
    public final int location;
    public final String name;

    public BigUniform(int location, String name) {
        this.location = location;
        this.name = name;
    }

    public int getLocation() {
        return location;
    }

    public String name() {
        return name;
    }

    public void set1f(float f1) {
        glUniform1f(location, f1);
    }

    public void set2f(float f1, float f2) {
        glUniform2f(location, f1, f2);
    }

    public void set3f(float f1, float f2, float f3) {
        glUniform3f(location, f1, f2, f3);
    }

    public void set4f(float f1, float f2, float f3, float f4) {
        glUniform4f(location, f1, f2, f3, f4);
    }

    public void set1fv(float[] floats) {
        glUniform1fv(location, floats);
    }

    public void set2fv(float[] floats) {
        glUniform2fv(location, floats);
    }

    public void set3fv(float[] floats) {
        glUniform3fv(location, floats);
    }

    public void set4fv(float[] floats) {
        glUniform4fv(location, floats);
    }

    public void set1i(int f1) {
        glUniform1i(location, f1);
    }

    public void set2i(int f1, int f2) {
        glUniform2i(location, f1, f2);
    }

    public void set3i(int f1, int f2, int f3) {
        glUniform3i(location, f1, f2, f3);
    }

    public void set4i(int f1, int f2, int f3, int f4) {
        glUniform4i(location, f1, f2, f3, f4);
    }

    public void set1iv(int[] ints) {
        glUniform1iv(location, ints);
    }

    public void set2iv(int[] ints) {
        glUniform2iv(location, ints);
    }

    public void set3iv(int[] ints) {
        glUniform3iv(location, ints);
    }

    public void set4iv(int[] ints) {
        glUniform4iv(location, ints);
    }

    public void setM2f(Matrix2f mat) {
        try (var stack = stackPush()) {
            glUniformMatrix2fv(location, false, mat.get(stack.mallocFloat(4)));
        }
    }

    public void setM3f(Matrix3f mat) {
        try (var stack = stackPush()) {
            glUniformMatrix3fv(location, false, mat.get(stack.mallocFloat(9)));
        }
    }

    public void setM4f(Matrix4f mat) {
        try (var stack = stackPush()) {
            glUniformMatrix4fv(location, false, mat.get(stack.mallocFloat(16)));
        }
    }
}
