package com.main.vlbi.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.main.vlbi.models.planning.Project;
import com.main.vlbi.models.planning.GroupObs;
import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.repositorys.planning.IGroupObsRepo;
import com.main.vlbi.repositorys.planning.IObservationRepo;
import com.main.vlbi.repositorys.planning.IProjectRepo;
import com.main.vlbi.services.implementations.ObsPlanningServiceImpl;

@Component
public class WeeklyEventSchedulerPlanning {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	@Value("${google.color.script.path}")
	private String googleColorScriptPath;

	private static String staticGoogleColorScriptPath;

	@PostConstruct
	public void init() {
		staticGoogleColorScriptPath = googleColorScriptPath;
	}

	@Autowired
	private ObsPlanningServiceImpl planningService;

	@Autowired
	private IObservationRepo obsRepo;

	@Autowired
	private IProjectRepo projectRepo;

	@Autowired
	private IGroupObsRepo groupObsRepo;

	private static LocalTime eightThirty = LocalTime.of(8, 30);

	// example for 2 minutes --> @Scheduled(fixedRate = 120000)
	// TODO Uncomment this line for production release:
	/* @Scheduled(cron = "0 30 8 ? * MON") */
	public void myScheduledFunction() throws Exception {
		try {

			getThisWeekObservationsStatus();
			rescheduleThisWeekUnsuccesfulObservations();
			rescheduleThisWeekObservationsAutomatically();

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public void rescheduleThisWeekObservationsAutomatically() throws Exception {
		LocalDateTime thisMonday0830 = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.with(eightThirty);

		ArrayList<GroupObs> groups = groupObsRepo.getAllGroupsByAscendingCounter();

		for (GroupObs tempGr : groups) {
			boolean isDateTimeFound = false;
			LocalDateTime nextDay = (thisMonday0830.with(TemporalAdjusters.next(DayOfWeek.TUESDAY))).with(LocalTime
					.of(tempGr.getLstime().getHour(), tempGr.getLstime().getMinute(), tempGr.getLstime().getSecond()));
			int howManyDays = 0;
			do {

				LocalDateTime localDatetimeUTCStart = planningService.calculateUTCtimeFromLST(nextDay);
				float durationInHours = tempGr.getDuration();
				long hours = (long) Math.floor(durationInHours);
				long minutes = Math.round((durationInHours - hours) * 60);
				LocalDateTime localDatetimeUTCEnd = localDatetimeUTCStart.plusHours(hours).plusMinutes(minutes);
				if (planningService.isAntenaAvailableFromDefenceMinistry(localDatetimeUTCStart, localDatetimeUTCEnd,
						"RT16")
						&& planningService.isAntenaAvailableFromDefenceMinistry(localDatetimeUTCStart,
								localDatetimeUTCEnd, "RT32")
						&& planningService.isAntenaAvailableFromGoogleCalendar(localDatetimeUTCStart,
								localDatetimeUTCEnd, "RT16")
						&& planningService.isAntenaAvailableFromGoogleCalendar(localDatetimeUTCStart,
								localDatetimeUTCEnd, "RT32")) {

					Project ivarsProject = projectRepo.findByTitle("IVARS");
					ArrayList<Project> projects = new ArrayList<>(Arrays.asList(ivarsProject));
					Observation newObs = planningService.copyObservationWithDifferentDateTime(
							String.format("%s%03d", tempGr.getGroupObsTitle(), tempGr.getCounter()), nextDay,
							"admin@admin.lv", projects);
					newObs.setRepeating(false);
					obsRepo.save(newObs);

					// TODO Uncomment these lines for production release:
					/*
					ActionsWithGoogleCalendar.createEventInGoogleCalendar(localDatetimeUTCStart,
					localDatetimeUTCEnd, "RT-16 " + "IVARS" + " " + newObs.getExpcode() + "(C band)");
				    ActionsWithGoogleCalendar.createEventInGoogleCalendar(localDatetimeUTCStart,
					localDatetimeUTCEnd, "RT-32 " + "IVARS" + " " + newObs.getExpcode() + "(C band)");
					*/
					isDateTimeFound = true;
				}

				nextDay = nextDay.plusDays(1);
				howManyDays++;
			} while (!isDateTimeFound && howManyDays <= 6);

		}
	}

	public void rescheduleThisWeekUnsuccesfulObservations() throws Exception {
		LocalDateTime thisMonday0830 = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.with(eightThirty);
		LocalDateTime lastMonday0830 = thisMonday0830.minusDays(7);

		ArrayList<Observation> observationsInThisWeek = obsRepo.findByDateTimeUTCBetween(lastMonday0830,
				thisMonday0830);

		for (Observation obs : observationsInThisWeek) {
			// TODO can be upgraded with retrieving info from obs_acceptance DB table 
			if (obs.isRepeating()) {
				boolean isDateTimeFound = false;
				LocalDateTime nextDay = (thisMonday0830.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)))
						.with(LocalTime.of(obs.getDateTimeLST().getHour(), obs.getDateTimeLST().getMinute(),
								obs.getDateTimeLST().getSecond()));
				int howManyDays = 0;
				do {

					LocalDateTime localDatetimeUTCStart = planningService.calculateUTCtimeFromLST(nextDay);
					float durationInHours = obs.getDurationInHours();
					long hours = (long) Math.floor(durationInHours);
					long minutes = Math.round((durationInHours - hours) * 60);
					LocalDateTime localDatetimeUTCEnd = localDatetimeUTCStart.plusHours(hours).plusMinutes(minutes);
					if (planningService.isAntenaAvailableFromDefenceMinistry(localDatetimeUTCStart, localDatetimeUTCEnd,
							"RT16")
							&& planningService.isAntenaAvailableFromDefenceMinistry(localDatetimeUTCStart,
									localDatetimeUTCEnd, "RT32")
							&& planningService.isAntenaAvailableFromGoogleCalendar(localDatetimeUTCStart,
									localDatetimeUTCEnd, "RT16")
							&& planningService.isAntenaAvailableFromGoogleCalendar(localDatetimeUTCStart,
									localDatetimeUTCEnd, "RT32")) {
						ArrayList<Project> projects = new ArrayList<>();
						for (Project tempP : obs.getProjects()) {
							projects.add(tempP);
						}

						Observation newObs = planningService.copyObservationWithDifferentDateTime(obs.getExpcode(),
								nextDay, "admin@admin.lv", projects);
						obs.setRepeating(false);
						obsRepo.save(obs);
						newObs.setRepeating(false);
						obsRepo.save(newObs);

						// TODO Uncomment these lines for production release:
						/* 
						 ActionsWithGoogleCalendar.createEventInGoogleCalendar(localDatetimeUTCStart,
						 localDatetimeUTCEnd, "RT-16 " + "IVARS" + " " + newObs.getExpcode() + "(C band)");
						 ActionsWithGoogleCalendar.createEventInGoogleCalendar(localDatetimeUTCStart,
						 localDatetimeUTCEnd, "RT-32 " + "IVARS" + " " + newObs.getExpcode() + "(C band)");
						*/
						isDateTimeFound = true;
					}

					nextDay = nextDay.plusDays(1);
					howManyDays++;
				} while (!isDateTimeFound && howManyDays <= 6);

			}
		}

	}

	public void getThisWeekObservationsStatus() throws IOException, InterruptedException {
		LocalDateTime thisMonday0830 = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.with(eightThirty);
		;
		LocalDateTime lastMonday0830 = thisMonday0830.minusDays(7);
		ArrayList<Observation> observationsInThisWeek = obsRepo.findByDateTimeUTCBetween(lastMonday0830,
				thisMonday0830);
		for (Observation obs : observationsInThisWeek) {
			List<String> command = new ArrayList<String>();
			command.add("python3");
			command.add(staticGoogleColorScriptPath);
			command.add(lastMonday0830.format(formatter));
			command.add(thisMonday0830.format(formatter));
			command.add(obs.getExpcode());
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Path path = Paths.get(googleColorScriptPath);
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
			if (result.toString().contains("true")) {
				obs.setRepeating(true);
				obsRepo.save(obs);
			} else {
				obs.setRepeating(false);
				obsRepo.save(obs);
			}
		}
	}

}
