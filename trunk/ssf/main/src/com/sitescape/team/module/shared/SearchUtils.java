/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterToSearchBooleanConverter;
import com.sitescape.team.web.WebKeys;

public class SearchUtils {
	private static  String[] docTypes = new String[] {BasicIndexUtils.DOC_TYPE_BINDER, BasicIndexUtils.DOC_TYPE_ENTRY,BasicIndexUtils.DOC_TYPE_ATTACHMENT};

	//Common search support
	public static List getSearchEntries(Hits hits) {
		//Iterate through the search results and build the entries array
		ArrayList<Map> childEntries = new ArrayList(hits.length());
		try {
			int count=0;
			Field fld;
			while (count < hits.length()) {
				HashMap ent = new HashMap();
				Document doc = hits.doc(count);
				//enumerate thru all the returned fields, and add to the map object
				Enumeration flds = doc.fields();
				while (flds.hasMoreElements()) {
					fld = (Field)flds.nextElement();
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
				childEntries.add(ent);
				++count;
		            
		    }
		} finally {
		}
		return childEntries;
	}
	private static boolean isDateField(String fieldName) {
    	
    	if (fieldName == null) return false;
	    	
    	if (fieldName.equals(EntityIndexUtils.CREATION_DATE_FIELD)) return true;
	    	
	    if (fieldName.equals(EntityIndexUtils.MODIFICATION_DATE_FIELD)) return true;

    	if (fieldName.equals(IndexUtils.LASTACTIVITY_FIELD)) return true;    	

    	if (fieldName.endsWith(EntityIndexUtils.EVENT_FIELD_START_DATE)) return true;

	    if (fieldName.endsWith(EntityIndexUtils.EVENT_FIELD_END_DATE)) return true;
	    
	    if (fieldName.equals("due_date")) return true;
	    	
	    return false;
    }
  	public static SortField[] getSortFields(Map options) {
   		SortField[] fields = new SortField[1];
   		String sortBy = EntityIndexUtils.MODIFICATION_DATE_FIELD;   		
    	boolean descend = true;
    	if (options != null) {    		
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_BY))
    			sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
    		if (options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) 
    			descend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
    	}
    	int sortType = SortField.AUTO;
    	if (sortBy.equals(EntityIndexUtils.MODIFICATION_DATE_FIELD) || sortBy.equals(IndexUtils.LASTACTIVITY_FIELD)) 
    		sortType = SortField.STRING;
    	
