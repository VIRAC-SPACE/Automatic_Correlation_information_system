package com.main.vlbi.models.planning;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.vlbi.models.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Pipeline")
@Table(name = "Pipeline")
@Getter @Setter @NoArgsConstructor
public class Pipeline {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PipelineId")
 	@Setter(value = AccessLevel.NONE)
    private Long pipelineId;
	
	
	@Column(name = "title")
	private String title;
	
	
	
	@JsonIgnore
    @ManyToMany(mappedBy = "pipelines")
    private Collection<DataProcessing> dataProcessings = new ArrayList<>();	
	public Pipeline(String title) {
		this.title = title;
	}
	
	
	public void addDataProcessing(DataProcessing ... inputDataProcessing)
	{
		for(DataProcessing temp: inputDataProcessing)
		{
			if(!dataProcessings.contains(temp))
			{
				dataProcessings.add(temp);
			}
		}
		
	}	
}
