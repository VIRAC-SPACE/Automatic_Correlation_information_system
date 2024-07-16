package com.main.vlbi.services;

import java.io.FileNotFoundException;
import java.util.HashMap;

import com.main.vlbi.models.planning.Source;

public interface IImportSourceDataService {
	void importSourceDataFromConfFiles(String filename)  throws FileNotFoundException;
	void saveSourceInDB(Source source);
	void importSourceDataFromExcel(String filename)  throws FileNotFoundException;
	HashMap<String, String> getInfoAboutSourceFromSimbad(String sourceTitle);
	
	
}
