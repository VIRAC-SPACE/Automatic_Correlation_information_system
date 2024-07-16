package com.main.vlbi.services.implementations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.vlbi.helpers.ActionsWithGoogleCalendar;
import com.main.vlbi.helpers.CheckAntennaAvailability;
import com.main.vlbi.helpers.PySchedCalling;
import com.main.vlbi.models.User;
import com.main.vlbi.models.planning.CorrelationType;
import com.main.vlbi.models.planning.CorrelatorInfo;
import com.main.vlbi.models.planning.Equinox;
import com.main.vlbi.models.planning.GlobalParameters;
import com.main.vlbi.models.planning.ObsAcceptance;
import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.ObservationMode;
import com.main.vlbi.models.planning.ObservationParams;
import com.main.vlbi.models.planning.ObservationType;
import com.main.vlbi.models.planning.Project;
import com.main.vlbi.models.planning.Scan;
import com.main.vlbi.models.planning.Source;
import com.main.vlbi.models.planning.Station;
import com.main.vlbi.models.planning.StationParameters;
import com.main.vlbi.models.planning.UserInfo;
import com.main.vlbi.models.planning.Pipeline;
import com.main.vlbi.models.planning.dto.CheckAntennaDTO;
import com.main.vlbi.models.planning.dto.ObservationDTO;
import com.main.vlbi.models.planning.dto.ObservationTitleAndDateTimeDTO;
import com.main.vlbi.repositorys.UserRepository;
import com.main.vlbi.repositorys.planning.ICorrInfoRepo;
import com.main.vlbi.repositorys.planning.ICorrelationType;
import com.main.vlbi.repositorys.planning.IGlobalParameterRepo;
import com.main.vlbi.repositorys.planning.IObsAcceptanceRepo;
import com.main.vlbi.repositorys.planning.IObsTypeRepo;
import com.main.vlbi.repositorys.planning.IObservationModeRepo;
import com.main.vlbi.repositorys.planning.IObservationParamsRepo;
import com.main.vlbi.repositorys.planning.IObservationRepo;
import com.main.vlbi.repositorys.planning.IProjectRepo;
import com.main.vlbi.repositorys.planning.IScanRepo;
import com.main.vlbi.repositorys.planning.ISourceRepo;
import com.main.vlbi.repositorys.planning.IStationParametersRepo;
import com.main.vlbi.repositorys.planning.IStationRepo;
import com.main.vlbi.repositorys.planning.IUserInfoRepo;
import com.main.vlbi.repositorys.planning.IPipelineRepo;
import com.main.vlbi.services.ICalculateUTCService;
import com.main.vlbi.services.IDataProcessingService;
import com.main.vlbi.services.IImportSourceDataService;
import com.main.vlbi.services.IObsPlanningService;
import com.main.vlbi.utils.KeyFileDataUtils;
import com.main.vlbi.utils.ServiceEmailPrepare;
import com.main.vlbi.utils.ServiceForKeyFileGenerator;
import com.main.vlbi.models.planning.GroupObs;
import com.main.vlbi.repositorys.planning.IGroupObsRepo;

@Service("ObsPlanningServiceImpl")
public class ObsPlanningServiceImpl implements IObsPlanningService {

	@Autowired
	private IObservationRepo obsRepo;

	@Autowired
	private IProjectRepo projectRepo;

	@Autowired
	private IPipelineRepo pipelineRepo;

	@Autowired
	private IGlobalParameterRepo globParamsRepo;

	@Autowired
	private IObservationParamsRepo obsParamsRepo;

	@Autowired
	private IGroupObsRepo groupRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private IUserInfoRepo userInfoRepo;

	@Autowired
	private IObservationModeRepo obsModeRepo;

	@Autowired
	private IObsTypeRepo obsTypeRepo;

	@Autowired
	private ICorrInfoRepo corrInfoRepo;

	@Autowired
	private ICorrelationType corrTypeRepo;

	@Autowired
	private ISourceRepo sourceRepo;

	@Autowired
	private IStationParametersRepo statParamsRepo;

	@Autowired
	private IStationRepo statRepo;

	@Autowired
	private IScanRepo scanRepo;

	@Autowired
	private ICalculateUTCService calculateUTCService;

	@Autowired
	private IObsAcceptanceRepo accceptanceRepo;

	@Autowired
	private IImportSourceDataService dataImportService;

	private ServiceEmailPrepare emailPreparedService = new ServiceEmailPrepare();
	private EmailServiceImpl emailService = new EmailServiceImpl();
	
