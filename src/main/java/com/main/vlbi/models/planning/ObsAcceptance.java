package com.main.vlbi.models.planning;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ObsAcceptance")
@Table(name = "ObsAcceptance")
@Getter @Setter @NoArgsConstructor
public class ObsAcceptance {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ObsAcceptanceId")
 	@Setter(value = AccessLevel.NONE)
    private Long obsAcceptanceId;
	
    @Column(name = "Accept1")
	private boolean accept1 = false;
    
    @Column(name = "Accept2")
   	private boolean accept2 = false;
    
    
    @Column(name = "Accept3")
   	private boolean accept3 = false;
    
    @Column(name = "Accept4")
   	private boolean accept4 = false;
    
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "ObservationId")
    private Observation observation;

	public ObsAcceptance(Observation observation) {
		super();
		this.observation = observation;
	}
    
    
    
    
	
	
	
	
	

}
