package org.basaltmc.stratum.bukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.minecraft.SessionService;
import com.mojang.authlib.services.ProfileResult;
import com.mojang.authlib.yggdrasil.response.NameAndId;
import com.google.common.collect.HashMultimap;
import java.util.Optional;
import java.util.UUID;

/** Name and texture lookups against Mojang's services, done natively and meant for worker threads. */
public final class MojangProfiles implements GameProfileRepository, SessionService {
    @Override
    public void findProfilesByNames(String[] names, ProfileLookupCallback callback) {
        for (String name : names) {
            UUID id = uuidOf(name);
            if (id != null) {
                callback.onProfileLookupSucceeded(name, id);
            } else {
                callback.onProfileLookupFailed(name, new Exception("Profile not found: " + name));
            }
        }
    }

    @Override
    public Optional<NameAndId> findProfileByName(String name) {
        UUID id = uuidOf(name);
        return id == null ? Optional.empty() : Optional.of(new NameAndId(id, name));
    }

    @Override
    public ProfileResult fetchProfile(UUID id, boolean requireSecure) {
        String[] fields = WorldNatives.profileTextures(id.toString());
        if (fields == null || fields.length < 3) {
            return null;
        }
        HashMultimap<String, Property> properties = HashMultimap.create();
        for (int at = 1; at + 1 < fields.length; at += 3) {
            properties.put(fields[at], new Property(fields[at], fields[at + 1], fields[at + 2].isEmpty() ? null : fields[at + 2]));
        }
        return new ProfileResult(new GameProfile(id, fields[0], new PropertyMap(properties)));
    }

    private static UUID uuidOf(String name) {
        String text = WorldNatives.profileUuid(name);
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
