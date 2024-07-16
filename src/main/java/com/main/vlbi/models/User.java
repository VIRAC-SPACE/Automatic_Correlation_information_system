package com.main.vlbi.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.vlbi.models.planning.CorrelatorInfo;
import com.main.vlbi.models.planning.Observation;
import com.main.vlbi.models.planning.UserInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name = "User")
@Table(name = "user")
@Getter @Setter @NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 3263870433835795518L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true)
    @Email()
    @NotNull
    private String email;

    @JsonIgnore
    @Column(name = "password")
    @Length(min = 8)
    @NotNull
    private String password;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "last_name")
    @NotNull
    private String last_name;

    private String fullname = name + " " + last_name;

    @Column(name = "is_new")
    @NotNull
    private Boolean isNew;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", table = "roles", referencedColumnName = "role_id")})
    private Collection<Role> roles = new ArrayList<>();
    
    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Collection<Observation> observations = new ArrayList<>();
    
    @JsonIgnore
    @OneToOne (mappedBy = "user")
    private UserInfo userInfo;
  
    @JsonIgnore
    @OneToMany(mappedBy = "userAsCorshipAll")
    private Collection<CorrelatorInfo> corrInfo;
    

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User(String email, String name, String surname, Role role) {
        super();
        this.email = email;
        this.name = name;
        this.last_name = surname;
        this.roles.add(role);
    }
    
    public User(String email, String name, String surname, Role role, String password, Boolean isNew) {
        super();
        this.email = email;
        this.name = name;
        this.last_name = surname;
        this.roles.add(role);
        setPassword(password);
        setIsNew(isNew);
    }

    public void addRole(Role role)
    {
    	if(!roles.contains(role))
    	{
    		roles.add(role);
    	}
    }
    
    public void addObservation(Observation obs)
	{
		if(!observations.contains(obs))
		{
			observations.add(obs);
		}
	}
   
}
