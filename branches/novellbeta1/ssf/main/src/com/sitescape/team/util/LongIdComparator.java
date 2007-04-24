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

import java.util.Comparator;

import com.sitescape.team.domain.PersistentLongId;

public class LongIdComparator implements Comparator {
	public int compare(Object obj1, Object obj2) {
		PersistentLongId f1,f2;
		f1 = (PersistentLongId)obj1;
		f2 = (PersistentLongId)obj2;
				
		if (f1 == f2) return 0;
		if (f1==null) return 1;
		if (f2 == null) return -1;
		return f1.getId().compareTo(f2.getId());
	}

}
