package com.sitescape.ef.module.file.impl;

import java.util.HashSet;
import java.util.Set;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.util.FileUploadItem;

/**
 * Provides transaction-enabled access to the metadata associated with file 
 * attachments.
 * 
 * @author jong
 *
 */
public class FileModuleMetadata {
	
	private CoreDao coreDao;

    protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

    public void writeFilePart2(Binder binder, Entry entry, FileUploadItem fui,
    		FileAttachment fAtt, boolean isNew) {	
		if(isNew) {
    		// Since file attachment is stored into custom attribute using
    		// its id value rather than association, this new object must
    		// be persisted here just in case it is to be put into custom
    		// attribute down below. 
    		getCoreDao().save(fAtt);    		
    	}

    	if (fui.getType() == FileUploadItem.TYPE_FILE) {
			// Find custom attribute by the attribute name. 
			Set fAtts = null;
			CustomAttribute ca = entry.getCustomAttribute(fui.getName());
			if(ca != null)
				fAtts = (Set) ca.getValue();
			else
				fAtts = new HashSet();

			// Simply because the file already exists for the entry does not 
			// mean that it is known through this particular data element
			// (i.e., custom attribute). So we need to make sure that it is
			// made visible through this element.
			fAtts.add(fAtt); // If it is already in the set, it will have no effect
			
			if(ca != null)
				ca.setValue(fAtts);
			else
				entry.addCustomAttribute(fui.getName(), fAtts);
		} else if (fui.getType() == FileUploadItem.TYPE_ATTACHMENT) {
			// Add the file attachment to the entry only if new file. 
			if(isNew) {
				entry.addAttachment(fAtt);
			}
		}     	
    }

	public void setCheckout(FileAttachment fAtt, HistoryStamp co) {
		fAtt.setCheckout(co);
	}
	
	public void removeAttachment(Entry entry, FileAttachment fAtt) {
		entry.removeAttachment(fAtt);
	}
}
