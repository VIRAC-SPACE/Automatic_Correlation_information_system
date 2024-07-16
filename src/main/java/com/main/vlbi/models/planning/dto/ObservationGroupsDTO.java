package com.main.vlbi.models.planning.dto;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ObservationGroupsDTO {
    private ArrayList<ObservationOneGroupDTO> groups;
    
    @JsonCreator
    public ObservationGroupsDTO(@JsonProperty("groups") ArrayList<ObservationOneGroupDTO> groups) {
        this.groups = groups;
    }
}
