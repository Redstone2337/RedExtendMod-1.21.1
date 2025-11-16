// SupportedFormats.java
package net.redstone233.redextent.functions;

public record SupportedFormats(int minInclusive, int maxInclusive) {
    public boolean supports(int format) {
        return format >= minInclusive && format <= maxInclusive;
    }
}