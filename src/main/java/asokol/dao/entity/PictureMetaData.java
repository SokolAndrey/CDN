package asokol.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Representation of table for storing downloads statistics.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PictureMetaData implements Serializable {

    private static final long serialVersionUID = 2117290118273513845L;

    @Id
    private String id;

    @Column(nullable = false)
    private Long downloads = 0L;

    public void increaseDownloads() {
        downloads++;
    }
}
