package com.main.vlbi.models.planning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "GlobalParameters")
@Table(name = "GlobalParameters")
@Getter @Setter @NoArgsConstructor
public class GlobalParameters {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GlobalParameterId")
 	@Setter(value = AccessLevel.NONE)
    private Long globalParameterId;

	@Column(name = "Parameter")
	private String parameter;
	
	@Column(name = "ParamValue")
	private String paramValue;

	
	public GlobalParameters(String parameter, String paramValue) {
		setParameter(parameter);
		setParamValue(paramValue);
	}
	
	
}
