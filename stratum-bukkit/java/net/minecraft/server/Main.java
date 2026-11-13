// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.server;

public class Main {
    public static void main(String[] args) {
        System.err.println("The server is started by the native Basalt launcher, not by this class.");
        System.exit(2);
    }
}
