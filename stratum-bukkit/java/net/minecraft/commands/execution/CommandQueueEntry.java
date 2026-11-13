// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.commands.execution;
public record CommandQueueEntry<T>(Frame frame, EntryAction<T> action) {
    public void execute(ExecutionContext<T> context) {
        action.execute(context, frame);
    }
}
