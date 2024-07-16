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

public class PySchedCalling {

	@Value("${pysched.script.path}")
	private String pyschedRemoteScriptPath;
	
	
	private static String staticPyschedRemoteScriptPath;
	
    @PostConstruct
    public void init() {
    	staticPyschedRemoteScriptPath = pyschedRemoteScriptPath;
    }

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static boolean createvexFileUsingPySched(String expcode, LocalDateTime date, String project)
			throws IOException, InterruptedException {
		List<String> command = new ArrayList<String>();
		command.add("python3");
		command.add(staticPyschedRemoteScriptPath);
		command.add(expcode);
		command.add(date.format(formatter));
		command.add(project);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Path path = Paths.get(staticPyschedRemoteScriptPath);
		processBuilder.directory(new File(path.getParent().toString()));
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		process.waitFor();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}

		return (result.toString().contains("100%"));

	}

}
