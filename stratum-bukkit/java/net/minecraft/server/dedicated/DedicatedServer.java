// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server.dedicated;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.SystemReport;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.jsonrpc.ManagementServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;

public class DedicatedServer extends MinecraftServer implements ServerInterface {
    private static final int kCommandWaitSeconds = 10;
    private static final DedicatedServer kInstance = new DedicatedServer();

    private DedicatedServer() {}

    private DedicatedServerProperties properties;
    private DedicatedServerSettings settings;
    public static DedicatedServer instance() {
        return kInstance;
    }

    public static MinecraftServer getServer() {
        return kInstance;
    }

    @Override
    public RegistryAccess.Frozen registryAccess() {
        return super.registryAccess();
    }
    public DedicatedServer(Thread serverThread, LevelStorageSource.LevelStorageAccess levelStorageSource, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, DedicatedServerSettings settings, DataFixer fixerUpper, Services services, ManagementServer jsonRpcServer, NotificationManager notificationManager) {
        super(serverThread, levelStorageSource, packRepository, worldStem, gameRules, java.net.Proxy.NO_PROXY, fixerUpper, services, null, true, notificationManager);
        this.settings = settings;
        this.properties = settings == null ? null : settings.getProperties();
    }
    private static final String kTrue = "true";

    private static boolean flag(String key, boolean fallback) {
        String value = org.basaltmc.stratum.bukkit.WorldEnvNatives.setting(key);
        return value == null ? fallback : value.equals(kTrue);
    }

    private static int number(String key, int fallback) {
        String value = org.basaltmc.stratum.bukkit.WorldEnvNatives.setting(key);
        try {
            return value == null ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException malformed) {
            return fallback;
        }
    }

    private static void store(String key, Object value) {
        org.basaltmc.stratum.bukkit.WorldEnvNatives.setSetting(key, String.valueOf(value));
    }

    public void setAllowFlight(boolean allowed) {
        store("allow_flight", allowed);
    }

    @Override
    public boolean allowFlight() {
        return flag("allow_flight", false);
    }

    @Override
    public boolean repliesToStatus() {
        return flag("status_replies", true);
    }

    @Override
    public boolean hidesOnlinePlayers() {
        return flag("hide_online_players", false);
    }

    @Override
    public int playerIdleTimeout() {
        return number("idle_timeout", 0);
    }

    @Override
    public boolean enforceSecureProfile() {
        return flag("enforce_secure_profile", false) && flag("online_mode", true);
    }

    @Override
    public boolean acceptsTransfers() {
        return flag("accept_transfers", false);
    }

    @Override
    public boolean forceGameMode() {
        return flag("force_game_mode", false);
    }

    @Override
    public void setForceGameMode(boolean forceGameMode) {
        store("force_game_mode", forceGameMode);
        GameType forced = getForcedGameType();
        if (forced != null) {
            enforceGameTypeForPlayers(forced);
        }
    }

    @Override
    public GameType getForcedGameType() {
        return forceGameMode() ? gameMode() : null;
    }

    @Override
    public int getScaledTrackingDistance(int range) {
        return entityBroadcastRangePercentage() * range / 100;
    }

    public int statusHeartbeatInterval() {
        return number("status_heartbeat_interval", 0);
    }

