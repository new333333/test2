
package com.sitescape.ef.module.folder.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.TitleException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.util.FileUploadItem;

/**
 * Library folders have the restriction that the title of all toplevel entries must be unique
 * @author Janet McCann
 *
 */
public class DefaultFileFolderCoreProcessor extends DefaultFolderCoreProcessor {
	
	/*
	 * Make sure we have a title upload item 
	 * (non-Javadoc)
	 * @see com.sitescape.ef.module.binder.impl.AbstractEntryProcessor#addEntry_filterFiles(com.sitescape.ef.domain.Binder, java.util.List)
	 */
	protected FilesErrors addEntry_filterFiles(Binder binder, List fileUploadItems) throws FilterException {
    	FilesErrors errors = getFileModule().filterFiles(binder, fileUploadItems);
    	for (int i=0; i<fileUploadItems.size(); ++i) {
    		FileUploadItem fui = (FileUploadItem)fileUploadItems.get(i);
    		if (fui.getType() == FileUploadItem.TYPE_TITLE) return errors;
    	}
    	throw new TitleException("");
    }
	/*
	 * If file failed to be copied, delete entire entry
	 *  (non-Javadoc)
	 * @see com.sitescape.ef.module.binder.impl.AbstractEntryProcessor#addEntry_processFiles(com.sitescape.ef.domain.Binder, com.sitescape.ef.domain.Entry, java.util.List, com.sitescape.ef.module.file.FilesErrors)
	 */
	protected FilesErrors addEntry_processFiles(Binder binder, 
    		Entry entry, List fileUploadItems, FilesErrors filesErrors) {
		FilesErrors errors = getFileModule().writeFiles(binder, entry, fileUploadItems, filesErrors);
	   	for (int i=0; i<fileUploadItems.size(); ++i) {
    		FileUploadItem fui = (FileUploadItem)fileUploadItems.get(i);
    		if (fui.getType() == FileUploadItem.TYPE_TITLE) return errors;
    	}
	   	deleteEntry(binder,entry);
    	throw new TitleException("");
    }
 
    protected Map addBinder_toEntryData(Binder parent, Definition def, InputDataAccessor inputData, Map fileItems) {
        //Call the definition processor to get the entry data to be stored
	    //make sure sub-folder is file folder
        if (def.getType() != Definition.FILE_FOLDER_VIEW) {
        	throw new NotSupportedException("Sub-folder must be a file folder type");
        }
        return super.addBinder_toEntryData(parent, def, inputData, fileItems);
    }

   /*
    * Cannot remove title - can only add versions
    * @see com.sitescape.ef.module.binder.impl.AbstractEntryProcessor#modifyEntry_removeAttachments(com.sitescape.ef.domain.Binder, com.sitescape.ef.domain.Entry, java.util.Collection)
    */
   protected void modifyEntry_removeAttachments(Binder binder, Entry entry, Collection deleteAttachments) {
	   for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
   			Attachment a = (Attachment)iter.next();
   			if ((a instanceof FileAttachment) && (!(a instanceof VersionAttachment)) &&
   				("_fileEntryTitle".equals(a.getName()))) throw new TitleException("");
	   }
   }

   protected void modifyEntry_postFillIn(Binder binder, Entry entry, 
		   InputDataAccessor inputData, Map entryData) {
	   super.modifyEntry_postFillIn(binder, entry, inputData, entryData);
	   
	   if(!inputData.exists("_renameFileTo"))
		   return;
	   
	   // We have a request for renaming the library file associated with
	   // the file folder entry.
	   String toName = inputData.getSingleValue("_renameFileTo");
	   FileAttachment fa = (FileAttachment) inputData.getSingleObject("_renameFileTo_fa");
	   
	   getFileModule().renameFile(binder, entry, fa, toName);
	   
	   // If you're still here, the file renaming was successful.
	   // We can change the title of the entry now. 
	   entry.setTitle(toName);
   }
   
   protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
	   super.addEntry_fillIn(binder, entry, inputData, entryData);	   
	   takeCareOfLastModDate(entry, inputData);
   }
      
   protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
	   super.modifyEntry_fillIn(binder, entry, inputData, entryData);
	   takeCareOfLastModDate(entry, inputData);
   }

   private void takeCareOfLastModDate(Entry entry, InputDataAccessor inputData) {
	   Date lastModDate = (Date) inputData.getSingleObject("_lastModifiedDate");
	   if(lastModDate != null) {
		   // We have a caller-supplied last-modified date.
	        User user = RequestContextHolder.getRequestContext().getUser();
	        entry.setModification(new HistoryStamp(user, lastModDate));
	   }
   }
}
