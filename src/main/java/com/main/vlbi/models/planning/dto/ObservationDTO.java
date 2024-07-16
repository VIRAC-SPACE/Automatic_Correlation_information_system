package com.main.vlbi.models.planning.dto;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.main.vlbi.models.planning.ObsAcceptance;
import com.main.vlbi.models.planning.Project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ObservationDTO {
    private String expcode;
	
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTimeUTC;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTimeLST;
        
    private String antennas;
       
    private Collection<Project> projects;
    
    private ObsAcceptance acceptance;
}

