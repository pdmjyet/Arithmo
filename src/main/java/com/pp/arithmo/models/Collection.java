package com.pp.arithmo.models;

import com.pp.arithmo.enums.ComplexityType;
import com.pp.arithmo.enums.ProblemType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Collection {
	private Integer id;
	private String name;
	private ComplexityType cType;
	private ProblemType pType;
	private boolean active; 
}
