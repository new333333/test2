package com.sitescape.team.search.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.dom4j.Document;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.web.util.PortletRequestUtils;

public class SearchFilterRequestParser {
		
	public static Document getSearchQuery(PortletRequest request, DefinitionModule definitionModule) {
		Boolean joiner = PortletRequestUtils.getBooleanParameter(request, SearchFilterKeys.SearchJoiner, true);
		SearchFilter searchFilter = new SearchFilter(joiner);
		
		String filterName = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterNameField, "");
		if (filterName != null && !filterName.equals("")) {
			searchFilter.addFilterName(filterName);
		}
		
		String searchText = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchText, "");
		if (!searchText.equals("")) {
			searchFilter.addText(searchText);
		}
		
		String authors = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchAuthors, "");
		if (authors!=null && !authors.equals("")) {
			String[] authorsArr = authors.split(" ");
			for (int i=0; i<authorsArr.length; i++) {
				searchFilter.addCreator(authorsArr[i]);
			}
		}
		
		String tags =  PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchTags, "");
		if (tags!=null && !tags.equals("")) {
			String[] tagsArr = tags.split(" ");
			for (int i=0; i<tagsArr.length; i++) {
				searchFilter.addTag(tagsArr[i]);
			}
		}
		
		String[] numbers = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchNumbers, "").split(" ");
		String[] types = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchTypes, "").split(" ");
		
		for (int i=0; i<types.length; i++) {
			if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeWorkflow)) {
				String workflowId =  PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchWorkflowId.concat(numbers[i]), "");
				String[] workflowSteps =  PortletRequestUtils.getStringParameters(request, SearchFilterKeys.SearchWorkflowStep.concat(numbers[i]));
				if (!workflowId.equals("")) searchFilter.addWorkflow(workflowId, workflowSteps);
			}
			if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeEntry)) {
				String entryTypeId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterEntryDefIdField.concat(numbers[i]), "");
				
				String entryFieldId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField.concat(numbers[i]), SearchFilter.AllEntries);
				String[] value = PortletRequestUtils.getStringParameters(request, SearchFilterKeys.FilterElementValueField.concat(numbers[i]));
				String valueType = null;
				if (entryTypeId != null && !entryTypeId.equals("")) {
					Map fieldsMap = definitionModule.getEntryDefinitionElements(entryTypeId);
					if (fieldsMap != null && fieldsMap.get(entryFieldId) != null) {
						valueType = (String)((Map)fieldsMap.get(entryFieldId)).get(SearchFilterToMapConverter.EntryField.TypeField);
					}
				}
				if (!entryTypeId.equals("")) searchFilter.addEntryAttributeValues(entryTypeId, entryFieldId, value, valueType);
				
			}
			if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeCreationDate) || 
					types[i].equals(SearchFilterToMapConverter.SearchBlockTypeModificationDate)) {
				String startDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchStartDate.concat(numbers[i]), "");
				String endDate = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchEndDate.concat(numbers[i]), "");
				SimpleDateFormat inputFormater = new SimpleDateFormat("yyyy-MM-dd");
				User user = RequestContextHolder.getRequestContext().getUser();
				inputFormater.setTimeZone(user.getTimeZone());
				Date startD = null;
				Date endD = null;
				if (!startDate.equals("")) {
					try {startD = inputFormater.parse(startDate);} 
					catch (ParseException e) {
						//logger.error("Parse exception by date:"+startDate);
					}
				}
				if (!endDate.equals("")) {	
					try {endD = inputFormater.parse(endDate);} 
					catch (ParseException e) {
						// logger.error("Parse exception by date:"+endDate);
					}
				}
				if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeCreationDate))
					searchFilter.addCreationDateRange(startD, endD);
				else if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeModificationDate))
					searchFilter.addModificationDateRange(startD, endD);
				
			}
			if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeAuthor)) {
				String authorTitle = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchAuthors.concat(numbers[i]).concat("_selected"), "");
				String authorId = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchAuthors.concat(numbers[i]), "");
				if (!authorId.equals("")) searchFilter.addCreatorById(authorId, authorTitle);
				else if (!authorTitle.equals("")) searchFilter.addCreator(authorTitle); 
			}
			if (types[i].equals(SearchFilterToMapConverter.SearchBlockTypeTag)) {
				String personalTag = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchPersonalTags.concat(numbers[i]), "");
				String communityTag = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.SearchCommunityTags.concat(numbers[i]), "");
				if (!personalTag.equals("")) searchFilter.addPersonalTag(personalTag);
				if (!communityTag.equals("")) searchFilter.addCommunityTag(communityTag);
			}
		}
		return searchFilter.getFilter();
	}
	
	
}
