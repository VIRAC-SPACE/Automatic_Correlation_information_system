package com.main.vlbi.models.planning;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Station")
@Table(name = "Station")
@Getter @Setter @NoArgsConstructor
public class Station {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StationId")
 	@Setter(value = AccessLevel.NONE)
    private Long stationId;
	
	@Column(name="LongTitle")
	private String longTitle;
	
	@Column(name="ShortTitle")
	private String shortTitle;
	
	@Column(name="PopTitle")
	private String popTitle;
	
	@Column(name="CoordinatesLon")
	private String coordinatesLon;
	
	@Column(name="CoordinatesLat")
	private String coordinatesLat;
	
	@OneToMany(mappedBy = "station")
	@JsonIgnore
	private Collection<StationParameters> parameters;
	
	
	public Station(String shortTitle, String longTitle, String popTitle)
	{
		setLongTitle(longTitle);
		setShortTitle(shortTitle);
		setPopTitle(popTitle);
	}

	public Station(String shortTitle, String longTitle, String popTitle, String coordinatesLon, String coordinatesLat) {
		setLongTitle(longTitle);
		setShortTitle(shortTitle);
		setCoordinatesLat(coordinatesLat);
		setCoordinatesLon(coordinatesLon);
		setPopTitle(popTitle);
	}
	
	
	
}
