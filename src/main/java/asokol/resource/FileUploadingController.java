package asokol.resource;

import asokol.dto.FileStatisticDTO;
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
import java.util.Map;

@RestController
public class FileUploadingController {

    private final ImageService imageService;

    @Autowired
    public FileUploadingController(ImageService imageService) {
        this.imageService = imageService;
    }

    // TODO(asokol): 11/7/17 201 response
    @PostMapping("file")
    public UrlDTO uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String hostName = fetchHost(request, "file");
        String url = imageService.saveImage(file, hostName);
        return new UrlDTO(url);
    }

    // TODO(asokol): 11/9/17 code response
    @GetMapping("file/{id}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("id") String imageId) {
        Resource file = imageService.getImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(file);
    }

    // TODO(asokol): 11/9/17 code response
    @GetMapping("statistics")
    public Map<String, FileStatisticDTO> getStatistics(HttpServletRequest request) {
        String hostName = fetchHost(request, "statistics");
        return imageService.getStatistics(hostName);
    }

    private static String fetchHost(HttpServletRequest request, String leftover) {
        StringBuffer requestURL = request.getRequestURL();
        int statisticsIndex = requestURL.indexOf(leftover);
        return requestURL.substring(0, statisticsIndex);
    }

}
