package com.sitescape.ef.module.shared;

import java.util.Map;

import com.sitescape.ef.InternalException;

public class MapInputData implements InputDataAccessor {

	private Map source;
	
	public MapInputData(Map source) {
		this.source = source;
	}
	
	public String getSingleValue(String key) {
		Object result = source.get(key);
		if(result == null)
			return null;
		else if (result instanceof String) 
			return (String)result;
		else if (result instanceof String[]) 
			return ((String[]) result)[0];
		else
			throw new InternalException("Illgal value type [" + result.getClass() + "]");
	}

	public String[] getValues(String key) {
		Object result = source.get(key);
		if(result == null)
			return null;
		else if (result instanceof String[]) 
			return (String[])result;
		else if(result instanceof String)
			return new String[] { (String) result };
		else {
			throw new InternalException("Illgal value type [" + result.getClass() + "]");			
		}
	}

	public boolean exists(String key) {
		return source.containsKey(key);
	}

	public Object getSingleObject(String key) {
		return source.get(key);
	}
}
