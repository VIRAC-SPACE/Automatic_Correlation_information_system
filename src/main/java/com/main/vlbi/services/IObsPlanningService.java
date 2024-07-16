package com.main.vlbi.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.main.vlbi.models.User;
import com.main.vlbi.models.planning.CorrelatorInfo;
import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.ObservationParams;
import com.main.vlbi.models.planning.Project;
import com.main.vlbi.models.planning.Pipeline;
import com.main.vlbi.models.planning.Source;
import com.main.vlbi.models.planning.dto.CheckAntennaDTO;
import com.main.vlbi.models.planning.dto.ObservationDTO;
import com.main.vlbi.models.planning.dto.ObservationTitleAndDateTimeDTO;
import com.main.vlbi.utils.KeyFileDataUtils;
import com.main.vlbi.models.planning.GroupObs;
public interface IObsPlanningService  {
	File createObsKeyFile(String obsTile)  throws IOException;
	
	KeyFileDataUtils readObsKeyFile(String keyFileLocation, String projectsIds)  throws IOException;
	
	void createObservation(String title, ArrayList<User> users, LocalDateTime  dateAndStartTime, ObservationParams obsParams, CorrelatorInfo corrInfo, ArrayList<Source> sources ) throws Exception;

	Observation copyObservationWithDifferentDateTime(String title, LocalDateTime datetime, String username, ArrayList<Project> projects) throws Exception;
	
	ArrayList<ObservationTitleAndDateTimeDTO> copyObservationsWithDifferentDateTime(ArrayList<ObservationTitleAndDateTimeDTO> obs,  String username) throws Exception;
	
	ArrayList<Source> retrieveSourcesLike(String title);
	
	void saveSource(Source source) throws Exception;
	
	void saveKeyFileDataInDB(KeyFileDataUtils data, String usernameWhoUploaded) throws Exception;
	
	File createObsVexFile(String obsTitle) throws IOException;
	
	boolean isAntenaAvailableFromDefenceMinistry(LocalDateTime from, LocalDateTime to, String antenna) throws IOException, InterruptedException;
	
	ArrayList<ObservationDTO> retrieveObservationsByUsername(String username) throws Exception;
	
	ArrayList<ObservationTitleAndDateTimeDTO> calculateUTCtimeForArrayList(ArrayList<ObservationTitleAndDateTimeDTO> observations) throws Exception ;
	
	LocalDateTime calculateUTCtimeFromLST(LocalDateTime lstTime) throws Exception;
	
	boolean isAntenaAvailableFromGoogleCalendar(LocalDateTime from, LocalDateTime to, String antenna) throws IOException, InterruptedException;
	
	ArrayList<ObservationTitleAndDateTimeDTO> checkAvailability(ArrayList<ObservationTitleAndDateTimeDTO> observations) throws Exception;
	
	CheckAntennaDTO checkAvailabilityAntenna(CheckAntennaDTO info) throws Exception;

	KeyFileDataUtils readObsKeyFile(byte[] fileBytes, String projectsIds) throws IOException;
		
	boolean isNeedToRepeat(String expcode) throws Exception;
	
	void addCheckToObservation(String expcode, int check) throws Exception;
	
	ArrayList<Project> getAllProjects();
	
	ArrayList<Pipeline> getAllPipelines();

	ArrayList<GroupObs> getAllGroups();
	
}
