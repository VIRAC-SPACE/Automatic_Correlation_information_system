package com.main.vlbi.services;

import java.util.ArrayList;

import com.main.vlbi.models.planning.Pipeline;

public interface IDataProcessingService {
	
	void saveDataProcessingParams(String authorizationHeader, String passType, ArrayList<Pipeline> pipelines,
			ArrayList<String> expCodes)throws Exception;

}

