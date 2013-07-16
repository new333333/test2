/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.lucene.util.LanguageTaster;



public class TagUtil {

    /**
     * Convenience method to find all the unique tags 
     * in a set to return to the user.
     * 
     * @param allTags
     * @return
     */
    public static Map<String, List<Tag>> splitTags(Collection<Tag> tags) {
        Map results = new HashMap();
  
        List publicTags = new ArrayList();
        List privateTags = new ArrayList();
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
    
    public static Map<String, SortedSet<Tag>> uniqueTags(Collection<Tag> tags) {
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
	public static Collection<String> buildTags(String newTag) {
		ArrayList<String> newTags = new ArrayList();
		String []tags = null;
		String lang = LanguageTaster.taste(newTag.toCharArray());
		if (lang.equalsIgnoreCase(LanguageTaster.CJK)) {
			tags = new String[1];
			tags[0] = newTag;
		} else {
			newTag = newTag.replaceAll("[\\p{Punct}]", " ").trim().replaceAll("\\s+"," ");
			tags = newTag.split(" ");
		}
		for (int i=0; i<tags.length; ++i) {
			String tagName = tags[i].trim();
			if (tagName.length() > ObjectKeys.MAX_TAG_LENGTH) {
				//Truncate the tag so it fits in the database field
				tagName = tagName.substring(0, ObjectKeys.MAX_TAG_LENGTH);
			}
			newTags.add(tagName);
		}
		return newTags;
	}
}
