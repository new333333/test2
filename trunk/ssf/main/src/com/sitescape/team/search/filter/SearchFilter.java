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
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

public class SearchFilter {

	// default join as 'OR' so joinAnd is false 
	private Boolean joinAnd = Boolean.FALSE;
	
	protected Document filter;
	
	protected Element sfRoot;
	
	protected Element currentFilterTerms = null;

	public static String AllEntries = "_all_entries";

	public SearchFilter() {
		this(SearchFilterKeys.FilterRootName);
	}
	
	public SearchFilter(Document filter) {
		this.filter = filter;
		this.sfRoot = filter.getRootElement(); 
		
		Iterator nodes = sfRoot.nodeIterator();
		while (nodes.hasNext()) {
			Element el = (Element)nodes.next();
			if (el.getName().equals(SearchFilterKeys.FilterTerms)) {
				this.currentFilterTerms = el;
				Attribute joinAndAttr = this.currentFilterTerms.attribute(SearchFilterKeys.FilterAnd);
				joinAnd = Boolean.parseBoolean(joinAndAttr.getText());
				break;
			}
		}
	}
	
	public SearchFilter(Boolean joinAsAnd) {
		this();
		this.joinAnd = joinAsAnd;
	}

	public SearchFilter(String rootElement) {
		super();
		filter = DocumentHelper.createDocument();
		sfRoot = filter.addElement(rootElement);
	}

	public SearchFilter(String rootElement, Boolean joinAsAnd) {
		this(rootElement);
		this.joinAnd = joinAsAnd;
	}
	
