#include "JniSupport.hpp"
#include "NativeTable.hpp"

#include <basalt/server/plugins/PluginMechanics.hpp>
#include <basalt/server/plugins/PluginWorldEnv.hpp>

#include <array>

namespace basalt::stratum::bukkit {
namespace {
namespace mechanics = server::plugins::mechanics;

std::vector<double> doublesOf(JNIEnv* env, jdoubleArray array) {
    std::vector<double> out(array == nullptr ? 0 : static_cast<std::size_t>(env->GetArrayLength(array)));
    if (!out.empty()) {
        env->GetDoubleArrayRegion(array, 0, static_cast<jsize>(out.size()), out.data());
    }
    return out;
}

jdoubleArray arrayOfDoubles(JNIEnv* env, std::span<const double> values) {
    jdoubleArray out = env->NewDoubleArray(static_cast<jsize>(values.size()));
    if (out != nullptr) {
        env->SetDoubleArrayRegion(out, 0, static_cast<jsize>(values.size()), values.data());
    }
    return out;
}

jfloatArray arrayOfFloats(JNIEnv* env, std::span<const jfloat> values) {
    jfloatArray out = env->NewFloatArray(static_cast<jsize>(values.size()));
    if (out != nullptr) {
        env->SetFloatArrayRegion(out, 0, static_cast<jsize>(values.size()), values.data());
    }
    return out;
}

mechanics::Point pointOf(const std::vector<double>& values) {
    return values.size() < 3 ? mechanics::Point{} : mechanics::Point{values[0], values[1], values[2]};
}

template <std::size_t N>
jdoubleArray arrayOrNull(JNIEnv* env, const std::optional<std::array<double, N>>& values) {
    return values.has_value() ? arrayOfDoubles(env, *values) : nullptr;
}

jdoubleArray JNICALL nativeCollide(JNIEnv* env, jclass, jdoubleArray colliders, jdoubleArray box, jdouble mx,
                                   jdouble my, jdouble mz) {
    return arrayOfDoubles(env, mechanics::collide(doublesOf(env, colliders), doublesOf(env, box), {mx, my, mz}));
}

jdoubleArray JNICALL nativeStepUp(JNIEnv* env, jclass, jdoubleArray colliders, jdoubleArray grounded,
                                  jdouble groundedOffsetY, jdouble mx, jdouble my, jdouble mz, jfloat maxUpStep,
                                  jdouble reachSquared) {
    const auto stepped = mechanics::stepUp(doublesOf(env, colliders), doublesOf(env, grounded), groundedOffsetY,
                                           {mx, my, mz}, maxUpStep, reachSquared);
    return stepped.has_value() ? arrayOfDoubles(env, *stepped) : nullptr;
}

jdouble JNICALL nativeOffspringAttribute(JNIEnv*, jclass, jdouble parentA, jdouble parentB, jdouble min, jdouble max,
                                         jdouble first, jdouble second, jdouble third) {
    return mechanics::offspringAttribute(parentA, parentB, min, max, first, second, third);
}

jobjectArray JNICALL nativeCreativeTabItems(JNIEnv* env, jclass, jstring tab) {
    return jni::stringArrayOf(env, mechanics::creativeTabItems(toStd(env, tab)));
}

jbyteArray JNICALL nativeRegionRead(JNIEnv* env, jclass, jstring folder, jint chunkX, jint chunkZ) {
    return jni::byteArrayOf(env, mechanics::regionRead(toStd(env, folder), chunkX, chunkZ));
}

void JNICALL nativeRegionWrite(JNIEnv* env, jclass, jstring folder, jint chunkX, jint chunkZ, jbyteArray data,
                               jint timestamp) {
    mechanics::regionWrite(toStd(env, folder), chunkX, chunkZ, jni::bytesOf(env, data), timestamp);
}

jint JNICALL nativeRegionTimestamp(JNIEnv* env, jclass, jstring folder, jint chunkX, jint chunkZ) {
    return mechanics::regionTimestamp(toStd(env, folder), chunkX, chunkZ);
}

jboolean JNICALL nativeRegionFlush(JNIEnv* env, jclass, jstring folder, jboolean release) {
    return mechanics::regionFlush(toStd(env, folder), release == JNI_TRUE) ? JNI_TRUE : JNI_FALSE;
}

jfloat JNICALL nativeLevelValue(JNIEnv* env, jclass, jdoubleArray program, jint level) {
    return mechanics::levelValue(doublesOf(env, program), level);
}

jintArray JNICALL nativeBeaconScan(JNIEnv* env, jclass, jstring world, jint x, jint y, jint z) {
    const std::string name = toStd(env, world);
    const server::plugins::PluginBeacon scan = jni::onServer<server::plugins::PluginBeacon>({}, [&] {
        const auto found = BukkitBridge::current().services().worlds().byName(name);
        server::plugins::PluginWorldEnv* target = found == nullptr ? nullptr : found->env();
        return target == nullptr ? server::plugins::PluginBeacon{} : target->beaconScan(x, y, z);
    });
    std::vector<jint> flat{scan.levels};
    for (const auto& section : scan.sections) {
        flat.push_back(section[0]);
        flat.push_back(section[1]);
    }
    jintArray out = env->NewIntArray(static_cast<jsize>(flat.size()));
    if (out != nullptr) {
        env->SetIntArrayRegion(out, 0, static_cast<jsize>(flat.size()), flat.data());
    }
    return out;
}

jfloat JNICALL nativeAfterArmor(JNIEnv*, jclass, jfloat damage, jfloat armor, jfloat toughness, jfloat fractionAdd) {
    return mechanics::afterArmour(damage, armor, toughness, fractionAdd);
}

jboolean JNICALL nativeScalesWithDifficulty(JNIEnv* env, jclass, jstring scaling, jboolean livingNonPlayer) {
    return mechanics::scalesWithDifficulty(toStd(env, scaling), livingNonPlayer == JNI_TRUE) ? JNI_TRUE : JNI_FALSE;
}

jfloat JNICALL nativeAfterProtection(JNIEnv*, jclass, jfloat damage, jfloat protection) {
    return mechanics::afterProtection(damage, protection);
}

jfloat JNICALL nativeAfterResistance(JNIEnv*, jclass, jfloat damage, jint amplifier) {
    return mechanics::afterResistance(damage, amplifier);
}

jfloatArray JNICALL nativeAbsorb(JNIEnv* env, jclass, jfloat damage, jfloat absorption) {
    return arrayOfFloats(env, mechanics::absorb(damage, absorption));
}

jfloatArray JNICALL nativeRotateTowards(JNIEnv* env, jclass, jdouble mx, jdouble my, jdouble mz, jfloat xRotO,
                                        jfloat yRotO, jfloat xRot, jfloat yRot, jfloat speed) {
    const auto turned = mechanics::rotateTowardsMovement(mx, my, mz, xRotO, yRotO, xRot, yRot, speed);
    return turned.has_value() ? arrayOfFloats(env, *turned) : nullptr;
}

jdoubleArray JNICALL nativeClosestHit(JNIEnv* env, jclass, jdoubleArray candidates, jdouble fx, jdouble fy, jdouble fz,
                                      jdouble tx, jdouble ty, jdouble tz, jfloat margin) {
    return arrayOrNull(env, mechanics::closestHit(doublesOf(env, candidates), {fx, fy, fz}, {tx, ty, tz}, margin));
}

jdoubleArray JNICALL nativePickedHit(JNIEnv* env, jclass, jdoubleArray candidates, jint root, jdouble fx, jdouble fy,
                                     jdouble fz, jdouble tx, jdouble ty, jdouble tz, jdouble maxValue) {
    return arrayOrNull(env,
                       mechanics::pickedHit(doublesOf(env, candidates), root, {fx, fy, fz}, {tx, ty, tz}, maxValue));
}

jfloat JNICALL nativeProjectileMargin(JNIEnv*, jclass, jint tickCount) {
    return mechanics::projectileMargin(tickCount);
}

jdoubleArray JNICALL nativeShulkerBox(JNIEnv* env, jclass, jfloat size, jint direction, jfloat progressFrom,
                                      jfloat progressTo, jdouble x, jdouble y, jdouble z) {
    return arrayOfDoubles(env, mechanics::shulkerBox(size, direction, progressFrom, progressTo, {x, y, z}));
}

jfloat JNICALL nativeAngularFriction(JNIEnv*, jclass, jboolean onGround, jfloat blockFriction, jboolean inLiquid) {
    return mechanics::angularFriction(onGround == JNI_TRUE, blockFriction, inLiquid == JNI_TRUE);
}

jdoubleArray JNICALL nativeLeashPull(JNIEnv* env, jclass, jdoubleArray self, jdoubleArray holder, jboolean quad,
                                     jdouble slack, jdoubleArray selfMotion, jdoubleArray holderMotion,
                                     jdoubleArray knownMotion) {
    return arrayOrNull(env,
                       mechanics::leashPull(doublesOf(env, self), doublesOf(env, holder), quad == JNI_TRUE, slack,
                                            pointOf(doublesOf(env, selfMotion)), pointOf(doublesOf(env, holderMotion)),
                                            pointOf(doublesOf(env, knownMotion))));
}
} // namespace

void registerMechanicsNatives(JNIEnv* env, jclass natives) {
    const std::array methods{
        nativeMethod("afterArmor", "(FFFF)F", reinterpret_cast<void*>(&nativeAfterArmor)),
        nativeMethod("afterProtection", "(FF)F", reinterpret_cast<void*>(&nativeAfterProtection)),
        nativeMethod("scalesWithDifficulty", "(Ljava/lang/String;Z)Z",
                     reinterpret_cast<void*>(&nativeScalesWithDifficulty)),
        nativeMethod("afterResistance", "(FI)F", reinterpret_cast<void*>(&nativeAfterResistance)),
        nativeMethod("absorb", "(FF)[F", reinterpret_cast<void*>(&nativeAbsorb)),
        nativeMethod("rotateTowardsMovement", "(DDDFFFFF)[F", reinterpret_cast<void*>(&nativeRotateTowards)),
        nativeMethod("closestHit", "([DDDDDDDF)[D", reinterpret_cast<void*>(&nativeClosestHit)),
        nativeMethod("pickedHit", "([DIDDDDDDD)[D", reinterpret_cast<void*>(&nativePickedHit)),
        nativeMethod("projectileMargin", "(I)F", reinterpret_cast<void*>(&nativeProjectileMargin)),
        nativeMethod("shulkerBox", "(FIFFDDD)[D", reinterpret_cast<void*>(&nativeShulkerBox)),
        nativeMethod("angularFriction", "(ZFZ)F", reinterpret_cast<void*>(&nativeAngularFriction)),
        nativeMethod("collide", "([D[DDDD)[D", reinterpret_cast<void*>(&nativeCollide)),
        nativeMethod("stepUp", "([D[DDDDDFD)[D", reinterpret_cast<void*>(&nativeStepUp)),
        nativeMethod("offspringAttribute", "(DDDDDDD)D", reinterpret_cast<void*>(&nativeOffspringAttribute)),
        nativeMethod("creativeTabItems", "(Ljava/lang/String;)[Ljava/lang/String;",
                     reinterpret_cast<void*>(&nativeCreativeTabItems)),
        nativeMethod("regionRead", "(Ljava/lang/String;II)[B", reinterpret_cast<void*>(&nativeRegionRead)),
        nativeMethod("regionWrite", "(Ljava/lang/String;II[BI)V", reinterpret_cast<void*>(&nativeRegionWrite)),
        nativeMethod("regionTimestamp", "(Ljava/lang/String;II)I", reinterpret_cast<void*>(&nativeRegionTimestamp)),
        nativeMethod("regionFlush", "(Ljava/lang/String;Z)Z", reinterpret_cast<void*>(&nativeRegionFlush)),
        nativeMethod("levelValue", "([DI)F", reinterpret_cast<void*>(&nativeLevelValue)),
        nativeMethod("beaconScan", "(Ljava/lang/String;III)[I", reinterpret_cast<void*>(&nativeBeaconScan)),
        nativeMethod("leashPull", "([D[DZD[D[D[D)[D", reinterpret_cast<void*>(&nativeLeashPull)),
    };
    registerMethods(env, natives, methods.data(), static_cast<jint>(methods.size()));
}
} // namespace basalt::stratum::bukkit
