package com.main.vlbi.models.planning;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.vlbi.models.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ObservationParams")
@Table(name = "ObservationParams")
@Getter @Setter @NoArgsConstructor
public class ObservationParams {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ObservationParamsId")
 	@Setter(value = AccessLevel.NONE)
    private Long observationParamsId;
	
	@ManyToMany
	@JoinTable(name = "obs_stations", joinColumns = {
            @JoinColumn(name = "ObservationParamsId", referencedColumnName = "ObservationParamsId")}, inverseJoinColumns = {
            @JoinColumn(name = "stationParametersId", table = "StationParameters", referencedColumnName = "stationParametersId")})

	private Collection<StationParameters> statParams = new ArrayList<>();
	

	
	@Column(name="Restfreq")
	private String restfreq;
		
	@Column(name="Lineset")
	private String lineset;
	
	@Column(name="Linename")
	private String linename;
	
	@Column(name="IsDoppler")
	private boolean isDoppler;
	
	@Column(name="Prestart")
	private int prestart;
	
	@Column(name="Band")
	private String band;
	
	@Min(2)
	@Column(name="Nchan")
	private int nchan;
	
	@Min(2)
	@Column(name="Bits")
	private int bits;
	
	@Column(name="BbFilter")
	private float bbFilter;
	
	@Column(name="FreqRef")
	private float freqRef;
	
	@Column(name="FreqOff")
	private String freqOff;
	
	@Column(name="Pcal")
	private boolean pcal;
	
	@Column(name="Barrel")
	private String barrel;
	
	@ManyToOne
	@JoinColumn(name="ObservationModeId")
	private ObservationMode obsMode;
	
	@Column(name="ObsModeNote")
	private String obsModeNote;
	
	@ManyToOne
	@JoinColumn(name="ObservationTypeId")
	private ObservationType obsType;
	
	@Column(name="Note1")
	private String note1;
	
	@Min(0)
	@Column(name="Version")
	private int version;
	
	@Column(name="Expt")
	private String expt;
	
	@JsonIgnore
	@OneToMany(mappedBy = "param")
	private Collection<Observation> observations;
	
	public void addStatParams(StationParameters ... inputStatParams)
	{
		for(StationParameters temp: inputStatParams)
		{
			if(!statParams.contains(temp))
			{
				statParams.add(temp);
			}
		}
	}
	
	
	
	
	
}
