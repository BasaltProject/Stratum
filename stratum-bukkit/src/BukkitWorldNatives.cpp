#include "BukkitWorldNativesShared.hpp"

namespace basalt::stratum::bukkit {
void registerWorldNatives(JNIEnv* env, jclass natives) {
    registerWorldNativesRegistry(env, natives);
    registerWorldNativesBlocks(env, natives);
    registerWorldNativesEntities(env, natives);
    registerWorldNativesChunks(env, natives);
    registerWorldNativesJobs(env, natives);
    registerWorldNativesNpc(env, natives);
}
} // namespace basalt::stratum::bukkit
