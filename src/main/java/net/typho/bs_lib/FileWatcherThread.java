package net.typho.bs_lib;

import com.sun.nio.file.ExtendedWatchEventModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.InputSupplier;
import net.minecraft.util.Identifier;
import net.typho.bs_lib.error.FileWatchException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileWatcherThread extends Thread {
    public static final List<HotReloader> HOT_RELOADERS = new LinkedList<>();
    public final Path path;

    public FileWatcherThread(Path path) {
        System.out.println("create " + path);
        this.path = path;
    }

    public FileWatcherThread(@NotNull String name, Path path) {
        super(name);
        this.path = path;
    }

    public FileWatcherThread(@Nullable ThreadGroup group, @NotNull String name, Path path) {
        super(group, name);
        this.path = path;
    }

    @Override
    public void run() {
        try (WatchService service = path.getFileSystem().newWatchService()) {
            path.register(service, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE}, ExtendedWatchEventModifier.FILE_TREE);

            while (true) {
                WatchKey key = service.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    try {
                        Path changed = (Path) event.context();

                        if (!changed.getFileName().toString().endsWith("~")) {
                            Path abs = path.resolve(changed);

                            if (!Files.isDirectory(abs)) {
                                String s = Files.readString(abs);

                                if (!s.isEmpty()) {
                                    Map<Identifier, InputSupplier<InputStream>> map = null;

                                    if (changed.startsWith("assets")) {
                                        map = BigShotClient.HOT_RELOAD_PACK.client;
                                    } else if (changed.startsWith("data")) {
                                        map = BigShotClient.HOT_RELOAD_PACK.server;
                                    }

                                    if (map != null) {
                                        map.put(Identifier.of(changed.getName(1).toString(), changed.subpath(2, changed.getNameCount()).toString().replace("\\\\", "/").replace('\\', '/')), () -> new ByteArrayInputStream(s.getBytes()));
                                    }

                                    for (HotReloader reloader : HOT_RELOADERS) {
                                        if (reloader.reload(changed, s, MinecraftClient.getInstance().getResourceManager())) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }

                if (!key.reset()) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new FileWatchException(path, e);
        }
    }
}
