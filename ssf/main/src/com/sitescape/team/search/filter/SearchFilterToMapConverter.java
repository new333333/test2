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
import com.sitescape.team.domain.User;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.portlet.binder.SearchController;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.FilterHelper;

public class SearchFilterToMapConverter {
	
	private Log logger = LogFactory.getLog(this.getClass());

	public static final String SearchBlockTypeCreationDate = "creation_date";
	public static final String SearchBlockTypeModificationDate = "modification_date";
	public static final String SearchBlockTypeWorkflow = "workflow";
	public static final String SearchBlockTypeEntry = "entry";
	public static final String SearchBlockTypeAuthor = "creator_by_id";
	public static final String SearchBlockTypeTag = "tag";
	public static final String SearchBlockType = "type";
	public static final String SearchStartDate = "startDate";
	public static final String SearchEndDate = "endDate";
	public static final String SearchStartDateNotFormated = "startDateNotFormated";
	public static final String SearchEndDateNotFormated = "endDateNotFormated";
	
	public static final String SearchEntryType="entryType";
	public static final String SearchEntryElement="entryElement";
	public static final String SearchEntryValues="entryValues";
	public static final String SearchEntryValueType="valueType";
	public static final String SearchEntryValuesNotFormatted="entryValuesNotFormatted";
	public static final String SearchAuthor="authorId";
	public static final String SearchAuthorTitle="authorTitle";
	public static final String SearchTag="tag";
	public static final String SearchPersonalTag="personalTag";
	public static final String SearchCommunityTag="communityTag";
		
	private Document searchQuery;
	
	private DefinitionModule definitionModule;
	
	private ProfileModule profileModule;
	
	private Map convertedQuery;

	public SearchFilterToMapConverter(Document query, DefinitionModule definitionModule, ProfileModule profileModule) {
		super();
		this.searchQuery = query;
		this.definitionModule = definitionModule;
		this.profileModule = profileModule;
	}
	
