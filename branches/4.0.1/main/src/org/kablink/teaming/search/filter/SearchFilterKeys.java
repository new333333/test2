/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.search.filter;

/**
 * ?
 * 
 * @author ?
 */
public class SearchFilterKeys {

	//Search form field names
	public final static String SearchParseAdvancedForm = "ssSearchParseAdvancedForm";
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
	public final static String SearchTags = "searchTags";
	public final static String SearchFoldersType = "search_folderType";
	public final static String SearchFolders = "searchFolders";
	public final static String SearchSubfolders = "search_subfolders";
	public final static String SearchCurrentFolder = "search_currentFolder";
	public final static String SearchAdditionalFilters = "additionalFilters";
	public final static String SearchCaseSensitive = "search_caseSensitive";
	public final static String SearchPreDeletedOnly = "search_preDeletedOnly";
	
	public final static String SearchDaysNumber = "searchDaysNumber";
	public final static String SearchItemType = "searchItemType";
	
//   	//Search filter document element names
	public final static String FilterCreatorTitle = "creatorTitle";	
   	public final static String FilterRootName = "searchFilter";
   	public final static String FilterName = "filterName";
   	public final static String FilterGlobal = "filterGlobal";
	
	
//	//formData fields and values
	public final static String FilterNameField = "filterName";
	public final static String FilterGlobalField = "global";
	public final static String FilterGlobalFieldIsGlobal = "true";
//	
	public final static String FilterEntryDefLength = "ss_entry_def_length_hidden";
	public final static String FilterEntryDefIdField = "ss_entry_def_id";
	public final static String FilterElementNameField = "elementName";
	public final static String FilterElementValueField = "elementValue";
	public final static String FilterElementValueValueField = "elementValueValue";
	public final static String FilterElementValueSet = "elementValueSet";
	
	
	public final static String FilterAnd = "filterTermsAnd";
	
   	public final static String FilterTerms = "filterTerms";
   	public final static String FilterTerm = "filterTerm";
   	public final static String FilterType = "filterType";
   	public final static String FilterRelativeType = "filterRelativeType";
   	public final static String FilterEntryDefId = "filterEntryDefId";
   	public final static String FilterElementName = "filterElementName";
   	public final static String FilterElementValue = "filterElementValue";
   	public final static String FilterElementValueType = "filterElementValueType";   	
   	public final static String FilterEntityType = "filterEntityType";
   	public final static String FilterEntryType = "filterEntryType";
   	public final static String FilterDocType = "filterDocType";
   	public final static String FilterFolderId = "filterFolderId";
   	public final static String FilterBinderId = "filterBinderId";
   	public final static String FilterEntryId = "filterEntryId";
   	public final static String FilterEventUid = "eventUid";
   	public final static String FilterTypeSearchText = "text";
   	public final static String FilterTypeCaseSensitive = "caseSensitive";
   	public final static String FilterTypePreDeletedOnly = "preDeletedOnly";
   	public final static String FilterTypeCreatorByName = "author";
   	public final static String FilterTypeCreatorById = "creatorById";
   	public final static String FilterTypeDate = "date";
   	public final static String FilterTypeTaskStatus = "taskStatus";
	public final static String FilterTypePlace = "place";
   	public final static String FilterTypeEvent = "event";
   	public final static String FilterTypeCommunityTagSearch = "communityTag";
   	public final static String FilterTypePersonalTagSearch = "personalTag";
   	public final static String FilterTypeBinderDefinition = "binder";
   	public final static String FilterTypeEntryDefinition = "entry";
   	public final static String FilterTypeEntryId = "entryId";
   	public final static String FilterTypeTaskEntry = "task";
   	public final static String FilterTypeTopEntry = "topEntry";
   	public final static String FilterTypeWorkflow = "workflow";
   	public final static String FilterTypeFolders = "folders";
   	public final static String FilterTypeEventUid = "eventsUid";
   	public final static String FilterTypeFoldersList = "foldersList";
   	public final static String FilterTypeAncestry = "ancestry";
   	public final static String FilterTypeAncestriesList = "ancestriesList";
   	public final static String FilterTypeBinderParent = "binderParent";
   	public final static String FilterTypeTags = "tags";
   	public final static String FilterTypeDocTypes = "docTypes";
   	public final static String FilterTypeEntityTypes = "entityTypes";
   	public final static String FilterTypeEntryTypes = "entryTypes";
   	public final static String FilterTypeElement = "element";
   	public final static String FilterTypeIsTeam = "isTeam";
   	public final static String FilterTypeRelative = "relative";
   	public final static String FilterTypeItemTypes = "itemTypes";
   	public final static String FilterStartDate = "startDate";
   	public final static String FilterEndDate = "endDate";
   	public final static String FilterWorkflowDefId = "filterWorkflowDefId";
   	public final static String FilterWorkflowStateName = "filterWorkflowStateName";
   	public final static String FilterEventDate = "filterEventDate";
   	public final static String FilterItemType = "filterItemType";
   	public final static String FilterTaskStatusName = "filterTaskStatusName";
   	public final static String FilterValueTypeBoolean = "boolean";
   	
   	public final static String FilterListType = "listType";
   	public final static String FilterListTypeUserGroupTeam = "userGroupTeam";
   	
   	public final static String CurrentUserId="CURRENT_USER_ID";
   	
   	public static final String MinimumSystemDate = "19000000000000";
   	public static final String MinimumSystemDay = "19000000";
   	public static final String MaximumSystemDate = "30000000000000";
   	public static final String MaximumSystemDay = "30000000";      	
}
