package org.basaltmc.stratum.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;

/** What JavaPlugin#getPluginLoader answers, handing every call to the plugin manager that owns loading. */
public final class BasaltPluginLoader implements PluginLoader {
    private final BasaltPluginManager manager;

    public BasaltPluginLoader() {
        this.manager = null;
    }

    BasaltPluginLoader(BasaltPluginManager manager) {
        this.manager = manager;
    }

    private BasaltPluginManager manager() {
        return manager != null ? manager : (BasaltPluginManager) org.bukkit.Bukkit.getPluginManager();
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        try {
            return manager().loadPlugin(file);
        } catch (InvalidDescriptionException e) {
            throw new InvalidPluginException(e);
        }
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        try {
            return BasaltPluginManager.describe(file);
        } catch (IOException e) {
            throw new InvalidDescriptionException(e);
        }
    }

    @Override
    public Pattern[] getPluginFileFilters() {
        return new Pattern[] {Pattern.compile("\\.jar$")};
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener,
            Plugin plugin) {
        return manager().createRegisteredListeners(listener, plugin);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        manager().enablePlugin(plugin);
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        manager().disablePlugin(plugin);
    }
}
