package com.sitescape.team.search.filter;

import java.util.Collection;

import javax.portlet.PortletRequest;

import org.dom4j.Document;

import com.sitescape.team.web.util.PortletRequestUtils;

/**
 * Class simpliefied building some common search filters.
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class SearchFiltersBuilder {

	// Routine to parse the results of submitting the blog summary dashboard
	// config form
	public static Document buildFolderListQuery(PortletRequest request,
			Collection folderIds) {
		SearchFilter searchFilter = new SearchFilter();

		String filterName = PortletRequestUtils.getStringParameter(request,
				SearchFilterKeys.FilterNameField, "");
		searchFilter.addFilterName(filterName);

		for (Object id : folderIds) {
			searchFilter.addFolderId((String) id);
		}

		return searchFilter.getFilter();
	}
	
	public static Document buildGetEntryQuery(PortletRequest request,
			Long entryId) {
		SearchFilter searchFilter = new SearchFilter();

		String filterName = PortletRequestUtils.getStringParameter(request,
				SearchFilterKeys.FilterNameField, "");
		searchFilter.addFilterName(filterName);

		if (entryId != null) {
			searchFilter.addEntryId(entryId.toString());
		}
		
		return searchFilter.getFilter();
	}

}
