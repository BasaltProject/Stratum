// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.SessionService;
import com.mojang.authlib.services.MinecraftServicesDiscoveryService;
import com.mojang.authlib.services.ServicesKeySet;
import java.io.File;
import net.minecraft.server.players.CachedUserNameToIdResolver;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.server.players.UserNameToIdResolver;
import net.minecraft.util.SignatureValidator;

public record Services(SessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository,
    UserNameToIdResolver nameToIdCache, ProfileResolver profileResolver) {
    public static Services create(MinecraftServicesDiscoveryService serviceAccess, File nameCacheDir) {
        CachedUserNameToIdResolver cache = new CachedUserNameToIdResolver(null, new File(nameCacheDir, "usercache.json"));
        org.basaltmc.stratum.bukkit.MojangProfiles profiles = new org.basaltmc.stratum.bukkit.MojangProfiles();
        return new Services(profiles, null, profiles, cache, new ProfileResolver.Cached(profiles, cache));
    }

    public SignatureValidator profileKeySignatureValidator() {
        return null;
    }

    public boolean canValidateProfileKeys() {
        return false;
    }
}
