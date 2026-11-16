// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.resources;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.ResourceProvider;

public class NetworkRegistryLoadTask<T> extends RegistryLoadTask<T> {
    private final RegistryDataLoader.NetworkedRegistryData networkedData;

    public NetworkRegistryLoadTask(RegistryDataLoader.RegistryData<T> data, Lifecycle lifecycle, Map<ResourceKey<?>, Exception> loadingErrors,
            Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> networkedRegistries) {
        super(data, lifecycle, loadingErrors);
        this.networkedData = networkedRegistries.get(data.key());
    }

    @Override
    public CompletableFuture<?> load(RegistryOps.RegistryInfoLookup context, Executor executor) {
        return CompletableFuture.completedFuture(networkedData);
    }
    public NetworkRegistryLoadTask(RegistryDataLoader.RegistryData<T> data, Lifecycle lifecycle, Map<ResourceKey<?>, Exception> loadingErrors, Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> entries, ResourceProvider knownDataSource) {
        super((net.minecraft.resources.RegistryDataLoader.RegistryData) null, (com.mojang.serialization.Lifecycle) null, (java.util.Map) null);
        throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.resources.NetworkRegistryLoadTask#<init>");
    }
}
