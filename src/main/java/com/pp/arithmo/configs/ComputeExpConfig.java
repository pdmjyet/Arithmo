package com.pp.arithmo.configs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComputeExpConfig {
	private String problemsJsonFiles;
}
