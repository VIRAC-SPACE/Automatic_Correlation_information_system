package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.CorrelationType;
import com.main.vlbi.models.planning.CorrelatorInfo;
import com.main.vlbi.models.planning.Observation;

@Repository("ICorrInfoRepo")
public interface ICorrInfoRepo extends CrudRepository<CorrelatorInfo, Long>{

	CorrelatorInfo findByObservations(Observation obs);

	CorrelatorInfo findByCoravgAndCorchanAndCornantAndCorpolAndCorwtfnAndCorsrcsAndCortapeAndCorrType(float parseFloat,
			int parseInt, int parseInt2, boolean corpol, String string, String string2, String string3,
			CorrelationType corrT1);

}
