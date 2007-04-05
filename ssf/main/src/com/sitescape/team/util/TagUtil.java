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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sitescape.team.domain.Tag;



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
