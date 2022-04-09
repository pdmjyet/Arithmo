package com.pp.arithmo.provider;

import com.pp.arithmo.enums.ComplexityType;
import com.pp.arithmo.models.Problem;
import com.pp.arithmo.models.RHS;
import com.pp.arithmo.models.RHS.Result;

public interface IProblemProvider {
	Problem problem(ComplexityType cType);
	boolean evaluate(Integer probId, Result result);
}
