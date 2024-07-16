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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

public class ActionsWithGoogleCalendar {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	@Value("${google.event.script.path}")
	private String googleEventScriptPath;

	
	private static String staticGoogleEventScriptPath;

	
    @PostConstruct
    public void init() {
    	staticGoogleEventScriptPath = googleEventScriptPath;
    }
	
	
	public static boolean createEventInGoogleCalendar(LocalDateTime from, LocalDateTime to, String message)
			throws IOException, InterruptedException {
		List<String> command = new ArrayList<String>();
		command.add("python3");
		command.add(staticGoogleEventScriptPath);
		command.add(from.format(formatter));
		command.add(to.format(formatter));
		command.add(message);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Path path = Paths.get(staticGoogleEventScriptPath);
		processBuilder.directory(new File(path.getParent().toString()));
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		process.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder result = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}

		return (result.toString().contains(message));

	}
}
