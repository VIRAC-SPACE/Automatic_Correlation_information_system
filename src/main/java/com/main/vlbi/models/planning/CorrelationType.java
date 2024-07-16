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

@Entity(name = "CorrelationType")
@Table(name = "CorrelationType")
@Getter @Setter @NoArgsConstructor
public class CorrelationType {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CorrelationTypeId")
 	@Setter(value = AccessLevel.NONE)
    private Long CorrelationTypeId;
	
	@Column(name="TypeTitle")
	private String typeTitle;
	
	@JsonIgnore
	@OneToMany(mappedBy = "corrType")
	private Collection<CorrelatorInfo> corrInfo;
	
	public CorrelationType(String typeTitle) {
		setTypeTitle(typeTitle);
	}
	
}
