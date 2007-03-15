package com.sitescape.team.search;

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
 * @author renata
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
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntry);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.CREATORID_FIELD);
		Element newTerm = filterTerm.addElement(FilterHelper.FilterElementValue);
		newTerm.setText(userId);
	}
	
	public void addCreationDateBefore(String date) {
		// TODO implement
	}
	public void addCreationDateAfter(String date) {
		// TODO implement
	}
	public void addModificationDateBefore(String date) {
		// TODO implement
	}
	public void addModificationDateAfter(String date) {
		// TODO implement
	}
	
	public void addCreationDateBefore(Date date) {
		// TODO implement
	}
	public void addCreationDateAfter(Date date) {
		// TODO implement
	}
	public void addModificationDateBefore(Date date) {
		// TODO implement
	}
	public void addModificationDateAfter(Date date) {
		// TODO implement
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
	
	public  void addEntryType() {
		// TODO implement
	}
	
}
