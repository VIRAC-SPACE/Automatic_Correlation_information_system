package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.ObservationParams;
@Repository("IObservationParamsRepo")
public interface IObservationParamsRepo extends CrudRepository<ObservationParams, Long>{

	ObservationParams findByObservations(Observation obs);

}
