package com.main.vlbi.models.planning;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//ALTER TABLE data_processing MODIFY COLUMN proc_start_date_time DATETIME  DEFAULT CURRENT_TIMESTAMP;

@Entity(name = "DataProcessing")
@Table(name = "DataProcessing")
@Getter @Setter @NoArgsConstructor
public class DataProcessing {
	
 	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DataProcessingId")
 	@Setter(value = AccessLevel.NONE)
    private Long dataProcessingId;
 	
 	@CreationTimestamp
 	@Column(name = "ProcStartDateTime")
 	private LocalDateTime procStartDateTime;
 	
 	@Column(name = "UserEmail")
 	private String userEmail;
 	
 	
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "obs_processing", joinColumns = {
            @JoinColumn(name = "DataProcessingId", referencedColumnName = "DataProcessingId")}, inverseJoinColumns = {
            @JoinColumn(name = "ObservationId", table = "Observation", referencedColumnName = "ObservationId")})
    private Collection<Observation> observations = new ArrayList<>();
    
    
    @ManyToOne
    @JoinColumn(name="ObservationVIRACModeId")
    private ObservationVIRACMode viracParam;
    
    
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "data_proc_pipelines", joinColumns = {
            @JoinColumn(name = "DataProcessingId", referencedColumnName = "DataProcessingId")}, inverseJoinColumns = {
            @JoinColumn(name = "PipelineId", table = "Pipeline", referencedColumnName = "PipelineId")})

	private Collection<Pipeline> pipelines = new ArrayList<>();
	
	public void addPipelines(Pipeline ... inputPipelines)
	{
		for(Pipeline temp: inputPipelines)
		{
			if(!pipelines.contains(temp))
			{
				pipelines.add(temp);
			}
		}
		
	}
     
    public DataProcessing(String userEmail, ObservationVIRACMode viracParam, ArrayList<Observation> observations, ArrayList<Pipeline> pipelines) {
    	setUserEmail(userEmail);
    	setViracParam(viracParam);
    	setObservations(observations);
    	for(Pipeline temp: pipelines) {
    		addPipelines(temp);
    	}
    }
    
	 public void addObservation(Observation obs)
	 {
	  	if(!observations.contains(obs))
	   	{
	  		observations.add(obs);
	   	}
	 }
 	
 	

}

