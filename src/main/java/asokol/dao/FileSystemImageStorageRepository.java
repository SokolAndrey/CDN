package asokol.dao;

import asokol.dao.exception.DirCreationException;
import asokol.dao.exception.FileCreationException;
import asokol.dao.exception.FileReadingException;
import asokol.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * File System Image Storage responsible for saving and retrieving images into/from file system.
 */
@Slf4j
@Repository
public class FileSystemImageStorageRepository implements ImageStorageRepository {

    private static String POSTFIX = ".jpeg";

    private String applicationDir;

    @Autowired
    public FileSystemImageStorageRepository(@Value("${storage.files:}") String applicationDir) {
        this.applicationDir = Objects.equals(applicationDir, "")
                ? new File(".").getAbsolutePath()
                : new File(applicationDir).getAbsolutePath();
    }

    /**
     * Saves an image {@code image} into file system.
     *
     * @param image input image.
     * @return the result of the uploading an image. See {@link UploadResultDTO} for details.
     * @throws DirCreationException  when the directory cannot be created.
     * @throws FileCreationException when the file cannot be created or written.
     */
    public UploadResultDTO saveImage(MultipartFile image) {
        File dir = new File(applicationDir);
        String id = null;
        try {
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    log.error("Directory {} cannot be created", dir.toString());
                    throw new DirCreationException("Cannot create a dir for storing pictures");
                }
            }
            id = generateId(image);
            File file = new File(applicationDir + "/" + id + POSTFIX);
            if (file.exists()) {
                log.info("File with id: {} is already exist", id);
                return new UploadResultDTO(id, UploadResultDTO.Status.ALREADY_EXIST);
            }
            if (!file.createNewFile()) {
                log.error("File {} cannot be created", id);
                throw new FileCreationException("Cannot create a file");
            }
            image.transferTo(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Cannot write into file with id {}", id);
            throw new FileCreationException("Cannot write into a file");
        }
        return new UploadResultDTO(id, UploadResultDTO.Status.OK);
    }

    /**
     * Gets an image by its ID.
     *
     * @param imageId image ID.
     * @return requested image.
     */
    public Resource getImage(String imageId) {
        Path path = Paths.get(applicationDir + "/" + imageId + POSTFIX);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                log.error("Couldn't read a file: {}", path);
                throw new FileReadingException("Couldn't read a file: " + path);
            }
        } catch (MalformedURLException e) {
            log.error("Couldn't read a file: {}", path);
            throw new FileReadingException("Couldn't read a file: " + path);
        }
    }

    /**
     * Generates image id based on its hash.
     *
     * @param image input image.
     * @return generated ID.
     */
    private String generateId(MultipartFile image) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(image.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
