package com.main.vlbi.repositorys;

import com.main.vlbi.models.Experiment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("ExperimentRepository")
public interface ExperimentRepository extends CrudRepository<Experiment, Long> {
    Optional<Experiment> findByName(String experimentName);

    Optional<Experiment> findByNameAndType(String experimentName, String experimentType);
}
