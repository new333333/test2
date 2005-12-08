
package com.sitescape.ef.module.binder;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.sitescape.ef.domain.Binder;

/**
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
		if (f1==null) return 1;
		if (f2 == null) return -1;
		return c.compare(f1.getTitle(), f2.getTitle());
	}
}
