// SupportedFormatsData.java
package net.redstone233.redextent.functions;

import java.util.List;
import java.util.Optional;

public sealed interface SupportedFormatsData permits SupportedFormatsData.ObjectFormat, SupportedFormatsData.ListFormat, SupportedFormatsData.SingleFormat {

    record ObjectFormat(SupportedFormats formats) implements SupportedFormatsData {}
    record ListFormat(List<Integer> formats) implements SupportedFormatsData {}
    record SingleFormat(int format) implements SupportedFormatsData {}

    default int getMinFormat() {
        return switch (this) {
            case ObjectFormat object -> object.formats.minInclusive();
            case ListFormat list -> list.formats.stream().min(Integer::compareTo).orElse(0);
            case SingleFormat single -> single.format;
        };
    }

    default int getMaxFormat() {
        return switch (this) {
            case ObjectFormat object -> object.formats.maxInclusive();
            case ListFormat list -> list.formats.stream().max(Integer::compareTo).orElse(0);
            case SingleFormat single -> single.format;
        };
    }

    static SupportedFormatsData fromObject(SupportedFormats formats) {
        return new ObjectFormat(formats);
    }

    static SupportedFormatsData fromList(List<Integer> formats) {
        return new ListFormat(formats);
    }

    static SupportedFormatsData fromSingle(int format) {
        return new SingleFormat(format);
    }
}