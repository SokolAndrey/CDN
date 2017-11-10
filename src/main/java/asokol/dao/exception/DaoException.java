package asokol.dao.exception;

/**
 * Represents DAO layer exception
 */
public class DaoException extends RuntimeException{
    public DaoException(String message) {
        super(message);
    }
}
