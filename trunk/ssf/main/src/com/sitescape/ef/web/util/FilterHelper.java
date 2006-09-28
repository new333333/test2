package com.sitescape.ef.web.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;

/*********************************************************************
 * Object to hold a named search filter
 * @author Peter Hurley
 *
 */
public class FilterHelper {  
	//Search form field names
	public final static String SearchText = "searchText";
	
   	//Search filter document element names
   	public final static String FilterRootName = "searchFilter";
   	public final static String FilterName = "filterName";
   	public final static String FilterTerms = "filterTerms";
   	public final static String FilterTerm = "filterTerm";
   	public final static String FilterType = "filterType";
   	public final static String FilterEntryDefId = "filterEntryDefId";
   	public final static String FilterElementName = "filterElementName";
   	public final static String FilterElementValue = "filterElementValue";
   	public final static String FilterTypeSearchText = "text";
   	public final static String FilterTypeEntry = "entry";
   	public final static String FilterTypeWorkflow = "workflow";
   	public final static String FilterWorkflowDefId = "filterWorkflowDefId";
   	public final static String FilterWorkflowStateName = "filterWorkflowStateName";
   	
   	//formData fields and values
   	private final static String FilterNameField = "filterName";
   	private final static String FilterTypeField = "filterType";
   	
   	public final static String FilterEntryDefIdField = "ss_entry_def_id";
   	public final static String FilterEntryDefIdCaptionField = "ss_entry_def_id_caption";
   	public final static String FilterElementNameField = "elementName";
   	public final static String FilterElementNameCaptionField = "elementNameCaption";
   	public final static String FilterElementValueField = "elementValue";
   	public final static String FilterElementValueTypeField = "elementValueType";
   	public final static String FilterWorkflowDefIdField = "ss_workflow_def_id";
   	public final static String FilterWorkflowDefIdCaptionField = "ss_workflow_def_id_caption";
   	public final static String FilterWorkflowStateNameField = "ss_stateNameData";
	
