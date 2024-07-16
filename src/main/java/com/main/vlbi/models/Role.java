package com.main.vlbi.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name = "Role")
@Table(name = "roles")
public class Role implements Serializable {

    private static final long serialVersionUID = -416828985103738184L;
    @Id
    @Column(name = "role_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "role_type")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Collection<User> users = new ArrayList<>();
    
    public Role() {
        super();
    }

    public Role(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addUser(User user)
    {
    	if(!users.contains(user))
    	{
    		users.add(user);
    	}
    }
}
