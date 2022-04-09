package com.pp.arithmo.services;

import com.pp.arithmo.models.Problem;
import com.pp.arithmo.models.requests.EvaluateRequest;
import com.pp.arithmo.models.requests.GetProblemRequest;
import com.pp.arithmo.provider.ExcelProvider;
import com.pp.arithmo.provider.IProblemProvider;

import io.vertx.core.json.JsonObject;

public class ComputeExpressionService {

	private IProblemProvider provider;
	
	ComputeExpressionService() throws Exception {
		provider = new ExcelProvider();
		
	}

	public Problem problem(GetProblemRequest request) {
		return provider.problem(request.getCType());
	}

	public JsonObject evaluate(EvaluateRequest request) {
		return new JsonObject().put("result", provider.evaluate(request.getProblemId(), request.getActRes()));
	}
	
	public JsonObject getCollections() {
		return new JsonObject();
	}
}
