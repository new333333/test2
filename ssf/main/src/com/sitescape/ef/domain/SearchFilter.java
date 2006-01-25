package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.web.WebKeys;

/*********************************************************************
 * Object to hold a named search filter
 * @author Peter Hurley
 *
 */
public class SearchFilter {
   	private Map searchFilter;
   	
   	//Search filter map index values
   	private final static String FilterName = "filterName";
   	private final static String FilterTerms = "filterTerms";
   	private final static String FilterType = "filterType";
   	private final static String FilterValue = "filterValue";
   	private final static String FilterEntryDefId = "filterEntryDefId";
   	private final static String FilterElementName = "filterElementName";
   	private final static String FilterElementValue = "filterElementValue";

   	//formData fields snd values
   	private final static String FilterNameField = "filterName";
   	private final static String FilterTypeField = "filterType";
   	private final static String FilterTypeSearchText = "text";
   	private final static String FilterTypeEntry = "entry";
   	private final static String FilterTypeWorkflow = "workflow";
   	
   	private final static String FilterEntryDefIdField = "ss_filter_entry_def_id";
   	private final static String FilterElementNameField = "elementName";
   	private final static String FilterElementValueField = "elementValue";
	
	public SearchFilter (Map formData) {
		this.searchFilter = new HashMap();
		
		String filterName = ((String[])formData.get(FilterNameField))[0];
		if (filterName == null || filterName.equals("")) {
			//Throw an error?
			return;
		}
		this.searchFilter.put(FilterName, filterName);
		List filterTerms = new ArrayList();
		this.searchFilter.put(FilterTerms, filterTerms);
		
		//Get the terms out of the formData
		Integer maxTermNumber = new Integer(((String[])formData.get(WebKeys.FILTER_ENTRY_FILTER_TERM_NUMBER))[0]);
		for (int i = 1; i <= maxTermNumber.intValue(); i++) {
			if (formData.containsKey(FilterTypeField + String.valueOf(i))) {
				//Found a possible term
				String filterType = ((String[])formData.get(FilterTypeField + String.valueOf(i)))[0];
				if (filterType.equals(FilterTypeSearchText)) {
					//Get the search text
					if (formData.containsKey(FilterElementValueField + String.valueOf(i))) {
						String searchText = ((String[])formData.get(FilterElementValueField + String.valueOf(i)))[0];
						Map term = new HashMap();
						term.put(FilterType, filterType);
						term.put(FilterValue, searchText);
						filterTerms.add(term);
					}
				} else if (filterType.equals(FilterTypeEntry)) {
					//Get the entry definition, element and value
					if (formData.containsKey(FilterEntryDefIdField + String.valueOf(i)) && 
							formData.containsKey(FilterElementNameField + String.valueOf(i)) && 
							formData.containsKey(FilterElementValueField + String.valueOf(i))) {
						String defId = ((String[])formData.get(FilterEntryDefIdField + String.valueOf(i)))[0];
						String name = ((String[])formData.get(FilterElementNameField + String.valueOf(i)))[0];
						String[] value = (String[])formData.get(FilterElementValueField + String.valueOf(i));
						Map term = new HashMap();
						term.put(FilterType, filterType);
						term.put(FilterEntryDefId, defId);
						term.put(FilterElementName, name);
						term.put(FilterElementValue, value);
						filterTerms.add(term);
					}
				} else if (filterType.equals(FilterTypeWorkflow)) {
					
				}
			}
		}
	}

	public String getFilterName() {
		return (String) this.searchFilter.get(FilterName);
	}

}