    public boolean setStatusHeartbeatInterval(int statusHeartbeatInterval) {
        return org.basaltmc.stratum.bukkit.WorldEnvNatives.setSetting("status_heartbeat_interval", String.valueOf(statusHeartbeatInterval));
    }
    public DedicatedServerProperties getProperties() {
        DedicatedServerProperties current = properties;
        if (current == null) {
            java.util.Properties values = new java.util.Properties();
            org.bukkit.Server server = org.bukkit.Bukkit.getServer();
            values.setProperty("motd", server.getMotd());
            values.setProperty("server-ip", server.getIp());
            values.setProperty("server-port", Integer.toString(server.getPort()));
            values.setProperty("max-players", Integer.toString(server.getMaxPlayers()));
            values.setProperty("view-distance", Integer.toString(server.getViewDistance()));
            values.setProperty("simulation-distance", Integer.toString(server.getSimulationDistance()));
            values.setProperty("online-mode", Boolean.toString(server.getOnlineMode()));
            values.setProperty("allow-flight", Boolean.toString(server.getAllowFlight()));
            values.setProperty("white-list", Boolean.toString(server.hasWhitelist()));
            values.setProperty("enforce-whitelist", Boolean.toString(server.isWhitelistEnforced()));
            values.setProperty("hardcore", Boolean.toString(server.isHardcore()));
            values.setProperty("gamemode", server.getDefaultGameMode().name().toLowerCase(java.util.Locale.ROOT));
            values.setProperty("spawn-protection", Integer.toString(server.getSpawnRadius()));
            if (!server.getWorlds().isEmpty()) {
                values.setProperty("level-name", server.getWorlds().get(0).getName());
                values.setProperty("difficulty", server.getWorlds().get(0).getDifficulty().name().toLowerCase(java.util.Locale.ROOT));
                values.setProperty("generate-structures", Boolean.toString(server.getWorlds().get(0).canGenerateStructures()));
            }
            current = new DedicatedServerProperties(values);
            properties = current;
        }
        return current;
    }
    public void setDifficulty(Difficulty difficulty) {
        setDifficulty(difficulty, false);
    }
    public int viewDistance() {
        return org.bukkit.Bukkit.getServer().getViewDistance();
    }
    public void setViewDistance(int viewDistance) {
        store("view_distance", viewDistance);
    }
    public int simulationDistance() {
        return org.bukkit.Bukkit.getServer().getSimulationDistance();
    }
    public void setSimulationDistance(int simulationDistance) {
        store("simulation_distance", simulationDistance);
    }
    public void handleConsoleInput(String msg, CommandSourceStack source) {
    }
    public void handleConsoleInputs() {
    }
    public int getMaxPlayers() {
        return org.bukkit.Bukkit.getServer().getMaxPlayers();
    }
    public void setMaxPlayers(int maxPlayers) {
        store("max_players", maxPlayers);
    }
    public String getServerIp() {
        return org.bukkit.Bukkit.getServer().getIp();
    }
    public int getServerPort() {
        return org.bukkit.Bukkit.getServer().getPort();
    }
    public String getServerName() {
        return org.bukkit.Bukkit.getServer().getName();
    }
    public void showGui() {
    }
    public int spawnProtectionRadius() {
        String value = org.basaltmc.stratum.bukkit.WorldEnvNatives.setting("spawn_protection");
        return value == null ? 16 : Integer.parseInt(value);
    }
    public void setSpawnProtectionRadius(int spawnProtectionRadius) {
        org.basaltmc.stratum.bukkit.WorldEnvNatives.setSetting("spawn_protection", String.valueOf(spawnProtectionRadius));
    }
    public void setRepliesToStatus(boolean enable) {
        store("status_replies", enable);
    }
    public void setHidesOnlinePlayers(boolean hide) {
        org.basaltmc.stratum.bukkit.WorldEnvNatives.setSetting("hide_online_players", String.valueOf(hide));
    }
    public void setOperatorUserPermissions(LevelBasedPermissionSet permissions) {
        properties = getProperties().opPermissions.update(registryAccess(), permissions);
    }
    protected boolean convertOldUsers() {
        return true;
    }
    public long getMaxTickLength() {
        return 60000L;
    }
    public String getPluginNames() {
        return java.util.Arrays.stream(org.bukkit.Bukkit.getServer().getPluginManager().getPlugins()).map(org.bukkit.plugin.Plugin::getName).collect(java.util.stream.Collectors.joining(", "));
    }
    public String runCommand(String command) {
        Runnable dispatch = () -> org.bukkit.Bukkit.getServer().dispatchCommand(org.bukkit.Bukkit.getServer().getConsoleSender(), command);
        if (isSameThread()) {
            dispatch.run();
            return "";
        }
        java.util.concurrent.CompletableFuture<Void> done = new java.util.concurrent.CompletableFuture<>();
        execute(() -> {
            try {
                dispatch.run();
                done.complete(null);
            } catch (Throwable failure) {
                done.completeExceptionally(failure);
            }
        });
        try {
            done.get(kCommandWaitSeconds, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception failure) {
            throw new IllegalStateException("Command did not run on the server thread", failure);
        }
        return "";
    }
    public int entityBroadcastRangePercentage() {
        return number("entity_broadcast_range", 100);
    }
    public void setEntityBroadcastRangePercentage(int range) {
        store("entity_broadcast_range", range);
    }
    public String getLevelIdName() {
        return "world";
    }
    public GameType gameMode() {
        return GameType.byId(org.bukkit.Bukkit.getDefaultGameMode().getValue());
    }
    public void setGameMode(GameType gameMode) {
        org.basaltmc.stratum.bukkit.WorldEnvNatives.setSetting("gamemode", gameMode.getName());
    }
    public void setAcceptsTransfers(boolean acceptTransfers) {
        store("accept_transfers", acceptTransfers);
    }
    public void setPauseWhenEmptySeconds(int seconds) {
        org.basaltmc.stratum.bukkit.WorldEnvNatives.setSetting("pause_when_empty_seconds", String.valueOf(seconds));
    }
    protected boolean initServer() throws IOException {
        return super.initServer();
    }
    public boolean isEnforceWhitelist() {
        return super.isEnforceWhitelist();
    }
    public void setEnforceWhitelist(boolean enforceWhitelist) {
        super.setEnforceWhitelist(enforceWhitelist);
    }
    public boolean isUsingWhitelist() {
        return super.isUsingWhitelist();
    }
    public void setUsingWhitelist(boolean usingWhitelist) {
        super.setUsingWhitelist(usingWhitelist);
    }
    protected void tickServer(BooleanSupplier haveTime) {
        super.tickServer(haveTime);
    }
    public boolean saveAllChunks(boolean silent, boolean flush, boolean force) {
        return super.saveAllChunks(silent, flush, force);
    }
    public void sendLowDiskSpaceWarning() {
        super.sendLowDiskSpaceWarning();
    }
    protected void forceDifficulty() {
        super.forceDifficulty();
    }
    public SystemReport fillServerSystemReport(SystemReport systemReport) {
        return super.fillServerSystemReport(systemReport);
    }
    public void dumpServerProperties(Path path) throws IOException {
        super.dumpServerProperties(path);
    }
    protected void onServerExit() {
        super.onServerExit();
    }
    protected void tickConnection() {
        super.tickConnection();
    }
    public boolean isDedicatedServer() {
        return super.isDedicatedServer();
    }
    public int getRateLimitPacketsPerSecond() {
        return super.getRateLimitPacketsPerSecond();
    }
    public int getCommandSpamThresholdSeconds() {
        return super.getCommandSpamThresholdSeconds();
    }
    public int getChatSpamThresholdSeconds() {
        return super.getChatSpamThresholdSeconds();
    }
    public boolean useNativeTransport() {
        return super.useNativeTransport();
    }
    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList) super.getPlayerList();
    }
    public boolean isPublished() {
        return super.isPublished();
    }
    public boolean isUnderSpawnProtection(ServerLevel level, BlockPos pos, Player player) {
        return super.isUnderSpawnProtection(level, pos, player);
    }
    public LevelBasedPermissionSet operatorUserPermissions() {
        return super.operatorUserPermissions();
    }
    public PermissionSet getFunctionCompilationPermissions() {
        return super.getFunctionCompilationPermissions();
    }
    public void setPlayerIdleTimeout(int playerIdleTimeout) {
        super.setPlayerIdleTimeout(playerIdleTimeout);
    }
    public String getMotd() {
        return super.getMotd();
    }
    public void setMotd(String motd) {
        super.setMotd(motd);
    }
    public boolean shouldRconBroadcast() {
        return super.shouldRconBroadcast();
    }
    public boolean shouldInformAdmins() {
        return super.shouldInformAdmins();
    }
    public int getAbsoluteMaxWorldSize() {
        return super.getAbsoluteMaxWorldSize();
    }
    public int getCompressionThreshold() {
        return super.getCompressionThreshold();
    }
    public boolean logIPs() {
        return super.logIPs();
    }
    public int getMaxChainedNeighborUpdates() {
        return super.getMaxChainedNeighborUpdates();
    }
    protected void stopServer() {
        super.stopServer();
    }
    public boolean isSingleplayerOwner(NameAndId nameAndId) {
        return super.isSingleplayerOwner(nameAndId);
    }
    public boolean forceSynchronousWrites() {
        return super.forceSynchronousWrites();
    }
    public TextFilter createTextFilterForPlayer(ServerPlayer player) {
        return super.createTextFilterForPlayer(player);
    }
    public Optional<MinecraftServer.ServerResourcePackInfo> getServerResourcePack() {
        return super.getServerResourcePack();
    }
    protected void endMetricsRecordingTick() {
        super.endMetricsRecordingTick();
    }
    protected SampleLogger getTickTimeLogger() {
        return super.getTickTimeLogger();
    }
    public boolean isTickTimeLoggingEnabled() {
        return super.isTickTimeLoggingEnabled();
    }
    public ServerLinks serverLinks() {
        return super.serverLinks();
    }
    public int pauseWhenEmptySeconds() {
        return super.pauseWhenEmptySeconds();
    }
    public Map<String, String> getCodeOfConducts() {
        return super.getCodeOfConducts();
    }
}
