package org.basaltmc.stratum.bukkit.legacy;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.StringConcatFactory;
import org.bukkit.Material;

/** Bootstraps string concatenation so a LEGACY_ Material is written the way a pre-flattening plugin expects. */
public final class LegacyConcat {
    private static final MethodHandle kStringify = stringify();

    private LegacyConcat() {}

    private static MethodHandle stringify() {
        try {
            return MethodHandles.lookup().findStatic(LegacyConcat.class, "text",
                MethodType.methodType(Object.class, Object.class));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Object text(Object value) {
        return value instanceof Material material ? LegacyMaterials.name(material) : value;
    }

    private static CallSite adapt(MethodType type, java.util.function.Function<MethodType, CallSite> factory) {
        MethodType widened = type;
        boolean any = false;
        for (int at = 0; at < type.parameterCount(); at++) {
            Class<?> parameter = type.parameterType(at);
            if (!parameter.isPrimitive() && parameter.isAssignableFrom(Material.class)) {
                widened = widened.changeParameterType(at, Object.class);
                any = true;
            }
        }
        if (!any) {
            return factory.apply(type);
        }
        MethodHandle target = factory.apply(widened).dynamicInvoker();
        for (int at = 0; at < type.parameterCount(); at++) {
            if (widened.parameterType(at) != type.parameterType(at)) {
                target = MethodHandles.filterArguments(target, at,
                    kStringify.asType(MethodType.methodType(Object.class, type.parameterType(at))));
            }
        }
        return new ConstantCallSite(target);
    }

    public static CallSite makeConcatWithConstants(MethodHandles.Lookup lookup, String name, MethodType type,
            String recipe, Object... constants) {
        return adapt(type, widened -> {
            try {
                return StringConcatFactory.makeConcatWithConstants(lookup, name, widened, recipe, constants);
            } catch (java.lang.invoke.StringConcatException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public static CallSite makeConcat(MethodHandles.Lookup lookup, String name, MethodType type) {
        return adapt(type, widened -> {
            try {
                return StringConcatFactory.makeConcat(lookup, name, widened);
            } catch (java.lang.invoke.StringConcatException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
