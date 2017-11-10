package asokol.service.exception;

/**
 * Represents the state when the image is not found.
 */
public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}
