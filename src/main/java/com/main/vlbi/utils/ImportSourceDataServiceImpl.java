package com.main.vlbi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.main.vlbi.models.planning.Equinox;
import com.main.vlbi.models.planning.Source;
import com.main.vlbi.repositorys.planning.ISourceRepo;
import com.main.vlbi.services.IImportSourceDataService;

@Service
public class ImportSourceDataServiceImpl implements IImportSourceDataService {

	@Autowired
	private ISourceRepo sourceRepo;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void importSourceDataFromConfFiles(String filename) throws FileNotFoundException {
		File f = new File(filename);
		Scanner scanner;

		try {
			scanner = new Scanner(f);
			boolean blockStart = false;
			String source = "", ra = "", dec = "";
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.matches("\\[[a-zA-Z0-9_]+\\]")) {
					blockStart = true;
				}

				if (blockStart && line.matches("source = [a-zA-Z0-9]+")) {
					source = line.split("source = ")[1];
				}

				if (blockStart && line.matches("RA=[+-]?[hms0-9\\s.]+")) {
					ra = line.split("RA=")[1].replace('h', ':').replace('m', ':').replace('s', ' ').trim();
				}

				if (blockStart && line.matches("DEC= [+-]?[dms0-9\\s.]+")) {
					dec = line.split("DEC= ")[1].replace('d', ':').replace('m', ':').replace('s', ' ').trim();
					saveSourceInDB(new Source(source, ra, dec, Equinox.J2000));
					blockStart = false;
				}

			}

			scanner.close();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(e.getMessage());
		}

	}

	@Override
	public void saveSourceInDB(Source source) {
		if (!sourceRepo.existsByTitle(source.getTitle())) {
			sourceRepo.save(source);
		}

	}

	@Override
	public void importSourceDataFromExcel(String filename) throws FileNotFoundException {

		Map<Integer, List<String>> data = new HashMap<>();
		try {

			try (FileInputStream file = new FileInputStream(filename);
					ReadableWorkbook wb = new ReadableWorkbook(file)) {
				Sheet sheet = wb.getFirstSheet();
				try (Stream<Row> rows = sheet.openStream()) {
					rows.forEach(r -> {
						data.put(r.getRowNum(), new ArrayList<>());

						for (Cell cell : r) {
							if (cell != null)
								data.get(r.getRowNum()).add(cell.getRawValue());
						}
					});
				}
			}
			for (Map.Entry<Integer, List<String>> entry : data.entrySet()) {
				List<String> entries = entry.getValue();
				if (entries.size() > 7) {
					// excel start with A column with source title
					String source = entries.get(0);
					String ra = entries.get(1) + ":" + entries.get(2) + ":" + entries.get(3);
					String dec = entries.get(4) + ":" + entries.get(5) + ":" + entries.get(6);
					if (!source.contains("null") && !ra.contains("null") && !dec.contains("null")) {
						saveSourceInDB(new Source(source, ra, dec, Equinox.J2000));
					}
				}
			}

		} catch (IOException e) {
			throw new FileNotFoundException(e.getMessage());
		}

	}

	@Override
	public HashMap<String, String> getInfoAboutSourceFromSimbad(String sourceTitle) {
		HashMap<String, String> results = new HashMap<String, String>();
		//TODO need to be upgraded because simbad API has been updated and it returns different format data
		String response = restTemplate.getForObject(
				"http://simbad.u-strasbg.fr/simbad/sim-id?coodispN=2&output.format=ASCII&epochN=J2000&frameN=ICRS&Ident="
						+ sourceTitle,
				String.class);
		results.put("source", sourceTitle);
		Scanner scanner = new Scanner(response);
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.contains("Coordinates")) {
				String[] onlyRaAndDec = line.replaceAll("  ", " ").split(":")[1].split("\\(")[0].split("\\ ");
				int x = 0;
				if (onlyRaAndDec[0].equals(""))
					x = 1;
				if (onlyRaAndDec.length >= 6) {
					results.put("ra", onlyRaAndDec[x].trim() + ":" + onlyRaAndDec[x + 1].trim() + ":"
							+ onlyRaAndDec[x + 2].trim());
					results.put("dec", onlyRaAndDec[x + 3].trim() + ":" + onlyRaAndDec[x + 4].trim() + ":"
							+ onlyRaAndDec[x + 5].trim());
					scanner.close();
					return results;
				}
			}
		}

		scanner.close();
		return results;
	}

}
