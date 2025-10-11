package net.typho.bs_lib;

import net.minecraft.resource.ResourceManager;

import java.nio.file.Path;

public interface HotReloader {
    boolean reload(Path path, String contents, ResourceManager manager);
}
