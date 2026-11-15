package io.papermc.paper.plugin.provider.classloader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.plugin.java.PluginClassLoader;

/** Holds every registered plugin class loader and hands out the groups that decide which of them a loader can see. */
public final class BasaltClassLoaderStorage implements PaperClassLoaderStorage {
    private final List<ConfiguredPluginClassLoader> all = new CopyOnWriteArrayList<>();
    private final Group global = new Group(null);

    @Override
    public PluginClassLoaderGroup registerSpigotGroup(PluginClassLoader pluginClassLoader) {
        return register(pluginClassLoader, global.getAccess());
    }

    @Override
    public PluginClassLoaderGroup registerOpenGroup(ConfiguredPluginClassLoader classLoader) {
        return register(classLoader, global.getAccess());
    }

    @Override
    public PluginClassLoaderGroup registerAccessBackedGroup(ConfiguredPluginClassLoader classLoader,
            ClassLoaderAccess access) {
        return register(classLoader, access);
    }

    @Override
    public void unregisterClassloader(ConfiguredPluginClassLoader configuredPluginClassLoader) {
        all.remove(configuredPluginClassLoader);
    }

    @Override
    public boolean registerUnsafePlugin(ConfiguredPluginClassLoader pluginLoader) {
        if (all.contains(pluginLoader)) {
            return false;
        }
        all.add(pluginLoader);
        return true;
    }

    private PluginClassLoaderGroup register(ConfiguredPluginClassLoader loader, ClassLoaderAccess access) {
        if (!all.contains(loader)) {
            all.add(loader);
        }
        Group group = new Group(access);
        group.add(loader);
        return group;
    }

    private final class Group implements PluginClassLoaderGroup {
        private final ClassLoaderAccess access;
        private final List<ConfiguredPluginClassLoader> own = new CopyOnWriteArrayList<>();

        Group(ClassLoaderAccess access) {
            this.access = access != null ? access : loader -> all.contains(loader);
        }

        @Override
        public Class<?> getClassByName(String name, boolean resolve, ConfiguredPluginClassLoader requester) {
            for (ConfiguredPluginClassLoader loader : own) {
                Class<?> found = tryLoad(loader, name, resolve, requester);
                if (found != null) {
                    return found;
                }
            }
            for (ConfiguredPluginClassLoader loader : all) {
                if (access.canAccess(loader)) {
                    Class<?> found = tryLoad(loader, name, resolve, requester);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }

        private Class<?> tryLoad(ConfiguredPluginClassLoader loader, String name, boolean resolve,
                ConfiguredPluginClassLoader requester) {
            if (loader == requester) {
                return null;
            }
            try {
                return loader.loadClass(name, resolve, false, false);
            } catch (ClassNotFoundException missing) {
                return null;
            }
        }

        @Override
        public void remove(ConfiguredPluginClassLoader loader) {
            own.remove(loader);
        }

        @Override
        public void add(ConfiguredPluginClassLoader loader) {
            if (!own.contains(loader)) {
                own.add(loader);
            }
        }

        @Override
        public ClassLoaderAccess getAccess() {
            return access;
        }
    }
}
