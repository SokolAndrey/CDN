package asokol.dto;

import lombok.Value;

/**`
 * DTO for image statistic.
 */
@Value
public class ImageStatisticDTO {
    String url;
    Long downloads;
}
