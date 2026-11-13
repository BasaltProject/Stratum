// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.gametest.framework;
public class ExhaustedAttemptsException extends Throwable {
    public ExhaustedAttemptsException(int attempts, int successes, GameTestInfo testInfo) {
        super();
    }
}
