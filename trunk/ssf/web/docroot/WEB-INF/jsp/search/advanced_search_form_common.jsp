<c:set var="ssNamespace" value="${renderResponse.namespace}"/>

	<script type="text/javascript">
		var ss_user_locale = "${ss_locale}";
	</script>

	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<c:if test="${!filterDefinition}">
					<h4><ssf:nlt tag="searchForm.advanced.Title"/> <ssf:inlineHelp tag="ihelp.other.advanced_search"/></h4>
				</c:if>
				<a href="#" onClick="ss_showAdditionalOptions('ss_searchForm_additionalFilters');" class="ss_advanced"><ssf:nlt tag="searchForm.advanced.moreOptions"/></a>
				<div class="ss_clear"></div>
				<table>
					<tr>
						<th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" id="searchText_adv" value="${ss_filterMap.searchText}" <c:if test="${empty disableSearchButton || disableSearchButton == 0}">onkeypress="return ss_submitViaEnter(event)"</c:if>/></td>
						<td rowspan="2"><p class="ss_help_text"><ssf:nlt tag="searchForm.advanced.Help"/></p></td>
					</tr>
					<tr>
						<th><ssf:nlt tag="searchForm.searchAuthor"/>:</th>
						<td><input type="text" name="searchAuthors" id="searchAuthors" value="${ss_filterMap.searchAuthors}" <c:if test="${empty disableSearchButton || disableSearchButton == 0}">onkeypress="return ss_submitViaEnter(event)"</c:if>/></td>
					</tr>
					<tr>
						<th><ssf:nlt tag="searchForm.searchTag"/>:</th>
						<td colspan="2"><input type="text" name="searchTags" id="searchTags" value="${ss_filterMap.searchTags}" <c:if test="${empty disableSearchButton || disableSearchButton == 0}">onkeypress="return ss_submitViaEnter(event)"</c:if>/></td>
					</tr>
					<c:if test="${!filterDefinition}">
						<tr>
							<th><ssf:nlt tag="searchForm.searchFolders"/>:</th>
							<td colspan="2">
							
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
								</c:if>
								
								<c:choose>
									<c:when test="${!empty ss_filterMap && !empty ss_filterMap.searchFolders}">
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
									  multiSelectPrefix="searchFolders_"
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
							<td colspan="3" style="text-align: right; ">
								<select name="data_resultsCount" id="data_resultsCount">
									<option value="5" <c:if test="${resultsCount == 5}">selected="selected"</c:if>>5 items</option>							
									<option value="10" <c:if test="${resultsCount == 10}">selected="selected"</c:if>>10 items</option>
									<option value="25" <c:if test="${resultsCount == 25}">selected="selected"</c:if>>25 items</option>							
									<option value="50" <c:if test="${resultsCount == 50}">selected="selected"</c:if>>50 items</option>
									<option value="100" <c:if test="${resultsCount == 100}">selected="selected"</c:if>>100 items</option>								
								</select>
								<select name="data_summaryWordCount" id="data_summaryWordCount">
									<option value="15" <c:if test="${summaryWordCount == 15}">selected="selected"</c:if>>15 words</option>							
									<option value="20" <c:if test="${summaryWordCount == 20}">selected="selected"</c:if>>20 words</option>
									<option value="30" <c:if test="${summaryWordCount == 30}">selected="selected"</c:if>>30 words</option>							
									<option value="50" <c:if test="${summaryWordCount == 50}">selected="selected"</c:if>>50 words</option>
									<option value="100" <c:if test="${summaryWordCount == 100}">selected="selected"</c:if>>100 words</option>
								</select>
								<c:if test="${empty disableSearchButton || disableSearchButton == 0}">
								<a class="ss_searchButton" href="javascript: ss_search();" ><img 
								  src="<html:imagesPath/>pics/1pix.gif" <ssf:alt tag="alt.search"/>/></a>
								</c:if>
							</td>
						</tr>
					</c:if>
					
				</table>
				<!-- <ssf:nlt tag="searchForm.searchJoiner"/>: <input type="radio" name="searchJoinerAnd" value="true" id="searchJoinerAnd" checked="true"/><ssf:nlt tag="searchForm.searchJoiner.And"/>
					<input type="radio" name="searchJoinerAnd" id="searchJoinerOr" value="false"/><ssf:nlt tag="searchForm.searchJoiner.Or"/> -->
			</div>
		</div>
		<c:if test="${! empty ss_filterMap.additionalFilters}">
		<div id="ss_searchForm_filterSummary" style="visibility:visible; display: block;">
			<!-- Summary of user filters -->
			<%@ include file="/WEB-INF/jsp/search/filterSummary.jsp" %>
		</div>
		</c:if>

		<div id="ss_searchForm_additionalFilters" style="visibility:hidden; display: none;">
			<div id="ss_authors_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Author"/></h4>
				<div id="ss_authors_options" class="ss_options"></div>
				<div class="ss_more">
					<a href="javascript: ;" onClick="ss_addOption('creator_by_id');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
				</div>
			</div>		
			<div id="ss_tags_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Tag"/></h4>
				<div id="ss_tags_options" class="ss_options"></div>
				<div class="ss_more">
					<a href="javascript: ;" onClick="ss_addOption('tag');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
				</div>
			</div>			
			<div id="ss_workflows_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Workflow"/></h4>
				<div id="ss_workflows_options" class="ss_options"></div>
				<div class="ss_more">
					<a href="javascript: ;" onClick="ss_addOption('workflow');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
				</div>
			</div>
			<div id="ss_entries_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Entry"/></h4>
				<div id="ss_entries_options" class="ss_options"></div>
				<div class="ss_more">
					<a href="javascript: ;" onClick="ss_addOption('entry');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>				
				</div>
			</div>
			<div id="ss_lastActivities_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.LastActivity"/></h4>
				<div id="ss_lastActivities_options" class="ss_options"></div>
			</div>
			<div id="ss_creationDates_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.CreationDate"/></h4>
				<div id="ss_creationDates_options" class="ss_options"></div>
				<div class="ss_more">
					<a href="javascript: ;" onClick="ss_addOption('creation_date');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
				</div>
			</div>
			<div id="ss_modificationDates_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.ModificationDate"/></h4>
				<div id="ss_modificationDates_options" class="ss_options"></div>
				<div class="ss_more">
					<a href="javascript: ;" onClick="ss_addOption('modification_date');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
				</div>
			</div>
			<c:if test="${!filterDefinition}">
				<div id="ss_itemTypes_container" class="ss_options_container">
					<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.ItemType"/></h4>
					<div id="ss_itemType_options" class="ss_options">
						<input type="checkbox" name="searchItemType" value="workspace" id="ss_itemType_workspace"
							<c:if test="${ss_filterMap.additionalFilters.item_types.workspace}">
								checked="true"
							</c:if>
						/>&nbsp;<label for="ss_itemType_workspace"><ssf:nlt tag="searchForm.itemType.workspace"/></label>
						<input type="checkbox" name="searchItemType" value="folder" id="ss_itemType_folder"
							<c:if test="${ss_filterMap.additionalFilters.item_types.folder}">
								checked="true"
							</c:if>
						/>&nbsp;<label for="ss_itemType_folder"><ssf:nlt tag="searchForm.itemType.folder"/></label>
						<input type="checkbox" name="searchItemType" value="user" id="ss_itemType_user"
							<c:if test="${ss_filterMap.additionalFilters.item_types.user}">
								checked="true"
							</c:if>					
						/>&nbsp;<label for="ss_itemType_user"><ssf:nlt tag="searchForm.itemType.user"/></label>
						<input type="checkbox" name="searchItemType" value="attachment" id="ss_itemType_attachment"
							<c:if test="${ss_filterMap.additionalFilters.item_types.attachment}">
								checked="true"
							</c:if>						
						/>&nbsp;<label for="ss_itemType_attachment"><ssf:nlt tag="searchForm.itemType.attachment"/></label>
						<input type="checkbox" name="searchItemType" value="entry" id="ss_itemType_entry"
							<c:if test="${ss_filterMap.additionalFilters.item_types.entry}">
								checked="true"
							</c:if>						
						/>&nbsp;<label for="ss_itemType_entry"><ssf:nlt tag="searchForm.itemType.entry"/></label>
						<input type="checkbox" name="searchItemType" value="reply" id="ss_itemType_reply"
							<c:if test="${ss_filterMap.additionalFilters.item_types.reply}">
								checked="true"
							</c:if>						
						/>&nbsp;<label for="ss_itemType_reply"><ssf:nlt tag="searchForm.itemType.reply"/></label>
					</div>
				</div>
			</c:if>			
			<c:if test="${empty disableSearchButton || disableSearchButton == 0}">
			<div style="text-align: right; padding: 10px;">
					<a class="ss_searchButton" href="javascript: ss_search();" ><img <ssf:alt tag="alt.search"/> 
					  src="<html:imagesPath/>pics/1pix.gif" /></a> <ssf:nlt tag="searchForm.button.label"/>	
			</div>
			</c:if>
		</div>
		<div id="ss_buttonBar">
			<input type="hidden" name="operation" value="ss_searchResults"/>
			<input type="hidden" name="searchNumbers" id="searchNumbers" value=""/>		
			<input type="hidden" name="searchTypes" id="searchTypes" value=""/>
		</div>
	</div>
