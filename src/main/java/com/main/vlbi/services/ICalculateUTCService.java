package com.main.vlbi.services;

import java.time.LocalDateTime;

public interface ICalculateUTCService {
	
	 LocalDateTime getUTCTimeFromLST(int year, int month, int day, int hoursLst, int minutesLst, int secondsLSt) throws Exception;

}
