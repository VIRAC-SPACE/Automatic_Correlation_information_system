package com.main.vlbi.models.planning.dto;


import java.util.ArrayList;

import com.main.vlbi.models.planning.Pipeline;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataProcessingDTO {
   private String userEmail;
   private String passType;
   private ArrayList<Pipeline> pipelines;
   private ArrayList<String> expCodes;
}

