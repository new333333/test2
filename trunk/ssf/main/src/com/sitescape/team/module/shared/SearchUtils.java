package com.sitescape.team.module.shared;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.SortField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.team.module.folder.index.IndexUtils;

public class SearchUtils {
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
	    	
    	if (fieldName.equals(EntityIndexUtils.CREATION_DATE_FIELD)) 	return true;
	    	
	    if (fieldName.equals(EntityIndexUtils.MODIFICATION_DATE_FIELD)) return true;

    	if (fieldName.equals(IndexUtils.LASTACTIVITY_FIELD)) return true;    	

    	if (fieldName.endsWith(EntityIndexUtils.EVENT_FIELD_START_DATE)) return true;

	    if (fieldName.endsWith(EntityIndexUtils.EVENT_FIELD_END_DATE)) return true;
	    	
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
    	if (sortBy.equals(EntityIndexUtils.MODIFICATION_DATE_FIELD)) 
    		sortType = SortField.STRING;
    	
    	fields[0] = new SortField(sortBy, sortType, descend);
    	return fields;
   	}
  	public static org.dom4j.Document getInitalSearchDocument(org.dom4j.Document searchFilter, Map options) {
  		if (searchFilter == null) {
  			//Build a null search filter
  			searchFilter = DocumentHelper.createDocument();
  			Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
  			rootElement.addElement(FilterHelper.FilterTerms);
  		}
  		org.dom4j.Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchFilter, options);
   		return qTree;
  	}
  	public static void getQueryFields(org.dom4j.Document queryTree, Map options) {
  		if (options == null) return;
		Element rootElement = queryTree.getRootElement();
   		//assume this document was setup in getInitalSearchDocument
		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
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
    		Element orEventOrMofidifactionBoolElement = boolElement.addElement(QueryBuilder.OR_ELEMENT);
    			
    		Element orBoolElement = orEventOrMofidifactionBoolElement.addElement(QueryBuilder.OR_ELEMENT);
    		Iterator it = ((List)options.get(ObjectKeys.SEARCH_EVENT_DAYS)).iterator();
    		while (it.hasNext()) {
    			String eventDay = (String)it.next();
    			Element field = orBoolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.EVENT_DATES_FIELD);
    			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    			child.setText(eventDay);
    		}
        			        		
    		Element orModificationDateBoolElement = orEventOrMofidifactionBoolElement.addElement(QueryBuilder.OR_ELEMENT);
    		Element andStartAndEndModificationDate = orModificationDateBoolElement.addElement(QueryBuilder.AND_ELEMENT);
        			        		
    		//	See if there is a modification start date
    		if (options.containsKey(ObjectKeys.SEARCH_MODIFICATION_DATE_START)) {
    			Element range = andStartAndEndModificationDate.addElement(QueryBuilder.RANGE_ELEMENT);
    			range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.MODIFICATION_DAY_FIELD);
    			range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(QueryBuilder.RANGE_START);
    			start.addText((String) options.get(ObjectKeys.SEARCH_MODIFICATION_DATE_START));
    			Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
    			Calendar cal = Calendar.getInstance();
    			cal.set(2999, 0, 1);
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    			String s = formatter.format(cal.getTime());
    			finish.addText((String) s);
    		}
               	
    		//See if there is a modification end date
    		if (options.containsKey(ObjectKeys.SEARCH_MODIFICATION_DATE_END)) {
    			Element range = andStartAndEndModificationDate.addElement(QueryBuilder.RANGE_ELEMENT);
    			range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.MODIFICATION_DAY_FIELD);
    			range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, "true");
    			Element start = range.addElement(QueryBuilder.RANGE_START);
    			Calendar cal = Calendar.getInstance();
    			cal.set(1970, 0, 1);
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    			String s = formatter.format(cal.getTime());
    			start.addText((String) s);
    			Element finish = range.addElement(QueryBuilder.RANGE_FINISH);
    			finish.addText((String) options.get(ObjectKeys.SEARCH_MODIFICATION_DATE_END));  
    		}
    	}
  	}
    public static void extendPrincipalsInfo(List entries, ProfileDao profileDao) {
		Set ids = EntityIndexUtils.getPrincipalsFromSearch(entries);
		Map users = profileDao.loadPrincipalsData(ids, RequestContextHolder.getRequestContext().getZoneId(), false);

		// walk the entries, and stuff in the user object.
		for (int i = 0; i < entries.size(); i++) {
			HashMap entry = (HashMap)entries.get(i);
			if (entry.get(getEntryPrincipalField()) != null) {
				entry.put(WebKeys.PRINCIPAL, users.get(Long.parseLong((String)entry.get(getEntryPrincipalField()))));
	        }        	
		}		
	}
  	
	protected static String getEntryPrincipalField() {
    	return EntityIndexUtils.CREATORID_FIELD;
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
				System.out.println("Attachment:"+attachments.toString());
				
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
}
