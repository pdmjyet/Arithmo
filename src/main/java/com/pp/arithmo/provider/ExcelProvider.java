package com.pp.arithmo.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.pp.arithmo.enums.ComplexityType;
import com.pp.arithmo.enums.ProblemType;
import com.pp.arithmo.models.LHS;
import com.pp.arithmo.models.Problem;
import com.pp.arithmo.models.RHS;
import com.pp.arithmo.models.RHS.Result;
import com.pp.arithmo.models.RHS.Result.ValueUnit;
import com.pp.arithmo.utils.CommonUtils;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;

//@Slf4j
public class ExcelProvider implements IProblemProvider{
	private static final Logger log = Logger.getLogger(ExcelProvider.class); 
	private final String PROBLEMS_JSON = "/Problems.json";
	private final String PROBLEMS = "problems";
	private final String PROBLEM_ID = "problemId";
	private final String PTYPE = "pType";
	private final String ACTIVE = "active";
	private final String LHSVALUE = "lhsvalue";
	private final String LHSUNIT = "lhsunit";
	private final String LHS_STR = "lhs";
	
	private HashMap<Integer, Problem> problems = new HashMap<>();
	private HashMap<ProblemType, List<Integer>> problemTypeProblemsMap = new HashMap<>(); 
	private HashMap<ComplexityType, List<ProblemTypePair>> complexityProblemsMap = new HashMap<>(); 
	
	public ExcelProvider() throws Exception{
		populateProblems();
	}
	
	public void addProblem(Problem prob, List<ComplexityType> ctypes) throws Exception {
		problems.put(prob.getId(), prob);
		List<Integer> plist = problemTypeProblemsMap.getOrDefault(prob.getPtype(), new ArrayList<Integer>());
		plist.add(prob.getId());
		problemTypeProblemsMap.put(prob.getPtype(), plist);
		for(ComplexityType ctype : ctypes) {
			List<ProblemTypePair> clist = complexityProblemsMap.getOrDefault(ctype, new ArrayList<ProblemTypePair>());
			clist.add(ProblemTypePair.builder().id(prob.getId()).pType(prob.getPtype()).build());
			complexityProblemsMap.put(ctype, clist);
		}
	}
	
	@Override
	public Problem problem(ComplexityType cType) {
		if(Objects.nonNull(cType) && complexityProblemsMap.containsKey(cType)) {
			List<ProblemTypePair> probs = complexityProblemsMap.get(cType);
			Integer                  id = probs.get(CommonUtils.genRandom(0, probs.size()-1)).getId();
			
			return problems.get(id);
		}
		return null;
	}
	
	public Problem problem(ComplexityType cType, ProblemType pType) {
		if(cType == null || pType == null || !complexityProblemsMap.containsKey(cType)) return null;
		
		List<ProblemTypePair> probs = complexityProblemsMap.get(cType);
		probs = probs.stream().filter(m -> m.getPType()==pType).collect(Collectors.toList());
		
		return probs.size() > 0 ? problems.get(probs.get(CommonUtils.genRandom(0, probs.size()-1)).getId()) 
								: null;
	}

	@Override
	public boolean evaluate(Integer probId, Result result) {
		Problem prob = problems.get(probId);
		if(Objects.isNull(prob)) return false;
		
		RHS rhs = prob.getRhs();
		for(Result res : rhs.getResults()) {
			if(res.getResult().size() != result.getResult().size()) continue;
			boolean found = true;
			for(ValueUnit vu : res.getResult()) {
				for(ValueUnit actVU : result.getResult())
				{
					if(actVU.equalsVU(vu)) break;
					found = false;
				}
				// if not found, we want to try other result list
				if(!found) break;
			}
			//if found, we want to exit the search
			if(found) return true;
		}
		
		return false;
	}
	
	private void populateProblems() throws URISyntaxException, Exception {
		try {
			
			InputStream is = getClass().getResourceAsStream(PROBLEMS_JSON);
			String json = new BufferedReader(
				      		new InputStreamReader(is, StandardCharsets.UTF_8))
				        	.lines()
				        	.collect(Collectors.joining("\n"));
			JsonObject problemsJson = new JsonObject(json);
			
			problemsJson.getJsonArray(PROBLEMS).forEach(m -> {
				JsonObject p = (JsonObject) m;
				Problem problem = Problem.builder().id(Integer.parseInt(p.getString(PROBLEM_ID)))
						.ptype(ProblemType.get(Integer.parseInt(p.getString(PTYPE))))
						.active(p.getString(ACTIVE).compareTo("1") == 0 ? true : false)
						.lhs(LHS.builder().value(p.getString(LHS_STR)).build())
						.build();
				//TODO handle value units empty and null values. Basically have null unit value if there is no unit.
				ValueUnit vu = ValueUnit.builder().value(p.getString(LHSVALUE)).unit(p.getString(LHSUNIT)).build();
				Result result = RHS.Result.builder().result(new ArrayList<ValueUnit>() {{add(vu);}}).build();
				problem.setRhs(RHS.builder().results(new ArrayList<Result>() {{add(result);}}).build());
				
				try {
					this.addProblem(problem, new ArrayList<ComplexityType>() {{add(ComplexityType.get(Integer.parseInt(p.getString("level"))));}});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			log.debug(json);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Data
	@Builder
	private static class ProblemTypePair{
		Integer id;
		ProblemType pType;
	}
	
}
