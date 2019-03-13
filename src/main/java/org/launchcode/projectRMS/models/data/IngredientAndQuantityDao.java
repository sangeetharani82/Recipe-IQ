package org.launchcode.projectRMS.models.data;

import org.launchcode.projectRMS.models.IngredientAndQuantity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IngredientAndQuantityDao extends CrudRepository<IngredientAndQuantity, Integer> {
}
