package com.pp.arithmo.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplexityType {
	L0(0),
	L1(1),
	L2(2),
	L3(3);
	
	private static final Map<Integer, ComplexityType> lookup = new HashMap<>();
	
	static {
		for(ComplexityType p : ComplexityType.values()) {
			lookup.put(p.value, p);
		}
	}
	private int value;
	
	private ComplexityType(int value) {
		this.value = value;
	}
	
	public static ComplexityType get(Integer type) {
		return lookup.get(type);
	}

	@JsonValue
	public int getValue() {
		return this.value;
	}
}
