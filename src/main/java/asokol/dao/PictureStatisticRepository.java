package asokol.dao;

import asokol.dao.entity.PictureMetaData;
import org.springframework.data.repository.CrudRepository;

/**
 * Picture Statistic Repository responsible for saving downloads statistics.
 */
public interface PictureStatisticRepository extends CrudRepository<PictureMetaData, String> {
}
