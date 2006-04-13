package com.sitescape.ef.module.shared;


/**
 * An implementation of <code>InputDataAccessor</code> interface
 * where input data is empty. 
 * 
 * @author jong
 *
 */
public class EmptyInputData implements InputDataAccessor{

	public String getSingleValue(String key) {
		return null;
	}

	public String[] getValues(String key) {
		return null;
	}

	public boolean exists(String key) {
		return false;
	}

}
