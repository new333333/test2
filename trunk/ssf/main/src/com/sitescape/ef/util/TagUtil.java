package com.sitescape.ef.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sitescape.ef.domain.Tag;



public class TagUtil {

    /**
     * Convenience method to find all the unique tags 
     * in a set to return to the user.
     * 
     * @param allTags
     * @return
     */
    public static List uniqueTags(List allTags) {
    	List newTags = new ArrayList<Tag>();
    	HashMap tagMap = new HashMap();
    	for (Iterator iter=allTags.iterator(); iter.hasNext();) {
			Tag thisTag = (Tag)iter.next();
			if (tagMap.containsKey(thisTag.getName())) continue;
			tagMap.put(thisTag.getName(),thisTag);
			newTags.add(thisTag);
    	}
    	return newTags;    	
    }
}
