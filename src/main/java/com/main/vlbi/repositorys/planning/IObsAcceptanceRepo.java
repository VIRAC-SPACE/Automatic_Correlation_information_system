package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.ObsAcceptance;
@Repository("IObsAcceptanceRepo")
public interface IObsAcceptanceRepo extends CrudRepository<ObsAcceptance, Long> {

	boolean existsByObservationExpcodeAndAccept1AndAccept2AndAccept3AndAccept4(String expcode, boolean b, boolean c,
			boolean d, boolean e);

	ObsAcceptance findByObservationExpcode(String expcode);

}
