package com.sitescape.ef.module.shared;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.search.BasicIndexUtils;

/**
 * Index the fields common to all Entry types.
 *
 * @author Jong Kim
 */
public class EntryIndexUtils {
    
    // Defines field names
    
    public final static String ENTRY_TYPE_FIELD = "_entryType";
    public final static String ENTRY_TYPE_ENTRY = "entry";
    public final static String ENTRY_TYPE_REPLY = "reply";
    public final static String ENTRY_TYPE_USER = "user";
    public final static String ENTRY_TYPE_GROUP = "group";
    public static final String CREATION_DATE_FIELD = "_creationDate";
    public static final String CREATION_DAY_FIELD = "_creationDay";
    public static final String MODIFICATION_DATE_FIELD = "_modificationDate";
    public static final String MODIFICATION_DAY_FIELD = "_modificationDay";
    public static final String CREATORID_FIELD = "_creatorId";
    public static final String MODIFICATIONID_FIELD = "_modificationId";
    public static final String DOCID_FIELD = "_docId";
    public static final String COMMAND_DEFINITION_FIELD = "_commandDef";
    public static final String TITLE_FIELD = "title";
    public static final String TITLE1_FIELD = "_title1";
    public static final String NAME_FIELD = "_name";
    public static final String NAME1_FIELD = "_name1";
    public static final String DESC_FIELD = "_desc";
    public static final String CUSTOM_ATTRS_FIELD = "_customAttributes";
    public static final String EVENT_FIELD = "_event";
    public static final String EVENT_FIELD_START_DATE = "StartDate";
    public static final String EVENT_FIELD_END_DATE = "EndDate";
    public static final String EVENT_COUNT_FIELD = "_eventCount";
    public static final String WORKFLOW_PROCESS_FIELD = "_workflowProcess";
    public static final String WORKFLOW_STATE_FIELD = "_workflowState";
    public static final String WORKFLOW_STATE_CAPTION_FIELD = "_workflowStateCaption";
    public static final String BINDER_ID_FIELD = "_binderId";
    public static final String FILENAME_FIELD = "_fileName";
    public static final String FILE_EXT_FIELD = "_fileExt";
    public static final String FILE_TYPE_FIELD = "_fileType";
    public static final String FILE_ID_FIELD = "_fileID";
    // Defines field values
    public static final String READ_ACL_ALL = "all";
    
    public static void addTitle(Document doc, DefinableEntity entry) {
        // Add the title field
    	if (entry.getTitle() != null) {
    		String title = entry.getTitle();
    		title = title.trim();
            
            if(title.length() > 0) {
    	        Field allTextField = BasicIndexUtils.allTextField(title);
    	        Field titleField = new Field(EntryIndexUtils.TITLE_FIELD, title, true, true, true); 
    	        Field title1Field = Field.Keyword(EntryIndexUtils.TITLE1_FIELD, title.substring(0, 1));
                doc.add(titleField);
                doc.add(title1Field);
                doc.add(allTextField);
            }
    	}
    }
    
