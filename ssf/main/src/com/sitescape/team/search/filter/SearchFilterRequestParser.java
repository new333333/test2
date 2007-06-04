package com.sitescape.team.search.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.dom4j.Document;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.web.util.PortletRequestUtils;

public class SearchFilterRequestParser {
	
	private PortletRequest request;
	
	private DefinitionModule definitionModule;
	
	private boolean parseMoreOptions = false;
	
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
		
		parseMoreOptions = PortletRequestUtils.getBooleanParameter(request, SearchFilterKeys.SearchParseAdvancedForm, false);
		
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

		return searchFilter.getFilter();
	}

	private void parseLastActivity(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		Integer maxDaysNumber = 0;
		
		if (!parseMoreOptions) {
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
		
		if (!parseMoreOptions) {
			int count = PortletRequestUtils.getIntParameter(request, SearchFilterToMapConverter.SearchBlockTypeModificationDate.concat("_").concat("length"), -1);
			for (int index = 0; index < count; index++) {
				String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeModificationDate.concat("_").concat(SearchFilterKeys.SearchStartDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeModificationDate.concat("_").concat(SearchFilterKeys.SearchEndDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
				if (period.start != null || period.end != null) {
					modificationDates.add(period);
				}
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeModificationDate)) {
					String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchStartDate.concat(numbers[i]), "");
					String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchEndDate.concat(numbers[i]), "");
					SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
					if (period.start != null || period.end != null) {
						modificationDates.add(period);
					}
				}
			}
		}
		
		searchFilter.addModificationDates(modificationDates);
	}

	private void parseCreationDates(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers) {
		List<SearchFilter.Period> creationDates = new ArrayList();
		
		if (!parseMoreOptions) {
			int count = PortletRequestUtils.getIntParameter(request, SearchFilterToMapConverter.SearchBlockTypeCreationDate.concat("_").concat("length"), -1);
			for (int index = 0; index < count; index++) {
				String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeCreationDate.concat("_").concat(SearchFilterKeys.SearchStartDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterToMapConverter.SearchBlockTypeCreationDate.concat("_").concat(SearchFilterKeys.SearchEndDate).concat("_").concat(Integer.toString(index)).concat("_hidden"), "");
				SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
				if (period.start != null || period.end != null) {
					creationDates.add(period);
				}
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeCreationDate)) {
					String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchStartDate.concat(numbers[i]), "");
					String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchEndDate.concat(numbers[i]), "");
					SearchFilter.Period period = SearchFilter.Period.parseDatesToPeriod(startDate, endDate);
					if (period.start != null || period.end != null) {
						creationDates.add(period);
					}
				}
			}
		}
		
		searchFilter.addCreationDates(creationDates);
	}

	private void parseEntries(PortletRequest request, SearchFilter searchFilter, String[] types, String[] numbers, DefinitionModule definitionModule) {
		List<SearchFilter.Entry> entries = new ArrayList(); 
		
		if (!parseMoreOptions) {
			String[] entryTypeIds = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.FilterEntryDefIdField.concat("_hidden"));
			for (int i = 0; i < entryTypeIds.length; i++) {
				String entryTypeId = entryTypeIds[i];
				String entryFieldId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField.concat("_").concat(entryTypeId).concat("_hidden"), "");
				String[] value = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.FilterElementValueField.concat("_").concat(entryTypeId).concat("_hidden"));
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

		if (!parseMoreOptions) {
			String[] workflowIds =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowId.concat("_hidden"));
			for (int i = 0; i < workflowIds.length; i++) {
				String workflowId = workflowIds[i];
				String[] workflowSteps =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowStep.concat("_").concat(workflowId).concat("_step").concat("_hidden"));
				workflows.add(new SearchFilter.Workflow(workflowId, workflowSteps));
			}
		} else {
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeWorkflow)) {
					String workflowId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchWorkflowId.concat(numbers[i]), "");
					String[] workflowSteps =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowStep.concat(numbers[i]));
					if (!workflowId.equals("")) {
						workflows.add(new SearchFilter.Workflow(workflowId, workflowSteps));
					}
				}
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
		
		if (!parseMoreOptions) {
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
		
		
		if (!parseMoreOptions) {
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
				if (key.startsWith(SearchFilterKeys.SearchFolders)) {
					String folderId = key.replaceFirst(SearchFilterKeys.SearchFolders + "_", "");
					folderIds.add(folderId);
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
