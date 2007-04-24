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
package com.sitescape.team.search.filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.web.util.FilterHelper;

public class SearchFilter {

	// default join as 'OR' so joinAnd is false 
	private Boolean joinAnd = Boolean.FALSE;
	
	protected Document filter;
	
	protected Element sfRoot;
	
	protected Element currentFilterTerms = null;

	public static String AllEntries = "_all_entries";

	public SearchFilter(boolean joinAsAnd) {
		this();
		this.joinAnd = joinAsAnd;
	}

	public SearchFilter(String rootElement, Boolean joinAsAnd) {
		this(rootElement);
		this.joinAnd = joinAsAnd;
	}
	
	public SearchFilter() {
		this(FilterHelper.FilterRootName);
	}

	public SearchFilter(String rootElement) {
		super();
		filter = DocumentHelper.createDocument();
		sfRoot = filter.addElement(rootElement);
	}
	
	private void addFilter(String field, String type, String searchTerm) {
		if (currentFilterTerms == null) {
			newCurrent();
		}
		
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, type);
		filterTerm.addAttribute(FilterHelper.FilterElementName, field);
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(searchTerm.replaceFirst("\\*", "").trim());
		
		filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, type);
		filterTerm.addAttribute(FilterHelper.FilterElementName, field);
		filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(searchTerm.trim());
		
	}

	public void addTitleFilter(String type, String searchTerm) {
		addFilter(EntityIndexUtils.TITLE_FIELD, type, searchTerm);
	}
	
	public void addLoginNameFilter(String type, String searchTerm) {
		addFilter(ProfileIndexUtils.LOGINNAME_FIELD, type, searchTerm);
	}
	
	public void addTagsFilter(String tagsType, String searchTerm) {
		if (tagsType != null && tagsType == FilterHelper.FilterTypeCommunityTagSearch) { 
			addTermInCurrentFilter(tagsType, searchTerm);
		} else if (tagsType != null && tagsType == FilterHelper.FilterTypePersonalTagSearch) {
			addTermInCurrentFilter(tagsType, searchTerm);
		} else {
			// add general
			addTermInCurrentFilter(FilterHelper.FilterTypeTags, searchTerm);
		}
	}
	
	public void addAndFilter(String type, String tagType, List searchTerms) {
		//Add terms to search folders and workspaces
		
		newCurrent();
		currentFilterTerms.addAttribute(FilterHelper.FilterAnd, "true");
		
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		
		if (type != null) {
			filterTerm.addAttribute(FilterHelper.FilterType, type);
		}
		if ((tagType != null) && (searchTerms != null)) {
			Iterator it = searchTerms.iterator();
			while (it.hasNext()) {
				Element filterTerm2 = filterTerm.addElement(tagType);
				filterTerm2.setText((String) it.next());
			}
		}
	}
	public void addAndFilter(String type) {
		addAndFilter(type, null, null);
	}
	
	public void addAndFolderTerm(String folderId) {
		newCurrent();
		currentFilterTerms.addAttribute(FilterHelper.FilterAnd, "true");
		
		addFolderTerm(folderId);
	}

	public void addFolderTerm(String folderId) {
		if (currentFilterTerms == null) {
			newCurrent();
		}
 
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeFolders);
		filterTerm.addAttribute(FilterHelper.FilterFolderId, folderId);	
	}
	
	public void addTerm (String elementType, String attributeName, String value) {
		Element field = sfRoot.addElement(elementType);
		field.addAttribute(attributeName,value);
	}
	
	private void addTermInCurrentFilter (String elementType, String value) {
		if (currentFilterTerms == null) {
			newCurrent();
		}
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, elementType);
		filterTerm.addText(value);
	}
	
	public void addFilterName (String name) {
		Element filterNameEle = sfRoot.addElement(FilterHelper.FilterName);
		filterNameEle.setText(name);
	}
	
	public void addTextFilter(String searchText) {
		addTermInCurrentFilter(FilterHelper.FilterTypeSearchText, searchText);
	}
	
	public void addWorkflowFilter(String workflowId, String[] states) {
		if (currentFilterTerms == null) {
			newCurrent();
		}
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeWorkflow);
		filterTerm.addAttribute(FilterHelper.FilterWorkflowDefId, workflowId);
		for (int i = 0; i < states.length; i++) {
			Element newTerm = filterTerm.addElement(FilterHelper.FilterWorkflowStateName);
			newTerm.setText(states[i]);
		}
	}
	
	public void addEntryTypeFilter(String defId, String name, String[] value) {
		if (currentFilterTerms == null) {
			newCurrent();
		}
		if (!defId.equals("") && name.equals(AllEntries)) {
			Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			filterTerm.addAttribute(FilterHelper.FilterEntryDefId, defId);
		} else if (!defId.equals("") && !name.equals("") && value.length > 0) {
			Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
			//If not selecting a "common" element, store the definition id, too
			if (!defId.equals("_common")) filterTerm.addAttribute(FilterHelper.FilterEntryDefId, defId);
			filterTerm.addAttribute(FilterHelper.FilterElementName, name);
			for (int j = 0; j < value.length; j++) {
				Element newTerm = filterTerm.addElement(FilterHelper.FilterElementValue);
				newTerm.setText(value[j]);
			}
		}
	}
	
