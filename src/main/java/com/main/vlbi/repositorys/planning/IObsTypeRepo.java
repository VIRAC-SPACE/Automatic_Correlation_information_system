package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.ObservationParams;
import com.main.vlbi.models.planning.ObservationType;
@Repository("IObsTypeRepo")
public interface IObsTypeRepo extends CrudRepository<ObservationType, Long>{

	ObservationType findByObservations(ObservationParams obsParams);

	ObservationType findByTypeTitle(String string);

}
