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
package org.kablink.teaming.search.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.web.util.DateHelper;
import org.kablink.util.search.Constants;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class SearchFilter {
	private static String [] sample = new String[0];
	
	protected static List placeTypes = new ArrayList(3);
	static {	
		placeTypes.add(EntityIdentifier.EntityType.folder.name());
		placeTypes.add(EntityIdentifier.EntityType.workspace.name());
		placeTypes.add(EntityIdentifier.EntityType.profiles.name());
	}
	protected static List folderTypes = new ArrayList(1);
	static {	
		folderTypes.add(EntityIdentifier.EntityType.folder.name());
	}
	protected static List workspaceTypes = new ArrayList(1);
	static {	
		workspaceTypes.add(EntityIdentifier.EntityType.workspace.name());
	}	
	protected static List binderType = new ArrayList(1);
	static {
		binderType.add(Constants.DOC_TYPE_BINDER);
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
		
		List steps;

		public Workflow(String id, String[] steps) {
			this.id = id;
			addSteps(steps);
		}
		
		public void addSteps(String[] steps) {
			if (steps == null) {
				return;
			}
			if (this.steps == null) {
				this.steps = new ArrayList();
			}
			this.steps.addAll(Arrays.asList(steps));
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
		
		public String start;
		
		public String end;

		public Period(String start, String end) {
			this.start = start;
			this.end = end;
		}

		public static Period parseDatesToPeriod(String startDate, String endDate) {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
			DateTime startD = null;
			if (!startDate.equals("")) {
				startD = fmt.parseDateTime(startDate);
				startD = startD.withMillisOfDay(0);
			}
			DateTime endD = null;
			if (!endDate.equals("")) {
				endD = fmt.parseDateTime(endDate);
				endD = endD.withMillisOfDay(DateHelper.MILIS_IN_THE_DAY);
			}
			return new Period(startD != null ? fmt.print(startD) : null, endD != null ? fmt.print(endD) : null);
		}

		public String getEnd() {
			return end;
		}

		public String getStart() {
			return start;
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
					
					this.sfRoot.add((Element)el.clone());
					
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
	
	private void addFieldFilter(String field, String type, String searchTerm, boolean wildcardOnly, String valueType) {
		checkCurrent();

		boolean hasValueType = ((null != valueType) && (0 < valueType.length()));
		boolean hasWildcard  = searchTerm.contains("*");
		if ((!hasWildcard) || (!wildcardOnly)) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, type);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, field);
			Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTermValueEle.setText(searchTerm.replaceFirst("\\*", "").trim());
			if (hasValueType) {
				filterTermValueEle.addAttribute(SearchFilterKeys.FilterElementValueType, valueType);
			}
		}
		
		if (hasWildcard) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, type);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, field);
			Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTermValueEle.setText(searchTerm.trim());
			if (hasValueType) {
				filterTermValueEle.addAttribute(SearchFilterKeys.FilterElementValueType, valueType);
			}
		}
		
	}
	
	private void addFieldFilter(String field, String type, String searchTerm, String valueType) {
		// Always use the initial form of the method.
		addFieldFilter(field, type, searchTerm, false, valueType);
	}
	
	private void addFieldFilter(String field, String type, String searchTerm) {
		// Always use the initial form of the method.
		addFieldFilter(field, type, searchTerm, false, null);
	}

	/**
	 * Add a filter for the "internal" field
	 */
	public void addInternalFilter( boolean internalOnly )
	{
		addFieldFilter(
					Constants.IDENTITY_INTERNAL_FIELD,
					SearchFilterKeys.FilterTypeEntryDefinition,
					String.valueOf( internalOnly ),
					SearchFilterKeys.FilterValueTypeBoolean );
	}
	
	/**
	 * Add a filter for the "ldap container" field
	 */
	public void addAndLdapContainerFilter( boolean ldapContainer )
	{
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute( SearchFilterKeys.FilterAnd, "true" );
		
		addLdapContainerFilter( ldapContainer );
	}
	
	/**
	 * Add a filter for the "ldap container" field
	 */
	public void addLdapContainerFilter( boolean ldapContainer )
	{
		addFieldFilter(
					Constants.IS_LDAP_CONTAINER_FIELD,
					SearchFilterKeys.FilterTypeEntryDefinition,
					String.valueOf( ldapContainer ),
					SearchFilterKeys.FilterValueTypeBoolean );
	}
	
	/**
	 * Add a filter for determining if we are working with a "team group"
	 */
	public void addAndTeamGroupFilter( boolean teamGroup )
	{
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute( SearchFilterKeys.FilterAnd, "true" );
		
		addTeamGroupFilter( teamGroup );
	}
	
	/**
	 * Add a filter for determining if we are working with a "team group"
	 */
	public void addTeamGroupFilter( boolean teamGroup )
	{
		addFieldFilter(
					Constants.IS_TEAM_GROUP_FIELD,
					SearchFilterKeys.FilterTypeEntryDefinition,
					String.valueOf( teamGroup ),
					SearchFilterKeys.FilterValueTypeBoolean );
	}
	
	/**
	 * Add a filter for the "internal" field
	 */
	public void addAndInternalFilter( boolean internalOnly )
	{
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute( SearchFilterKeys.FilterAnd, "true" );
		
		addInternalFilter( internalOnly );
	}
	
	public void addTitleFilter(String searchTerm, boolean wildcardOnly) {
		addFieldFilter(Constants.TITLE_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm.toLowerCase(), wildcardOnly, null);
	}
	
	public void addTitleFilter(String searchTerm) {
		// Always use the initial form of the method.
		addTitleFilter(searchTerm, false);
	}
	
	public void addLoginNameFilter(String searchTerm, boolean wildcardOnly) {
		addFieldFilter(Constants.LOGINNAME_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm, wildcardOnly, null);
	}
	
	public void addLoginNameFilter(String searchTerm) {
		// Always use the initial form of the method.
		addLoginNameFilter(searchTerm, false);
	}
	
	public void addEmailFilter(String searchTerm, boolean wildcardOnly) {
		addFieldFilter(Constants.EMAIL_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm, wildcardOnly, null);
	}
	
	public void addEmailFilter(String searchTerm) {
		// Always use the initial form of the method.
		addEmailFilter(searchTerm, false);
	}

	public void addEmailDomainFilter(String searchTerm, boolean wildcardOnly) {
		addFieldFilter(Constants.EMAIL_DOMAIN_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm, wildcardOnly, null);
	}
	
	public void addEmailDomainFilter(String searchTerm) {
		// Always use the initial form of the method.
		addEmailDomainFilter(searchTerm, false);
	}

	public void addGroupNameFilter(String searchTerm) {
		addFieldFilter(Constants.GROUPNAME_FIELD, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm);
	}
	
	public void addAndPersonFlagFilter(boolean person) {
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");
		
		addPersonFlagFilter(person);
	}
	
	public void addPersonFlagFilter(boolean person) {
		addFieldFilter(
			Constants.PERSONFLAG_FIELD,
			SearchFilterKeys.FilterTypeEntryDefinition,
			String.valueOf(person),
			SearchFilterKeys.FilterValueTypeBoolean);
	}
	
	public void addAndDisabledPrincipalFilter(boolean disabled) {
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");

		addDisabledPrincipalFilter(disabled);
	}
	
	public void addDisabledPrincipalFilter(boolean disabled) {
		addFieldFilter(
			Constants.DISABLED_PRINCIPAL_FIELD,
			SearchFilterKeys.FilterTypeEntryDefinition,
			String.valueOf(disabled),
			SearchFilterKeys.FilterValueTypeBoolean);
	}
	
	public void addAndSiteAdminFilter(boolean siteAdmin) {
		newCurrentFilterTermsBlock();
		currentFilterTerms.addAttribute(SearchFilterKeys.FilterAnd, "true");

		addSiteAdminFilter(siteAdmin);
	}
	
	public void addSiteAdminFilter(boolean siteAdmin) {
		addFieldFilter(
			Constants.SITE_ADMIN_FIELD,
			SearchFilterKeys.FilterTypeEntryDefinition,
			String.valueOf(siteAdmin),
			SearchFilterKeys.FilterValueTypeBoolean);
	}
	
	public void addAssignmentFilter(String searchTerm) {
		addFieldFilter(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME, SearchFilterKeys.FilterTypeEntryDefinition, searchTerm);
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
	
	public void addBinderParentIds(Collection binderIds) {
		checkCurrent();
 
		Iterator it = binderIds.iterator();
		while (it.hasNext()) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeBinderParent);
			filterTerm.addAttribute(SearchFilterKeys.FilterBinderId, (String)it.next());
		}
	}
	
	public void addIsTeam() {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeIsTeam);
	}
	
	public void addEntryId(String entryId) {
		checkCurrent();
 
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryId);
		filterTerm.addAttribute(SearchFilterKeys.FilterEntryId, entryId);	
	}
	
	public void addDocumentType(String documentType) {
		if (documentType == null || documentType.equals("")) {
			return;
		}
		addDocumentTypes(new String[] { documentType });
	}
	
	public void addDocumentTypes(String[] documentTypes) {
		checkCurrent();
 
		if (documentTypes == null || documentTypes.length == 0) {
			return;
		}
		
		addNestedTerms(SearchFilterKeys.FilterTypeDocTypes, SearchFilterKeys.FilterDocType, Arrays.asList(documentTypes));
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
	
	public void addGlobal () {
		Element filterNameEle = sfRoot.addElement(SearchFilterKeys.FilterGlobal);
		filterNameEle.setText(SearchFilterKeys.FilterGlobalFieldIsGlobal);
	}
	
	public void addTextFilter(String searchText) {
		addTermInCurrentFilter(SearchFilterKeys.FilterTypeSearchText, searchText);
	}
	
	public void addEntryAttributeValues(String defId, String name, String[] value) {
		checkCurrent();
		addEntryAttributeValues(currentFilterTerms, defId, name, value, null);
	}
	
	private void addEntryAttributeValuesAndType(Element parent, String defId, String name, String[] value, String valueType) {
		if (defId != null && !defId.equals("") && name.equals(AllEntries)) {
			Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
			filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
		} else if ((defId != null && !defId.equals("")) || (name != null && !name.equals("") && value != null)) {
			Element filterTerm = parent.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
			if (valueType != null) {
				filterTerm.addAttribute(SearchFilterKeys.FilterElementValueType, valueType);
			}
			if (defId != null && !defId.equals("")) {
				//If not selecting a "common" element, store the definition id, too
				if (!defId.equals("_common")) filterTerm.addAttribute(SearchFilterKeys.FilterEntryDefId, defId);
			}
			
			if (name != null && !name.equals("") && value != null) {
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
			attributeName=Constants.DESC_TEXT_FIELD;
		}
		
		if ("event".equals(valueType)) {
			addEvent(parent, defId, attributeName, fieldValues);
		} else {
			if ("date_time".equals(valueType)) {
				if (fieldValues != null && fieldValues.length == 2) {
					fieldValues = new String[] {fieldValues[0] + " " + fieldValues[1].substring(0, 5)};
				}
			}
			addEntryAttributeValuesAndType(parent, defId, attributeName, fieldValues, valueType);
		}
	}

	public void addEntries(List<Entry> entries) {
		addEntries(entries, null);
	}
	
	public void addEntries(List<Entry> entries, String listType) {
		if (entries == null || entries.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element entriesListParent = newFilterTermsBlock(currentFilterTerms, false);
		if (listType != null) {
			entriesListParent.addAttribute(SearchFilterKeys.FilterListType, listType);
		}
		Iterator<Entry> it = entries.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			addEntryAttributeValues(entriesListParent, entry.typeId, entry.fieldId, entry.value, entry.valueType);
		}
	}
	
	public void addFolderFilter(String searchText) {
		addPlacesFilter(searchText, folderTypes);
	}
		
	public void addWorkspaceFilter(String searchText) {
		addPlacesFilter(searchText, workspaceTypes);
	}

	public void addTeamFilter() {
		addTeamFilter("");
	}
	
	public void addTeamFilter(String searchText) {
		addPlacesFilter(searchText, placeTypes);
		addIsTeam();
	}
		
	public void addPlacesFilter(String searchText, Boolean foldersOnly) {
		if (foldersOnly) {
			addPlacesFilter(searchText, folderTypes);
		} else {
			addPlacesFilter(searchText, placeTypes);
		}	
	}
	
	public void addPlacesFilter(String searchText, List placesTypes) {
		checkCurrent();
	
		// this is not the same as in addFilter! 
		// the setText method is called on filterTerm and not on filterTermValueElem
		if (searchText.length()>0) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.EXTENDED_TITLE_FIELD);
			Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTermValueEle.setText(searchText.replaceFirst("\\*", "").trim());
			
			filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
			filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.EXTENDED_TITLE_FIELD);
			filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTermValueEle.setText(searchText.trim());
		}
		addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes,SearchFilterKeys.FilterEntityType, placesTypes);		
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
	
	protected Element newNestedFilterTermsBlock() {
		return newNestedFilterTermsBlock(this.joinAnd);
	}
	
	public Element newNestedFilterTermsBlock(boolean joinAnd) {
		checkCurrent();
		
		this.currentFilterTerms = newFilterTermsBlock(this.currentFilterTerms, joinAnd);
		return this.currentFilterTerms;
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
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.CREATORID_FIELD);
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
	
	public void addCreatorsByStringIds(List<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return;
		}
		
		checkCurrent();
		
		Element creatorsListParent = newFilterTermsBlock(currentFilterTerms, false);
		
		Iterator<String> it = ids.iterator();
		while (it.hasNext()) {
			String id = it.next();
			addCreatorById(creatorsListParent, id, "");
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
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + "#LogicalStartDate");
		filterTerm.addAttribute(SearchFilterKeys.FilterStartDate, date);
	}
	
	public void addTaskEndDate(String date) {
		checkCurrent();
		
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + "#LogicalEndDate");
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	
	public void addCreationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.CREATION_DAY_FIELD);
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
	}
	
	public void addModificationDate(String date) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeDate);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.MODIFICATION_DAY_FIELD);
		filterTerm.addAttribute(SearchFilterKeys.FilterEndDate, date);
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
	
	public void addLastActivityDateRange(String startDate, String endDate) {
		checkCurrent();
		addLastActivityDateRange(currentFilterTerms, startDate, endDate);
	}
	
	private void addLastActivityDateRange(Element parent, String startDate, String endDate) {
		addDateRange(parent, Constants.LASTACTIVITY_FIELD, startDate, endDate);
	}

	public void addCreationDateRange(String startDate, String endDate) {
		checkCurrent();
		addCreationDateRange(currentFilterTerms, startDate, endDate);
	}
	
	private void addCreationDateRange(Element parent, String startDate, String endDate) {
		addDateRange(parent, Constants.CREATION_DATE_FIELD, startDate, endDate);
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
			addCreationDateRange(creationPeriodsListParent, period.getStart(), period.getEnd());
		}
	}
	
	public void addModificationDateRange(Element parent, String startDate, String endDate) {
		addDateRange(parent, Constants.MODIFICATION_DATE_FIELD, startDate, endDate);
	}
	
	public void addModificationDateRange(String startDate, String endDate) {
		checkCurrent();
		addModificationDateRange(currentFilterTerms, startDate, endDate);
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
			addModificationDateRange(modificationPeriodsListParent, period.getStart(), period.getEnd());
		}
	}
	
	public void addTitle(String title) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.TITLE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		filterTermValueEle.setText(title.trim());
	}

	public void addText(String text, Boolean caseSensitive) {
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeSearchText);
		if (caseSensitive) filterTerm.addAttribute(SearchFilterKeys.FilterTypeCaseSensitive, "true");
		filterTerm.addText(text);
	}
	
	public void addPreDeletedOnly(Boolean preDeleted) {
		checkCurrent();
		if (preDeleted) {
			Element filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
			filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypePreDeletedOnly);
			filterTerm.addAttribute(SearchFilterKeys.FilterTypePreDeletedOnly, "true");
		}
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
			addWorkflow(workflowsListParent, workflow.id, workflow.steps!=null?(String[])workflow.steps.toArray(sample):null);
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
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.TITLE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		filterTermValueEle.setText(place.replaceFirst("\\*", "").trim());
		
		filterTerm = currentFilterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeElement);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, Constants.TITLE_FIELD);
		filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
		filterTermValueEle.setText(place.trim());
	
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
	 * Check if a filter is global.
	 * 
	 * @param searchFilter <code>null</code> if search filter has no name or given document is <code>null</code>
	 * @return
	 */
	public static boolean checkIfFilterGlobal(Document searchFilter) {
		if (searchFilter == null) {
			return false;
		}
		Element sfRoot = searchFilter.getRootElement();
		if (sfRoot == null) {
			return false;
		}
		Element filterGlobal = sfRoot.element(SearchFilterKeys.FilterGlobal);
		if (filterGlobal == null) {
			return false;
		}
		return true;
	}

	/**
	 * It's a current place in query call time.
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

	/**
	 * There's no filter or empty filter.
	 * @return
	 */
	public boolean isEmpty() {
		return (filter == null || 
					!filter.hasContent() ||
					!filter.getRootElement().hasContent());
	}

	public Element getCurrentFilterTerms() {
		return this.currentFilterTerms;
	}
	
	public void setCurrentFilterTerms(Element currentFilterTerms) {
		this.currentFilterTerms = currentFilterTerms;
	}
}
