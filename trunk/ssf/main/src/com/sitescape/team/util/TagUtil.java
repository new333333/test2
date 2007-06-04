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
package com.sitescape.team.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.User;


public class TagUtil {

    /**
     * Convenience method to find all the unique tags 
     * in a set to return to the user.
     * 
     * @param allTags
     * @return
     */
    public static Map<String, SortedSet<Tag>> uniqueTags(List<Tag> tags) {
        Map results = new HashMap();
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new TagComparator(user.getLocale());
 
        SortedSet publicTags = new TreeSet(c);
        SortedSet privateTags = new TreeSet(c);
        for (Tag t: tags) {
        	if (t.isPublic()) {
       			publicTags.add(t);
        	} else {
       			privateTags.add(t);
         	}
        }
        
        results.put(ObjectKeys.COMMUNITY_ENTITY_TAGS, publicTags);
        results.put(ObjectKeys.PERSONAL_ENTITY_TAGS, privateTags);
        return results;
    }
    
	public static class TagComparator implements Comparator {
       	private Collator c;
       	boolean unqiueName;

    	public TagComparator(Locale locale) {
    		c = Collator.getInstance(locale);
    	}
    	public int compare(Object obj1, Object obj2) {
    		Tag f1,f2;
    		f1 = (Tag)obj1;
    		f2 = (Tag)obj2;
    				
    		if (f1 == f2) return 0;
    		if (f1==null) return -1;
    		if (f2 == null) return 1;
    		String t1,t2;
    		t1 = f1.getName().toLowerCase();
    		t2 = f2.getName().toLowerCase();
    		int result=0;
    		if ((t1!=null) && (t2 != null)) {
    			result = c.compare(t1, t2);
    			if (result != 0) return result;
    		} else if ((t1==null) && (t2 != null)) return -1;
    		else if ((t1 != null) && (t2 == null)) return 1;
    		//if want to remove duplicate tags, return equal
    		return 0;
    	}
	}
}
