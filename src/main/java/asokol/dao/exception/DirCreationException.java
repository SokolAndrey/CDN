package asokol.dao.exception;

/**
 * Represents the state when a directory for storing images cannot be created.
 */
public class DirCreationException extends DaoException {
    public DirCreationException(String message) {
        super(message);
    }
}
