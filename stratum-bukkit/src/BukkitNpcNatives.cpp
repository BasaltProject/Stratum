#include "BukkitWorldNativesShared.hpp"

#include <basalt/wire/auth/MojangSessionService.hpp>

#include <cmath>

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

template <typename Result, typename Body>
Result npcValue(JNIEnv* env, jstring world, jint id, Result fallback, Body&& body) {
    const auto found = worldOf(env, world);
    if (found == nullptr) {
        return fallback;
    }
    return guarded<Result>(fallback, [&] {
        const std::shared_ptr<PluginNpcRef> npc = found->npcById(id);
        return npc == nullptr ? fallback : body(*npc);
    });
}

jdouble JNICALL nativeNpcHealth(JNIEnv* env, jclass, jstring world, jint id) {
    return npcValue<jdouble>(env, world, id, 20.0, [](PluginNpcRef& npc) { return npc.health(); });
}

void JNICALL nativeNpcSetHealth(JNIEnv* env, jclass, jstring world, jint id, jdouble value) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.setHealth(value); });
}

jint JNICALL nativeNpcPostureOf(JNIEnv* env, jclass, jstring world, jint id) {
    return npcValue<jint>(env, world, id, 0,
                          [](PluginNpcRef& npc) { return static_cast<jint>(npc.flags() | (npc.pose() << 8)); });
}

jbyteArray JNICALL nativeNpcItem(JNIEnv* env, jclass, jstring world, jint id, jint slot) {
    const std::vector<std::uint8_t> stack =
        npcValue<std::vector<std::uint8_t>>(env, world, id, {}, [slot](PluginNpcRef& npc) { return npc.item(slot); });
    return stack.empty() ? nullptr : byteArray(env, stack);
}

void JNICALL nativeNpcSetItem(JNIEnv* env, jclass, jstring world, jint id, jint slot, jbyteArray stack) {
    const std::vector<std::uint8_t> data = bytes(env, stack);
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.setItem(slot, data); });
}

void JNICALL nativeNpcClearItems(JNIEnv* env, jclass, jstring world, jint id) {
    withNpc(env, world, id, [](PluginNpcRef& npc) { npc.clearItems(); });
}

jint JNICALL nativeNpcHeld(JNIEnv* env, jclass, jstring world, jint id) {
    return npcValue<jint>(env, world, id, 0, [](PluginNpcRef& npc) { return npc.held(); });
}

void JNICALL nativeNpcSetHeld(JNIEnv* env, jclass, jstring world, jint id, jint slot) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.setHeld(slot); });
}

void JNICALL nativeNpcAttrRegister(JNIEnv* env, jclass, jstring world, jint id, jstring key, jdouble fallback,
                                   jdouble lowest, jdouble highest) {
    const std::string name = toStd(env, key);
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.registerAttribute(name, fallback, lowest, highest); });
}

jdouble JNICALL nativeNpcAttrBase(JNIEnv* env, jclass, jstring world, jint id, jstring key) {
    const std::string name = toStd(env, key);
    return npcValue<jdouble>(env, world, id, std::nan(""),
                             [&](PluginNpcRef& npc) { return npc.attributeBase(name).value_or(std::nan("")); });
}

void JNICALL nativeNpcAttrSetBase(JNIEnv* env, jclass, jstring world, jint id, jstring key, jdouble value) {
    const std::string name = toStd(env, key);
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.setAttributeBase(name, value); });
}

jdouble JNICALL nativeNpcAttrValue(JNIEnv* env, jclass, jstring world, jint id, jstring key) {
    const std::string name = toStd(env, key);
    return npcValue<jdouble>(env, world, id, std::nan(""),
                             [&](PluginNpcRef& npc) { return npc.attributeValue(name).value_or(std::nan("")); });
}

jobjectArray JNICALL nativeNpcAttrModifiers(JNIEnv* env, jclass, jstring world, jint id, jstring key) {
    const std::string name = toStd(env, key);
    const std::vector<std::string> fields =
        npcValue<std::vector<std::string>>(env, world, id, {}, [&](PluginNpcRef& npc) {
            std::vector<std::string> flat;
            for (const server::plugins::NpcModifier& modifier : npc.attributeModifiers(name)) {
                flat.push_back(modifier.id);
                flat.push_back(std::to_string(modifier.amount));
                flat.push_back(std::to_string(modifier.operation));
                flat.push_back(modifier.slot);
            }
            return flat;
        });
    return stringArray(env, fields);
}

void JNICALL nativeNpcAttrAdd(JNIEnv* env, jclass, jstring world, jint id, jstring key, jstring modifierId,
                              jdouble amount, jint operation, jstring slot) {
    const std::string name = toStd(env, key);
    const server::plugins::NpcModifier modifier{
        .id = toStd(env, modifierId), .amount = amount, .operation = operation, .slot = toStd(env, slot)};
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.addAttributeModifier(name, modifier); });
}

