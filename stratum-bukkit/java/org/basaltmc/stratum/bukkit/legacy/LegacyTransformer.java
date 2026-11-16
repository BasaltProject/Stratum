package org.basaltmc.stratum.bukkit.legacy;

import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassHierarchyResolver;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Opcode;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.concurrent.ConcurrentHashMap;

/** Rewrites a plugin class built for an older Bukkit API so it links against the current one. */
public final class LegacyTransformer {
    private static final ConcurrentHashMap<String, Boolean> kInterfaces = new ConcurrentHashMap<>();
    private static final String kMaterialType = "Lorg/bukkit/Material;";

    /** Built on first use, after the constant classes are up: building them in the outer initializer is circular. */
    private static final class Descs {
        static final ClassDesc kObject = ConstantDescs.CD_Object;
        static final ClassDesc kMaterials = ClassDesc.of("org.basaltmc.stratum.bukkit.legacy.LegacyMaterials");
        static final MethodTypeDesc kStep = MethodTypeDesc.ofDescriptor("(" + kMaterialType + ")" + kMaterialType);
        static final MethodTypeDesc kWithData =
            MethodTypeDesc.ofDescriptor("(" + kMaterialType + "B)" + kMaterialType);
    }

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
        return classFile.transformClass(classFile.parse(bytes),
            ClassTransform.transformingMethodBodies((builder, element) -> {
                if (element instanceof InvokeInstruction call) {
                    invoke(builder, call, plugin);
                } else {
                    builder.with(element);
                }
            }));
    }

    private static void invoke(CodeBuilder builder, InvokeInstruction call, ApiVersion plugin) {
        String owner = call.owner().asInternalName();
        String name = call.name().stringValue();
        String descriptor = call.type().stringValue();
        Opcode opcode = call.opcode();
        LegacyCalls.Target helper =
            opcode != Opcode.INVOKESPECIAL ? LegacyCalls.instanceCall(plugin, owner, name, descriptor) : null;
        if (helper != null) {
            builder.invokestatic(ClassDesc.of(helper.owner().replace('/', '.')), helper.name(),
                MethodTypeDesc.ofDescriptor(helper.descriptor()));
            return;
        }
        boolean isInterface = call.isInterface();
        if (!isInterface && opcode != Opcode.INVOKESPECIAL && becameInterface(owner)) {
            opcode = opcode == Opcode.INVOKEVIRTUAL ? Opcode.INVOKEINTERFACE : opcode;
            isInterface = true;
        }
        if (opcode != Opcode.INVOKESPECIAL || name.equals("<init>")) {
            if (LegacyCalls.bridgesMaterial(plugin, owner, name, descriptor, opcode == Opcode.INVOKESTATIC)) {
                if (bridgeMaterial(builder, call, opcode, isInterface)) {
                    return;
                }
            }
        }
        if (isInterface != call.isInterface()) {
            builder.invoke(opcode, call.owner().asSymbol(), name, call.typeSymbol(), true);
        } else {
            builder.with(call);
        }
    }

    private static boolean isMaterial(ClassDesc type) {
        return type.descriptorString().equals(kMaterialType);
    }

    private static boolean convertible(MethodTypeDesc type, int at) {
        int above = type.parameterCount() - 1 - at;
        if (above == 0) {
            return true;
        }
        ClassDesc top = type.parameterType(at + 1);
        return above == 1 && !top.descriptorString().equals("J") && !top.descriptorString().equals("D");
    }

    /** Converts Materials in place on the stack, since Shim mishandles locals the original code never declared. */
    private static boolean bridgeMaterial(CodeBuilder builder, InvokeInstruction call, Opcode opcode,
            boolean isInterface) {
        MethodTypeDesc type = call.typeSymbol();
        String name = call.name().stringValue();
        String owner = call.owner().asInternalName();
        int count = type.parameterCount();
        for (int at = 0; at < count; at++) {
            if (isMaterial(type.parameterType(at)) && !convertible(type, at)) {
                return false;
            }
        }
        boolean receiver = opcode != Opcode.INVOKESTATIC && count == 0 && LegacyCalls.modernReceiver(owner, name);
        for (int at = count - 1; at >= 0; at--) {
            if (!isMaterial(type.parameterType(at))) {
                continue;
            }
            if (at == count - 1) {
                builder.invokestatic(Descs.kMaterials, "modern", Descs.kStep);
            } else if (type.parameterType(at + 1).descriptorString().equals("B")) {
                builder.dup_x1();
                builder.invokestatic(Descs.kMaterials, "modern", Descs.kWithData);
                builder.swap();
            } else {
                builder.swap();
                builder.invokestatic(Descs.kMaterials, "modern", Descs.kStep);
                builder.swap();
            }
        }
        if (receiver) {
            builder.invokestatic(Descs.kMaterials, "modern", Descs.kStep);
        }
        builder.invoke(opcode, call.owner().asSymbol(), name, type, isInterface);
        if (!name.equals("<init>") && isMaterial(type.returnType()) && !owner.startsWith("org/bukkit/material/")) {
            builder.invokestatic(Descs.kMaterials, "legacy", Descs.kStep);
        }
        return true;
    }
}
