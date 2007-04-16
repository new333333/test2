
	<script type="text/javascript">
		var ss_user_locale = "${ss_locale}";
	</script>

	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<h4><ssf:nlt tag="searchForm.advanced.Title"/></h4>
				<table>
					<tr><th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" id="searchText" onkeypress="return ss_submitViaEnter(event)"/></td>
						<td rowspan="2"><p class="ss_help_text"><ssf:nlt tag="searchForm.advanced.Help"/></p></td></tr>
					<tr><th><ssf:nlt tag="searchForm.searchAuthor"/>:</th>
						<td><input type="text" name="searchAuthors" id="searchAuthors" onkeypress="return ss_submitViaEnter(event)"/></td></tr>
					<tr><th><ssf:nlt tag="searchForm.searchTag"/>:</th>
						<td><input type="text" name="searchTags" id="searchTags" onkeypress="return ss_submitViaEnter(event)"/></td>
						<td>
							<select name="ssMaxHits" id="ssMaxHits">
								<option value="5" <c:if test="${ssMaxHits == 5}">selected="selected"</c:if>>5</option>							
								<option value="10" <c:if test="${ssMaxHits == 10}">selected="selected"</c:if>>10</option>
								<option value="25" <c:if test="${ssMaxHits == 25}">selected="selected"</c:if>>25</option>							
								<option value="50" <c:if test="${ssMaxHits == 50}">selected="selected"</c:if>>50</option>
								<option value="100" <c:if test="${ssMaxHits == 100}">selected="selected"</c:if>>100</option>								
							</select>
							<a class="ss_searchButton" href="javascript: document.ss_simpleSearchForm<portlet:namespace/>.submit();" ><img src="<html:imagesPath/>pics/1pix.gif" /></a>
							<a href="#" onClick="ss_showAdditionalOptions('ss_searchForm_additionalFilters');" class="ss_advanced"><ssf:nlt tag="searchForm.advanced.moreOptions"/></a>
						</td>
					</tr>
				</table>
				<!-- <ssf:nlt tag="searchForm.searchJoiner"/>: <input type="radio" name="searchJoinerAnd" value="true" id="searchJoinerAnd" checked="true"/><ssf:nlt tag="searchForm.searchJoiner.And"/>
					<input type="radio" name="searchJoinerAnd" id="searchJoinerOr" value="false"/><ssf:nlt tag="searchForm.searchJoiner.Or"/> -->
			</div>
		</div>
		<c:if test="${! empty filterMap.additionalFilters}">
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
		</div>
		<div id="ss_buttonBar">
			<input type="hidden" name="operation" value="ss_searchResults"/>
			<input type="hidden" name="searchNumbers" id="searchNumbers" value=""/>		
			<input type="hidden" name="searchTypes" id="searchTypes" value=""/>		
		</div>
	</div>
