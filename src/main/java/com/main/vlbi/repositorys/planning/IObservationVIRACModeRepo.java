package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.ObservationVIRACMode;
@Repository("IObservationVIRACModeRepo")
public interface IObservationVIRACModeRepo extends CrudRepository<ObservationVIRACMode, Long> {
	ObservationVIRACMode findByModeTitle(String string);
}
