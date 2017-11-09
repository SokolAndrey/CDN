package asokol.service;

import asokol.dao.FileStorageRepository;
import asokol.dao.PictureMetaDataRepository;
import asokol.dao.entity.PictureMetaData;
import asokol.dto.FileStatisticDTO;
import asokol.dto.UploadResultDTO;
import asokol.dto.UploadResultDTO.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ImageService {

    private PictureMetaDataRepository pictureMetaDataRepository;
    private FileStorageRepository fileStorageRepository;

    @Autowired
    public ImageService(PictureMetaDataRepository pictureMetaDataRepository, FileStorageRepository fileStorageRepository) {
        this.pictureMetaDataRepository = pictureMetaDataRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

    public String saveImage(MultipartFile file, String hostName) {
        UploadResultDTO uploadResult = fileStorageRepository.saveImage(file);
        Status status = uploadResult.getStatus();
        String imageUrl = null;
        if (Status.OK.equals(status)) {
            imageUrl = genUrl(uploadResult.getImageId(), hostName);
            pictureMetaDataRepository.save(new PictureMetaData(imageUrl, 0L));
        }
        if (Status.ALREADY_EXIST.equals(status) || Status.OK.equals(status)) {
            imageUrl = hostName + "file/" + uploadResult.getImageId();
        }
        return imageUrl;
    }

    public Resource getImage(String imageId) {

        PictureMetaData pictureMetaData = pictureMetaDataRepository.findOne(imageId);
        if (Objects.isNull(pictureMetaData)) {
            // TODO(asokol): 11/8/17 custom exception
            throw new IllegalStateException("There is no such image");
        }
        Resource resource = fileStorageRepository.readImageFromPath(imageId);
        if (Objects.isNull(resource)) {
            // TODO(asokol): 11/8/17 what to do with meta data?
            throw new IllegalStateException("There is no such image");
        } else {
            pictureMetaData.increaseDownloads();
            pictureMetaDataRepository.save(pictureMetaData);
        }
        return resource;
    }

    public Map<String, FileStatisticDTO> getStatistics(String hostName) {
        return StreamSupport.stream(pictureMetaDataRepository.findAll().spliterator(), false)
                .collect(
                        Collectors.toMap(
                                PictureMetaData::getId,
                                a -> new FileStatisticDTO(genUrl(a.getId(), hostName), a.getDownloads())
                        )
                );
    }

    private String genUrl(String id, String hostName) {
        return hostName + "file/" + id;
    }
}