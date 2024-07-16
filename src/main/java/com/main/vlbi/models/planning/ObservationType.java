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
import com.main.vlbi.models.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "ObservationType")
@Table(name = "ObservationType")
@Getter @Setter @NoArgsConstructor
public class ObservationType {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ObservationTypeId")
 	@Setter(value = AccessLevel.NONE)
    private Long observationTypeId;
	
	@Column(name="TypeTitle")
	private String typeTitle;
	
	@Column(name="Note")
	private String note;

	@JsonIgnore
	@OneToMany(mappedBy = "obsType")
	private Collection<ObservationParams> observations;
	
	public ObservationType(String typeTitle, String note) {
		setNote(note);
		setTypeTitle(typeTitle);
	}
	
	public ObservationType(String typeTitle) {
		setTypeTitle(typeTitle);
	}
	
}
