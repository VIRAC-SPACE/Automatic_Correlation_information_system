package com.main.vlbi.repositorys.planning;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.User;
import com.main.vlbi.models.planning.Observation;
@Repository("IObservationRepo")
public interface IObservationRepo extends CrudRepository<Observation, Long> {

	Observation findByExpcode(String obsTile);

	int countByUsers(User user);

	Observation findByExpcodeAndDateTimeLST(String string, LocalDateTime datetime);

	ArrayList<Observation> findByUsersEmail(String username);

	@Query(nativeQuery = true, value = "select * from observation where date_timeutc between \"(?1)\" and \"(?2)\";")
	ArrayList<Observation> findByDateTimeUTCBetween(LocalDateTime lastMonday0830, LocalDateTime thisMonday0830);

	ArrayList<Observation> findByRepeatingAndDateTimeUTCBetween(boolean b, LocalDateTime thisMonday0830,
			LocalDateTime lastMonday0830);

}
