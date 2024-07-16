package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.ObservationMode;
import com.main.vlbi.models.planning.ObservationParams;
@Repository("IObservationModeRepo")
public interface IObservationModeRepo extends CrudRepository<ObservationMode, Long>{

	ObservationMode findByObservations(ObservationParams obsParams);

	ObservationMode findByPolAndNetSide(String string, String string2);

}
