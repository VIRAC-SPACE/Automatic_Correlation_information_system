package com.main.vlbi.utils;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyFileDataUtils {
	
	private HashMap<String, String> otherInfo;
	private ArrayList<HashMap<String, String>> sources;
	private ArrayList<HashMap<String, String>> dataForFrequencies;
	private ArrayList<HashMap<String, String>> dataForScans;
	private String restFreqInfo;
	private String projects;
	
	

}