    public static void addEntryType(Document doc, DefinableEntity entry) {
        // Add the entry type (entry or reply)
    	if (entry instanceof FolderEntry) {
	        if (((FolderEntry)entry).getTopEntry() == null || ((FolderEntry)entry).getTopEntry() == entry) {
	        	Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_ENTRY);
	        	doc.add(entryTypeField);
	        } else {
	        	Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_REPLY);
	        	doc.add(entryTypeField);
	        }
    	} else if (entry instanceof User) {
        	Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_USER);
        	doc.add(entryTypeField);
    	} else if (entry instanceof Group) {
    		Field entryTypeField = Field.Keyword(EntryIndexUtils.ENTRY_TYPE_FIELD, EntryIndexUtils.ENTRY_TYPE_GROUP);
    		doc.add(entryTypeField);
    	} 
   }
    
    public static void addCreationDate(Document doc, DefinableEntity entry) {
        // Add creation-date field
    	if (entry.getCreation() != null) {
    		Date creationDate = entry.getCreation().getDate();
            Field creationDateField = Field.Keyword(CREATION_DATE_FIELD, creationDate);
            doc.add(creationDateField);
            Field creationDayField = Field.Keyword(CREATION_DAY_FIELD, formatDayString(creationDate));
            doc.add(creationDayField);
    	}
        
    }
    
    public static void addModificationDate(Document doc, DefinableEntity entry) {
    	// Add modification-date field
    	if (entry.getModification() != null ) {
    		Date modDate = entry.getModification().getDate();
        	Field modificationDateField = Field.Keyword(MODIFICATION_DATE_FIELD, modDate);
        	doc.add(modificationDateField);        
        	Field modificationDayField = Field.Keyword(MODIFICATION_DAY_FIELD, formatDayString(modDate));
            doc.add(modificationDayField);
    	}
    }

    public static void addWorkflow(Document doc, DefinableEntity entry) {
    	// Add the workflow fields
    	if (entry instanceof WorkflowSupport) {
    		WorkflowSupport wEntry = (WorkflowSupport)entry;
    		Set workflowStates = wEntry.getWorkflowStates();
    		if (workflowStates != null) {
    			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
    				WorkflowState ws = (WorkflowState)iter.next();
    				Field workflowStateField = Field.Keyword(WORKFLOW_STATE_FIELD, 
   						ws.getState());
    				Field workflowStateCaptionField = Field.Keyword(WORKFLOW_STATE_CAPTION_FIELD, 
   						ws.getStateCaption());
    				//Index the workflow state
    				doc.add(workflowStateField);
    				doc.add(workflowStateCaptionField);
   				
    				Definition def = ws.getDefinition();
    				if (def != null) {
    					Field workflowProcessField = Field.Keyword(WORKFLOW_PROCESS_FIELD, 
    							def.getId());
    					//	Index the workflow title (which is always the id of the workflow definition)
    					doc.add(workflowProcessField);
    				}
   				}
   			}
   		}
     }

	/**
	 * Events are index by their customAttribute name.  We need to get
	 * the events associated with an entry from the search results.
	 * This is needed for the calendar view.
	 * To do this, we create another mapping of events here.  This mapping
	 * maps a well known name to the real attribute name.
	 * @param doc
	 * @param entry
	 */
    public static void addEvents(Document doc, DefinableEntity entry) {
    	int count = 0;
    	Field eventName;
		Map customAttrs = entry.getCustomAttributes();
		Set keyset = customAttrs.keySet();
		Iterator attIt = keyset.iterator();
		// look through the custom attrs of this entry for any of type EVENT
		while (attIt.hasNext()) {
			CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
			if (att.getValueType() == CustomAttribute.EVENT) {
				// set the event name to event + count
				if (att.getValue() != null) {
					eventName = Field.Keyword(EVENT_FIELD + count, att.getName());
					doc.add(eventName);
					count++;
				}
			}
		}    	
		// Add event count field
    	Field eventCountField = Field.Keyword(EVENT_COUNT_FIELD, Integer.toString(count));
    	doc.add(eventCountField);
  
    }
    
    public static void addCommandDefinition(Document doc, DefinableEntity entry) {
        if (entry.getEntryDef() != null) {
        	Field cdefField = Field.Keyword(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId());
            doc.add(cdefField);
        }
    }
        
    public static void addCreationPrincipalId(Document doc, DefinableEntity entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) {
        	Field creationIdField = Field.Keyword(CREATORID_FIELD, entry.getCreation().getPrincipal().getId().toString());
            doc.add(creationIdField);
        }
    }   

    public static void addModificationPrincipalId(Document doc, DefinableEntity entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getModification() != null && entry.getModification().getPrincipal() != null) {
        	Field modificationIdField = Field.Keyword(MODIFICATIONID_FIELD, entry.getModification().getPrincipal().getId().toString());
        	doc.add(modificationIdField);
        }
    }   

     public static void addDocId(Document doc, DefinableEntity entry) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = Field.Keyword(DOCID_FIELD, entry.getId().toString());
        doc.add(docIdField);
    }

    public static void addBinder(Document doc, Binder binder) {
    	Field binderIdField;
    	if (binder != null)
    		binderIdField = Field.Keyword(BINDER_ID_FIELD, binder.getId().toString());
    	else
    		binderIdField = Field.Keyword(BINDER_ID_FIELD, "");
    		
       	doc.add(binderIdField);
    }   
    
    public static String formatDayString(Date date) {
    	DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
    	sf.applyPattern("yyyyMMdd");
    	return(df.format(date));
    }
    public static void addReadAcls(Document doc, Set ids) {
    	if (ids == null) return;
    	// Add ACL field. We only need to index ACLs for read access. 
        Field racField;
        // I'm not sure if putting together a long string value is more
        // efficient than processing multiple short strings... We will see.
        StringBuffer pIds = new StringBuffer();
   		for (Iterator i = ids.iterator(); i.hasNext();) {
    		pIds.append(i.next()).append(" ");
    	}
        racField = new Field(BasicIndexUtils.READ_ACL_FIELD, pIds.toString(), true, true, true);      
        doc.add(racField);
    }
    
    public static void addFileAttachmentName(Document doc,String filename) {
      	Field fileNameField = Field.Keyword(FILENAME_FIELD, filename);
       	doc.add(fileNameField);
    }
    
    public static void addFileExtension(Document doc,String fileName) {
      	Field fileExtField = Field.Keyword(FILE_EXT_FIELD, getFileExtension(fileName));
       	doc.add(fileExtField);   	
    }

    public static void addFileType(Document doc, File textfile) {
    	org.dom4j.Document document = null;
       	if ((textfile == null) || textfile.length() <= 0) return;
    	// open the file with an xml reader
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(textfile);
			//textfile.delete();
			if (document == null) return;
		} catch (Exception e) {e.toString();}
        //} catch (Exception e) {logger.info("Error with converted file: " + e.toString());}
		// <document type="Microsoft Word 2002">
		Element x = (Element)document.selectSingleNode("/searchml");
		Element y = (Element)x.selectSingleNode("document");
		List nodes = document.selectNodes("/searchml");
		
      	
		Field fileTypeField = Field.Keyword(FILE_TYPE_FIELD, x.getText());
       	doc.add(fileTypeField);   	
	
		
		return;    
    }
    
    public static String getFileExtension(String fileName) {
        int extensionStart = fileName.lastIndexOf('.');
        String extension = "";

        if (extensionStart >= 0) {
            extension = fileName.substring(extensionStart + 1);
        }

        return extension;
    }
    
    public static void addFileAttachmentUid(Document doc, FileAttachment fa) {
       	Field fileIDField = Field.Keyword(FILE_ID_FIELD, fa.getId());
    	doc.add(fileIDField); 
    }
}
