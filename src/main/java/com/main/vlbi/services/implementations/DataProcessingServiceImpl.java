package com.main.vlbi.services.implementations;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.vlbi.models.planning.DataProcessing;
import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.ObservationVIRACMode;
import com.main.vlbi.models.planning.Pipeline;
import com.main.vlbi.repositorys.UserRepository;
import com.main.vlbi.repositorys.planning.IDataProcessingRepo;
import com.main.vlbi.repositorys.planning.IObservationRepo;
import com.main.vlbi.repositorys.planning.IObservationVIRACModeRepo;
import com.main.vlbi.repositorys.planning.IPipelineRepo;
import com.main.vlbi.services.IDataProcessingService;

@Service("DataProcessingServiceImpl")
public class DataProcessingServiceImpl implements IDataProcessingService {

	@Autowired
	private IDataProcessingRepo dataProcRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private IPipelineRepo pipelineRepo;

	@Autowired
	private IObservationVIRACModeRepo viracModeRepo;

	@Autowired
	private IObservationRepo obsRepo;
	
	

	@Override
	public void saveDataProcessingParams(String authorizationHeader, String passType, ArrayList<Pipeline> pipelines,
			ArrayList<String> expCodes) throws Exception {

		if (userRepo.findByEmail(authorizationHeader) != null) {
			ArrayList<ObservationVIRACMode> modes = new ArrayList<>();
			if (passType.equals("Line")) {
				ObservationVIRACMode viracParamDB = viracModeRepo.findByModeTitle("line");
				if (viracParamDB != null)
					modes.add(viracParamDB);
			} else if (passType.equals("Continum")) {
				ObservationVIRACMode viracParamDB = viracModeRepo.findByModeTitle("continum");
				if (viracParamDB != null)
					modes.add(viracParamDB);
			} else if (passType.equals("All")) {
				ObservationVIRACMode viracParamDB = viracModeRepo.findByModeTitle("all");
				if (viracParamDB != null)
					modes.add(viracParamDB);
			} else if (passType.equals("Contiuum_Line")) {
				ObservationVIRACMode viracParamDB1 = viracModeRepo.findByModeTitle("line");
				if (viracParamDB1 != null)
					modes.add(viracParamDB1);
				ObservationVIRACMode viracParamDB2 = viracModeRepo.findByModeTitle("continum");
				if (viracParamDB2 != null)
					modes.add(viracParamDB2);
			} else
				throw new Exception("Problems with passType");

			ArrayList<Pipeline> involvedPipelines = new ArrayList<>();

			for (Pipeline temp : pipelines) {
				Pipeline pipelineDB = pipelineRepo.findById(temp.getPipelineId()).get();
				if (pipelineDB != null) {
					involvedPipelines.add(pipelineDB);
				}
			}

			ArrayList<Observation> observationsFor = new ArrayList<>();

			for (String temp : expCodes) {
				Observation obsDB = obsRepo.findByExpcode(temp);
				if (obsDB != null) {
					observationsFor.add(obsDB);
				}
			}

			for (ObservationVIRACMode temp : modes) {
				DataProcessing dataProcObj = new DataProcessing(authorizationHeader, temp, observationsFor, pipelines);
				dataProcRepo.save(dataProcObj);
			}

		} else
			throw new Exception("Incorrect username");

	}

}
