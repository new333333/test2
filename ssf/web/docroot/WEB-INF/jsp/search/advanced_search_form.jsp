<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<form action="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="advanced_search"/>
					<portlet:param name="tabTitle" value=""/>
					<portlet:param name="newTab" value="1"/>
					</portlet:actionURL>" method="post" onSubmit="return prepareAdditionalOptions();" id="advSearchForm">
	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<h4><ssf:nlt tag="searchForm.advanced.Title"/></h4>
				<table>
					<tr><th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" id="searchText"/></td>
						<td rowspan="2"><p class="ss_help_text"><ssf:nlt tag="searchForm.advanced.Help"/></p></td></tr>
					<tr><th><ssf:nlt tag="searchForm.searchAuthor"/>:</th>
						<td><input type="text" name="searchAuthors" id="searchAuthors"/></td></tr>
					<tr><th><ssf:nlt tag="searchForm.searchTag"/>:</th>
						<td><input type="text" name="searchTags" id="searchTags"/></td>
						<td><img src="<html:imagesPath/>pics/search_icon.gif" onClick="ss_search();"/>
							<a href="#" onClick="ss_showAdditionalOptions('ss_searchForm_additionalFilters');"><ssf:nlt tag="searchForm.advanced.moreOptions"/></a>
						</td>
					</tr>
				</table>
				<!-- <ssf:nlt tag="searchForm.searchJoiner"/>: <input type="radio" name="searchJoinerAnd" value="true" id="searchJoinerAnd" checked="true"/><ssf:nlt tag="searchForm.searchJoiner.And"/>
					<input type="radio" name="searchJoinerAnd" id="searchJoinerOr" value="false"/><ssf:nlt tag="searchForm.searchJoiner.Or"/> -->
			</div>
		</div>
		<c:if test="${!empty filterMap.additionalFilters}">
		<div id="ss_searchForm_additionalFilters" style="visibility:visible; display:block;">
		</c:if>
		<c:if test="${empty filterMap.additionalFilters}">
		<div id="ss_searchForm_additionalFilters" style="visibility:hidden; display: none;">
		</c:if>
			<div id="ss_workflows_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Workflow"/></h4>
				<div id="ss_workflows_options" class="ss_options"></div>
				<a href="javascript: ;" onClick="ss_addOption('workflow');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
			</div>
			<div id="ss_entries_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Entry"/></h4>
				<div id="ss_entries_options" class="ss_options"></div>
				<a href="javascript: ;" onClick="ss_addOption('entry');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>				
			</div>
			<div id="ss_tags_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Tag"/></h4>
				<div id="ss_tags_options" class="ss_options"></div>
				<a href="javascript: ;" onClick="ss_addOption('tag');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
			</div>
			<div id="ss_creationDates_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.CreationDate"/></h4>
				<div id="ss_creationDates_options" class="ss_options"></div>
				<a href="javascript: ;" onClick="ss_addOption('creation_date');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
			</div>
			<div id="ss_modificationDates_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.ModificationDate"/></h4>
				<div id="ss_modificationDates_options" class="ss_options"></div>
				<a href="javascript: ;" onClick="ss_addOption('modification_date');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
			</div>
			<div id="ss_authors_container" class="ss_options_container">
				<h4 class="ss_sectionTitle"><ssf:nlt tag="searchForm.sectionTitle.Author"/></h4>
				<div id="ss_authors_options" class="ss_options"></div>
				<a href="javascript: ;" onClick="ss_addOption('creator_by_id');" class="ss_button"><ssf:nlt tag="searchForm.moreCriteria"/></a>
			</div>
		</div>
		<div id="ss_buttonBar">
			<input type="hidden" name="operation" value="ss_searchResults"/>
			<input type="hidden" name="searchNumbers" id="searchNumbers" value=""/>		
			<input type="hidden" name="searchTypes" id="searchTypes" value=""/>		
		</div>
	</div>
</form>

	