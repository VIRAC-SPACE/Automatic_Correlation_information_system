package com.main.vlbi.models.planning;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ObservationVIRACMode")
@Table(name = "ObservationVIRACMode")
@Getter @Setter @NoArgsConstructor
public class ObservationVIRACMode {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ObservationVIRACModeId")
 	@Setter(value = AccessLevel.NONE)
    private Long ObservationVIRACModeId;
	
	@Column(name="ModeTitle")
	private String modeTitle;
	
	@Column(name="NumberOfChannels")
	private int numberOfChannels;
	
	@Column(name="Channels")
	private String channels;

	@JsonIgnore
	@OneToMany(mappedBy = "viracParam")
	private Collection<DataProcessing> dataProcessings;

	public ObservationVIRACMode(String modeTitle, int numberOfChannels, String channels) {
		super();
		this.modeTitle = modeTitle;
		this.numberOfChannels = numberOfChannels;
		this.channels = channels;
	}
	
	

}
