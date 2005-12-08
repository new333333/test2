
package com.sitescape.ef.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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
	   	Object o;
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
 		//remove members not in new set
    	for (Iterator iter=oColl.iterator(); iter.hasNext();) {
    		o=iter.next();
			if (!nColl.contains(o)) {
				oColl.remove(o);
    		}
    	}
    	//add new members
    	for (Iterator iter=nColl.iterator(); iter.hasNext();) {
    		o=iter.next();
			if (!oColl.contains(o)) {
				oColl.add(o);
    		}
    	}
    	return oColl;
	}
	public static Set differences(Collection coll1, Collection coll2) {
	   	Object o;
	   	if (coll1 == null) return new HashSet();
	   	if (coll2 == null) 	return new HashSet(coll1);
	   	Set coll = new HashSet();
	   	
		//return list of members in 1 but not in 2
    	for (Iterator iter=coll1.iterator(); iter.hasNext();) {
    		o=iter.next();
			if (!coll2.contains(o)) {
				coll.add(o);
    		}
    	}
    	return coll;
		
	}
}