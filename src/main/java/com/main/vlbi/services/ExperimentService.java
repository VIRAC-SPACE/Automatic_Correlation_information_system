package com.main.vlbi.services;


import com.main.vlbi.models.Experiment;

import java.util.Optional;

public interface ExperimentService {
    Optional<Experiment> getExperimentById(long id);

    Iterable<Experiment> getAllExperiments();

    void Delete(long id);

    Optional<Experiment> getExperimentByName(String ExperimentName);

    void createSFXC(String experimentName, String date, String corrType);

    void createKANA(String experimentName, String date);

    Optional<Experiment> getExperimentByNameAndType(String ExperimentName, String Experiment_Type);
}
