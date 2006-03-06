package com.sitescape.ef.domain;

import java.util.HashSet;
import java.util.Set;
import com.sitescape.util.StringUtil;
/*
 * This is a wrapper object that converts a set of objects to
 * a comma separated string for storage.  This is a bit of a hack.
 * We are trying to avoid having long lists of elements, stored as individual
 * rows in the customAttribute table.
 * This is used to store lists of users.
 */
public class CommaSeparatedValue {
	private Set values=null;
    private static String [] sample = new String[0];
	
    public CommaSeparatedValue() {
    }
    public void setValue(String commaSeparatedString) {
		String [] value = StringUtil.split(commaSeparatedString);
		setValue(value);
	}
     public void setValue(String [] value) {
		this.values = new HashSet();
	    for (int i=0; i<value.length; ++i) {
	    	values.add(value[i]);
	    }	 
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