void JNICALL nativeNpcAttrRemove(JNIEnv* env, jclass, jstring world, jint id, jstring key, jstring modifierId) {
    const std::string name = toStd(env, key);
    const std::string modifier = toStd(env, modifierId);
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.removeAttributeModifier(name, modifier); });
}

void JNICALL nativeNpcAnimate(JNIEnv* env, jclass, jstring world, jint id, jint action) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.animate(action); });
}

void JNICALL nativeNpcRemove(JNIEnv* env, jclass, jstring world, jint id) {
    withNpc(env, world, id, [&](PluginNpcRef& npc) { npc.remove(); });
}

jstring JNICALL nativeProfileUuid(JNIEnv* env, jclass, jstring name) {
    const std::optional<std::string> id = wire::auth::mojangUuidOf(toStd(env, name));
    return id.has_value() ? toJava(env, *id) : nullptr;
}

jobjectArray JNICALL nativeProfileTextures(JNIEnv* env, jclass, jstring uuid) {
    const std::optional<wire::auth::SessionProfile> profile = wire::auth::mojangProfileOf(toStd(env, uuid));
    if (!profile.has_value()) {
        return nullptr;
    }
    std::vector<std::string> fields{profile->name};
    for (const wire::auth::SessionProfileProperty& property : profile->properties) {
        fields.push_back(property.name);
        fields.push_back(property.value);
        fields.push_back(property.signature.value_or(""));
    }
    return stringArray(env, fields);
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
        nativeMethod("npcHealth", "(Ljava/lang/String;I)D", reinterpret_cast<void*>(&nativeNpcHealth)),
        nativeMethod("npcSetHealth", "(Ljava/lang/String;ID)V", reinterpret_cast<void*>(&nativeNpcSetHealth)),
        nativeMethod("npcPostureOf", "(Ljava/lang/String;I)I", reinterpret_cast<void*>(&nativeNpcPostureOf)),
        nativeMethod("npcItem", "(Ljava/lang/String;II)[B", reinterpret_cast<void*>(&nativeNpcItem)),
        nativeMethod("npcSetItem", "(Ljava/lang/String;II[B)V", reinterpret_cast<void*>(&nativeNpcSetItem)),
        nativeMethod("npcClearItems", "(Ljava/lang/String;I)V", reinterpret_cast<void*>(&nativeNpcClearItems)),
        nativeMethod("npcHeld", "(Ljava/lang/String;I)I", reinterpret_cast<void*>(&nativeNpcHeld)),
        nativeMethod("npcSetHeld", "(Ljava/lang/String;II)V", reinterpret_cast<void*>(&nativeNpcSetHeld)),
        nativeMethod("npcAttrRegister", "(Ljava/lang/String;ILjava/lang/String;DDD)V",
                     reinterpret_cast<void*>(&nativeNpcAttrRegister)),
        nativeMethod("npcAttrBase", "(Ljava/lang/String;ILjava/lang/String;)D",
                     reinterpret_cast<void*>(&nativeNpcAttrBase)),
        nativeMethod("npcAttrSetBase", "(Ljava/lang/String;ILjava/lang/String;D)V",
                     reinterpret_cast<void*>(&nativeNpcAttrSetBase)),
        nativeMethod("npcAttrValue", "(Ljava/lang/String;ILjava/lang/String;)D",
                     reinterpret_cast<void*>(&nativeNpcAttrValue)),
        nativeMethod("npcAttrModifiers", "(Ljava/lang/String;ILjava/lang/String;)[Ljava/lang/String;",
                     reinterpret_cast<void*>(&nativeNpcAttrModifiers)),
        nativeMethod("npcAttrAdd", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;DILjava/lang/String;)V",
                     reinterpret_cast<void*>(&nativeNpcAttrAdd)),
        nativeMethod("npcAttrRemove", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V",
                     reinterpret_cast<void*>(&nativeNpcAttrRemove)),
        nativeMethod("npcAnimate", "(Ljava/lang/String;II)V", reinterpret_cast<void*>(&nativeNpcAnimate)),
        nativeMethod("npcRemove", "(Ljava/lang/String;I)V", reinterpret_cast<void*>(&nativeNpcRemove)),
        nativeMethod("profileUuid", "(Ljava/lang/String;)Ljava/lang/String;",
                     reinterpret_cast<void*>(&nativeProfileUuid)),
        nativeMethod("profileTextures", "(Ljava/lang/String;)[Ljava/lang/String;",
                     reinterpret_cast<void*>(&nativeProfileTextures)),
    };
    registerMethods(env, natives, methods.data(), static_cast<jint>(methods.size()));
}
} // namespace basalt::stratum::bukkit
