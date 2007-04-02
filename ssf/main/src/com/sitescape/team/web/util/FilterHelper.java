package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchEntryFilter;
import com.sitescape.team.search.SearchFilter;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;

/*********************************************************************
 * Object to hold a named search filter
 * @author Peter Hurley
 *
 */
public class FilterHelper {  
	//Search form field names
	public final static String SearchAuthors = "searchAuthors";
	public final static String SearchNumbers = "searchNumbers";
	public final static String SearchStartDate = "searchStartDate";
	public final static String SearchEndDate = "searchEndDate";
	public final static String SearchTypes = "searchTypes";
	public final static String SearchText = "searchText";
	public final static String SearchWorkflowId = "searchWorkflow";
	public final static String SearchWorkflowStep ="searchWorkflowStep";
	public final static String SearchJoiner = "searchJoinerAnd";
	public final static String SearchCommunityTags = "searchCommunityTags";
	public final static String SearchPersonalTags = "searchPersonalTags";
	public final static String SearchTextAndTags = "searchTextAndTags";
	public final static String SearchTags = "searchTags";
	public final static String SearchTagsOr = "searchTagsOr";
	public final static String SearchAdditionalFilters = "additionalFilters";
	public final static String TabTitle = "tabTitle";
	
   	//Search filter document element names
   	public final static String FilterRootName = "searchFilter";
   	public final static String FilterName = "filterName";
   	public final static String FilterTerms = "filterTerms";
   	public final static String FilterTerm = "filterTerm";
   	public final static String FilterType = "filterType";
   	public final static String FilterEntryDefId = "filterEntryDefId";
   	public final static String FilterElementName = "filterElementName";
   	public final static String FilterElementValue = "filterElementValue";
   	public final static String FilterEntityType = "filterEntityType";
   	public final static String FilterDocType = "filterDocType";
   	public final static String FilterFolderId = "filterFolderId";
   	public final static String FilterTypeSearchText = "text";
   	public final static String FilterTypeAuthor = "author";
   	public final static String FilterTypeDate = "date";
   	public final static String FilterTypeCommunityTagSearch = "communityTag";
   	public final static String FilterTypePersonalTagSearch = "personalTag";
   	public final static String FilterTypeEntry = "entry";
   	public final static String FilterTypeCreatorById = "creatorById";
   	public final static String FilterTypeTopEntry = "topEntry";
   	public final static String FilterTypeWorkflow = "workflow";
   	public final static String FilterTypeFolders = "folders";
   	public final static String FilterTypeTags = "tags";
   	public final static String FilterTypeDocTypes = "docTypes";
   	public final static String FilterTypeEntityTypes = "entityTypes";
   	public final static String FilterTypeElement = "element";
   	public final static String FilterStartDate = "startDate";
   	public final static String FilterEndDate = "endDate";
   	public final static String FilterWorkflowDefId = "filterWorkflowDefId";
   	public final static String FilterWorkflowStateName = "filterWorkflowStateName";
   	
   	//Attribute names
   	public final static String FilterAnd = "filterTermsAnd";
   	
   	//formData fields and values
   	private final static String FilterNameField = "filterName";
   	private final static String FilterTypeField = "filterType";
   	
   	public final static String FilterEntryDefIdField = "ss_entry_def_id";
   	public final static String FilterEntryDefIdCaptionField = "ss_entry_def_id_caption";
   	public final static String FilterElementNameField = "elementName";
   	public final static String FilterElementNameCaptionField = "elementNameCaption";
   	public final static String FilterElementValueField = "elementValue";
   	public final static String FilterCommunityTagsField = "searchCommunityTags";
   	public final static String FilterPersonalTagsField = "searchPersonalTags";
   	public final static String FilterElementValueTypeField = "elementValueType";
   	public final static String FilterWorkflowDefIdField = "ss_workflow_def_id";
   	public final static String FilterWorkflowDefIdCaptionField = "ss_workflow_def_id_caption";
   	public final static String FilterWorkflowStateNameField = "ss_stateNameData";

