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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.DateTools;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.task.TaskHelper;

public class SearchFilter {
	protected static List placeTypes = new ArrayList(3);
	static {	
		placeTypes.add(EntityIdentifier.EntityType.folder.name());
		placeTypes.add(EntityIdentifier.EntityType.workspace.name());
		placeTypes.add(EntityIdentifier.EntityType.profiles.name());
	}
	protected static List binderType = new ArrayList(1);
	static {
		binderType.add(BasicIndexUtils.DOC_TYPE_BINDER);
	}
	
	public static class Creator {
		
		String id;
		
		String name;
		
		public Creator(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	public static class Tag {
		
		enum Type {
			PERSONAL, COMMUNITY, BOTH;
		}
		
		Type type;
		
		String tag;
		
		public Tag(Type type, String tag) {
			this.type = type;
			this.tag = tag;
		}
	}
	
	public static class Workflow {
		
		String id;
		
		String[] steps;

		public Workflow(String id, String[] steps) {
			this.id = id;
			this.steps = steps;
		}
		
	}
	
	public static class Entry {
		
		String typeId;
		
		String fieldId;
		
		String[] value;
		
		String valueType;

		public Entry(String typeId, String fieldId, String[] value, String valueType) {
			super();
			this.typeId = typeId;
			this.fieldId = fieldId;
			this.value = value;
			this.valueType = valueType;
		}
	}
	
	public static class Period {
		
		Date start;
		
		Date end;

		public Period(Date start, Date end) {
			this.start = start;
			this.end = end;
		}
		
	}

	// default join as 'OR' so joinAnd is false 
	private Boolean joinAnd = Boolean.FALSE;
	
	protected Document filter;
	
	protected Element sfRoot;
	
	protected Element currentFilterTerms = null;

	public static String AllEntries = "_all_entries";

	public SearchFilter() {
		filter = DocumentHelper.createDocument();
		sfRoot = filter.addElement(SearchFilterKeys.FilterRootName);
	}
	
	public SearchFilter(Document filter) {
		this.filter = (Document)filter.clone();
		this.sfRoot = this.filter.getRootElement(); 
		
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
	
	public void appendFilter(Document newFilter) {
		if (newFilter == null) {
			return;
		}
		
		if (this.filter == null || sfRoot.nodeCount() == 0) {// no filter or empty filter
		
			this.filter = (Document)newFilter.clone();
			this.sfRoot = this.filter.getRootElement();
			
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
		
		} else {		
			Element paramRoot = newFilter.getRootElement();
			
			Iterator nodes = paramRoot.nodeIterator();
			while (nodes.hasNext()) {
				Element el = (Element)nodes.next();
				if (el.getName().equals(SearchFilterKeys.FilterTerms)) {
					
					this.sfRoot.add(el);
					
					this.currentFilterTerms = el;
					Attribute joinAndAttr = this.currentFilterTerms.attribute(SearchFilterKeys.FilterAnd);
					joinAnd = Boolean.parseBoolean(joinAndAttr.getText());
				}
			}
		}
	}
	
	public SearchFilter(Boolean joinAsAnd) {
		this();
		this.joinAnd = joinAsAnd;
	}
	
	private void addFieldFilter(String field, String type, String searchTerm) {
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
	public void addTitleFilter(String searchTerm) {
		addFieldFilter(EntityIndexUtils.TITLE_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm);
	}
	
	public void addLoginNameFilter(String searchTerm) {
		addFieldFilter(ProfileIndexUtils.LOGINNAME_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm);
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
	
	public void addAndNestedTerms(String type, String tagType, Collection searchTerms) {
		//Add terms to search folders and workspaces
		
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");
		
		addNestedTerms(type, tagType, searchTerms);
	}
	
	public void addNestedTerms(String type, String tagType, Collection searchTerms) {
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
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");
		
		addFolderId(folderId);
	}

	public void addFolderId(String folderId) {
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeFolders);
		filterTerm.addAttribute(SearchFilterKeys.FilterFolderId, folderId);	
	}
	
	public void addFolderIds(Collection folderIds) {
		if (folderIds == null || folderIds.isEmpty()) {
			return;
		}
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeFoldersList);
		Iterator it = folderIds.iterator();
		while (it.hasNext()) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterFolderId);
			newTerm.setText((String)it.next());
		}
	}
	
