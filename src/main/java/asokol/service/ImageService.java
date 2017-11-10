package asokol.service;

import asokol.dao.ImageStorageRepository;
import asokol.dao.PictureStatisticRepository;
import asokol.dao.entity.PictureMetaData;
import asokol.dto.ImageStatisticDTO;
import asokol.dto.UploadResultDTO;
import asokol.dto.UploadResultDTO.Status;
import asokol.service.exception.ImageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Image service controls the process of the uploading and getting images and getting the statistics.
 */
@Slf4j
@Service
public class ImageService {

    private static final String FILE_PATH = "file/";

    private PictureStatisticRepository pictureStatisticRepository;
    private ImageStorageRepository fileSystemImageStorageRepository;

    @Autowired
    public ImageService(PictureStatisticRepository pictureStatisticRepository, ImageStorageRepository fileSystemImageStorageRepository) {
        this.pictureStatisticRepository = pictureStatisticRepository;
        this.fileSystemImageStorageRepository = fileSystemImageStorageRepository;
    }

    /**
     * Save an image into file storage {@link ImageStorageRepository}
     * and insert record into meta repository {@link PictureStatisticRepository}.
     *
     * @param file     input file.
     * @param hostName the host name from where the file is available.
     * @return URL where the saved file is available.
     */
    public String saveImage(MultipartFile file, String hostName) {
        UploadResultDTO uploadResult = fileSystemImageStorageRepository.saveImage(file);
        Status status = uploadResult.getStatus();
        String imageUrl = null;
        if (Status.OK.equals(status)) {
            imageUrl = generateUrl(uploadResult.getImageId(), hostName);
            pictureStatisticRepository.save(new PictureMetaData(imageUrl, 0L));
        }
        if (Status.ALREADY_EXIST.equals(status) || Status.OK.equals(status)) {
            imageUrl = hostName + FILE_PATH + uploadResult.getImageId();
        }
        return imageUrl;
    }

    /**
     * Retrieve a file by {@code imageId} and updates data about the number of downloads.
     *
     * @param imageId the ID of the image.
     * @return requested image as a {@link Resource}.
     * @throws ImageNotFoundException when there is no image with given image ID.
     */
    public Resource getImage(String imageId) {

        PictureMetaData pictureMetaData = pictureStatisticRepository.findOne(imageId);
        if (Objects.isNull(pictureMetaData)) {
            log.error("The metadata for image with id: {} is not found", imageId);
            throw new ImageNotFoundException("The image with id: " + imageId + " is not found");
        }
        Resource resource = fileSystemImageStorageRepository.getImage(imageId);
        if (Objects.isNull(resource)) {
            // TODO(asokol): 11/9/17 what to do in that case?
            log.error("The image with id: {} is not found in file storage", imageId);
            throw new ImageNotFoundException("The image with id: " + imageId + " is not found");
        } else {
            pictureMetaData.increaseDownloads();
            pictureStatisticRepository.save(pictureMetaData);
        }
        return resource;
    }

    /**
     * Get statistics for uploaded images.
     * Such as the number of downloads and the URL where the image is available.
     *
     * @param hostName the name of the host where the image is available.
     * @return a map of image ID to its statistic information.
     */
    public Map<String, ImageStatisticDTO> getStatistics(String hostName) {
        return StreamSupport.stream(pictureStatisticRepository.findAll().spliterator(), false)
                .collect(
                        Collectors.toMap(
                                PictureMetaData::getId,
                                a -> new ImageStatisticDTO(generateUrl(a.getId(), hostName), a.getDownloads())
                        )
                );
    }

    /**
     * Concat an image ID and host where this image is available into URL.
     *
     * @param id       of the image.
     * @param hostName where the image should be available.
     * @return URL of the image.
     */
    private String generateUrl(String id, String hostName) {
        return hostName + FILE_PATH + id;
    }
}