   	public static String MinimumSystemDate = "19000000";
   	public static String MaximumSystemDate = "30000000";
   	
	//Routine to parse the results of submitting the search form
   	static public Document getEmptySearchQuery () throws Exception {
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);

		Element filterTerms = sfRoot.addElement(FilterTerms);

		//searchFilter.asXML();
		return searchFilter;
	}   	
   	
	//Routine to parse the results of submitting the search form
   	static public Document getSearchQuery (PortletRequest request) throws Exception {
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);

		Element filterTerms = sfRoot.addElement(FilterTerms);

		//Get the search text
		String searchText = PortletRequestUtils.getStringParameter(request, SearchText, "");
		if (!searchText.equals("")) {
			Element filterTerm = filterTerms.addElement(FilterTerm);
			filterTerm.addAttribute(FilterType, FilterTypeSearchText);
			filterTerm.addText(searchText);
		}

		//searchFilter.asXML();
		return searchFilter;
	}
   	
  	
   	//Routine to parse the results of submitting the filter builder form
   	static public Document getSearchFilter (PortletRequest request) throws Exception {
		
   		SearchFilter sf = new SearchFilter();
   		
   		Map formData = request.getParameterMap();

   		String filterName = PortletRequestUtils.getStringParameter(request, FilterNameField, "");
		sf.addFilterName(filterName);
	
		//Get the terms out of the formData
		Integer maxTermNumber = new Integer(PortletRequestUtils.getRequiredStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX));
		for (int i = 1; i <= maxTermNumber.intValue(); i++) {
			String filterType = PortletRequestUtils.getStringParameter(request, FilterTypeField + String.valueOf(i), "");
			
			
			if (filterType != null && !filterType.equals("")) {
				//Found a possible term
				if (formData.containsKey("deleteTerm")) {
					String filterTermNumber = PortletRequestUtils.getStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER);
					if (filterTermNumber.equals(String.valueOf(i))) {
						//This term is to be deleted
						continue;
					}
				}
				
				if (filterType.equals(FilterTypeSearchText)) {
					//Get the search text
					String searchText = PortletRequestUtils.getStringParameter(request, FilterElementValueField + String.valueOf(i), "");
					if (!searchText.equals("")) {
						sf.addTextFilter(searchText);
					}
				} else if (filterType.equals(FilterTypeEntry)) {
					//Get the entry definition, element and value
					String defId = PortletRequestUtils.getStringParameter(request, FilterEntryDefIdField + String.valueOf(i), "");
					String name = PortletRequestUtils.getStringParameter(request, FilterElementNameField + String.valueOf(i), "");
					String[] value = PortletRequestUtils.getStringParameters(request, FilterElementValueField + String.valueOf(i));
					String[] valueType = PortletRequestUtils.getStringParameters(request, FilterElementValueTypeField + String.valueOf(i));
					if (valueType.length > 0 && valueType[0].equals("checkbox")) {
						//Fix up the value for a checkbox. Make it either true or false
						if (value.length > 0 && value[0].equals("on")) {
							value[0] = "true";
						} else if (value.length > 0) {
							value[0] = "false";
						} else {
							value = new String[] {"false"};
						}
					}
					
					sf.addEntryTypeFilter(defId, name, value);
				} else if (filterType.equals(FilterTypeWorkflow)) {
					//Get the workflow definition and state
					String defId = PortletRequestUtils.getStringParameter(request, FilterWorkflowDefIdField + String.valueOf(i), "");
					String[] states = (String[])formData.get(FilterWorkflowStateNameField + String.valueOf(i));
					if (!defId.equals("") && states != null && states.length > 0) {
						sf.addWorkflowFilter(defId, states);
					}
				} else if (filterType.equals(FilterTypeFolders)) {
					//Get the list of folders
					Iterator itFormData = formData.keySet().iterator();
					List folderIds = new ArrayList();
					while (itFormData.hasNext()) {
						String key = (String)itFormData.next();
						if (key.matches("^ss_sf_id_[0-9]+$")) {
							folderIds.add(key.replaceFirst("^ss_sf_id_", ""));
						}
					}
					for (Object id : folderIds) {
						sf.addFolderTerm((String) id);
					}
				} else if (filterType.equals(FilterTypeTags)) {
					// TODO: temporary, remove later
					// System.out.println("FILTER TYPE TAG! ");
					String searchTags = PortletRequestUtils.getStringParameter(request, FilterCommunityTagsField + String.valueOf(i), "");
					// addFilterTermElement(filterTerms, FilterTypeCommunityTagSearch, searchTags);
					if (!searchTags.equals("")) {
						sf.addTagsFilter(FilterTypeCommunityTagSearch, searchTags);
					}
					
					String searchPersonalTags = PortletRequestUtils.getStringParameter(request, FilterPersonalTagsField + String.valueOf(i), "");
					// addFilterTermElement(filterTerms, FilterTypePersonalTagSearch, searchPersonalTags);
					if (!searchPersonalTags.equals("")) {
						sf.addTagsFilter(FilterTypePersonalTagSearch, searchPersonalTags);
					}
				}
			}
		}
		// TODO: temporary, remove later
		// System.out.println("AFTER ADD TAGS: "+searchFilter.asXML());
		//searchFilter.asXML();
		
		//return searchFilter;
		// end remove
		return sf.getFilter();
	}
   	
   	static public Map buildFilterFormMap(Document searchFilter, Map commonElements) {
   		Map searchFilterData = new HashMap();
   		if (searchFilter == null) {
   			//Create an empty filter
   			searchFilter = DocumentHelper.createDocument();
   			Element sfRoot = searchFilter.addElement(FilterRootName);
   			Element filterNameEle = sfRoot.addElement(FilterName);
   			filterNameEle.setText("");
   			Element filterTerms = sfRoot.addElement(FilterTerms);
   		}
   		Element sfRoot = searchFilter.getRootElement();
    	List liFilterTerms = sfRoot.selectNodes(FilterTerms + "/" + FilterTerm);
		searchFilterData.put("filterTermCount", new Integer(liFilterTerms.size()));
    	for (int i = 0; i < liFilterTerms.size(); i++) {
    		Element filterTerm = (Element) liFilterTerms.get(i);
    		String filterType = filterTerm.attributeValue(FilterType, "");
       		if (filterType.equals(FilterTypeSearchText)) {
    			//Add the search text field
    			searchFilterData.put(FilterTypeField + String.valueOf(i+1), FilterTypeSearchText);
    			searchFilterData.put(FilterElementValueField+ String.valueOf(i+1), filterTerm.getText());
    		} else if (filterType.equals(FilterTypeEntry)) {
    			//This is an entry term. 
    			String defId = filterTerm.attributeValue(FilterHelper.FilterEntryDefId, "");
    			String elementName = filterTerm.attributeValue(FilterHelper.FilterElementName, "");
    			String defIdCaption = defId;
    			String elementNameCaption = elementName;
    			Definition def;
    			if (defId.equals("")) {
    				defId = "_common";
    				defIdCaption = NLT.get("filter.commonElements");
    				if (commonElements.containsKey(elementName)) {
    					elementNameCaption = (String) ((Map)commonElements.get(elementName)).get("caption");
    				}
    			} else {
    				//Get the definition title
    				try {
    					def = DefinitionHelper.getDefinition(defId);
     					defIdCaption = def.getTitle();
    					Document defDoc = def.getDefinition();
    					Element item = (Element)defDoc.getRootElement().selectSingleNode("//item/properties/property[@name='name' and @value='"+elementName+"']");
    					if (item != null) {
    						Element captionEle = (Element) item.selectSingleNode("../property[@name='caption']");
    						if (captionEle != null) elementNameCaption = NLT.getDef(captionEle.attributeValue("value", elementName));
    					}
    				} catch (Exception ex) {/*skip*/}
    			}
    			
    			searchFilterData.put(FilterTypeField + String.valueOf(i+1), FilterTypeEntry);
    			searchFilterData.put(FilterEntryDefIdField+ String.valueOf(i+1), defId);
    			searchFilterData.put(FilterEntryDefIdCaptionField+ String.valueOf(i+1), defIdCaption);
    			searchFilterData.put(FilterElementNameField+ String.valueOf(i+1), elementName);
    			searchFilterData.put(FilterElementNameCaptionField+ String.valueOf(i+1), elementNameCaption);
     			
    			Iterator itTermValues = filterTerm.selectNodes(FilterHelper.FilterElementValue).iterator();
    			Map valueMap = new HashMap();
    			while (itTermValues.hasNext()) {
    				String value = ((Element) itTermValues.next()).getText();
    				if (!value.equals("")) {
    					valueMap.put(value, value);
    				}
    			}
    			searchFilterData.put(FilterElementValueField+ String.valueOf(i+1), valueMap);
    		} else if (filterType.equals(FilterTypeWorkflow)) {
    			//This is a workflow state term. 
    			Definition def = null;
    			Document defDoc = null;
    			String defId = filterTerm.attributeValue(FilterHelper.FilterWorkflowDefId, "");
    			String defIdCaption = defId;
				try {
	 				//Get the definition title
					def = DefinitionHelper.getDefinition(defId);
 					defIdCaption = def.getTitle();
					defDoc = def.getDefinition();
				} catch (Exception ex) {/*skip*/}
				
    			//String stateName = filterTerm.attributeValue(FilterHelper.FilterWorkflowStateName, "");
    			Iterator itTermStates = filterTerm.selectNodes(FilterHelper.FilterWorkflowStateName).iterator();
    			Map stateNameMap = new HashMap();
    			while (itTermStates.hasNext()) {
    				String stateName = ((Element) itTermStates.next()).getText();
    				if (!stateName.equals("")) {
    	    			String stateNameCaption = stateName;
    					try {
    						Element item = (Element)defDoc.getRootElement().selectSingleNode("//item/properties/property[@name='name' and @value='"+stateName+"']");
    						if (item != null) {
    							Element captionEle = (Element) item.selectSingleNode("../property[@name='caption']");
    							if (captionEle != null) stateNameCaption = NLT.getDef(captionEle.attributeValue("value", stateName));
    						}
        					stateNameMap.put(stateName, stateNameCaption);
    					} catch (Exception ex) {/*skip*/}
    				}
    			}
    			searchFilterData.put(FilterTypeField + String.valueOf(i+1), FilterTypeWorkflow);
    			searchFilterData.put(FilterWorkflowDefIdField+ String.valueOf(i+1), defId);
    			searchFilterData.put(FilterWorkflowDefIdCaptionField+ String.valueOf(i+1), defIdCaption);
    			searchFilterData.put(FilterWorkflowStateNameField+ String.valueOf(i+1), stateNameMap);
    		} else if (filterType.equals(FilterTypeTags)) {
    			searchFilterData.put(FilterTypeField + String.valueOf(i+1), FilterTypeTags);
    			searchFilterData.put(FilterElementValueField + String.valueOf(i+1), filterTerm.getText());
    		}
    	}
   		
   		return searchFilterData;
   	}

	//Routine to parse the results of submitting the blog summary dashboard config form
   	static public Document getFolderListQuery (PortletRequest request, List folderIds) throws Exception {
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);

		String filterName = PortletRequestUtils.getStringParameter(request, FilterNameField, "");
		Element filterNameEle = sfRoot.addElement(FilterName);
		filterNameEle.setText(filterName);
		
		Element filterTerms = sfRoot.addElement(FilterTerms);
		
		for (Object id : folderIds) {
			Element filterTerm = filterTerms.addElement(FilterTerm);
			filterTerm.addAttribute(FilterType, FilterTypeFolders);
			filterTerm.addAttribute(FilterFolderId, (String)id);
		}
		//searchFilter.asXML();
		return searchFilter;
	}
   	

	//Routine to convert a search filter into the form that Lucene wants 
   	static public Document convertSearchFilterToSearchBoolean(Document searchFilter) {
   		Map options = new HashMap();
   		return convertSearchFilterToSearchBoolean(searchFilter, options);
   	}
   	
   	static public Document convertSearchFilterToSearchBoolean(Document searchFilter, Map options) {
		//Build the search query
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		
		// create main AND element for all terms 
		// one terms block is for user defined filters, additional blocks are for acl definitions 
		// and kind of entry specification e.g. folder, user or workspace, it has sense only with AND
    	Element qTreeAndElement = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
    	    	
    	Element sfRootElement = searchFilter.getRootElement();
    	List liFilterTerms = sfRootElement.selectNodes(FilterTerms);

    	for (int i = 0; i < liFilterTerms.size(); i++) {
    		Element filterTerms = (Element) liFilterTerms.get(i);
    		// each terms block can have information if the children should be join with AND
    		// if not defined use OR as default
    		String joiner = QueryBuilder.OR_ELEMENT;
    		if (filterTerms.attributeValue(FilterAnd, "").equals(Boolean.TRUE.toString())) {
    			joiner = QueryBuilder.AND_ELEMENT;
    		}

    		List liFilterTermsTerm = filterTerms.selectNodes("./" + FilterTerm);
    		if (liFilterTermsTerm.size() > 0) {
    			Element block = qTreeAndElement.addElement(joiner);

            	for (int j = 0; j < liFilterTermsTerm.size(); j++) {
    	    		// add term to current block
    	    		Element filterTerm = (Element) liFilterTermsTerm.get(j);
    	    		String filterType = filterTerm.attributeValue(FilterType, "");
    	    		if (filterType.equals(FilterTypeSearchText)) {
    	    			addSearchTextField(block, filterTerm.getText());
    	    		} else if (filterType.equals(FilterTypeAuthor)) {
    	    			addAuthorField(block, filterTerm.getText());
    	    		} else if (filterType.equals(FilterTypeTags)) {
    	    			addTagsField(block, filterTerm.getText());
    	    		} else if (filterType.equals(FilterTypeEntry) || filterType.equals(FilterTypeCreatorById)) {	    			
    	    			parseAndAddEntryField(block, filterTerm);
    	    		} else if (filterType.equals(FilterTypeTopEntry)) {
    	    			addTopEntryField(block);
    	    		} else if (filterType.equals(FilterTypeWorkflow)) {
    	    			parseAndAddWorkflowField(block, filterTerm);
    	    		} else if (filterType.equals(FilterTypeFolders)) {
    	    			addFolderField(block, filterTerm.attributeValue(FilterHelper.FilterFolderId, ""));
    	    		} else if (filterType.equals(FilterTypeEntityTypes)) {
    	    			parseAndAddEntityTypesField(block, filterTerm);
    	    		} else if (filterType.equals(FilterTypeDocTypes)) {
    	    			parseAndAddDocTypesField(block, filterTerm);
    	    		} else if (filterType.equals(FilterTypeElement)) {
    	    			addElementField(block, filterTerm);
    	    		} else if (filterType.equals(FilterTypeCommunityTagSearch)) {
    	    			addCommunityTagField(block, filterTerm.getText());
    	    		} else if (filterType.equals(FilterTypePersonalTagSearch)) {
    	    			addPersonalTagField(block, filterTerm.getText());
    		    	} else if (filterType.equals(FilterTypeDate)) {
    		    		addDateRange(block, filterTerm.attributeValue(FilterElementName, ""), filterTerm.attributeValue(FilterStartDate, ""),filterTerm.attributeValue(FilterEndDate, ""));
    		    	}
            	}
    		}
    	}
    	//Add in any additional fields from the options map
    	if ((options != null) && options.containsKey(ObjectKeys.SEARCH_FILTER_AND)) {
    		Document filter = (Document) options.get(ObjectKeys.SEARCH_FILTER_AND);
    		Element filterRoot = filter.getRootElement();
    		qTreeAndElement.add((Element)filterRoot.clone());
    		
    		//System.out.println("AND IN OPTIONS: "+filter.asXML());
    	}
    	
    	return qTree;
	}
   	
   	private static void addDateRange(Element block, String fieldName, String startDate, String endDate) {
   		
   		Element range = block.addElement(QueryBuilder.RANGE_ELEMENT);
   		range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, fieldName);
   		if (startDate == null || startDate.equals("")) startDate = MinimumSystemDate;
   		if (endDate == null || endDate.equals("")) endDate = MaximumSystemDate;
   		
		Element start = range.addElement(QueryBuilder.RANGE_START);
		start.setText(startDate);
   		
		Element end = range.addElement(QueryBuilder.RANGE_FINISH);
		end.setText(endDate);
	}

	private static void addPersonalTagField(Element block, String personalTag) {
		Element field = block.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
	    String [] strTagArray = personalTag.split("\\s");
	    for (int k = 0; k < strTagArray.length; k++) {
	    	String strTag = strTagArray[k];
	    	if (strTag.equals("")) continue;
    		Element child = field.addElement(QueryBuilder.TAG_ELEMENT);
    	    child.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, strTag);
	    }
	}

	private static void addCommunityTagField(Element block, String text) {
		Element field = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.TAG_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);
	}

	private static void addElementField(Element block, Element filterTerm) {
    	//Search for an element value
		String elementName = filterTerm.attributeValue(FilterHelper.FilterElementName, "");
		Element andField = block.addElement(QueryBuilder.AND_ELEMENT);
		Element field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, elementName);
	    Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    child.setText(filterTerm.getText());
	}

	private static void parseAndAddDocTypesField(Element block, Element filterTerm) {
    	//Add an OR field with all of the desired docId types
		Element andField = block;
		Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
		Iterator itTermTypes = filterTerm.selectNodes(FilterDocType).iterator();
		while (itTermTypes.hasNext()) {
			String entityTypeName = ((Element) itTermTypes.next()).getText();
			if (!entityTypeName.equals("")) {
				Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
				field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.DOC_TYPE_FIELD);
				field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
				Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
				child2.setText(entityTypeName);
			}
		}
	}

	private static void parseAndAddEntityTypesField(Element block, Element filterTerm) {
		//Add an OR field with all of the desired entity types
		Element andField = block;
		Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
		Iterator itTermTypes = filterTerm.selectNodes(FilterEntityType).iterator();
		while (itTermTypes.hasNext()) {
			String entityTypeName = ((Element) itTermTypes.next()).getText();
			if (!entityTypeName.equals("")) {
				Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
				field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.ENTITY_FIELD);
				field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
				Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
				child2.setText(entityTypeName);
			}
		}
   	}
	
	private static void addFolderField(Element block, String folderId) {
		Element field;
		Element child;
		Element andField = block;
		if (!folderId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.BINDER_ID_FIELD);
	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(folderId);
		}
	}

	private static void parseAndAddWorkflowField(Element block, Element filterTerm) {
		//This is a workflow state term. Build booleans from the state name.
		String defId = filterTerm.attributeValue(FilterHelper.FilterWorkflowDefId, "");
		Element field;
		Element child;
		Element andField = block;
		if (!defId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.WORKFLOW_PROCESS_FIELD);
	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(defId);
		}
		
    	//Add an OR field with all of the desired states
		if (filterTerm.selectNodes(FilterWorkflowStateName).size()>0) {
			Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
			Iterator itTermStates = filterTerm.selectNodes(FilterWorkflowStateName).iterator();			
			while (itTermStates.hasNext()) {
				String stateName = ((Element) itTermStates.next()).getText();
				if (!stateName.equals("")) {
					Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
					field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.WORKFLOW_STATE_FIELD);
					field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
					Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child2.setText(stateName);
				}
			}
		}
	}

	private static void addTopEntryField(Element block) {
		//This is asking for top entries only (e.g., no replies or attachments)
    	//Look only for entryType=entry
		Element field;
		Element child;
       	field = block.addElement(QueryBuilder.FIELD_ELEMENT);
       	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
       	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
       	child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);
       	//Look only for docType=entry
       	field = block.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);		
	}

	private static void parseAndAddEntryField(Element block, Element filterTerm) {
		//This is an entry term. Build booleans from the element name and values.
		String defId = filterTerm.attributeValue(FilterHelper.FilterEntryDefId, "");
		String elementName = filterTerm.attributeValue(FilterHelper.FilterElementName, "");
		if (elementName.equals("")) {
			//If no element name is specified, search for all entries with this definition id
			Element field;
			Element child;
			Element andField = block;
			if (!defId.equals("")) {
				andField = block.addElement(QueryBuilder.AND_ELEMENT);
				field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
				field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.COMMAND_DEFINITION_FIELD);
		    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		    	child.setText(defId);
			}
		} else {
			Iterator itTermValues = filterTerm.selectNodes(FilterHelper.FilterElementValue).iterator();
			while (itTermValues.hasNext()) {
				String value = ((Element) itTermValues.next()).getText();
				if (!value.equals("")) {
					Element field;
					Element child;
					Element andField = block;
	    			if (!defId.equals("")) {
	    				andField = block.addElement(QueryBuilder.AND_ELEMENT);
		    			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
		    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.COMMAND_DEFINITION_FIELD);
		    	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		    	    	child.setText(defId);
	    			}
	    			
	    	    	field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
	    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, elementName);
	    				    	    			
	    			if (value.contains("*"))
	    				field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "false");
	    			else
	    				field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
	    			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	    	child.setText(value);
				}
			}
		}
	}

	private static void addTagsField(Element block, String text) {
		Element subOr = block.addElement(QueryBuilder.OR_ELEMENT);
		Element field = subOr.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.TAG_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);
		field = subOr.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
		child = field.addElement(QueryBuilder.TAG_ELEMENT);
	    child.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, text);
	}

	private static void addAuthorField(Element block, String text) {
		Element subOr = block.addElement(QueryBuilder.OR_ELEMENT);
		
		Element field = subOr.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATOR_NAME_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);
    	
    	field = subOr.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATOR_TITLE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);		
	}

	private static void addSearchTextField(Element block, String text) {
		Element field = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.ALL_TEXT_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(text);
	}
	
	
   	//Routine to convert a search filter into a Lucene query for People (People, Places, and Things) 
   	static public Document convertSearchFilterToPeopleSearchBoolean(
			Document searchFilter) {
		// Build the search query
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		Element qTreeBoolElement = qTreeRootElement
				.addElement(QueryBuilder.AND_ELEMENT);
		Element andElement = qTreeBoolElement;

		Element sfRootElement = searchFilter.getRootElement();

		// Add the filter terms to the boolean query
		List liFilterTerms = sfRootElement.selectNodes(FilterTerms + "/"
				+ FilterTerm);
		for (int i = 0; i < liFilterTerms.size(); i++) {
			Element filterTerm = (Element) liFilterTerms.get(i);
			String filterType = filterTerm.attributeValue(FilterType, "");
			if (filterType.equals(FilterTypeSearchText)) {
				// Add the search text as a field
				Element field = andElement
						.addElement(QueryBuilder.FIELD_ELEMENT);
				field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,
						BasicIndexUtils.ALL_TEXT_FIELD);
				String value = filterTerm.getText();
				if (value.contains("*"))
					field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE,
							"false");
				else
					field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE,
							"true");
				Element child = field
						.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
				child.setText(value);
			}

