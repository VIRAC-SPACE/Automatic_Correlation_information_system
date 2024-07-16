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
public class ObservationTitleAndDateTimeMultipleDTO {
    private ArrayList<ObservationTitleAndDateTimeDTO> observations;
    
    @JsonCreator
    public ObservationTitleAndDateTimeMultipleDTO(@JsonProperty("observations") ArrayList<ObservationTitleAndDateTimeDTO> observations) {
        this.observations = observations;
    }
}
