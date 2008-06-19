package com.sitescape.team.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;


public class StringComparator implements Comparator {
   	private Collator c;
   	private Locale locale;

	public StringComparator(Locale locale) {
		c = Collator.getInstance(locale);
		this.locale = locale;
	}
	public int compare(Object obj1, Object obj2) {
		String s1,s2;
		s1 = (String)obj1;
		s2 = (String)obj2;
				
		if (s1 == s2) return 0;
		if (s1==null) return -1;
		if (s2 == null) return 1;
		String l1 = s1.toLowerCase(locale);
		String l2 = s2.toLowerCase(locale);
		int result=0;
		result = c.compare(l1, l2);
		//strings compare the same, but represent different objects
		if (result != 0) return result;
		return breakTie(s1, s2);
	}
	protected int breakTie(String s1, String s2) {
		return -1;
	}
}
