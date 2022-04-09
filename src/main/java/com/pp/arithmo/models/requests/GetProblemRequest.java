package com.pp.arithmo.models.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pp.arithmo.enums.ComplexityType;
import com.pp.arithmo.enums.ProblemType;

import lombok.Data;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetProblemRequest {
	private ComplexityType cType;
	private ProblemType pType;
	
	@JsonProperty("cType")
	public void setCType(int type) {
		this.cType = ComplexityType.get(type);
	}
	
	@JsonProperty("pType")
	public void setPType(int type) {
		this.pType = ProblemType.get(type);
	}
}
