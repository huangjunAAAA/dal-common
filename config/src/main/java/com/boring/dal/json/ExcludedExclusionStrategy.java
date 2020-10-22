package com.boring.dal.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;


public class ExcludedExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		Excluded excluded = f.getAnnotation(Excluded.class);
		if (excluded != null) return true;
		
		return false;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		Excluded excluded = clazz.getAnnotation(Excluded.class);
		if (excluded != null) return true;
		
		return false;
	}

}
