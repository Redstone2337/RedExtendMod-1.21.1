// ComponentUtils.java
package net.redstone233.redextent.core.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.Optional;

public class ComponentUtils {

    /**
     * 将 Component 序列化为 JSON 字符串
     * 需要 HolderLookup.Provider 参数
     */
    public static String toJsonString(Component component, HolderLookup.Provider registries) {
        return Component.Serializer.toJson(component, registries);
    }

    /**
     * 从 JSON 字符串反序列化为 Component
     * 需要 HolderLookup.Provider 参数
     */
    public static Optional<Component> fromJson(String json, HolderLookup.Provider registries) {
        return Optional.ofNullable(Component.Serializer.fromJson(json, registries));
    }

    /**
     * 从 JsonElement 反序列化为 Component
     * 需要 HolderLookup.Provider 参数
     */
    public static Optional<Component> fromJson(JsonElement json, HolderLookup.Provider registries) {
        return Optional.ofNullable(Component.Serializer.fromJson(json, registries));
    }

    /**
     * 宽松模式从 JSON 字符串反序列化为 Component
     * 可以处理非严格 JSON 格式
     */
    public static Optional<Component> fromJsonLenient(String json, HolderLookup.Provider registries) {
        return Optional.ofNullable(Component.Serializer.fromJsonLenient(json, registries));
    }

    /**
     * 便捷方法：创建纯文本 Component
     */
    public static Component literal(String text) {
        return Component.literal(text);
    }

    /**
     * 便捷方法：创建可翻译 Component
     */
    public static Component translatable(String key) {
        return Component.translatable(key);
    }

    /**
     * 便捷方法：创建带参数的可翻译 Component
     */
    public static Component translatable(String key, Object... args) {
        return Component.translatable(key, args);
    }

    /**
     * 检查 Component 是否为空或只包含空白字符
     */
    public static boolean isBlank(Component component) {
        if (component == null) return true;
        String text = component.getString();
        return text == null || text.trim().isEmpty();
    }

    /**
     * 如果 Component 为 null，返回空 Component
     */
    public static Component nullToEmpty(Component component) {
        return component != null ? component : Component.empty();
    }

    /**
     * 如果字符串为 null，返回空 Component
     */
    public static Component nullToEmpty(String text) {
        return text != null ? Component.literal(text) : Component.empty();
    }

    /**
     * 创建支持 Component 和字符串的 Codec
     * 需要 HolderLookup.Provider 参数
     */
    public static Codec<Component> createComponentCodec(HolderLookup.Provider registries) {
        return Codec.withAlternative(
                ComponentSerialization.CODEC,
                Codec.STRING.xmap(Component::literal, Component::getString)
        );
    }

    /**
     * 从 JsonElement 解析 Component，处理字符串和对象两种格式
     */
    public static DataResult<Component> parseComponent(JsonElement element, HolderLookup.Provider registries) {
        // 如果元素是字符串，直接创建文本组件
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return DataResult.success(Component.literal(element.getAsString()));
        }

        // 如果元素是对象，使用 Component 的 Codec 解析
        if (element.isJsonObject()) {
            return ComponentSerialization.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), element);
        }

        return DataResult.error(() -> "Invalid component format: expected string or object");
    }
}