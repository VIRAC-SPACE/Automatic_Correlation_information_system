package com.main.vlbi.models.planning;



import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Project")
@Table(name = "Project")
@Getter @Setter @NoArgsConstructor
public class Project {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectId")
 	@Setter(value = AccessLevel.NONE)
    private Long projectId;
	
	@Column(name="Title")
	@Size(min = 3, max = 300)
	private String title;
	
	
	@JsonIgnore
    @ManyToMany(mappedBy = "projects")
    private Collection<Observation> observations = new ArrayList<>();
	
	public Project(String title) {
		this.title = title;
	}
	
	
	public void addObservations(Observation ... inputObs)
	{
		for(Observation temp: inputObs)
		{
			if(!observations.contains(temp))
			{
				observations.add(temp);
			}
		}
		
	}
}
