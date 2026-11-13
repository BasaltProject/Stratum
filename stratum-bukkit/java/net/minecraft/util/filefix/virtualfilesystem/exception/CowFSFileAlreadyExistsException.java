// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.filefix.virtualfilesystem.exception;
import java.nio.file.FileAlreadyExistsException;
public class CowFSFileAlreadyExistsException extends FileAlreadyExistsException {
    public CowFSFileAlreadyExistsException(String message) {
        super(message);
    }
}
