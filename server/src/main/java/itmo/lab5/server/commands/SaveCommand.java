package itmo.lab5.server.commands;

import itmo.lab5.server.io.Writer;
import itmo.lab5.shared.models.Flat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 *
 * @author oxff
 */
public class SaveCommand {
    public static String execute(HashMap<Integer, Flat> collection, Path filePath) {
        if (collection == null)
            return "Collection is empty: nothing to save!";

        if (filePath == null)
            return "Error: File path is not defined.";

        try {
            checkWritePermissions(filePath);
            new Writer().writeCollection(filePath.toString(), collection);
            return "Collection has been written!";
        } catch (SecurityException | IllegalArgumentException e) {
            return "Access denied: " + e.getMessage();
        } catch (IOException e) {
            return "An I/O error occurred while saving the collection: " + e.getMessage();
        }
    }

    /**
     * Checks whether the application can write to the given file.
     *
     * @param path the file path to check
     * @throws SecurityException if access is denied
     * @throws IOException       if an I/O error occurs
     */
    private static void checkWritePermissions(Path path) throws IOException {
        if (Files.exists(path)) {
            if (Files.isDirectory(path))
                throw new SecurityException("Path is a directory: " + path);
           
            if (!Files.isWritable(path))
                throw new SecurityException("File is not writable: " + path);
           
        } else {
            Path parentDir = path.getParent();
            if (parentDir == null || !Files.exists(parentDir))
                throw new SecurityException("Parent directory does not exist: " + parentDir);
            
            if (!Files.isDirectory(parentDir))
                throw new SecurityException("Parent path is not a directory: " + parentDir);
            
            if (!Files.isWritable(parentDir))
                throw new SecurityException("Directory is not writable: " + parentDir);
        }
    }
}