	@Override
	public File createObsKeyFile(String obsTile) throws IOException {

		File f = new File(obsTile + ".key");
		FileWriter writer = new FileWriter(f);
		ServiceForKeyFileGenerator gen = new ServiceForKeyFileGenerator();

		Observation obs = obsRepo.findByExpcode(obsTile);
		GlobalParameters globParams = globParamsRepo.findByParameter("sumitem");
		ObservationParams obsParams = obsParamsRepo.findByObservations(obs);
		ArrayList<User> users = userRepo.findByObservations(obs);

		User mainUser = users.get(0);
		UserInfo uMainInfo = userInfoRepo.findByUser(mainUser);
		User corrUser = mainUser;
		UserInfo uCorrInfo = uMainInfo;
		if (users.size() > 3) {
			corrUser = users.get(1);
			uCorrInfo = userInfoRepo.findByUser(corrUser);
		}

		ObservationMode obsMode = obsModeRepo.findByObservations(obsParams);
		ObservationType obsType = obsTypeRepo.findByObservations(obsParams);
		CorrelatorInfo corrInfo = corrInfoRepo.findByObservations(obs);
		CorrelationType corrType = corrTypeRepo.findByCorrInfo(corrInfo);

		ArrayList<StationParameters> statParams = statParamsRepo.findByObservations(obsParams);

		writer.write(gen.retrieveInfoHeader("Control code and print requests"));
		writer.write("overwrite\n");

		writer.write(gen.retrieveLine(globParams.getParameter(), globParams.getParamValue(), false, true));
		globParams = globParamsRepo.findByParameter("optmode");
		writer.write(gen.retrieveLine(globParams.getParameter(), globParams.getParamValue(), false, true));
		globParams = globParamsRepo.findByParameter("opminant");
		writer.write(gen.retrieveLine(globParams.getParameter(), globParams.getParamValue(), false, true));
		globParams = globParamsRepo.findByParameter("autotape");
		writer.write(gen.retrieveLine(globParams.getParameter(), globParams.getParamValue(), false, true));

		writer.write(gen.retrieveInfoHeader("Cover Information"));
		writer.write(gen.retrieveLine("expt", obsParams.getExpt(), true, true));
		writer.write(gen.retrieveLine("expcode", obs.getExpcode(), true, true));
		writer.write(gen.retrieveLine("version", obsParams.getVersion(), true));

		writer.write(gen.retrieveLine("piname", mainUser.getName() + " " + mainUser.getLast_name(), true, true));
		if (uMainInfo != null) {
			writer.write(gen.retrieveLine("address1", uMainInfo.getAddress1(), true, true));

			writer.write(gen.retrieveLine("address2", uMainInfo.getAddress2(), true, true));

			writer.write(gen.retrieveLine("address3", uMainInfo.getAddress3(), true, true));
			writer.write(gen.retrieveLine("phone", uMainInfo.getPhone(), true, true));
			writer.write(gen.retrieveLine("obsphone", uMainInfo.getObsphone(), true, true));
			writer.write(gen.retrieveLine("fax", uMainInfo.getFax(), true, true));

		}
		writer.write(gen.retrieveLine("email", mainUser.getEmail(), true, true));
		writer.write(gen.retrieveLine("obsmode", obsMode.getTitle(), true, true));
		writer.write(gen.retrieveLine("obstype", obsType.getTypeTitle(), true, true));
		writer.write(gen.retrieveLine("note1", obsParams.getNote1(), true, true));

		writer.write(gen.retrieveInfoHeader("Correlator Information"));
		writer.write(gen.retrieveLine("correl", corrType.getTypeTitle(), true, true));
		writer.write(gen.retrieveLine("coravg", corrInfo.getCoravg(), true));
		writer.write(gen.retrieveLine("corchan", corrInfo.getCorchan(), true));
		writer.write(gen.retrieveLine("cornant", corrInfo.getCornant(), true));
		writer.write(gen.retrieveLine("corpol", corrInfo.isCorpol()));
		writer.write(gen.retrieveLine("corwtfn", corrInfo.getCorwtfn(), true, true));
		writer.write(gen.retrieveLine("corsrcs", corrInfo.getCorsrcs(), true, true));
		writer.write(gen.retrieveLine("cortape", corrInfo.getCortape(), true, true));

		writer.write(gen.retrieveLine("corship1", corrUser.getName() + " " + corrUser.getLast_name(), true, true));
		if (uCorrInfo != null) {
			writer.write(gen.retrieveLine("corship2", uCorrInfo.getAddress1(), true, true));
			writer.write(gen.retrieveLine("corship3", uCorrInfo.getAddress2(), true, true));
			writer.write(gen.retrieveLine("corship4", uCorrInfo.getAddress3(), true, true));
			writer.write(gen.retrieveLine("cornote1", corrInfo.getCornoteAll(), true, true));
		}
		writer.write(gen.retrieveInfoHeader("Station Catalog"));
		globParams = globParamsRepo.findByParameter("stafile");
		writer.write(gen.retrieveLine(globParams.getParameter(), globParams.getParamValue(), true, true));
		globParams = globParamsRepo.findByParameter("locfile");
		writer.write(gen.retrieveLine(globParams.getParameter(), globParams.getParamValue(), true, true));

		writer.write(gen.retrieveInfoHeader("Source Catalog"));
		writer.write("SRCCAT /\n");
		//TODO added sources
		ArrayList<Scan> scans = scanRepo.findByObservations(obs);
		for (Scan sc : scans) {
			if (sc.getSource() != null)
			{
				Source source = sourceRepo.findByTitle(sc.getSource().getTitle());
				
				writer.write(gen.retrieveLine("source", source.getTitle(), true, false));
				writer.write(gen.retrieveLine("ra", source.getRightAscension(), false, false));
				writer.write(gen.retrieveLine("dec", source.getDeclination(), false, false));
				writer.write(gen.retrieveLine("equinox", source.getEquinox().toString(), true, false));
				writer.write(gen.retrieveLine("vel", source.getVel(), false));
				writer.write(gen.retrieveLine("vref", source.getVref(), true, false));
				writer.write(gen.retrieveLine("vdef", source.getVdef(), true, true));

			}
		}	
			
		
		
		
		writer.write("ENDCAT /\n");

		writer.write(gen.retrieveInfoHeader("Spectral line rest frequecies"));
		writer.write("LINEINIT /\n");
		writer.write(gen.retrieveLine("lineset", obsParams.getLineset(), true, true));

		writer.write(gen.retrieveLine("restfreq", obsParams.getRestfreq(), true, false) + " /\n");

		writer.write("ENDLINES /\n");
		writer.write(gen.retrieveLine("linename", obsParams.getLinename(), true, false));
		writer.write(((obsParams.isDoppler()) ? " doppler" : "") + "\n");

		writer.write(gen.retrieveInfoHeader("Observing setup"));
		writer.write(gen.retrieveLine("PRESTART", obsParams.getPrestart(), true));
		writer.write(gen.retrieveLine("SETINI", obs.getExpcode(), false, false) + "/\n");
		writer.write(gen.retrieveLine("BAND", obsParams.getBand(), true, true));
		writer.write(gen.retrieveLine("NCHAN", obsParams.getNchan(), true));
		writer.write(gen.retrieveLine("BITS", obsParams.getBits(), true));
		writer.write(gen.retrieveLine("BBFILTER", obsParams.getBbFilter(), true));
		writer.write(gen.retrieveLine("FREQREF", obsParams.getFreqRef(), true));
		writer.write(gen.retrieveLine("FREQOFF", obsParams.getFreqOff(), false, true));
		writer.write(gen.retrieveLine("NETSIDE", obsMode.getNetSide(), false, true));
		writer.write(gen.retrieveLine("pol", obsMode.getPol(), false, true));
		writer.write(gen.retrieveLine("PCAL", obsParams.isPcal()));
		writer.write(gen.retrieveLine("BARREL", obsParams.getBarrel(), false, true));
		writer.write("/\n\n");

		String stations = "";

		for (StationParameters statP : statParams) {
			Station stat = statRepo.findByParameters(statP);
			writer.write(gen.retrieveLine("firstlo", statP.getFirstLo(), false, true));
			writer.write(gen.retrieveLine("ifchan", statP.getIfChan(), false, true));
			writer.write(gen.retrieveLine("bbc", statP.getBbc(), false, true));
			writer.write(gen.retrieveLine("format", statP.getRecordingFormat(), false, false));
			writer.write(gen.retrieveLine("station", stat.getLongTitle(), false, false));
			stations += stat.getLongTitle() + ", ";
			writer.write("/\n");
		}

		writer.write("ENDSET\n /\n");
		writer.write(gen.retrieveLine("setup", obs.getExpcode(), true, true));
		writer.write(gen.retrieveLine("year", obs.getDateTimeLST().getYear(), true));
		writer.write(
				gen.retrieveLine("month", obs.getDateTimeLST().format(DateTimeFormatter.ofPattern("MM")), false, true));
		writer.write(
				gen.retrieveLine("day", obs.getDateTimeLST().format(DateTimeFormatter.ofPattern("dd")), false, true));
		writer.write(gen.retrieveLine("start", obs.getDateTimeLST().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
				false, true));

		writer.write(gen.retrieveInfoHeader("The Scans"));

		writer.write(gen.retrieveLine("stations", stations.substring(0, stations.length() - 2), false, true));

		for (Scan sc : scans) {
			if (sc.getSource() != null)
			{
				writer.write(gen.retrieveLine("source", sc.getSource().getTitle(), true, false));
			}

			LocalTime durationTime = LocalTime.ofSecondOfDay(sc.getDuration());
			writer.write(gen.retrieveLine("duration", durationTime.format(DateTimeFormatter.ofPattern("mm:ss")), false,
					false));
			LocalTime gapTime = LocalTime.ofSecondOfDay(sc.getGap());
			writer.write(gen.retrieveLine("gap", gapTime.format(DateTimeFormatter.ofPattern("mm:ss")), false, false));
			writer.write(gen.retrieveLine("dopsrc", sc.getDopsrc(), true, false) + " /\n");
			if (sc.getSource() != null) {
				if (sc.getSource().getVdef() != null) {
					writer.write(gen.retrieveLine("vdef", sc.getSource().getVdef(), true, false));
					writer.write(gen.retrieveLine("vel", sc.getSource().getVel(), false));
					writer.write(gen.retrieveLine("vref", sc.getSource().getVref(), true, false));
				}
			}

		}

		writer.close();
		return f;
	}

