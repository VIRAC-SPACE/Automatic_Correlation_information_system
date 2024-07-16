package com.main.vlbi.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "Experiment")
@Table(name = "experiments")
public class Experiment implements Serializable {
    private static final long serialVersionUID = 2551986708692450936L;
    @Id
    @Column(name = "experiment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "experiment_name", unique = true)
    private String name;

    @NotNull
    @Column(name = "experiment_date")
    private String experiment_date;

    @NotNull
    @Column(name = "experiment_type")
    private String type;

    @Column(name = "corr_type")
    private String corr_type;

    public Experiment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExperiment_date() {
        return experiment_date;
    }

    public void setExperiment_date(String experiment_date) {
        this.experiment_date = experiment_date;
    }

    public String getExperiment_type() {
        return type;
    }

    public void setSFXC() {
        this.type = "SFXC";
    }

    public void setKANA() {
        this.type = "KANA";
    }

    public void setCorr_type(String corr_type) {
        this.corr_type = corr_type;
    }
    public String getCorr_type() {
        return this.corr_type; }

}
