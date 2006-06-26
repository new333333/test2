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
import com.sitescape.ef.module.shared.EntryIndexUtils;
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
   	
   	//formData fields and values
   	private final static String FilterNameField = "filterName";
   	private final static String FilterTypeField = "filterType";
   	
   	public final static String FilterEntryDefIdField = "ss_entry_def_id";
   	public final static String FilterEntryDefIdCaptionField = "ss_entry_def_id_caption";
   	public final static String FilterElementNameField = "elementName";
   	public final static String FilterElementNameCaptionField = "elementNameCaption";
   	public final static String FilterElementValueField = "elementValue";
   	public final static String FilterElementValueTypeField = "elementValueType";
	
	//Routine to parse the results of submitting the filter builder form
   	static public Document getSearchFilter (PortletRequest request) throws Exception {
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);
		Map formData = request.getParameterMap();

		String filterName = PortletRequestUtils.getRequiredStringParameter(request, FilterNameField);
		Element filterNameEle = sfRoot.addElement(FilterName);
		filterNameEle.setText(filterName);
		
		Element filterTerms = sfRoot.addElement(FilterTerms);
		
		//Get the terms out of the formData
		Integer maxTermNumber = new Integer(PortletRequestUtils.getRequiredStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER_MAX));
		for (int i = 1; i <= maxTermNumber.intValue(); i++) {
			String filterType = PortletRequestUtils.getStringParameter(request, FilterTypeField + String.valueOf(i));
			if (!filterType.equals("")) {
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
					
				}
			}
		}
		//searchFilter.asXML();
		return searchFilter;
	}
   	
   	static public Map buildFilterFormMap(Document searchFilter, Map entryDefs, Map commonElements) {
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
    				if (entryDefs.containsKey(defId)) {
    					def = (Definition)entryDefs.get(defId);
    					defIdCaption = def.getTitle();
    					Document defDoc = def.getDefinition();
    					Element item = (Element)defDoc.getRootElement().selectSingleNode("//item/properties/property[@name='name' and @value='"+elementName+"']");
    					if (item != null) {
    						Element captionEle = (Element) item.selectSingleNode("../property[@name='caption']");
    						if (captionEle != null) elementNameCaption = NLT.getDef(captionEle.attributeValue("value", elementName));
    					}
    				}
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
    			//TODO Add the workflow boolean term
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
        	    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntryIndexUtils.COMMAND_DEFINITION_FIELD);
        	    	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	    	    	child.setText(defId);
    	    			}
    	    			
    	    	    	field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
    	    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, elementName);
    	    	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	    	    	child.setText(value);
    				}
    			}
    		} else if (filterType.equals(FilterTypeWorkflow)) {
    			//TODO Add the workflow boolean term
    		}
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