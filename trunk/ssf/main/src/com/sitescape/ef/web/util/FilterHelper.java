package com.sitescape.ef.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.web.WebKeys;

/*********************************************************************
 * Object to hold a named search filter
 * @author Peter Hurley
 *
 */
public class FilterHelper {   	
   	//Search filter document element names
   	private final static String FilterRootName = "search_filter";
   	private final static String FilterName = "filterName";
   	private final static String FilterTerms = "filterTerms";
   	private final static String FilterTerm = "filterTerm";
   	private final static String FilterType = "filterType";
   	private final static String FilterEntryDefId = "filterEntryDefId";
   	private final static String FilterElementName = "filterElementName";
   	private final static String FilterElementValue = "filterElementValue";

   	//formData fields and values
   	private final static String FilterNameField = "filterName";
   	private final static String FilterTypeField = "filterType";
   	private final static String FilterTypeSearchText = "text";
   	private final static String FilterTypeEntry = "entry";
   	private final static String FilterTypeWorkflow = "workflow";
   	
   	private final static String FilterEntryDefIdField = "ss_filter_entry_def_id";
   	private final static String FilterElementNameField = "elementName";
   	private final static String FilterElementValueField = "elementValue";
	
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
						filterTerm.addAttribute(FilterEntryDefId, defId);
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
	
	static public String getFilterName(Document searchFilter) {
		Element sfRoot = searchFilter.getRootElement();
		Element filterName = sfRoot.element(FilterName);
		return filterName.getText();
	}

}