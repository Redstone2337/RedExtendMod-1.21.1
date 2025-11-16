// CodecUtils.java
package net.redstone233.redextent.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.redstone233.redextent.functions.RemFormatV1;
import net.redstone233.redextent.functions.RemFormatV2;
import net.redstone233.redextent.functions.SupportedFormats;
import net.redstone233.redextent.functions.SupportedFormatsData;

import java.util.List;

public class CodecUtils {

    // 支持的格式范围 Codec
    public static final Codec<SupportedFormats> SUPPORTED_FORMATS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("min_inclusive").forGetter(SupportedFormats::minInclusive),
                    Codec.INT.fieldOf("max_inclusive").forGetter(SupportedFormats::maxInclusive)
            ).apply(instance, SupportedFormats::new)
    );

    // 统一的格式数据 Codec - 支持三种格式
    public static final Codec<SupportedFormatsData> SUPPORTED_FORMATS_DATA_CODEC = Codec.withAlternative(
            Codec.withAlternative(
                    // 第一种格式：对象格式 { "min_inclusive": x, "max_inclusive": y }
                    SUPPORTED_FORMATS_CODEC.xmap(
                            SupportedFormatsData::fromObject,
                            data -> {
                                if (data instanceof SupportedFormatsData.ObjectFormat(SupportedFormats formats)) {
                                    return formats;
                                }
                                throw new IllegalArgumentException("Cannot convert non-object format to SupportedFormats");
                            }
                    ),
                    // 第二种格式：列表格式 [x, y, z]
                    Codec.list(Codec.INT).xmap(
                            SupportedFormatsData::fromList,
                            data -> {
                                if (data instanceof SupportedFormatsData.ListFormat(List<Integer> formats)) {
                                    return formats;
                                }
                                throw new IllegalArgumentException("Cannot convert non-list format to List<Integer>");
                            }
                    )
            ),
            // 第三种格式：单个整数值 x
            Codec.INT.xmap(
                    SupportedFormatsData::fromSingle,
                    data -> {
                        if (data instanceof SupportedFormatsData.SingleFormat(int format)) {
                            return format;
                        }
                        throw new IllegalArgumentException("Cannot convert non-single format to Integer");
                    }
            )
    );

    /**
     * 创建 1.0 版本的 rem 配置 Codec (当前版本 - rem_format: 8)
     * 需要 HolderLookup.Provider 参数来处理 Component
     */
    public static Codec<RemFormatV1> createRemFormatV1Codec(HolderLookup.Provider registries) {
        Codec<Component> componentCodec = ComponentUtils.createComponentCodec(registries);

        return RecordCodecBuilder.create(instance ->
                instance.group(
                        // rem_format 字段，固定为 8 表示 1.21.1 语法版本
                        Codec.INT.fieldOf("rem_format").forGetter(RemFormatV1::remFormat),
                        // description 字段，支持 Component 和字符串
                        componentCodec.fieldOf("description").forGetter(RemFormatV1::description),
                        // supported_formats 字段，支持三种格式
                        SUPPORTED_FORMATS_DATA_CODEC.fieldOf("supported_formats").forGetter(RemFormatV1::supportedFormats)
                ).apply(instance, RemFormatV1::new)
        );
    }

    /**
     * 创建 2.0 版本的 rem 配置 Codec (未来版本保留)
     * 需要 HolderLookup.Provider 参数来处理 Component
     */
    public static Codec<RemFormatV2> createRemFormatV2Codec(HolderLookup.Provider registries) {
        Codec<Component> componentCodec = ComponentUtils.createComponentCodec(registries);

        return RecordCodecBuilder.create(instance ->
                instance.group(
                        componentCodec.fieldOf("description").forGetter(RemFormatV2::description),
                        Codec.INT.fieldOf("min_format").forGetter(RemFormatV2::minFormat),
                        Codec.INT.fieldOf("max_format").forGetter(RemFormatV2::maxFormat)
                ).apply(instance, RemFormatV2::new)
        );
    }

    /**
     * 创建统一的 rem 配置 Codec，优先使用 V1 格式
     * 需要 HolderLookup.Provider 参数来处理 Component
     */
    public static Codec<RemConfig> createRemConfigCodec(HolderLookup.Provider registries) {
        Codec<RemFormatV1> v1Codec = createRemFormatV1Codec(registries);
        Codec<RemFormatV2> v2Codec = createRemFormatV2Codec(registries);

        return Codec.withAlternative(
                // 主解析器：V1 格式 (当前版本)
                v1Codec.xmap(
                        CodecUtils::convertV1ToRemConfig,
                        config -> new RemFormatV1(8, config.description(), SupportedFormatsData.fromObject(new SupportedFormats(config.minFormat(), config.maxFormat())))
                ),
                // 备选解析器：V2 格式 (为未来版本保留)
                v2Codec.xmap(
                        v2 -> new RemConfig(v2.description(), v2.minFormat(), v2.maxFormat()),
                        config -> new RemFormatV2(config.description(), config.minFormat(), config.maxFormat())
                )
        );
    }

    /**
     * 解析 rem 配置的便捷方法
     */
    public static DataResult<RemConfig> parseRemConfig(com.google.gson.JsonElement element, HolderLookup.Provider registries) {
        Codec<RemConfig> remConfigCodec = createRemConfigCodec(registries);
        return remConfigCodec.parse(registries.createSerializationContext(JsonOps.INSTANCE), element);
    }

    /**
     * 将 V1 格式转换为统一的 RemConfig
     */
    private static RemConfig convertV1ToRemConfig(RemFormatV1 v1) {
        // 验证 rem_format 是否为 8
        if (v1.remFormat() != 8) {
            throw new IllegalArgumentException("Unsupported rem_format: " + v1.remFormat() + ". Only rem_format: 8 is supported in this version.");
        }

        SupportedFormatsData supportedFormats = v1.supportedFormats();

        return new RemConfig(v1.description(), supportedFormats.getMinFormat(), supportedFormats.getMaxFormat());
    }

    /**
     * 便捷方法：直接解析 supported_formats 字段
     */
    public static DataResult<SupportedFormatsData> parseSupportedFormats(com.google.gson.JsonElement element) {
        return SUPPORTED_FORMATS_DATA_CODEC.parse(JsonOps.INSTANCE, element);
    }
}