	public void addAncestryId(String folderId) {
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeAncestry);
		filterTerm.addAttribute(SearchFilterKeys.FilterFolderId, folderId);	
	}
	
	public void addAncestryIds(Collection folderIds) {
		if (folderIds == null) {
			return;
		}
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeAncestriesList);
		Iterator it = folderIds.iterator();
		while (it.hasNext()) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterFolderId);
			newTerm.setText((String)it.next());
		}
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
	
	public void addEntryAttributeValues(String defId, String name, String[] value) {
		checkCurrent();
		addEntryAttributeValues(currentFilterTerms, defId, name, value);
	}
	
	private void addEntryAttributeValues(Element parent, String defId, String name, String[] value) {
		if (defId != null && !defId.equals("") && name.equals(AllEntries)) {
			Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
			filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
		} else if ((defId != null && !defId.equals("")) || (name != null && !name.equals("") && value.length > 0)) {
			Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
			
			if (defId != null && !defId.equals("")) {
				//If not selecting a "common" element, store the definition id, too
				if (!defId.equals("_common")) filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
			}
			
			if (name != null && !name.equals("") && value.length > 0) {
				filterTerm.addAttribute(SearchFilterKeys.FilterElementName, name);
				for (int j = 0; j < value.length; j++) {
					Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
					newTerm.setText(value[j]);
				}
			}
		}
	}

	public void addEntryAttributeValues(String defId, String attributeName, String[] fieldValues, String valueType) {
		checkCurrent();
		addEntryAttributeValues(currentFilterTerms, defId, attributeName, fieldValues, valueType);
	}
	
	private void addEntryAttributeValues(Element parent, String defId, String attributeName, String[] fieldValues, String valueType) {
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
			addEvent(parent, defId, attributeName, fieldValues);
		} else {
			addEntryAttributeValues(parent, defId, attributeName, fieldValues);
		}
	}

	public void addEntries(List<Entry> entries) {
		if (entries == null || entries.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element entriesListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<Entry> it = entries.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			addEntryAttributeValues(entriesListParent, entry.typeId, entry.fieldId, entry.value, entry.valueType);
		}
	}
		
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
		
		addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes,SearchFilterKeys.FilterEntityType, placeTypes);		

		addAndNestedTerms(SearchFilterKeys.FilterTypeDocTypes,SearchFilterKeys.FilterDocType, binderType);		
	}
	
	
	protected void newCurrentFilterTermsBlock() {
		newCurrentFilterTermsBlock(this.joinAnd);
	}
	
	public void newCurrentFilterTermsBlock(boolean joinAnd) {
		this.currentFilterTerms = newFilterTermsBlock(sfRoot, joinAnd);
	}
	
	private Element newFilterTermsBlock(Element parent, boolean joinAnd) {
		Element filterTerms = parent.addElement(SearchFilterKeys.FilterTerms);
		filterTerms.addAttribute(SearchFilterKeys.FilterAnd, Boolean.toString(joinAnd));
		return filterTerms;
	}
	
	protected void checkCurrent() {
		if (currentFilterTerms == null) {
			newCurrentFilterTermsBlock();
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
	public void addCreatorByName(String name) {
		checkCurrent();
		addCreatorByName(currentFilterTerms, name);
	}
	
	private void addCreatorByName(Element parent, String name) {
		if (name == null || name.equals("")) {
			return;
		}
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeCreatorByName);
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
		addCreatorById(currentFilterTerms, userId, userTitle);
	}
	
	public void addCreatorById(Element parent, String userId, String userTitle) {
		if (userId == null || userId.equals("")) {
			return;
		}
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		if (userId.equals(SearchFilterKeys.CurrentUserId)) {
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeRelative);
			filterTerm.addAttribute(SearchFilterKeys.FilterRelativeType, SearchFilterKeys.FilterTypeCreatorById);
		} else {
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeCreatorById);
		}
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.CREATORID_FIELD);
		if (userTitle != null && !userTitle.equals("")) {
			filterTerm.addAttribute(SearchFilterKeys.FilterCreatorTitle, userTitle);
		}
		Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		newTerm.setText(userId);
	}
	
	/**
	 * Add a list of entry creators to filter. Creators are conneted with OR operator.
	 * 
	 * @param creators
	 */
	public void addCreators(List<Creator> creators) {
		if (creators == null || creators.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element creatorsListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<Creator> it = creators.iterator();
		while (it.hasNext()) {
			Creator creator = it.next();
			if (creator.id == null) {
				addCreatorByName(creatorsListParent, creator.name);
			} else {
				addCreatorById(creatorsListParent, creator.id, creator.name);
			}
		}
	}
	
	public void addTaskStatuses(String[] statuses) {
		if (statuses == null) {
			return;
		}
		
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeTaskStatus);
		for (int i = 0; i < statuses.length; i++) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterTaskStatusName);
			newTerm.setText(statuses[i]);
		}
	}
	
	public void addTaskStartDate(String date) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + "#StartDate");
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	
	public void addCreationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.CREATION_DAY_FIELD);
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	
	public void addCreationDate(Date date) {
		addCreationDate(searchFormated(date));
	}
	
	public void addModificationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, EntityIndexUtils.MODIFICATION_DAY_FIELD);
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	
	public void addModificationDate(Date date) {
		addModificationDate(searchFormated(date));
	}
	
	private void addDateRange(Element parent, String fieldName, String startDate, String endDate) {
		if ((startDate== null || startDate.equals("")) && ((endDate == null) || endDate.equals(""))) {
			return;
		}
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, fieldName);
		if (startDate != null && !startDate.equals("")) { filterTerm.addAttribute(SearchFilterKeys.FilterStartDate, startDate);}
		if (endDate != null && !endDate.equals("")) { filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, endDate);}		
	}
	
	private void addCreationDateRange(Element parent, String startDate, String endDate) {
		addDateRange(parent, EntityIndexUtils.CREATION_DAY_FIELD, startDate, endDate);
	}
	
	public void addCreationDateRange(Date startDate, Date endDate) {
		checkCurrent();
		addCreationDateRange(currentFilterTerms, startDate, endDate);
	}
	
	private void addCreationDateRange(Element parent, Date startDate, Date endDate) {
		String formatedStartDate = null;
		if (startDate != null) formatedStartDate = searchFormated(startDate);
		String formatedEndDate = null;
		if (endDate != null) formatedEndDate = searchFormated(endDate);
		addCreationDateRange(parent, formatedStartDate, formatedEndDate);
	}
	
	public void addCreationDates(List<Period> creationPeriods) {
		if (creationPeriods == null || creationPeriods.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element creationPeriodsListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<Period> it = creationPeriods.iterator();
		while (it.hasNext()) {
			Period period = it.next();
			addCreationDateRange(creationPeriodsListParent, period.start, period.end);
		}
	}
	
	private String searchFormated(Date date) {
		return DateTools.dateToString(date, DateTools.Resolution.DAY);
	}
	
	public void addModificationDateRange(Element parent, String startDate, String endDate) {
		addDateRange(parent, EntityIndexUtils.MODIFICATION_DAY_FIELD, startDate, endDate);
	}
	
	public void addModificationDateRange(Date startDate, Date endDate) {
		checkCurrent();
		addModificationDateRange(currentFilterTerms, startDate, endDate);
	}
	
	public void addModificationDateRange(Element parent, Date startDate, Date endDate) {
		String formatedStartDate = null;
		if (startDate != null) formatedStartDate = searchFormated(startDate);
		String formatedEndDate = null;
		if (endDate != null) formatedEndDate = searchFormated(endDate);
		addModificationDateRange(parent, formatedStartDate, formatedEndDate);
	}

	public void addModificationDates(List<Period> modificationPeriods) {
		if (modificationPeriods == null || modificationPeriods.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element modificationPeriodsListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<Period> it = modificationPeriods.iterator();
		while (it.hasNext()) {
			Period period = it.next();
			addModificationDateRange(modificationPeriodsListParent, period.start, period.end);
		}
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
		addTag(currentFilterTerms, tag);
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeTags);
		filterTerm.addText(tag);
	}
	
	private void addTag(Element parent, String tag) {
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeTags);
		filterTerm.addText(tag);
	}
	
	public void addPersonalTag(String tag) {
		checkCurrent();
		addPersonalTag(currentFilterTerms, tag);
	}
	
	private void addPersonalTag(Element parent, String tag) {
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypePersonalTagSearch);
		filterTerm.addText(tag);
	}
	
	public void addCommunityTag(String tag) {
		checkCurrent();
		addCommunityTag(currentFilterTerms, tag);	
	}
	
	private void addCommunityTag(Element parent, String tag) {
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeCommunityTagSearch);
		filterTerm.addText(tag);		
	}
	
	/**
	 * Add a list of tags to filter. Tags are conneted with OR operator.
	 * 
	 * @param tags
	 */
	public void addTags(List<Tag> tags) {
		if (tags == null || tags.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element tagsListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<Tag> it = tags.iterator();
		while (it.hasNext()) {
			Tag tag = it.next();
			if (tag.type.equals(Tag.Type.BOTH)) {
				addTag(tagsListParent, tag.tag);
			} else if (tag.type.equals(Tag.Type.PERSONAL)) {
				addPersonalTag(tagsListParent, tag.tag);
			} else if (tag.type.equals(Tag.Type.COMMUNITY)) {
				addCommunityTag(tagsListParent, tag.tag);
			}
		}
	}
	
	public void addWorkflow(String workflowId, String[] states) {
		checkCurrent();
		addWorkflow(currentFilterTerms, workflowId, states);
	}
	
	private void addWorkflow(Element parent, String workflowId, String[] states) {
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeWorkflow);
		filterTerm.addAttribute(SearchFilterKeys.FilterWorkflowDefId, workflowId);
		for (int i = 0; i < states.length; i++) {
			Element newTerm = filterTerm.addElement(SearchFilterKeys.FilterWorkflowStateName);
			newTerm.setText(states[i]);
		}
	}
	
	public void addWorkflows(List<Workflow> workflows) {
		if (workflows == null || workflows.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element workflowsListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<Workflow> it = workflows.iterator();
		while (it.hasNext()) {
			Workflow workflow = it.next();
			addWorkflow(workflowsListParent, workflow.id, workflow.steps);
		}
	}
	
	public void addEvent(String defId, String name, String[] eventDates) {
		checkCurrent();
		addEvent(currentFilterTerms, defId, name, eventDates);
	}
	
	private void addEvent(Element parent, String defId, String name, String[] eventDates) {
		Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
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
	
		addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes,SearchFilterKeys.FilterEntityType, placeTypes);		

		addAndNestedTerms(SearchFilterKeys.FilterTypeDocTypes,SearchFilterKeys.FilterDocType, binderType);	
	}
	

	/**
	 * Adds relative date
	 * @param daysNumber - how many days in the past (1, 3, 7, 30, 90,...)
	 */
	public void addRelativeLastActivityDate(Integer daysNumber) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeRelative);
		filterTerm.addAttribute(SearchFilterKeys.FilterRelativeType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addText(daysNumber.toString());
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

	/**
	 * It's a currentplace in query call time.
	 * 
	 */
	public void addRelativePlace(boolean searchSubfolders) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeRelative);
		filterTerm.addAttribute(SearchFilterKeys.FilterRelativeType, SearchFilterKeys.FilterTypePlace);
		filterTerm.addText(Boolean.toString(searchSubfolders));
	}

	public void addItemTypes(Collection itemTypes) {
		if (itemTypes == null || itemTypes.isEmpty()) {
			return;
		}
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeItemTypes);
		
		Iterator it = itemTypes.iterator();
		while (it.hasNext()) {
			Element itemTypeTerm = filterTerm.addElement(SearchFilterKeys.FilterItemType);
			itemTypeTerm.setText((String)it.next());
		}
	}

}
