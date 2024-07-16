package com.main.vlbi.repositorys.planning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.main.vlbi.models.planning.Pipeline;
@Repository("IPipelineRepo")
public interface IPipelineRepo extends CrudRepository<Pipeline, Long> {

}
