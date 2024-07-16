package com.main.vlbi.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CheckAntennaAvailability {

	@Value("${api.script.path}")
	private String apiScriptPath;

	@Value("${google.script.path}")
	private String googleScriptPath;
	
	
	private static String staticApiScriptPath;
	private static String staticGoogleScriptPath;

	
    @PostConstruct
    public void init() {
    	staticGoogleScriptPath = googleScriptPath;
    	staticApiScriptPath = apiScriptPath;
    }
	
	

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	public static boolean getInfoAboutAntennaAvailibilityDefenceAPI(LocalDateTime from, LocalDateTime to,
			String antenna) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("python", staticApiScriptPath, from.format(formatter),
				to.format(formatter), antenna);
		
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);

		}
		return (result.toString().contains("True") || result.toString().contains("true"));
	}

	public static boolean getInfoAboutAntennaAvailibilityGoogleCalendarApi(LocalDateTime from, LocalDateTime to,
			String antenna) throws IOException, InterruptedException {
		List<String> command = new ArrayList<String>();

		command.add("python3");
		command.add(staticGoogleScriptPath);
		command.add(from.format(formatter));
		command.add(to.format(formatter));
		command.add(antenna);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Path path = Paths.get(staticGoogleScriptPath);
		processBuilder.directory(new File(path.getParent().toString()));
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		process.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder result = new StringBuilder();
		String line;
		if (antenna.equals("RT16") || antenna.equals("ib"))
			antenna = "RT-16";
		if (antenna.equals("RT32") || antenna.equals("ir"))
			antenna = "RT-32";
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}

		if (antenna.equals("LOFAR")) {
			if (result.toString().contains("LOFAR Local Mode"))
				return true;
			else
				return false;

		}
		
		return (!result.toString().contains(antenna));

	}

}
