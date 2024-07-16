package com.main.vlbi.repositorys.planning;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.ObservationParams;
import com.main.vlbi.models.planning.Station;
import com.main.vlbi.models.planning.StationParameters;
@Repository("IStationParametersRepo")
public interface IStationParametersRepo extends CrudRepository<StationParameters, Long>{

	ArrayList<StationParameters> findByObservations(ObservationParams obsParams);

	StationParameters findByRecordingFormatAndFirstLoAndIfChanAndBbcAndStation(String string, String string2,
			String string3, String string4, Station tempSt);

}
