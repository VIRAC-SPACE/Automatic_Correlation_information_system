package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.Station;
import com.main.vlbi.models.planning.StationParameters;
@Repository("IStationRepo")
public interface IStationRepo extends CrudRepository<Station, Long> {

	Station findByParameters(StationParameters statP);

	Station findByLongTitle(String temp);

	Station findByShortTitle(String lowerCase);

}
