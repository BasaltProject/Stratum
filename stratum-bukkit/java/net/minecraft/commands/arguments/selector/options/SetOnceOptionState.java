// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.commands.arguments.selector.options;

public class SetOnceOptionState {
    private boolean parsed;

    public boolean canParse() {
        return !parsed;
    }

    public void markParsed() {
        parsed = true;
    }
}
