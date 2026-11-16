package org.basaltmc.stratum.bukkit.legacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** The renamed-API tables in legacy_renames.txt, applied the way a plugin built for an older API expects. */
public final class LegacyRenames {
    private record Section(ApiVersion limit, boolean before, Map<String, String> names) {
        boolean applies(ApiVersion plugin) {
            return limit == null || (before ? plugin.isOlderThan(limit) : !plugin.isOlderThan(limit));
        }
    }

    private record ClassRename(String from, String to, ApiVersion before) {}

    private static final Map<String, List<Section>> kSections = new HashMap<>();
    private static final List<ClassRename> kClasses = new ArrayList<>();

    static {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                LegacyRenames.class.getResourceAsStream("/legacy_renames.txt"), StandardCharsets.UTF_8))) {
            String owner = null;
            Section current = null;
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.trim().split(" ");
                if (parts[0].equals("@")) {
                    owner = parts[1];
                    boolean before = parts.length > 2 && parts[2].equals("before");
                    current = new Section(parts.length > 3 ? ApiVersion.parse(parts[3]) : null, before, new HashMap<>());
                    kSections.computeIfAbsent(owner, k -> new ArrayList<>()).add(current);
                } else if (parts[0].equals("class")) {
                    kClasses.add(new ClassRename(parts[1], parts[2], parts.length > 4 ? ApiVersion.parse(parts[4]) : null));
                } else if (current != null) {
                    current.names().put(parts[0], parts[1]);
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new ExceptionInInitializerError(e);
        }
        for (List<Section> sections : kSections.values()) {
            sections.sort((a, b) -> a.limit() == null ? (b.limit() == null ? 0 : -1)
                : b.limit() == null ? 1 : a.limit().compareTo(b.limit()));
        }
    }

    private LegacyRenames() {}

    public static boolean hasOwner(String owner) {
        return kSections.containsKey(owner);
    }

    public static String rename(ApiVersion plugin, String owner, String name) {
        if (owner.equals("org/bukkit/scoreboard/DisplaySlot")) {
            return name.startsWith("SIDEBAR_") && !name.startsWith("SIDEBAR_TEAM_")
                ? name.replace("SIDEBAR_", "SIDEBAR_TEAM_") : name;
        }
        List<Section> sections = kSections.get(owner);
        if (sections == null) {
            return name;
        }
        String result = name;
        for (Section section : sections) {
            if (section.applies(plugin)) {
                result = section.names().getOrDefault(result, result);
            }
        }
        return result;
    }

    /** The renamed spelling of a constant given as text, as valueOf and getByName receive it. */
    public static String renameText(ApiVersion plugin, String owner, String name) {
        if (name == null) {
            return null;
        }
        String result = rename(plugin, owner, name.toUpperCase(Locale.ROOT));
        return owner.equals("org/bukkit/attribute/Attribute") ? result.replace('.', '_') : result;
    }

    public static Map<String, String> classRenames(ApiVersion plugin) {
        Map<String, String> out = new HashMap<>();
        for (ClassRename each : kClasses) {
            if (each.before() == null || plugin.isOlderThan(each.before())) {
                out.put(each.from(), each.to());
            }
        }
        return out;
    }
}
