/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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