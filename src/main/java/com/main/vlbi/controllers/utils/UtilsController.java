package com.main.vlbi.controllers.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.main.vlbi.services.IImportSourceDataService;

@RestController
@RequestMapping("/utils")
public class UtilsController {

	@Autowired
	private IImportSourceDataService importSourceRepo;

	@PostMapping("/import/conf")
	public ResponseEntity<?> importSourceFromConfFiles(@RequestParam("files") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select at least one file to upload", HttpStatus.BAD_REQUEST);
		}

		try {

			String uploadDir = "tempUploads/";
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			if (!file.isEmpty()) {
				String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(file.getInputStream(), filePath);
				importSourceRepo.importSourceDataFromConfFiles(filePath.toString());
			}

			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/import/excel")
	public ResponseEntity<?> importSourceFromExcel(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return new ResponseEntity<String>("Please select a file to upload", HttpStatus.BAD_REQUEST);
		}

		try {
			String tempDir = "tempUploads/";
			Path tempPath = Paths.get(tempDir);

			if (!Files.exists(tempPath)) {
				Files.createDirectories(tempPath);
			}

			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Path tempFilePath = tempPath.resolve(fileName);

			Files.copy(file.getInputStream(), tempFilePath);

			importSourceRepo.importSourceDataFromExcel(tempFilePath.toString());

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
