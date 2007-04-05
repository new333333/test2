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

package com.sitescape.team.domain;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * @author Peter Hurley
 *
 */
public class HistoryMapComparator implements Comparator {
	public int compare(Object obj1, Object obj2) {
		Map.Entry f1,f2;
		f1 = (Map.Entry)obj1;
		f2 = (Map.Entry)obj2;
				
		if (f1 == f2) return 0;
		if (f1 == null) return 1;
		if (f2 == null) return -1;
		return ((Date) f1.getValue()).compareTo((Date) f2.getValue());
	}
}
