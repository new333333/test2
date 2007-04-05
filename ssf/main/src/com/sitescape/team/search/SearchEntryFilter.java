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
import com.sitescape.team.web.util.FilterHelper;

/**
 * @author Renata Nowicka
 *
 */
public class SearchEntryFilter extends SearchFilter {

	// default join as 'OR' so joinAnd is false 
	private Boolean joinAnd = new Boolean(false);

	public SearchEntryFilter() {
		super();
	}

	public SearchEntryFilter(boolean joinAsAnd) {
		super();
		this.joinAnd = joinAsAnd;
	}

	public SearchEntryFilter(String rootElement) {
		super(rootElement);
	}

	public SearchEntryFilter(String rootElement, Boolean joinAsAnd) {
		super(rootElement);
		this.joinAnd = joinAsAnd;
	}
	
	protected void newCurrent() {
		currentFilterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
		currentFilterTerms.addAttribute(FilterHelper.FilterAnd, joinAnd.toString());
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
		checkCurrent();
		Element filterTerm = currentFilterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeCreatorById);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.CREATORID_FIELD);
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
	
		List searchTerms = new ArrayList(2);
		searchTerms.add(EntityIdentifier.EntityType.folder.name());
		searchTerms.add(EntityIdentifier.EntityType.workspace.name());
		addAndFilter(FilterHelper.FilterTypeEntityTypes,FilterHelper.FilterEntityType, searchTerms);		

		searchTerms = new ArrayList(1);
		searchTerms.add(BasicIndexUtils.DOC_TYPE_BINDER);
		addAndFilter(FilterHelper.FilterTypeDocTypes,FilterHelper.FilterDocType, searchTerms);	
	}
	
	public  void addEntryType(String defId, String name, String[] value) {
		// TODO implement it better, it should be possible look for entry type independent of value...
		if (name.equals("description")) name="_desc";
		addEntryTypeFilter(defId, name, value);
		
	}
	
}
