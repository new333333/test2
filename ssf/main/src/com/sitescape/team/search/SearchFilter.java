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
package com.sitescape.team.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.web.util.FilterHelper;

public class SearchFilter {

	protected Document filter;
	protected Element sfRoot;
	protected Element currentFilterTerms = null;

	

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
		if (!defId.equals("") && name.equals("_all_entries")) {
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
		
		List searchTerms = new ArrayList(2);
		searchTerms.add(EntityIdentifier.EntityType.folder.name());
		searchTerms.add(EntityIdentifier.EntityType.workspace.name());
		addAndFilter(FilterHelper.FilterTypeEntityTypes,FilterHelper.FilterEntityType, searchTerms);		

		searchTerms = new ArrayList(1);
		searchTerms.add(BasicIndexUtils.DOC_TYPE_BINDER);
		addAndFilter(FilterHelper.FilterTypeDocTypes,FilterHelper.FilterDocType, searchTerms);		
}
	
	
	protected void newCurrent() {
		currentFilterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
	}
	protected void checkCurrent() {
		if (currentFilterTerms == null) {
			newCurrent();
		}
	}
	public SearchFilter() {
		this(FilterHelper.FilterRootName);
	}

	public SearchFilter(String rootElement) {
		super();
		filter = DocumentHelper.createDocument();
		sfRoot = filter.addElement(rootElement);
	}

	// TODO remove after test
	public Document getFilter() {
		return filter;
	}
	
}
