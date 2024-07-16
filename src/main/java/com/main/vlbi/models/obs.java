package com.main.vlbi.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter @Setter @NoArgsConstructor
public class obs {
    @Getter @Setter private String correlator;
    @Getter @Setter private String start_data;
    @Getter @Setter private String exp_code;
    @Getter @Setter private String project;
}
