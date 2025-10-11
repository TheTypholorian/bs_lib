package net.typho.bs_lib;

import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DynamicResourcePack extends AbstractFileResourcePack {
    public final Map<Identifier, InputSupplier<InputStream>> client = new LinkedHashMap<>(), server = new LinkedHashMap<>();

    public DynamicResourcePack(ResourcePackInfo info) {
        super(info);
    }

    @Override
    public @Nullable InputSupplier<InputStream> openRoot(String... segments) {
        return null;
    }

    @Override
    public @Nullable InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        return type == ResourceType.CLIENT_RESOURCES ? client.get(id) : server.get(id);
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        Map<Identifier, InputSupplier<InputStream>> map = type == ResourceType.CLIENT_RESOURCES ? client : server;

        map.forEach((id, input) -> {
            if (id.getNamespace().equals(namespace) && id.getPath().startsWith(prefix)) {
                consumer.accept(id, input);
            }
        });
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return (type == ResourceType.CLIENT_RESOURCES ? client : server).keySet()
                .stream()
                .map(Identifier::getNamespace)
                .collect(Collectors.toSet());
    }

    @Override
    public void close() {
    }
}
