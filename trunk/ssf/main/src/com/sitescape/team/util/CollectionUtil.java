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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/**
 * @author Janet McCann
 *
 */
public class CollectionUtil {

	/**
	 * Merge 2 collections so that duplicates do not get added. This is 
	 * necessary when we are using Lists but want Set semantics.  This occurs in
	 * our hibernate mappings for associations.  We don't use sets, cause it forces
	 * the lazy-loading of assoctions when we add new members.  The hibernate proxies do this
	 * to ensure duplicates are not added.  
	 * 
	 */
	public static List mergeAsSet(List oColl, Collection nColl) {
      	if (oColl == null) {
       		if (nColl instanceof List)
       			oColl = (List)nColl;
       		else
       			oColl = new ArrayList(nColl);
       		return oColl;
       	}
       	if ((nColl == null) || nColl.isEmpty()) {
    		oColl.clear();
    		return oColl;
    	}
       	Set coll = differences(oColl,nColl);
       	oColl.removeAll(coll);
       	coll = differences(nColl, oColl);
       	oColl.addAll(coll);
       	
   	return oColl;
	}
	//return list of members in 1 but not in 2
	public static Set differences(Collection coll1, Collection coll2) {
	   	Object o;
	   	if (coll1 == null) return new HashSet();
	   	if (coll2 == null) 	return new HashSet(coll1);
	   	Set coll = new HashSet();
	   	
    	for (Iterator iter=coll1.iterator(); iter.hasNext();) {
    		o=iter.next();
			if (!coll2.contains(o)) {
				coll.add(o);
    		}
    	}
    	return coll;
		
	}

}