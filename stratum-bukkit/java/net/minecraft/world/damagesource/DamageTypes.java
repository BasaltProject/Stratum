// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.damagesource;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public interface DamageTypes {
    ResourceKey<DamageType> IN_FIRE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("in_fire");
    ResourceKey<DamageType> CAMPFIRE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("campfire");
    ResourceKey<DamageType> LIGHTNING_BOLT = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("lightning_bolt");
    ResourceKey<DamageType> ON_FIRE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("on_fire");
    ResourceKey<DamageType> LAVA = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("lava");
    ResourceKey<DamageType> HOT_FLOOR = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("hot_floor");
    ResourceKey<DamageType> SULFUR_CUBE_HOT = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("sulfur_cube_hot");
    ResourceKey<DamageType> IN_WALL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("in_wall");
    ResourceKey<DamageType> CRAMMING = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("cramming");
    ResourceKey<DamageType> DROWN = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("drown");
    ResourceKey<DamageType> STARVE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("starve");
    ResourceKey<DamageType> CACTUS = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("cactus");
    ResourceKey<DamageType> FALL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("fall");
    ResourceKey<DamageType> ENDER_PEARL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("ender_pearl");
    ResourceKey<DamageType> FLY_INTO_WALL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("fly_into_wall");
    ResourceKey<DamageType> FELL_OUT_OF_WORLD = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("fell_out_of_world");
    ResourceKey<DamageType> GENERIC = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("generic");
    ResourceKey<DamageType> MAGIC = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("magic");
    ResourceKey<DamageType> WITHER = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("wither");
    ResourceKey<DamageType> DRAGON_BREATH = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("dragon_breath");
    ResourceKey<DamageType> DRY_OUT = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("dry_out");
    ResourceKey<DamageType> SWEET_BERRY_BUSH = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("sweet_berry_bush");
    ResourceKey<DamageType> FREEZE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("freeze");
    ResourceKey<DamageType> STALAGMITE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("stalagmite");
    ResourceKey<DamageType> FALLING_BLOCK = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("falling_block");
    ResourceKey<DamageType> FALLING_ANVIL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("falling_anvil");
    ResourceKey<DamageType> FALLING_STALACTITE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("falling_stalactite");
    ResourceKey<DamageType> STING = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("sting");
    ResourceKey<DamageType> MOB_ATTACK = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("mob_attack");
    ResourceKey<DamageType> MOB_ATTACK_NO_AGGRO = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("mob_attack_no_aggro");
    ResourceKey<DamageType> PLAYER_ATTACK = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("player_attack");
    ResourceKey<DamageType> SPEAR = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("spear");
    ResourceKey<DamageType> ARROW = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("arrow");
    ResourceKey<DamageType> TRIDENT = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("trident");
    ResourceKey<DamageType> MOB_PROJECTILE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("mob_projectile");
    ResourceKey<DamageType> SPIT = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("spit");
    ResourceKey<DamageType> WIND_CHARGE = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("wind_charge");
    ResourceKey<DamageType> FIREWORKS = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("fireworks");
    ResourceKey<DamageType> FIREBALL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("fireball");
    ResourceKey<DamageType> UNATTRIBUTED_FIREBALL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("unattributed_fireball");
    ResourceKey<DamageType> WITHER_SKULL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("wither_skull");
    ResourceKey<DamageType> THROWN = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("thrown");
    ResourceKey<DamageType> INDIRECT_MAGIC = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("indirect_magic");
    ResourceKey<DamageType> THORNS = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("thorns");
    ResourceKey<DamageType> EXPLOSION = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("explosion");
    ResourceKey<DamageType> PLAYER_EXPLOSION = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("player_explosion");
    ResourceKey<DamageType> SONIC_BOOM = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("sonic_boom");
    ResourceKey<DamageType> BAD_RESPAWN_POINT = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("bad_respawn_point");
    ResourceKey<DamageType> OUTSIDE_BORDER = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("outside_border");
    ResourceKey<DamageType> GENERIC_KILL = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("generic_kill");
    ResourceKey<DamageType> MACE_SMASH = org.basaltmc.stratum.bukkit.facade.DamageKeys.of("mace_smash");
    static void bootstrap(BootstrapContext<DamageType> context) {
        org.basaltmc.stratum.bukkit.facade.Bootstraps.replay(context, net.minecraft.core.registries.Registries.DAMAGE_TYPE, DamageTypes.class);
    }
}
