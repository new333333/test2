/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.ArrayList;
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
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WfAcl;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.CloudFolderHelper;
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
    
    private static final int DEFAULT_MAX_EVENT_DAYS	= 3650;	// (365 * 10) = 3650:  ~10 years.
    
    private static BinderModule m_binderModule = null;
    
    /**
     * 
     */
	private static BinderModule getBinderModule()
	{
		if ( m_binderModule == null )
			m_binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");

		return m_binderModule;
	}

	public static void addTitle(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        // Add the title field
    	if (entry.getTitle() != null) {
    		String title = entry.getTitle();
    		title = title.trim();
            
            if(title.length() > 0) {
            	// For the built-in title field, avoid indexing the same text twice. So don't add it in the catch-all field.
            	// Bug 740533 - Store original title rather than trimmed one in the title field
            	Field titleField = FieldFactory.createFullTextFieldIndexed(Constants.TITLE_FIELD, entry.getTitle(), true);
            	// This field is used only for sorting, so needs not be stored.
    	        Field sortTitleField = FieldFactory.createFieldNotStoredNotAnalyzed(Constants.SORT_TITLE_FIELD, title.toLowerCase());
    	        Field title1Field = FieldFactory.createFieldStoredNotAnalyzed(Constants.TITLE1_FIELD, title.substring(0, 1));
    	        // Create another field instance with the same name as the primary title field, and put the lower-cased
    	        // version of the title value as a single term, so as to allow accurate wild care matches using * or ?
    	        // An alternate approach would be to use SORT_TITLE_FIELD field since it already has the single token
    	        // value indexed. However, that would require changing the default search code to include that additional
    	        // field by default, which makes things more complicated. Besides, this literal string match on title
    	        // is useful primarily for file entries where the title happens to represent the file name in the entry.
    	        // For all other entry types, this isn't useful, so the extra field isn't generic enough to be included
    	        // in all queries by default.
    	        Field lowercasedSingleTermTitleField = FieldFactory.createField(Constants.TITLE_FIELD, title.toLowerCase(), Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS, false);
    	        		
    	        doc.add(titleField);
    	        doc.add(sortTitleField);
                doc.add(title1Field);
                doc.add(lowercasedSingleTermTitleField);
                if (entry instanceof Binder) {
                	Binder binder = (Binder)entry;
                	//Special case: user workspaces and top workspace don't show parent folder 
                	String extendedTitle = title;
                	if (!binder.isRoot() && !binder.getParentBinder().getEntityType().equals(EntityType.profiles)) {
                		extendedTitle = title + " (" + entry.getParentBinder().getTitle() + ")";
                	} 
                	
        	        Field extendedTitleField = FieldFactory.createFullTextFieldIndexed(Constants.EXTENDED_TITLE_FIELD, extendedTitle, true);
        	        doc.add(extendedTitleField);
        	        Field binderSortTitleField = FieldFactory.createFieldStoredNotAnalyzed(Constants.BINDER_SORT_TITLE_FIELD, title.toLowerCase());
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
				Field bucketTitleField = FieldFactory.createFieldStoredNotAnalyzed(Constants.NORM_TITLE, normTitle.toLowerCase());
				doc.add(bucketTitleField);
				// now add it without lowercasing
				bucketTitleField = FieldFactory.createFieldStoredNotAnalyzed(Constants.NORM_TITLE_FIELD, normTitle);
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
    			Field rateField = FieldFactory.createFieldStoredNotAnalyzed(RATING_FIELD, entry.getAverageRating().getAverage().toString());
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
    	Field field = FieldFactory.createFieldNotStoredNotAnalyzed(Constants.PRE_DELETED_FIELD, (preDeleted ? Constants.TRUE : Constants.FALSE));
    	doc.add(field);
    	
    	// Is entry preDeleted?
    	if (preDeleted) {
    		// Yes!  If we know who it was deleted by by...
        	if (null != preDeletedBy) {
        		// ...add both their ID and title to the index.
        		field = FieldFactory.createFieldStoredNotAnalyzed(Constants.PRE_DELETED_BY_ID_FIELD, String.valueOf(preDeletedBy.getId().intValue()));
        		doc.add(field);
        		
            	field = FieldFactory.createFieldStoredNotAnalyzed(Constants.PRE_DELETED_BY_TITLE_FIELD, preDeletedBy.getTitle());
            	doc.add(field);
        	}
        	
        	// If we know when the item was deleted...
        	if (null != preDeletedWhen) {
        		// ...add it to the index
        		field = FieldFactory.createFieldStoredNotAnalyzed(Constants.PRE_DELETED_WHEN_FIELD, String.valueOf(preDeletedWhen));
        		doc.add(field);
        	}
        	
        	// If we know where the item was deleted from... 
        	if (null != preDeletedFrom) {
        		// ...add it to the index
        		field = FieldFactory.createFieldStoredNotAnalyzed(Constants.PRE_DELETED_FROM_FIELD, preDeletedFrom);
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
      	Field eField = FieldFactory.createFieldStoredNotAnalyzed(ENTITY_FIELD, entry.getEntityType().name());
       	doc.add(eField);
    }
    public static void addDefinitionType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Integer definitionType = entry.getDefinitionType();
    	if (definitionType == null) definitionType = 0;
      	Field eField = FieldFactory.createFieldStoredNotAnalyzed(DEFINITION_TYPE_FIELD, definitionType.toString());
       	doc.add(eField);
    }
    public static void addEntryType(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        // Add the entry type (entry or reply)
    	if (entry instanceof FolderEntry) {
	        if (((FolderEntry)entry).isTop()) {
	        	Field entryTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_ENTRY);
	        	doc.add(entryTypeField);
	        } else {
	        	Field entryTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_REPLY);
	        	doc.add(entryTypeField);
	        	
	        	FolderEntry folderEntry = (FolderEntry)entry;
	        	Field entryParentEntryId = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_PARENT_ID_FIELD, folderEntry.getParentEntry().getId().toString());
	        	doc.add(entryParentEntryId);
	        	Field entryTopEntryId = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TOP_ENTRY_ID_FIELD, folderEntry.getTopEntry().getId().toString());
	        	doc.add(entryTopEntryId);
	        	String topEntryTitle = folderEntry.getTopEntry().getTitle().toString();
	        	if (topEntryTitle.trim().equals("")) topEntryTitle = EntityIndexUtils.DEFAULT_NOTITLE_TITLE;
	        	Field entryTopEntryTitle = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TOP_ENTRY_TITLE_FIELD, topEntryTitle);
	        	doc.add(entryTopEntryTitle);
	        }
    	} else if (entry instanceof User) {
        	Field entryTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_USER);
        	doc.add(entryTypeField);
    	} else if (entry instanceof Group) {
    		Field entryTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_GROUP);
    		doc.add(entryTypeField);
    	} else if (entry instanceof Application) {
        	Field entryTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_APPLICATION);
        	doc.add(entryTypeField);
    	} else if (entry instanceof ApplicationGroup) {
    		Field entryTypeField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_APPLICATION_GROUP);
    		doc.add(entryTypeField);
    	} 
   }
    
    public static void addEntryPath(Document doc, Entry entry) {
    	Binder parent = entry.getParentBinder();
    	if(parent == null) return;
    	if(!parent.isLibrary()) return;
    	Set<FileAttachment> fAtts = entry.getFileAttachments();
    	if(fAtts.size() != 1) return;
    	String fileName = fAtts.iterator().next().getFileItem().getName();
		Field sortPath = FieldFactory.createFieldNotStoredNotAnalyzed(SORT_ENTITY_PATH, parent.getPathName().toLowerCase() + "/" + fileName.toLowerCase());
		doc.add(sortPath);
    }

    public static void addHiddenSearchField(Document doc, DefinableEntity entry, boolean hidden) {
    	if (hidden) {
	      	Field eField = FieldFactory.createFieldStoredNotAnalyzed(HIDDEN_FROM_SEARCH_FIELD, "true");
	       	doc.add(eField);
    	}
    }

    public static void addHiddenFindUserField(Document doc, DefinableEntity entry, boolean hidden) {
    	if (hidden) {
	      	Field eField = FieldFactory.createFieldStoredNotAnalyzed(HIDDEN_FROM_FIND_USER_FIELD, "true");
	       	doc.add(eField);
    	}
    }

    public static void addCreation(Document doc, HistoryStamp stamp, boolean fieldsOnly) {
    	if (stamp == null) return;
    	Date creationDate = stamp.getDate();
    	Principal principal = stamp.getPrincipal();
    	if (creationDate != null) {		
    		Field creationDateField = FieldFactory.createFieldStoredNotAnalyzed(CREATION_DATE_FIELD, DateTools.dateToString(creationDate,DateTools.Resolution.SECOND));
    		doc.add(creationDateField);
    		//index the YYYYMMDD string
    		String dayString = formatDayString(creationDate);
    		Field creationDayField = FieldFactory.createFieldStoredNotAnalyzed(CREATION_DAY_FIELD, dayString);
    		doc.add(creationDayField);
    		// index the YYYYMM string
    		String yearMonthString = dayString.substring(0,6);
    		Field creationYearMonthField = FieldFactory.createFieldStoredNotAnalyzed(CREATION_YEAR_MONTH_FIELD, yearMonthString);
    		doc.add(creationYearMonthField);
    		// index the YYYY string
    		String yearString = dayString.substring(0,4);
    		Field creationYearField = FieldFactory.createFieldStoredNotAnalyzed(CREATION_YEAR_FIELD, yearString);
    		doc.add(creationYearField);
    	}
    	//Add the id of the creator (no, not that one...)
    	if (principal != null) {
            Field creationIdField = FieldFactory.createFieldStoredNotAnalyzed(CREATORID_FIELD, principal.getId().toString());
            doc.add(creationIdField);
            Field creationNameField = FieldFactory.createFieldStoredNotAnalyzed(CREATOR_NAME_FIELD, principal.getName().toString());
            doc.add(creationNameField);
            Field creationTitleField = FieldFactory.createFullTextFieldIndexed(CREATOR_TITLE_FIELD, principal.getTitle().toString(), true);
            doc.add(creationTitleField);
            Field creationSortTitleField = FieldFactory.createFieldStoredNotAnalyzed(SORT_CREATOR_TITLE_FIELD, principal.getTitle().toString().toLowerCase());
            doc.add(creationSortTitleField);
        }
    }
    public static void addModification(Document doc, HistoryStamp stamp, boolean fieldsOnly) {
    	if (stamp == null) return;
    	Date modDate = stamp.getDate();
    	Principal principal = stamp.getPrincipal();
     	if (modDate != null) {
     	
     		// Add modification-date field
     		Field modificationDateField = FieldFactory.createFieldStoredNotAnalyzed(MODIFICATION_DATE_FIELD, DateTools.dateToString(modDate,DateTools.Resolution.SECOND));
     		doc.add(modificationDateField);        
     		// index the YYYYMMDD string
     		String dayString = formatDayString(modDate);
     		Field modificationDayField = FieldFactory.createFieldStoredNotAnalyzed(MODIFICATION_DAY_FIELD, dayString);
     		doc.add(modificationDayField);
     		// index the YYYYMM string
     		String yearMonthString = dayString.substring(0,6);
     		Field modificationYearMonthField = FieldFactory.createFieldStoredNotAnalyzed(MODIFICATION_YEAR_MONTH_FIELD, yearMonthString);
     		doc.add(modificationYearMonthField);
     		// index the YYYY string
     		String yearString = dayString.substring(0,4);
     		Field modificationYearField = FieldFactory.createFieldStoredNotAnalyzed(MODIFICATION_YEAR_FIELD, yearString);
     		doc.add(modificationYearField);
     	}
       	//Add the id of the modifier 
        if (principal != null) {
        	Field modificationIdField = FieldFactory.createFieldStoredNotAnalyzed(MODIFICATIONID_FIELD, principal.getId().toString());
            doc.add(modificationIdField);
            Field modificationNameField = FieldFactory.createFieldStoredNotAnalyzed(MODIFICATION_NAME_FIELD, principal.getName().toString());
            doc.add(modificationNameField);
            Field modificationTitleField = FieldFactory.createFullTextFieldIndexed(MODIFICATION_TITLE_FIELD, principal.getTitle().toString(), true);
            doc.add(modificationTitleField);
        }   
   } 
     
    public static void addReserved(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	if (entry instanceof FolderEntry) {
			HistoryStamp historyStamp = ((FolderEntry)entry).getReservation();
			if (historyStamp != null) {
				Principal lockedByUser = historyStamp.getPrincipal();
	        	Field reservedField = FieldFactory.createFieldStoredNotAnalyzed(RESERVEDBY_ID_FIELD, lockedByUser.getId().toString());
	            doc.add(reservedField);
			}
    	}
   } 
     
    public static void addOwner(Document doc, Principal owner, boolean fieldsOnly) {
        if (owner != null) {
        	Field ownerIdField = FieldFactory.createFieldStoredNotAnalyzed(OWNERID_FIELD, owner.getId().toString());
            doc.add(ownerIdField);
            Field ownerNameField = FieldFactory.createFieldStoredNotAnalyzed(OWNER_NAME_FIELD, owner.getName().toString());
            doc.add(ownerNameField);
            Field ownerTitleField = FieldFactory.createFieldStoredNotAnalyzed(OWNER_TITLE_FIELD, owner.getTitle().toString());
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
    				Field workflowStateField = FieldFactory.createFieldStoredNotAnalyzed(WORKFLOW_STATE_FIELD, 
   						ws.getState());
    				String workflowCaption = WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState());
    				Field workflowStateCaptionField = FieldFactory.createFieldStoredNotAnalyzed(WORKFLOW_STATE_CAPTION_FIELD, 
    						workflowCaption);
    				//Index the workflow state
    				doc.add(workflowStateField);
    				doc.add(workflowStateCaptionField);
    				
    				//Add the caption to the generalText field
    				Field generalTextField = BasicIndexUtils.generalTextField(workflowCaption);
            		doc.add(generalTextField);
   				
    				Definition def = ws.getDefinition();
    				if (def != null) {
    					Field workflowProcessField = FieldFactory.createFieldNotStoredNotAnalyzed(WORKFLOW_PROCESS_FIELD, def.getId());
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
					int maxEventDaysToIndex = getMaxEventDaysToIndex();
					List recurencesDates = event.getAllRecurrenceDatesForIndexing(maxEventDaysToIndex);
					List allEventDays = Event.getEventDaysFromRecurrencesDatesForIndexing(recurencesDates, maxEventDaysToIndex);
					entryEventsDates.addAll(allEventDays);
									
					if (att.getValue() != null) {
						doc.add(FieldFactory.createFieldStoredNotAnalyzed(EVENT_FIELD + count, att.getName()));
						doc.add(FieldFactory.createFieldStoredNotAnalyzed(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_ID, event.getId()));
						doc.add(getEntryEventDaysField(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_DATES, new HashSet(allEventDays)));
						count++;
					}
					doc.add(getRecurrenceDatesField(event, recurencesDates));
				}
			} else if (att.getValueType() == CustomAttribute.DATE) {
				Date dateValue = ((Date)att.getValue());
				if (att.getName().equals(TaskHelper.TASK_COMPLETED_DATE_ATTRIBUTE)) {
					doc.add(
							FieldFactory.createFieldStoredNotAnalyzed(
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
    	Field eventCountField = FieldFactory.createFieldStoredNotAnalyzed(EVENT_COUNT_FIELD, Integer.toString(count));
    	doc.add(eventCountField);
    }
	
	/*
	 * Returns the maximum number of days that can contribute to the
	 * event information added to the index.
	 */
	private static int getMaxEventDaysToIndex() {
		int reply = SPropsUtil.getInt("max.event.days.to.index", DEFAULT_MAX_EVENT_DAYS);
		if (0 == reply) {
			reply = Integer.MAX_VALUE;
		}
		return reply;
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
		
		return FieldFactory.createField(fieldName, sb.toString(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);
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

		return FieldFactory.createFieldStoredNotAnalyzed(event.getName() + BasicIndexUtils.DELIMITER + Constants.EVENT_RECURRENCE_DATES_FIELD, sb.toString());
	}
        
    public static void addCommandDefinition(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getEntryDefId() != null) {
        	Field cdefField = FieldFactory.createFieldStoredNotAnalyzed(COMMAND_DEFINITION_FIELD, entry.getEntryDefId());
            doc.add(cdefField);
        }
    }
        
    public static void addCreatedWithDefinition(Document doc, DefinableEntity entry, boolean fieldsOnly) {
        if (entry.getCreatedWithDefinitionId() != null) {
        	Field field = FieldFactory.createFieldStoredNotAnalyzed(CREATED_WITH_DEFINITION_FIELD, entry.getCreatedWithDefinitionId());
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
        	Field cdefField = FieldFactory.createFieldNotStoredNotAnalyzed(ENTRY_DEFINITIONS_FIELD, entryDefs);
            doc.add(cdefField);
    	}
    }
        
    public static void addFamily(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	String family = null;
    	if(entry.supportsCustomFields()) {
            if (entry.getEntryDefId() != null) {
            	org.dom4j.Document def = entry.getEntryDefDoc();
            	family = DefinitionUtils.getFamily(def);
            }    		
    	}
    	else {
    		// The only entities not support custom fields are net folders and files. 
    		// Short circuit the use of definition facility for better efficiency.
    		family = Constants.FAMILY_FIELD_FILE;
    	}
    	if (Validator.isNotNull(family)) {
  			Field eField = FieldFactory.createFieldStoredNotAnalyzed(FAMILY_FIELD, family);
	       	doc.add(eField);	
    	}	
    }


     public static void addDocId(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docIdField = FieldFactory.createFieldStoredNotAnalyzed(DOCID_FIELD, entry.getId().toString());
        doc.add(docIdField);
        
       	// Entity gets another field containing its ID value, but this time as a numeric field.
       	doc.add(new NumericField(ENTITY_ID_FIELD).setLongValue(entry.getId().longValue()));
    }

    public static void addParentBinder(Document doc, DefinableEntity entry, boolean fieldsOnly) {
    	Field binderIdField;
    	if (entry instanceof Binder) {
    		if (entry.getParentBinder() == null) return;
       		binderIdField = FieldFactory.createFieldStoredNotAnalyzed(BINDERS_PARENT_ID_FIELD, entry.getParentBinder().getId().toString());
       	    		
    	} else if (entry != null) {
    		binderIdField = FieldFactory.createFieldStoredNotAnalyzed(BINDER_ID_FIELD, entry.getParentBinder().getId().toString());
    	} else {
    		binderIdField = FieldFactory.createFieldStoredNotAnalyzed(BINDER_ID_FIELD, "");
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
		doc.add(FieldFactory.createField(TEAM_MEMBERS_FIELD, LongIdUtil.getIdsAsString(ids), Field.Store.NO, Field.Index.ANALYZED_NO_NORMS));
		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(IS_TEAM_FIELD, "true"));
   	
    }
    // Get ids for folder read access.  Replace owner indicator with search owner search flag. Replace team indicator with team owner search flag
    public static String getFolderAclString(Binder binder) {
    	return getFolderAclString(binder, false);
    }
    public static String getFolderAclString(Binder binder, boolean includeTitleAcl) {
		Set<String> binderIds = AccessUtils.getReadAccessIds(binder, includeTitleAcl);
		
/*
//!		...doesn't work as I'd hoped...  Gives too wide of access.
		// Bugzilla 944231:  If the binder is inheriting its ACLs,
		// add in any CREATOR_READ rights as READ_ENTRIES.
		if (((WorkArea) binder).isFunctionMembershipInherited()) {
			Set<String> roBinderIds = AccessUtils.getReadOwnedEntriesIds(binder);
			if (MiscUtil.hasItems(roBinderIds)) {
				if (null == binderIds) binderIds = new HashSet<String>();
				binderIds.addAll(roBinderIds);
			}
		}
*/

   		String ids = LongIdUtil.getIdsAsString(binderIds);
        return ids.trim();
    }
    public static String getFolderTeamAclString(Binder binder) {
    	Long allUsersId = Utils.getAllUsersGroupId();
    	Set teamList = getBinderModule().getTeamMemberIds( binder );  
    	//Note: condition acls are not needed here 
    	//  since these get paired with _folderAcl:team which does have any conditions applied
    	if (teamList.contains(allUsersId)) {
    		//Add in the groups of the owner of the binder
    		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
         	ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
         	Set<Long> userGroupIds = profileDao.getApplicationLevelGroupMembership(binder.getOwner().getId(), zoneId);
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
    private static String getEntryAclString(Binder binder, Entry entry) {
     	if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop()) {
    		//This is a reply to a folder entry. Get the acl of the top entry
    		entry = ((FolderEntry)entry).getTopEntry();
    	}
    	if (entry.hasEntryAcl() || entry.hasEntryExternalAcl()) {
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
    
    //Routine to add the sharing ids into the index doc
    //Returns true if sharing ids were added, false if not
    private static void addSharingIds(Document doc, DefinableEntity entity) {
    	Set<EntityIdentifier> entityIdentifiers = new HashSet<EntityIdentifier>();
     	ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
    	String entryAclField, teamAclField;
    	if (entity instanceof FolderEntry) {
    		entryAclField = Constants.ENTRY_ACL_FIELD;
    		teamAclField = Constants.TEAM_ACL_FIELD;
         	entityIdentifiers.add(entity.getEntityIdentifier());
    	} else if(entity instanceof Binder) {
    		entryAclField = Constants.FOLDER_ACL_FIELD;
    		teamAclField = Constants.TEAM_ACL_FIELD;
    		WorkArea workArea = (Binder) entity;
        	entityIdentifiers.add(entity.getEntityIdentifier());
        	if (workArea.isAclExternallyControlled()) {
            	while (workArea.isExtFunctionMembershipInherited()) {
            		workArea = workArea.getParentWorkArea();
            		if(workArea instanceof DefinableEntity) {
            			entityIdentifiers.add(((DefinableEntity)workArea).getEntityIdentifier());
            		}
            	}
        	} else {
            	while (workArea.isFunctionMembershipInherited()) {
            		workArea = workArea.getParentWorkArea();
            		if(workArea instanceof DefinableEntity) {
            			entityIdentifiers.add(((DefinableEntity)workArea).getEntityIdentifier());
            		}
            	}
        	}
    	}
    	else {
    		return; // sharing not supported
    	}
    	List<Long> sharingIds = new ArrayList<Long>();
    	List<Long> sharingTeamIds = new ArrayList<Long>();
    	@SuppressWarnings("unused")
		Long allUsersId = Utils.getAllUsersGroupId();
    	@SuppressWarnings("unused")
		boolean personal = Utils.isWorkareaInProfilesTree((WorkArea)entity);
     	Map<RecipientType, Set<Long>> idMap = profileDao.getRecipientIdsWithGrantedRightToSharedEntities(
     			entityIdentifiers, WorkAreaOperation.READ_ENTRIES.getName());
     	for (Long id : idMap.get(RecipientType.user)) {
     		Field f = FieldFactory.createFieldNotStoredNotAnalyzed(entryAclField, id.toString());
     		if (!doc.getFields().contains(f)) {
     			doc.add(f); 
     			sharingIds.add(id);
     		}
     	}
     	for (Long id : idMap.get(RecipientType.group)) {
 			Field f = FieldFactory.createFieldNotStoredNotAnalyzed(entryAclField, id.toString());
 			if (!doc.getFields().contains(f)) {
 				doc.add(f); 
 				sharingIds.add(id);
 			}
     	}
     	for (Long id : idMap.get(RecipientType.team)) {
     		Field f = FieldFactory.createFieldNotStoredNotAnalyzed(teamAclField, id.toString());
     		if (!doc.getFields().contains(f)) {
     			doc.add(f); 
     			sharingTeamIds.add(id);
     		}
     	}
     	if (!sharingIds.isEmpty() || !sharingTeamIds.isEmpty()) {
     		//Indicate that this entity has been shared 
     		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.SHARED, Constants.SHARED_IS_SHARED));
	     	for (Long id : sharingIds) {
	     		//Indicate that this entity has been shared 
	     		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.SHARED_IDS, String.valueOf(id)));
	     	}
	     	for (Long id : sharingTeamIds) {
	     		//Indicate that this entity has been shared 
	     		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.SHARED_TEAM_IDS, String.valueOf(id)));
	     	}
     	}
     	//Add in the original sharer id
     	entityIdentifiers = new HashSet<EntityIdentifier>();
     	entityIdentifiers.add(entity.getEntityIdentifier());
     	Set<Long> sharerIds = profileDao.getSharerIdsToSharedEntities(entityIdentifiers);
     	for (Long id : sharerIds) {
     		//Indicate that this entity has been shared by someone
     		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.SHARE_CREATOR, String.valueOf(id)));
     	}
    }
    
    //Routine to add the sharing ids into the index doc
    private static void addSharingIds(org.dom4j.Element parent, DefinableEntity entity) {
    	String entryAclField, teamAclField;
    	if (entity instanceof FolderEntry) {
    		entryAclField = Constants.ENTRY_ACL_FIELD;
    		teamAclField = Constants.TEAM_ACL_FIELD;
    	} else {
    		entryAclField = Constants.FOLDER_ACL_FIELD;
    		teamAclField = Constants.TEAM_ACL_FIELD;
    	}
    	Long allUsersId = Utils.getAllUsersGroupId();
    	Set<EntityIdentifier> entityIdentifiers = new HashSet<EntityIdentifier>();
    	entityIdentifiers.add(entity.getEntityIdentifier());
     	ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
     	Map<RecipientType, Set<Long>> idMap = profileDao.getRecipientIdsWithGrantedRightToSharedEntities(
     			entityIdentifiers, WorkAreaOperation.READ_ENTRIES.getName());
     	org.dom4j.Element aclEle = (org.dom4j.Element) parent.selectSingleNode(entryAclField);
     	if (!idMap.get(RecipientType.user).isEmpty() || !idMap.get(RecipientType.group).isEmpty()) {
     		if (aclEle == null) {
     			aclEle = parent.addElement(entryAclField);
     		}
	     	String text = aclEle.getText().trim();
	     	for (Long id : idMap.get(RecipientType.user)) {
	     		text += " " + id.toString(); 
	     	}
	     	for (Long id : idMap.get(RecipientType.group)) {
	     		if (id.equals(allUsersId)) {
	     			text += " " + Constants.READ_ACL_ALL; 
	     		} else {
	     			text += " " + id.toString(); 
	     		}
	     	}
	     	aclEle.setText(text);
     	}
     	aclEle = (org.dom4j.Element) parent.selectSingleNode(teamAclField);
     	if (!idMap.get(RecipientType.team).isEmpty()) {
     		if (aclEle == null) {
     			aclEle = parent.addElement(teamAclField);
     		}
	     	String text = aclEle.getText().trim();
	     	for (Long id : idMap.get(RecipientType.team)) {
	     		text += " " + id.toString(); 
	     	}
	     	aclEle.setText(text);
     	}
    }
    
    //Routines to add the net folder root acl into the index doc
    private static void addRootAcl(Document doc, DefinableEntity entity) {
    	Set<String> rootIds = AccessUtils.getRootIds(entity);
    	for (String id : rootIds) {
    		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ROOT_FOLDER_ACL_FIELD, id));
    	}
    }
    private static void addRootAcl(org.dom4j.Element parent, DefinableEntity entity) {
    	Set<String> rootIds = AccessUtils.getRootIds(entity);
      	String ids = LongIdUtil.getIdsAsString(rootIds);
    	Element acl = parent.addElement(Constants.ROOT_FOLDER_ACL_FIELD);
   		acl.setText(ids);
    }
    
    private static void addEntryAcls(Document doc, Binder binder, Entry entry) {
		//get real entry access
    	String[] acls = StringUtil.split(getEntryAclString(binder, entry), " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, acl));
		//add entry owner
		Long owner = entry.getOwnerId();
		String ownerStr = Constants.EMPTY_ACL_FIELD;
		if (owner != null) ownerStr = String.valueOf(owner);  
		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_OWNER_ACL_FIELD, ownerStr));    	
    }

    @SuppressWarnings("unused")
	private static void addDefaultEntryAcls(Document doc, Binder binder, Entry entry) {
    	boolean personal = Utils.isWorkareaInProfilesTree(binder);
		//get default entry access
		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL));
		if (!personal) {
			doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_GLOBAL));
		}
    }

    private static void markEntryAsInheritingAcls(Document doc, Binder binder, Entry entry, boolean rss) {
    	if (entry instanceof FolderEntry) {
    		if (rss) {
    	    	//Note: if rss=true, we add "entryAcl=all". 
    			//This is only valid for Vibe. Net folders are not supportted in RSS
    			doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL));
    		}
    		long value = binder.getId().longValue(); // parent folder id
    		// Currently only the FAMT resource driver exposes files whose ACLs are not stored in Filr.
    		if(binder.noAclDredgedWithEntries())
    			value *= -1; // make it negative number
    		doc.add(new NumericField(Constants.ENTRY_ACL_PARENT_ID_FIELD, Field.Store.YES, true).setLongValue(value));
    	}
    }
    
    //Add acl fields for binder for storage in search engine
    public static void addBinderAcls(Document doc, Binder binder) {
    	addBinderAcls(doc, binder, false);
    }
    private static void addBinderAcls(Document doc, Binder binder, boolean includeTitleAcl) {
		//get real binder access
    	String[] acls = StringUtil.split(getFolderAclString(binder, includeTitleAcl), " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.FOLDER_ACL_FIELD, acl));
		//get team members
    	acls = StringUtil.split(getFolderTeamAclString(binder), " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.TEAM_ACL_FIELD, acl));
		//add binder owner
		Long owner = binder.getOwnerId();
		String ownerStr = Constants.EMPTY_ACL_FIELD;
		if (owner != null) ownerStr = owner.toString();  //TODO fix this
		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.BINDER_OWNER_ACL_FIELD, ownerStr));    	
		//Add sharing acls
		addSharingIds(doc, binder);
		//Add the net folder root ACL
		addRootAcl(doc, binder);
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
   		String tms = getBinderModule().getTeamMemberString( binder );
   		if ( getBinderModule().getTeamMemberIds( binder ).contains(allUsersId)) {
   			tms = tms + " " + Constants.READ_ACL_GLOBAL + " " + Constants.READ_ACL_ALL;
   		}
   		acl.setText(tms);
   		//Add sharing ids
   		addSharingIds(parent, binder);
   		//Add root acl
   		addRootAcl(parent, binder);
    }
    
    public static void addReadAccess(Document doc, Binder binder, boolean fieldsOnly) {
    	addReadAccess(doc, binder, fieldsOnly, false);
    }
    public static void addReadAccess(Document doc, Binder binder, boolean fieldsOnly, boolean includeTitleAcl) {
    	boolean personal = Utils.isWorkareaInProfilesTree(binder);
    	//set entryAcl to "all" and 
		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_ALL));
		if (!personal) {
			doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, Constants.READ_ACL_GLOBAL));
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
     	Set<Long> userGroupIds = profileDao.getApplicationLevelGroupMembership(entry.getId(), zoneId);
     	ids.addAll(userGroupIds);
     	StringBuffer pIds = new StringBuffer(LongIdUtil.getIdsAsString(ids));
    	//add allUsers
   		pIds.append(Constants.READ_ACL_ALL_USERS);      			
   		return pIds.toString();
    }
    
	// Add ACL field. We only need to index ACLs for read access.
    public static void addReadAccess(Document doc, Binder binder, DefinableEntity entry, boolean fieldsOnly) {
    	addReadAccess(doc, binder, entry, fieldsOnly, false);
    }
    public static void addReadAccess(Document doc, Binder binder, DefinableEntity entry, boolean fieldsOnly, boolean rss) {
    	//Note: if rss=true, an old style ACL is returned. This is only valid for Vibe and not for net folders. 
    	//IMPORTANT: CALLS MADE WITH "rss=true" WILL RENDER ALL NET FOLDER ENTRIES VISIBLE TO SEARCH!!!!!! 
    	//Only RSS should use this rss mode. Net folders are not supportted in RSS.
    	if (entry instanceof WorkflowSupport) {
       		Entry e = (Entry)entry;
    		WorkflowSupport wEntry = (WorkflowSupport)entry;
       		// Add the Entry_ACL field
       		if (wEntry.hasAclSet() && !e.isAclExternallyControlled()) {
       			//There is a workflow in play, and this is not a Net Folder file
       			//Workflow access settings are currently not valid for net folder files.
       			addReadAccessForWorkflow(doc, binder, e, wEntry, rss);
       		} else {
	       		//add entry access. 
	       		if (entry instanceof FolderEntry && !((FolderEntry)entry).isTop()) {
	       			//Make sure to use the acl of the top entry since replies use the top entry acl (unless they specify their own acl)
	       			entry = ((FolderEntry)entry).getTopEntry();
	       			wEntry = (WorkflowSupport)entry;
	       			e = (Entry)entry;
	       		}
	       		//Re-check if there is a workflow in play (since we may have changed wEntry)
	       		//As above, Note that workflow access settings are currently not valid for net folder files.
	       		if (wEntry.hasAclSet() && !e.isAclExternallyControlled()) {
	       			//This must have been a reply not running a workflow, so check the top entry workflow ACL
	       			addReadAccessForWorkflow(doc, binder, e, wEntry, rss);
		       	} else {
	       			if (e.isAclExternallyControlled()) {
	       				// Since the "all important" read right comes from external ACL in this case, 
	       				// it doesn't matter whether the entry is inheriting its Vibe-managed ACLs from
	       				// the parent or not. Simply put, Vibe-managed ACLs will play no role here 
	       				// as far as ACL indexing is concerned, because read right will never originate
	       				// from the Vibe-managed ACLs.
	       				if (e.hasEntryExternalAcl()) {
	       					// This entry has its own external ACL. We need to index it. 
			       			addEntryAcls(doc, binder, e);
	       				}
	       				else {
	       					// This entry is using the folder's external ACL. If user has read access to the 
	       					// parent folder through its external ACL, then she shall have same access to this entry. 
			    			markEntryAsInheritingAcls(doc, binder, e, rss);
	       				}
	       			} else {
	       				if (e.hasEntryAcl()) {
			    			//The entry has its own ACL specified
			       			addEntryAcls(doc, binder, e);
			       			if (e.isIncludeFolderAcl()) {
			       				// In addition to entry-level ACL, this entry is also inheriting ACLs from its parent folder.
				    			markEntryAsInheritingAcls(doc, binder, e, rss);
			       			}
			    		} else {
			    			// The entry has neither workflow ACL nor its own ACL.
			    			// The entry is using the folder's ACL
			    			//
			    			// Bugzilla 944231:  If the owner has
			    			// inherited CREATOR_READ rights to the
			    			// entry, add a separate ACL to give them
			    			// access.
			    	        Set<String> readOwnedEntries = AccessUtils.getReadOwnedEntriesIds(binder);
			    	        Long ownerId = ((WorkflowSupport) entry).getOwnerId();
		    	        	if (ownerHasReadOwnedEntriesRights(ownerId, readOwnedEntries, entry.getZoneId())) {
		    	        		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, String.valueOf(ownerId)));
		    	        	}
	    	        		markEntryAsInheritingAcls(doc, binder, e, rss);
			    		}
	       			}
	       		}
       		}
       		//Finally, add in the sharing acls and root acl
       		addSharingIds(doc, entry);
       		addRootAcl(doc, entry);
    	} else if (entry instanceof User) {
            // Add the Entry_ACL field
    		String[] acls = StringUtil.split(getUserEntryAccess((User)entry), " ");
    		for(String acl:acls)
    			doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ENTRY_ACL_FIELD, acl));
           	//add binder access
        	addBinderAcls(doc, binder);
        	addRootAcl(doc, binder);

    	} else {
    		addReadAccess(doc, binder, fieldsOnly);
       		//Finally, add in the sharing acls and root acl
       		addSharingIds(doc, binder);
       		addRootAcl(doc, binder);
    	}
	}
    
    private static void addReadAccessForWorkflow(Document doc, Binder binder, Entry entry, WorkflowSupport wEntry, boolean rss) {
		String[] acls = StringUtil.split(getWfEntryAccess(wEntry), " ");
		boolean includeTeam = false;
		for (String acl : acls) {
			doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(
					Constants.ENTRY_ACL_FIELD, acl));
			if(Constants.READ_ACL_TEAM.equals(acl))
				includeTeam = true;
		}
		// add binder access if "folder default" is specified
		if (wEntry.isWorkAreaAccess(WfAcl.AccessType.read)) {
			// In addition to entry-level ACL, this entry is also inheriting
			// ACLs from its parent folder.
			// Since ACLs for team membership are indexed at the team binder (and their inheriting 
			// descendant binders), there is no need to index them at the entry level when the entry
			// also honors the ACLs associated with its parent.
			markEntryAsInheritingAcls(doc, binder, entry, rss);
		}
		else {
			if(includeTeam) {
				// Bug 948857 - We need to index the team ACLs with this entry.
		    	acls = StringUtil.split(getFolderTeamAclString(binder), " ");
		    	for(String acl:acls)
		    		doc.add(new Field(Constants.TEAM_ACL_FIELD, acl, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
			}
		}
    }
    
    /*
     * Returns true if ownerId is a member of readOwnedEntries.
     * 
     * ownerId can be a member directly of readOwnedEntries or any
     * groups that it may resolve to.
     */
	private static boolean ownerHasReadOwnedEntriesRights(Long ownerId, Set<String> readOwnedEntries, Long zoneId) {
		// If we don't have an owner ID or any readOwnedEntries...
		if ((null == ownerId) || (!(MiscUtil.hasItems(readOwnedEntries)))) {
			// ...return false.
			return false;
		}

		// Scan the readOwnedEntries.
		List<Long> roIDs = new ArrayList<Long>();
		for (String roE:  readOwnedEntries) {
			// Is this readOwnedEntry the owner?
			Long roEId;
			try                 {roEId = Long.parseLong(roE);}
			catch (Exception e) {continue;                   }	// May be 'own', 'team', ...  Simply skip it if it can't be parsed.
			if (ownerId.equals(roEId)) {
				// Yes!  That's all we need to check, return true.
				return true;
			}
			
			// No, it's not the owner.  Track the ID.
			roIDs.add(roEId);
		}
		
		// Do we have an readOwnedEntries IDs?
		if (!(MiscUtil.hasItems(roIDs))) {
			// No!  Return false.
			return false;
		}
		
		// Can we resolve the IDs we have to any Principal's?
		List<Principal> roPrincipals  = ResolveIds.getPrincipals(roIDs, false);
		if (!(MiscUtil.hasItems(roPrincipals))) {
			// No!  Return false.
			return false;
		}

		// Scan the Principal's.
		List<Long> memberGroupIds = new ArrayList<Long>();
		for (Principal member:  roPrincipals) {
			// Is this Principal a Group?
			if (member instanceof GroupPrincipal) {
				// Yes!  Track its ID.
				memberGroupIds.add(member.getId());
			}
			
			// At this point, we don't care about users because
			// we would have matched it in an earlier check and
			// would have already returned.
		}
		
		// Are we tracking any group IDs?
		if (memberGroupIds.isEmpty()) {
			// No!  Return false.
			return false;
		}

		// Can we explode the group into any constituent members?
		Set<Long> groupMemberIds = ((ProfileDao) SpringContextUtil.getBean("profileDao")).explodeGroups(memberGroupIds, zoneId);
		if (!(MiscUtil.hasItems(groupMemberIds))) {
			// No!  Return false.
			return false;
		}
		
		// Return true if the group member IDs contain the owner and
		// false otherwise.
		return groupMemberIds.contains(ownerId);
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
     		//Add sharing ids
     		addSharingIds(parent, entry);
       		//add binder access
    		addBinderAcls(parent, binder);
    		addRootAcl(parent, binder);

    	} else if (entry instanceof User) {
    		// Add the Entry_ACL field
   	   		Element acl = parent.addElement(Constants.ENTRY_ACL_FIELD);
       		acl.setText(getUserEntryAccess((User)entry));
       		//add binder access
			addBinderAcls(parent, binder);
			addRootAcl(parent, binder);

    	} else {
     		addReadAccess(parent, binder, fieldsOnly);
     		addRootAcl(parent, binder);
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
    	Field ttfTagField = FieldFactory.createFieldNotStoredNotAnalyzed(Constants.TAG_FIELD_TTF, "");
    	// index all the public tags (allTags field and tag_acl field)
		for (Tag thisTag: pubTags) {
			tag = thisTag.getName();
			indexableTags += " " + tag;
			// Add the ttf fields.  The ttf fields are case insensitive before the ":",
			// so users can search and find tags regardless of case. The strategy is to use
			// this field for the type to find searches, but keep the original fields for 
			// display.
			tag = tag.toLowerCase() + ":" + tag; 
			ttfTagField = FieldFactory.createFieldNotStoredNotAnalyzed(Constants.TAG_FIELD_TTF, tag);
			doc.add(ttfTagField);
			aclTag = BasicIndexUtils.buildAclTag(thisTag.getName(), Constants.READ_ACL_ALL);
			aclTags += " " + aclTag;
			lowerAclTag = BasicIndexUtils.buildAclTag(thisTag.getName().toLowerCase(), Constants.READ_ACL_ALL);
			aclTag = lowerAclTag + ":" + aclTag;
			ttfTagField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ACL_TAG_FIELD_TTF, aclTag);
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
			ttfTagField = FieldFactory.createFieldStoredNotAnalyzed(Constants.ACL_TAG_FIELD_TTF, aclTag);
			doc.add(ttfTagField);
		}
    
    	Field tagField = FieldFactory.createField(Constants.TAG_FIELD, indexableTags, Field.Store.YES, Field.Index.NO);
    	doc.add(tagField);
    	String[] its = StringUtil.split(indexableTags, " ");
    	for(String it:its)
    		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.TAG_FIELD, it));
    	
    	if (!fieldsOnly) {
    		tagField = BasicIndexUtils.generalTextField(indexableTags);
    		doc.add(tagField);
    	}
    	
    	String[] acls = StringUtil.split(aclTags, " ");
    	for(String acl:acls)
    		doc.add(FieldFactory.createFieldNotStoredNotAnalyzed(Constants.ACL_TAG_FIELD, acl));
    }
	
    @SuppressWarnings("unused")
	public static void addFileType(Document doc, File textfile, boolean fieldsOnly) {
    	org.dom4j.Document document = null;
       	if ((textfile == null) || textfile.length() <= 0) return;
    	// open the file with an xml reader
		SAXReader reader = XmlUtil.getSAXReader();
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
		
      	
		Field fileTypeField = FieldFactory.createFieldStoredNotAnalyzed(FILE_TYPE_FIELD, x.getText());
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
		// Can we determine a primary file attachment for this entry?
		FileAttachment pfa = MiscUtil.getPrimaryFileAttachment(entry);
		if (null != pfa) {
			// Yes!  Add it to the index.
        	Field primaryFileIDField = FieldFactory.createFieldStoredNotAnalyzed(PRIMARY_FILE_ID_FIELD, pfa.getId());
        	doc.add(primaryFileIDField); 
		}
		
		Collection<FileAttachment> atts = entry.getFileAttachments();
        for (FileAttachment fa : atts) {
        	Field fileIDField = FieldFactory.createFieldStoredNotAnalyzed(FILE_ID_FIELD, fa.getId());
        	doc.add(fileIDField); 
        	Field fileOwnerIdField = FieldFactory.createFieldStoredNotAnalyzed(FILE_CREATOR_ID_FIELD, String.valueOf(fa.getCreation().getPrincipal().getId()));
        	doc.add(fileOwnerIdField); 
        	Field fileSizeField = FieldFactory.createFieldStoredNotAnalyzed(FILE_SIZE_FIELD, getSortableNumber(String.valueOf(fa.getFileItem().getLengthKB()), ObjectKeys.MAX_FILE_SIZE_DECIMAL_PLACES));
        	doc.add(fileSizeField); 
        	Field fileSizeInBytesField = FieldFactory.createFieldStoredNotAnalyzed(FILE_SIZE_IN_BYTES_FIELD, String.valueOf(fa.getFileItem().getLength()));
        	doc.add(fileSizeInBytesField);
            if (fa.getFileItem().getMd5()!=null) {
                Field fileMd5Field = FieldFactory.createFieldStoredNotAnalyzed(FILE_MD5_FIELD, fa.getFileItem().getMd5());
                doc.add(fileMd5Field);
            }
        	Field fileVersionField = FieldFactory.createFieldStoredNotAnalyzed(FILE_VERSION_FIELD, String.valueOf(fa.getHighestVersionNumber()));
        	doc.add(fileVersionField);
        	Field fileMajorVersionField = FieldFactory.createFieldStoredNotAnalyzed(FILE_MAJOR_VERSION_FIELD, String.valueOf(fa.getMajorVersion()));
        	doc.add(fileMajorVersionField);
        	Field fileMinorVersionField = FieldFactory.createFieldStoredNotAnalyzed(FILE_MINOR_VERSION_FIELD, String.valueOf(fa.getMinorVersion()));
        	doc.add(fileMinorVersionField);
        	Field fileTimeField = FieldFactory.createFieldStoredNotAnalyzed(FILE_TIME_FIELD, String.valueOf(fa.getModification().getDate().getTime()));
        	doc.add(fileTimeField); 
        	Field fileNameFieldStoredIndexed = FieldFactory.createFullTextFieldIndexed(FILENAME_FIELD, fa.getFileItem().getName(), true);
        	doc.add(fileNameFieldStoredIndexed);
        	Field fileNameFieldLowerCasedSingleTerm = FieldFactory.createFieldNotStoredNotAnalyzed(FILENAME_FIELD, fa.getFileItem().getName().trim().toLowerCase());
        	doc.add(fileNameFieldLowerCasedSingleTerm);
        	//create names that groups all the related values together for parsing in displays
        	//doc.add(new Field(FILE_SIZE_AND_ID_FIELD, fa.getId()+fileSizeField.stringValue()));
        	doc.add(FieldFactory.createFieldStoredNotAnalyzed(FILE_TIME_AND_ID_FIELD, Constants.UNIQUE_PREFIX + fa.getId() + fileTimeField.stringValue()));
        	doc.add(FieldFactory.createFieldStoredNotAnalyzed(FILENAME_AND_ID_FIELD, Constants.UNIQUE_PREFIX + fa.getId() + fa.getFileItem().getName()));
        }
        //While we are here, make sure the version agingEnabled flags are set properly
        FileUtils.setFileVersionAging(entry);
    }    
    //Used to index the file.  Only want info about this file, so remove extraneous stuff
    public static void addFileAttachment(Document doc, FileAttachment fa, boolean fieldsOnly) {
    	Field fileIDField = FieldFactory.createFieldStoredNotAnalyzed(FILE_ID_FIELD, fa.getId());
    	doc.add(fileIDField); 
    	Field fileOnlyIDField = FieldFactory.createFieldNotStoredNotAnalyzed(FILE_ONLY_ID_FIELD, fa.getId());
    	doc.add(fileOnlyIDField); 
    	Field fileOwnerIdField = FieldFactory.createFieldStoredNotAnalyzed(FILE_CREATOR_ID_FIELD, String.valueOf(fa.getCreation().getPrincipal().getId()));
    	doc.add(fileOwnerIdField); 
    	Field fileSizeField = FieldFactory.createFieldStoredNotAnalyzed(FILE_SIZE_FIELD, getSortableNumber(String.valueOf(fa.getFileItem().getLengthKB()), ObjectKeys.MAX_FILE_SIZE_DECIMAL_PLACES));
    	doc.add(fileSizeField); 
    	Field fileSizeInBytesField = FieldFactory.createFieldStoredNotAnalyzed(FILE_SIZE_IN_BYTES_FIELD, String.valueOf(fa.getFileItem().getLength()));
    	doc.add(fileSizeInBytesField); 
        if (fa.getFileItem().getMd5()!=null) {
	        Field fileMd5Field = FieldFactory.createFieldStoredNotAnalyzed(FILE_MD5_FIELD, fa.getFileItem().getMd5());
	       	doc.add(fileMd5Field);
        }
        Field fileVersionField = FieldFactory.createFieldStoredNotAnalyzed(FILE_VERSION_FIELD, String.valueOf(fa.getHighestVersionNumber()));
       	doc.add(fileVersionField);
        Field fileMajorVersionField = FieldFactory.createFieldStoredNotAnalyzed(FILE_MAJOR_VERSION_FIELD, String.valueOf(fa.getMajorVersion()));
        doc.add(fileMajorVersionField);
        Field fileMinorVersionField = FieldFactory.createFieldStoredNotAnalyzed(FILE_MINOR_VERSION_FIELD, String.valueOf(fa.getMinorVersion()));
        doc.add(fileMinorVersionField);
        Field fileTimeField = FieldFactory.createFieldStoredNotAnalyzed(FILE_TIME_FIELD, String.valueOf(fa.getModification().getDate().getTime()));
    	doc.add(fileTimeField);
    	Field fileNameFieldStoredIndexed = FieldFactory.createFullTextFieldIndexed(FILENAME_FIELD, fa.getFileItem().getName(), true);
    	doc.add(fileNameFieldStoredIndexed); 
    	Field fileNameFieldLowerCasedSingleTerm = FieldFactory.createFieldNotStoredNotAnalyzed(FILENAME_FIELD, fa.getFileItem().getName().trim().toLowerCase());
    	doc.add(fileNameFieldLowerCasedSingleTerm);    	
      	Field fileDescField = FieldFactory.createFieldStoredNotAnalyzed(FILE_DESCRIPTION_FIELD, fa.getFileItem().getDescription().getText());
       	doc.add(fileDescField);
      	Field fileStatusField = FieldFactory.createFieldStoredNotAnalyzed(FILE_STATUS_FIELD, String.valueOf(fa.getFileStatus()));
       	doc.add(fileStatusField);
       	Field fileExtField = FieldFactory.createFieldStoredNotAnalyzed(FILE_EXT_FIELD, getFileExtension(fa.getFileItem().getName()));
       	doc.add(fileExtField);   	
       	Field uniqueField = FieldFactory.createFieldStoredNotAnalyzed(FILE_UNIQUE_FIELD, Boolean.toString(fa.isCurrentlyLocked()));
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
    		Field ancestry = FieldFactory.createFieldStoredNotAnalyzed(ENTRY_ANCESTRY, parentBinder.getId().toString());
    		doc.add(ancestry);
    		parentBinder = ((Binder)parentBinder).getParentBinder();
    	}
    }

    public static void addBinderIconName(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createFieldStoredNotAnalyzed(ICON_NAME_FIELD, binder.getIconName());
		doc.add(path);
    }

    public static void addBinderPath(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createFieldStoredNotIndexed(ENTITY_PATH, binder.getPathName());
		doc.add(path);
		Field sortPath = FieldFactory.createFieldNotStoredNotAnalyzed(SORT_ENTITY_PATH, binder.getPathName().toLowerCase());
		doc.add(sortPath);
    }

    public static void addBinderIsLibrary(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createFieldStoredNotAnalyzed(IS_LIBRARY_FIELD, (binder.isLibrary() ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }

    public static void addBinderIsMirrored(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createFieldStoredNotAnalyzed(IS_MIRRORED_FIELD, (binder.isMirrored() ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }

    public static void addBinderIsHomeDir(Document doc, Binder binder, boolean fieldsOnly) {
		Field path = FieldFactory.createFieldStoredNotAnalyzed(IS_HOME_DIR_FIELD, (binder.isHomeDir() ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }

    public static void addBinderIsMyFilesDir(Document doc, Binder binder, boolean fieldsOnly) {
    	boolean isMyFilesDir = BinderHelper.isBinderMyFilesStorage(binder, false);	// false -> Don't update the My Files Storage binder markers.
		Field path = FieldFactory.createFieldStoredNotAnalyzed(IS_MYFILES_DIR_FIELD, (isMyFilesDir ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }

    public static void addBinderHasResourceDriver(Document doc, Binder binder, boolean fieldsOnly) {
    	if ((binder instanceof Folder) && binder.isMirrored()) {
    		boolean hasResourceDriver = MiscUtil.hasString(binder.getResourceDriverName());
    		if (hasResourceDriver) {
    			try {
    				ResourceDriver driver = binder.getResourceDriver();
    				if (null != driver) {
    					String dc = driver.getClass().getName();
    					if (MiscUtil.hasString(dc) && dc.equals("com.novell.teaming.fi.connection.file.FileResourceDriver")) {
    						hasResourceDriver = MiscUtil.hasString(driver.getRootPath());
    					}
    					
    					else
    					{
	        				ResourceDriverConfig rdConfig = driver.getConfig();
	        				if (null != rdConfig) {
		        				// Is everything configured?
		        				String rootPath = rdConfig.getRootPath();
		        				if (MiscUtil.hasString(rootPath)) {
		        					Boolean useProxyIdentity = rdConfig.getUseProxyIdentity();
			        				if ((null != useProxyIdentity) && useProxyIdentity && (null != rdConfig.getProxyIdentityId())) {
			        					hasResourceDriver = true;
			        				}
			        				else if (MiscUtil.hasString(rdConfig.getAccountName()) && MiscUtil.hasString(rdConfig.getPassword())) {
			        					hasResourceDriver = true;
			        				}
		        					else if (CloudFolderHelper.isCloudFolder(binder)) {
			        					hasResourceDriver = true;
		        					}
		        				}
	        				}
    					}
    				}
    			}
    			
    			catch (Exception ex) {
    				hasResourceDriver = false;
    			}
    		}
    		
    		Field path = FieldFactory.createFieldStoredNotAnalyzed(HAS_RESOURCE_DRIVER_FIELD, (hasResourceDriver ? Constants.TRUE : Constants.FALSE));
    		doc.add(path);
            if (hasResourceDriver) {
                path = FieldFactory.createFieldStoredNotIndexed(ALLOW_DESKTOP_SYNC_FIELD, (binder.getAllowDesktopAppToSyncData() ? Constants.TRUE : Constants.FALSE));
                doc.add(path);
                path = FieldFactory.createFieldStoredNotIndexed(ALLOW_MOBILE_SYNC_FIELD, (binder.getAllowMobileAppsToSyncData() ? Constants.TRUE : Constants.FALSE));
                doc.add(path);
            }
    	}
    }

    public static void addBinderIsTopFolder(Document doc, Binder binder, boolean fieldsOnly) {
    	if (binder instanceof Folder) {
    		Field path = FieldFactory.createFieldStoredNotAnalyzed(IS_TOP_FOLDER_FIELD, (((Folder)binder).isTop() ? Constants.TRUE : Constants.FALSE));
    		doc.add(path);
    	}
    }
    
    /**
     * 
     */
    public static void addResourceDriverName( Document doc, DefinableEntity entity, boolean fieldsOnly )
    {
    	if(entity instanceof Folder) {
    		Folder folder = (Folder) entity;
    		if(folder.isMirrored() && folder.getResourceDriverName() != null) {
        		Field path = FieldFactory.createFieldStoredNotAnalyzed( RESOURCE_DRIVER_NAME_FIELD, folder.getResourceDriverName() );
        		doc.add( path );    			
    		}
    	}
    	else if(entity instanceof FolderEntry) {
    		Folder parentFolder = ((FolderEntry)entity).getParentFolder();
    		if(parentFolder.isMirrored() && parentFolder.getResourceDriverName() != null) {
        		Field path = FieldFactory.createFieldStoredNotAnalyzed( RESOURCE_DRIVER_NAME_FIELD, parentFolder.getResourceDriverName() );
        		doc.add( path );    			
    		}
    	}
    }
    
    public static void addNetFolderResourcePath( Document doc, DefinableEntity entity, boolean fieldsOnly )
    {
    	if(entity instanceof Folder) {
    		Folder folder = (Folder) entity;
    		if(folder.isMirrored() && folder.getResourceDriver() != null) {
    			ResourceDriver driver = getResourceDriverManager().getDriver(folder.getResourceDriverName());
    			ResourceDriverConfig config = driver.getConfig();
    			if ((null != config) && config.isAclAware()) {
            		Field path = FieldFactory.createFieldStoredNotIndexed(RESOURCE_PATH_FIELD, folder.getResourcePath());
            		doc.add( path );    			
    			}
    		}
    	}
    	if(entity instanceof FolderEntry) {
    		FolderEntry folderEntry = (FolderEntry) entity;
    		Folder parentFolder = folderEntry.getParentFolder();
    		if(parentFolder.isMirrored() && parentFolder.getResourceDriverName() != null) {
    			ResourceDriver driver = getResourceDriverManager().getDriver(parentFolder.getResourceDriverName());
    			ResourceDriverConfig config = driver.getConfig();
    			if ((null != config) && config.isAclAware()) {
            		Field path = FieldFactory.createFieldStoredNotIndexed(RESOURCE_PATH_FIELD, 
            				driver.normalizedResourcePath(parentFolder.getResourcePath(), 
            						// Only the top-most entry's title correctly reflects the file name associated with it.
            						// A comment/reply may have title like "Re: debug.doc", etc.
            						(folderEntry.isTop())? folderEntry.getTitle() : folderEntry.getTopEntry().getTitle()));
            		doc.add( path );    			
    			}
     		}
    	}
    }
    
    /**
     * Adds whether a binder is a Cloud Folder to the index.  If a
     * binder is a Cloud Folder, adds the name if it Cloud Folder root
     * to the index.
     */
    public static void addBinderCloudFolderInfo(Document doc, Binder binder, boolean fieldsOnly) {
		String cfRoot = CloudFolderHelper.getCloudFolderRoot(binder);
		boolean isCloudFolder = MiscUtil.hasString(cfRoot);
		if (isCloudFolder) {
			Field path = FieldFactory.createFieldStoredNotAnalyzed(RESOURCE_DRIVER_NAME_FIELD, cfRoot);
			doc.add(path);
		}
		
		Field path = FieldFactory.createFieldStoredNotAnalyzed(IS_CLOUD_FOLDER_FIELD, (isCloudFolder ? Constants.TRUE : Constants.FALSE));
		doc.add(path);
    }
    
    /**
     * Adds a file time to the index for file folders.
     */
    public static void addFolderFileTime(Document doc, Folder folder, boolean fieldsOnly) {
    	// Is the folder a file folder?
    	org.dom4j.Document def = folder.getEntryDefDoc();
    	String family = DefinitionUtils.getFamily(def);
    	if (Validator.isNotNull(family) && family.equals(Definition.FAMILY_FILE)) {
    		// Yes!  Does it have a modification timestamp?
    		HistoryStamp stamp = folder.getModification();
    		if (null != stamp) {
    			// Yes!  Does that timestamp contain a date?
    	    	Date modDate = stamp.getDate();
    	     	if (null != modDate) {
    	     		// Yes!  Use it to add a _fileTime field.
    				Field path = FieldFactory.createFieldStoredNotAnalyzed(FILE_TIME_FIELD, String.valueOf(modDate.getTime()));
    				doc.add(path);
    	     	}
    		}
    	}
    }
    
    private static ResourceDriverManager getResourceDriverManager() {
    	return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
    }
}
