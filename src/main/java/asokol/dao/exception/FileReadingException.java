package asokol.dao.exception;

/**
 * Represents the state when a file cannot be read.
 */
public class FileReadingException extends DaoException {
    public FileReadingException(String message) {
        super(message);
    }
}
