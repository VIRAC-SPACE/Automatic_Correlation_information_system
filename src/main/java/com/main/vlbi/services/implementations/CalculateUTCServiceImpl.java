package com.main.vlbi.services.implementations;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.main.vlbi.helpers.CalculateUTCHelper;
import com.main.vlbi.services.ICalculateUTCService;

@Service("CalculateUTCServiceImpl")
public class CalculateUTCServiceImpl implements ICalculateUTCService {

	/**
	 * The mathematical constant PI.
	 */
	public static final double PI = 3.14159265358979;

	@Override
	public LocalDateTime getUTCTimeFromLST(int year, int month, int day, int hoursLst, int minutesLst, int secondsLSt)
			throws Exception {

		double glong = 21.857f;
		double leapSecs = 0.0;

		for (int hoursx = 0; hoursx < 24; hoursx++) {
			for (int minutesx = 0; minutesx < 60; minutesx++) {
				for (int secondsx = 0; secondsx < 60; secondsx++) {

					double utx = hoursx + minutesx / 60.0 + secondsx / 3600.0;

					double lstx = CalculateUTCHelper.CalcLST(year, month, day, utx, glong, leapSecs);

					String lst_s = CalculateUTCHelper.decToString(lstx, 0, 24, false);
					LocalTime timeLST = LocalTime.parse(lst_s);
					int hours = timeLST.getHour();
					int minutes = timeLST.getMinute();
					int seconds = timeLST.getSecond();
					if (Math.abs(hoursLst - hours) < 0.1 && Math.abs(minutesLst - minutes) < 0.1
							&& Math.abs(secondsLSt - seconds) < 0.1) {
						LocalDate date = LocalDate.of(year, month, day);
						LocalTime time = LocalTime.of(hoursx, minutesx, secondsx);
						LocalDateTime combinedDateTime = LocalDateTime.of(date, time);
						return combinedDateTime;
					}
				}
			}
		}

		throw new Exception("Problems with calculations: LST -> UTC");
	}

}