	private void addFilter(String field, String type, String searchTerm) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, type);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, field);
		Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		filterTermValueEle.setText(searchTerm.replaceFirst("\\*", "").trim());
		
		filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, type);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, field);
		filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		filterTermValueEle.setText(searchTerm.trim());
		
	}
	public void addTitleFilter(String type, String searchTerm) {
		addFilter(EntityIndexUtils.TITLE_FIELD, type, searchTerm);
	}
	
	public void addLoginNameFilter(String type, String searchTerm) {
		addFilter(ProfileIndexUtils.LOGINNAME_FIELD, type, searchTerm);
	}
	
	public void addTagsFilter(String tagsType, String searchTerm) {
		if (tagsType != null && tagsType == SearchFilterKeys.FilterTypeCommunityTagSearch) { 
			addTermInCurrentFilter(tagsType, searchTerm);
		} else if (tagsType != null && tagsType == SearchFilterKeys.FilterTypePersonalTagSearch) {
			addTermInCurrentFilter(tagsType, searchTerm);
		} else {
			// add general
			addTermInCurrentFilter(SearchFilterKeys.FilterTypeTags, searchTerm);
		}
	}
	
	public void addAndNestedTerms(String type, String tagType, List searchTerms) {
		//Add terms to search folders and workspaces
		
		newCurrent();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");
		
		addNestedTerms(type, tagType, searchTerms);
	}
	
	public void addNestedTerms(String type, String tagType, List searchTerms) {
		//Add terms to search folders and workspaces
		
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		
		if (type != null) {
			filterTerm.addAttribute(SearchFilterKeys.FilterType, type);
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
		addAndNestedTerms(type, null, null);
	}
	
	public void addAndFolderId(String folderId) {
		newCurrent();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");
		
		addFolderId(folderId);
	}

	public void addFolderId(String folderId) {
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeFolders);
		filterTerm.addAttribute(SearchFilterKeys.FilterFolderId, folderId);	
	}
	
	public void addBinderParentId(String binderId) {
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeBinderParent);
		filterTerm.addAttribute(SearchFilterKeys.FilterBinderId, binderId);	
	}
	
	public void addEntryId(String entryId) {
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryId);
		filterTerm.addAttribute(SearchFilterKeys.FilterEntryId, entryId);	
	}
	
	public void addDocumentType(String documentType) {
		checkCurrent();
 
		if (documentType == null || documentType.equals("")) {
			return;
		}
		
		addNestedTerms(SearchFilterKeys.FilterTypeDocTypes, SearchFilterKeys.FilterDocType, Arrays.asList(new String[] { documentType }));
	}
	
	public void addEntryTypes(String[] entryTypes) {
		checkCurrent();
 
		if (entryTypes == null || entryTypes.length == 0) {
			return;
		}
		
		addNestedTerms(SearchFilterKeys.FilterTypeEntryTypes, SearchFilterKeys.FilterEntryType, Arrays.asList(entryTypes));
	}
	
	public void addTerm (String elementType, String attributeName, String value) {
		Element field = sfRoot.addElement(elementType);
		field.addAttribute(attributeName,value);
	}
	
	private void addTermInCurrentFilter (String elementType, String value) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, elementType);
		filterTerm.addText(value);
	}
	
	public void addFilterName (String name) {
		Element filterNameEle = sfRoot.addElement(SearchFilterKeys.FilterName);
		filterNameEle.setText(name);
	}
	
	public void addTextFilter(String searchText) {
		addTermInCurrentFilter(SearchFilterKeys.FilterTypeSearchText, searchText);
	}
	
	public void addWorkflowFilter(String workflowId, String[] states) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeWorkflow);
		filterTerm.addAttribute(SearchFilterKeys.FilterWorkflowDefId, workflowId);
		for (int i = 0; i < states.length; i++) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterWorkflowStateName);
			newTerm.setText(states[i]);
		}
	}
	
	public void addEntryAttributeValues(String defId, String name, String[] value) {
		checkCurrent();
		if (!defId.equals("") && name.equals(AllEntries)) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
			filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
		} else if (!defId.equals("") && !name.equals("") && value.length > 0) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
			//If not selecting a "common" element, store the definition id, too
			if (!defId.equals("_common")) filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, name);
			for (int j = 0; j < value.length; j++) {
				Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
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
		checkCurrent();
	
		// this is not the same as in addFilter! 
		// the setText method is called on filterTerm and not on filterTermValueElem
		if (searchText.length()>0) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.EXTENDED_TITLE_FIELD);
			Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTerm.setText(searchText.replaceFirst("\\*", "").trim());
			
			filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.EXTENDED_TITLE_FIELD);
			filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTerm.setText(searchText.trim());
		}
		
		List searchTerms = new ArrayList(3);
		searchTerms.add(EntityIdentifier.EntityType.folder.name());
		searchTerms.add(EntityIdentifier.EntityType.workspace.name());
		searchTerms.add(EntityIdentifier.EntityType.profiles.name());
		addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes,SearchFilterKeys.FilterEntityType, searchTerms);		

		searchTerms = new ArrayList(1);
		searchTerms.add(BasicIndexUtils.DOC_TYPE_BINDER);
		addAndNestedTerms(SearchFilterKeys.FilterTypeDocTypes,SearchFilterKeys.FilterDocType, searchTerms);		
	}
	
	
	protected void newCurrent() {
		currentFilterTerms = sfRoot.addElement(SearchFilterKeys.FilterTerms);
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, joinAnd.toString());
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
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeAuthor);
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
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeCreatorById);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.CREATORID_FIELD);
		if (userTitle != null && userTitle != "") filterTerm.addAttribute(SearchFilterKeys.FilterCreatorTitle, userTitle);
		Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		newTerm.setText(userId);
	}
	
	public void addCreationDateRange(String startDate, String endDate) {
		addDateRange(EntityIndexUtils.CREATION_DAY_FIELD, startDate, endDate);
	}
	
	public void addCreationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.CREATION_DAY_FIELD);
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	
	public void addModificationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.MODIFICATION_DAY_FIELD);
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	public void addModificationDateRange(String startDate, String endDate) {
		addDateRange(EntityIndexUtils.MODIFICATION_DAY_FIELD, startDate, endDate);
	}
	
	private void addDateRange(String fieldName, String startDate, String endDate) {
		if ((startDate== null || startDate.equals("")) && ((endDate == null) || endDate.equals(""))) return;
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, fieldName);
		if (startDate != null && !startDate.equals("")) { filterTerm.addAttribute(SearchFilterKeys.FilterStartDate, startDate);}
		if (endDate != null && !endDate.equals("")) { filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, endDate);}		
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
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		filterTermValueEle.setText(title.trim());
	}

	public void addText(String text) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeSearchText);
		filterTerm.addText(text);
	}
	
	public void addTag(String tag) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeTags);
		filterTerm.addText(tag);
	}
	
	public void addPersonalTag(String tag) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypePersonalTagSearch);
		filterTerm.addText(tag);
	}
	
	public void addCommunityTag(String tag) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeCommunityTagSearch);
		filterTerm.addText(tag);		
	}
	
	public void addWorkflow(String workflowId, String[] states) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeWorkflow);
		filterTerm.addAttribute(SearchFilterKeys.FilterWorkflowDefId, workflowId);
		for (int i = 0; i < states.length; i++) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterWorkflowStateName);
			newTerm.setText(states[i]);
		}
	}
	
	public void addEvent(String defId, String name, String[] eventDates) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEvent);
				
		filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, name);
		
		for (int i = 0; i < eventDates.length; i++) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterEventDate);
			newTerm.setText(eventDates[i]);
		}
	}
	
	public void addPlace(String place) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		filterTerm.setText(place.replaceFirst("\\*", "").trim());
		Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		
		filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		filterTerm.setText(place.trim());
		filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
	
		List searchTerms = new ArrayList(3);
		searchTerms.add(EntityIdentifier.EntityType.folder.name());
		searchTerms.add(EntityIdentifier.EntityType.workspace.name());
		searchTerms.add(EntityIdentifier.EntityType.profiles.name());
		addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes,SearchFilterKeys.FilterEntityType, searchTerms);		

		searchTerms = new ArrayList(1);
		searchTerms.add(BasicIndexUtils.DOC_TYPE_BINDER);
		addAndNestedTerms(SearchFilterKeys.FilterTypeDocTypes,SearchFilterKeys.FilterDocType, searchTerms);	
	}
	
	public  void addEntryAttributeValues(String defId, String attributeName, String[] fieldValues, String valueType) {
		if (valueType != null && valueType.equals("description")) {
			attributeName="_desc";
		}
		if (fieldValues != null && valueType != null &&
			(valueType.equals("date") || valueType.equals("event"))) {
			for (int c = 0; c < fieldValues.length; c++) {
				if (fieldValues[c] != null) {
					fieldValues[c] = fieldValues[c].replaceAll("-", "");
				}
			}
		}	
		
		if (valueType != null && valueType.equals("event")) {
			addEvent(defId, attributeName, fieldValues);
		} else {
			addEntryAttributeValues(defId, attributeName, fieldValues);
		}
		
	}
	

	/**
	 * Gets filter name for given search filter document.
	 * 
	 * @param searchFilter <code>null</code> if search filter has no name or given document is <code>null</code>
	 * @return
	 */
	public static String getFilterName(Document searchFilter) {
		if (searchFilter == null) {
			return null;
		}
		Element sfRoot = searchFilter.getRootElement();
		if (sfRoot == null) {
			return null;
		}
		Element filterName = sfRoot.element(SearchFilterKeys.FilterName);
		if (filterName == null) {
			return null;
		}
		return filterName.getText();
	}
	
}
