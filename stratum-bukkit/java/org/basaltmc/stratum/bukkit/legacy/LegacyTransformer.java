package org.basaltmc.stratum.bukkit.legacy;

import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassHierarchyResolver;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeTransform;
import java.lang.classfile.Instruction;
import java.lang.classfile.instruction.TypeCheckInstruction;
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
        static final ClassDesc kEnums = ClassDesc.of("org.basaltmc.stratum.bukkit.legacy.LegacyEnums");
        static final MethodTypeDesc kValueOf =
            MethodTypeDesc.ofDescriptor("(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;");
        static final Conversion kMaterialConversion = new Conversion("modern", "legacy", kStep);
        static final Conversion kSetConversion = shaped("Set", "Ljava/util/Set;");
        static final Conversion kListConversion = shaped("List", "Ljava/util/List;");
        static final Conversion kCollectionConversion = shaped("Collection", "Ljava/util/Collection;");
        static final Conversion kArrayConversion = shaped("Array", "[Lorg/bukkit/Material;");
        static final MethodTypeDesc kWithData =
            MethodTypeDesc.ofDescriptor("(" + kMaterialType + "B)" + kMaterialType);
    }

    private static Conversion shaped(String shape, String descriptor) {
        return new Conversion("modern" + shape, "legacy" + shape,
            MethodTypeDesc.ofDescriptor("(" + descriptor + ")" + descriptor));
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

    private static final java.util.WeakHashMap<ClassLoader, java.util.HashMap<String, Boolean>> kWidens =
        new java.util.WeakHashMap<>();

    /** Whether the class is the plugin's own and takes Enum parameters that can widen; a cycle counts as widening. */
    private static boolean widensEnum(ClassLoader loader, String owner) {
        if (!(loader instanceof java.net.URLClassLoader urls)) {
            return false;
        }
        java.util.HashMap<String, Boolean> known;
        synchronized (kWidens) {
            known = kWidens.computeIfAbsent(loader, k -> new java.util.HashMap<>());
            Boolean cached = known.get(owner);
            if (cached != null) {
                return cached;
            }
            known.put(owner, Boolean.TRUE);
        }
        boolean result = false;
        java.net.URL resource = urls.findResource(owner + ".class");
        if (resource != null) {
            try (java.io.InputStream in = resource.openStream()) {
                result = ConstantPoolPatch.widensEnum(in.readAllBytes(), next -> widensEnum(loader, next));
            } catch (java.io.IOException | RuntimeException e) {
                result = false;
            }
        }
        synchronized (kWidens) {
            known.put(owner, result);
        }
        return result;
    }

    private static boolean materialExists(String name) {
        return org.bukkit.Material.getMaterial(name) != null;
    }

    /** The rewritten class, or the same array when nothing in it needs changing. */
    public static byte[] transform(byte[] bytes, ApiVersion plugin, ClassLoader loader) {
        ConstantPoolPatch.Result patched = ConstantPoolPatch.apply(bytes, plugin,
            new ConstantPoolPatch.Env(LegacyTransformer::becameInterface, LegacyTransformer::materialExists,
                LegacyTransformer::materialExists, owner -> widensEnum(loader, owner)));
        if (patched == null) {
            return bytes;
        }
        return patched.formerClassCalls() ? interfaceCalls(patched.bytes(), plugin, loader) : patched.bytes();
    }

    private static byte[] interfaceCalls(byte[] bytes, ApiVersion plugin, ClassLoader loader) {
        ClassFile classFile = ClassFile.of(ClassFile.ClassHierarchyResolverOption.of(
            ClassHierarchyResolver.defaultResolver().orElse(ClassHierarchyResolver.ofResourceParsing(loader))));
        return classFile.transformClass(classFile.parse(bytes),
            ClassTransform.transformingMethodBodies(new Rewriter(plugin)));
    }

    /** Rewrites the calls of one method; an Enum.valueOf waits for the next instruction to see whether a cast follows. */
    private static final class Rewriter implements CodeTransform {
        private final ApiVersion plugin;
        private InvokeInstruction pending;
        private final java.util.List<CodeElement> held = new java.util.ArrayList<>();

        Rewriter(ApiVersion plugin) {
            this.plugin = plugin;
        }

        private static boolean isEnumValueOf(InvokeInstruction call) {
            return call.opcode() == Opcode.INVOKESTATIC && call.name().equalsString("valueOf")
                && call.owner().asInternalName().equals("java/lang/Enum")
                && call.type().equalsString(LegacyCalls.kEnumValueOf);
        }

        @Override
        public void accept(CodeBuilder builder, CodeElement element) {
            if (pending != null) {
                if (!(element instanceof Instruction)) {
                    held.add(element);
                    return;
                }
                InvokeInstruction call = pending;
                pending = null;
                if (element instanceof TypeCheckInstruction check && check.opcode() == Opcode.CHECKCAST) {
                    builder.invokestatic(Descs.kEnums, plugin.isOlderThan(ApiVersion.kFlattening) ? "valueOfFlat" : "valueOf",
                        Descs.kValueOf);
                } else {
                    builder.with(call);
                }
                held.forEach(builder::with);
                held.clear();
            }
            if (element instanceof InvokeInstruction call) {
                if (plugin.isOlderThan(ApiVersion.kRegistryEnums) && isEnumValueOf(call)) {
                    pending = call;
                } else {
                    invoke(builder, call, plugin);
                }
            } else {
                builder.with(element);
            }
        }
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

    private record Conversion(String modern, String legacy, MethodTypeDesc type) {}

    private static Conversion conversionOf(ClassDesc type) {
        String descriptor = type.descriptorString();
        return switch (descriptor) {
            case kMaterialType -> Descs.kMaterialConversion;
            case "Ljava/util/Set;" -> Descs.kSetConversion;
            case "Ljava/util/List;" -> Descs.kListConversion;
            case "Ljava/util/Collection;" -> Descs.kCollectionConversion;
            case "[Lorg/bukkit/Material;" -> Descs.kArrayConversion;
            default -> null;
        };
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
            if (conversionOf(type.parameterType(at)) != null && !convertible(type, at)) {
                return false;
            }
        }
        boolean receiver = opcode != Opcode.INVOKESTATIC && count == 0 && LegacyCalls.modernReceiver(owner, name);
        for (int at = count - 1; at >= 0; at--) {
            Conversion conversion = conversionOf(type.parameterType(at));
            if (conversion == null) {
                continue;
            }
            if (at == count - 1) {
                builder.invokestatic(Descs.kMaterials, conversion.modern(), conversion.type());
            } else if (isMaterial(type.parameterType(at)) && type.parameterType(at + 1).descriptorString().equals("B")) {
                builder.dup_x1();
                builder.invokestatic(Descs.kMaterials, "modern", Descs.kWithData);
                builder.swap();
            } else {
                builder.swap();
                builder.invokestatic(Descs.kMaterials, conversion.modern(), conversion.type());
                builder.swap();
            }
        }
        if (receiver) {
            builder.invokestatic(Descs.kMaterials, "modern", Descs.kStep);
        }
        builder.invoke(opcode, call.owner().asSymbol(), name, type, isInterface);
        Conversion back = conversionOf(type.returnType());
        if (back != null && !name.equals("<init>") && !owner.startsWith("org/bukkit/material/")) {
            builder.invokestatic(Descs.kMaterials, back.legacy(), back.type());
        }
        return true;
    }
}