//	public void addCommunityTagField (String tag) {
//		currentFilterTerms = sfRoot.addElement(QueryBuilder.FIELD_ELEMENT);
//		currentFilterTerms.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.TAG_FIELD);
//		Element child = currentFilterTerms.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
//		child.setText(tag);
//	}
//	
//	public void addPersonalTagField (String tags) {
//		Element fieldTag = sfRoot.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
//	    String [] strTagArray = tags.split("\\s");
//	    for (int k = 0; k < strTagArray.length; k++) {
//	    	String strTag = strTagArray[k];
//	    	if (strTag.equals("")) continue;
//    		Element childTag = fieldTag.addElement(QueryBuilder.TAG_ELEMENT);
//    		childTag.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, strTag);
//	    }
//	}
	
	public void addPlacesFilter(String searchText) {
		if (currentFilterTerms == null) {
			newCurrent();
		}
	
		// this is not the same as in addFilter! 
		// the setText method is called on filterTerm and not on filterTermValueElem
		if (searchText.length()>0) {
			Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeElement);
			filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.EXTENDED_TITLE_FIELD);
			Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTerm.setText(searchText.replaceFirst("\\*", "").trim());
			
			filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
			filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeElement);
			filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.EXTENDED_TITLE_FIELD);
			filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
			filterTerm.setText(searchText.trim());
		}
		
		List searchTerms = new ArrayList(3);
		searchTerms.add(EntityIdentifier.EntityType.folder.name());
		searchTerms.add(EntityIdentifier.EntityType.workspace.name());
		searchTerms.add(EntityIdentifier.EntityType.profiles.name());
		addAndFilter(FilterHelper.FilterTypeEntityTypes,FilterHelper.FilterEntityType, searchTerms);		

		searchTerms = new ArrayList(1);
		searchTerms.add(BasicIndexUtils.DOC_TYPE_BINDER);
		addAndFilter(FilterHelper.FilterTypeDocTypes,FilterHelper.FilterDocType, searchTerms);		
	}
	
	
	protected void newCurrent() {
		currentFilterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
		currentFilterTerms.addAttribute(FilterHelper.FilterAnd, joinAnd.toString());
	}
	
	protected void checkCurrent() {
		if (currentFilterTerms == null) {
			newCurrent();
		}
	}

	public Document getFilter() {
		return filter;
	}

	public void setJoiner(boolean joiner) {
		this.joinAnd = joiner;
	}
	
	/**
	 * extends current search fiter to look for author by name (name or title fields)
	 * @param name
	 */
	public void addCreator(String name) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeAuthor);
		filterTerm.setText(name);
		
	}
	
	/**
	 * extends current search fiter to look for author by id
	 * @param userId
	 */
	public void addCreatorById(String userId) {
		addCreatorById(userId, null);
	}
	
	/**
	 * extends current search fiter to look for author by id
	 * userTitle is only additional information to display in search form, not used for search
	 * @param userId
	 * @param userTitle
	 */
	public void addCreatorById(String userId, String userTitle) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeCreatorById);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.CREATORID_FIELD);
		if (userTitle != null && userTitle != "") filterTerm.addAttribute(FilterHelper.FilterCreatorTitle, userTitle);
		Element newTerm = filterTerm.addElement(FilterHelper.FilterElementValue);
		newTerm.setText(userId);
	}
	
	public void addCreationDateRange(String startDate, String endDate) {
		addDateRange(EntityIndexUtils.CREATION_DAY_FIELD, startDate, endDate);
	}
	
	public void addCreationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeDate);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.CREATION_DAY_FIELD);
		filterTerm.addAttribute(FilterHelper.FilterEndDate, date);
	}
	
	public void addModificationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeDate);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.MODIFICATION_DAY_FIELD);
		filterTerm.addAttribute(FilterHelper.FilterEndDate, date);
	}
	public void addModificationDateRange(String startDate, String endDate) {
		addDateRange(EntityIndexUtils.MODIFICATION_DAY_FIELD, startDate, endDate);
	}
	
	private void addDateRange(String fieldName, String startDate, String endDate) {
		if ((startDate== null || startDate.equals("")) && ((endDate == null) || endDate.equals(""))) return;
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeDate);
		filterTerm.addAttribute(FilterHelper.FilterElementName, fieldName);
		if (startDate != null && !startDate.equals("")) { filterTerm.addAttribute(FilterHelper.FilterStartDate, startDate);}
		if (endDate != null && !endDate.equals("")) { filterTerm.addAttribute(FilterHelper.FilterEndDate, endDate);}		
	}
	
	public void addCreationDate(Date date) {
		addCreationDate(searchFormated(date));
	}
	public void addCreationDateBefore(Date date) {
		addCreationDateRange(null, searchFormated(date));
	}
	public void addCreationDateAfter(Date date) {
		addCreationDateRange(searchFormated(date), null);
	}
	
	public void addCreationDateRange(Date startDate, Date endDate) {
		String formatedStartDate = null;
		if (startDate != null) formatedStartDate = searchFormated(startDate);
		String formatedEndDate = null;
		if (endDate != null) formatedEndDate = searchFormated(endDate);
		addCreationDateRange(formatedStartDate, formatedEndDate);
	}
	
	private String searchFormated(Date date) {
		SimpleDateFormat outputFormater = new SimpleDateFormat("yyyyMMdd");
		String outputDate = outputFormater.format(date);
		return outputDate;
	}
	
	public void addModificationDateRange(Date startDate, Date endDate) {
		String formatedStartDate = null;
		if (startDate != null) formatedStartDate = searchFormated(startDate);
		String formatedEndDate = null;
		if (endDate != null) formatedEndDate = searchFormated(endDate);
		addModificationDateRange(formatedStartDate, formatedEndDate);
	}

	public void addModificationDate(Date date) {
		addModificationDate(searchFormated(date));
	}
	public void addModificationDateBefore(Date date) {
		addModificationDateRange(null, searchFormated(date));
	}
	public void addModificationDateAfter(Date date) {
		addModificationDateRange(searchFormated(date), null);
	}
	
	public void addTitle(String title) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(title.trim());
	}

	public void addText(String text) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeSearchText);
		filterTerm.addText(text);
	}
	
	public void addTag(String tag) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeTags);
		filterTerm.addText(tag);
	}
	
	public void addPersonalTag(String tag) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypePersonalTagSearch);
		filterTerm.addText(tag);
	}
	
	public void addCommunityTag(String tag) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeCommunityTagSearch);
		filterTerm.addText(tag);		
	}
	
	public void addWorkflow(String workflowId, String[] states) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeWorkflow);
		filterTerm.addAttribute(FilterHelper.FilterWorkflowDefId, workflowId);
		for (int i = 0; i < states.length; i++) {
			Element newTerm = filterTerm.addElement(FilterHelper.FilterWorkflowStateName);
			newTerm.setText(states[i]);
		}
	}
	
	public void addEvent(String defId, String name, String[] eventDates) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEvent);
				
		filterTerm.addAttribute(FilterHelper.FilterEntryDefId, defId);
		filterTerm.addAttribute(FilterHelper.FilterElementName, name);
		
		for (int i = 0; i < eventDates.length; i++) {
			Element newTerm = filterTerm.addElement(FilterHelper.FilterEventDate);
			newTerm.setText(eventDates[i]);
		}
	}
	
	public void addPlace(String place) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeElement);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		filterTerm.setText(place.replaceFirst("\\*", "").trim());
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		
		filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeElement);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		filterTerm.setText(place.trim());
		filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
	
		List searchTerms = new ArrayList(3);
		searchTerms.add(EntityIdentifier.EntityType.folder.name());
		searchTerms.add(EntityIdentifier.EntityType.workspace.name());
		searchTerms.add(EntityIdentifier.EntityType.profiles.name());
		addAndFilter(FilterHelper.FilterTypeEntityTypes,FilterHelper.FilterEntityType, searchTerms);		

		searchTerms = new ArrayList(1);
		searchTerms.add(BasicIndexUtils.DOC_TYPE_BINDER);
		addAndFilter(FilterHelper.FilterTypeDocTypes,FilterHelper.FilterDocType, searchTerms);	
	}
	
	public  void addEntryType(String defId, String name, String[] value, String valueType) {
		if (valueType != null && valueType.equals("description")) {
			name="_desc";
		}
		if (value != null && valueType != null &&
			(valueType.equals("date") || valueType.equals("event"))) {
			for (int c = 0; c < value.length; c++) {
				if (value[c] != null) {
					value[c] = value[c].replaceAll("-", "");
				}
			}
		}	
		
		if (valueType != null && valueType.equals("event")) {
			addEvent(defId, name, value);
		} else {
			addEntryTypeFilter(defId, name, value);
		}
		
	}
	
	
}
