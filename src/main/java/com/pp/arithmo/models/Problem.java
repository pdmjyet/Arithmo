package com.pp.arithmo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pp.arithmo.enums.ComplexityType;
import com.pp.arithmo.enums.ProblemType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Problem {
	int id;
	ProblemType ptype;
	RHS rhs;
	LHS lhs;
	boolean active;
}
