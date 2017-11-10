package asokol.dao.exception;

/**
 * Represents the state when a file for storing an image cannot be created.
 */
public class FileCreationException extends RuntimeException {
    public FileCreationException(String message) {
        super(message);
    }
}
