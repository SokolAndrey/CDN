package asokol.handler;

import asokol.dao.exception.DaoException;
import asokol.service.exception.ImageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler for custom exceptions.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(value = HttpStatus.INSUFFICIENT_STORAGE)
    @ExceptionHandler(DaoException.class)
    protected void handleDaoExceptions(DaoException ex) {
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ImageNotFoundException.class)
    protected void handleDaoExceptions(ImageNotFoundException ex) {
    }
}
