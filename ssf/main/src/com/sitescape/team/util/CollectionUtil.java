
package com.sitescape.team.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.domain.PersistentLongId;
import com.sitescape.ef.domain.PersistentStringId;
import com.sitescape.util.StringUtil;
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
	public static String toCommaIds(Collection values) {
		if ((values == null) || values.isEmpty()) return null;
		String[] buf = new String[values.size()];
		int i=0;
		for (Iterator iter = values.iterator(); iter.hasNext();) {
			Object val = iter.next();
			if (val instanceof PersistentLongId) {
				buf[i] = ((PersistentLongId)val).getId().toString();
			} else if (val instanceof PersistentStringId) {
				buf[i] = ((PersistentStringId)val).getId();		
			} else {
				buf[i] = val.toString();
			}
			++i;
		}
        return StringUtil.merge(buf);
	}
}