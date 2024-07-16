package com.main.vlbi.models.planning;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Source")
@Table(name = "Source")
@Getter @Setter @NoArgsConstructor
public class Source {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "SourceId")
	 	@Setter(value = AccessLevel.NONE)
	    private Long sourceId;
	 	
	 	@Column(name="Title")
	 	private String title;

	 	
	 	@Column(name="RightAscension")
	 	@Pattern(regexp = "[+-]?[0-9]{1,3}:[0-9]{1,3}:[0-9]{1,3}.[0-9]*")
	 	private String rightAscension;
	 	
	 	@Column(name="Declination")
	 	@Pattern(regexp = "[+-]?[0-9]{1,3}:[0-9]{1,3}:[0-9]{1,3}.[0-9]*")
	 	private String declination;
	 	
	 	
	 	
	 	@Column(name="Equinox")
	 	private Equinox equinox;
	 	
	 	@Column(name="Vref")
	 	private String vref;
	 	
	 	@Column(name="Vdef")
	 	private String vdef;
	 	
	 	@Column(name="Vel")
	 	private float vel;
	 	
	 	@OneToMany(mappedBy = "source")
	 	@JsonIgnore
	 	private Collection<Scan> sourceScans;
	 	
	 	
	 	public Source(String title, String ra, String dec, Equinox equinox)
	 	{
	 		setTitle(title);
	 		setRightAscension(ra);
	 		setDeclination(dec);
	 		setEquinox(equinox);
	 	}

		public Source(String title,String ra, String dec, Equinox equinox, String vref,
				String vdef, float vel) {
			setTitle(title);
	 		setRightAscension(ra);
	 		setDeclination(dec);
	 		setEquinox(equinox);
	 		setVdef(vdef);
	 		setVel(vel);
	 		setVref(vref);
		}
	 	

}
