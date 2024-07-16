package com.main.vlbi.controllers;

import com.main.vlbi.services.implementations.ObsPlanningServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.main.vlbi.models.planning.dto.DataProcessingDTO;
import com.main.vlbi.services.IDataProcessingService;

@RestController
@RequestMapping("/data-processing")
public class DataProcessingController {

	@Autowired
	private IDataProcessingService dataProcessingService;

	@Autowired
	private ObsPlanningServiceImpl obsPlanningServiceImpl;
	
	
	@PostMapping("/save") // data-processing/save
	public ResponseEntity<?> create(@RequestBody DataProcessingDTO dataProcessingDTO, @RequestHeader HttpHeaders headers) {
		String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
		try {
			dataProcessingService.saveDataProcessingParams(authorizationHeader, dataProcessingDTO.getPassType(), dataProcessingDTO.getPipelines(), dataProcessingDTO.getExpCodes());
				return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/savedataprocessingresults")
	public ResponseEntity<?> saveDataProcessingResults(@RequestParam String expcode, @RequestParam int check) {
        try {
            obsPlanningServiceImpl.addCheckToObservation(expcode, check);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        	return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