	public Map toMap() {
		convertedQuery = new HashMap();
		if (searchQuery != null) { 
	    	Element rootElement = searchQuery.getRootElement();
	    	List liFilterTerms = rootElement.selectNodes(FilterHelper.FilterTerms);

	    	logger.debug("Query: "+searchQuery.asXML());

	    	// read the joiner information, probably the size will be always 1 so it can be in loop
	    	for (int i = 0; i < liFilterTerms.size(); i++) {
	    		Element filterTerms = (Element) liFilterTerms.get(i);

	    		String andJoiner = Boolean.FALSE.toString();
	    		if (filterTerms.attributeValue(FilterHelper.FilterAnd, "").equals(Boolean.TRUE.toString())) {
	    			andJoiner = Boolean.TRUE.toString();
	    		}
	    		
	    		String searchedText = "";
	    		String searchedTags = "";
	    		String searchedAuthors = "";
	    		Map blocks = new HashMap();
	    		List liFilterTermsTerm = filterTerms.selectNodes("./" + FilterHelper.FilterTerm);
	    		if (liFilterTermsTerm.size() > 0) {
	            	for (int j = 0; j < liFilterTermsTerm.size(); j++) {
	    	    		Element filterTerm = (Element) liFilterTermsTerm.get(j);
	    	    		String filterType = filterTerm.attributeValue(FilterHelper.FilterType, "");
	    	    		if (filterType.equals(FilterHelper.FilterTypeSearchText)) {
	    	    			if (searchedText.equals("")) searchedText = filterTerm.getText(); 
	    	    			else searchedText = searchedText.concat(" "+filterTerm.getText()); 
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeAuthor)) {
	    	    			if (searchedAuthors.equals("")) searchedAuthors = filterTerm.getText(); 
	    	    			else  searchedAuthors = searchedAuthors.concat(" "+filterTerm.getText());
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeTags)) {
	    	    			if (searchedTags.equals("")) searchedTags = filterTerm.getText(); 
	    	    			else  searchedTags = searchedTags.concat(" "+filterTerm.getText());
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeEntry)) {
	    	    			if (blocks.get(SearchBlockTypeEntry) == null) blocks.put(SearchBlockTypeEntry, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeEntry)).add(createEntryBlock(filterTerm, definitionModule, profileModule));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeEvent)) {
	    	    			if (blocks.get(SearchBlockTypeEntry) == null) blocks.put(SearchBlockTypeEntry, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeEntry)).add(createEventBlock(filterTerm, definitionModule));
	    	    		} else if ( filterType.equals(FilterHelper.FilterTypeCreatorById)) {
	    	    			if (blocks.get(SearchBlockTypeAuthor) == null) blocks.put(SearchBlockTypeAuthor, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeAuthor)).add(createCreatorBlock(filterTerm));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeWorkflow)) {
	    	    			if (blocks.get(SearchBlockTypeWorkflow) == null) blocks.put(SearchBlockTypeWorkflow, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeWorkflow)).add(createWorkflowBlock(filterTerm));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeCommunityTagSearch)) {
	    	    			if (blocks.get(SearchBlockTypeTag) == null) blocks.put(SearchBlockTypeTag, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeTag)).add(createTagBlock(filterTerm));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypePersonalTagSearch)) {
	    	    			if (blocks.get(SearchBlockTypeTag) == null) blocks.put(SearchBlockTypeTag, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeTag)).add(createTagBlock(filterTerm));
	    		    	} else if (filterType.equals(FilterHelper.FilterTypeDate)) {
	    	    			Map dateBlock = createDateBlock(filterTerm);
	    	    			if (SearchBlockTypeCreationDate.equals(dateBlock.get(SearchBlockType))) {
	    	    				if (blocks.get(SearchBlockTypeCreationDate) == null) blocks.put(SearchBlockTypeCreationDate, new ArrayList());
	    	    				((List)blocks.get(SearchBlockTypeCreationDate)).add(dateBlock);
	    	    			} else {
	    	    				if (blocks.get(SearchBlockTypeModificationDate) == null) blocks.put(SearchBlockTypeModificationDate, new ArrayList());
	    	    				((List)blocks.get(SearchBlockTypeModificationDate)).add(dateBlock);	    	    				
	    	    			}
	    		    	}
	            	}
	    		}
	    		convertedQuery.put(FilterHelper.SearchText, searchedText);
	    		convertedQuery.put(FilterHelper.SearchAuthors, searchedAuthors);
	    		convertedQuery.put(FilterHelper.SearchTags, searchedTags);
	    		convertedQuery.put(FilterHelper.SearchJoiner, andJoiner);
	    		convertedQuery.put(FilterHelper.SearchAdditionalFilters, blocks);
	    	}
		}
		return convertedQuery;
	
	}
	
	private Map createEntryBlock(Element filterTerm, DefinitionModule definitionModule, ProfileModule profileModule) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		String entryTypeId = filterTerm.attributeValue(FilterHelper.FilterEntryDefId, "");
		if (entryTypeId == null || entryTypeId.equals("")) {
			return new HashMap();
		}
		block.put(SearchEntryType, entryTypeId);
		String entryFieldId = filterTerm.attributeValue(FilterHelper.FilterElementName, "");
		if (entryFieldId == null || entryFieldId.equals("")) {
			return block;
		}
		if (entryFieldId.equals("_desc")) {
			entryFieldId = "description";
		}		
		
		block.put(SearchEntryElement, entryFieldId);
		List values = getElementValues(filterTerm);
		if (values.size() > 0) {
			Map fieldsMap = definitionModule.getEntryDefinitionElements(entryTypeId);
			String valueType = (String)((Map)fieldsMap.get(entryFieldId)).get(EntryField.TypeField);
			block.put(SearchEntryValueType, valueType);
			String value = (String)values.get(0);
			
			Object parsedValue = value;
			String formattedValue = value;
			if (valueType.equals("date")) {
				parsedValue = parseDate_from_yyyyMMdd(value);
				formattedValue = formatDate_to_yyyy_MM_dd((Date)parsedValue);
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
				Iterator users = profileModule.getUsers(Collections.singleton(Long.parseLong(value))).iterator();
				if (users.hasNext()) {
					formattedValue = ((User)users.next()).getTitle();
				}
			}
			
			block.put(SearchEntryValues, formattedValue);
			block.put(SearchEntryValuesNotFormatted, parsedValue);
		} 
		return block;
	}

	private Map createEventBlock(Element filterTerm, DefinitionModule definitionModule) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		String entryTypeId = filterTerm.attributeValue(FilterHelper.FilterEntryDefId, "");
		block.put(SearchEntryType, entryTypeId);
		String entryFieldId = filterTerm.attributeValue(FilterHelper.FilterElementName, "");
		block.put(SearchEntryElement, entryFieldId);
		List values = new ArrayList();
		
		List eventDatesNodes = filterTerm.selectNodes(FilterHelper.FilterEventDate);
		Iterator eventDatesNodesIt = eventDatesNodes.iterator(); 
		while (eventDatesNodesIt.hasNext()) {
			values.add(((Element) eventDatesNodesIt.next()).getText());
		}
		
		if (values.size() > 0) {
			Map fieldsMap = definitionModule.getEntryDefinitionElements(entryTypeId);
			String valueType = (String)((Map)fieldsMap.get(entryFieldId)).get(EntryField.TypeField);
			block.put(SearchEntryValueType, valueType);
			String value = (String)values.get(0);
			
			Object parsedValue = value;
			String formattedValue = value;
			if (valueType.equals("event")) {
				parsedValue = parseDate_from_yyyyMMdd(value);
				formattedValue = formatDate_to_yyyy_MM_dd((Date)parsedValue);
			}
			
			block.put(SearchEntryValues, formattedValue);
			block.put(SearchEntryValuesNotFormatted, parsedValue);
		} 
		return block;
	}

	private Map createCreatorBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		block.put(SearchAuthorTitle, filterTerm.attributeValue(FilterHelper.FilterCreatorTitle, ""));
		block.put(SearchAuthor, getElementValues(filterTerm).get(0));
		return block;
	}

	private Map createWorkflowBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));	
		block.put(FilterHelper.SearchWorkflowId, filterTerm.attributeValue(FilterHelper.FilterWorkflowDefId, ""));
		
		List steps = filterTerm.selectNodes(FilterHelper.FilterWorkflowStateName);
		List modelSteps = new ArrayList();
		Iterator it = steps.iterator();
		while (it.hasNext()) {
			modelSteps.add(((Element)it.next()).getText());
		}
		block.put(FilterHelper.FilterWorkflowStateName, modelSteps);
		return block;
	}
	private Map createTagBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		block.put(SearchTag, filterTerm.getTextTrim());
		return block;
	}
	
	private Map createDateBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		if (EntityIndexUtils.CREATION_DAY_FIELD.equalsIgnoreCase(filterTerm.attributeValue(FilterHelper.FilterElementName))) {
			block.put(SearchBlockType, SearchBlockTypeCreationDate);
		} else {
			block.put(SearchBlockType, SearchBlockTypeModificationDate);
		}
		
		String startDate = filterTerm.attributeValue(SearchStartDate, "");
		String endDate = filterTerm.attributeValue(SearchEndDate, "");
		
		Date startDateParsed = parseDate_from_yyyyMMdd(startDate);
		String formatedStartDate = formatDate_to_yyyy_MM_dd(startDateParsed);
		
		Date endDateParsed = parseDate_from_yyyyMMdd(endDate);
		String formatedEndDate = formatDate_to_yyyy_MM_dd(endDateParsed);
		
		block.put(SearchStartDate, formatedStartDate);
		block.put(SearchEndDate, formatedEndDate);
		block.put(SearchStartDateNotFormated, startDateParsed);
		block.put(SearchEndDateNotFormated, endDateParsed);
		return block;
	}
	
	private List getElementValues(Element filterTerm) {
		List values = filterTerm.selectNodes(FilterHelper.FilterElementValue);
		List modelValues = new ArrayList();
		Iterator it = values.iterator(); 
		while (it.hasNext()) {
			modelValues.add(((Element) it.next()).getText());
		}
		return modelValues;
	}	
	
	private Date parseDate_from_yyyyMMdd(String s) {
		User user = RequestContextHolder.getRequestContext().getUser();
		
		SimpleDateFormat inputFormater = new SimpleDateFormat("yyyyMMdd");
		inputFormater.setTimeZone(user.getTimeZone());
		
		Date result = null;
		
		try { 
			result = inputFormater.parse(s);
		} catch (ParseException e) {
			logger.error("Date [" + s + "] in search mask is in wrong format");			
		}
		
		return result;
	}
	
	/*
	 * to yyyy-MM-dd
	 */
	private String formatDate_to_yyyy_MM_dd(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat outputFormater = new SimpleDateFormat("yyyy-MM-dd");
		return outputFormater.format(date);
	}	

	public Map prepareAdditionalFiltersData(Map model) {
		if (convertedQuery == null) {
			toMap();
		}
		
		Map result = new HashMap();
		
		Map additionalOptions = (Map)convertedQuery.get(FilterHelper.SearchAdditionalFilters);
		if (additionalOptions != null) {
			prepareTags();
			Collection preparedWorkflows = prepareWorkflows();
			if (preparedWorkflows != null) {
				model.put(WebKeys.WORKFLOW_DEFINTION_MAP, preparedWorkflows);
			}
			Collection preparedEntries = prepareEntries();
			if (preparedEntries != null) {
				model.put(WebKeys.ENTRY_DEFINTION_MAP, preparedEntries);
			}
		}
		
		return result;
	}

	private Collection prepareEntries() {
		List entryTypes = DefinitionHelper.getDefinitions(Definition.FOLDER_ENTRY);
		Map additionalOptions = (Map)convertedQuery.get(FilterHelper.SearchAdditionalFilters);		
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
				Map fieldsMap = definitionModule.getEntryDefinitionElements(entryType);
				if (fieldsMap != null && fieldName != null && fieldsMap.get(fieldName) != null) {
					EntryField entryField = new EntryField(fieldName, (String)((Map)fieldsMap.get(fieldName)).get(EntryField.TitleField), (String)((Map)fieldsMap.get(fieldName)).get(EntryField.TypeField));
					((Entry)entriesMap.get(entryType)).addField(entryField);
				}	
			}
		}
		return entriesMap.values();
	}
	
	private  void prepareTags() {
		Map additionalOptions = (Map)convertedQuery.get(FilterHelper.SearchAdditionalFilters);
		List tagsFromFilter = (List) additionalOptions.get(SearchBlockTypeTag);
		if (tagsFromFilter != null) {
			Iterator it = tagsFromFilter.iterator();
			List personalTags = new ArrayList();
			List communityTags = new ArrayList();
			while (it.hasNext()) {
				Map tag = (Map)it.next();
				if (tag.get(SearchBlockType).equals(FilterHelper.FilterTypePersonalTagSearch)) {
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
	private Collection prepareWorkflows() {
		List workflows = DefinitionHelper.getDefinitions(Definition.WORKFLOW);
		Map additionalOptions = (Map)convertedQuery.get(FilterHelper.SearchAdditionalFilters);		
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
			String wfId = (String)wfFilter.get(FilterHelper.SearchWorkflowId);
			Map steps = definitionModule.getWorkflowDefinitionStates(wfId);
			
			List selectedStepsNames = (List) wfFilter.get(FilterHelper.FilterWorkflowStateName);
			Iterator filterSteps = selectedStepsNames.iterator();
			while (filterSteps.hasNext()) {
				String stepName = (String)filterSteps.next();
				WorkflowStep wfStep = new WorkflowStep(stepName, (String)((Map)steps.get(stepName)).get(WorkflowStep.TitleField));
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
