package com.sitescape.ef.module.shared;

import java.util.Map;


public class MapInputData implements InputDataAccessor {

	private Map source;
	
	public MapInputData(Map source) {
		this.source = source;
	}
	
	public String getSingleValue(String key) {
		Object result = source.get(key);
		if (result instanceof String) return (String)result;
		return ((String[]) source.get(key))[0];
	}

	public String[] getValues(String key) {
		Object result = source.get(key);
		if (result instanceof String[]) return (String[])result;
		String[] val = new String[1];
		val[0] = (String)result;
		return val;
	}

	public boolean exists(String key) {
		return source.containsKey(key);
	}
}
