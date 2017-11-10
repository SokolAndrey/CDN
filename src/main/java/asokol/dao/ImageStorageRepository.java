package asokol.dao;

import asokol.dto.UploadResultDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * A contract for image storage.
 */
public interface ImageStorageRepository {

    /**
     * Save given image.
     *
     * @param image input image.
     * @return the result of the uploading an image. See {@link UploadResultDTO} for details.
     */
    UploadResultDTO saveImage(MultipartFile image);

    /**
     * Retrieves and image by its ID.
     *
     * @param imageId image ID.
     * @return requested image.
     */
    Resource getImage(String imageId);

}
