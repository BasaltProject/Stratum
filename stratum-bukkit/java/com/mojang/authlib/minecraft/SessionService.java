// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package com.mojang.authlib.minecraft;

import com.mojang.authlib.services.ProfileResult;
import java.util.UUID;

public interface SessionService {
    default ProfileResult fetchProfile(UUID id, boolean requireSecure) {
        return null;
    }
}
