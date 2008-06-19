/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.search.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.search.Constants;

public class SearchFilterToMapConverter {
	
	private Log logger = LogFactory.getLog(this.getClass());

	static final String SearchBlockTypeCreationDate = "creation_date";
	static final String SearchBlockTypeModificationDate = "modification_date";
	static final String SearchBlockTypeWorkflow = "workflow";
	static final String SearchBlockTypeEntry = "entry";
	static final String SearchBlockTypeAuthor = "creator_by_id";
	static final String SearchBlockTypeTag = "tag";
	static final String SearchBlockTypeLastActivity = "last_activity";
	static final String SearchBlockTypeItemTypes = "item_types";
	
	static final String SearchBlockType = "type";
	static final String SearchBlockTypeRelative = "relative";	
	static final String SearchStartDate = "startDate";
	static final String SearchEndDate = "endDate";
	static final String SearchStartDateNotFormated = "startDateNotFormated";
	static final String SearchEndDateNotFormated = "endDateNotFormated";
	
	static final String SearchEntryType="entryType";
	static final String SearchEntryElement="entryElement";
	static final String SearchEntryValues="entryValues";
	static final String SearchEntryValueType="valueType";
	static final String SearchEntryValuesNotFormatted="entryValuesNotFormatted";
	static final String SearchAuthor="authorId";
	static final String SearchAuthorTitle="authorTitle";
	static final String SearchDaysNumber="daysNumber";
	static final String SearchTag="tag";
	static final String SearchPersonalTag="personalTag";
	static final String SearchCommunityTag="communityTag";
		
	private Document searchQuery;
	private AllModulesInjected bs;
	
	public SearchFilterToMapConverter(AllModulesInjected bs, Document query) {
		super();
		this.searchQuery = query;
		this.bs = bs;
	}
	
	/**
	 * Returns map with three keys:
	 * <code>WebKeys.SEARCH_FILTER_MAP</code>, <code>WebKeys.WORKFLOW_DEFINTION_MAP</code>, <code>WebKeys.ENTRY_DEFINTION_MAP</code>.  
	 * It means methods chenge query to map and puts some additional data required to show advanced search query form.
	 * 
	 * @return
	 */
	public Map convertAndPrepareFormData() {
		Map result = new HashMap();
		
		Map convertedQuery = new HashMap();
		if (searchQuery != null) { 
	    	Element rootElement = searchQuery.getRootElement();
	    	List liFilterTerms = rootElement.selectNodes(SearchFilterKeys.FilterTerms);

	    	logger.debug("Query: "+searchQuery.asXML());

	    	convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, new HashMap());
	    	
	    	Iterator filterTermsIt = liFilterTerms.iterator();
	    	while (filterTermsIt.hasNext()) {
	    		Element filterTerms = (Element) filterTermsIt.next();
	    		convertFilterTerms(filterTerms, convertedQuery);
	    	}
		}
		
		
		result.put(WebKeys.SEARCH_FILTER_MAP, convertedQuery);
		result.putAll(prepareAdditionalFiltersData(convertedQuery));
		
