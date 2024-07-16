package com.main.vlbi.controllers;

import java.io.File;
import java.io.FileInputStream;
import org.springframework.http.HttpHeaders;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.main.vlbi.models.planning.Project;
import com.main.vlbi.models.planning.Pipeline;
import com.main.vlbi.models.planning.Source;
import com.main.vlbi.models.planning.dto.CheckAntennaDTO;
import com.main.vlbi.models.planning.dto.ObservationDTO;
import com.main.vlbi.models.planning.dto.ObservationTitleAndDateTimeDTO;
import com.main.vlbi.models.planning.dto.ObservationTitleAndDateTimeMultipleDTO;
import com.main.vlbi.services.IObsPlanningService;
import com.main.vlbi.utils.KeyFileDataUtils;
import com.main.vlbi.models.planning.GroupObs;

@RestController
@RequestMapping("/planning")
public class PlanningController {

	@Autowired
	private IObsPlanningService planningService;

	//INFO - only development purposes for testing
	@GetMapping("/download/key/{obs}") // planning/download/key/irib5
	public ResponseEntity<InputStreamResource> downloadKeyFile(@PathVariable String obs) {
		try {
			File file = planningService.createObsKeyFile(obs);
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
					.contentLength(file.length()).body(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/create-using") // planning/create-using
	public ResponseEntity<?> create(@RequestBody ObservationTitleAndDateTimeMultipleDTO obs,
			@RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
		try {
			ArrayList<ObservationTitleAndDateTimeDTO> newObs = planningService
					.copyObservationsWithDifferentDateTime(obs.getObservations(), authorizationHeader);
			if (newObs != null) {
				return new ResponseEntity<ArrayList<ObservationTitleAndDateTimeDTO>>(newObs, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/obs/check-availability")
	public ResponseEntity<?> calculateUtcTime(@RequestBody ObservationTitleAndDateTimeMultipleDTO obs) {
		try {
			ArrayList<ObservationTitleAndDateTimeDTO> observations = planningService
					.calculateUTCtimeForArrayList(obs.getObservations());
			observations = planningService.checkAvailability(observations);
			return new ResponseEntity<ArrayList<ObservationTitleAndDateTimeDTO>>(observations, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/antenna/check-availability")
	public ResponseEntity<?> checkAvailabilityAntenna(@RequestBody CheckAntennaDTO info) {
		try {
			info = planningService.checkAvailabilityAntenna(info);
			return new ResponseEntity<CheckAntennaDTO>(info, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/search/source/{title}") // planning/search/source/title
	public ResponseEntity<?> searchSources(@PathVariable String title) {
		if (title.length() > 1)
			return new ResponseEntity<ArrayList<Source>>(planningService.retrieveSourcesLike(title), HttpStatus.OK);
		else
			return new ResponseEntity<String>("Lenght must be at least 1 symbol", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@PostMapping("/new-source") // planning/new-source
	public ResponseEntity<String> createNewSource(@RequestBody Source source) {

		try {
			planningService.saveSource(source);
			return new ResponseEntity<String>(HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/read/key", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<KeyFileDataUtils> readFromKeyFile(@RequestParam("file") MultipartFile file,
			@RequestParam("projects") String projects, @RequestHeader HttpHeaders headers2) {
		try {
			String authorizationHeader = headers2.getFirst(HttpHeaders.AUTHORIZATION);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			byte[] fileBytes = file.getBytes();
			KeyFileDataUtils tempData = planningService.readObsKeyFile(fileBytes, projects);
			planningService.saveKeyFileDataInDB(tempData, authorizationHeader);
			return new ResponseEntity<>(tempData, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/observation") // /planning/observation
	public ResponseEntity<?> retrieveObservationsByUser(@RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
		try {
			return new ResponseEntity<ArrayList<ObservationDTO>>(
					planningService.retrieveObservationsByUsername(authorizationHeader), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/projects") // /planning/projects
	public ResponseEntity<?> retrieveProjects() {
		try {
			return new ResponseEntity<ArrayList<Project>>(planningService.getAllProjects(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/pipelines") // /planning/pipelines
	public ResponseEntity<?> retrievePipelines() {
		try {
			return new ResponseEntity<ArrayList<Pipeline>>(planningService.getAllPipelines(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/groups") // /planning/groups
	public ResponseEntity<?> retrieveGroups() {
		try {
			return new ResponseEntity<ArrayList<GroupObs>>(planningService.getAllGroups(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
