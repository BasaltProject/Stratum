package org.basaltmc.stratum.bukkit.legacy;

import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassHierarchyResolver;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.Opcode;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.concurrent.ConcurrentHashMap;

/** Rewrites a plugin class built for an older Bukkit API so it links against the current one. */
public final class LegacyTransformer {
    private static final ConcurrentHashMap<String, Boolean> kInterfaces = new ConcurrentHashMap<>();

    private LegacyTransformer() {}

    private static boolean becameInterface(String owner) {
        if (!owner.startsWith("org/bukkit/")) {
            return false;
        }
        return kInterfaces.computeIfAbsent(owner, name -> {
            try {
                return Class.forName(name.replace('/', '.'), false, LegacyTransformer.class.getClassLoader()).isInterface();
            } catch (ClassNotFoundException | LinkageError e) {
                return false;
            }
        });
    }

    private static boolean materialExists(String name) {
        return org.bukkit.Material.getMaterial(name) != null;
    }

    /** The rewritten class, or the same array when nothing in it needs changing. */
    public static byte[] transform(byte[] bytes, ApiVersion plugin, ClassLoader loader) {
        ConstantPoolPatch.Result patched = ConstantPoolPatch.apply(bytes, plugin, LegacyTransformer::becameInterface,
            LegacyTransformer::materialExists, LegacyTransformer::materialExists);
        if (patched == null) {
            return bytes;
        }
        return patched.formerClassCalls() ? interfaceCalls(patched.bytes(), plugin, loader) : patched.bytes();
    }

    private static byte[] interfaceCalls(byte[] bytes, ApiVersion plugin, ClassLoader loader) {
        ClassFile classFile = ClassFile.of(ClassFile.ClassHierarchyResolverOption.of(
            ClassHierarchyResolver.defaultResolver().orElse(ClassHierarchyResolver.ofResourceParsing(loader))));
        return classFile.transformClass(classFile.parse(bytes), ClassTransform.transformingMethodBodies((builder, element) -> {
            LegacyCalls.Target helper = element instanceof InvokeInstruction call
                && call.opcode() != Opcode.INVOKESPECIAL && plugin.isOlderThan(ApiVersion.kFlattening)
                ? LegacyCalls.instanceCall(call.owner().asInternalName(), call.name().stringValue(),
                    call.type().stringValue()) : null;
            if (helper != null) {
                builder.invokestatic(ClassDesc.of(helper.owner().replace('/', '.')), helper.name(),
                    MethodTypeDesc.ofDescriptor(helper.descriptor()));
            } else if (element instanceof InvokeInstruction call && !call.isInterface() && call.opcode() != Opcode.INVOKESPECIAL
                    && becameInterface(call.owner().asInternalName())) {
                Opcode opcode = call.opcode() == Opcode.INVOKEVIRTUAL ? Opcode.INVOKEINTERFACE : call.opcode();
                builder.invoke(opcode, call.owner().asSymbol(), call.name().stringValue(), call.typeSymbol(), true);
            } else {
                builder.with(element);
            }
        }));
    }
}
