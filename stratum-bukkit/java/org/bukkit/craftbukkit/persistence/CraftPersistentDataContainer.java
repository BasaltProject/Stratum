package org.bukkit.craftbukkit.persistence;

import io.papermc.paper.persistence.PaperPersistentDataContainerView;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.basaltmc.stratum.bukkit.facade.NbtCodec;
import org.basaltmc.stratum.bukkit.persistence.MemoryPdcStore;
import org.basaltmc.stratum.bukkit.persistence.PdcStore;
import org.basaltmc.stratum.bukkit.persistence.PdcTags;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CraftPersistentDataContainer extends PaperPersistentDataContainerView implements PersistentDataContainer {
    public CraftPersistentDataContainer() {
        super(new MemoryPdcStore());
    }

    public CraftPersistentDataContainer(PdcStore store) {
        super(store);
    }

    public CraftPersistentDataContainer(CompoundTag tag) {
        super(new MemoryPdcStore(tag));
    }

    public static CraftPersistentDataContainer of(CompoundTag tag) {
        return new CraftPersistentDataContainer(tag);
    }

    public static PersistentDataContainer newContainer() {
        return new CraftPersistentDataContainer();
    }

    @Override
    public synchronized <P, C> void set(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(value, "value");
        Tag wrapped = PdcTags.wrap(type, value, getAdapterContext());
        CompoundTag tag = store.load();
        tag.put(key.toString(), wrapped);
        store.save(tag);
    }

    @Override
    public synchronized void remove(NamespacedKey key) {
        Objects.requireNonNull(key, "key");
        CompoundTag tag = store.load();
        if (tag.remove(key.toString()) != null) {
            store.save(tag);
        }
    }

    @Override
    public synchronized void readFromBytes(byte[] bytes, boolean clear) throws IOException {
        Objects.requireNonNull(bytes, "bytes");
        CompoundTag read;
        try {
            read = NbtCodec.fromBytes(bytes);
        } catch (IllegalStateException e) {
            throw new IOException(e.getCause() != null ? e.getCause() : e);
        }
        if (read == null) {
            throw new IOException("The bytes are not a compound tag");
        }
        CompoundTag tag = clear ? new CompoundTag() : store.load();
        for (var entry : read.entrySet()) {
            tag.put(entry.getKey(), entry.getValue());
        }
        store.save(tag);
    }

    public synchronized void putAll(CompoundTag other, boolean replace) {
        CompoundTag tag = store.load();
        for (var entry : other.entrySet()) {
            if (replace || !tag.contains(entry.getKey())) {
                tag.put(entry.getKey(), entry.getValue().copy());
            }
        }
        store.save(tag);
    }

    public synchronized void putAll(CompoundTag other) {
        putAll(other, true);
    }

    public synchronized void put(String key, Tag tag) {
        CompoundTag current = store.load();
        current.put(key, tag);
        store.save(current);
    }

    public synchronized void putAll(java.util.Map<String, Tag> map) {
        CompoundTag current = store.load();
        map.forEach(current::put);
        store.save(current);
    }

    public java.util.Map<String, Tag> getRaw() {
        return new RawView();
    }

    private final class RawView extends java.util.AbstractMap<String, Tag> {
        @Override
        public java.util.Set<Entry<String, Tag>> entrySet() {
            java.util.Map<String, Tag> copy = new java.util.LinkedHashMap<>();
            toTagCompound().entrySet().forEach(entry -> copy.put(entry.getKey(), entry.getValue()));
            return copy.entrySet();
        }

        @Override
        public Tag get(Object key) {
            return key instanceof String name ? toTagCompound().get(name) : null;
        }

        @Override
        public boolean containsKey(Object key) {
            return key instanceof String name && toTagCompound().contains(name);
        }

        @Override
        public Tag put(String key, Tag tag) {
            synchronized (CraftPersistentDataContainer.this) {
                Tag old = toTagCompound().get(key);
                CraftPersistentDataContainer.this.put(key, tag);
                return old;
            }
        }

        @Override
        public Tag remove(Object key) {
            synchronized (CraftPersistentDataContainer.this) {
                CompoundTag current = store.load();
                Tag old = key instanceof String name ? current.remove(name) : null;
                if (old != null) {
                    store.save(current);
                }
                return old;
            }
        }

        @Override
        public void clear() {
            CraftPersistentDataContainer.this.clear();
        }
    }

    public synchronized void clear() {
        store.save(new CompoundTag());
    }

    public PersistentDataAdapterContext adapterContext() {
        return getAdapterContext();
    }
}
