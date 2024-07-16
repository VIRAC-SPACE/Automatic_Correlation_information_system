package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.CorrelationType;
import com.main.vlbi.models.planning.CorrelatorInfo;
@Repository("ICorrelationType")
public interface ICorrelationType extends CrudRepository<CorrelationType, Long> {

	CorrelationType findByCorrInfo(CorrelatorInfo corrInfo);

	CorrelationType findByTypeTitle(String string);

}
