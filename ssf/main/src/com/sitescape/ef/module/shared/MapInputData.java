package com.sitescape.ef.module.shared;

import java.util.Map;

import com.sitescape.ef.module.folder.InputDataAccessor;

public class MapInputData implements InputDataAccessor {

	private Map source;
	
	public MapInputData(Map source) {
		this.source = source;
	}
	
	public String getSingleValue(String key) {
		return ((String[]) source.get(key))[0];
	}

	public String[] getValues(String key) {
		return (String[]) source.get(key);
	}

	public boolean exists(String key) {
		return source.containsKey(key);
	}
}