		return result;
	}
	
	private void convertFilterTerms(Element filterTerms, Map convertedQuery) {
		// TODO: now is not in use, in future parse only on first level
		if (filterTerms.attributeValue(SearchFilterKeys.FilterAnd, "").equals(Boolean.TRUE.toString())) {
			convertedQuery.put(SearchFilterKeys.SearchJoiner, Boolean.TRUE.toString());
		} else {
			convertedQuery.put(SearchFilterKeys.SearchJoiner, Boolean.FALSE.toString());
		}
		
    	Iterator filterTermsIt = filterTerms.selectNodes(SearchFilterKeys.FilterTerms).iterator();
    	while (filterTermsIt.hasNext()) {
    		Element filterTermsChild = (Element) filterTermsIt.next();
    		convertFilterTerms(filterTermsChild, convertedQuery);
    	}

		Iterator filterTermsTermIt = filterTerms.selectNodes("./" + SearchFilterKeys.FilterTerm).iterator();
		while (filterTermsTermIt.hasNext()) {
    		Element filterTerm = (Element) filterTermsTermIt.next();
    		String filterType = filterTerm.attributeValue(SearchFilterKeys.FilterType, "");
    		if (filterType.equals(SearchFilterKeys.FilterTypeSearchText)) {
    			String searchedText = (String)convertedQuery.get(SearchFilterKeys.SearchText);
    			if (searchedText == null || searchedText.equals("")) {
    				searchedText = filterTerm.getText(); 
    			} else {
    				searchedText = searchedText.concat(" "+filterTerm.getText()); 
    			}
    			convertedQuery.put(SearchFilterKeys.SearchText, searchedText);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeCreatorByName)) {
    			String searchedAuthors = (String)convertedQuery.get(SearchFilterKeys.SearchAuthors);
    			if (searchedAuthors == null || searchedAuthors.equals("")) {
    				searchedAuthors = filterTerm.getText(); 
    			} else {
    				searchedAuthors = searchedAuthors.concat(" "+filterTerm.getText());
    			}
    			convertedQuery.put(SearchFilterKeys.SearchAuthors, searchedAuthors);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeFoldersList)) {
    			List searchFolders = (List)convertedQuery.get(SearchFilterKeys.SearchFolders);
    			if (searchFolders == null) {
    				searchFolders = new ArrayList();
    			}
    			searchFolders.addAll(createFolderIdsList(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchFolders, searchFolders);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeAncestriesList)) {
    			List searchFolders = (List)convertedQuery.get(SearchFilterKeys.SearchFolders);
    			if (searchFolders == null) {
    				searchFolders = new ArrayList();
    			}
    			searchFolders.addAll(createFolderIdsList(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchFolders, searchFolders);
    			convertedQuery.put(SearchFilterKeys.SearchSubfolders, Boolean.TRUE);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeTags)) {
    			String searchedTags = (String)convertedQuery.get(SearchFilterKeys.SearchTags);
    			if (searchedTags == null || searchedTags.equals("")) {
    				searchedTags = filterTerm.getText(); 
    			} else {
    				searchedTags = searchedTags.concat(" "+filterTerm.getText());
    			}
    			convertedQuery.put(SearchFilterKeys.SearchTags, searchedTags);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEntryDefinition)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeEntry) == null) {
    				blocks.put(SearchBlockTypeEntry, new ArrayList());
    			}
    			((List)blocks.get(SearchBlockTypeEntry)).add(createEntryBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEvent)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeEntry) == null) blocks.put(SearchBlockTypeEntry, new ArrayList());
    			((List)blocks.get(SearchBlockTypeEntry)).add(createEventBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks); 			
    		} else if ( filterType.equals(SearchFilterKeys.FilterTypeCreatorById)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeAuthor) == null) blocks.put(SearchBlockTypeAuthor, new ArrayList());
    			((List)blocks.get(SearchBlockTypeAuthor)).add(createCreatorBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeWorkflow)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeWorkflow) == null) blocks.put(SearchBlockTypeWorkflow, new ArrayList());
    			((List)blocks.get(SearchBlockTypeWorkflow)).add(createWorkflowBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeCommunityTagSearch)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeTag) == null) blocks.put(SearchBlockTypeTag, new ArrayList());
    			((List)blocks.get(SearchBlockTypeTag)).add(createTagBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypePersonalTagSearch)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeTag) == null) blocks.put(SearchBlockTypeTag, new ArrayList());
    			((List)blocks.get(SearchBlockTypeTag)).add(createTagBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
	    	} else if (filterType.equals(SearchFilterKeys.FilterTypeDate)) {
	    		Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			Map dateBlock = createDateBlock(filterTerm);
    			if (SearchBlockTypeCreationDate.equals(dateBlock.get(SearchBlockType))) {
    				if (blocks.get(SearchBlockTypeCreationDate) == null) blocks.put(SearchBlockTypeCreationDate, new ArrayList());
    				((List)blocks.get(SearchBlockTypeCreationDate)).add(dateBlock);
    			} else {
    				if (blocks.get(SearchBlockTypeModificationDate) == null) blocks.put(SearchBlockTypeModificationDate, new ArrayList());
    				((List)blocks.get(SearchBlockTypeModificationDate)).add(dateBlock);	    	    				
    			}
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeRelative)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			String filterRelativeType = filterTerm.attributeValue(SearchFilterKeys.FilterRelativeType, "");
    			if (filterRelativeType.equals(SearchFilterKeys.FilterTypeDate)) {
    				if (blocks.get(SearchBlockTypeLastActivity) == null) blocks.put(SearchBlockTypeLastActivity, new ArrayList());
    				((List)blocks.get(SearchBlockTypeLastActivity)).add(createLastActivityBlock(filterTerm));
    			} else if (filterRelativeType.equals(SearchFilterKeys.FilterTypeCreatorById)) {
	    			if (blocks.get(SearchBlockTypeAuthor) == null) blocks.put(SearchBlockTypeAuthor, new ArrayList());
	    			((List)blocks.get(SearchBlockTypeAuthor)).add(createCreatorBlock(filterTerm));
    			} else if (filterRelativeType.equals(SearchFilterKeys.FilterTypePlace)) {
    				convertedQuery.put(SearchFilterKeys.SearchCurrentFolder, true);
    				if (filterTerm.getTextTrim().equals(Boolean.TRUE.toString())) {
    					convertedQuery.put(SearchFilterKeys.SearchSubfolders, Boolean.TRUE);
    				}
    			}
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		} else if (filterType.equals(SearchFilterKeys.FilterTypeItemTypes)) {
    			Map blocks = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
    			if (blocks.get(SearchBlockTypeItemTypes) == null) {
    				blocks.put(SearchBlockTypeItemTypes, new HashMap());
    			}
    			((Map)blocks.get(SearchBlockTypeItemTypes)).putAll(createItemTypesBlock(filterTerm));
    			convertedQuery.put(SearchFilterKeys.SearchAdditionalFilters, blocks);
    		}
    	}
	}

	private List createFolderIdsList(Element filterTerm) {
		List folderIds = new ArrayList();
		
		if (filterTerm.selectNodes(SearchFilterKeys.FilterFolderId).size() > 0) {
			Iterator itTermStates = filterTerm.selectNodes(SearchFilterKeys.FilterFolderId).iterator();			
			while (itTermStates.hasNext()) {
				String folderId = ((Element) itTermStates.next()).getText();
				if (!folderId.equals("")) {
					folderIds.add(folderId);
				}
			}
		}
		return folderIds;
	}

	private Map createEntryBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));
		String entryTypeId = filterTerm.attributeValue(SearchFilterKeys.FilterEntryDefId, "");
		if (entryTypeId == null || entryTypeId.equals("")) {
			return new HashMap();
		}
		block.put(SearchEntryType, entryTypeId);
		String entryFieldId = filterTerm.attributeValue(SearchFilterKeys.FilterElementName, "");
		if (entryFieldId == null || entryFieldId.equals("")) {
			return block;
		}
		if (entryFieldId.equals("_desc")) {
			entryFieldId = "description";
		}		
		
		block.put(SearchEntryElement, entryFieldId);
		List values = getElementValues(filterTerm);
		if (values.size() > 0) {
			Map fieldsMap = bs.getDefinitionModule().getEntryDefinitionElements(entryTypeId);
			if (fieldsMap.containsKey(entryFieldId)) {
				String valueType = (String)((Map)fieldsMap.get(entryFieldId)).get(EntryField.TypeField);
				block.put(SearchEntryValueType, valueType);
				String value = (String)values.get(0);
				
				Object parsedValue = value;
				String formattedValue = value;
				if (valueType.equals("date")) {
					parsedValue = parseDate_from_yyyy_MM_dd(value);
					formattedValue = value;
				} else if (valueType.equals("date_time")) {
					parsedValue = parseDateTime_from_yyyy_MM_dd_HH_mm(value);
					formattedValue = value;					
				} else if (valueType.equals("checkbox")) {
					boolean valueObj = Boolean.parseBoolean(value);
					if (valueObj) {
						formattedValue = NLT.get("searchForm.checkbox.selected");
					} else {
						formattedValue = NLT.get("searchForm.checkbox.unselected");
					}
				} else if (valueType.equals("selectbox") || valueType.equals("radio")) {
					Map selectBoxDefinedValues = (Map)((Map)fieldsMap.get(entryFieldId)).get(EntryField.ValuesField);
					formattedValue = (String)selectBoxDefinedValues.get(value);
				} else if (valueType.equals("user_list")) {
					if (SearchFilterKeys.CurrentUserId.equals(value.toString())) {
						formattedValue = NLT.get("searchForm.currentUserTitle");
					} else {
						Iterator users = bs.getProfileModule().getUsers(Collections.singleton(Long.parseLong(value))).iterator();
						if (users.hasNext()) {
							formattedValue = ((User)users.next()).getTitle();
						}
					}
				} else if (valueType.equals("group_list")) {
					Iterator groups = bs.getProfileModule().getGroups(Collections.singleton(Long.parseLong(value))).iterator();
					if (groups.hasNext()) {
						formattedValue = ((Group)groups.next()).getTitle();
					}
				} else if (valueType.equals("team_list")) {
					try {
						Binder team = bs.getBinderModule().getBinder(Long.parseLong(value));
						if (team != null) {
							formattedValue = team.getTitle();
						}
					} catch (NoBinderByTheIdException e) {
						formattedValue = "[" + value + " - " + NLT.get("binder.deleted") + "]";
					}
				}
				
				block.put(SearchEntryValues, formattedValue);
				block.put(SearchEntryValuesNotFormatted, parsedValue);
			}
		} 
		return block;
	}

	private Map createEventBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));
		String entryTypeId = filterTerm.attributeValue(SearchFilterKeys.FilterEntryDefId, "");
		block.put(SearchEntryType, entryTypeId);
		String entryFieldId = filterTerm.attributeValue(SearchFilterKeys.FilterElementName, "");
		block.put(SearchEntryElement, entryFieldId);
		List values = new ArrayList();
		
		List eventDatesNodes = filterTerm.selectNodes(SearchFilterKeys.FilterEventDate);
		Iterator eventDatesNodesIt = eventDatesNodes.iterator(); 
		while (eventDatesNodesIt.hasNext()) {
			values.add(((Element) eventDatesNodesIt.next()).getText());
		}
		
		if (values.size() > 0) {
			Map fieldsMap = bs.getDefinitionModule().getEntryDefinitionElements(entryTypeId);
			if (fieldsMap.containsKey(entryFieldId)) {
				String valueType = (String)((Map)fieldsMap.get(entryFieldId)).get(EntryField.TypeField);
				block.put(SearchEntryValueType, valueType);
				String value = (String)values.get(0);
				
				Object parsedValue = value;
				String formattedValue = value;
				if (valueType.equals("event")) {
					parsedValue = parseDate_from_yyyy_MM_dd(value);
					formattedValue = value;
				}
				
				block.put(SearchEntryValues, formattedValue);
				block.put(SearchEntryValuesNotFormatted, parsedValue);
			}
		} 
		return block;
	}

	private Map createCreatorBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));
		block.put(SearchAuthorTitle, filterTerm.attributeValue(SearchFilterKeys.FilterCreatorTitle, ""));
		block.put(SearchAuthor, getElementValues(filterTerm).get(0));
		return block;
	}

	private Map createLastActivityBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));
		block.put(SearchBlockTypeRelative, filterTerm.attributeValue(SearchFilterKeys.FilterTypeRelative, ""));
		block.put(SearchDaysNumber, filterTerm.getText());
		return block;
	}

	private Map createWorkflowBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));	
		block.put(SearchFilterKeys.SearchWorkflowId, filterTerm.attributeValue(SearchFilterKeys.FilterWorkflowDefId, ""));
		
		List steps = filterTerm.selectNodes(SearchFilterKeys.FilterWorkflowStateName);
		List modelSteps = new ArrayList();
		Iterator it = steps.iterator();
		while (it.hasNext()) {
			modelSteps.add(((Element)it.next()).getText());
		}
		block.put(SearchFilterKeys.FilterWorkflowStateName, modelSteps);
		return block;
	}
	private Map createTagBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));
		block.put(SearchTag, filterTerm.getTextTrim());
		return block;
	}
	
	private Map createDateBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(SearchFilterKeys.FilterType, ""));
		if (Constants.CREATION_DATE_FIELD.equalsIgnoreCase(filterTerm.attributeValue(SearchFilterKeys.FilterElementName))) {
			block.put(SearchBlockType, SearchBlockTypeCreationDate);
		} else {
			block.put(SearchBlockType, SearchBlockTypeModificationDate);
		}
		
		String startDate = filterTerm.attributeValue(SearchStartDate, "");
		String endDate = filterTerm.attributeValue(SearchEndDate, "");
		
		Date startDateParsed = parseDate_from_yyyy_MM_dd(startDate);
		Date endDateParsed = parseDate_from_yyyy_MM_dd(endDate);
		
		block.put(SearchStartDate, startDate);
		block.put(SearchEndDate, endDate);
		block.put(SearchStartDateNotFormated, startDateParsed);
		block.put(SearchEndDateNotFormated, endDateParsed);
		return block;
	}
	
	private Map createItemTypesBlock(Element filterTerm) {
		Map block = new HashMap();
		
		Iterator itTermValues = filterTerm.selectNodes(SearchFilterKeys.FilterItemType).iterator();
		while (itTermValues.hasNext()) {
			String itemType = ((Element) itTermValues.next()).getText();
			block.put(itemType, true);
		}
		return block;
	}
	
	private List getElementValues(Element filterTerm) {
		List values = filterTerm.selectNodes(SearchFilterKeys.FilterElementValue);
		List modelValues = new ArrayList();
		Iterator it = values.iterator(); 
		while (it.hasNext()) {
			modelValues.add(((Element) it.next()).getText());
		}
		return modelValues;
	}	
	
	private Date parseDate_from_yyyy_MM_dd(String s) {
		if (s == null || "".equals(s)) {
			return null;
		}
		User user = RequestContextHolder.getRequestContext().getUser();
		
		SimpleDateFormat inputFormater = new SimpleDateFormat("yyyy-MM-dd");
		if (!s.contains("-")) inputFormater = new SimpleDateFormat("yyyyMMdd");
		inputFormater.setTimeZone(user.getTimeZone());
		
		Date result = null;
		
		try { 
			result = inputFormater.parse(s);
		} catch (ParseException e) {
			logger.info("Date [" + s + "] in search mask is in wrong format");
		}
		
		return result;
	}
	
	private Date parseDateTime_from_yyyy_MM_dd_HH_mm(String s) {
		if (s == null || "".equals(s)) {
			return null;
		}
		User user = RequestContextHolder.getRequestContext().getUser();
		
		SimpleDateFormat inputFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		inputFormater.setTimeZone(user.getTimeZone());
		
		Date result = null;
		
		try { 
			result = inputFormater.parse(s);
		} catch (ParseException e) {
			result = parseDate_from_yyyy_MM_dd(s);
		}
		
		return result;
	}	

	private Map prepareAdditionalFiltersData(Map convertedQuery) {
		
		Map result = new HashMap();
		
		if (convertedQuery.get(SearchFilterKeys.SearchFolders) != null) {
			Iterator<String> it = ((List)convertedQuery.get(SearchFilterKeys.SearchFolders)).iterator();
			List<Long> idLs = new ArrayList();
			while (it.hasNext()) {
				String ids = it.next();
				try {
					idLs.add(Long.parseLong(ids.trim()));
				} catch (NumberFormatException e) {}
			}
			result.put(WebKeys.FOLDER_LIST, bs.getBinderModule().getBinders(idLs));
		}
		
		Map additionalOptions = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
		if (additionalOptions != null) {
			prepareTags(convertedQuery);
			Collection preparedWorkflows = prepareWorkflows(convertedQuery);
			if (preparedWorkflows != null) {
				result.put(WebKeys.WORKFLOW_DEFINITION_MAP, preparedWorkflows);
			}
			Collection preparedEntries = prepareEntries(convertedQuery);
			if (preparedEntries != null) {
				result.put(WebKeys.ENTRY_DEFINITION_MAP, preparedEntries);
			}
		}
		
		return result;
	}

	private Collection prepareEntries(Map convertedQuery) {
		List entryTypes = bs.getDefinitionModule().getDefinitions(Definition.FOLDER_ENTRY);
		Map additionalOptions = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);		
		List entriesFromSearch = (List) additionalOptions.get(SearchBlockTypeEntry);
		if (entryTypes == null || entriesFromSearch == null) {
			return null;
		}
		Iterator entriesIt = entryTypes.iterator();
		Map entriesMap = new HashMap(); 
		while (entriesIt.hasNext()) {
			Entry entry = new Entry((Definition) entriesIt.next(), null);
			entriesMap.put(entry.getId(), entry);
		}
		Iterator entriesFromSearchIt = entriesFromSearch.iterator();
		while (entriesFromSearchIt.hasNext()) {
			Map entryMap = (Map) entriesFromSearchIt.next();
			String entryType = (String) entryMap.get(SearchEntryType);
			String fieldName = (String) entryMap.get(SearchEntryElement);
			
			if (entryType != null && !entryType.equals("")) {
				Map fieldsMap = bs.getDefinitionModule().getEntryDefinitionElements(entryType);
				if (fieldName != null && fieldsMap.get(fieldName) != null) {
					EntryField entryField = new EntryField(fieldName, (String)((Map)fieldsMap.get(fieldName)).get(EntryField.TitleField), (String)((Map)fieldsMap.get(fieldName)).get(EntryField.TypeField));
					((Entry)entriesMap.get(entryType)).addField(entryField);
				}	
			}
		}
		return entriesMap.values();
	}
	
	private  void prepareTags(Map convertedQuery) {
		Map additionalOptions = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);
		List tagsFromFilter = (List) additionalOptions.get(SearchBlockTypeTag);
		if (tagsFromFilter != null) {
			Iterator it = tagsFromFilter.iterator();
			List personalTags = new ArrayList();
			List communityTags = new ArrayList();
			while (it.hasNext()) {
				Map tag = (Map)it.next();
				if (tag.get(SearchBlockType).equals(SearchFilterKeys.FilterTypePersonalTagSearch)) {
					personalTags.add((String)tag.get(SearchTag));
				} else {
					communityTags.add((String)tag.get(SearchTag));
				}
			}
			List tagsDuets = new ArrayList();
			int maxSize = communityTags.size();
			if (maxSize < personalTags.size()) maxSize = personalTags.size();
			for (int i=0; i<maxSize; i++){
				Map tagsDuet = new HashMap();
				tagsDuet.put(SearchBlockType, SearchBlockTypeTag);
				if (i < personalTags.size()) tagsDuet.put(SearchPersonalTag, personalTags.get(i));
				else tagsDuet.put(SearchPersonalTag, "");
				if (i < communityTags.size()) tagsDuet.put(SearchCommunityTag, communityTags.get(i));
				else tagsDuet.put(SearchCommunityTag, "");
				tagsDuets.add(tagsDuet);
			}
			additionalOptions.put(SearchBlockTypeTag, tagsDuets);
		}
	}
	private Collection prepareWorkflows(Map convertedQuery) {
		List workflows = bs.getDefinitionModule().getDefinitions(Definition.WORKFLOW);
		Map additionalOptions = (Map)convertedQuery.get(SearchFilterKeys.SearchAdditionalFilters);		
		List wfFromSearch = (List) additionalOptions.get(SearchBlockTypeWorkflow);
		if (workflows == null || wfFromSearch == null) {
			return null;
		}
		Iterator wfIt = workflows.iterator();
		Map wfMap = new HashMap();
		while (wfIt.hasNext()) {
			Workflow workflow = new Workflow((Definition)wfIt.next(), null); 
			wfMap.put(workflow.getId(), workflow);
		}
		Iterator it = wfFromSearch.iterator();
		while (it.hasNext()) {
			Map wfFilter = (Map) it.next();
			String wfId = (String)wfFilter.get(SearchFilterKeys.SearchWorkflowId);
			Map steps = bs.getDefinitionModule().getWorkflowDefinitionStates(wfId);
			if (steps.isEmpty()) continue;
			List selectedStepsNames = (List) wfFilter.get(SearchFilterKeys.FilterWorkflowStateName);
			Iterator filterSteps = selectedStepsNames.iterator();
			while (filterSteps.hasNext()) {
				String stepName = (String)filterSteps.next();
				Map stepMap = (Map)steps.get(stepName);
				if (stepMap == null) continue;
				WorkflowStep wfStep = new WorkflowStep(stepName, (String)stepMap.get(WorkflowStep.TitleField));
				((Workflow)wfMap.get(wfId)).addStep(wfStep);
			}
		}
		
		return wfMap.values();
	}
	
	public static class Workflow {
		String id;
		String title;
		List<WorkflowStep> steps;
		
		public Workflow(String id, String title, List steps) {
			this.id = id;
			this.title = title;
			this.steps = steps;
		}
		public Workflow(Definition definition, List steps) {
			this.id = definition.getId();
			this.title = definition.getTitle();
			this.steps = steps;
		}
		public String getId() {
			return this.id;
		}
		public String getTitle() {
			return this.title;
		}
		public List getSteps() {
			return steps;
		}
		public void setSteps(List steps) {
			this.steps = steps;
		}
		public void addStep(WorkflowStep step){
			if (steps == null) {
				steps = new ArrayList();
			}
			steps.add(step);
		}
	}
	
	public static class WorkflowStep {
		String name;
		String title;
		public WorkflowStep(String name, String title) {
			this.name = name;
			this.title = title;
		}
		
		public static String TitleField = "caption";
		
		public String getName() {
			return this.name;
		}
		public String getTitle() {
			return this.title;
		}		
	}

	public static class Entry {
		String id;
		String title;
		List<EntryField> fields;
		
		public Entry(String id, String title, List fields) {
			this.id = id;
			this.title = title;
			this.fields = fields;
		}
		public Entry(Definition definition, List fields) {
			this.id = definition.getId();
			this.title = NLT.getDef(definition.getTitle());
			this.fields = fields;
		}
		public String getId() {
			return this.id;
		}
		public String getTitle() {
			return this.title;
		}
		public List getFields() {
			return this.fields;
		}
		public void addField(EntryField field){
			if (this.fields == null) {
				fields = new ArrayList();
			}
			this.fields.add(field);
		}		
	}
	public static class EntryField {
		String name;
		String title;
		String type;
		public static String TitleField = "caption";
		public static String TypeField = "type";
		public static String ValuesField = "values";
		
		public EntryField(String name, String title, String type) {
			this.name = name;
			this.title = title;
			this.type = type;
		}
		public String getName() {
			return this.name;
		}
		public String getTitle() {
			return this.title;
		}
		public String getType() {
			return this.type;
		}
	}

	
}
