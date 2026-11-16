#include "BukkitWorldNativesShared.hpp"

namespace basalt::stratum::bukkit {
namespace {
using namespace shared;
using server::plugins::PluginNpcRef;

template <typename Body>
void withNpc(JNIEnv* env, jstring world, jint id, Body&& body) {
    const auto found = worldOf(env, world);
    if (found == nullptr) {
        return;
    }
    static_cast<void>(guarded<bool>(false, [&] {
        if (const std::shared_ptr<PluginNpcRef> npc = found->npcById(id); npc != nullptr) {
            body(*npc);
        }
        return true;
    }));
}

jint JNICALL nativeNpcCreate(JNIEnv* env, jclass, jstring world, jstring uuid, jstring name, jdouble x, jdouble y,
                             jdouble z) {
    const auto found = worldOf(env, world);
    const std::string id = toStd(env, uuid);
    const std::string label = toStd(env, name);
    if (found == nullptr) {
        return -1;
    }
    return guarded<jint>(-1, [&] {
        const std::shared_ptr<PluginNpcRef> npc = found->fakePlayer(id, label, x, y, z);
        return npc == nullptr ? -1 : npc->id();
    });
}

void JNICALL nativeNpcSkin(JNIEnv* env, jclass, jstring world, jint id, jstring texture, jstring signature) {
    const std::string value = toStd(env, texture);
    std::optional<std::string> sign;
    if (signature != nullptr) {
        sign = toStd(env, signature);
    }
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.skin(value, sign); });
}

void JNICALL nativeNpcPlace(JNIEnv* env, jclass, jstring world, jint id, jdouble x, jdouble y, jdouble z, jfloat yaw,
                            jfloat pitch, jfloat headYaw, jboolean onGround) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.place(x, y, z, yaw, pitch, headYaw, onGround == JNI_TRUE); });
}

void JNICALL nativeNpcPosture(JNIEnv* env, jclass, jstring world, jint id, jint flags, jint pose) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.posture(static_cast<std::uint8_t>(flags), pose); });
}

void JNICALL nativeNpcListed(JNIEnv* env, jclass, jstring world, jint id, jboolean shown) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.listed(shown == JNI_TRUE); });
}

void JNICALL nativeNpcEquip(JNIEnv* env, jclass, jstring world, jint id, jint slot, jbyteArray stack) {
    const std::vector<std::uint8_t> data = bytes(env, stack);
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.equip(slot, data); });
}

void JNICALL nativeNpcAnimate(JNIEnv* env, jclass, jstring world, jint id, jint action) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.animate(action); });
}

void JNICALL nativeNpcRemove(JNIEnv* env, jclass, jstring world, jint id) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.remove(); });
}
} // namespace

void registerWorldNativesNpc(JNIEnv* env, jclass natives) {
    const std::array methods{
        nativeMethod("npcCreate", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;DDD)I",
                     reinterpret_cast<void*>(&nativeNpcCreate)),
        nativeMethod("npcSkin", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V",
                     reinterpret_cast<void*>(&nativeNpcSkin)),
        nativeMethod("npcPlace", "(Ljava/lang/String;IDDDFFFZ)V", reinterpret_cast<void*>(&nativeNpcPlace)),
        nativeMethod("npcPosture", "(Ljava/lang/String;III)V", reinterpret_cast<void*>(&nativeNpcPosture)),
        nativeMethod("npcListed", "(Ljava/lang/String;IZ)V", reinterpret_cast<void*>(&nativeNpcListed)),
        nativeMethod("npcEquip", "(Ljava/lang/String;II[B)V", reinterpret_cast<void*>(&nativeNpcEquip)),
        nativeMethod("npcAnimate", "(Ljava/lang/String;II)V", reinterpret_cast<void*>(&nativeNpcAnimate)),
        nativeMethod("npcRemove", "(Ljava/lang/String;I)V", reinterpret_cast<void*>(&nativeNpcRemove)),
    };
    registerMethods(env, natives, methods.data(), static_cast<jint>(methods.size()));
}
} // namespace basalt::stratum::bukkit
