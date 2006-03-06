package com.sitescape.ef.search;

import java.util.HashSet;
import java.util.Set;

import com.sitescape.util.StringUtil;

public class SearchFieldResult {
	private Set values=new HashSet();
    private static String [] sample = new String[0];

    public void addValue(String value) {
    	values.add(value);	 
	}
 	public Set getValueSet() {
		return values;
	}
	public String getValueString() {
		return toString();
	}
    public String toString() {
        if ((values == null) || values.isEmpty()) return null;
        return StringUtil.merge((String[])values.toArray(sample));
     }
    
}
