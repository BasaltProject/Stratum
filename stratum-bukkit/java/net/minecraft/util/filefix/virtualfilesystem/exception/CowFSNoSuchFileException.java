// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.filefix.virtualfilesystem.exception;
import java.nio.file.NoSuchFileException;
public class CowFSNoSuchFileException extends NoSuchFileException {
    public CowFSNoSuchFileException(String message) {
        super(message);
    }
}
