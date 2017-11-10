package asokol.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

/**
 * DTO for result of the uploading.
 */
@Value
public class UploadResultDTO {
    String imageId;
    @JsonIgnore
    Status status;

    public enum Status {
        OK, FUCKED_UP, ALREADY_EXIST
    }
}