	@Override
	public void createObservation(String title, ArrayList<User> users, LocalDateTime dateAndStartTime,
			ObservationParams obsParams, CorrelatorInfo corrInfo, ArrayList<Source> sources) throws Exception {

		Observation newObservation = new Observation();
		newObservation.setUsers(users);
		newObservation.setDateTimeLST(dateAndStartTime);
		newObservation.setDateTimeUTC(calculateUTCtimeFromLST(newObservation.getDateTimeLST()));
		newObservation.setExpcode(title);
		newObservation.setCorrInfo(corrInfo);
		newObservation.setParam(obsParams);

		obsRepo.save(newObservation);
		if (accceptanceRepo.findByObservationExpcode(newObservation.getExpcode()) == null) {
			ObsAcceptance acceptance = new ObsAcceptance(newObservation);
			accceptanceRepo.save(acceptance);
		}

	}

	@Override
	public Observation copyObservationWithDifferentDateTime(String title, LocalDateTime datetimeLST, String username,
			ArrayList<Project> projects) throws Exception {

		LocalDateTime dateTimeUTC = calculateUTCtimeFromLST(datetimeLST);
		if (dateTimeUTC.isAfter(LocalDateTime.now().plusHours(4))) {
			Observation obs = obsRepo.findByExpcode(title);
			Observation newObs = new Observation();

			newObs.setCorrInfo(corrInfoRepo.findByObservations(obs));
			newObs.setDateTimeLST(datetimeLST);
			newObs.setDateTimeUTC(dateTimeUTC);
			newObs.setDurationInHours(obs.getDurationInHours());
			ArrayList<User> users = userRepo.findByObservations(obs);
			newObs.setUsers(users);
			User userFromSession = userRepo.findByEmail(username).get();
			if (userFromSession != null) {
				newObs.addUsers(userFromSession);
			}

			Pattern pattern = Pattern.compile("[^a-zA-Z]");
			Matcher matcher = pattern.matcher(obs.getExpcode());
			String result = matcher.replaceAll("");
			GroupObs groupObs = groupRepo.findByGroupObsTitle(result.toUpperCase());
			int counter = groupObs.getCounter();
			groupObs.setCounter(counter + 1);
			groupRepo.save(groupObs);

			newObs.setExpcode(result + String.format("%03d", (counter + 1)));
			newObs.setAntennas(obs.getAntennas());
			newObs.setRepeating(false);
			newObs.setGroupObs(obs.getGroupObs());
			newObs.setProjects(projects);
			
			ObservationParams params = obsParamsRepo.findByObservations(obs);
			newObs.setParam(params);

			ArrayList<Scan> scans = scanRepo.findByObservationsExpcode(obs.getExpcode());
			for (Scan tempSc : scans) {
				newObs.addScans(tempSc);
			}

			obsRepo.save(newObs);
			for (Scan tempSc : scans) {
				tempSc.addObservation(newObs);
				scanRepo.save(tempSc);
			}

			if (accceptanceRepo.findByObservationExpcode(newObs.getExpcode()) == null) {
				ObsAcceptance acceptance = new ObsAcceptance(newObs);
				accceptanceRepo.save(acceptance);
			}

			params.setObservations(new ArrayList<>(Arrays.asList(newObs)));
			obsParamsRepo.save(params);
			obsRepo.save(newObs);
			// TODO Uncomment these lines for production release:
			/*
			LocalDateTime datetime = LocalDateTime.now(); 
			  for (User u : users) { emailService.send(u.getEmail(),
			  "VSRC novērojuma pieteikums uz " +  datetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) +
			  "  /  VIRAC observation application to " +
			  datetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
			  emailPreparedService.retrieveEmailInfoOfObs(newObs)); }
			*/
			return obsRepo.findByExpcode(newObs.getExpcode());
		} else {
			throw (new Exception("Wrong datetime"));
		}

	}

