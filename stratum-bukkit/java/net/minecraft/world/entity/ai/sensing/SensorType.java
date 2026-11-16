// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.armadillo.Armadillo;

public class SensorType<U extends Sensor<?>> {
    private final Supplier<U> factory;

    public SensorType(Supplier<U> factory) {
        this.factory = factory;
    }

    public U create() {
        return factory.get();
    }
    public static final SensorType<DummySensor> DUMMY = null;
    public static final SensorType<NearestItemSensor> NEAREST_ITEMS = null;
    public static final SensorType<NearestLivingEntitySensor<LivingEntity>> NEAREST_LIVING_ENTITIES = null;
    public static final SensorType<PlayerSensor> NEAREST_PLAYERS = null;
    public static final SensorType<NearestBedSensor> NEAREST_BED = null;
    public static final SensorType<HurtBySensor> HURT_BY = null;
    public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = null;
    public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = null;
    public static final SensorType<SecondaryPoiSensor> SECONDARY_POIS = null;
    public static final SensorType<GolemSensor> GOLEM_DETECTED = null;
    public static final SensorType<MobSensor<Armadillo>> ARMADILLO_SCARE_DETECTED = null;
    public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = null;
    public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = null;
    public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = null;
    public static final SensorType<AdultSensor> NEAREST_ADULT = null;
    public static final SensorType<AdultSensor> NEAREST_ADULT_ANY_TYPE = null;
    public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = null;
    public static final SensorType<TemptingSensor> FOOD_TEMPTATIONS = null;
    public static final SensorType<TemptingSensor> FROG_TEMPTATIONS = null;
    public static final SensorType<TemptingSensor> NAUTILUS_TEMPTATIONS = null;
    public static final SensorType<FrogAttackablesSensor> FROG_ATTACKABLES = null;
    public static final SensorType<IsInWaterSensor> IS_IN_WATER = null;
    public static final SensorType<WardenEntitySensor> WARDEN_ENTITY_SENSOR = null;
    public static final SensorType<BreezeAttackEntitySensor> BREEZE_ATTACK_ENTITY_SENSOR = null;
}
