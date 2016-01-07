<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<style type="text/css">
        @import "<html:rootPath />js/dojo/dijit/themes/tundra/tundra.css";
        @import "<html:rootPath />js/dojo/dojo/resources/dojo.css";
</style>

<script type="text/javascript">

	var ss_user_locale = "${ssUser.locale.language}";
	var ss_searchBinderUrl = "<ssf:url adapter="true" portletName="ss_forum" actionUrl="true" ><ssf:param 
		name="binderId" value="${ssBinder.id}"/></ssf:url>";
		
	dojo.addOnLoad(function() {
			dojo.addClass(document.body, "tundra");
			ss_loadSearchOptions("${ssNamespace}")
		}
	);
</script>

<c:choose>
<c:when test="${empty ssTotalRecords || ssShowAdvancedSearchForm}">
	<div id="ss_searchForm_container" class="tundra">
</c:when>	
<c:otherwise>
    <div id="ss_searchForm_changeBox">
		<%@ include file="/WEB-INF/jsp/search/filterSummary.jsp" %>
    </div>
	<div id="ss_searchForm_container" class="tundra" style="display:none;">
</c:otherwise>
</c:choose>	
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<c:if test="${!filterDefinition}">
					<div class="n-buttonright"><ssf:showHelp guideName="user" pageId="informed_search" sectionId="informed_search_advanced" /></div>
				</c:if>
				<div class="ss_clear"></div>

				  <c:if test="${!empty ss_searchError}">
				    <div style="padding:20px;">
				      <span class="ss_errorLabel ss_bold ss_largeprint">${ss_searchError}</span>
				    </div>
				  </c:if>

				<table>
					<tr>
						<th class="ss_nowrap ss_size_15px ss_bold" style="vertical-align: middle;">
						  <c:if test="${filterDefinition }">
						    <ssf:nlt tag="filter.searchText"/>
						  </c:if>
						  <c:if test="${!filterDefinition }">
						    <ssf:nlt tag="searchForm.advanced.Title"/>
						  </c:if>
						</th>
						<td class="ss_nowrap" colspan="2" width="100%">
						  <input type="text" name="searchText" 
						    id="searchText_adv" 
						    value="<ssf:escapeQuotes>${ss_filterMap.searchText}</ssf:escapeQuotes>" 
						    <c:if test="${empty disableSearchButton || disableSearchButton == 0}">
						      onkeypress="return ss_submitViaEnter(event)"
						    </c:if>/>
						</td>
					</tr>
					
					<c:if test="${!filterDefinition }">
					<tr>
						<td></td>
						<td colspan="2">
						  <input type="hidden" name="context" value="${ss_searchContext}" />
						  <table cellspacing="0" cellpadding="0" style="padding-bottom:16px;" width="100%">
						    <tr>
						      <td width="20">
								<input type="radio" name="scope" value="all" 
								<c:if test="${ss_searchScope == 'all'}"> checked="checked" </c:if>
								style="width:20px;">
							  </td>
							  <td nowrap>
								<ssf:nlt tag="search.scope.all"/>
							  </td>
							</tr>
						    <tr>
						      <td width="20">
								<input type="radio" name="scope" value="myFiles"
								  <c:if test="${ss_searchScope == 'myFiles'}"> checked="checked" </c:if>
								  style="width:20px;">
							  </td>
							  <td nowrap>
								<ssf:nlt tag="search.scope.myFiles"/>
							  </td>
							</tr>
							<ssf:ifFilr>
						    <tr>
						      <td width="20">
								<input type="radio" name="scope" value="netFolders" 
								<c:if test="${ss_searchScope == 'netFolders'}"> checked="checked" </c:if>
								style="width:20px;">
							  </td>
							  <td>
								<ssf:nlt tag="search.scope.netFolders"/>
							  </td>
							</tr>
							</ssf:ifFilr>
						    <tr>
						      <td width="20">
								<input type="radio" name="scope" value="sharedWithMe" 
								<c:if test="${ss_searchScope == 'sharedWithMe'}"> checked="checked" </c:if>
								style="width:20px;">
							  </td>
							  <td>
								<ssf:nlt tag="search.scope.sharedWithMe"/>
							  </td>
							</tr>
						    <tr>
						      <td width="20">
								<input type="radio" name="scope" value="sharedByMe" 
								<c:if test="${ss_searchScope == 'sharedByMe'}"> checked="checked" </c:if>
								style="width:20px;">
							  </td>
							  <td>
								<ssf:nlt tag="search.scope.sharedByMe"/>
							  </td>
							</tr>
						    <c:if test="${ss_searchContext == 'binder' || scope == 'current'}">
						     <tr>
						      <td width="20" valign="top">
								<input type="radio" name="scope" value="current" id="search_scope_current"
								  <c:if test="${ss_searchScope == 'current'}"> checked="checked" </c:if>
								  style="width:20px;"
								>
								<input type="hidden" name="contextBinderId" id="contextBinderId" value="${ss_searchContextBinderId}" />
							  </td>
							  <td>
							    <c:if test="${!empty ss_searchContextBinder}">
									<ssf:nlt tag="search.scope.current"/>&nbsp;
									<a href="<ssf:url action="view_folder_listing" 
										binderId="${ss_searchContextBinderId}" />"
										title="${ss_searchContextBinder.pathName}"
										onClick="ss_openUrlInWorkarea(this.href, '${ss_searchContextBinderId}', 'view_folder_listing');return false;"
									>
									  <span>${ss_searchContextBinder.title}</span>
									</a>
								</c:if>
							  </td>
							 </tr>
							 <tr>
						      <td width="20" valign="top">&nbsp;</td>
							  <td align="left" nowrap>
							    <c:if test="${ss_searchIncludeNestedBinders}">
							      <input type="checkbox" 
							        name="includeNestedBinders" 
							        checked />
							    </c:if>
							    <c:if test="${!ss_searchIncludeNestedBinders}">
							      <input type="checkbox" name="includeNestedBinders" /> 
							    </c:if>
							    <span>&nbsp;<ssf:nlt tag="search.scope.includeSubBinders"/></span>
							  </td>
							 </tr>
							</c:if>
						  </table>
						</td>
					</tr>
					</c:if>

					<c:if test="${!filterDefinition}">
					<tr>
						<td class="ss_nowrap" style="vertical-align: top; padding-top: 5px; text-align: right;" >
							<c:if test="${!filterDefinition}"><ssf:nlt tag="searchForm.searchFolders"/>:</c:if>
						</td>
						<td class="ss_nowrap" style="padding-right: 25px;">
							<c:if test="${!filterDefinition}">
								<ul>
									<c:if test="${empty ssFolderList && not empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssFolderList}">
										<c:set var="ssFolderList" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssFolderList}" />
									</c:if>
									<c:forEach var="folder" items="${ssFolderList}">
									    <li>${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span></li>
									</c:forEach>
								</ul>
							
								<c:if test="${activateDashboardFolder}">
									<input type="radio" onchange="ss_searchToggleFolders('ss_foldersTree_${ssNamespace}', 'dashboard');" 
											name="search_folderType" value="dashboard" id="search_currentFolder" style="width: 19px; margin: 0; padding: 0; "
											<c:if test="${ss_filterMap.search_currentFolder}">
												checked="true"
											</c:if>
											/> <label for="search_currentFolder"><ssf:nlt tag="searchForm.searchCurrentFolder"/></label>
								 
									<div>
										<input type="radio" onchange="ss_searchToggleFolders('ss_foldersTree_${ssNamespace}', 'foldersTree');" name="search_folderType" value="selected" id="search_selectedFolders" style="width: 19px; margin: 0; padding: 0; "
											<c:if test="${!ss_filterMap.search_currentFolder}">
													checked="true"
											</c:if>
											/> <label for="search_selectedFolders"><ssf:nlt tag="searchForm.searchSelectedFolders"/></label>
									</div>
								
								<input type="hidden" name="search_dashboardFolders" id="search_dashboardFolders" value="${ssBinder.id}"/>
								</c:if>
								
								<c:if test="${!activateDashboardFolder && !empty ssBinder}">
								  <div style="padding-bottom:2px;">
								 	<input type="checkbox" name="searchFolders${ssBinder.id}" id="search_currentAndSubfolders" 
								 	  style="width: 19px; margin: 0; padding: 0; " 
								 	  onClick="ss_searchSetCheckbox(this, 'search_subfolders');"
								 		<c:if test="${ss_filterMap.search_currentAndSubfolders}">
								 			checked="checked"
								 		</c:if>
								 	> <label for="search_currentAndSubfolders">
								 	<c:if test="${ssBinder.entityType != 'folder'}">
								 	  <ssf:nlt tag="move.currentWorkspace"/> (${ssBinder.title})
								 	</c:if>
								 	<c:if test="${ssBinder.entityType == 'folder'}">
								 	  <ssf:nlt tag="move.currentFolder"/> (${ssBinder.title})
								 	</c:if>
								 	</label>
								  </div>
								</c:if>
								
								<c:if test="${!empty ssDomTree}">
								<c:choose>
									<c:when test="${!empty ss_filterMap.searchFolders}">
										<c:set var="folderIds" value="${ss_filterMap.searchFolders}" />
									</c:when>
									<c:otherwise>
										<c:set var="folderIds" value="<%= new ArrayList() %>" />
									</c:otherwise>
								</c:choose>
	
								<div 
									<c:if test="${activateDashboardFolder}">
										id="ss_foldersTree_${ssNamespace}" style="padding-left: 24px; padding-top: 6px; "
									</c:if>
								>
								    <ssf:tree 
									  treeName="t_searchForm_wsTree"
									  treeDocument="${ssDomTree}"  
									  rootOpen="false" 
									  multiSelect="${folderIds}" 
									  multiSelectPrefix="searchFolders"
									  showIdRoutine="t_advSearchForm_wsTree_showId"/>
								
								</div>
	
								<div class="ss_additionals">
								 	<input type="checkbox" name="search_subfolders" id="search_subfolders" value="true" style="width: 19px; margin: 0; padding: 0; " 
								 		<c:if test="${ss_filterMap.search_subfolders}">
								 			checked="checked"
								 		</c:if>
								 	> <label for="search_subfolders"><ssf:nlt tag="searchForm.searchSubfolders"/></label>
								</div>
								</c:if>
							</c:if>
							<div class="ss_additionals">
							 	<input type="checkbox" name="ss_searchPreDeletedOnly" id="ss_searchPreDeletedOnly" 
							 	  value="true" style="width: 19px; margin: 0; padding: 0; " 
							 		<c:if test="${ss_searchPreDeletedOnly}">
							 			checked="checked"
							 		</c:if>
							 	> <label for="ss_searchPreDeletedOnly"><ssf:nlt tag="searchForm.search_preDeletedOnly"/></label>
							</div>
 						</td>
						<td class="ss_nowrap" style="vertical-align: top; padding-top: 5px;" width="100%">
						    <div class="ss_bold"><ssf:nlt tag="searchForm.advanced.presentationOptions"/></div>
							<table class="margintop1">
								<tr>
									<td nowrap>
										<label for="data_resultsCount">
											<ssf:nlt tag="searchForm.advanced.options.limitResults"/>:
										</label>
									</td>
									<td width="100%">
										<select class="ss_compactSelectBox" name="data_resultsCount" id="data_resultsCount">
											<option value="1" <c:if test="${resultsCount == 1}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems.single"/></option>
											<option value="5" <c:if test="${resultsCount == 5}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="5"/></ssf:nlt></option>
											<option value="10" <c:if test="${resultsCount == 10}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="10"/></ssf:nlt></option>
											<option value="25" <c:if test="${resultsCount == 25}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="25"/></ssf:nlt></option>
											<option value="50" <c:if test="${resultsCount == 50}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="50"/></ssf:nlt></option>
											<option value="100" <c:if test="${resultsCount == 100}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="100"/></ssf:nlt></option>
											<c:if test="${resultsCount != 1 || resultsCount != 5 || resultsCount != 10 || resultsCount != 25 || resultsCount != 50 || resultsCount != 100}">
												<option value="${resultsCount}" selected="selected"><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="${resultsCount}"/></ssf:nlt></option>
											</c:if>
										</select>
									</td>
								</tr>	
									<td nowrap>
										<label for="data_summaryWordCount">
											<ssf:nlt tag="searchForm.advanced.options.limitWords"/>: 
										</label>
									</td>
									<td width="100%">
										<select class="ss_compactSelectBox" name="data_summaryWordCount" id="data_summaryWordCount">
											<option value="15" <c:if test="${summaryWordCount == 15}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="15"/></ssf:nlt></option>
											<option value="20" <c:if test="${summaryWordCount == 20}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="20"/></ssf:nlt></option>
											<option value="30" <c:if test="${summaryWordCount == 30}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="30"/></ssf:nlt></option>
											<option value="50" <c:if test="${summaryWordCount == 50}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="50"/></ssf:nlt></option>
											<option value="100" <c:if test="${summaryWordCount == 100}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="100"/></ssf:nlt></option>
										</select>
									</td>
								</tr>
								<tr>
									<td nowrap>
										<label for="data_sortBy">
											<ssf:nlt tag="searchForm.advanced.options.sortBy"/> 
										</label>
									</td>
									<td width="100%">
										<select class="ss_compactSelectBox" name="data_sortBy" id="data_sortBy">
											<option value="sortByRelevance" <c:if test="${sortBy == 'sortByRelevance'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.relevance"></ssf:nlt></option>
											<option value="sortByDate" <c:if test="${sortBy == 'sortByDate'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.date"></ssf:nlt></option>
											<ssf:ifNotFilr><option value="sortByRating" <c:if test="${sortBy == 'sortByRating'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.rating"></ssf:nlt></option></ssf:ifNotFilr>
											<option value="sortByReplyCount" <c:if test="${sortBy == 'sortByReplyCount'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.replyCount"></ssf:nlt></option>
										</select>
									</td>
								</tr>
								<tr>
									<td nowrap>
										<label for="data_sortBy_secondary">
											<ssf:nlt tag="searchForm.advanced.options.sortBySecondary"/> 
										</label>
									</td>
									<td width="100%">
										<select class="ss_compactSelectBox" name="data_sortBy_secondary" id="data_sortBy_secondary">
											<option value="sortByRelevance" <c:if test="${sortBySecondary == 'sortByRelevance'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.relevance"></ssf:nlt></option>
											<option value="sortByDate" <c:if test="${sortBySecondary == 'sortByDate'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.date"></ssf:nlt></option>
											<ssf:ifNotFilr><option value="sortByRating" <c:if test="${sortBySecondary == 'sortByRating'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.rating"></ssf:nlt></option></ssf:ifNotFilr>
											<option value="sortByReplyCount" <c:if test="${sortBySecondary == 'sortByReplyCount'}">selected="selected"</c:if>><ssf:nlt tag="searchForm.advanced.options.sortBy.replyCount"></ssf:nlt></option>
										</select>
									</td>
								</tr>
							</table>		
						</td>
					</tr>
					</c:if>
					
					<c:if test="${!filterDefinition}">
						<tr>
							<td></td>
							<td colspan="2" 
							  style="padding-bottom: 10px; padding-top: 15px;">
								<c:if test="${empty disableSearchButton || disableSearchButton == 0}">
								<a class="ss_tinyButton ss_bold" href="javascript: ss_search();" ><ssf:nlt tag="searchForm.button.label"/></a> 
								</c:if>
							</td>
						</tr>
					</c:if>
					
				</table>
			</div>
		</div>

		<div id="ss_searchForm_additionalFilters" style="padding:4px;">
			<input type="hidden" id="ssSearchParseAdvancedForm${ssNamespace}" name="ssSearchParseAdvancedForm" value="false" />
			<table>
			  <tbody>
			    <tr>
			      <td class="ss_fixed_TD" style="padding-top: 5px;"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.Author"/></div></td>
			      <td class="ss_fixed_TD">
	 			    <div id="ss_authors_container">			
	  				  <div id="ss_authors_options" class="ss_options"></div>
					  <div class="margintop1">
					    <a href="javascript: ;" onClick="ss_addOption('creator_by_id');" class="ss_tinyButton" title="<ssf:nlt tag="searchForm.filterButton.addAuthor"/>"><ssf:nlt tag="searchForm.filterButton.add"/></a>
				      </div>
					</div>
				  </td>
			    </tr>
			  <ssf:ifNotFilr>
			    <tr>
			      <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.Tag"/></div></td>
			      <td class="ss_fixed_TD">
					<div id="ss_tags_container">	
						<div id="ss_tags_options" class="ss_options"></div>
						<div class="margintop1">
							<a href="javascript: ;" onClick="ss_addOption('tag');" class="ss_tinyButton" title="<ssf:nlt tag="searchForm.filterButton.addTag"/>"><ssf:nlt tag="searchForm.filterButton.add"/></a>
						</div>
					</div>	
			      </td>
			    </tr>
			  </ssf:ifNotFilr>
			  <ssf:ifNotFilr>
			    <tr>
			      <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.Workflow"/></div></td>
			      <td class="ss_fixed_TD">
					<div id="ss_workflows_container">	
						<div id="ss_workflows_options" class="ss_options"></div>
						<div class="margintop1">
							<a href="javascript: ;" onClick="ss_addOption('workflow');" class="ss_tinyButton" title="<ssf:nlt tag="searchForm.filterButton.addWorkflow"/>"><ssf:nlt tag="searchForm.filterButton.add"/></a>
						</div>
					</div>			      
			      </td>
			    </tr>
			  </ssf:ifNotFilr>
			  <ssf:ifNotFilr>
				<tr>
				  <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.Entry"/></div></td>
				  <td class="ss_fixed_TD">
					<div id="ss_entries_container">
						<div id="ss_entries_options" class="ss_options"></div>
						<div class="margintop1" style="clear: left;">
							<a href="javascript: ;" onClick="ss_addOption('entry');" class="ss_tinyButton" title="<ssf:nlt tag="searchForm.filterButton.addField"/>"><ssf:nlt tag="searchForm.filterButton.add"/></a>				
						</div>
					</div>				  
					<c:if test="${filterDefinition }">
					    <input type="hidden" id="search_scope_current_filter" name="search_scope_current_filter" value="current" />
					</c:if>
				  </td>
				</tr>
			  </ssf:ifNotFilr>
				<tr>
				  <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.LastActivity"/></div></td>
				  <td class="ss_fixed_TD">
					<div id="ss_lastActivities_container">				
						<div id="ss_lastActivities_options" class="ss_options"></div>
					</div>
				  </td>
				</tr>
				<tr>
				  <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.CreationDate"/></div></td>
				  <td class="ss_fixed_TD">
					<div id="ss_creationDates_container">				
						<div id="ss_creationDates_options" class="ss_options"></div>
						<div class="margintop1">
							<a href="javascript: ;" onClick="ss_addOption('creation_date');" class="ss_tinyButton" title="<ssf:nlt tag="searchForm.filterButton.addDate"/>"><ssf:nlt tag="searchForm.filterButton.add"/></a>
						</div>
					</div>
				  </td>
				</tr>
				<tr>
				  <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.ModificationDate"/></div></td>
				  <td class="ss_fixed_TD">
					<div id="ss_modificationDates_container">
						<div id="ss_modificationDates_options" class="ss_options"></div>
						<div class="margintop1">
							<a href="javascript: ;" onClick="ss_addOption('modification_date');" class="ss_tinyButton" title="<ssf:nlt tag="searchForm.filterButton.addDate"/>"><ssf:nlt tag="searchForm.filterButton.add"/></a>
						</div>
					</div>
				  </td>
				</tr>
			<c:if test="${!filterDefinition}">
				<tr>
				  <td class="ss_fixed_TD"><div class="ss_sectionTitle ss_bold"><ssf:nlt tag="searchForm.sectionTitle.ItemType"/></div></td>
				  <td class="ss_fixed_TD">
						<div id="ss_itemTypes_container">					
							<div id="ss_itemType_options" class="ss_options">
								<input type="checkbox" name="searchItemType" value="workspace" id="ss_itemType_workspace"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.workspace}">
										checked="true"
									</c:if>
								/>&nbsp;<span style="padding-right: 10px;"><label for="ss_itemType_workspace"><ssf:nlt tag="searchForm.itemType.workspace"/></label></span>
								<input type="checkbox" name="searchItemType" value="folder" id="ss_itemType_folder"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.folder}">
										checked="true"
									</c:if>
								/>&nbsp;<span style="padding-right: 10px;"><label for="ss_itemType_folder"><ssf:nlt tag="searchForm.itemType.folder"/></label></span>
								<input type="checkbox" name="searchItemType" value="user" id="ss_itemType_user"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.user}">
										checked="true"
									</c:if>					
								/>&nbsp;<span style="padding-right: 10px;"><label for="ss_itemType_user"><ssf:nlt tag="searchForm.itemType.user"/></label></span>
								<input type="checkbox" name="searchItemType" value="attachment" id="ss_itemType_attachment"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.attachment}">
										checked="true"
									</c:if>						
								/>&nbsp;<span style="padding-right: 10px;"><label for="ss_itemType_attachment"><ssf:nlt tag="searchForm.itemType.attachment"/></label></span>
								<input type="checkbox" name="searchItemType" value="entry" id="ss_itemType_entry"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.entry}">
										checked="true"
									</c:if>						
								/>&nbsp;<span style="padding-right: 10px;"><label for="ss_itemType_entry"><ssf:nlt tag="searchForm.itemType.entry"/></label></span>
								<input type="checkbox" name="searchItemType" value="reply" id="ss_itemType_reply"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.reply}">
										checked="true"
									</c:if>						
								/>&nbsp;<span style="padding-right: 10px;"><label for="ss_itemType_reply"><ssf:nlt tag="searchForm.itemType.reply"/></label></span>
							</div>
						</div>
				  </td>
				</tr>
			</c:if>			
			  </tbody>
			</table>
			
			<c:if test="${empty disableSearchButton || disableSearchButton == 0}">
			<div class="ss_searchFormFooter" style="text-align: center; padding: 10px;">
					<a class="ss_tinyButton ss_bold" href="javascript: ss_search();" ><img <ssf:alt tag="alt.search"/> 
					  src="<html:imagesPath/>pics/1pix.gif" /> <ssf:nlt tag="searchForm.button.label"/></a> 	
			</div>
			</c:if>
		</div>
		
		<c:if test="${!empty ss_filterMap.additionalFilters && !filterDefinition}">
		<div id="ss_searchForm_filterSummary" style="visibility:visible; display: block;">
			<!-- Summary of user filters -->
			<%@ include file="/WEB-INF/jsp/search/filterSummary.jsp" %>
		</div>
		</c:if>

		<div id="ss_buttonBar">
			<input type="hidden" name="operation" value="ss_searchResults"/>
			<input type="hidden" name="searchNumbers" id="searchNumbers" value=""/>		
			<input type="hidden" name="searchTypes" id="searchTypes" value=""/>
		</div>
	</div>

