package com.pp.arithmo.models.requests;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pp.arithmo.models.Problem;
import com.pp.arithmo.models.RHS;
import com.pp.arithmo.models.RHS.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateRequest {
	@Nonnull
	Integer problemId;
	@Nonnull
	Result actRes;
}
