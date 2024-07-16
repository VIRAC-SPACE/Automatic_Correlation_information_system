package com.main.vlbi.models.planning;

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

@Entity(name = "ObservationMode")
@Table(name = "ObservationMode")
@Getter @Setter @NoArgsConstructor
public class ObservationMode {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ObservationModeId")
 	@Setter(value = AccessLevel.NONE)
    private Long observationModeID;
	
	@Column(name="Title")
	private String title;
	
	@Column(name="Pol")
	private String pol;
	
	@Column(name="NetSide")
	private String netSide;

	@JsonIgnore
	@OneToMany(mappedBy = "obsMode")
	private Collection<ObservationParams> observations;
	
	public ObservationMode(String title, String pol, String netSide) {
		setTitle(title);
		setPol(pol);
		setNetSide(netSide);
	}
	
	
	
}
