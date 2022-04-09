package com.pp.arithmo.enums;

import java.util.HashMap;
import java.util.Map;

public enum ProblemType {
	ADDITION(0),
	SUBTRACTION(1),
	MULTIPLICATION(2),
	DIVISION(3);
	
	private int value;
	
	private static final Map<Integer, ProblemType> lookup = new HashMap<>();
	
	static {
		for(ProblemType p : ProblemType.values()) {
			lookup.put(p.value, p);
		}
	}
	
	private ProblemType(int value) {
		this.value = value;
	}
	
	public static ProblemType get(Integer type) {
		return lookup.get(type);
	}
}
