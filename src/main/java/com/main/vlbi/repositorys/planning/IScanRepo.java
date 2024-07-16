package com.main.vlbi.repositorys.planning;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.Scan;
import com.main.vlbi.models.planning.Source;

@Repository("IScanRepo")
public interface IScanRepo extends CrudRepository<Scan, Long> {

	ArrayList<Scan> findByObservations(Observation obs);

	Scan findBySourceAndDurationAndDopsrcAndGap(Source sc, int duration, String dopsrc, int gap);

	ArrayList<Scan> findByObservationsExpcode(String expcode);

}
