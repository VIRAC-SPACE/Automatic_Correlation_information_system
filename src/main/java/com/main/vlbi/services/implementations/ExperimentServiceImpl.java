package com.main.vlbi.services.implementations;

import com.main.vlbi.models.Experiment;
import com.main.vlbi.repositorys.ExperimentRepository;
import com.main.vlbi.services.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service("ExperimentService")
@Transactional
public class ExperimentServiceImpl implements ExperimentService {
    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    public ExperimentServiceImpl(ExperimentRepository experimentRepository) {
        this.experimentRepository = experimentRepository;
    }

    @Override
    public Optional<Experiment> getExperimentById(long id) {
        return experimentRepository.findById(id);
    }

    @Override
    public Iterable<Experiment> getAllExperiments() {
        return experimentRepository.findAll();
    }

    public void createSFXC(String experimentName, String date, String corrType) {
        Experiment experiment = new Experiment();
        experiment.setName(experimentName);
        experiment.setExperiment_date(date);
        experiment.setSFXC();
        experiment.setCorr_type(corrType);
        experimentRepository.save(experiment);
    }

    public void createKANA(String experimentName, String date) {
        Experiment experiment = new Experiment();
        experiment.setName(experimentName);
        experiment.setExperiment_date(date);
        experiment.setKANA();
        experimentRepository.save(experiment);
    }

    @Override
    public void Delete(long id) {
        try{
            Optional<Experiment> experiment_ = getExperimentById(id);
            Experiment experiment = experiment_.get();
            experimentRepository.delete(experiment);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Experiment> getExperimentByName(String ExperimentName) {
        return experimentRepository.findByName(ExperimentName);
    }

    @Override
    public Optional<Experiment> getExperimentByNameAndType(String ExperimentName, String Experiment_Type) {
        return experimentRepository.findByNameAndType(ExperimentName, Experiment_Type);
    }

}
