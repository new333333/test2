package com.sitescape.ef.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.module.profile.index.IndexUtils;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
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

   	//formData fields and values
   	private final static String FilterNameField = "filterName";
   	private final static String FilterTypeField = "filterType";
   	
   	public final static String FilterTypeSearchText = "text";
   	public final static String FilterTypeEntry = "entry";
   	public final static String FilterTypeWorkflow = "workflow";
   	
   	public final static String FilterEntryDefIdField = "ss_filter_entry_def_id";
   	public final static String FilterElementNameField = "elementName";
   	public final static String FilterElementValueField = "elementValue";
	
	//Routine to parse the results of submitting the filter builder form
   	static public Document getSearchFilter (PortletRequest request) throws Exception {
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterRootName);

		String filterName = PortletRequestUtils.getRequiredStringParameter(request, FilterNameField);
		Element filterNameEle = sfRoot.addElement(FilterName);
		filterNameEle.setText(filterName);
		
		Element filterTerms = sfRoot.addElement(FilterTerms);
		
		//Get the terms out of the formData
		Integer maxTermNumber = new Integer(PortletRequestUtils.getRequiredStringParameter(request, WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER));
		for (int i = 1; i <= maxTermNumber.intValue(); i++) {
			String filterType = PortletRequestUtils.getRequiredStringParameter(request, FilterTypeField + String.valueOf(i));
			if (!filterType.equals("")) {
				//Found a possible term
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