    	fields[0] = new SortField(sortBy, sortType, descend);
    	return fields;
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
   		return qTree;
  	}
  	
  	public static void getQueryFields(org.dom4j.Document queryTree, Map options) {
  		if (options == null) return;
		Element rootElement = queryTree.getRootElement();
   		//assume this document was setup in getInitalSearchDocument
		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
    	//Add in any additional fields from the options map
    	if (options.containsKey(ObjectKeys.SEARCH_FILTER_AND)) {
    		org.dom4j.Document filter = (org.dom4j.Document) options.get(ObjectKeys.SEARCH_FILTER_AND);
    		Element filterRoot = filter.getRootElement();
    		boolElement.add((Element)filterRoot.clone());
     	}
		
       	//See if there is a title field search request
   		if (options.containsKey(ObjectKeys.SEARCH_TITLE)) {
   			Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
   			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.TITLE_FIELD);
   			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
   			child.setText((String) options.get(ObjectKeys.SEARCH_TITLE));
    	}

    	//See if there is an end date
    	if (options.containsKey(ObjectKeys.SEARCH_END_DATE)) {
    		Element range = boolElement.addElement(QueryBuilder.RANGE_ELEMENT);
    		range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATION_DAY_FIELD);
    		range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
    		Element start = range.addElement(QueryBuilder.RANGE_START);
    		Calendar cal = Calendar.getInstance();
    		cal.set(1970, 0, 1);
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    		String s = formatter.format(cal.getTime());
    		start.addText((String) s);
    		Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
    		finish.addText((String) options.get(ObjectKeys.SEARCH_END_DATE));
    	}

    	//See if there is a year/month
    	if (options.containsKey(ObjectKeys.SEARCH_YEAR_MONTH)) {
    		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATION_YEAR_MONTH_FIELD);
    		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_YEAR_MONTH));
    	}
    	//See if there is a tag
    	if (options.containsKey(ObjectKeys.SEARCH_COMMUNITY_TAG)) {
    		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.TAG_FIELD);
    		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    		child.setText((String) options.get(ObjectKeys.SEARCH_COMMUNITY_TAG));
    	}
    	if (options.containsKey(ObjectKeys.SEARCH_PERSONAL_TAG)) {
    		Element field = boolElement.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
    		Element child = field.addElement(QueryBuilder.TAG_ELEMENT);
    		child.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, (String)options.get(ObjectKeys.SEARCH_PERSONAL_TAG));
    	}

    	//See if there are event days (modification is also an event)
    	if (options.containsKey(ObjectKeys.SEARCH_EVENT_DAYS)) {
    		Element orEventOrLastActivityOrCreationBoolElement = boolElement.addElement(QueryBuilder.OR_ELEMENT);
    			
    		Element orBoolElement = orEventOrLastActivityOrCreationBoolElement.addElement(QueryBuilder.OR_ELEMENT);
    		Iterator it = ((List)options.get(ObjectKeys.SEARCH_EVENT_DAYS)).iterator();
    		while (it.hasNext()) {
    			String[] eventDay = (String[])it.next();
    			Element range = orBoolElement.addElement(QueryBuilder.RANGE_ELEMENT);
    			range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.EVENT_DATES_FIELD);
    			range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(QueryBuilder.RANGE_START);
    			start.addText(eventDay[0]);
    			Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
    			finish.addText(eventDay[1]);
    		}
        			        		
    		Element orLastActivityDateBoolElement = orEventOrLastActivityOrCreationBoolElement.addElement(QueryBuilder.OR_ELEMENT);
    		Element andStartAndEndLastActivityDate = orLastActivityDateBoolElement.addElement(QueryBuilder.AND_ELEMENT);
        			        		
    		//	See if there is a last activity start date
    		if (options.containsKey(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START) && 
    				options.containsKey(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END)) {
    			Element range = andStartAndEndLastActivityDate.addElement(QueryBuilder.RANGE_ELEMENT);
    			range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, IndexUtils.LASTACTIVITY_FIELD);
    			range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(QueryBuilder.RANGE_START);
    			start.addText((String) options.get(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START));
    			Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
    			finish.addText((String) options.get(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END));
    		}

    		Element orCreationDateBoolElement = orEventOrLastActivityOrCreationBoolElement.addElement(QueryBuilder.OR_ELEMENT);
    		Element andStartAndEndCreationDate = orCreationDateBoolElement.addElement(QueryBuilder.AND_ELEMENT);
        			        		
    		//	See if there is a last activity start date
    		if (options.containsKey(ObjectKeys.SEARCH_CREATION_DATE_START) && 
    				options.containsKey(ObjectKeys.SEARCH_CREATION_DATE_END)) {
    			Element range = andStartAndEndCreationDate.addElement(QueryBuilder.RANGE_ELEMENT);
    			range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATION_DATE_FIELD);
    			range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(QueryBuilder.RANGE_START);
    			start.addText((String) options.get(ObjectKeys.SEARCH_CREATION_DATE_START));
    			Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
    			finish.addText((String) options.get(ObjectKeys.SEARCH_CREATION_DATE_END));
    		}
    	
    	}
  	}
    public static void extendPrincipalsInfo(List<Map> entries, ProfileDao profileDao, String userField) {
    	Set ids = new HashSet();
    	//build list of user ids to load
    	for (Map entry: entries) {
    		if (entry.get(userField) != null)
    			try {
    				ids.add(Long.parseLong((String)entry.get(userField)));
    			} catch (Exception ex) {}
    	}
		Map users = profileDao.loadPrincipalsData(ids, RequestContextHolder.getRequestContext().getZoneId(), false);

		// walk the entries, and stuff in the requested user object.
		for (int i = 0; i < entries.size(); i++) {
			HashMap entry = (HashMap)entries.get(i);
			if (entry.get(userField) != null) {
				entry.put(WebKeys.PRINCIPAL, users.get(Long.parseLong((String)entry.get(userField))));
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
			String uniqueId = entry.get(EntityIndexUtils.ENTITY_FIELD)+"_"+entry.get(EntityIndexUtils.DOCID_FIELD);
			Map entryValues = (Map)entriesCombined.get(uniqueId);
			if (entryValues == null) entryValues = new HashMap();
			if (((String)entry.get(BasicIndexUtils.DOC_TYPE_FIELD)).equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ATTACHMENT)) {
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
	public static void buildMembershipUpdate(List<Binder>binders, ArrayList<Query> updateQueries, ArrayList<String> updateIds) {
		// Now, create a query which can be used by the index update method to modify all the
		// entries, replies, attachments, and binders(workspaces) in the index with this new 
		// Acl list.
		//find descendent binders with the same owner team membership and re-index together
		//the owner  may be part of the acl set, so cannot always share
		while (!binders.isEmpty()) {
			Binder top = binders.get(0);
			binders.remove(0);
			List ids = new ArrayList();
			ids.add(top.getId());
			List<Binder> others = new ArrayList(binders);
			for (Binder b:others) {
				//have same owner and team membership have same acl
				if (b.getOwnerId() != null && b.getOwnerId().equals(top.getOwnerId()) &&
						top.getTeamMemberIds().equals(b.getTeamMemberIds())) {
					binders.remove(b);
					ids.add(b.getId());
				}
			}
			org.dom4j.Document qTree = buildQueryforUpdate(ids);
	    	//don't need to add access check to update of acls
			//access to entries is not required to update the folder acl
	    	QueryBuilder qb = new QueryBuilder(null);
			// add this query and list of ids to the lists we'll pass to updateDocs.
			updateQueries.add(qb.buildQuery(qTree, true).getQuery());
			updateIds.add(EntityIndexUtils.getBinderAccess(top));
		}
		
	}
	private static org.dom4j.Document buildQueryforUpdate(List folderIds) {
		org.dom4j.Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		Element qTreeOrElement = qTreeRootElement.addElement(QueryBuilder.OR_ELEMENT);
    	Element qTreeAndElement = qTreeOrElement.addElement(QueryBuilder.AND_ELEMENT);
    	Element idsOrElement = qTreeAndElement.addElement((QueryBuilder.OR_ELEMENT));
    	//get all the entrys, replies and attachments
    	// Folderid's and doctypes:{entry, binder, attachment}
    	for (Iterator iter = folderIds.iterator(); iter.hasNext();) {
    		Element field = idsOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(iter.next().toString());
    	}
    	
    	Element typeOrElement = qTreeAndElement.addElement((QueryBuilder.OR_ELEMENT));
    	for (int i =0; i < docTypes.length; i++) {
    		Element field = typeOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(docTypes[i]);
    	}
    	// Get all the binder's themselves
    	// OR Doctype=binder and binder id's
    	Element andElement = qTreeOrElement.addElement((QueryBuilder.AND_ELEMENT));
    	Element orOrElement = andElement.addElement((QueryBuilder.OR_ELEMENT));
    	Element field = andElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(BasicIndexUtils.DOC_TYPE_BINDER);
	   	for (Iterator iter = folderIds.iterator(); iter.hasNext();) {
    		field = orOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.DOCID_FIELD);
			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(iter.next().toString());
    	}
	   	return qTree;
	}
	
}
