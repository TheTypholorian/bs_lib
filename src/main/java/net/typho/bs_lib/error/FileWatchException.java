package net.typho.bs_lib.error;

import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class FileWatchException extends RuntimeException {
    public FileWatchException(Path path, String message) {
        super("Error with file watcher at " + path + ": " + message);
    }

    public FileWatchException(Path path, String message, Throwable cause) {
        super("Error with file watcher at " + path + ": " + message, cause);
    }

    public FileWatchException(Path path, Throwable cause) {
        super("Error with file watcher at " + path + ": " + cause.getMessage(), cause);
    }
}
