package org.basaltmc.stratum.bukkit.legacy;

/** A plugin.yml api-version; a missing one is older than every real version. */
public record ApiVersion(int major, int minor, int patch) implements Comparable<ApiVersion> {
    public static final ApiVersion kNone = new ApiVersion(Integer.MIN_VALUE, 0, 0);
    public static final ApiVersion kFlattening = new ApiVersion(1, 13, 0);
    public static final ApiVersion kRegistryEnums = new ApiVersion(1, 21, 3);

    public static ApiVersion parse(String text) {
        if (text == null || text.isBlank() || text.equalsIgnoreCase("none")) {
            return kNone;
        }
        String[] parts = text.trim().split("\\.");
        try {
            return new ApiVersion(Integer.parseInt(parts[0]), parts.length > 1 ? Integer.parseInt(parts[1]) : 0,
                parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
        } catch (NumberFormatException e) {
            return kNone;
        }
    }

    public boolean isOlderThan(ApiVersion other) {
        return compareTo(other) < 0;
    }

    @Override
    public int compareTo(ApiVersion other) {
        int byMajor = Integer.compare(major, other.major);
        if (byMajor != 0) {
            return byMajor;
        }
        int byMinor = Integer.compare(minor, other.minor);
        return byMinor != 0 ? byMinor : Integer.compare(patch, other.patch);
    }
}
