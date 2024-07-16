package com.main.vlbi.models.planning;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.vlbi.models.Role;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "StationParameters")
@Table(name = "StationParameters")
@Getter @Setter @NoArgsConstructor
public class StationParameters {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stationParametersId")
 	@Setter(value = AccessLevel.NONE)
    private Long stationParametersId;
	
	@Column(name="RecordingFormat")
	private String recordingFormat;
	
	@Column(name="FirstLo")
	private String firstLo;
	
	@Column(name="IfChan")
	private String ifChan;
	
	@Column(name="Bbc")
	private String bbc;
	
	@ManyToOne
	@JoinColumn(name="StationId")
	private Station station;

	@JsonIgnore
	@ManyToMany(mappedBy = "statParams")
	private Collection<ObservationParams> observations = new ArrayList<>();
	
	
	
	public StationParameters(String recordingFormat, String firstLo, String ifChan,
			String bbc, Station station) {
		setRecordingFormat(recordingFormat);
		setFirstLo(firstLo);
		setIfChan(ifChan);
		setBbc(bbc);
		setStation(station);
	}
	
	  public void addObservation(ObservationParams obs)
	    {
	    	if(!observations.contains(obs))
	    	{
	    		observations.add(obs);
	    	}
	    }
	
	
}
