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
	package org.kablink.teaming.module.shared;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Collection;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.IPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WfAcl;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;


import static org.kablink.util.search.Constants.*;

/**
 * Index the fields common to all Entry types.
 *
 * @author Jong Kim
 */
public class EntityIndexUtils {

    // Defines field values
    public static final String DEFAULT_NOTITLE_TITLE = "---";
        
    public static void addTitle(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        // Add the title field
    	if (entry.getTitle() != null) {
    		String title = entry.getTitle();
    		title = title.trim();
            
            if(title.length() > 0) {
            	if (!fieldsOnly) {
            		Field allTextField = BasicIndexUtils.allTextField(title);
            		doc.add(allTextField);
            	}
            	Field titleField = new Field(Constants.TITLE_FIELD, title, Field.Store.YES, Field.Index.TOKENIZED);
    	        Field sortTitleField = new Field(Constants.SORT_TITLE_FIELD, title.toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	        Field title1Field = new Field(Constants.TITLE1_FIELD, title.substring(0, 1), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	        doc.add(titleField);
    	        doc.add(sortTitleField);
                doc.add(title1Field);
                if (entry instanceof Binder) {
                	Binder binder = (Binder)entry;
                	//Special case: user workspaces and top workspace don't show parent folder 
                	String extendedTitle = title;
                	if (!binder.isRoot() && !binder.getParentBinder().getEntityType().equals(EntityType.profiles)) {
                		extendedTitle = title + " (" + entry.getParentBinder().getTitle() + ")";
                	} 
                	
        	        Field extendedTitleField = new Field(Constants.EXTENDED_TITLE_FIELD, extendedTitle, Field.Store.YES, Field.Index.TOKENIZED);
        	        doc.add(extendedTitleField);
                }
            }
    	}
    }
    
    	        
    public static void addNormTitle(Document doc, Binder entry, boolean fieldsOnly) {
		// Add the title field
		String normTitle = "";
		if (entry.getTitle() != null) {
			String title = entry.getSearchTitle();
			title = title.trim();
			if (title.length() > 0) {
				normTitle = title;
				Field bucketTitleField = new Field(
						Constants.NORM_TITLE, normTitle.toLowerCase(),
						Field.Store.YES, Field.Index.UN_TOKENIZED);
				doc.add(bucketTitleField);
				// now add it without lowercasing
				bucketTitleField = new Field(
						Constants.NORM_TITLE_FIELD, normTitle,
						Field.Store.YES, Field.Index.UN_TOKENIZED);
				doc.add(bucketTitleField);
			}
		}
	}  	        
    	        
   public static void addRating(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	//rating may not exist or not be supported
    	try {
    		if(entry.getAverageRating() != null) {
	        	Field rateField = new Field(RATING_FIELD, entry.getAverageRating().getAverage().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(rateField);
    		}
        } catch (Exception ex) {};
   	
    }

    /**
     * If an entry is in the preDeleted state, adds appropriate fields
     * to the index for the UI to manage them.
     * 
     * Note:  To sort on a field, you MUST used
     * Field.Index.UN_TOKENIZED.
     * 
     * @param doc
     * @param entry
     * @param fieldsOnly
     */
    public static void addPreDeletedFields(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	boolean preDeleted = false;
    	Long preDeletedWhen = null;
    	User preDeletedBy = null;
    	String preDeletedFrom = null;
    	
    	// Determine the values to use for the various preDeleted
    	// fields based on what entry is.
      	if (entry instanceof FolderEntry) {
      		FolderEntry fe = ((FolderEntry) entry);
      		if (fe.isPreDeleted()) {
      			Binder feBinder = fe.getParentBinder();
      			preDeleted      = true;
      			preDeletedWhen  = fe.getPreDeletedWhen();
      			preDeletedBy    = getUserById(fe.getPreDeletedBy(), entry.getZoneId());
      			preDeletedFrom  = feBinder.getTitle() + " (" + ((Binder) feBinder.getParentWorkArea()).getTitle() + ")";
      		}
      	}
      	else if (entry instanceof Folder) {
      		Folder folder = ((Folder) entry);
      		if (folder.isPreDeleted()) {
      			preDeleted     = true;
      			preDeletedWhen = folder.getPreDeletedWhen();
      			preDeletedBy   = getUserById(folder.getPreDeletedBy(), entry.getZoneId());
      			preDeletedFrom = folder.getParentBinder().getTitle();
      		}
      	}
      	else if (entry instanceof Workspace) {
      		Workspace ws = ((Workspace) entry);
      		if (ws.isPreDeleted()) {
      			preDeleted     = true;
      			preDeletedWhen = ws.getPreDeletedWhen();
      			preDeletedBy   = getUserById(ws.getPreDeletedBy(), entry.getZoneId());
      			preDeletedFrom = ws.getParentBinder().getTitle();
      		}
      	}
      	
      	// Add the preDeleted flag itself.
    	Field field = new Field(Constants.PRE_DELETED_FIELD, (preDeleted ? Constants.TRUE : Constants.FALSE), Field.Store.NO, Field.Index.TOKENIZED);
    	doc.add(field);
    	
    	// Is entry preDeleted?
    	if (preDeleted) {
    		// Yes!  If we know who it was deleted by by...
        	if (null != preDeletedBy) {
        		// ...add both their ID and title to the index.
        		field = new Field(Constants.PRE_DELETED_BY_ID_FIELD, String.valueOf(preDeletedBy.getId().intValue()), Field.Store.YES, Field.Index.TOKENIZED);
        		doc.add(field);
        		
            	field = new Field(Constants.PRE_DELETED_BY_TITLE_FIELD, preDeletedBy.getTitle(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            	doc.add(field);
        	}
        	
        	// If we know when the item was deleted...
        	if (null != preDeletedWhen) {
        		// ...add it to the index
        		field = new Field(Constants.PRE_DELETED_WHEN_FIELD, String.valueOf(preDeletedWhen), Field.Store.YES, Field.Index.UN_TOKENIZED);
        		doc.add(field);
        	}
        	
        	// If we know where the item was deleted from... 
        	if (null != preDeletedFrom) {
        		// ...add it to the index
        		field = new Field(Constants.PRE_DELETED_FROM_FIELD, preDeletedFrom, Field.Store.YES, Field.Index.UN_TOKENIZED);
        		doc.add(field);
        	}
    	}
    }
    
    /*
     * Returns a User object given a user ID and a zone ID.
     */
    private static User getUserById(Long userId, Long zoneId) {
    	User reply = null;
    	try {
    		ProfileDao pd = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
    		reply = pd.loadUser(userId, zoneId);
    	}
    	catch (Exception e) {
    		reply = null;
    	}
		return reply;
    }
    
    public static void addEntityType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
      	Field eField = new Field(ENTITY_FIELD, entry.getEntityType().name(), Field.Store.YES, Field.Index.TOKENIZED);
       	doc.add(eField);
    }
    public static void addDefinitionType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Integer definitionType = entry.getDefinitionType();
    	if (definitionType == null) definitionType = 0;
      	Field eField = new Field(DEFINITION_TYPE_FIELD, definitionType.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(eField);
    }
    public static void addEntryType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        // Add the entry type (entry or reply)
    	if (entry instanceof FolderEntry) {
	        if (((FolderEntry)entry).isTop()) {
	        	Field entryTypeField = new Field(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_ENTRY, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryTypeField);
	        } else {
	        	Field entryTypeField = new Field(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_REPLY, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryTypeField);
	        	
	        	FolderEntry folderEntry = (FolderEntry)entry;
	        	Field entryParentEntryId = new Field(Constants.ENTRY_PARENT_ID_FIELD, folderEntry.getParentEntry().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryParentEntryId);
	        	Field entryTopEntryId = new Field(Constants.ENTRY_TOP_ENTRY_ID_FIELD, folderEntry.getTopEntry().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryTopEntryId);
	        	String topEntryTitle = folderEntry.getTopEntry().getTitle().toString();
	        	if (topEntryTitle.trim().equals("")) topEntryTitle = EntityIndexUtils.DEFAULT_NOTITLE_TITLE;
	        	Field entryTopEntryTitle = new Field(Constants.ENTRY_TOP_ENTRY_TITLE_FIELD, topEntryTitle, Field.Store.YES, Field.Index.UN_TOKENIZED);
	        	doc.add(entryTopEntryTitle);
	        }
    	} else if (entry instanceof User) {
        	Field entryTypeField = new Field(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_USER, Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(entryTypeField);
    	} else if (entry instanceof Group) {
    		Field entryTypeField = new Field(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_GROUP, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(entryTypeField);
    	} else if (entry instanceof Application) {
        	Field entryTypeField = new Field(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_APPLICATION, Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(entryTypeField);
    	} else if (entry instanceof ApplicationGroup) {
    		Field entryTypeField = new Field(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_APPLICATION_GROUP, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(entryTypeField);
    	} 
   }
    public static void addCreation(Document doc, HistoryStamp stamp, boolean fieldsOnly) {
    	if (stamp == null) return;
    	Date creationDate = stamp.getDate();
    	Principal principal = stamp.getPrincipal();
    	if (creationDate != null) {		
    		Field creationDateField = new Field(CREATION_DATE_FIELD, DateTools.dateToString(creationDate,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(creationDateField);
    		//index the YYYYMMDD string
    		String dayString = formatDayString(creationDate);
    		Field creationDayField = new Field(CREATION_DAY_FIELD, dayString, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(creationDayField);
    		// index the YYYYMM string
    		String yearMonthString = dayString.substring(0,6);
    		Field creationYearMonthField = new Field(CREATION_YEAR_MONTH_FIELD, yearMonthString, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(creationYearMonthField);
    		// index the YYYY string
    		String yearString = dayString.substring(0,4);
    		Field creationYearField = new Field(CREATION_YEAR_FIELD, yearString, Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(creationYearField);
    	}
    	//Add the id of the creator (no, not that one...)
    	if (principal != null) {
            Field creationIdField = new Field(CREATORID_FIELD, principal.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationIdField);
            Field creationNameField = new Field(CREATOR_NAME_FIELD, principal.getName().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationNameField);
            Field creationTitleField = new Field(CREATOR_TITLE_FIELD, principal.getTitle().toString(), Field.Store.YES, Field.Index.TOKENIZED);
            doc.add(creationTitleField);
            Field creationSortTitleField = new Field(SORT_CREATOR_TITLE_FIELD, principal.getTitle().toString().toLowerCase(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(creationSortTitleField);
        }
    }
    public static void addModification(Document doc, HistoryStamp stamp, boolean fieldsOnly) {
    	if (stamp == null) return;
    	Date modDate = stamp.getDate();
    	Principal principal = stamp.getPrincipal();
     	if (modDate != null) {
     	
     		// Add modification-date field
     		Field modificationDateField = new Field(MODIFICATION_DATE_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND), Field.Store.YES, Field.Index.UN_TOKENIZED);
     		doc.add(modificationDateField);        
     		// index the YYYYMMDD string
     		String dayString = formatDayString(modDate);
     		Field modificationDayField = new Field(MODIFICATION_DAY_FIELD, dayString, Field.Store.YES, Field.Index.UN_TOKENIZED);
     		doc.add(modificationDayField);
     		// index the YYYYMM string
     		String yearMonthString = dayString.substring(0,6);
     		Field modificationYearMonthField = new Field(MODIFICATION_YEAR_MONTH_FIELD, yearMonthString, Field.Store.YES, Field.Index.UN_TOKENIZED);
     		doc.add(modificationYearMonthField);
     		// index the YYYY string
     		String yearString = dayString.substring(0,4);
     		Field modificationYearField = new Field(MODIFICATION_YEAR_FIELD, yearString, Field.Store.YES, Field.Index.UN_TOKENIZED);
     		doc.add(modificationYearField);
     	}
       	//Add the id of the modifier 
        if (principal != null) {
        	Field modificationIdField = new Field(MODIFICATIONID_FIELD, principal.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationIdField);
            Field modificationNameField = new Field(MODIFICATION_NAME_FIELD, principal.getName().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationNameField);
            Field modificationTitleField = new Field(MODIFICATION_TITLE_FIELD, principal.getTitle().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(modificationTitleField);
        }   
   } 
     
    public static void addOwner(Document doc, Principal owner, boolean fieldsOnly) {
        if (owner != null) {
        	Field ownerIdField = new Field(OWNERID_FIELD, owner.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(ownerIdField);
            Field ownerNameField = new Field(OWNER_NAME_FIELD, owner.getName().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(ownerNameField);
            Field ownerTitleField = new Field(OWNER_TITLE_FIELD, owner.getTitle().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(ownerTitleField);
        }   
   } 
     
    public static void addWorkflow(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	// Add the workflow fields
    	if (entry instanceof WorkflowSupport) {
    		WorkflowSupport wEntry = (WorkflowSupport)entry;
    		Set workflowStates = wEntry.getWorkflowStates();
    		if (workflowStates != null) {
    			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
    				WorkflowState ws = (WorkflowState)iter.next();
    				Field workflowStateField = new Field(WORKFLOW_STATE_FIELD, 
   						ws.getState(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    				String workflowCaption = WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState());
    				Field workflowStateCaptionField = new Field(WORKFLOW_STATE_CAPTION_FIELD, 
    						workflowCaption, Field.Store.YES, Field.Index.UN_TOKENIZED);
    				//Index the workflow state
    				doc.add(workflowStateField);
    				doc.add(workflowStateCaptionField);
    				
    				//Add the caption to the allText field
    				Field allTextField = BasicIndexUtils.allTextField(workflowCaption);
            		doc.add(allTextField);
   				
    				Definition def = ws.getDefinition();
    				if (def != null) {
    					Field workflowProcessField = new Field(WORKFLOW_PROCESS_FIELD, 
    							def.getId(), Field.Store.NO, Field.Index.UN_TOKENIZED);
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
    public static void addEvents(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	int count = 0;
		Map customAttrs = entry.getCustomAttributes();
		Set keyset = customAttrs.keySet();
		Iterator attIt = keyset.iterator();
		// look through the custom attrs of this entry for any of type EVENT, DATE, or DATE_TIME

		Set entryEventsDates = new HashSet();
		while (attIt.hasNext()) {
			CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
			if (att.getValueType() == CustomAttribute.EVENT) {
				// set the event name to event + count
				Event event = (Event)att.getValue();
				List recurencesDates = event.getAllRecurrenceDates();
				List allEventDays = Event.getEventDaysFromRecurrencesDates(recurencesDates);
				entryEventsDates.addAll(allEventDays);
								
				if (att.getValue() != null) {
					doc.add(new Field(EVENT_FIELD + count, att.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
					doc.add(new Field(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_ID, event.getId(), Field.Store.YES, Field.Index.UN_TOKENIZED));
					doc.add(getEntryEventDaysField(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_DATES, new HashSet(allEventDays)));
					count++;
				}
				doc.add(getRecurrenceDatesField(event, recurencesDates));
			} else if (att.getValueType() == CustomAttribute.DATE) {
				Calendar dateAttr = new GregorianCalendar();
				dateAttr.setTime((Date)att.getValue());
				entryEventsDates.add(dateAttr);
			}
		}
		
		doc.add(getEntryEventDaysField(EVENT_DATES_FIELD, entryEventsDates));
		
		// Add event count field
    	Field eventCountField = new Field(EVENT_COUNT_FIELD, Integer.toString(count), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(eventCountField);
    }
    
    
	private static Field getEntryEventDaysField(String fieldName, Set dates) {
		StringBuilder sb = new StringBuilder();
		Iterator datesIt = dates.iterator();
		while (datesIt.hasNext()) {
			Calendar c = (Calendar)datesIt.next();
			//sb.append(DateTools.dateToString(c.getTime(), DateTools.Resolution.DAY));
			//sb.append(" ");
			sb.append(DateTools.dateToString(c.getTime(), DateTools.Resolution.MINUTE));
			sb.append(" ");
		}

		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		
		return new Field(fieldName, sb.toString(), Field.Store.YES, Field.Index.TOKENIZED);
	}
	
	private static Field getRecurrenceDatesField(Event event, List recurencesDates) {
		StringBuilder sb = new StringBuilder();
		Iterator it = recurencesDates.iterator();
		while (it.hasNext()) {
			Calendar[] eventDates = (Calendar[]) it.next();
			sb.append(DateTools.dateToString(eventDates[0].getTime(), DateTools.Resolution.MINUTE));
			sb.append(" ");
			sb.append(DateTools.dateToString(eventDates[1].getTime(), DateTools.Resolution.MINUTE));
			sb.append(",");
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);

		return new Field(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_RECURRENCE_DATES_FIELD, sb.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
	}
        
    public static void addCommandDefinition(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getEntryDef() != null) {
        	Field cdefField = new Field(COMMAND_DEFINITION_FIELD, entry.getEntryDef().getId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
            doc.add(cdefField);
        }
    }
        
    public static void addEntryDefinitions(Document doc, DefinableEntity folder, boolean fieldsOnly) {
    	if (folder instanceof Folder) {
    		Folder f = (Folder)folder;
    		List folderEntryDefinitions = f.getEntryDefinitions();
    		String entryDefs = "";
			for (int i=0; i < folderEntryDefinitions.size(); ++i) {
				Definition def = (Definition) folderEntryDefinitions.get(i);
				entryDefs += " " + def.getId();
			}    		
        	Field cdefField = new Field(ENTRY_DEFINITIONS_FIELD, entryDefs, Field.Store.NO, Field.Index.UN_TOKENIZED);
            doc.add(cdefField);
    	}
    }
        
    public static void addFamily(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getEntryDef() != null) {
        	org.dom4j.Document def = entry.getEntryDef().getDefinition();
        	String family = DefinitionUtils.getFamily(def);
        	if (Validator.isNotNull(family)) {
      			Field eField = new Field(FAMILY_FIELD, family, Field.Store.NO, Field.Index.UN_TOKENIZED);
    	       	doc.add(eField);	
        	}
        }
    }


     public static void addDocId(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = new Field(DOCID_FIELD, entry.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        doc.add(docIdField);
    }

    public static void addParentBinder(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Field binderIdField;
    	if (entry instanceof Binder) {
    		if (entry.getParentBinder() == null) return;
       		binderIdField = new Field(BINDERS_PARENT_ID_FIELD, entry.getParentBinder().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	    		
    	} else if (entry != null) {
    		binderIdField = new Field(BINDER_ID_FIELD, entry.getParentBinder().getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	} else {
    		binderIdField = new Field(BINDER_ID_FIELD, "", Field.Store.YES, Field.Index.UN_TOKENIZED);
    	}
       	doc.add(binderIdField);
    }   
    
    public static String formatDayString(Date date) {
    	DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
    	sf.applyPattern("yyyyMMdd");
    	return(df.format(date));
    }
    public static String formatDaySecondString(Date date) {
    	DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
    	sf.applyPattern("yyyyMMddHHmmss");
    	return(df.format(date));
    }
    public static void addTeamMembership(Document doc, Set<Long> ids, boolean fieldsOnly) {
    	if ((ids == null) || ids.isEmpty()) return;
		doc.add(new Field(TEAM_MEMBERS_FIELD, LongIdUtil.getIdsAsString(ids), Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(IS_TEAM_FIELD, "true", Field.Store.NO, Field.Index.TOKENIZED));
   	
    }
    // Get ids for folder read access.  Replace owner indicator with search owner search flag. Replace team indicator with team owner search flag
    public static String getFolderAclString(Binder binder) {
    	return getFolderAclString(binder, false);
    }
    public static String getFolderAclString(Binder binder, boolean includeTitleAcl) {
		Set binderIds = AccessUtils.getReadAccessIds(binder, includeTitleAcl);
   		String ids = LongIdUtil.getIdsAsString(binderIds);
       	ids = ids.replaceFirst(ObjectKeys.TEAM_MEMBER_ID.toString(), Constants.READ_ACL_TEAM);
       	ids = ids.replaceFirst(ObjectKeys.OWNER_USER_ID.toString(), Constants.READ_ACL_BINDER_OWNER);
       	if (Validator.isNull(ids)) return Constants.EMPTY_ACL_FIELD;
        return ids;
    }
    public static String getEntryAclString(Binder binder, Entry entry) {
    	if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop()) {
    		//This is a reply to a folder entry. Get the acl of the top entry
    		entry = ((FolderEntry)entry).getTopEntry();
    	}
    	if (entry.hasEntryAcl()) {
			Set entryIds = AccessUtils.getReadAccessIds(entry);
	   		String ids = LongIdUtil.getIdsAsString(entryIds);
	       	ids = ids.replaceFirst(ObjectKeys.TEAM_MEMBER_ID.toString(), Constants.READ_ACL_TEAM);
	       	ids = ids.replaceFirst(ObjectKeys.OWNER_USER_ID.toString(), Constants.READ_ACL_BINDER_OWNER);
	        if (entry.isIncludeFolderAcl()) ids += Constants.READ_ACL_ALL;
	       	if (Validator.isNull(ids)) return Constants.EMPTY_ACL_FIELD;
	        return ids;
    	} else {
    		return Constants.READ_ACL_ALL;
    	}
    }
    
    private static void addEntryAcls(Document doc, Binder binder, Entry entry) {
		//get real entry access
		doc.add(new Field(Constants.ENTRY_ACL_FIELD, getEntryAclString(binder, entry), Field.Store.NO, Field.Index.TOKENIZED));
    }

    private static void addDefaultEntryAcls(Document doc, Binder binder, Entry entry) {
		//get default entry access
		doc.add(new Field(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL, Field.Store.NO, Field.Index.TOKENIZED));
    }

    //Add acl fields for binder for storage in search engine
    private static void addBinderAcls(Document doc, Binder binder) {
    	addBinderAcls(doc, binder, false);
    }
    private static void addBinderAcls(Document doc, Binder binder, boolean includeTitleAcl) {
		//get real binder access
		doc.add(new Field(Constants.FOLDER_ACL_FIELD, getFolderAclString(binder, includeTitleAcl), Field.Store.NO, Field.Index.TOKENIZED));
		//get team members
		doc.add(new Field(Constants.TEAM_ACL_FIELD, binder.getTeamMemberString(), Field.Store.NO, Field.Index.TOKENIZED));
		//add binder owner
		Long owner = binder.getOwnerId();
		String ownerStr = Constants.EMPTY_ACL_FIELD;
		if (owner != null) ownerStr = owner.toString();
		doc.add(new Field(Constants.BINDER_OWNER_ACL_FIELD, ownerStr, Field.Store.NO, Field.Index.TOKENIZED));    	
    }
    //Add acl fields for binder for storage in dom4j documents.
    //In this case replace owner with real owner in _folderAcl
 	//The extra field is not necessary cause bulk updating is not done
    private static void addBinderAcls(org.dom4j.Element parent, Binder binder) {
    	addBinderAcls(parent, binder, false);
    }
    private static void addBinderAcls(org.dom4j.Element parent, Binder binder, boolean includeTitleAcl) {
		Set binderIds = AccessUtils.getReadAccessIds(binder, includeTitleAcl);
      	if (binderIds.remove(ObjectKeys.OWNER_USER_ID)) binderIds.add(binder.getOwnerId());
      	String ids = LongIdUtil.getIdsAsString(binderIds);
       	ids = ids.replaceFirst(ObjectKeys.TEAM_MEMBER_ID.toString(), Constants.READ_ACL_TEAM);
    	Element acl = parent.addElement(Constants.FOLDER_ACL_FIELD);
   		acl.setText(ids);
   		//add Team
   		acl = parent.addElement(Constants.TEAM_ACL_FIELD);
   		acl.setText(binder.getTeamMemberString());
   }
    
    public static void addReadAccess(Document doc, Binder binder, boolean fieldsOnly) {
    	addReadAccess(doc, binder, fieldsOnly, false);
    }
    public static void addReadAccess(Document doc, Binder binder, boolean fieldsOnly, boolean includeTitleAcl) {
    	//set entryAcl to "all" and 
		doc.add(new Field(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL, Field.Store.NO, Field.Index.TOKENIZED));
		//add binder acls
		addBinderAcls(doc, binder, includeTitleAcl);
    }
    public static void addReadAccess(org.dom4j.Element parent, Binder binder, boolean fieldsOnly) {
    	addReadAccess(parent, binder, fieldsOnly, false);
    }
    public static void addReadAccess(org.dom4j.Element parent, Binder binder, boolean fieldsOnly, boolean includeTitleAcl) {
    	//set entryAcl to all
   		Element  acl = parent.addElement(Constants.ENTRY_ACL_FIELD);
 		acl.setText(Constants.READ_ACL_ALL);
		//add binder access
   		addBinderAcls(parent, binder, includeTitleAcl);
    }
    private static String getWfEntryAccess(WorkflowSupport wEntry) {
		//get principals given read access 
     	Set ids = wEntry.getStateMembers(WfAcl.AccessType.read);
     	//replace owner indicator, but leave team member alone for now
        //for entries, the owner is stored in the entry acl and not its own field like binders
     	//The extra field is not necessary cause updateing does not have to optimized
     	if (ids.remove(ObjectKeys.OWNER_USER_ID)) ids.add(wEntry.getOwnerId());
     	// I'm not sure if putting together a long string value is more
     	// 	efficient than processing multiple short strings... We will see.
     	StringBuffer pIds = new StringBuffer(LongIdUtil.getIdsAsString(ids));
   		if (ids.isEmpty() || wEntry.isWorkAreaAccess(WfAcl.AccessType.read)) {
   			//add all => folder check
   			pIds.append(Constants.READ_ACL_ALL);      			
   		}
   		return pIds.toString().replaceFirst(ObjectKeys.TEAM_MEMBER_ID.toString(), Constants.READ_ACL_TEAM);
    }
    private static String getUserEntryAccess(User entry) {
		//get principals given read access 
     	Set ids = new HashSet();
     	ids.add(entry.getId());
     	if (!entry.getId().equals(entry.getCreation().getPrincipal().getId())) 
     			ids.add(entry.getCreation().getPrincipal().getId());
     	//Add groups this user is in
		List memberOf = entry.getMemberOf();
		for (Iterator i = memberOf.iterator(); i.hasNext();) {
			ids.add(((Group)i.next()).getId());
        }
     	StringBuffer pIds = new StringBuffer(LongIdUtil.getIdsAsString(ids));
    	//add allUsers
   		pIds.append(Constants.READ_ACL_ALL_USERS);      			
   		return pIds.toString();
    }
    public static void addReadAccess(Document doc, Binder binder, DefinableEntity entry, boolean fieldsOnly) {
		// Add ACL field. We only need to index ACLs for read access.
    	if (entry instanceof WorkflowSupport) {
    		WorkflowSupport wEntry = (WorkflowSupport)entry;
       		// Add the Entry_ACL field
       		if (wEntry.hasAclSet()) {
       			doc.add(new Field(Constants.ENTRY_ACL_FIELD, getWfEntryAccess(wEntry), Field.Store.NO, Field.Index.TOKENIZED));
       		}
       		//add entry access
    		if (((Entry)entry).hasEntryAcl()) {
    			addEntryAcls(doc, binder, (Entry)entry);
    		} else if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop() && ((FolderEntry)entry).getTopEntry().hasEntryAcl()) {
    			//This is a reply to an folder entry, set the acl the same as the top entry
    			addEntryAcls(doc, binder, (Entry)entry);
    		} else if (!wEntry.hasAclSet() && !((Entry)entry).hasEntryAcl()) {
    			addDefaultEntryAcls(doc, binder, (Entry)entry);
    		}
       		//add binder access
    		addBinderAcls(doc, binder);

    	} else if (entry instanceof Entry) {
       		// Add the Entry_ACL field
    		if (((Entry)entry).hasEntryAcl()) {
    			addEntryAcls(doc, binder, (Entry)entry);
    		} else {
    			addDefaultEntryAcls(doc, binder, (Entry)entry);
    		}
       		//add binder access
    		addBinderAcls(doc, binder);

    	} else if (entry instanceof User) {
            // Add the Entry_ACL field
           	doc.add(new Field(Constants.ENTRY_ACL_FIELD, getUserEntryAccess((User)entry), Field.Store.NO, Field.Index.TOKENIZED));
           	//add binder access
        	addBinderAcls(doc, binder);

    	} else {
    		addReadAccess(doc, binder, fieldsOnly);
    	}
	}
    //This is used to store the "read" acls in a document that is not a search document
    public static void addReadAccess(org.dom4j.Element parent, Binder binder, DefinableEntity entry, boolean fieldsOnly) {
		// Add ACL field. We only need to index ACLs for read access.
  		//add binder access
   		if (entry instanceof WorkflowSupport) {
  	   		WorkflowSupport wEntry = (WorkflowSupport)entry;
       		// Add the Entry_ACL field
   	   		Element acl = parent.addElement(Constants.ENTRY_ACL_FIELD);
       		acl.setText(getWfEntryAccess(wEntry));
    		addBinderAcls(parent, binder);

    	} else if (entry instanceof User) {
    		// Add the Entry_ACL field
   	   		Element acl = parent.addElement(Constants.ENTRY_ACL_FIELD);
       		acl.setText(getUserEntryAccess((User)entry));
       		//add binder access
			addBinderAcls(parent, binder);

    	} else {
     		addReadAccess(parent, binder, fieldsOnly);
    	}
	}
 
    public static void addTags(Document doc, DefinableEntity entry, Collection allTags, boolean fieldsOnly) {
    	String indexableTags = "";
    	String aclTags = "";
    	String tag = "";
    	String aclTag = "";
    	String lowerAclTag = "";
    	   	
    	Map<String, SortedSet<Tag>> uniqueTags = TagUtil.uniqueTags(allTags);
    	SortedSet<Tag> pubTags = uniqueTags.get(ObjectKeys.COMMUNITY_ENTITY_TAGS);
    	SortedSet<Tag> privTags = uniqueTags.get(ObjectKeys.PERSONAL_ENTITY_TAGS);
    	Field ttfTagField = new Field(Constants.TAG_FIELD_TTF, "", Field.Store.NO, Field.Index.UN_TOKENIZED);
    	// index all the public tags (allTags field and tag_acl field)
		for (Tag thisTag: pubTags) {
			tag = thisTag.getName();
			indexableTags += " " + tag;
			// Add the ttf fields.  The ttf fields are case insensitive before the ":",
			// so users can search and find tags regardless of case. The strategy is to use
			// this field for the type to find searches, but keep the original fields for 
			// display.
			tag = tag.toLowerCase() + ":" + tag; 
			ttfTagField = new Field(Constants.TAG_FIELD_TTF, tag, Field.Store.NO, Field.Index.UN_TOKENIZED);
			doc.add(ttfTagField);
			aclTag = BasicIndexUtils.buildAclTag(thisTag.getName(), Constants.READ_ACL_ALL);
			aclTags += " " + aclTag;
			lowerAclTag = BasicIndexUtils.buildAclTag(thisTag.getName().toLowerCase(), Constants.READ_ACL_ALL);
			aclTag = lowerAclTag + ":" + aclTag;
			ttfTagField = new Field(Constants.ACL_TAG_FIELD_TTF, aclTag, Field.Store.YES, Field.Index.UN_TOKENIZED);
			doc.add(ttfTagField);
		}
	
		// now index the private tags (just the tag_acl field)
		for (Tag thisTag: privTags) {
			tag = thisTag.getName();
			aclTag = BasicIndexUtils.buildAclTag(tag, thisTag.getOwnerIdentifier().getEntityId().toString());
			aclTags += " " + aclTag;
			// type to find fields.
			lowerAclTag = BasicIndexUtils.buildAclTag(tag.toLowerCase(), thisTag.getOwnerIdentifier().getEntityId().toString());
			aclTag = lowerAclTag + ":" + aclTag;
			ttfTagField = new Field(Constants.ACL_TAG_FIELD_TTF, aclTag, Field.Store.YES, Field.Index.UN_TOKENIZED);
			doc.add(ttfTagField);
		}
    
    	Field tagField = new Field(Constants.TAG_FIELD, indexableTags, Field.Store.YES, Field.Index.TOKENIZED);
    	doc.add(tagField);
    	
    	if (!fieldsOnly) {
    		tagField = BasicIndexUtils.allTextField(indexableTags);
    		doc.add(tagField);
    	}
    	
    	tagField = new Field(Constants.ACL_TAG_FIELD, aclTags, Field.Store.YES, Field.Index.TOKENIZED);
    	doc.add(tagField);
    }
	
    public static void addFileType(Document doc, File textfile, boolean fieldsOnly) {
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
		
      	
		Field fileTypeField = new Field(FILE_TYPE_FIELD, x.getText(), Field.Store.YES, Field.Index.UN_TOKENIZED);
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
    //Used to index info about a file with its owner
	public static void addAttachedFileIds(Document doc, DefinableEntity entry, boolean fieldsOnly) {
		Collection<FileAttachment> atts = entry.getFileAttachments();
        for (FileAttachment fa : atts) {
        	Field fileIDField = new Field(FILE_ID_FIELD, fa.getId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(fileIDField); 
        	Field fileOwnerIdField = new Field(FILE_CREATOR_ID_FIELD, String.valueOf(fa.getCreation().getPrincipal().getId()), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(fileOwnerIdField); 
        	Field fileSizeField = new Field(FILE_SIZE_FIELD, String.valueOf(fa.getFileItem().getLengthKB()), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(fileSizeField); 
        	Field fileTimeField = new Field(FILE_TIME_FIELD, String.valueOf(fa.getModification().getDate().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(fileTimeField); 
        	Field fileNameField = new Field(FILENAME_FIELD, fa.getFileItem().getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
        	doc.add(fileNameField);
        	//create names that groups all the related values together for parsing in displays
        	//doc.add(new Field(FILE_SIZE_AND_ID_FIELD, fa.getId()+fileSizeField.stringValue(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        	doc.add(new Field(FILE_TIME_AND_ID_FIELD, Constants.UNIQUE_PREFIX + fa.getId() + fileTimeField.stringValue(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        	doc.add(new Field(FILENAME_AND_ID_FIELD, Constants.UNIQUE_PREFIX + fa.getId() + fileNameField.stringValue(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }
    }    
    //Used to index the file.  Only want info about this file, so remove extraneous stuff
    public static void addFileAttachment(Document doc, FileAttachment fa, boolean fieldsOnly) {
    	Field fileIDField = new Field(FILE_ID_FIELD, fa.getId(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(fileIDField); 
    	Field fileOwnerIdField = new Field(FILE_CREATOR_ID_FIELD, String.valueOf(fa.getCreation().getPrincipal().getId()), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(fileOwnerIdField); 
    	Field fileSizeField = new Field(FILE_SIZE_FIELD, String.valueOf(fa.getFileItem().getLengthKB()), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(fileSizeField); 
    	Field fileTimeField = new Field(FILE_TIME_FIELD, String.valueOf(fa.getModification().getDate().getTime()), Field.Store.YES, Field.Index.UN_TOKENIZED);
    	doc.add(fileTimeField); 
      	Field fileNameField = new Field(FILENAME_FIELD, fa.getFileItem().getName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(fileNameField);
       	Field fileExtField = new Field(FILE_EXT_FIELD, getFileExtension(fa.getFileItem().getName()), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(fileExtField);   	
       	Field uniqueField = new Field(FILE_UNIQUE_FIELD, Boolean.toString(fa.isCurrentlyLocked()), Field.Store.YES, Field.Index.UN_TOKENIZED);
       	doc.add(uniqueField);     	
    }
    
    // in the _allText field for this attachment, just add the contents of
    // the file attachment, it's name, and it's creator/modifier
    public static Document addFileAttachmentAllText(Document doc) {
       	String text = "";
       	doc.removeFields(Constants.ALL_TEXT_FIELD);
       	// just in case there wasn't any text from the converted file 
       	// i.e. the file didn't really exist
       	try {
       		text = doc.getField(Constants.TEMP_FILE_CONTENTS_FIELD).stringValue();
       	} catch (Exception e) {}
       	doc.removeFields(Constants.TEMP_FILE_CONTENTS_FIELD);
       	text += " " + doc.getField(Constants.FILENAME_FIELD).stringValue();
       	text += " " + doc.getField(Constants.MODIFICATION_NAME_FIELD).stringValue();
       	text += " " + doc.getField(Constants.CREATOR_NAME_FIELD).stringValue();
       	Field allText = new Field(Constants.ALL_TEXT_FIELD, text, Field.Store.NO, Field.Index.TOKENIZED);
       	doc.add(allText);
       	return doc;
    }

    public static void addAncestry(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Binder parentBinder;
    	if (entry instanceof Binder) {
    		parentBinder = (Binder)entry;  //include self in ancestor list - needed for update of acls on binder
    	} else {
    		parentBinder = entry.getParentBinder();
    		
    	}
    	
    	while (parentBinder != null) {	
    		Field ancestry = new Field(ENTRY_ANCESTRY, parentBinder.getId().toString(), Field.Store.YES, Field.Index.UN_TOKENIZED);
    		doc.add(ancestry);
    		parentBinder = ((Binder)parentBinder).getParentBinder();
    	}
    }

    public static void addBinderPath(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = new Field(ENTITY_PATH, binder.getPathName(), Field.Store.YES, Field.Index.UN_TOKENIZED);
		doc.add(path);
    }

}
