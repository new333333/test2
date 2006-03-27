
package com.sitescape.ef.module.binder;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.sitescape.ef.domain.Binder;

/**
 * This comparator is used to produce a sorted collection based on title
 * @author Janet McCann
 *
 */
public class BinderComparator implements Comparator {
   	private Collator c;
	public BinderComparator(Locale locale) {
		c = Collator.getInstance(locale);		
	}
	public int compare(Object obj1, Object obj2) {
		Binder f1,f2;
		f1 = (Binder)obj1;
		f2 = (Binder)obj2;
				
		if (f1 == f2) return 0;
		if (f1==null) return -1;
		if (f2 == null) return 1;
		String t1 = f1.getTitle();
		String t2 = f2.getTitle();
		int result=0;
		if ((t1!=null) && (t2 != null)) {
			result = c.compare(f1.getTitle(), f2.getTitle());
			if (result != 0) return result;
		} else if ((t1==null) && (t2 != null)) return -1;
		else if ((t1 != null) && (t2 == null)) return 1;
		//if titles match - compare type
		result = f1.getType().compareTo(f2.getType());
		if (result != 0) return result;
		//if titles and type match - compare ids
		return f1.getId().compareTo(f2.getId());
	}
}
