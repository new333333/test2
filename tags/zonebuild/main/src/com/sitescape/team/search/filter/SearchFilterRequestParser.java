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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.dom4j.Document;

import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.PortletRequestUtils;

public class SearchFilterRequestParser {
	
	private static String[] ALL_ITEM_TYPES = new String[] {"workspace", "folder", "user", "attachment", "entry", "reply"};
	
	private PortletRequest request;
	
	private DefinitionModule definitionModule;
	
	private boolean parseAdvancedOptions = false;
	
	public SearchFilterRequestParser(PortletRequest request, DefinitionModule definitionModule) {
		super();
		this.request = request;
		this.definitionModule = definitionModule;
	}
		
	public Document getSearchQuery() {
		Boolean joiner = PortletRequestUtils.getBooleanParameter(request, SearchFilterKeys.SearchJoiner, true);
		
		SearchFilter searchFilter = new SearchFilter(joiner);
		
		parseFilterName(request, searchFilter);
		parseFreeText(request, searchFilter);
		parsePlaces(request, searchFilter);
		
		parseAdvancedOptions = PortletRequestUtils.getBooleanParameter(request, SearchFilterKeys.SearchParseAdvancedForm, false);
		
		String[] numbers = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchNumbers, "").split(" ");
		String[] types = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchTypes, "").split(" ");
		
		parseItemTypes(request, searchFilter);
		parseAuthors(request, searchFilter, types, numbers);
		parseTags(request, searchFilter, types, numbers);
		parseWorkflows(request, searchFilter, types, numbers);
		parseEntries(request, searchFilter, types, numbers, definitionModule);
		parseCreationDates(request, searchFilter, types, numbers);
		parseModificationDates(request, searchFilter, types, numbers);
		parseLastActivity(request, searchFilter, types, numbers);

		fixEmptyQuery(searchFilter);
		
		return searchFilter.getFilter();
	}

	private void fixEmptyQuery(SearchFilter searchFilter) {
		if (searchFilter.isEmpty()) {
			searchFilter.addItemTypes(Arrays.asList(ALL_ITEM_TYPES));
		}
	}

	private void parseLastActivity(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		Integer maxDaysNumber = 0;
		
		if (!parseAdvancedOptions) {
			Integer daysNumber = PortletRequestUtils.getIntParameter(request, SearchFilterKeys.SearchDaysNumber.concat("_hidden"), 0);
			if (daysNumber > maxDaysNumber) {
				maxDaysNumber = daysNumber;
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeLastActivity)) {
					Integer daysNumber = PortletRequestUtils.getIntParameter(request, SearchFilterKeys.SearchDaysNumber.concat(numbers[i]), 0);
					if (daysNumber > maxDaysNumber) {
						maxDaysNumber = daysNumber;
					}
				}
			}
		}
		
		if (maxDaysNumber > 0) {
			searchFilter.addRelativeLastActivityDate(maxDaysNumber);
		}
	}

	private void parseModificationDates(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		List<SearchFilter.Period> modificationDates = new ArrayList();
		
		if (!parseAdvancedOptions) {
			int count = PortletRequestUtils.getIntParameter(request, SearchFilterToMapConverter.SearchBlockTypeModificationDate.concat("_").concat("length"), -1);
			for (int index = 0; index < count; index++) {
				String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeModificationDate.concat("_").concat(SearchFilterKeys.SearchStartDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeModificationDate.concat("_").concat(SearchFilterKeys.SearchEndDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
				if (period.getStart() != null || period.getEnd() != null) {
					modificationDates.add(period);
				}
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeModificationDate)) {
					String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchStartDate.concat(numbers[i]), "");
					String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchEndDate.concat(numbers[i]), "");
					SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
					if (period.getStart() != null || period.getEnd() != null) {
						modificationDates.add(period);
					}
				}
			}
		}
		
		searchFilter.addModificationDates(modificationDates);
	}

	private void parseCreationDates(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		List<SearchFilter.Period> creationDates = new ArrayList();
		
		if (!parseAdvancedOptions) {
			int count = PortletRequestUtils.getIntParameter(request, SearchFilterToMapConverter.SearchBlockTypeCreationDate.concat("_").concat("length"), -1);
			for (int index = 0; index < count; index++) {
				String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeCreationDate.concat("_").concat(SearchFilterKeys.SearchStartDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeCreationDate.concat("_").concat(SearchFilterKeys.SearchEndDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
				if (period.getStart() != null || period.getEnd() != null) {
					creationDates.add(period);
				}
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeCreationDate)) {
					String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchStartDate.concat(numbers[i]), "");
					String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchEndDate.concat(numbers[i]), "");
					SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
					if (period.getStart() != null || period.getEnd() != null) {
						creationDates.add(period);
					}
				}
			}
		}
		
		searchFilter.addCreationDates(creationDates);
	}

	private void parseEntries(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers, DefinitionModule definitionModule) {
		List<SearchFilter.Entry> entries = new ArrayList(); 
		
		if (!parseAdvancedOptions) {
			String[] entryTypeIds = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.FilterEntryDefIdField.concat("_hidden"));
			for (int i = 0; i < entryTypeIds.length; i++) {
				String entryTypeId = entryTypeIds[i];
				String entryFieldId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField.concat("_").concat(entryTypeId).concat("_hidden"), "");
				String[] value = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.FilterElementValueField.concat("_").concat(entryTypeId).concat("_hidden"));
				String value2 = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementValueField.concat("0_").concat(entryTypeId).concat("_hidden"), null);
				if (value != null && value2 != null) {
					String[] allValues = new String[value.length + 1];
					System.arraycopy(value, 0, allValues, 0, value.length);
					allValues[allValues.length - 1] = value2;
					value = allValues;
				}				
				String valueType = getEntryValueType(entryTypeId, entryFieldId);
				if (!entryTypeId.equals("")) {
					entries.add(new SearchFilter.Entry(entryTypeId, entryFieldId, value, valueType));
				}
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeEntry)) {
					String entryTypeId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterEntryDefIdField.concat(numbers[i]), "");
					
					String entryFieldId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField.concat(numbers[i]), SearchFilter.AllEntries);
					String[] value = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.FilterElementValueField.concat(numbers[i]));
					String value2 = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementValueField.concat(numbers[i]).concat("0"), null);
					if (value != null && value2 != null) {
						String[] allValues = new String[value.length + 1];
						System.arraycopy(value, 0, allValues, 0, value.length);
						allValues[allValues.length - 1] = value2;
						value = allValues;
					}
					String valueType = getEntryValueType(entryTypeId, entryFieldId);
					if (!entryTypeId.equals("")) {
						entries.add(new SearchFilter.Entry(entryTypeId, entryFieldId, value, valueType));
					}
				}
			}
		}
		
		searchFilter.addEntries(entries);
	}

	private String getEntryValueType(String entryTypeId, String entryFieldId) {
		String valueType = null;
		if (entryTypeId != null && !entryTypeId.equals("")) {
			Map fieldsMap = definitionModule.getEntryDefinitionElements(entryTypeId);
			if (fieldsMap != null && fieldsMap.get(entryFieldId) != null) {
				valueType = (String)((Map)fieldsMap.get(entryFieldId)).get(SearchFilterToMapConverter.EntryField.TypeField);
			}
		}
		return valueType;
	}

	private void parseWorkflows(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		List<SearchFilter.Workflow> workflows = new ArrayList();

		if (!parseAdvancedOptions) {
			String[] workflowIds =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowId.concat("_hidden"));
			for (int i = 0; i < workflowIds.length; i++) {
				String workflowId = workflowIds[i];
				String[] workflowSteps =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowStep.concat("_").concat(workflowId).concat("_step").concat("_hidden"));
				workflows.add(new SearchFilter.Workflow(workflowId, workflowSteps));
			}
		} else {
			Map<String, SearchFilter.Workflow> workflowsMap = new HashMap();
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeWorkflow)) {
					String workflowId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchWorkflowId.concat(numbers[i]), "");
					String[] workflowSteps =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowStep.concat(numbers[i]));
					if (!workflowId.equals("")) {
						if (workflowsMap.containsKey(workflowId)) {
							workflowsMap.get(workflowId).addSteps(workflowSteps);
						} else {
							workflowsMap.put(workflowId, new SearchFilter.Workflow(workflowId, workflowSteps));
						}
					}
				}
			}
			
			Iterator<SearchFilter.Workflow> it = workflowsMap.values().iterator();
			while (it.hasNext()) {
				workflows.add(it.next());
			}
		}

		searchFilter.addWorkflows(workflows);
	}

	private void parseAuthors(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		List<SearchFilter.Creator> creators = new ArrayList();
		
		String authors = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchAuthors, "");
		if (authors!=null && !authors.equals("")) {
			String[] authorsArr = authors.split(" ");
			for (int i = 0; i < authorsArr.length; i++) {
				creators.add(new SearchFilter.Creator(null, authorsArr[i]));
			}
		}
		
		if (!parseAdvancedOptions) {
			String[] authorTitles = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchAuthors.concat("_selected").concat("_hidden"));
			String[] authorIds = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchAuthors.concat("_hidden"));
			for (int i = 0; i < authorIds.length; i++) {
				String authorTitle = authorTitles[i];
				String authorId = authorIds[i];
				if (!authorId.equals("")) {
					creators.add(new SearchFilter.Creator(authorId, authorTitle));
				} else if (!authorTitle.equals("")) {
					creators.add(new SearchFilter.Creator(null, authorTitle));
				}
			}
		} else {
			for (int i=0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeAuthor)) {
					String authorTitle = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchAuthors.concat(numbers[i]).concat("_selected"), "");
					String authorId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchAuthors.concat(numbers[i]), "");
					if (!authorId.equals("")) {
						creators.add(new SearchFilter.Creator(authorId, authorTitle));
					} else if (!authorTitle.equals("")) {
						creators.add(new SearchFilter.Creator(null, authorTitle));
					}
				}
			}
		}
		
		searchFilter.addCreators(creators);
	}
	
	private void parseTags(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		List<SearchFilter.Tag> tagsList = new ArrayList();
		
		String tags = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchTags, "");
		
		if (tags != null && !tags.equals("")) {
			String[] tagsArr = tags.split(" ");
			for (int i = 0; i < tagsArr.length; i++) {
				tagsList.add(new SearchFilter.Tag(SearchFilter.Tag.Type.BOTH, tagsArr[i]));
			}
		}
		
		
		if (!parseAdvancedOptions) {
			Iterator personalTagIt = Arrays.asList(PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchPersonalTags.concat("_hidden"))).iterator();
			while (personalTagIt.hasNext()) {
				tagsList.add(new SearchFilter.Tag(SearchFilter.Tag.Type.PERSONAL, (String)personalTagIt.next()));
			}
			Iterator communityTagIt = Arrays.asList(PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchCommunityTags.concat("_hidden"))).iterator();
			while (communityTagIt.hasNext()) {
				tagsList.add(new SearchFilter.Tag(SearchFilter.Tag.Type.COMMUNITY, (String)communityTagIt.next()));
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeTag)) {
					String personalTag = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchPersonalTags.concat(numbers[i]), "");
					String communityTag = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchCommunityTags.concat(numbers[i]), "");
					
					if (personalTag.equals("")) {
						personalTag = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchPersonalTags.concat(numbers[i]).concat("_selected"), "");
					}
					if (communityTag.equals("")) {
						communityTag = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchCommunityTags.concat(numbers[i]).concat("_selected"), "");
					}
					
					if (!personalTag.equals("")) {
						tagsList.add(new SearchFilter.Tag(SearchFilter.Tag.Type.PERSONAL, personalTag));
					}
					if (!communityTag.equals("")) {
						tagsList.add(new SearchFilter.Tag(SearchFilter.Tag.Type.COMMUNITY, communityTag));
					}
				}
			}
		}
		
		searchFilter.addTags(tagsList);
	}

	private void parseFreeText(PortletRequest request, SearchFilter searchFilter) {
		String searchText = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchText, "");
		if (!searchText.equals("")) {
			searchFilter.addText(searchText);
		}
	}

	private void parseFilterName(PortletRequest request, SearchFilter searchFilter) {
		String filterName = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterNameField, "");
		if (filterName != null && !filterName.equals("")) {
			searchFilter.addFilterName(filterName);
		}
	}

	private void parseItemTypes(PortletRequest request, SearchFilter searchFilter) {
		List itemTypes = Arrays.asList(PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchItemType));
		searchFilter.addItemTypes(itemTypes);
	}

	private void parsePlaces(PortletRequest request, SearchFilter searchFilter) {
		String foldersType = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchFoldersType, "selected");
		boolean searchSubfolders = PortletRequestUtils.getBooleanParameter(request, SearchFilterKeys.SearchSubfolders, false);
		
		if (foldersType.equals("selected")) {
			List folderIds = new ArrayList();
			Map formData = request.getParameterMap();
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry) itFormData.next();
				String key = (String)me.getKey();
				String[] values = (String[])me.getValue();
				if (key.equals(WebKeys.URL_ID_CHOICES) && values != null) {
					for (int i = 0; i < values.length; i++) {
						String[] valueSplited = values[i].split("\\s");
						for (int j = 0; j < valueSplited.length; j++) {
							if (valueSplited[j] != null && valueSplited[j].indexOf(SearchFilterKeys.SearchFolders) > -1) {
								String folderId = valueSplited[j].replaceFirst(SearchFilterKeys.SearchFolders, "");
								folderIds.add(folderId);
							}
						}
					}
				}
			}

			if (!searchSubfolders) {
				searchFilter.addFolderIds(folderIds);
			} else {
				searchFilter.addAncestryIds(folderIds);
			}
		} else if (foldersType.equals("dashboard")) {
			searchFilter.addRelativePlace(searchSubfolders);
		}
	}
	
}
