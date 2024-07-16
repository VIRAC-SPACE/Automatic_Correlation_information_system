package com.main.vlbi.models.planning;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.vlbi.models.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Observation")
@Table(name = "Observation")
@Getter @Setter @NoArgsConstructor
public class Observation {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ObservationId")
 	@Setter(value = AccessLevel.NONE)
    private Long observationId;
	
	@Column(name="Expcode")
	@Pattern(regexp = "[a-zA-Z0-9]{2,6}")
	private String expcode;
	
	@Min(0)
	@Column(name="DurationInHours")
	private float durationInHours;
	
	
	@Column(name="Repeating")
	private boolean repeating = false;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "obs_users", joinColumns = {
            @JoinColumn(name = "ObservationId", referencedColumnName = "ObservationId")}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", table = "user", referencedColumnName = "user_id")})

	private Collection<User> users = new ArrayList<>();
	
	@Column(name = "DateTimeLST")
	private LocalDateTime  dateTimeLST;
	
	@Column(name = "DateTimeUTC")
	private LocalDateTime  dateTimeUTC;
	
	@Column(name = "Antennas")
	private String antennas;
	 
	@ManyToOne
	@JoinColumn(name = "CorrelatorInfoId")
	private CorrelatorInfo corrInfo;
	
	
	@ManyToMany(mappedBy = "observations")
	private Collection<Scan> scans = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="ObservationParamsId")
	private ObservationParams param;
	
	
	@ManyToMany(mappedBy = "observations")
	private Collection<DataProcessing> dataProcessings = new ArrayList<>();
	
	
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "obs_project", joinColumns = {
            @JoinColumn(name = "ObservationId", referencedColumnName = "ObservationId")}, inverseJoinColumns = {
            @JoinColumn(name = "ProjectId", table = "Project", referencedColumnName = "ProjectId")})
	private Collection<Project> projects = new ArrayList<>();
	
	 
	
	
	
	@OneToOne(mappedBy = "observation")
	private ObsAcceptance acceptance;
	

	@ManyToOne
	@JoinColumn(name="GroupObsId")
	private GroupObs groupObs;
	
	public void addProject(Project ... inputProjects)
	{
		for(Project temp: inputProjects)
		{
			if(!projects.contains(temp))
			{
				projects.add(temp);
			}
		}
		
	}
	
	public void addDataProcessing(DataProcessing ... inputDataProcessing)
	{
		for(DataProcessing temp: inputDataProcessing)
		{
			if(!dataProcessings.contains(temp))
			{
				dataProcessings.add(temp);
			}
		}
		
	}	
	public void addUsers(User ... inputUsers)
	{
		for(User temp: inputUsers)
		{
			if(!users.contains(temp))
			{
				users.add(temp);
			}
		}
		
	}
	
	public void addScans(Scan scan)
	 {
	  	if(!scans.contains(scan))
	   	{
	  		scans.add(scan);
	   	}
	 }
	

	public Observation(CorrelatorInfo corrInfo1, LocalDateTime datetimeLST, LocalDateTime datetimeUTC,  String expcode,GroupObs group, ObservationParams observationParams, User ... users ) {
		for(User tempU: users)
			addUsers(tempU);
		setCorrInfo(corrInfo1);
		setDateTimeLST(datetimeLST);
		setDateTimeUTC(datetimeUTC);
		setExpcode(expcode);
		setParam(observationParams);
		setGroupObs(group);
	}
}
