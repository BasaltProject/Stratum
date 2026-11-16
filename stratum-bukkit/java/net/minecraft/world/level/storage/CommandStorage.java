// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** Command storage kept in the persistent data of the main world, one NBT blob per storage id. */
public class CommandStorage {
    private static final String kNamespace = "command_storage";

    public CommandStorage(SavedDataStorage savedDataStorage) {
    }

    private static PersistentDataContainer container() {
        return org.bukkit.Bukkit.getWorlds().get(0).getPersistentDataContainer();
    }

    private static NamespacedKey keyOf(Identifier id) {
        return new NamespacedKey(kNamespace, id.getNamespace() + "/" + id.getPath());
    }

    public CompoundTag get(Identifier id) {
        byte[] bytes = container().get(keyOf(id), PersistentDataType.BYTE_ARRAY);
        if (bytes == null) {
            return new CompoundTag();
        }
        try {
            return NbtIo.read(new DataInputStream(new ByteArrayInputStream(bytes)));
        } catch (IOException failure) {
            return new CompoundTag();
        }
    }

    public void set(Identifier id, CompoundTag contents) {
        if (contents.isEmpty()) {
            container().remove(keyOf(id));
            return;
        }
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            NbtIo.write(contents, new DataOutputStream(bytes));
            container().set(keyOf(id), PersistentDataType.BYTE_ARRAY, bytes.toByteArray());
        } catch (IOException failure) {
            throw new IllegalStateException(failure);
        }
    }

    public Stream<Identifier> keys() {
        List<Identifier> out = new ArrayList<>();
        for (NamespacedKey key : container().getKeys()) {
            if (key.getNamespace().equals(kNamespace)) {
                int slash = key.getKey().indexOf('/');
                out.add(Identifier.fromNamespaceAndPath(key.getKey().substring(0, slash), key.getKey().substring(slash + 1)));
            }
        }
        return out.stream();
    }
    static class Container extends SavedData {
        public static final Codec<Container> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.codec(net.minecraft.world.level.storage.CommandStorage.Container.class, "CODEC");
        private Container(Map<String, CompoundTag> storage) {
            
        }
        private Container() {
            
        }
        public static SavedDataType<Container> type(String namespace) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.storage.CommandStorage.Container#type");
        }
        public CompoundTag get(String id) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.storage.CommandStorage.Container#get");
        }
        public void put(String id, CompoundTag contents) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.storage.CommandStorage.Container#put");
        }
        public Stream<Identifier> getKeys(String namespace) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.world.level.storage.CommandStorage.Container#getKeys");
        }
    }
}
