package com.sitescape.team.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.sitescape.team.domain.FileAttachment;

public class FileAttachmentComparator implements Comparator {
   	private Collator c;
   	private SortByField type;
	public enum SortByField {
		name ,
		createDateDescending,
		createDateAscending};

	public FileAttachmentComparator(Locale locale, SortByField type) {
		c = Collator.getInstance(locale);
		this.type = type;
	}
	public int compare(Object obj1, Object obj2) {
		FileAttachment f1,f2;
		f1 = (FileAttachment)obj1;
		f2 = (FileAttachment)obj2;
				
		if (f1 == f2) return 0;
		if (f1==null) return -1;
		if (f2 == null) return 1;
		if (type.equals(SortByField.name)) {
			int result = f1.getFileItem().getName().compareToIgnoreCase(f2.getFileItem().getName());
			if (result != 0) return result;
		} else if (type == SortByField.createDateDescending) {
			int result = f2.getCreation().compareDate(f1.getCreation());
			if (result != 0) return result;
		} else {
			int result = f1.getCreation().compareDate(f2.getCreation());
			if (result != 0) return result;
			
		}
		// compare ids
		return f1.getId().compareTo(f2.getId());
	}
}
