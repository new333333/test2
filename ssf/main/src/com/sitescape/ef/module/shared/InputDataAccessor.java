package com.sitescape.ef.module.shared;

public interface InputDataAccessor {

	/**
	 * Returns as a single string the value associated with the key.
	 * Returns <code>null</code> if no value exists for this key. 
	 * 
	 * @param key
	 * @return
	 */
	public String getSingleValue(String key);
	
	/**
	 * Returns as an array of string the value associated with the key.
	 * Returns <code>null</code> if no value exists for this key.
	 * 
	 * @param key
	 * @return
	 */
	public String[] getValues(String key);
	
	/**
	 * Returns <code>true</code> if the source contains a value for the
	 * specified key.  
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key);
	
	/**
	 * Returns as a single object the value associated with the key.
	 * If there are multiple values associated with the key, this will return
	 * the first value. Returns <code>null</code> if no value exists for this key.
	 * Sort of catch-all method, which provides a hook for passing arbitrary
	 * objects that are not necessarily strings. 
	 * 
	 * value. 
	 * @param key
	 * @return
	 */
	public Object getSingleObject(String key);
}
