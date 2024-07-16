package com.main.vlbi.repositorys.planning;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.GroupObs;
@Repository("IGroupObsRepo")
public interface IGroupObsRepo extends CrudRepository<GroupObs, Long>{

	GroupObs findByGroupObsTitle(String groupTitle);

	@Query(nativeQuery = true, value = "select * from group_obs order by counter;")
	ArrayList<GroupObs> getAllGroupsByAscendingCounter();

}

