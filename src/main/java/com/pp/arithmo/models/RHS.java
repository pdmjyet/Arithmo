package com.pp.arithmo.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RHS {
	List<Result> results = new ArrayList<Result>();
	
	@Data
	@Builder
	@JsonIgnoreProperties(ignoreUnknown = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Result{
		List<ValueUnit> result = new ArrayList<>();
				
		@Data
		@Builder
		@JsonIgnoreProperties(ignoreUnknown = true)
		@NoArgsConstructor
		@AllArgsConstructor
		public static class ValueUnit{
			String value;
			String unit;
			
			public boolean equalsVU(ValueUnit vu) {
				if(vu.getUnit()==null || vu.getUnit().trim().isEmpty()) vu.setUnit(StringUtils.EMPTY);
				return StringUtils.compare(this.value, vu.getValue()) == 0 &&
						StringUtils.compare(this.unit, vu.getUnit()) == 0;
			}
			
		}
	}
}
