package com.main.vlbi.models.planning;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "GroupObs")
@Table(name = "GroupObs")
@Getter @Setter @NoArgsConstructor
public class GroupObs {
 	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GroupObsId")
 	@Setter(value = AccessLevel.NONE)
    private Long groupId;
 	
 	@Column(name = "GroupObsTitle")
 	private String groupObsTitle;

 	@Column(name = "Lstime")
 	private LocalTime lstime; 	
 	@Column(name="Duration")
 	private float duration;

	@Column(name="Counter")
 	private int counter;

	@JsonIgnore
 	@OneToMany(mappedBy = "groupObs")
 	private Collection<Observation> observations;
 	
 	public GroupObs(String groupTitle) {
 		setGroupObsTitle(groupTitle);
 	}

}

