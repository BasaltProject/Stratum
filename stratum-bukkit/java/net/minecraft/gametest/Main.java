// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.gametest;

import net.minecraft.gametest.framework.GameTestMainUtil;

public class Main {
    public static void main(String[] args) throws Exception {
        GameTestMainUtil.runGameTestServer(args, universe -> { });
    }
}
