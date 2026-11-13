// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.network.chat;
public record LastSeenTrackedEntry(MessageSignature signature, boolean pending) {
    public LastSeenTrackedEntry acknowledge() {
        return pending ? new LastSeenTrackedEntry(signature, false) : this;
    }
}