	//Routine to parse the results of submitting the search form
   	static public Document getSearchQuery (PortletRequest request) throws Exception {
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);
		Map formData = request.getParameterMap();

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
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);
		Map formData = request.getParameterMap();

		String filterName = PortletRequestUtils.getStringParameter(request, FilterNameField, "");
		Element filterNameEle = sfRoot.addElement(FilterName);
		filterNameEle.setText(filterName);
		
		Element filterTerms = sfRoot.addElement(FilterTerms);
		
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
						Element filterTerm = filterTerms.addElement(FilterTerm);
						filterTerm.addAttribute(FilterType, filterType);
						filterTerm.addText(searchText);
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
					if (!defId.equals("") && !name.equals("") && value.length > 0) {
						Element filterTerm = filterTerms.addElement(FilterTerm);
						filterTerm.addAttribute(FilterType, filterType);
						//If not selecting a "common" element, store the definition id, too
						if (!defId.equals("_common")) filterTerm.addAttribute(FilterEntryDefId, defId);
						filterTerm.addAttribute(FilterElementName, name);
						for (int j = 0; j < value.length; j++) {
							Element newTerm = filterTerm.addElement(FilterElementValue);
							newTerm.setText(value[j]);
						}
					}
				} else if (filterType.equals(FilterTypeWorkflow)) {
					//Get the workflow definition and state
					String defId = PortletRequestUtils.getStringParameter(request, FilterWorkflowDefIdField + String.valueOf(i), "");
					String[] states = (String[])formData.get(FilterWorkflowStateNameField + String.valueOf(i));
					if (!defId.equals("") && states != null && states.length > 0) {
						Element filterTerm = filterTerms.addElement(FilterTerm);
						filterTerm.addAttribute(FilterType, filterType);
						filterTerm.addAttribute(FilterWorkflowDefId, defId);
						for (int i2 = 0; i2 < states.length; i2++) {
							Element newTerm = filterTerm.addElement(FilterWorkflowStateName);
							newTerm.setText(states[i2]);
						}
					}
				}
			}
		}
		//searchFilter.asXML();
		return searchFilter;
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
    		}
    	}
   		
   		return searchFilterData;
   	}
	
	//Routine to convert a search filter into the form that Lucene wants 
   	static public Document convertSearchFilterToSearchBoolean(Document searchFilter) {
		//Build the search query
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element qTreeBoolElement = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
    	Element andElement = qTreeBoolElement;
    	
    	Element sfRootElement = searchFilter.getRootElement();

    	//Add the filter terms to the boolean query
    	List liFilterTerms = sfRootElement.selectNodes(FilterTerms + "/" + FilterTerm);
		Element orField = qTreeBoolElement;
		if (liFilterTerms.size() > 1) orField = qTreeBoolElement.addElement(QueryBuilder.OR_ELEMENT);
    	for (int i = 0; i < liFilterTerms.size(); i++) {
    		Element filterTerm = (Element) liFilterTerms.get(i);
    		String filterType = filterTerm.attributeValue(FilterType, "");
    		if (filterType.equals(FilterTypeSearchText)) {
    			//Add the search text as a field
    			Element field = orField.addElement(QueryBuilder.FIELD_ELEMENT);
    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.ALL_TEXT_FIELD);
    	    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	    	child.setText(filterTerm.getText());
    		} else if (filterType.equals(FilterTypeEntry)) {
    			//This is an entry term. Build booleans from the element name and values.
    			String defId = filterTerm.attributeValue(FilterHelper.FilterEntryDefId, "");
    			String elementName = filterTerm.attributeValue(FilterHelper.FilterElementName, "");
    			Iterator itTermValues = filterTerm.selectNodes(FilterHelper.FilterElementValue).iterator();
    			while (itTermValues.hasNext()) {
    				String value = ((Element) itTermValues.next()).getText();
    				if (!value.equals("")) {
    					Element field;
    					Element child;
    					Element andField = orField;
    	    			if (!defId.equals("")) {
    	    				andField = orField.addElement(QueryBuilder.AND_ELEMENT);
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
    		} else if (filterType.equals(FilterTypeWorkflow)) {
    			//This is a workflow state term. Build booleans from the state name.
    			String defId = filterTerm.attributeValue(FilterHelper.FilterWorkflowDefId, "");
				Element field;
				Element child;
				Element andField = orField;
    			if (!defId.equals("")) {
    				andField = orField.addElement(QueryBuilder.AND_ELEMENT);
	    			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
	    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.WORKFLOW_PROCESS_FIELD);
	    	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	    	child.setText(defId);
    			}
    			
    	    	//Add an OR field with all of the desired states
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
    	//qTree.asXML();
    	return qTree;
	}
   	//Routine to convert a search filter into a Lucene query for People (People, Places, and Things) 
   	static public Document convertSearchFilterToPeopleSearchBoolean(Document searchFilter) {
   		//Build the search query
   		Document qTree = DocumentHelper.createDocument();
   		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
   		Element qTreeBoolElement = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
   		Element andElement = qTreeBoolElement;
   		
   		Element sfRootElement = searchFilter.getRootElement();
   		
   		//Add the filter terms to the boolean query
   		List liFilterTerms = sfRootElement.selectNodes(FilterTerms + "/" + FilterTerm);
   		for (int i = 0; i < liFilterTerms.size(); i++) {
   			Element filterTerm = (Element) liFilterTerms.get(i);
   			String filterType = filterTerm.attributeValue(FilterType, "");
   			if (filterType.equals(FilterTypeSearchText)) {
   				//Add the search text as a field
   				Element field = andElement.addElement(QueryBuilder.FIELD_ELEMENT);
   				field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.ALL_TEXT_FIELD);
   				String value = filterTerm.getText();
   				if (value.contains("*"))
   					field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "false");
   				else
   					field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
   				Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
   				child.setText(value);
   			}
   			
   	   		Element gFilterTerm = (Element)sfRootElement.selectSingleNode(QueryBuilder.GROUP_VISIBILITY_ELEMENT);
    		if (gFilterTerm != null) {
    			Element viz = andElement.addElement(QueryBuilder.GROUP_VISIBILITY_ELEMENT);
    			viz.addAttribute(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE, gFilterTerm.attributeValue(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE));
    		}
    		
    		// Add entrytype=user|group boolean term
    		Element orElement = andElement.addElement(QueryBuilder.OR_ELEMENT);
    		
    		Element field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(EntityIndexUtils.ENTRY_TYPE_USER);
	    	
	    	field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(EntityIndexUtils.ENTRY_TYPE_GROUP);
   		}
   		//qTree.asXML();
   		return qTree;
   	}
   	
   	
	
	static public String getFilterName(Document searchFilter) {
		Element sfRoot = searchFilter.getRootElement();
		Element filterName = sfRoot.element(FilterName);
		return filterName.getText();
	}

}