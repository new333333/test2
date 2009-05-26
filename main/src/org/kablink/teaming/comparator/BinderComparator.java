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

package org.kablink.teaming.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.kablink.teaming.domain.Binder;


/**
 * This comparator is used to produce a sorted collection based on title
 * @author Janet McCann
 *
 */
public class BinderComparator implements Comparator {
   	private Collator c;
   	private SortByField type;
	public enum SortByField {
		title ,
		searchTitle };

	public BinderComparator(Locale locale, SortByField type) {
		c = Collator.getInstance(locale);
		this.type = type;
	}
	public int compare(Object obj1, Object obj2) {
		Binder f1,f2;
		f1 = (Binder)obj1;
		f2 = (Binder)obj2;
				
		if (f1 == f2) return 0;
		if (f1==null) return -1;
		if (f2 == null) return 1;
		String t1,t2;
		if (type.equals(SortByField.title)) {
			t1 = f1.getTitle().toLowerCase();
			t2 = f2.getTitle().toLowerCase();
		} else {
			t1 = f1.getSearchTitle().toLowerCase();
			t2 = f2.getSearchTitle().toLowerCase();
			
		}
		int result=0;
		if ((t1!=null) && (t2 != null)) {
			result = c.compare(t1, t2);
			if (result != 0) return result;
		} else if ((t1==null) && (t2 != null)) return -1;
		else if ((t1 != null) && (t2 == null)) return 1;
		//if titles match - compare type
		result = f1.getEntityType().compareTo(f2.getEntityType());
		if (result != 0) return result;
		//if titles and type match - compare ids
		return f1.getId().compareTo(f2.getId());
	}
}
