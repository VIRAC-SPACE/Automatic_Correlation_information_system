package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.GlobalParameters;
@Repository("IGlobalParameterRepo")
public interface IGlobalParameterRepo extends CrudRepository<GlobalParameters, Long>{

	GlobalParameters findByParameter(String string);

}