	@Override
	public ArrayList<Source> retrieveSourcesLike(String title) {
		return sourceRepo.findByTitleContains(title);
	}

	@Override
	public void saveSource(Source source) throws Exception {
		if (source == null || sourceRepo.existsByTitle(source.getTitle())) {
			throw new Exception("Not posible to save source because it is already there");
		}
		sourceRepo.save(source);

	}

	@Override
	public KeyFileDataUtils readObsKeyFile(String keyFileLocation, String projectsIds) throws IOException {

		HashMap<String, String> keyFileData = new HashMap<>();
		String[] keyFileTags = { "sumitem", "optmode", "opminant", "autotape", "expt", "expcode", "version", "piname",
				"address1", "address2", "address3", "phone", "obsphone", "fax", "email", "obsmode", "obstype", "note1",
				"correl ", "coravg", "corchan", "cornant", "corpol", "corwtfn", "corsrcs", "cortape", "corship1",
				"corship2", "corship3", "corship4", "cornote1", "cornote2", "cornote3", "cornote4", "stafile",
				"locfile", "lineset", "restfreq", "linename", "PRESTART", "SETINI", "BAND", "NCHAN", "BITS", "BBFILTER",
				"FREQREF", "FREQOFF", "NETSIDE", "pol", "PCAL", "BARREL", "setup", "stations" };

		ArrayList<HashMap<String, String>> frequencyBandInfo = new ArrayList<>();
		HashMap<String, String> freqOneInfo = new HashMap<>();
		ArrayList<HashMap<String, String>> scans = new ArrayList<>();
		ArrayList<HashMap<String, String>> sources = new ArrayList<>();
		Scanner scanner = new Scanner(new File(keyFileLocation));
		boolean sourceBlock = false;
		boolean restfreqBlock = false;
		boolean frequencyBlock = false;

		String restFrq = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.contains("!")) {
				if (line.toUpperCase().contains("SRCCAT")) {
					sourceBlock = true;
				} else if (line.toUpperCase().contains("ENDCAT")) {
					sourceBlock = false;
				} else if (line.toLowerCase().contains("restfreq")) {
					restfreqBlock = true;
				} else if (line.toUpperCase().contains("ENDLINES")) {
					restfreqBlock = false;
					restFrq = restFrq.replace('/', ' ').trim();
				} else if (line.toLowerCase().contains("firstlo")) {
					freqOneInfo = new HashMap<>();
					frequencyBlock = true;

				} else if (line.toUpperCase().contains("ENDSET")) {
					frequencyBlock = false;
				}

				if (sourceBlock || restfreqBlock || frequencyBlock) {

					if (sourceBlock) {
						if (line.toLowerCase().contains("source")) {
							String sourceTitle = line.split("source")[1].trim().replace('=', ' ').replace('\'', ' ')
									.trim().split("ra")[0].trim();
							if (!line.toLowerCase().contains("ra") && !line.toLowerCase().contains("dec")) {
								line = scanner.nextLine();
							}
							String ra = line.split("ra")[1].split("dec")[0].trim().replace('=', ' ').replace('\'', ' ')
									.trim();
							String dec = line.split("dec")[1].split("equinox")[0].trim().replace('=', ' ')
									.replace('\'', ' ').trim();
							String equinox = "j2000";
							if (line.contains("equinox") && !line.contains("vel")) {
								equinox = line.split("equinox")[1].trim().replace('=', ' ').replace('\'', ' ')
										.replace('/', ' ').trim();
							} else {
								equinox = line.split("equinox")[1].trim().replace('=', ' ').replace('\'', ' ')
										.replace('/', ' ').trim().split(" ")[0];
							}

							HashMap<String, String> sourcesRead = new HashMap<>();
							sourcesRead.put("source", sourceTitle);

							sourcesRead.put("ra", ra);
							sourcesRead.put("dec", dec);
							sourcesRead.put("equinox", equinox);
							sources.add(sourcesRead);
						}
					}

					if (restfreqBlock) {
						if (line.toLowerCase().contains("restfreq")) {
							line = line.split("restfreq")[1].trim().replace('=', ' ').trim();
						}
						restFrq += line;
					}

					if (frequencyBlock) {
						if (line.toLowerCase().contains("firstlo")) {
							freqOneInfo.put("firstlo", line.split("firstlo")[1].trim().replace('=', ' ').trim());
						} else if (line.toLowerCase().contains("ifchan")) {
							freqOneInfo.put("ifchan", line.split("ifchan")[1].trim().replace('=', ' ').trim());
						} else if (line.toLowerCase().contains("bbc")) {
							freqOneInfo.put("bbc", line.split("bbc")[1].trim().replace('=', ' ').trim());
						} else if (line.toLowerCase().contains("format") && line.toLowerCase().contains("station")) {
							String[] linesTemp = line.split("station");
							freqOneInfo.put("format", linesTemp[0].split("format")[1].trim().replace('=', ' ').trim());
							freqOneInfo.put("station", linesTemp[1].replace('=', ' ').trim().replace('/', ' ').trim());
							frequencyBandInfo.add(freqOneInfo);
						}
					}
				}

				//te kļuda
				if (line.toLowerCase().contains("duration")) {
					HashMap<String, String> scanOneInfo = new HashMap<>();

					scanOneInfo.put("source", line.split("gap")[0].split("source")[1].trim().replace('=', ' ')
							.replace('\'', ' ').trim());
					scanOneInfo.put("gap", line.split("duration")[0].split("gap")[1].trim().replace('=', ' ').trim());
					scanOneInfo.put("duration",
							line.split("dopsrc")[0].split("duration")[1].trim().replace('=', ' ').trim());
					scanOneInfo.put("dopsrc", line.split("dopsrc")[1].trim().replace('=', ' ').replace('\'', ' ')
							.replace('/', ' ').trim());
					scans.add(scanOneInfo);
				} else if (line.toLowerCase().contains("dur ")) {
					HashMap<String, String> scanOneInfo = new HashMap<>();

					scanOneInfo.put("source",
							line.split("gap")[0].split("source")[1].trim().replace('=', ' ').replace('\'', ' ').trim());
					scanOneInfo.put("gap", line.split("dur")[0].split("gap")[1].trim().replace('=', ' ').trim());
					scanOneInfo.put("duration", line.split("dopsrc")[0].split("dur")[1].trim());

					scanOneInfo.put("dopsrc", line.split("dopsrc")[1].trim().replace('=', ' ').replace('\'', ' ')
							.replace('/', ' ').trim());
					scans.add(scanOneInfo);
				} else if (line.contains("station") && !line.contains("format")) {
					keyFileData.put("stations",
							line.split("station")[1].trim().replace('=', ' ').replace('\'', ' ').trim());
				} else if (line.toLowerCase().contains("start") && line.contains(":")) {
					keyFileData.put("start",
							line.split("(?i)start")[1].trim().replace('=', ' ').replace('\'', ' ').trim());
				} else if (line.toLowerCase().contains("year")) {
					keyFileData.put("year",
							line.split("(?i)year")[1].trim().replace('=', ' ').replace('\'', ' ').trim());
				} else if (line.toLowerCase().contains("month")) {
					keyFileData.put("month",
							line.split("(?i)month")[1].trim().replace('=', ' ').replace('\'', ' ').trim());
				} else if (line.toLowerCase().contains("day")) {
					keyFileData.put("day", line.split("(?i)day")[1].trim().replace('=', ' ').replace('\'', ' ').trim());
				} else {

					for (String tempKeytag : keyFileTags) {
						if (line.toLowerCase().contains(tempKeytag.toLowerCase())) {
							keyFileData.put(tempKeytag,
									line.split(tempKeytag)[1].trim().replace('=', ' ').replace('\'', ' ').trim());
						}
					}
				}

			}
		}

		scanner.close();
		KeyFileDataUtils data = new KeyFileDataUtils(keyFileData, sources, frequencyBandInfo, scans, restFrq,
				projectsIds);
		return data;
	}

	@Override
	public void saveKeyFileDataInDB(KeyFileDataUtils data, String usernameWhoUploaded) throws Exception {
		if (data != null) {
			ArrayList<Source> sourcesFromRepo = new ArrayList<>();
			for (HashMap<String, String> temp : data.getSources()) {
				Source tempSource = sourceRepo.findByTitle(temp.get("source"));
				
				if (tempSource == null) {
					if (!temp.get("ra").equals("") && !temp.get("dec").equals("")) {
						if (!temp.get("ra").contains(".")) {
							temp.put("ra", temp.get("ra") + ".00");
						}

						if (!temp.get("dec").contains(".")) {
							temp.put("dec", temp.get("dec") + ".00");
						}
						tempSource = sourceRepo.save(new Source(temp.get("source"), temp.get("ra"), temp.get("dec"),
								Equinox.valueOf(temp.get("equinox").toUpperCase())));
					} else {

					}
				}
				sourcesFromRepo.add(tempSource);
			}

			ArrayList<Station> stationsFromDB = new ArrayList<>();
			String antennas = "";
			String[] tempStations = data.getOtherInfo().get("stations").split(", ");
			for (String temp : tempStations) {
				Station st = statRepo.findByShortTitle(temp.toLowerCase());
				if (st == null) {
					st = statRepo.save(new Station("", temp, ""));
				}
				stationsFromDB.add(st);
				antennas += st.getPopTitle() + " ";
			}

			ArrayList<StationParameters> stationParametersInDB = new ArrayList<>();

			for (HashMap<String, String> temp : data.getDataForFrequencies()) {
				for (Station tempSt : stationsFromDB) {

					if (tempSt.getLongTitle().equals(temp.get("station"))) {
						StationParameters paramtemp = statParamsRepo
								.findByRecordingFormatAndFirstLoAndIfChanAndBbcAndStation(temp.get("format"),
										temp.get("firstlo"), temp.get("ifchan"), temp.get("bbc"), tempSt);
						if (paramtemp == null) {
							paramtemp = statParamsRepo.save(new StationParameters(temp.get("format"),
									temp.get("firstlo"), temp.get("ifchan"), temp.get("bbc"), tempSt));
						}

						stationParametersInDB.add(paramtemp);
					}
				}
			}

			ObservationMode mode1 = obsModeRepo.findByPolAndNetSide(data.getOtherInfo().get("pol").trim(),
					data.getOtherInfo().get("NETSIDE").trim());
			if (mode1 == null) {
				mode1 = obsModeRepo.save(new ObservationMode("", data.getOtherInfo().get("pol").trim(),
						data.getOtherInfo().get("NETSIDE").trim()));
			}

			ObservationType type1 = obsTypeRepo.findByTypeTitle(data.getOtherInfo().get("obstype"));
			if (type1 == null) {
				type1 = obsTypeRepo.save(new ObservationType(data.getOtherInfo().get("obstype")));
			}

			ObservationParams tempObsParams = new ObservationParams();

			for (StationParameters statTemp : stationParametersInDB) {
				tempObsParams.addStatParams(statTemp);
			}

			String linename = data.getOtherInfo().get("linename").split("dopple")[0].trim();
			if (linename != null) {
				tempObsParams.setDoppler(true);
				tempObsParams.setLinename(linename);
			} else {
				tempObsParams.setDoppler(false);
			}

			tempObsParams.setLineset(data.getOtherInfo().get("lineset"));

			tempObsParams.setRestfreq(data.getRestFreqInfo());

			tempObsParams.setObsMode(mode1);
			tempObsParams.setPrestart(Integer.parseInt(data.getOtherInfo().get("PRESTART")));
			tempObsParams.setBand(data.getOtherInfo().get("BAND"));
			tempObsParams.setNchan(Integer.parseInt(data.getOtherInfo().get("NCHAN")));
			tempObsParams.setBits(Integer.parseInt(data.getOtherInfo().get("BITS")));
			tempObsParams.setBbFilter(Float.parseFloat(data.getOtherInfo().get("BBFILTER")));
			tempObsParams.setFreqRef(Float.parseFloat(data.getOtherInfo().get("FREQREF")));
			tempObsParams.setFreqOff(data.getOtherInfo().get("FREQOFF"));
			if (data.getOtherInfo().get("PCAL").equals("off"))
				tempObsParams.setPcal(false);
			else
				tempObsParams.setPcal(true);

			tempObsParams.setBarrel(data.getOtherInfo().get("BARREL"));
			tempObsParams.setExpt(data.getOtherInfo().get("expt"));
			tempObsParams.setVersion(Integer.parseInt(data.getOtherInfo().get("version")));
			tempObsParams.setNote1(data.getOtherInfo().get("note1"));
			tempObsParams.setObsType(type1);
			tempObsParams.setObsModeNote(data.getOtherInfo().get("obsmode"));

			obsParamsRepo.save(tempObsParams);

			for (StationParameters statTemp : stationParametersInDB) {
				statTemp.addObservation(tempObsParams);
				statParamsRepo.save(statTemp);
			}

			CorrelationType corrT1 = corrTypeRepo.findByTypeTitle(data.getOtherInfo().get("correl "));
			if (corrT1 == null) {
				corrT1 = corrTypeRepo.save(new CorrelationType(data.getOtherInfo().get("correl")));
			}

			Optional<User> userOpt = userRepo.findByEmail(data.getOtherInfo().get("email"));
			User user;
			if (!userOpt.isEmpty()) {
				user = userOpt.get();
			} else {
				user = userRepo.findByEmail("admin@admin.lv").get();
			}

			boolean corpol = (data.getOtherInfo().get("corpol").equals("off")) ? false : true;
			CorrelatorInfo corrInfo1 = corrInfoRepo
					.findByCoravgAndCorchanAndCornantAndCorpolAndCorwtfnAndCorsrcsAndCortapeAndCorrType(
							Float.parseFloat(data.getOtherInfo().get("coravg")),
							Integer.parseInt(data.getOtherInfo().get("corchan")),
							Integer.parseInt(data.getOtherInfo().get("cornant")), corpol,
							data.getOtherInfo().get("corwtfn"), data.getOtherInfo().get("corsrcs"),
							data.getOtherInfo().get("cortape"), corrT1);

			String corrNotesAll = data.getOtherInfo().get("cornote1") + data.getOtherInfo().get("cornote2")
					+ data.getOtherInfo().get("cornote3") + data.getOtherInfo().get("cornote4");

			if (corrInfo1 == null && user != null) {
				corrInfo1 = corrInfoRepo.save(new CorrelatorInfo(Float.parseFloat(data.getOtherInfo().get("coravg")),
						Integer.parseInt(data.getOtherInfo().get("corchan")),
						Integer.parseInt(data.getOtherInfo().get("cornant")), corpol,
						data.getOtherInfo().get("corwtfn"), data.getOtherInfo().get("corsrcs"),
						data.getOtherInfo().get("cortape"), corrNotesAll, user, corrT1));
			}

			String[] time = data.getOtherInfo().get("start").split(":");

			LocalDateTime datetime = LocalDateTime.of(Integer.parseInt(data.getOtherInfo().get("year")),
					Integer.parseInt(data.getOtherInfo().get("month")),
					Integer.parseInt(data.getOtherInfo().get("day")), Integer.parseInt(time[0]),
					Integer.parseInt(time[1]), Integer.parseInt(time[2]));

			Observation obsfromDB = obsRepo.findByExpcodeAndDateTimeLST(data.getOtherInfo().get("expcode"), datetime);

			User userWhoUploaded = userRepo.findByEmail(usernameWhoUploaded).get();

			if (obsfromDB == null) {
				String groupTitle = Pattern.compile("[^A-Z]+").matcher(data.getOtherInfo().get("expcode"))
						.replaceAll("");

				GroupObs group = groupRepo.findByGroupObsTitle(groupTitle);
				if (obsRepo.findByExpcode(data.getOtherInfo().get("expcode")) == null) {
					obsfromDB = obsRepo.save(new Observation(corrInfo1, datetime, calculateUTCtimeFromLST(datetime),
							data.getOtherInfo().get("expcode"), group, tempObsParams, user, userWhoUploaded));
				} else {
					obsfromDB = obsRepo.save(new Observation(corrInfo1, datetime, calculateUTCtimeFromLST(datetime),
							data.getOtherInfo().get("expcode"), group, tempObsParams, user, userWhoUploaded));				}

				if (data.getProjects() != null && data.getProjects() != "") {
					JSONArray projectsArray = new JSONArray(data.getProjects());

					for (int i = 0; i < projectsArray.length(); i++) {
						JSONObject project = projectsArray.getJSONObject(i);
						long projectId = project.getLong("projectId");
						Project p = projectRepo.findById(projectId).get();
						obsfromDB.addProject(p);

					}
				}

			}

			user.addObservation(obsfromDB);
			userRepo.save(user);

			int obsDurationTotal = 0;
			for (HashMap<String, String> temp : data.getDataForScans()) {
				Source sc = sourceRepo.findByTitle(temp.get("source"));
			
				if (sc == null) {
					HashMap<String, String> sourceData = dataImportService
							.getInfoAboutSourceFromSimbad(temp.get("source"));
					
					sc = sourceRepo.save(new Source(sourceData.get("source"), sourceData.get("ra"),
							sourceData.get("dec"), Equinox.J2000));
				}

				int duration = Integer.parseInt(temp.get("duration").split(":")[0]) * 60
						+ Integer.parseInt(temp.get("duration").split(":")[1]);
				String dopsrc = temp.get("dopsrc");
				int gap = Integer.parseInt(temp.get("gap").split(":")[0]) * 60
						+ Integer.parseInt(temp.get("gap").split(":")[1]);
				obsDurationTotal += duration + gap;

				Scan scanTemp = scanRepo.findBySourceAndDurationAndDopsrcAndGap(sc, duration, dopsrc, gap);

				if (scanTemp == null) {
					scanTemp = scanRepo.save(new Scan(duration, gap, dopsrc, sc));

				}
				scanTemp.addObservation(obsfromDB);
				scanRepo.save(scanTemp);
				obsfromDB.addScans(scanTemp);

				obsRepo.save(obsfromDB);
				if (accceptanceRepo.findByObservationExpcode(obsfromDB.getExpcode()) == null) {
					ObsAcceptance acceptance = new ObsAcceptance(obsfromDB);
					accceptanceRepo.save(acceptance);
				}
			}

			obsfromDB.setDurationInHours(obsDurationTotal / 3600f);
			obsfromDB.setAntennas(antennas);
			obsRepo.save(obsfromDB);
			// TODO Uncomment these lines for production release:
			/*
			 emailService.send(user.getEmail(), "VSRC novērojuma pieteikums uz " +
			 datetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) +
			 "  /  VIRAC observation application to " +
			 datetime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
			 emailPreparedService.retrieveEmailInfoOfObs(obsfromDB));
			 */

		}

	}

	@Override
	public File createObsVexFile(String obsTitle) throws IOException {
		File f = new File(obsTitle + ".vex");
		FileWriter writer = new FileWriter(f);

		Observation obs = obsRepo.findByExpcode(obsTitle);
		writer.write(obsTitle);
		writer.write(obs.toString());
		writer.close();
		return f;
	}

	@Override
	public boolean isAntenaAvailableFromDefenceMinistry(LocalDateTime UTCfrom, LocalDateTime UTCto, String antenna)
			throws IOException, InterruptedException {
		if (antenna.equals("RT32") || antenna.equals("RT16") || antenna.equals("RT-32") || antenna.equals("RT-16")) {
			String antenaShortName = (antenna.equals("RT32") || antenna.equals("RT-32")) ? "ir" : "ib";
			return CheckAntennaAvailability.getInfoAboutAntennaAvailibilityDefenceAPI(UTCfrom, UTCto, antenaShortName);
		}

		return false;
	}

	@Override
	public boolean isAntenaAvailableFromGoogleCalendar(LocalDateTime from, LocalDateTime to, String antenna)
			throws IOException, InterruptedException {

		if (antenna.equals("RT32") || antenna.equals("RT16") || antenna.equals("RT-32") || antenna.equals("RT-16")
				|| antenna.equals("LOFAR")) {
			return CheckAntennaAvailability.getInfoAboutAntennaAvailibilityGoogleCalendarApi(from, to, antenna);
		}

		return false;
	}

	@Override
	public ArrayList<ObservationDTO> retrieveObservationsByUsername(String username) throws Exception {
		if (username != null) {
			ArrayList<Observation> obsList = obsRepo.findByUsersEmail(username);
			if (obsList != null) {
				ArrayList<ObservationDTO> obsDTOlist = new ArrayList<>();

				for (Observation obs : obsList) {
					obsDTOlist.add(new ObservationDTO(obs.getExpcode(), obs.getDateTimeUTC(), obs.getDateTimeLST(),
							obs.getAntennas(), obs.getProjects(), obs.getAcceptance()));
				}
				return obsDTOlist;
			}
		}

		throw new Exception("Problems with username");
	}

	@Override
	public ArrayList<ObservationTitleAndDateTimeDTO> calculateUTCtimeForArrayList(
			ArrayList<ObservationTitleAndDateTimeDTO> observations) throws Exception {
		for (ObservationTitleAndDateTimeDTO obs : observations) {
			obs.setUtcDateTimeStart(calculateUTCtimeFromLST(obs.getDateTime()));
			float durationInHours = obs.getDurationInHours();
			long hours = (long) Math.floor(durationInHours);
			long minutes = Math.round((durationInHours - hours) * 60);
			LocalDateTime endTimeUTC = obs.getUtcDateTimeStart().plusHours(hours).plusMinutes(minutes);
			obs.setUtcDateTimeEnd(endTimeUTC);
		}
		return observations;
	}

	@Override
	public ArrayList<ObservationTitleAndDateTimeDTO> checkAvailability(
			ArrayList<ObservationTitleAndDateTimeDTO> observations) throws Exception {

		for (ObservationTitleAndDateTimeDTO temp : observations) {
			temp.setStatus32(
					isAntenaAvailableFromDefenceMinistry(temp.getUtcDateTimeStart(), temp.getUtcDateTimeEnd(), "RT32")
							&& isAntenaAvailableFromGoogleCalendar(temp.getUtcDateTimeStart(), temp.getUtcDateTimeEnd(),
									"RT32"));
			temp.setStatus16(
					isAntenaAvailableFromDefenceMinistry(temp.getUtcDateTimeStart(), temp.getUtcDateTimeEnd(), "RT16")
							&& isAntenaAvailableFromGoogleCalendar(temp.getUtcDateTimeStart(), temp.getUtcDateTimeEnd(),
									"RT16"));
		}
		return observations;
	}

	@Override
	public KeyFileDataUtils readObsKeyFile(byte[] fileBytes, String projectsIds) throws IOException {
		Path tempFilePath = Files.createTempFile("uploadedFile", ".key");
		Files.write(tempFilePath, fileBytes);
		KeyFileDataUtils tempData = readObsKeyFile(tempFilePath.toString(), projectsIds);
		Files.delete(tempFilePath);
		return tempData;
	}

	@Override
	public LocalDateTime calculateUTCtimeFromLST(LocalDateTime lstTime) throws Exception {
		return calculateUTCService.getUTCTimeFromLST(lstTime.getYear(), lstTime.getMonthValue(),
				lstTime.getDayOfMonth(), lstTime.getHour(), lstTime.getMinute(), lstTime.getSecond());
	}

	@Override
	public ArrayList<ObservationTitleAndDateTimeDTO> copyObservationsWithDifferentDateTime(
			ArrayList<ObservationTitleAndDateTimeDTO> obs, String username) throws Exception {
		ArrayList<ObservationTitleAndDateTimeDTO> result = new ArrayList<>();

		for (ObservationTitleAndDateTimeDTO temp : obs) {

			ArrayList<Project> projects = new ArrayList<>();
			String projectAsString = "";
			for (Project prTemp : temp.getProjects()) {
				Project fromDB = projectRepo.findById(prTemp.getProjectId()).get();
				projects.add(fromDB);
				projectAsString += fromDB.getTitle() + " ";
			}
			Observation obsNew = copyObservationWithDifferentDateTime(temp.getTitle(), temp.getDateTime(), username,
					projects);
			
			float durationInHours = obsNew.getDurationInHours();
			long hours = (long) Math.floor(durationInHours);
			long minutes = Math.round((durationInHours - hours) * 60);
			LocalDateTime endTimeUTC = obsNew.getDateTimeUTC().plusHours(hours).plusMinutes(minutes);
			
			result.add(new ObservationTitleAndDateTimeDTO(obsNew.getExpcode(), obsNew.getDateTimeLST(), obsNew.getDateTimeUTC(), endTimeUTC,
					true, true, obsNew.getDurationInHours(), projects));
			
			// TODO Uncomment these lines for production release:
			/*
			PySchedCalling.createvexFileUsingPySched(obsNew.getExpcode(), obsNew.getDateTimeUTC(),
					temp.getProjects().get(0).getTitle());
			ActionsWithGoogleCalendar.createEventInGoogleCalendar(obsNew.getDateTimeUTC(), endTimeUTC,
					"RT-16 " + projectAsString + " " + obsNew.getExpcode() + "(C band)");
			ActionsWithGoogleCalendar.createEventInGoogleCalendar(obsNew.getDateTimeUTC(), endTimeUTC,
					"RT-32 " + projectAsString + " " + obsNew.getExpcode() + "(C band)");
			 */
		}

		return result;
	}

	@Override
	public boolean isNeedToRepeat(String expcode) throws Exception {
		Observation obs = obsRepo.findByExpcode(expcode);

		if (obs != null) {
			return (!accceptanceRepo.existsByObservationExpcodeAndAccept1AndAccept2AndAccept3AndAccept4(expcode, true,
					true, true, true));
		} else
			throw new Exception("Problem with observation expcode");

	}

	@Override
	public void addCheckToObservation(String expcode, int check) throws Exception {
		Observation obs = obsRepo.findByExpcode(expcode);

		if (obs != null && check >= 1 && check <= 4) {
			ObsAcceptance accetance = accceptanceRepo.findByObservationExpcode(expcode);
			if (check == 1)
				accetance.setAccept1(true);
			else if (check == 2)
				accetance.setAccept2(true);
			else if (check == 3)
				accetance.setAccept3(true);
			else if (check == 4)
				accetance.setAccept4(true);
			accceptanceRepo.save(accetance);

		} else
			throw new Exception("Problem with observation expcode");

	}

	@Override
	public CheckAntennaDTO checkAvailabilityAntenna(CheckAntennaDTO info) throws Exception {
		if (info != null) {
			ArrayList<Boolean> availability = new ArrayList<>();
			for (String temp : info.getAntennas()) {
				if (temp.equals("RT-32") || temp.equals("RT-16")) {
					availability.add(
							isAntenaAvailableFromDefenceMinistry(info.getStartDateTime(), info.getEndDateTime(), temp)
									&& isAntenaAvailableFromGoogleCalendar(info.getStartDateTime(),
											info.getEndDateTime(), temp));

				} else if (temp.equals("LOFAR")) {
					availability.add(
							isAntenaAvailableFromGoogleCalendar(info.getStartDateTime(), info.getEndDateTime(), temp));
				} else
					availability.add(false);

			}
			info.setAvailability(availability);
			return info;
		}
		throw new Exception("Problems with data");
	}

	@Override
	public ArrayList<Project> getAllProjects() {
		return (ArrayList<Project>) projectRepo.findAll();
	}

	@Override
	public ArrayList<Pipeline> getAllPipelines() {
		return (ArrayList<Pipeline>) pipelineRepo.findAll();
	}

	@Override
	public ArrayList<GroupObs> getAllGroups() {
		return (ArrayList<GroupObs>) groupRepo.findAll();
	}

}
