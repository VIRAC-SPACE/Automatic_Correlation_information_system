package com.main.vlbi.models.planning;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Scan")
@Table(name = "Scan")
@Getter @Setter @NoArgsConstructor
public class Scan {
 	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScanId")
 	@Setter(value = AccessLevel.NONE)
    private Long scanId;
 	
 	@Column(name="Duration")
 	private int duration;
 	
 	@Column(name="Gap")
 	private int gap;
 	
 	@Column(name="Dopsrc")
 	private String dopsrc;
 	
 	@ManyToOne
 	@JoinColumn(name = "SourceId")
 	private Source source;
 	
 	@JsonIgnore
 	@ManyToMany(fetch = FetchType.EAGER)
 	@JoinTable(name = "scan_obs", joinColumns = {
            @JoinColumn(name = "ScanId", referencedColumnName = "ScanId")}, inverseJoinColumns = {
            @JoinColumn(name = "ObservationId", table = "Observation", referencedColumnName = "ObservationId")})
 	private Collection<Observation> observations = new ArrayList<>();
 	
 	
	public Scan(int duration, int gap, String dopsrc, Source source) {
		this.duration = duration;
		this.gap = gap;
		this.dopsrc = dopsrc;
		this.source = source;
	}
 	
	 public void addObservation(Observation obs)
	 {
	  	if(!observations.contains(obs))
	   	{
	  		observations.add(obs);
	   	}
	 }
 	
}
