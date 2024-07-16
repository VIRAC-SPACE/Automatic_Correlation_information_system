package com.main.vlbi.utils;

public class ServiceForKeyFileGenerator {
	
	public String retrieveInfoHeader(String title) {
		String result = "!";
		
		
		for(int i = 0; i < 58; i++) {
			result += "=";
		}
		result += "\n!";
		
		int half = title.length()/2;
		
		for(int i = 0; i < 58/2 - half - 1; i++) {
			result += "=";
		}
		result +=" " +title + " ";
		
		for(int i = 0; i < 58/2 - half - 2; i++) {
			result += "=";
		}
		result += "\n!";
		for(int i = 0; i < 58; i++) {
			result += "=";
		}
		result += "\n";
		return result;
	}

	public String retrieveLine(String param, String value, boolean isApostroph, boolean isEnd) {
		if(isApostroph)
			if(isEnd)
				return param + " = '" + value + "'\n";
			else
				return param + " = '" + value + "'\t";
		else
			if(isEnd)
				return param + " = " + value + "\n";
			else
				return param + " = " + value + "\t";
	}
	
	
	public String retrieveLine(String param, int value, boolean isEnd) {
		if(isEnd)
			return param + " = " + value + "\n";
		else
			return param + " = " + value + "\t";
	}
	
	public String retrieveLine(String param, float value, boolean isEnd) {
		if(isEnd)
			return param + " = " + value + "\n";
		else
			return param + " = " + value + "\t";
	}
	public String retrieveLine(String param, boolean value) {
		if(value)
			return param + " = 'on'\n";
		else
			return param + " = 'off'\n";
	}
}
