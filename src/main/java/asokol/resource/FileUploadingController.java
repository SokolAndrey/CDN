package asokol.resource;

import asokol.dto.ImageStatisticDTO;
import asokol.dto.UrlDTO;
import asokol.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * REST controller for storing and getting images and for getting statistics.
 */
@RestController
public class FileUploadingController {

    private final ImageService imageService;

    @Autowired
    public FileUploadingController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * POST method for uploading an image {@code file}.
     * <p>
     * Responses:
     * - 201=CREATED=   - in case of successful uploading.
     * - 404=NOT FOUND= - in case of unsuccessful uploading.
     *
     * @param request http request.
     * @param file    incoming file.
     * @return URL where the uploaded file is available.
     */
    @PostMapping("file")
    public ResponseEntity<UrlDTO> uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws URISyntaxException {
        String hostName = fetchHost(request, "file");
        String url = imageService.saveImage(file, hostName);
        URI uri = new URI(url);
        return ResponseEntity.created(uri)
                .body(new UrlDTO(url));

    }

    /**
     * GET method for retrieving an image by its ID.
     * <p>
     * Responses:
     * - 200=OK=        - in case of successful response.
     * - 404=NOT FOUND= - in case the image with given ID is not found.
     *
     * @param imageId the ID of the image.
     * @return requested image.
     */
    @GetMapping("file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("id") String imageId) {
        Resource file = imageService.getImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(file);
    }

    /**
     * GET method for retrieving statistics about existed images.
     *
     * @param request http request.
     * @return download statistics.
     */
    @GetMapping("statistics")
    public Map<String, ImageStatisticDTO> getStatistics(HttpServletRequest request) {
        String hostName = fetchHost(request, "statistics");
        return imageService.getStatistics(hostName);
    }

    private static String fetchHost(HttpServletRequest request, String leftover) {
        StringBuffer requestURL = request.getRequestURL();
        int statisticsIndex = requestURL.indexOf(leftover);
        return requestURL.substring(0, statisticsIndex);
    }

}
