package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.Project;
@Repository("IProjectRepo")
public interface IProjectRepo extends CrudRepository<Project, Long>{

	Project findByTitle(String string);

}
