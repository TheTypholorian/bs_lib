package net.typho.bs_lib.error;

import net.minecraft.util.Identifier;

public class ShaderCompileException extends RuntimeException {
    public ShaderCompileException(Identifier id, String message) {
        super("Error while compiling shader " + id + ": " + message);
    }

    public ShaderCompileException(Identifier id, String message, Throwable cause) {
        super("Error while compiling shader " + id + ": " + message, cause);
    }

    public ShaderCompileException(Identifier id, Throwable cause) {
        super("Error while compiling shader " + id + ": " + cause.getMessage(), cause);
    }
}
