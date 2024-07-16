package com.main.vlbi.models.planning;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.vlbi.models.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "CorrelatorInfo")
@Table(name = "CorrelatorInfo")
@Getter @Setter @NoArgsConstructor
public class CorrelatorInfo {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CorrelatorInfoId")
 	@Setter(value = AccessLevel.NONE)
    private Long correlatorInfoId;
	
	@Column(name = "Coravg")
	private float coravg;
	
	@Column(name = "Corchan")
	private int corchan;
	
	@Column(name = "Cornant")
	private int cornant;
	
	@Column(name = "Corpol")
	private boolean corpol;
	
	@Column(name = "Corwtfn")
	private String corwtfn;
	
	@Column(name = "Corsrcs")
	private String corsrcs;
	
	@Column(name = "Cortape")
	private String cortape;
	
	@Column(name = "CornoteAll")
	private String cornoteAll;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userAsCorshipAll;
	
		
	@ManyToOne
	@JoinColumn(name="CorrelationTypeId")
	private CorrelationType corrType;

	@OneToMany(mappedBy = "corrInfo")
	@JsonIgnore
	private Collection<Observation> observations;
	

	public CorrelatorInfo(float coravg, int corchan, int cornant, boolean corpol, String corwtfn, String corsrcs,
			String cortape, String cornoteAll, User userAsCorshipAll, CorrelationType corrType) {
		setCoravg(coravg);
		setCorchan(corchan);
		setCornant(cornant);
		setCorpol(corpol);
		setCorwtfn(corwtfn);
		setCorsrcs(corsrcs);
		setCorrType(corrType);
		setCortape(cortape);
		setCornoteAll(cornoteAll);
		setUserAsCorshipAll(userAsCorshipAll);
	}
	
	
	

}
