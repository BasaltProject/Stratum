package org.basaltmc.stratum.bukkit.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.basaltmc.stratum.bukkit.LeftoverNatives;
import org.bukkit.block.data.BlockData;

/** The pre-1.13 numeric id and data of a modern block state, the inverse of the native flattening table. */
public final class LegacyBlocks {
    private record Row(int id, Map<String, String> properties) {}

    private static final Object kLock = new Object();
    private static final ConcurrentHashMap<String, Integer> kCache = new ConcurrentHashMap<>();
    private static volatile Map<String, List<Row>> kByName;

    private LegacyBlocks() {}

    private static Map<String, List<Row>> table() {
        Map<String, List<Row>> built = kByName;
        if (built == null) {
            synchronized (kLock) {
                built = kByName;
                if (built == null) {
                    built = new HashMap<>();
                    for (int id = 0; id < 4096; id++) {
                        String[] state = LeftoverNatives.datafixLegacyState(id);
                        if (id != 0 && state[0].equals("minecraft:air")) {
                            continue;
                        }
                        Map<String, String> properties = new HashMap<>();
                        for (int at = 1; at + 1 < state.length; at += 2) {
                            properties.put(state[at], state[at + 1]);
                        }
                        built.computeIfAbsent(state[0], k -> new ArrayList<>()).add(new Row(id, properties));
                    }
                    kByName = built;
                }
            }
        }
        return built;
    }

    private static Map<String, String> propertiesOf(String text) {
        Map<String, String> out = new HashMap<>();
        int open = text.indexOf('[');
        if (open < 0) {
            return out;
        }
        for (String pair : text.substring(open + 1, text.length() - 1).split(",")) {
            int equals = pair.indexOf('=');
            if (equals > 0) {
                out.put(pair.substring(0, equals), pair.substring(equals + 1));
            }
        }
        return out;
    }

    /** The legacy `block << 4 | data` of a state, or -1 when it has none. */
    public static int idOf(BlockData data) {
        String text = data.getAsString();
        Integer known = kCache.get(text);
        if (known != null) {
            return known;
        }
        int open = text.indexOf('[');
        String name = open < 0 ? text : text.substring(0, open);
        List<Row> rows = table().get(name);
        int best = -1;
        if (rows != null) {
            Map<String, String> actual = propertiesOf(text);
            int bestScore = -1;
            for (Row row : rows) {
                if (actual.entrySet().containsAll(row.properties().entrySet()) && row.properties().size() > bestScore) {
                    best = row.id();
                    bestScore = row.properties().size();
                }
            }
            if (best < 0) {
                best = rows.get(0).id();
            }
        }
        kCache.put(text, best);
        return best;
    }

    public static int blockOf(BlockData data) {
        int id = idOf(data);
        return id < 0 ? 0 : id >> 4;
    }

    public static byte dataOf(BlockData data) {
        int id = idOf(data);
        return id < 0 ? 0 : (byte) (id & 15);
    }
}