/* Need a beter implemenation - disable for now
			Element gFilterTerm = (Element) sfRootElement
					.selectSingleNode(QueryBuilder.GROUP_VISIBILITY_ELEMENT);
			if (gFilterTerm != null) {
				Element viz = andElement
						.addElement(QueryBuilder.GROUP_VISIBILITY_ELEMENT);
				viz
						.addAttribute(
								QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,
								gFilterTerm
										.attributeValue(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE));
			}
*/
		}
		// Add entrytype=user|group boolean term
		Element orElement = andElement.addElement(QueryBuilder.OR_ELEMENT);

		Element field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,
				EntityIndexUtils.ENTRY_TYPE_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(EntityIndexUtils.ENTRY_TYPE_USER);

		field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,
				EntityIndexUtils.ENTRY_TYPE_FIELD);
		child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(EntityIndexUtils.ENTRY_TYPE_GROUP);

		// qTree.asXML();
		return qTree;
	}
   	
   	
	
	static public String getFilterName(Document searchFilter) {
		Element sfRoot = searchFilter.getRootElement();
		Element filterName = sfRoot.element(FilterName);
		return filterName.getText();
	}

	static public void createTagsFilterTerms(Element sfRoot, Element filterTerms, String searchText) {
		//Add the title term
		Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeElement);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTerm.setText(searchText.replaceFirst("\\*", "").trim());
		
		filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeElement);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTerm.setText(searchText.trim());
		
		//Add terms to search folders and workspaces
		filterTerms = addAttributeFilterElement(sfRoot, FilterTerms, FilterAnd, "true");
		filterTerm = addAttributeFilterElement(filterTerms, FilterTerm, FilterType, FilterTypeEntityTypes);
		addTextFilterElement(filterTerm, FilterEntityType, EntityIdentifier.EntityType.folder.name());
		addTextFilterElement(filterTerm, FilterEntityType, EntityIdentifier.EntityType.workspace.name());
		
	}

	static public Element createFilterTerms(Element rootElement, String filterType, String searchText) {
		Element filterTerms = rootElement.addElement(FilterHelper.FilterTerms);
		// Add the title term
		Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, filterType);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(searchText.replaceFirst("\\*", "").trim());
		
		filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, filterType);
		filterTerm.addAttribute(FilterHelper.FilterElementName, EntityIndexUtils.TITLE_FIELD);
		filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(searchText.trim());
		return filterTerms;
		
	}

	static public void createPlacesFilterTerms (Element root, String searchText) {
		Element filterTerms = createFilterTerms(root, FilterTypeElement, searchText);
		
//		Add terms to search folders and workspaces
		filterTerms = root.addElement(FilterHelper.FilterTerms);
		filterTerms.addAttribute(FilterHelper.FilterAnd, "true");
		Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterType, FilterHelper.FilterTypeEntityTypes);
		Element filterTerm2 = filterTerm.addElement(FilterHelper.FilterEntityType);
		filterTerm2.setText(EntityIdentifier.EntityType.folder.name());
		filterTerm2 = filterTerm.addElement(FilterHelper.FilterEntityType);
		filterTerm2.setText(EntityIdentifier.EntityType.workspace.name());
	}
	
	
	static public void addFilterTermElement(Element filterTerms, String attributeType, String attributeValue) {
		if (!attributeValue.equals("")) {
			Element filterTerm = filterTerms.addElement(FilterTerm);
			filterTerm.addAttribute(FilterType, attributeType);
			filterTerm.addText(attributeValue);
		}
	}
	
	static public void addTextFilterElement(Element parentElement, String filterType, String filterValue) {
		Element filterElement = parentElement.addElement(filterType);
		filterElement.setText(filterValue);
	}
	static public Element addAttributeFilterElement(Element parentElement, String type, String attribute, String value) {
		Element filterElement = parentElement.addElement(type);
		filterElement.addAttribute(attribute, value);
		return filterElement;
	}
	
   	public static Document getSearchTextQuery(String searchText) {
   		SearchEntryFilter entryFilter = new SearchEntryFilter();

		if (!searchText.equals("")) {
			entryFilter.addText(searchText);
		}
		return entryFilter.getFilter();
   	}

	
}