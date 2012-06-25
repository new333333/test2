/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.Collection;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
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
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.FieldFactory;

import static org.kablink.util.search.Constants.*;

/**
 * Index the fields common to all Entry types.
 *
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public class EntityIndexUtils {

    // Defines field values
    public static final String DEFAULT_NOTITLE_TITLE = "---";
        
    public static void addTitle(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        // Add the title field
    	if (entry.getTitle() != null) {
    		String title = entry.getTitle();
    		title = title.trim();
            
            if(title.length() > 0) {
            	// For the built-in title field, avoid indexing the same text twice. So don't add it in the catch-all field.
            	// Bug 740533 - Store original title rather than trimmed one in the title field
            	Field titleField = FieldFactory.createTitle(entry.getTitle());
    	        Field sortTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.SORT_TITLE_FIELD, title.toLowerCase());
    	        Field title1Field = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.TITLE1_FIELD, title.substring(0, 1));
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
                	
        	        Field extendedTitleField = new Field(Constants.EXTENDED_TITLE_FIELD, extendedTitle, Field.Store.YES, Field.Index.ANALYZED);
        	        doc.add(extendedTitleField);
        	        Field binderSortTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.BINDER_SORT_TITLE_FIELD, title.toLowerCase());
        	        doc.add(binderSortTitleField);
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
				Field bucketTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(
						Constants.NORM_TITLE, normTitle.toLowerCase());
				doc.add(bucketTitleField);
				// now add it without lowercasing
				bucketTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(
						Constants.NORM_TITLE_FIELD, normTitle);
				doc.add(bucketTitleField);
			}
		}
	}  	        
    	        
   public static void addRating(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	//rating may not exist or not be supported
    	try {
    		if(entry.getAverageRating() != null && entry.getAverageRating().getAverage() != null) {
    			//NumericField rateField = new NumericField(RATING_FIELD, Field.Store.YES, true);
    			//rateField.setDoubleValue(entry.getAverageRating().getAverage());
    			// With rating, there is always only one digit to the left of the decimal point.
    			// So we don't need to pad the number.
    			Field rateField = FieldFactory.createStoredNotAnalyzedNoNorms(RATING_FIELD, entry.getAverageRating().getAverage().toString());
	        	doc.add(rateField);
    		}
        } catch (Exception ex) {};
   	
    }

    /**
     * If an entry is in the preDeleted state, adds appropriate fields
     * to the index for the UI to manage them.
     * 
     * Note:  To sort on a field, you MUST used
     * Field.Index.NOT_ANALYZED_NO_NORMS.
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
    	Field field = FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.PRE_DELETED_FIELD, (preDeleted ? Constants.TRUE : Constants.FALSE));
    	doc.add(field);
    	
    	// Is entry preDeleted?
    	if (preDeleted) {
    		// Yes!  If we know who it was deleted by by...
        	if (null != preDeletedBy) {
        		// ...add both their ID and title to the index.
        		field = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.PRE_DELETED_BY_ID_FIELD, String.valueOf(preDeletedBy.getId().intValue()));
        		doc.add(field);
        		
            	field = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.PRE_DELETED_BY_TITLE_FIELD, preDeletedBy.getTitle());
            	doc.add(field);
        	}
        	
        	// If we know when the item was deleted...
        	if (null != preDeletedWhen) {
        		// ...add it to the index
        		field = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.PRE_DELETED_WHEN_FIELD, String.valueOf(preDeletedWhen));
        		doc.add(field);
        	}
        	
        	// If we know where the item was deleted from... 
        	if (null != preDeletedFrom) {
        		// ...add it to the index
        		field = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.PRE_DELETED_FROM_FIELD, preDeletedFrom);
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
      	Field eField = FieldFactory.createStoredNotAnalyzedNoNorms(ENTITY_FIELD, entry.getEntityType().name());
       	doc.add(eField);
    }
    public static void addDefinitionType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Integer definitionType = entry.getDefinitionType();
    	if (definitionType == null) definitionType = 0;
      	Field eField = FieldFactory.createStoredNotAnalyzedNoNorms(DEFINITION_TYPE_FIELD, definitionType.toString());
       	doc.add(eField);
    }
    public static void addEntryType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        // Add the entry type (entry or reply)
    	if (entry instanceof FolderEntry) {
	        if (((FolderEntry)entry).isTop()) {
	        	Field entryTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_ENTRY);
	        	doc.add(entryTypeField);
	        } else {
	        	Field entryTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_REPLY);
	        	doc.add(entryTypeField);
	        	
	        	FolderEntry folderEntry = (FolderEntry)entry;
	        	Field entryParentEntryId = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_PARENT_ID_FIELD, folderEntry.getParentEntry().getId().toString());
	        	doc.add(entryParentEntryId);
	        	Field entryTopEntryId = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TOP_ENTRY_ID_FIELD, folderEntry.getTopEntry().getId().toString());
	        	doc.add(entryTopEntryId);
	        	String topEntryTitle = folderEntry.getTopEntry().getTitle().toString();
	        	if (topEntryTitle.trim().equals("")) topEntryTitle = EntityIndexUtils.DEFAULT_NOTITLE_TITLE;
	        	Field entryTopEntryTitle = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TOP_ENTRY_TITLE_FIELD, topEntryTitle);
	        	doc.add(entryTopEntryTitle);
	        }
    	} else if (entry instanceof User) {
        	Field entryTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_USER);
        	doc.add(entryTypeField);
    	} else if (entry instanceof Group) {
    		Field entryTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_GROUP);
    		doc.add(entryTypeField);
    	} else if (entry instanceof Application) {
        	Field entryTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_APPLICATION);
        	doc.add(entryTypeField);
    	} else if (entry instanceof ApplicationGroup) {
    		Field entryTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_APPLICATION_GROUP);
    		doc.add(entryTypeField);
    	} 
   }
    public static void addCreation(Document doc, HistoryStamp stamp, boolean fieldsOnly) {
    	if (stamp == null) return;
    	Date creationDate = stamp.getDate();
    	Principal principal = stamp.getPrincipal();
    	if (creationDate != null) {		
    		Field creationDateField = FieldFactory.createStoredNotAnalyzedNoNorms(CREATION_DATE_FIELD, DateTools.dateToString(creationDate,DateTools.Resolution.SECOND));
    		doc.add(creationDateField);
    		//index the YYYYMMDD string
    		String dayString = formatDayString(creationDate);
    		Field creationDayField = FieldFactory.createStoredNotAnalyzedNoNorms(CREATION_DAY_FIELD, dayString);
    		doc.add(creationDayField);
    		// index the YYYYMM string
    		String yearMonthString = dayString.substring(0,6);
    		Field creationYearMonthField = FieldFactory.createStoredNotAnalyzedNoNorms(CREATION_YEAR_MONTH_FIELD, yearMonthString);
    		doc.add(creationYearMonthField);
    		// index the YYYY string
    		String yearString = dayString.substring(0,4);
    		Field creationYearField = FieldFactory.createStoredNotAnalyzedNoNorms(CREATION_YEAR_FIELD, yearString);
    		doc.add(creationYearField);
    	}
    	//Add the id of the creator (no, not that one...)
    	if (principal != null) {
            Field creationIdField = FieldFactory.createStoredNotAnalyzedNoNorms(CREATORID_FIELD, principal.getId().toString());
            doc.add(creationIdField);
            Field creationNameField = FieldFactory.createStoredNotAnalyzedNoNorms(CREATOR_NAME_FIELD, principal.getName().toString());
            doc.add(creationNameField);
            Field creationTitleField = new Field(CREATOR_TITLE_FIELD, principal.getTitle().toString(), Field.Store.YES, Field.Index.ANALYZED);
            doc.add(creationTitleField);
            Field creationSortTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(SORT_CREATOR_TITLE_FIELD, principal.getTitle().toString().toLowerCase());
            doc.add(creationSortTitleField);
        }
    }
    public static void addModification(Document doc, HistoryStamp stamp, boolean fieldsOnly) {
    	if (stamp == null) return;
    	Date modDate = stamp.getDate();
    	Principal principal = stamp.getPrincipal();
     	if (modDate != null) {
     	
     		// Add modification-date field
     		Field modificationDateField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATION_DATE_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND));
     		doc.add(modificationDateField);        
     		// index the YYYYMMDD string
     		String dayString = formatDayString(modDate);
     		Field modificationDayField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATION_DAY_FIELD, dayString);
     		doc.add(modificationDayField);
     		// index the YYYYMM string
     		String yearMonthString = dayString.substring(0,6);
     		Field modificationYearMonthField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATION_YEAR_MONTH_FIELD, yearMonthString);
     		doc.add(modificationYearMonthField);
     		// index the YYYY string
     		String yearString = dayString.substring(0,4);
     		Field modificationYearField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATION_YEAR_FIELD, yearString);
     		doc.add(modificationYearField);
     	}
       	//Add the id of the modifier 
        if (principal != null) {
        	Field modificationIdField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATIONID_FIELD, principal.getId().toString());
            doc.add(modificationIdField);
            Field modificationNameField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATION_NAME_FIELD, principal.getName().toString());
            doc.add(modificationNameField);
            Field modificationTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(MODIFICATION_TITLE_FIELD, principal.getTitle().toString());
            doc.add(modificationTitleField);
        }   
   } 
     
    public static void addReserved(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	if (entry instanceof FolderEntry) {
			HistoryStamp historyStamp = ((FolderEntry)entry).getReservation();
			if (historyStamp != null) {
				Principal lockedByUser = historyStamp.getPrincipal();
	        	Field reservedField = FieldFactory.createStoredNotAnalyzedNoNorms(RESERVEDBY_ID_FIELD, lockedByUser.getId().toString());
	            doc.add(reservedField);
			}
    	}
   } 
     
    public static void addOwner(Document doc, Principal owner, boolean fieldsOnly) {
        if (owner != null) {
        	Field ownerIdField = FieldFactory.createStoredNotAnalyzedNoNorms(OWNERID_FIELD, owner.getId().toString());
            doc.add(ownerIdField);
            Field ownerNameField = FieldFactory.createStoredNotAnalyzedNoNorms(OWNER_NAME_FIELD, owner.getName().toString());
            doc.add(ownerNameField);
            Field ownerTitleField = FieldFactory.createStoredNotAnalyzedNoNorms(OWNER_TITLE_FIELD, owner.getTitle().toString());
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
    				Field workflowStateField = FieldFactory.createStoredNotAnalyzedNoNorms(WORKFLOW_STATE_FIELD, 
   						ws.getState());
    				String workflowCaption = WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState());
    				Field workflowStateCaptionField = FieldFactory.createStoredNotAnalyzedNoNorms(WORKFLOW_STATE_CAPTION_FIELD, 
    						workflowCaption);
    				//Index the workflow state
    				doc.add(workflowStateField);
    				doc.add(workflowStateCaptionField);
    				
    				//Add the caption to the generalText field
    				Field generalTextField = BasicIndexUtils.generalTextField(workflowCaption);
            		doc.add(generalTextField);
   				
    				Definition def = ws.getDefinition();
    				if (def != null) {
    					Field workflowProcessField = FieldFactory.createNotStoredNotAnalyzedNoNorms(WORKFLOW_PROCESS_FIELD, 
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
	public static void addEvents(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	int count = 0;
		Map customAttrs = entry.getCustomAttributes();
		Set keyset = customAttrs.keySet();
		Iterator attIt = keyset.iterator();
		// look through the custom attributes of this entry for any of type EVENT, DATE, or DATE_TIME

		Set entryEventsDates = new HashSet();
		org.dom4j.Document defDoc = entry.getEntryDefDoc();
		while (attIt.hasNext()) {
			CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
			if (att.getValueType() == CustomAttribute.EVENT) {
				//See if this attribute is still in the definition
				Element attrEle = DefinitionHelper.findAttribute(att.getName(), defDoc);
				if (attrEle != null) {
					// set the event name to event + count
					Event event = (Event)att.getValue();
					List recurencesDates = event.getAllRecurrenceDates();
					List allEventDays = Event.getEventDaysFromRecurrencesDates(recurencesDates);
					entryEventsDates.addAll(allEventDays);
									
					if (att.getValue() != null) {
						doc.add(FieldFactory.createStoredNotAnalyzedNoNorms(EVENT_FIELD + count, att.getName()));
						doc.add(FieldFactory.createStoredNotAnalyzedNoNorms(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_ID, event.getId()));
						doc.add(getEntryEventDaysField(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_DATES, new HashSet(allEventDays)));
						count++;
					}
					doc.add(getRecurrenceDatesField(event, recurencesDates));
				}
			} else if (att.getValueType() == CustomAttribute.DATE) {
				Date dateValue = ((Date)att.getValue());
				if (att.getName().equals(TaskHelper.TASK_COMPLETED_DATE_ATTRIBUTE)) {
					doc.add(
							FieldFactory.createStoredNotAnalyzedNoNorms(
							TASK_COMPLETED_DATE_FIELD,
							DateTools.dateToString(dateValue, DateTools.Resolution.SECOND)));
				}
				else {
					Calendar dateAttr = new GregorianCalendar();
					dateAttr.setTime(dateValue);
					entryEventsDates.add(dateAttr);
				}
			}
		}
		
		doc.add(getEntryEventDaysField(EVENT_DATES_FIELD, entryEventsDates));
		
		// Add event count field
    	Field eventCountField = FieldFactory.createStoredNotAnalyzedNoNorms(EVENT_COUNT_FIELD, Integer.toString(count));
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
		
		return FieldFactory.createAnalyzedNoNorms(fieldName, sb.toString(), Field.Store.YES);
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

		return FieldFactory.createStoredNotAnalyzedNoNorms(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_RECURRENCE_DATES_FIELD, sb.toString());
	}
        
    public static void addCommandDefinition(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getEntryDefId() != null) {
        	Field cdefField = FieldFactory.createStoredNotAnalyzedNoNorms(COMMAND_DEFINITION_FIELD, entry.getEntryDefId());
            doc.add(cdefField);
        }
    }
        
    public static void addCreatedWithDefinition(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getCreatedWithDefinitionId() != null) {
        	Field field = FieldFactory.createStoredNotAnalyzedNoNorms(CREATED_WITH_DEFINITION_FIELD, entry.getCreatedWithDefinitionId());
            doc.add(field);
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
        	Field cdefField = FieldFactory.createNotStoredNotAnalyzedNoNorms(ENTRY_DEFINITIONS_FIELD, entryDefs);
            doc.add(cdefField);
    	}
    }
        
    public static void addFamily(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getEntryDefId() != null) {
        	org.dom4j.Document def = entry.getEntryDefDoc();
        	String family = DefinitionUtils.getFamily(def);
        	if (Validator.isNotNull(family)) {
      			Field eField = FieldFactory.createStoredNotAnalyzedNoNorms(FAMILY_FIELD, family);
    	       	doc.add(eField);	
        	}
        }
    }


     public static void addDocId(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = FieldFactory.createStoredNotAnalyzedNoNorms(DOCID_FIELD, entry.getId().toString());
        doc.add(docIdField);
       	
       	if(entry instanceof Folder) { // Folder gets another field containing its ID value, but this time as a numeric field.
       		doc.add(new NumericField(FOLDER_ID_FIELD).setLongValue(entry.getId().longValue()));
       	}
    }

    public static void addParentBinder(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Field binderIdField;
    	if (entry instanceof Binder) {
    		if (entry.getParentBinder() == null) return;
       		binderIdField = FieldFactory.createStoredNotAnalyzedNoNorms(BINDERS_PARENT_ID_FIELD, entry.getParentBinder().getId().toString());
       	    		
    	} else if (entry != null) {
    		binderIdField = FieldFactory.createStoredNotAnalyzedNoNorms(BINDER_ID_FIELD, entry.getParentBinder().getId().toString());
    	} else {
    		binderIdField = FieldFactory.createStoredNotAnalyzedNoNorms(BINDER_ID_FIELD, "");
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
		doc.add(FieldFactory.createAnalyzedNoNorms(TEAM_MEMBERS_FIELD, LongIdUtil.getIdsAsString(ids), Field.Store.NO));
		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(IS_TEAM_FIELD, "true"));
   	
    }
    // Get ids for folder read access.  Replace owner indicator with search owner search flag. Replace team indicator with team owner search flag
    public static String getFolderAclString(Binder binder) {
    	return getFolderAclString(binder, false);
    }
    public static String getFolderAclString(Binder binder, boolean includeTitleAcl) {
		Set<String> binderIds = AccessUtils.getReadAccessIds(binder, includeTitleAcl);
   		String ids = LongIdUtil.getIdsAsString(binderIds);
        return ids.trim();
    }
    public static String getFolderTeamAclString(Binder binder) {
    	Long allUsersId = Utils.getAllUsersGroupId();
    	Set teamList = binder.getTeamMemberIds();  
    	//Note: condition acls are not needed here 
    	//  since these get paired with _folderAcl:team which does have any conditions applied
    	if (teamList.contains(allUsersId)) {
    		//Add in the groups of the owner of the binder
    		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
         	ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
         	Set<Long> userGroupIds = profileDao.getAllGroupMembership(binder.getOwner().getId(), zoneId);
    		teamList.addAll(userGroupIds);
    	}
    	String ids = LongIdUtil.getIdsAsString(teamList);
    	if (teamList.contains(allUsersId)) {
    		boolean personal = Utils.isWorkareaInProfilesTree(binder);
	    	if (!personal) {
	    		ids = ids.trim() + " " + Constants.READ_ACL_GLOBAL + " " + Constants.READ_ACL_ALL;
	    	} else {
	    		ids = ids.trim() + " " + Constants.READ_ACL_ALL;
	    	}
    	}
    	return ids;
    }
    public static String getEntryAclString(Binder binder, Entry entry) {
     	if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop()) {
    		//This is a reply to a folder entry. Get the acl of the top entry
    		entry = ((FolderEntry)entry).getTopEntry();
    	}
    	if (entry.hasEntryAcl()) {
			Set<String> entryIds = AccessUtils.getReadAccessIds(entry);
	   		String ids = LongIdUtil.getIdsAsString(entryIds);
	        return ids.trim();
    	} else {
       		//This entry uses the folder ACL. Therefore we can set it to "all" without any conditions. 
    		//  The folder ACL provides the condition checking.
    		String ids;
       		boolean personal = Utils.isWorkareaInProfilesTree(binder);
       		if (personal) {
       			ids = Constants.READ_ACL_ALL;
       		} else {
       			ids = Constants.READ_ACL_ALL + " " + Constants.READ_ACL_GLOBAL;
       		}
           	return ids.trim();
    	}
    }
    
    private static void addEntryAcls(Document doc, Binder binder, Entry entry) {
		//get real entry access
    	String[] acls = StringUtil.split(getEntryAclString(binder, entry), " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, acl));
		//add entry owner
		Long owner = entry.getOwnerId();
		String ownerStr = Constants.EMPTY_ACL_FIELD;
		if (owner != null) ownerStr = String.valueOf(owner);  
		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_OWNER_ACL_FIELD, ownerStr));    	
    }

    @SuppressWarnings("unused")
	private static void addDefaultEntryAcls(Document doc, Binder binder, Entry entry) {
    	boolean personal = Utils.isWorkareaInProfilesTree(binder);
		//get default entry access
		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL));
		if (!personal) {
			doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_GLOBAL));
		}
    }

    private static void markEntryAsInheritingAcls(Document doc, Binder binder, Entry entry) {
    	if(entry instanceof FolderEntry) {
    		doc.add(new NumericField(Constants.ENTRY_ACL_PARENT_ID_FIELD).setLongValue(binder.getId().longValue()));
    	}
    }
    
    //Add acl fields for binder for storage in search engine
    private static void addBinderAcls(Document doc, Binder binder) {
    	addBinderAcls(doc, binder, false);
    }
    private static void addBinderAcls(Document doc, Binder binder, boolean includeTitleAcl) {
		//get real binder access
    	String[] acls = StringUtil.split(getFolderAclString(binder, includeTitleAcl), " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.FOLDER_ACL_FIELD, acl));
		//get team members
    	acls = StringUtil.split(getFolderTeamAclString(binder), " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.TEAM_ACL_FIELD, acl));
		//add binder owner
		Long owner = binder.getOwnerId();
		String ownerStr = Constants.EMPTY_ACL_FIELD;
		if (owner != null) ownerStr = owner.toString();  //TODO fix this
		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.BINDER_OWNER_ACL_FIELD, ownerStr));    	
    }
    //Add acl fields for binder for storage in dom4j documents.
    //In this case replace owner with real owner in _folderAcl
 	//The extra field is not necessary cause bulk updating is not done
    private static void addBinderAcls(org.dom4j.Element parent, Binder binder) {
    	addBinderAcls(parent, binder, false);
    }
    private static void addBinderAcls(org.dom4j.Element parent, Binder binder, boolean includeTitleAcl) {
    	Long allUsersId = Utils.getAllUsersGroupId();
		Set<String> binderIds = AccessUtils.getReadAccessIds(binder, includeTitleAcl);
      	String ids = LongIdUtil.getIdsAsString(binderIds);
    	Element acl = parent.addElement(Constants.FOLDER_ACL_FIELD);
   		acl.setText(ids);
   		//add Team 
   		//  Don't need to add conditions since this is paired with _folderAcl:team which does have conditions applied
   		acl = parent.addElement(Constants.TEAM_ACL_FIELD);
   		String tms = binder.getTeamMemberString();
   		if (binder.getTeamMemberIds().contains(allUsersId)) {
   			tms = tms + " " + Constants.READ_ACL_GLOBAL + " " + Constants.READ_ACL_ALL;
   		}
   		acl.setText(tms);
    }
    
    public static void addReadAccess(Document doc, Binder binder, boolean fieldsOnly) {
    	addReadAccess(doc, binder, fieldsOnly, false);
    }
    public static void addReadAccess(Document doc, Binder binder, boolean fieldsOnly, boolean includeTitleAcl) {
    	boolean personal = Utils.isWorkareaInProfilesTree(binder);
    	//set entryAcl to "all" and 
		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL));
		if (!personal) {
			doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_GLOBAL));
		}
		//add binder acls
		addBinderAcls(doc, binder, includeTitleAcl);
    }
    public static void addReadAccess(org.dom4j.Element parent, Binder binder, boolean fieldsOnly) {
    	addReadAccess(parent, binder, fieldsOnly, false);
    }
    public static void addReadAccess(org.dom4j.Element parent, Binder binder, boolean fieldsOnly, boolean includeTitleAcl) {
    	boolean personal = Utils.isWorkareaInProfilesTree(binder);
    	//set entryAcl to all
   		Element  acl = parent.addElement(Constants.ENTRY_ACL_FIELD);
 		if (personal) {
 			acl.setText(Constants.READ_ACL_ALL);
 		} else {
 			acl.setText(Constants.READ_ACL_ALL + " " + Constants.READ_ACL_GLOBAL);
 		}
		//add binder access
   		addBinderAcls(parent, binder, includeTitleAcl);
    }
    private static String getWfEntryAccess(WorkflowSupport wEntry) {
    	@SuppressWarnings("unused")
		Long allUsersId = Utils.getAllUsersGroupId();
    	boolean personal = false;
    	if (wEntry instanceof FolderEntry) 
    		personal = Utils.isWorkareaInProfilesTree(((FolderEntry)wEntry).getParentBinder());
    	//get principals given read access 
     	Set<Long> ids = wEntry.getStateMembers(WfAcl.AccessType.read);
     	//replace owner indicator, but leave team member alone for now
        //for entries, the owner is stored in the entry acl and not its own field like binders
     	//The extra field is not necessary cause updating does not have to optimized
     	if (ids.remove(ObjectKeys.OWNER_USER_ID)) ids.add(wEntry.getOwnerId());
     	if (wEntry instanceof FolderEntry && !((FolderEntry)wEntry).isTop()) {
     		//This is a reply. Make sure to also check for access to the top entry
     		WorkflowSupport wEntryTop = ((FolderEntry)wEntry).getTopEntry();
     		Set idsTop = wEntryTop.getStateMembers(WfAcl.AccessType.read);
     		if (idsTop.remove(ObjectKeys.OWNER_USER_ID)) idsTop.add(wEntryTop.getOwnerId());
     		if (ids.isEmpty()) {
     			ids.addAll(idsTop);
     		}
     		if (!idsTop.isEmpty() && !wEntryTop.isWorkAreaAccess(WfAcl.AccessType.read)) {
     			//The top entry has specified a list of ids and has not specified "folder default", so filter the ids
	     		Set<Long> idsToDelete = new HashSet<Long>();
	     		for (Long id : ids) {
	     			//If the top entry does not also allow this id, the remove the id
	     			if (!idsTop.contains(id)) idsToDelete.add(id);
	     		}
	     		for (Long id : idsToDelete) {
	     			ids.remove(id);
	     		}
     		}
     	}
     	// I'm not sure if putting together a long string value is more
     	// 	efficient than processing multiple short strings... We will see.
     	StringBuffer pIds = new StringBuffer(LongIdUtil.getIdsAsString(ids));
   		if (ids.isEmpty() || wEntry.isWorkAreaAccess(WfAcl.AccessType.read)) {
   			//add all => folder check
   			if (personal) {
   	   			pIds.append(Constants.READ_ACL_ALL);
   			} else {
   	   			pIds.append(Constants.READ_ACL_ALL + " " + Constants.READ_ACL_GLOBAL);
   			}
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
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
     	ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
     	Set<Long> userGroupIds = profileDao.getAllGroupMembership(entry.getId(), zoneId);
     	ids.addAll(userGroupIds);
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
       			String[] acls = StringUtil.split(getWfEntryAccess(wEntry), " ");
	       		for(String acl:acls) {
	       			doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, acl));
	       		}
	       		//add binder access
	    		addBinderAcls(doc, binder);
       		} else {
	       		//add entry access. 
	       		if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop()) {
	       			//Make sure to use the acl of the top entry since replies use the top entry acl (unless they specify their own acl)
	       			entry = ((FolderEntry)entry).getTopEntry();
	       			wEntry = (WorkflowSupport)entry;
	       		}
	       		if (wEntry.hasAclSet()) {
	       			//This must have been a reply not running a workflow, so check the top entry workflow ACL
	       			String[] acls = StringUtil.split(getWfEntryAccess(wEntry), " ");
	       			for(String acl:acls) {
	       				doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, acl));
	       			}
	           		//add binder access
	        		addBinderAcls(doc, binder);
	       		} else if (((Entry)entry).hasEntryAcl()) {
	    			//The entry has its own ACL specified
	       			addEntryAcls(doc, binder, (Entry)entry);
	           		//add binder access
	        		addBinderAcls(doc, binder);
	    		} else {
	    			// The entry has neither workflow ACL nor its own ACL.
	    			//The entry is using the folder's ACL
	    			markEntryAsInheritingAcls(doc, binder, (Entry)entry);
	    		}
       		}
    	} else if (entry instanceof FolderEntry) {
    		//(This case may no longer be valid now that workflow is included in the Kablink build)
       		// Add the Entry_ACL field
       		if (!((FolderEntry)entry).isTop()) {
       			//Make sure to use the acl of the top entry since replies use the top entry acl
       			entry = ((FolderEntry)entry).getTopEntry();
       		}
    		if (((Entry)entry).hasEntryAcl()) {
    			addEntryAcls(doc, binder, (Entry)entry);
           		//add binder access
        		addBinderAcls(doc, binder);
    		} else {
    			markEntryAsInheritingAcls(doc, binder, (Entry)entry);
    		}
    	} else if (entry instanceof User) {
            // Add the Entry_ACL field
    		String[] acls = StringUtil.split(getUserEntryAccess((User)entry), " ");
    		for(String acl:acls)
    			doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ENTRY_ACL_FIELD, acl));
           	//add binder access
        	addBinderAcls(doc, binder);

    	} else {
    		addReadAccess(doc, binder, fieldsOnly);
    	}
	}
    //This is used to store the "read" acls in a document that is not a search document
    public static void addReadAccess(org.dom4j.Element parent, Binder binder, DefinableEntity entry, boolean fieldsOnly) {
		// Add ACL field. We only need to index ACLs for read access.
   		if (entry instanceof WorkflowSupport) {
  	   		WorkflowSupport wEntry = (WorkflowSupport)entry;
       		// Add the Entry_ACL field
   	   		Element acl = parent.addElement(Constants.ENTRY_ACL_FIELD);

       		// Add the Entry_ACL field
       		if (wEntry.hasAclSet()) {
           		acl.setText(getWfEntryAccess(wEntry));
       		} else {
	       		//add entry access of the top entry. 
	       		if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop()) {
	       			//Make sure to use the acl of the top entry since replies use the top entry acl
	       			entry = ((FolderEntry)entry).getTopEntry();
	       			wEntry = (WorkflowSupport)entry;
	       		}
	       		acl.setText(getWfEntryAccess(wEntry));
	       	}
       		//add binder access
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
    	   	
    	Map<String, List<Tag>> uniqueTags = TagUtil.splitTags(allTags);
    	List<Tag> pubTags = uniqueTags.get(ObjectKeys.COMMUNITY_ENTITY_TAGS);
    	List<Tag> privTags = uniqueTags.get(ObjectKeys.PERSONAL_ENTITY_TAGS);
    	Field ttfTagField = FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.TAG_FIELD_TTF, "");
    	// index all the public tags (allTags field and tag_acl field)
		for (Tag thisTag: pubTags) {
			tag = thisTag.getName();
			indexableTags += " " + tag;
			// Add the ttf fields.  The ttf fields are case insensitive before the ":",
			// so users can search and find tags regardless of case. The strategy is to use
			// this field for the type to find searches, but keep the original fields for 
			// display.
			tag = tag.toLowerCase() + ":" + tag; 
			ttfTagField = FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.TAG_FIELD_TTF, tag);
			doc.add(ttfTagField);
			aclTag = BasicIndexUtils.buildAclTag(thisTag.getName(), Constants.READ_ACL_ALL);
			aclTags += " " + aclTag;
			lowerAclTag = BasicIndexUtils.buildAclTag(thisTag.getName().toLowerCase(), Constants.READ_ACL_ALL);
			aclTag = lowerAclTag + ":" + aclTag;
			ttfTagField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ACL_TAG_FIELD_TTF, aclTag);
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
			ttfTagField = FieldFactory.createStoredNotAnalyzedNoNorms(Constants.ACL_TAG_FIELD_TTF, aclTag);
			doc.add(ttfTagField);
		}
    
    	Field tagField = new Field(Constants.TAG_FIELD, indexableTags, Field.Store.YES, Field.Index.NO);
    	doc.add(tagField);
    	String[] its = StringUtil.split(indexableTags, " ");
    	for(String it:its)
    		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.TAG_FIELD, it));
    	
    	if (!fieldsOnly) {
    		tagField = BasicIndexUtils.generalTextField(indexableTags);
    		doc.add(tagField);
    	}
    	
    	String[] acls = StringUtil.split(aclTags, " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createNotStoredNotAnalyzedNoNorms(Constants.ACL_TAG_FIELD, acl));
    }
	
    @SuppressWarnings("unused")
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
		
      	
		Field fileTypeField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_TYPE_FIELD, x.getText());
       	doc.add(fileTypeField);   	
	
		
		return;    
    }
    
    public static String getFileNameWithoutExtension(String fileName) {
        int extensionStart = fileName.lastIndexOf('.');
        String name = "";
        if (extensionStart >= 0) {
            name = fileName.substring(0, extensionStart);
        }
        return name;
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
        	Field fileIDField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_ID_FIELD, fa.getId());
        	doc.add(fileIDField); 
        	Field fileOwnerIdField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_CREATOR_ID_FIELD, String.valueOf(fa.getCreation().getPrincipal().getId()));
        	doc.add(fileOwnerIdField); 
        	Field fileSizeField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_SIZE_FIELD, getSortableNumber(String.valueOf(fa.getFileItem().getLengthKB()), ObjectKeys.MAX_FILE_SIZE_DECIMAL_PLACES));
        	doc.add(fileSizeField); 
        	Field fileSizeInBytesField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_SIZE_IN_BYTES_FIELD, String.valueOf(fa.getFileItem().getLength()));
        	doc.add(fileSizeInBytesField); 
        	Field fileTimeField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_TIME_FIELD, String.valueOf(fa.getModification().getDate().getTime()));
        	doc.add(fileTimeField); 
        	Field fileNameField = FieldFactory.createStoredNotAnalyzedNoNorms(FILENAME_FIELD, fa.getFileItem().getName());
        	doc.add(fileNameField);
        	//create names that groups all the related values together for parsing in displays
        	//doc.add(new Field(FILE_SIZE_AND_ID_FIELD, fa.getId()+fileSizeField.stringValue()));
        	doc.add(FieldFactory.createStoredNotAnalyzedNoNorms(FILE_TIME_AND_ID_FIELD, Constants.UNIQUE_PREFIX + fa.getId() + fileTimeField.stringValue()));
        	doc.add(FieldFactory.createStoredNotAnalyzedNoNorms(FILENAME_AND_ID_FIELD, Constants.UNIQUE_PREFIX + fa.getId() + fileNameField.stringValue()));
        }
        //While we are here, make sure the version agingEnabled flags are set properly
        FileUtils.setFileVersionAging(entry);
    }    
    //Used to index the file.  Only want info about this file, so remove extraneous stuff
    public static void addFileAttachment(Document doc, FileAttachment fa, boolean fieldsOnly) {
    	Field fileIDField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_ID_FIELD, fa.getId());
    	doc.add(fileIDField); 
    	Field fileOwnerIdField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_CREATOR_ID_FIELD, String.valueOf(fa.getCreation().getPrincipal().getId()));
    	doc.add(fileOwnerIdField); 
    	Field fileSizeField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_SIZE_FIELD, getSortableNumber(String.valueOf(fa.getFileItem().getLengthKB()), ObjectKeys.MAX_FILE_SIZE_DECIMAL_PLACES));
    	doc.add(fileSizeField); 
    	Field fileSizeInBytesField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_SIZE_IN_BYTES_FIELD, String.valueOf(fa.getFileItem().getLength()));
    	doc.add(fileSizeInBytesField); 
    	Field fileTimeField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_TIME_FIELD, String.valueOf(fa.getModification().getDate().getTime()));
    	doc.add(fileTimeField); 
      	Field fileNameField = FieldFactory.createStoredNotAnalyzedNoNorms(FILENAME_FIELD, fa.getFileItem().getName());
       	doc.add(fileNameField);
      	Field fileDescField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_DESCRIPTION_FIELD, fa.getFileItem().getDescription().getText());
       	doc.add(fileDescField);
      	Field fileStatusField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_STATUS_FIELD, String.valueOf(fa.getFileStatus()));
       	doc.add(fileStatusField);
       	Field fileExtField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_EXT_FIELD, getFileExtension(fa.getFileItem().getName()));
       	doc.add(fileExtField);   	
       	Field uniqueField = FieldFactory.createStoredNotAnalyzedNoNorms(FILE_UNIQUE_FIELD, Boolean.toString(fa.isCurrentlyLocked()));
       	doc.add(uniqueField);     	
    }
    
    public static String getSortableNumber(String number, int maxLength) {
    	final String zeros = "00000000000000000000";
    	int leadingZeros = maxLength - number.length();
    	if (leadingZeros <= 0) return number;
    	return zeros.substring(0, leadingZeros) + number;
    }
    
    // in the _generalText field for this attachment, just add the contents of
    // the file attachment, it's name, and it's creator/modifier
    public static Document addFileAttachmentGeneralText(Document doc) {
       	String text = "";
       	doc.removeFields(Constants.GENERAL_TEXT_FIELD);
       	// just in case there wasn't any text from the converted file 
       	// i.e. the file didn't really exist
       	try {
       		text = doc.getFieldable(Constants.TEMP_FILE_CONTENTS_FIELD).stringValue();
       	} catch (Exception e) {}
       	doc.removeFields(Constants.TEMP_FILE_CONTENTS_FIELD);
       	text += " " + doc.getFieldable(Constants.FILENAME_FIELD).stringValue();
       	text += " " + doc.getFieldable(Constants.FILE_DESCRIPTION_FIELD).stringValue();
       	text += " " + doc.getFieldable(Constants.MODIFICATION_NAME_FIELD).stringValue();
       	text += " " + doc.getFieldable(Constants.CREATOR_NAME_FIELD).stringValue();
       	Field generalText = BasicIndexUtils.generalTextField(text);
       	doc.add(generalText);
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
    		Field ancestry = FieldFactory.createStoredNotAnalyzedNoNorms(ENTRY_ANCESTRY, parentBinder.getId().toString());
    		doc.add(ancestry);
    		parentBinder = ((Binder)parentBinder).getParentBinder();
    	}
    }

    public static void addBinderIconName(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createStoredNotAnalyzedNoNorms(ICON_NAME_FIELD, binder.getIconName());
		doc.add(path);
    }

    public static void addBinderPath(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createStoredNotAnalyzedNoNorms(ENTITY_PATH, binder.getPathName());
		doc.add(path);
    }

    public static void addBinderIsLibrary(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createStoredNotAnalyzedNoNorms(IS_LIBRARY_FIELD, (binder.isLibrary() ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }

    public static void addBinderIsMirrored(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createStoredNotAnalyzedNoNorms(IS_MIRRORED_FIELD, (binder.isMirrored() ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }

    public static void addBinderHasResourceDriver(Document doc, Binder binder, boolean fieldsOnly) {
    	if ((binder instanceof Folder) && binder.isMirrored()) {
    		boolean hasResourceDriver = MiscUtil.hasString(binder.getResourceDriverName());
    		Field path = FieldFactory.createStoredNotAnalyzedNoNorms(HAS_RESOURCE_DRIVER_FIELD, (hasResourceDriver ? Constants.TRUE : Constants.FALSE));
    		doc.add(path);
    	}
    }

    public static void addBinderIsTopFolder(Document doc, Binder binder, boolean fieldsOnly) {
    	if (binder instanceof Folder) {
    		Field path = FieldFactory.createStoredNotAnalyzedNoNorms(IS_TOP_FOLDER_FIELD, (((Folder)binder).isTop() ? Constants.TRUE : Constants.FALSE));
    		doc.add(path);
    	}
    }

}
