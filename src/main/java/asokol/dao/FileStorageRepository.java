package asokol.dao;

import asokol.dto.UploadResultDTO;
import lombok.extern.log4j.Log4j;
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

@Log4j
@Repository
public class FileStorageRepository {

    private static String POSTFIX = ".jpeg";

    private String applicationDir;

    @Autowired
    public FileStorageRepository(@Value("${storage.files:}") String applicationDir) {
        this.applicationDir = Objects.equals(applicationDir, "")
                ? new File(".").getAbsolutePath()
                : applicationDir;
    }


    public UploadResultDTO saveImage(MultipartFile image) {
        File dir = new File(applicationDir);
        String id = null;
        try {
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IllegalStateException("Cannot create a dir for storing pictures");
                }
            }
            id = generateId(image);
            File file = new File(applicationDir + "/" + id + POSTFIX);
            if (file.exists()) {
                log.info("File is already uploaded");
                return new UploadResultDTO(id, UploadResultDTO.Status.ALREADY_EXIST);
            }
            if (!file.createNewFile()) {
                throw new IllegalStateException("Cannot create a file");
            }
            image.transferTo(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new UploadResultDTO(id, UploadResultDTO.Status.OK);
    }

    // TODO(asokol): 11/7/17 catch exception
    public Resource readImageFromPath(String imageId) {
        Path path = Paths.get(applicationDir + "/" + imageId + POSTFIX);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalStateException("Couldn't read a file: " + path);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Couldn't read a file: " + path);
        }
    }

    /**
     * @param image
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
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
