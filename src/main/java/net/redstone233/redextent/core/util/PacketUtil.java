package net.redstone233.redextent.core.util;

import net.minecraft.resources.ResourceLocation;

public final class PacketUtil {
    private PacketUtil() {}
    public static ResourceLocation location(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
