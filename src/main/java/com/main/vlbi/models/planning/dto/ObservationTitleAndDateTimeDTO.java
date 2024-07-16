package com.main.vlbi.models.planning.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.main.vlbi.models.planning.Project;

import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ObservationTitleAndDateTimeDTO {
	private String title;
	
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime dateTime;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime utcDateTimeStart;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime utcDateTimeEnd;
    
    private boolean status32;
    private boolean status16;
    
    private float durationInHours;
    
    private ArrayList<Project> projects;

}
