package net.typho.bs_lib;

import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class ResourceResolver extends ResourceFinder {
    public ResourceResolver(String directoryName, String fileExtension) {
        super(directoryName, fileExtension);
    }

    public static ResourceResolver json(String directoryName) {
        return new ResourceResolver(directoryName, ".json");
    }

    public @Nullable Identifier resolvePath(Path path) {
        try {
            if (!path.getName(0).toString().equals("assets")) {
                return null;
            }

            String namespace = path.getName(1).toString();

            int i = 2;

            for (Path token : Path.of(directoryName)) {
                if (!path.getName(i++).equals(token)) {
                    return null;
                }
            }

            Path subPath = path.subpath(i, path.getNameCount());

            if (!subPath.getFileName().toString().endsWith(fileExtension)) {
                return null;
            }

            String s = subPath.toString();

            return Identifier.of(namespace, s.substring(0, s.length() - fileExtension.length()));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
