/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.AbstractIntervalView;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.fi.auth.AuthException;
import org.kablink.teaming.fi.auth.AuthSupport;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.acl.AclItemPrincipalMappingException;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.fi.connection.acl.ResourceItem;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.lucene.util.LanguageTaster;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.search.filter.SearchFilterToSearchBooleanConverter;
import org.kablink.teaming.search.postfilter.PostFilterCallback;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class SearchUtils {
	private static class AssigneeAttrTag {
		String attr;
		String tag;
		AssigneeAttrTag(String attr, String tag) {
			this.attr = attr;
			this.tag = tag;
		}
	}
	private final static int AT_INDIVIDUAL = 0;
	private final static int AT_GROUP      = 1;
	private final static int AT_TEAM       = 2;
	private final static AssigneeAttrTag[] TASK_ASSIGNEE_ATS = new AssigneeAttrTag[] {
		new AssigneeAttrTag(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME,        TaskHelper.TASK_ASSIGNED_TO),
		new AssigneeAttrTag(TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME, TaskHelper.TASK_ASSIGNED_TO_GROUPS),
		new AssigneeAttrTag(TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME,  TaskHelper.TASK_ASSIGNED_TO_TEAMS),
	};
	private final static AssigneeAttrTag[] CALENDAR_ASSIGNEE_ATS = new AssigneeAttrTag[] {
		new AssigneeAttrTag(EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME,        EventHelper.CALENDAR_ATTENEE),
		new AssigneeAttrTag(EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME, EventHelper.CALENDAR_ATTENEE_GROUPS),
		new AssigneeAttrTag(EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME,  EventHelper.CALENDAR_ATTENEE_TEAMS),	
	};
	
	protected static final Log logger = LogFactory.getLog(SearchUtils.class);
	
	public enum AssigneeType {
		TASK, CALENDAR;
	}

	//Common search support
	public static List getSearchEntries(Hits hits) {
		return hits.getDocuments();
	}
	
	public static List getSearchEntries(List<org.apache.lucene.document.Document> entries) {
		//Iterate through each entry and build the entries array as if the entry came from a search
		ArrayList<Map> childEntries = new ArrayList(entries.size());
		for (org.apache.lucene.document.Document doc : entries) {
			HashMap ent = new HashMap();
			childEntries.add(ent);
			Fieldable fld;
			//enumerate thru all the returned fields, and add to the map object
			List flds = doc.getFields();
			for(int i = 0; i < flds.size(); i++) {
				fld = (Fieldable)flds.get(i);
				if (fld.isStored()) {
					//TODO This hack needs to go.
					if (SearchUtils.isDateField(fld.name())) {
						try {
							ent.put(fld.name(),DateTools.stringToDate(fld.stringValue()));
		            		} catch (ParseException e) {ent.put(fld.name(),new Date());
		            	}	
		            } else if (!ent.containsKey(fld.name())) {
		            	ent.put(fld.name(), fld.stringValue());
		            } else {
		            	Object obj = ent.get(fld.name());
		            	SearchFieldResult val;
		            	if (obj instanceof String) {
		            		val = new SearchFieldResult();
		            		//replace
		            		ent.put(fld.name(), val);
		            		val.addValue((String)obj);
		            	} else {
		            		val = (SearchFieldResult)obj;
		            	}
		            	val.addValue(fld.stringValue());
		            } 
				}
			}
		}
		return childEntries;
	}
	private static boolean isDateField(String fieldName) {
    	
    	if (fieldName == null) return false;
	    	
    	if (fieldName.equals(Constants.CREATION_DATE_FIELD)) return true;
	    	
	    if (fieldName.equals(Constants.MODIFICATION_DATE_FIELD)) return true;

    	if (fieldName.equals(Constants.LASTACTIVITY_FIELD)) return true;    	

    	if (fieldName.endsWith(Constants.EVENT_FIELD_START_DATE)) return true;

    	if (fieldName.endsWith(Constants.EVENT_FIELD_CALC_START_DATE)) return true;

    	if (fieldName.endsWith(Constants.EVENT_FIELD_LOGICAL_START_DATE)) return true;

	    if (fieldName.endsWith(Constants.EVENT_FIELD_END_DATE)) return true;
	    
	    if (fieldName.endsWith(Constants.EVENT_FIELD_CALC_END_DATE)) return true;
	    
	    if (fieldName.endsWith(Constants.EVENT_FIELD_LOGICAL_END_DATE)) return true;
	    
	    if (fieldName.equals("due_date")) return true;
	    	
	    return false;
    }
  	public static SortField[] getSortFields(Map options) {
   		String sortBy = Constants.MODIFICATION_DATE_FIELD;   		
    	boolean descend = true;
    	Integer sortFieldType = null;
    	String sortBySecondary = null;
    	boolean descendSecondary = true;
    	Integer sortFieldTypeSecondary = null;

    	if (options != null) {    		
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_BY))
    			sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) 
    			descend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_FIELD_TYPE))
    			sortFieldType = (Integer) options.get(ObjectKeys.SEARCH_SORT_FIELD_TYPE);
    		
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_BY_SECONDARY))
    			sortBySecondary = (String) options.get(ObjectKeys.SEARCH_SORT_BY_SECONDARY);
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY)) 
    			descendSecondary = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY);
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_FIELD_TYPE_SECONDARY))
    			sortFieldTypeSecondary = (Integer) options.get(ObjectKeys.SEARCH_SORT_FIELD_TYPE_SECONDARY);
    	}

    	SortField[] fields;
    	if(sortBySecondary != null && !sortBySecondary.equals(sortBy))
    		fields = new SortField[2];
    	else
    		fields = new SortField[1];

    	fields[0] = toSortField(sortBy, descend, sortFieldType);
    	if(fields.length > 1)
    		fields[1] = toSortField(sortBySecondary, descendSecondary, sortFieldTypeSecondary);
    	
    	return fields;
   	}
  	
  	private static SortField toSortField(String sortBy, boolean descend, Integer sortFieldType) {
  		if(sortBy.equals(ObjectKeys.SEARCH_SORT_BY_RELEVANCE)) {
  			return new SortField(null, SortField.SCORE, descend);
  		}
  		else if(sortBy.equals(ObjectKeys.SEARCH_SORT_BY_DATE)) {
  			return new SortField(Constants.MODIFICATION_DATE_FIELD, SortField.STRING, descend);
  		}
  		else if(sortBy.equals(ObjectKeys.SEARCH_SORT_BY_RATING)) {
  			//Use this when using NumericField for this field.
  			//return new SortField(Constants.RATING_FIELD, SortField.DOUBLE, descend);
  			return new SortField(Constants.RATING_FIELD, SortField.STRING, descend);
  		}
  		else if(sortBy.equals(ObjectKeys.SEARCH_SORT_BY_REPLY_COUNT)) {
  			// Use this when using NumericField for this field.
  			//return new SortField(Constants.TOTALREPLYCOUNT_FIELD, SortField.INT, descend);
  			return new SortField(Constants.TOTALREPLYCOUNT_FIELD, SortField.STRING, descend);
  		}
  		else if (isDateField(sortBy)) {
    		return new SortField(sortBy, SortField.STRING, descend);
    	}
  		else if (null != sortFieldType) {
  			return new SortField(sortBy, sortFieldType.intValue(), descend);
  		}
    	else {
	    	User user = RequestContextHolder.getRequestContext().getUser();
	    	Locale locale = user.getLocale();
	    	return new SortField(sortBy, locale, descend);
    	}
  	}
  	
  	public static org.dom4j.Document getInitalSearchDocument(org.dom4j.Document searchFilter, Map options) {
  		if (searchFilter == null) {
  			//Build a null search filter
  			searchFilter = DocumentHelper.createDocument();
  			Element rootElement = searchFilter.addElement(SearchFilterKeys.FilterRootName);
  			rootElement.addElement(SearchFilterKeys.FilterTerms);
  		}
  		String currentBinderId = null;
  		if (options != null) {
  			currentBinderId = (String)options.get(ObjectKeys.SEARCH_DASHBOARD_CURRENT_BINDER_ID);
  		}
  		org.dom4j.Document qTree = SearchFilterToSearchBooleanConverter.convertSearchFilterToSearchBoolean(searchFilter, currentBinderId);
   		
  		//See if there are criteria to be merged
  		if (options != null && options.containsKey(ObjectKeys.SEARCH_CRITERIA_AND)) {
  			Criteria crit = (Criteria)options.get(ObjectKeys.SEARCH_CRITERIA_AND);
  			if (crit != null) {
  				Element rootEle = crit.toQuery().getRootElement();
  				if (rootEle != null) {
  						Element critEle = rootEle.element(Constants.AND_ELEMENT);
  						Element qTreeRootEle = qTree.getRootElement();
  						if (critEle != null && qTreeRootEle != null && 
  								qTreeRootEle.element(Constants.AND_ELEMENT) != null) {
  							qTreeRootEle.element(Constants.AND_ELEMENT).add((Element)critEle.clone());
  						}
  				}
  			}
  		}
  		return qTree;
  	}
  	
  	public static void getQueryFields(org.dom4j.Document queryTree, Map options) {
  		if (options == null) return;
		Element rootElement = queryTree.getRootElement();
   		//assume this document was setup in getInitalSearchDocument
		Element boolElement = rootElement.element(Constants.AND_ELEMENT);
    	//Add in any additional fields from the options map
    	if (options.containsKey(ObjectKeys.SEARCH_FILTER_AND)) {
    		org.dom4j.Document filter = (org.dom4j.Document) options.get(ObjectKeys.SEARCH_FILTER_AND);
    		Element filterRoot = filter.getRootElement();
    		boolElement.add((Element)filterRoot.clone());
     	}
		
       	//See if there is a title field search request
   		if (options.containsKey(ObjectKeys.SEARCH_TITLE)) {
   			Element field = boolElement.addElement(Constants.FIELD_ELEMENT);
   			field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.TITLE_FIELD);
   			Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
   			child.setText((String) options.get(ObjectKeys.SEARCH_TITLE));
   			//Make sure this string is tasted in case it is a double-byte language
   			String lang = LanguageTaster.taste(((String)options.get(ObjectKeys.SEARCH_TITLE)).toCharArray());
   			if (!lang.equalsIgnoreCase(LanguageTaster.DEFAULT))
   				rootElement.addAttribute(Constants.LANGUAGE_ATTRIBUTE, lang);

    	}

    	//See if there is an end date
    	if (options.containsKey(ObjectKeys.SEARCH_END_DATE)) {
    		Element range = boolElement.addElement(Constants.RANGE_ELEMENT);
    		range.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.CREATION_DAY_FIELD);
    		range.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, "true");
    		Element start = range.addElement(Constants.RANGE_START);
    		Calendar cal = Calendar.getInstance();
    		cal.set(1970, 0, 1);
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    		formatter.setTimeZone(TimeZoneHelper.getTimeZone("GMT"));
    		String s = formatter.format(cal.getTime());
    		start.addText((String) s);
    		Element finish = range.addElement(Constants.RANGE_FINISH);
    		finish.addText((String) options.get(ObjectKeys.SEARCH_END_DATE));
    	}

    	//See if there is a year/month
    	if (options.containsKey(ObjectKeys.SEARCH_YEAR_MONTH)) {
    		Element field = boolElement.addElement(Constants.FIELD_ELEMENT);
    		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.CREATION_YEAR_MONTH_FIELD);
    		Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_YEAR_MONTH));
    	}
    	//See if there is a tag
    	if (options.containsKey(ObjectKeys.SEARCH_COMMUNITY_TAG)) {
    		Element field = boolElement.addElement(Constants.FIELD_ELEMENT);
    		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.TAG_FIELD);
    		Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_COMMUNITY_TAG));
    	}
    	if (options.containsKey(ObjectKeys.SEARCH_PERSONAL_TAG)) {
    		Element field = boolElement.addElement(Constants.PERSONALTAGS_ELEMENT);
    		Element child = field.addElement(Constants.TAG_ELEMENT);
    		child.addAttribute(Constants.TAG_NAME_ATTRIBUTE, (String)options.get(ObjectKeys.SEARCH_PERSONAL_TAG));
    	}
    	if (options.containsKey(ObjectKeys.SEARCH_ANCESTRY)) {
    		Element field = boolElement.addElement(Constants.FIELD_ELEMENT);
    		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, org.kablink.util.search.Constants.ENTRY_ANCESTRY);
    		Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_ANCESTRY));
    	}
    	if (options.containsKey(ObjectKeys.SEARCH_HIDDEN)) {
    		Element field = boolElement.addElement(Constants.FIELD_ELEMENT);
    		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, org.kablink.util.search.Constants.HIDDEN_FROM_SEARCH_FIELD);
    		Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_HIDDEN));
    	}
    	if (options.containsKey(ObjectKeys.SEARCH_FIND_USER_HIDDEN)) {
    		Element field = boolElement.addElement(Constants.FIELD_ELEMENT);
    		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, org.kablink.util.search.Constants.HIDDEN_FROM_FIND_USER_FIELD);
    		Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_FIND_USER_HIDDEN));
    	}

    	//See if there are event days (modification is also an event)
    	if (options.containsKey(ObjectKeys.SEARCH_EVENT_DAYS)) {
    		Element orEventOrLastActivityOrCreationBoolElement = boolElement.addElement(Constants.OR_ELEMENT);
    			
    		Element orBoolElement = orEventOrLastActivityOrCreationBoolElement.addElement(Constants.OR_ELEMENT);
    		Iterator it = ((List)options.get(ObjectKeys.SEARCH_EVENT_DAYS)).iterator();
    		while (it.hasNext()) {
    			AbstractIntervalView.VisibleIntervalFormattedDates eventDay = (AbstractIntervalView.VisibleIntervalFormattedDates)it.next();
    			Element range = orBoolElement.addElement(Constants.RANGE_ELEMENT);
    			range.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.EVENT_DATES_FIELD);
    			range.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(Constants.RANGE_START);
    			start.addText(eventDay.startDate);
    			Element finish = range.addElement(Constants.RANGE_FINISH);
    			finish.addText(eventDay.endDate);
    		}
        			        		
        			        		
    		//	See if there is a last activity start date
    		if (options.containsKey(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START) && 
    				options.containsKey(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END)) {
    			Element orLastActivityDateBoolElement = orEventOrLastActivityOrCreationBoolElement.addElement(Constants.OR_ELEMENT);
    			Element range = orLastActivityDateBoolElement.addElement(Constants.RANGE_ELEMENT);
    			range.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.LASTACTIVITY_FIELD);
    			range.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(Constants.RANGE_START);
    			start.addText((String) options.get(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START));
    			Element finish = range.addElement(Constants.RANGE_FINISH);
    			finish.addText((String) options.get(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END));
    		}

        			        		
    		//	See if there is a last activity start date
    		if (options.containsKey(ObjectKeys.SEARCH_CREATION_DATE_START) && 
    				options.containsKey(ObjectKeys.SEARCH_CREATION_DATE_END)) {
    			Element orCreationDateBoolElement = orEventOrLastActivityOrCreationBoolElement.addElement(Constants.OR_ELEMENT);
    			Element range = orCreationDateBoolElement.addElement(Constants.RANGE_ELEMENT);
    			range.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, Constants.CREATION_DATE_FIELD);
    			range.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(Constants.RANGE_START);
    			start.addText((String) options.get(ObjectKeys.SEARCH_CREATION_DATE_START));
    			Element finish = range.addElement(Constants.RANGE_FINISH);
    			finish.addText((String) options.get(ObjectKeys.SEARCH_CREATION_DATE_END));
    		}
    	
    	}
  	}
    public static void extendPrincipalsInfo(List<Map> entries, ProfileDao profileDao, String userField) {
    	Set ids = new HashSet();
    	Set idsOwner = new HashSet();
    	//build list of user ids to load
    	for (Map entry: entries) {
    		if (entry.get(userField) != null)
    			try {
    				ids.add(Long.parseLong((String)entry.get(userField)));
    			} catch (Exception ex) {}
        		if (entry.get(Constants.OWNERID_FIELD) != null)
        			try {
        				idsOwner.add(Long.parseLong((String)entry.get(Constants.OWNERID_FIELD)));
        			} catch (Exception ex) {}
    	}
    	List<UserPrincipal> principles = profileDao.loadUserPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), false);
    	principles = profileDao.filterInaccessiblePrincipals(principles);
    	Map users = new HashMap();
		for (Principal p:principles) {
			users.put(p.getId(), p);
		}
    	principles = profileDao.loadUserPrincipals(idsOwner, RequestContextHolder.getRequestContext().getZoneId(), false);
    	principles = profileDao.filterInaccessiblePrincipals(principles);
    	for (Principal p:principles) {
			users.put(p.getId(), p);
		}
		
		// walk the entries, and stuff in the requested user object.
		for (int i = 0; i < entries.size(); i++) {
			HashMap entry = (HashMap)entries.get(i);
			if (entry.get(userField) != null && users.containsKey(Long.parseLong((String)entry.get(userField)))) {
				entry.put(WebKeys.PRINCIPAL, users.get(Long.parseLong((String)entry.get(userField))));
	        }        	
			if (entry.get(Constants.OWNERID_FIELD) != null && 
					users.containsKey(Long.parseLong((String)entry.get(Constants.OWNERID_FIELD)))) {
				entry.put(WebKeys.PRINCIPAL_OWNER, users.get(Long.parseLong((String)entry.get(Constants.OWNERID_FIELD))));
	        }        	
		}		
	}
  	
    
	public static void filterEntryAttachmentResults(Map results) {
		List entries = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		entries = filterEntryAttachments(entries);
		int newCountOfReturnedEntries = entries.size();
		int newTotalCount = (Integer)results.get(ObjectKeys.SEARCH_COUNT_TOTAL)-((Integer)results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED) - newCountOfReturnedEntries);
		
		results.put(ObjectKeys.SEARCH_COUNT_TOTAL, newTotalCount);
		results.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, newCountOfReturnedEntries);
	}
	
	private static String ATTACHMENTS="attachments";
	private static String ENTRY="entry";
	
	public static List filterEntryAttachments(List entries) {
		List result = new ArrayList();
		// combine all entries related attachments with entry
		Map entriesCombined = new HashMap();
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map entry = (Map) it.next();
			String uniqueId = entry.get(Constants.ENTITY_FIELD)+"_"+entry.get(Constants.DOCID_FIELD);
			Map entryValues = (Map)entriesCombined.get(uniqueId);
			if (entryValues == null) entryValues = new HashMap();
			if (((String)entry.get(Constants.DOC_TYPE_FIELD)).equalsIgnoreCase(Constants.DOC_TYPE_ATTACHMENT)) {
				List attachments = (List) entryValues.get(ATTACHMENTS);
				if (attachments == null) attachments = new ArrayList();
				attachments.add(entry);
				
				entryValues.put(ATTACHMENTS, attachments);
			} else {
				entryValues.put(ENTRY, entry);
			}
			entriesCombined.put(uniqueId, entryValues);				
		}
		Iterator entryIt = entriesCombined.values().iterator();
		while (entryIt.hasNext()) {
			Map entryMap = (Map) entryIt.next();
			Map resultEntry = (Map) entryMap.get(ENTRY);
			if (resultEntry == null) {
				resultEntry = new HashMap();
				resultEntry.put(WebKeys.ENTITY_TYPE, WebKeys.ATTACHMENTS_TYPE);
				resultEntry.put(WebKeys.ENTRY_HAS_META_HIT, Boolean.FALSE);
			} else {
				resultEntry.put(WebKeys.ENTRY_HAS_META_HIT, Boolean.TRUE);
			} 
			resultEntry.put(WebKeys.ENTRY_ATTACHMENTS, entryMap.get(ATTACHMENTS));
			result.add(resultEntry);
		}
		return result;
	}

	public static void setupAssignees(SearchFilter searchFilter, Map componentData,
			String assignedTo, String assignedToGroup, String assignedToTeam, AssigneeType assigneeType)
	{
		List<SearchFilter.Entry> entries = new ArrayList<SearchFilter.Entry>();
		final AssigneeAttrTag[] assigneeATs = ((AssigneeType.TASK == assigneeType) ? TASK_ASSIGNEE_ATS : CALENDAR_ASSIGNEE_ATS);
		
		// Handle individual assignments.
		String[] assignedToSplit = new String[0];
		if (!"".equals(assignedTo)) {
			assignedToSplit = assignedTo.trim().split("\\s");
			Set ids = new HashSet();
			for (int i = 0; i < assignedToSplit.length; i++) {
				try {
					if (SearchFilterKeys.CurrentUserId.equals(assignedToSplit[i])) {
						ids.add(SearchFilterKeys.CurrentUserId);
					} else {
						ids.add(Long.parseLong(assignedToSplit[i]));
					}
				} catch (NumberFormatException e) {
					logger.debug("SearchUtils.setupAssignees(NumberFormatException):  1. Ignored");
				}
			}
			if (null != componentData) {
				componentData.put(assigneeATs[AT_INDIVIDUAL].tag, ids);
			}
		}
		entries.add(new SearchFilter.Entry(null, assigneeATs[AT_INDIVIDUAL].attr,
					assignedToSplit, "user_list"));

		// Handle group assignments.
		String[] assignedToGroupSplit = new String[0];
		if (!"".equals(assignedToGroup)) {
			assignedToGroupSplit = assignedToGroup.trim().split("\\s");
			Set ids = new HashSet();
			for (int i = 0; i < assignedToGroupSplit.length; i++) {
				try {
					ids.add(Long.parseLong(assignedToGroupSplit[i]));
				} catch (NumberFormatException e) {
					logger.debug("SearchUtils.setupAssignees(NumberFormatException):  2:  Ignored");
				}
			}
			if (null != componentData) {
				componentData.put(assigneeATs[AT_GROUP].tag, ids);
			}
		}
		entries.add(new SearchFilter.Entry(null, assigneeATs[AT_GROUP].attr,
					assignedToGroupSplit, "group_list"));

		// Handle team assignments.
		String[] assignedToTeamSplit = new String[0];
		if (!"".equals(assignedToTeam)) {
			assignedToTeamSplit = assignedToTeam.trim().split("\\s");
			Set ids = new HashSet();
			for (int i = 0; i < assignedToTeamSplit.length; i++) {
				try {
					ids.add(Long.parseLong(assignedToTeamSplit[i]));
				} catch (NumberFormatException e) {
					logger.debug("SearchUtils.setupAssignees(NumberFormatException):  3:  Ignored");
				}
			}
			if (null != componentData) {
				componentData.put(assigneeATs[AT_TEAM].tag, ids);
			}
		}
		entries.add(new SearchFilter.Entry(null, assigneeATs[AT_TEAM].attr,
					assignedToTeamSplit, "team_list"));
		
		// Store the assignees that we're searching for.
		searchFilter.addEntries(entries, "userGroupTeam");		
	}
	
	/**
	 * Wraps the request to the search index with a check for jits need on the binder
	 * in order to add a simple near-real-time directory navigation support. 
	 * 
	 * @param luceneSession
	 * @param contextUserId
	 * @param aclQueryStr
	 * @param mode
	 * @param query
	 * @param sort
	 * @param offset
	 * @param size
	 * @param parentBinderId
	 * @param parentBinderPath
	 * @return
	 * @throws LuceneException
	 */
	public static Hits searchFolderOneLevelWithInferredAccess(LuceneReadSession luceneSession,
			Long contextUserId, 
			SearchObject so,
			int mode, 
			int offset, 
			int size, 
			Binder parentBinder,
            boolean allowJits)
					throws LuceneException, AuthException {
		if(so == null)
			throw new IllegalArgumentException("Search object must be specifed");
		
		String baseAclQueryStr = so.getBaseAclQueryStr();
		String extendedAclQueryStr = so.getExtendedAclQueryStr();
		Query query = so.getLuceneQuery();
		Sort sort = so.getSortBy();
		
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + query.toString());
    	}

		try {
			if(allowJits && parentBinder.isMirrored() && parentBinder instanceof Folder) {
				if(!getFolderModule().jitSynchronize((Folder)parentBinder)) {
					// As result of JITS, the parent binder just disappeared from the system.
					// We have to somehow notify the caller of this situation - 
					// Shall we throw an exception or return an empty hits? Each has pros and cons.
					// Exception throwing is more unpredictable and risky since it's not exactly
					// clear how various clients would behave upon the error. So instead, we will
					// throw an empty result, which will make the caller think that this binder
					// has no children. And then this binder can disapear more graciously from
					// the client's view when the caller invokes this method on the binder's 
					// parent at a later time.
					
					//throw new NoBinderByTheIdException(parentBinder.getId());
					return new Hits(0);
				}
			}	
		}
		catch(AuthException e) {
			throw e; // Propagate this up
		}
		catch(Exception ignore) {
			// Ignore all other exceptions, but still log it to show why it failed.
			logger.warn("JITS request failed", ignore);
		}
		
		Binder netFoldersBinder = org.kablink.teaming.search.SearchUtils.getNetFoldersRootBinder();
		
		if(netFoldersBinder != null && netFoldersBinder.getId().equals(parentBinder.getId())) {
			// Getting a list of net folders that the user has access to. This requires special processing and
			// filtering to ensure that the result only contains those net folders that meet all of the 
			// following requirements.
			// 1. The admin has granted the user "access net folder" right on the specific net folder.
			// 2. The user has file system access to the net folder via either direct access or inferred access.
			// 3. The user should not see this net folder listed under his "Net Folders" collection view just 
			//    because some other user shared with him this net folder or anything within that net folder
			//    (i.e., sub-folder or file within).
			return luceneSession.search(contextUserId, so.getNetFolderRootAclQueryStr(), null, mode, query, null, sort, offset, size,
					new PostFilterCallback() {
				@Override
				public Boolean preFilter(Map<String,Object> doc, boolean noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl) {
					// This filter implementation ignores the second arg.
					String resourceDriverName = (String) doc.get(Constants.RESOURCE_DRIVER_NAME_FIELD);
					if(resourceDriverName == null) 
						return Boolean.FALSE; // no resource driver
					return null;
				}
				
				@Override
				public int getPredictedSuccessRatePercentage() {
					return SPropsUtil.getInt("netfolders.search.post.filtering.predicted.success.rate.percentage", 25);
				}
			});
			
			//return luceneSession.searchFolderOneLevelWithInferredAccess(contextUserId, aclQueryStr, mode, query, sort, offset, size, parentBinder.getId(), parentBinder.getPathName());
		}
		else if(parentBinder.noAclDredgedWithEntries()) {
			// The parent binder is a net folder which does not store file ACLs in the search index.
			List<String> childrenTitles = null;
			if(Validator.isNotNull(so.getAclQueryStr())) {
				boolean shareGrantedAccess = getAccessControlManager().testRightGrantedBySharing(RequestContextHolder.getRequestContext().getUser(), (WorkArea) parentBinder, WorkAreaOperation.READ_ENTRIES);
				if(shareGrantedAccess) {
					// This means that we already know the calling user has the right to read the parent binder given by
					// at least one sharing involving the binder either directly or indirectly. In this case, we give
					// the user read access to all files and sub-folders within the binder WITHOUT asking the index.
					// As a matter of fact, currently, the index doesn't have enough information to figure this out on its own.
					// A couple of important points:
					// 1. Filr supports folder sharing only for Home Folders (and not Net Folders), and with all Home
					//    Folders, we assume that the owner/sharer always has read access to EVERYTHING in her Home
					//    Folder. Consequently, a sharee of a folder within a Home Folder automatically has read access
					//    to EVERYTHING within that folder and down. Therefore, it is OK to short circuit ACL checking
					//    for contents of the shared folder.
					// 2. The normal execution path where we obtain children list from the file system and pass it
					//    down to the search service as a kind of ACL filter (see below) does NOT work in this case, 
					//    because the calling user has NO file system rights to the content of the shared folder.
					// 3. This strategy works OK for folder navigation, but not for general search. In other word,
					//    general search is still broken with regard to folder sharing. For that reason, this fix
					//    is only temporary and we need a real fix (see bug #858636).
					// NOTE: It may be more efficient to implement this logic within the search service itself
					//       so that this caller won't have to check share-granted right on the binder. 
					//       We will revisit that topic when rewriting search service for next Filr release.
					baseAclQueryStr = null; // Let the Lucene Service skip access checking on the contents of the binder.
					extendedAclQueryStr = null;
				}
				else {		
					// The sharing facility doesn't grant this user read right to the parent binder, which means
					// that the system must rely on the regular file system right to make determination.
					
					// We need to consolidate the information in the search index with the dynamic list
					// obtained from the file system in order to determine which subset of the children
					// the user has access to.
					childrenTitles = getChildrenTitlesFromSourceSystem((Folder)parentBinder);
				}
			}
			else {
				// The user is not confined by access check (e.g. admin), which means that 
				// dynamic filtering against file system is not necessary either.
			}
		    return luceneSession.searchNetFolderOneLevel(contextUserId, baseAclQueryStr, extendedAclQueryStr, childrenTitles, query, sort, offset, size);					
		}
		else {
			// All other cases
			return luceneSession.searchNonNetFolderOneLevelWithInferredAccess(contextUserId, baseAclQueryStr, extendedAclQueryStr, mode, query, sort, offset, size, parentBinder.getId(), parentBinder.getPathName());
		}
	}
	
	private static List<String> getChildrenTitlesFromSourceSystem(Folder parentBinder) {
		AclResourceSession session = openAclResourceSession((AclResourceDriver) parentBinder.getResourceDriver(), FolderUtils.getNetFolderOwnerId(parentBinder));
		
		if(session == null)
			return null; // The source system doesn't recognize this user. 
		
		try {
			session.setPath(parentBinder.getResourcePath(), parentBinder.getResourceHandle(), Boolean.TRUE);
			List<ResourceItem> children = session.getChildren(false, false, false, false);
			List<String> titles = null;
			if(children != null && children.size() > 0) {
				titles = new ArrayList<String>(children.size());
				for(ResourceItem child:children) {
					titles.add(child.getName());
				}
			}
			return titles;
		}
		catch(Exception e) {
			/*
			logger.error("Error getting listing for folder [" + parentBinder.getPathName() + "] with resource path [" + 
					parentBinder.getResourcePath() + "] through net folder server '" + parentBinder.getResourceDriverName() + "'");
			return null;
			*/
			// (Bug #865093) Propagate the exception directly up the call stack
			throw e; 
		}
		finally {
			session.close();
		}
	}
	
	public static AclResourceSession openAclResourceSession(String resourceDriverName, Long netFolderOwnerId) {
		ResourceDriver driver;
		
		try {
			driver = getResourceDriverManager().getDriver(resourceDriverName);
		}
		catch(Exception e) {
			logger.warn("Can not find resource driver by name '" + resourceDriverName + "'", e);
			return null;
		}
		
		return openAclResourceSession(driver, netFolderOwnerId);
	}
	
	public static AclResourceSession openAclResourceSession(ResourceDriver driver, Long netFolderOwnerId) {
		User user = RequestContextHolder.getRequestContext().getUser();

		if(!(driver instanceof AclResourceDriver)) {
			logger.warn("Unable to open session on resource driver '" + driver.getName() + "' for user '" + user.getName() + " because the driver is of class '" + driver.getClass().getName() + "'");
			return null;
		}

		if(logger.isDebugEnabled())
			logger.debug("Opening a session on resource driver '" + driver.getName() + "' for user '" + user.getName() + "' to check access");
		
		try {
			AclResourceDriver adriver = (AclResourceDriver) driver;
			if(adriver instanceof AuthSupport)
				return getResourceDriverManager().openSessionWithAuth(adriver, netFolderOwnerId);
			else 
				return getResourceDriverManager().openSessionUserMode(adriver);
		} catch (AclItemPrincipalMappingException e) {
			if(logger.isDebugEnabled())
				logger.debug("Unable to open session on ACL resource driver '" + driver.getName() + "' for user '" + user.getName() + "'");
			return null;
		}
	}
	
	public static List<String> fieldNamesList(String... fieldName) {
		return Arrays.asList(fieldName);
	}
	
	private static FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}
	
	private static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
	
	private static AccessControlManager getAccessControlManager() {
		return (AccessControlManager) SpringContextUtil.getBean("accessControlManager");
	}
}
