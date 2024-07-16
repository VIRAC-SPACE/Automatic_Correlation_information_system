package com.main.vlbi.utils;

import java.time.format.DateTimeFormatter;

import com.main.vlbi.models.User;
import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.StationParameters;

public class ServiceEmailPrepare {

	public String retrieveEmailInfoOfObs(Observation obs) {
		String result = "Sveicināti! \n\n Informējam, ka uz "
				+ obs.getDateTimeUTC().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " ir pieteikts novērojums "
				+ obs.getExpcode() + ", kurā tiks izmantotas sekojošas VSRC stacijas: ";

		String stations = "";
		for (StationParameters statP : obs.getParam().getStatParams()) {
			stations += statP.getStation().getLongTitle() + "; ";
		}

		result += stations;
		User mainUser = obs.getUsers().iterator().next();
		result += "\n\nNovērojumu pieteicējs ir " + mainUser.getName() + " " + mainUser.getLast_name() + ".";
		result += "\nJautājumu gadījumā sūtīt epastu uz " + mainUser.getEmail();

		result += "\n\nŠis epasts ir nosūtīts automātiski no VSRC datu apstrādes sistēmas ACor. Lūdzu uz epastu neatbildēt!";

		result += "\n\n------------------------------------------------------------";

		result += "\nHello! \n\nPlease be informed that an observation " + obs.getExpcode() + " has been requested for "
				+ obs.getDateTimeUTC().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
				+ " using the following VIRAC stations: ";
		result += stations;
		result += "\n\nThe observation applicant is " + mainUser.getName() + " " + mainUser.getLast_name() + ".";
		result += "\nIn case of questions, send an email to " + mainUser.getEmail();

		result += "\n\nThis email has been sent automatically from the VSRC data processing system ACor. Please do not reply to this email!";

		return result;
	}

}
