/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sitescape.util.StringPool;
import com.sitescape.util.StringUtil;

public class SearchFieldResult {
	private Set<String> values=new HashSet<String>();
    private static String [] sample = new String[0];

    public void addValue(String value) {
    	values.add(value);	 
	}
    
 	public Set getValueSet() {
		return values;
	}
 	
 	public ArrayList getValueArray() {
 		return new ArrayList<String>(values);
 	}
 	
	public String getValueString() {
		return toString();
	}
	
    public String toString() {
        if ((values == null) || values.isEmpty()) return null;
        return StringUtil.merge((String[])values.toArray(sample), 
        		StringPool.COMMA+StringPool.SPACE);
     }
    
}
