#include "BukkitMiscNativesShared.hpp"

#include "basalt/stratum/bukkit/JniLocalFrame.hpp"

#include <basalt/server/plugins/PluginPermissions.hpp>

#include <mutex>

namespace basalt::stratum::bukkit {
namespace {
namespace perms = server::plugins::permissions;

std::mutex gLock;
server::plugins::PluginHandle gProvider;
bool gProviding = false;
jclass gBridgeClass = nullptr;
jmethodID gProvided = nullptr;

std::int32_t askJava(std::string_view uuid, std::string_view node) {
    JNIEnv* env = BukkitBridge::liveEnv();
    if (env == nullptr || gBridgeClass == nullptr || gProvided == nullptr) {
        return perms::kUndefined;
    }
    JniLocalFrame frame(env);
    if (!frame) {
        return perms::kUndefined;
    }
    jstring player = toJava(env, uuid);
    jstring name = toJava(env, node);
    const jint answer = env->CallStaticIntMethod(gBridgeClass, gProvided, player, name);
    if (env->ExceptionCheck() == JNI_TRUE) {
        env->ExceptionClear();
        return perms::kUndefined;
    }
    return answer;
}

void JNICALL nativePermissionProvider(JNIEnv*, jclass, jboolean active) {
    const std::lock_guard lock(gLock);
    if (active == JNI_TRUE && !gProviding) {
        gProvider = BukkitBridge::current().services().permissions().provide("bukkit", perms::kPriorityAdapter,
                                                                              askJava, perms::Meta{});
        gProviding = true;
    } else if (active != JNI_TRUE && gProviding) {
        gProvider.remove();
        gProviding = false;
    }
}

void JNICALL nativePermissionDeclare(JNIEnv* env, jclass, jstring node, jint defaultKind, jobjectArray children,
                                     jbooleanArray values) {
    const std::string name = toStd(env, node);
    const std::vector<std::string> childNames = jni::stringsOf(env, children);
    const jsize count = values == nullptr ? 0 : env->GetArrayLength(values);
    std::vector<jboolean> flags(static_cast<std::size_t>(count));
    if (count > 0) {
        env->GetBooleanArrayRegion(values, 0, count, flags.data());
    }
    const std::size_t size = std::min(childNames.size(), flags.size());
    std::vector<std::string_view> views(childNames.begin(), childNames.begin() + static_cast<std::ptrdiff_t>(size));
    std::unique_ptr<bool[]> signs = std::make_unique<bool[]>(size);
    for (std::size_t index = 0; index < size; ++index) {
        signs[index] = flags[index] == JNI_TRUE;
    }
    BukkitBridge::current().services().permissions().declare(name, defaultKind, views,
                                                              std::span<const bool>(signs.get(), size));
}

void JNICALL nativePermissionUndeclare(JNIEnv* env, jclass, jstring node) {
    BukkitBridge::current().services().permissions().undeclare(toStd(env, node));
}
} // namespace

void registerPermissionNatives(JNIEnv* env, jclass natives) {
    if (jclass bridge = env->FindClass("org/basaltmc/stratum/bukkit/misc/PermissionBridge"); bridge != nullptr) {
        gBridgeClass = static_cast<jclass>(env->NewGlobalRef(bridge));
        gProvided = env->GetStaticMethodID(bridge, "providedPermission", "(Ljava/lang/String;Ljava/lang/String;)I");
    }
    if (gProvided == nullptr) {
        env->ExceptionClear();
    }
    const std::array<JNINativeMethod, 3> methods{
        nativeMethod("permissionProvider", "(Z)V", reinterpret_cast<void*>(&nativePermissionProvider)),
        nativeMethod("permissionDeclare", "(Ljava/lang/String;I[Ljava/lang/String;[Z)V",
                     reinterpret_cast<void*>(&nativePermissionDeclare)),
        nativeMethod("permissionUndeclare", "(Ljava/lang/String;)V", reinterpret_cast<void*>(&nativePermissionUndeclare)),
    };
    registerMethods(env, natives, methods.data(), static_cast<jint>(methods.size()));
}
} // namespace basalt::stratum::bukkit
