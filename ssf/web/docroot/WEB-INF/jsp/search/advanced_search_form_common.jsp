<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sitescape.team.util.CalendarHelper" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<style type="text/css">
        @import "<html:rootPath />js/dojo/dijit/themes/tundra/tundra.css";
        @import "<html:rootPath />js/dojo/dojo/resources/dojo.css"
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
<c:when test="${empty ssTotalRecords}">
	<div id="ss_searchForm_container" class="tundra">
</c:when>	
<c:otherwise>
    <div id="ss_searchForm_changeBox">
		<%@ include file="/WEB-INF/jsp/search/filterSummary.jsp" %>
        <p style="text-align: center;">
          <a class="ss_tinyButton" href="javascript: ss_showObjBlock('ss_searchForm_container'); ss_hideObj('ss_searchForm_changeBox');"><ssf:nlt tag="search.button.reviseAdvQuery"/></a>
        </p>
    </div>
	<div id="ss_searchForm_container" class="tundra" style="display:none;">
</c:otherwise>
</c:choose>	
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<c:if test="${!filterDefinition}">
					<h4><ssf:nlt tag="searchForm.advanced.Title"/> <ssf:inlineHelp tag="ihelp.other.advanced_search" /></h4>
				</c:if>
				<div class="ss_clear"></div>
				<table>
					<tr>
						<th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" id="searchText_adv" value="${ss_filterMap.searchText}" <c:if test="${empty disableSearchButton || disableSearchButton == 0}">onkeypress="return ss_submitViaEnter(event)"</c:if>/></td>
						<td rowspan="3">
						    <b><ssf:nlt tag="searchForm.advanced.presentationOptions"/></b><br/>
							<ssf:nlt tag="searchForm.advanced.options.limitResults"/>:
							<select class="ss_compactSelectBox" name="data_resultsCount" id="data_resultsCount">
								<option value="1" <c:if test="${resultsCount == 1}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems.single"/></option>
								<option value="5" <c:if test="${resultsCount == 5}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="5"/></ssf:nlt></option>
								<option value="10" <c:if test="${resultsCount == 10}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="10"/></ssf:nlt></option>
								<option value="25" <c:if test="${resultsCount == 25}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="25"/></ssf:nlt></option>
								<option value="50" <c:if test="${resultsCount == 50}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="50"/></ssf:nlt></option>
								<option value="100" <c:if test="${resultsCount == 100}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectItems"><ssf:param name="value" value="100"/></ssf:nlt></option>
							</select>
							<br/>
							<ssf:nlt tag="searchForm.advanced.options.limitWords"/>: 
							<select class="ss_compactSelectBox" name="data_summaryWordCount" id="data_summaryWordCount">
								<option value="15" <c:if test="${summaryWordCount == 15}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="15"/></ssf:nlt></option>
								<option value="20" <c:if test="${summaryWordCount == 20}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="20"/></ssf:nlt></option>
								<option value="30" <c:if test="${summaryWordCount == 30}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="30"/></ssf:nlt></option>
								<option value="50" <c:if test="${summaryWordCount == 50}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="50"/></ssf:nlt></option>
								<option value="100" <c:if test="${summaryWordCount == 100}">selected="selected"</c:if>><ssf:nlt tag="searchForm.results.selectWords"><ssf:param name="value" value="100"/></ssf:nlt></option>
							</select>
						</td>
					</tr>
					<c:if test="${!filterDefinition}">
						<tr>
							<th><ssf:nlt tag="searchForm.searchFolders"/>:</th>
							<td colspan="2">
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
								
									<div id="ss_foldersTree_${ssNamespace}" style="padding-left: 24px; padding-top: 6px; ">
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
								
								<c:choose>
									<c:when test="${!empty ss_filterMap.searchFolders}">
										<c:set var="folderIds" value="${ss_filterMap.searchFolders}" />
									</c:when>
									<c:otherwise>
										<c:set var="folderIds" value="<%= new ArrayList() %>" />
									</c:otherwise>
								</c:choose>
	
								<ssf:tree 
									  treeName="t_searchForm_wsTree"
									  treeDocument="${ssDomTree}"  
									  rootOpen="false" 
									  multiSelect="${folderIds}" 
									  multiSelectPrefix="searchFolders"
									 showIdRoutine="t_advSearchForm_wsTree_showId"/>
								
								<c:if test="${activateDashboardFolder}">
									</div>
								</c:if>
	
								<div class="ss_additionals">
								 	<input type="checkbox" name="search_subfolders" id="search_subfolders" value="true" style="width: 19px; margin: 0; padding: 0; " 
								 		<c:if test="${ss_filterMap.search_subfolders}">
								 			checked="checked"
								 		</c:if>
								 	> <label for="search_subfolders"><ssf:nlt tag="searchForm.searchSubfolders"/></label>
								</div>
	 						</td>
						</tr>
					</c:if>
					
					<c:if test="${!filterDefinition}">
						<tr>
							<td colspan="3" style="text-align: center; padding-bottom: 3px;">
								<c:if test="${empty disableSearchButton || disableSearchButton == 0}">
								<a class="ss_searchButton" href="javascript: ss_search();" ><img <ssf:alt tag="alt.search"/> 
					  				src="<html:imagesPath/>pics/1pix.gif" /> <ssf:nlt tag="searchForm.button.label"/></a> 
								</c:if>
							</td>
						</tr>
					</c:if>
					
				</table>
			</div>
		</div>

		<div id="ss_searchForm_additionalFilters">
			<input type="hidden" id="ssSearchParseAdvancedForm${ssNamespace}" name="ssSearchParseAdvancedForm" value="false" />
			<table>
			  <tbody>
			    <tr>
			      <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Author"/></h4></td>
			      <td>
	 			    <div id="ss_authors_container" class="ss_options_container">			
	  				  <div id="ss_authors_options" class="ss_options"></div>
					  <div class="ss_more">
					    <a href="javascript: ;" onClick="ss_addOption('creator_by_id');" class="ss_button"><ssf:nlt tag="searchForm.filterButton.addAuthor"/></a>
				      </div>
					</div>
				  </td>
			    </tr>
			    <tr>
			      <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Tag"/></h4></td>
			      <td>
					<div id="ss_tags_container" class="ss_options_container">	
						<div id="ss_tags_options" class="ss_options"></div>
						<div class="ss_more">
							<a href="javascript: ;" onClick="ss_addOption('tag');" class="ss_button"><ssf:nlt tag="searchForm.filterButton.addTag"/></a>
						</div>
					</div>	
			      </td>
			    </tr>
			    <tr>
			      <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Workflow"/></h4></td>
			      <td>
					<div id="ss_workflows_container" class="ss_options_container">	
						<div id="ss_workflows_options" class="ss_options"></div>
						<div class="ss_more">
							<a href="javascript: ;" onClick="ss_addOption('workflow');" class="ss_button"><ssf:nlt tag="searchForm.filterButton.addWorkflow"/></a>
						</div>
					</div>			      
			      </td>
			    </tr>
				<tr>
				  <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Entry"/></h4></td>
				  <td>
					<div id="ss_entries_container" class="ss_options_container">
						<div id="ss_entries_options" class="ss_options"></div>
						<div class="ss_more" style="clear: left;">
							<a href="javascript: ;" onClick="ss_addOption('entry');" class="ss_button"><ssf:nlt tag="searchForm.filterButton.addField"/></a>				
						</div>
					</div>				  
				  </td>
				</tr>
				<tr>
				  <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.LastActivity"/></h4></td>
				  <td>
					<div id="ss_lastActivities_container" class="ss_options_container">				
						<div id="ss_lastActivities_options" class="ss_options"></div>
					</div>
				  </td>
				</tr>
				<tr>
				  <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.CreationDate"/></h4></td>
				  <td>
					<div id="ss_creationDates_container" class="ss_options_container">				
						<div id="ss_creationDates_options" class="ss_options"></div>
						<div class="ss_more">
							<a href="javascript: ;" onClick="ss_addOption('creation_date');" class="ss_button"><ssf:nlt tag="searchForm.filterButton.addDate"/></a>
						</div>
					</div>
				  </td>
				</tr>
				<tr>
				  <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.ModificationDate"/></h4></td>
				  <td>
					<div id="ss_modificationDates_container" class="ss_options_container">
						<div id="ss_modificationDates_options" class="ss_options"></div>
						<div class="ss_more">
							<a href="javascript: ;" onClick="ss_addOption('modification_date');" class="ss_button"><ssf:nlt tag="searchForm.filterButton.addDate"/></a>
						</div>
					</div>
				  </td>
				</tr>
			<c:if test="${!filterDefinition}">
				<tr>
				  <td><h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.ItemType"/></h4></td>
				  <td>
						<div id="ss_itemTypes_container" class="ss_options_container">					
							<div id="ss_itemType_options" class="ss_options">
								<input type="checkbox" name="searchItemType" value="workspace" id="ss_itemType_workspace"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.workspace}">
										checked="true"
									</c:if>
								/>&nbsp;<label for="ss_itemType_workspace"><ssf:nlt tag="searchForm.itemType.workspace"/></label>
								<input type="checkbox" name="searchItemType" value="folder" id="ss_itemType_folder"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.folder}">
										checked="true"
									</c:if>
								/>&nbsp;<label for="ss_itemType_folder"><ssf:nlt tag="searchForm.itemType.folder"/></label>
								<input type="checkbox" name="searchItemType" value="user" id="ss_itemType_user"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.user}">
										checked="true"
									</c:if>					
								/>&nbsp;<label for="ss_itemType_user"><ssf:nlt tag="searchForm.itemType.user"/></label>
								<input type="checkbox" name="searchItemType" value="attachment" id="ss_itemType_attachment"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.attachment}">
										checked="true"
									</c:if>						
								/>&nbsp;<label for="ss_itemType_attachment"><ssf:nlt tag="searchForm.itemType.attachment"/></label>
								<input type="checkbox" name="searchItemType" value="entry" id="ss_itemType_entry"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.entry}">
										checked="true"
									</c:if>						
								/>&nbsp;<label for="ss_itemType_entry"><ssf:nlt tag="searchForm.itemType.entry"/></label>
								<input type="checkbox" name="searchItemType" value="reply" id="ss_itemType_reply"
									<c:if test="${empty ss_filterMap || ss_filterMap.additionalFilters.item_types.reply}">
										checked="true"
									</c:if>						
								/>&nbsp;<label for="ss_itemType_reply"><ssf:nlt tag="searchForm.itemType.reply"/></label>
							</div>
						</div>
				  </td>
				</tr>
			</c:if>			
			  </tbody>
			</table>
			
			<c:if test="${empty disableSearchButton || disableSearchButton == 0}">
			<div class="ss_searchFormFooter" style="text-align: center; padding: 10px;">
					<a class="ss_searchButton" href="javascript: ss_search();" ><img <ssf:alt tag="alt.search"/> 
					  src="<html:imagesPath/>pics/1pix.gif" /> <ssf:nlt tag="searchForm.button.label"/></a> 	
			</div>
			</c:if>
		</div>
		
		<c:if test="${! empty ss_filterMap.additionalFilters}">
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

