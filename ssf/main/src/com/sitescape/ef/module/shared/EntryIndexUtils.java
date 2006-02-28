package com.sitescape.ef.module.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.DocumentException;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;

import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.util.DirPath;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.util.GetterUtil;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.function.WorkAreaOperation;

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
    public static final String BINDER_ID_FIELD = "_binderId";
    public static final String FILENAME_FIELD = "_fileName";
    public static final String FILE_EXT_FIELD = "_fileExt";
    public static final String FILE_TYPE_FIELD = "_fileType";
    public static final String FILE_ID_FIELD = "_fileID";
    private static final String NULLXSL = "<?xml version='1.0' ?> \n    <xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0' />"; 
    // Defines field values
    public static final String READ_ACL_ALL = "all";
    private static TransformerFactory transFactory = TransformerFactory.newInstance();
 
    
    public static void addTitle(Document doc, Entry entry) {
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
    
    public static void addEntryType(Document doc, Entry entry) {
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
    
    public static void addCreationDate(Document doc, Entry entry) {
        // Add creation-date field
    	if (entry.getCreation() != null) {
    		Date creationDate = entry.getCreation().getDate();
            Field creationDateField = Field.Keyword(CREATION_DATE_FIELD, creationDate);
            doc.add(creationDateField);
            Field creationDayField = Field.Keyword(CREATION_DAY_FIELD, formatDayString(creationDate));
            doc.add(creationDayField);
    	}
        
    }
    
    public static void addModificationDate(Document doc, Entry entry) {
    	// Add modification-date field
    	if (entry.getModification() != null ) {
    		Date modDate = entry.getModification().getDate();
        	Field modificationDateField = Field.Keyword(MODIFICATION_DATE_FIELD, modDate);
        	doc.add(modificationDateField);        
        	Field modificationDayField = Field.Keyword(MODIFICATION_DAY_FIELD, formatDayString(modDate));
            doc.add(modificationDayField);
    	}
    }

    public static void addWorkflow(Document doc, Entry entry) {
    	// Add the workflow fields
   		Set workflowStates = entry.getWorkflowStates();
   		if (workflowStates != null) {
   			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
   				WorkflowState ws = (WorkflowState)iter.next();
   				Field workflowStateField = Field.Keyword(WORKFLOW_STATE_FIELD, 
   						ws.getState());
   				//Index the workflow state
   				doc.add(workflowStateField);
   				Definition def = ws.getDefinition();
   				if (def != null) {
	   				Field workflowProcessField = Field.Keyword(WORKFLOW_PROCESS_FIELD, 
	   						def.getId());
	   				//Index the workflow title (which is always the id of the workflow definition)
	   				doc.add(workflowProcessField);
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
    public static void addEvents(Document doc, Entry entry) {
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
    
    public static void addCommandDefinition(Document doc, Entry entry) {
        if (entry.getEntryDef() != null) {
        	Field cdefField = Field.Keyword(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId());
            doc.add(cdefField);
        }
    }
        
    public static void addCreationPrincipalId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) {
        	Field creationIdField = Field.Keyword(CREATORID_FIELD, entry.getCreation().getPrincipal().getId().toString());
            doc.add(creationIdField);
        }
    }   

    public static void addModificationPrincipalId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        if (entry.getModification() != null && entry.getModification().getPrincipal() != null) {
        	Field modificationIdField = Field.Keyword(MODIFICATIONID_FIELD, entry.getModification().getPrincipal().getId().toString());
        	doc.add(modificationIdField);
        }
    }   

     public static void addDocId(Document doc, Entry entry) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = Field.Keyword(DOCID_FIELD, entry.getId().toString());
        doc.add(docIdField);
    }

    public static void addBinder(Document doc, Entry entry) {
       	Field binderIdField = Field.Keyword(BINDER_ID_FIELD, entry.getParentBinder().getId().toString());
       	doc.add(binderIdField);
    }   
    
    public static String formatDayString(Date date) {
    	DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
    	sf.applyPattern("yyyyMMdd");
    	return(df.format(date));
    }
    public static void addReadAcls(Document doc, Binder binder, WorkflowControlledEntry entry, AccessControlManager accessManager) {
        // Add ACL field. We only need to index ACLs for read access. 
        Field racField;
    	List readMemberIds = accessManager.getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);
		Set ids = new HashSet();
        if (entry.hasAclSet()) {
        	//index binders access
        	if (entry.checkWorkArea(AccessType.READ)) {
        		ids.addAll(readMemberIds);
 	        }
        	//index workflow access - ignore widen for search engine - prune results later
           	ids.addAll(entry.getAclSet().getMemberIds(AccessType.READ));
        	if (entry.checkOwner(AccessType.READ)) {
        		ids.add(entry.getCreatorId());
        	}
        	//no access specified, add binder default
        	if (ids.isEmpty())
        		ids.addAll(readMemberIds);
        		
       } else {
            ids.addAll(readMemberIds);
            if (accessManager.testOperation(binder, WorkAreaOperation.CREATOR_READ))
            	ids.add(entry.getCreatorId());
        }
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
		java.util.List nodes = document.selectNodes("/searchml");
		
      	
		Field fileTypeField = Field.Keyword(FILE_TYPE_FIELD, x.getText());
       	doc.add(fileTypeField);   	
	
		
		return;    
    }
    
    public static void addAttachmentText(Document doc, File textfile, File transformFile) {
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
		// get the text and meta data?
		String text = getTextFromXML(document, transformFile);
		
    	//insert the text into the "alltext" field
		BasicIndexUtils.addAllText(doc,text);
		//Field allTextField = BasicIndexUtils.allTextField(text);
		//doc.add(allTextField);
    	// add the meta data fields?
    	return;
    }	
    
    protected static String getTextFromXML(org.dom4j.Document tempfile, File transformFile) {
    	
    	Locale l = Locale.getDefault();
		Templates trans;
		Transformer tranny = null;
		Source xsltSource = new StreamSource(NULLXSL);
        
        try {
			Source s = new StreamSource(transformFile);
			trans = transFactory.newTemplates(s);
			tranny =  trans.newTransformer();
		} catch (TransformerConfigurationException tce) {}
		
		StreamResult result = new StreamResult(new StringWriter());
		try {
			tranny.setParameter("Lang", l);
			tranny.transform(new DocumentSource(tempfile), result);
		} catch (Exception ex) {
			return ex.getMessage();
		}
		return result.getWriter().toString();
	}
    
    public static String getFileExtension(String fileName) {
        int extensionStart = fileName.lastIndexOf('.');
        String extension = "";

        if (extensionStart >= 0) {
            extension = fileName.substring(extensionStart + 1);
        }

        return extension;
    }
    
    public static void addFileAttachmentUid(Document doc, FileUploadItem fui, Set attachments) {
    
        Iterator iter=attachments.iterator();
        while (iter.hasNext()) {
             FileAttachment f = (FileAttachment)iter.next();
             if (fui.getOriginalFilename().equals(f.getFileItem().getName())){
               	Field fileIDField = Field.Keyword(FILE_ID_FIELD, f.getId());
            	doc.add(fileIDField); 
            	return;
             }
        }
    }
}
