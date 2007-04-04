	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
		<h4><ssf:nlt tag="searchForm.advanced.Title"/></h4>
		<form action="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="advanced_search"/>
					<portlet:param name="tabTitle" value=""/>
					<portlet:param name="newTab" value="1"/>
					</portlet:actionURL>" method="post" onSubmit="return prepareAdditionalOptions();" id="advSearchForm">
		
			<div id="ss_searchForm_main">
				<table>
					<tr><th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" id="searchText"/></td>
						<td rowspan="2"><p class="ss_help_text"><ssf:nlt tag="searchForm.advanced.Help"/></p></td></tr>
					<tr><th><ssf:nlt tag="searchForm.searchAuthor"/>:</th>
						<td><input type="text" name="searchAuthors" id="searchAuthors"/></td></tr>
					<tr><th><ssf:nlt tag="searchForm.searchTag"/>:</th>
						<td><input type="text" name="searchTags" id="searchTags"/></td>
						<td><img src="<html:imagesPath/>pics/search_icon.gif" onClick="document.getElementById('advSearchForm').submit();"/>
							<a href="#" onClick="ss_showHide('ss_searchForm_additionalFilters');"><ssf:nlt tag="searchForm.advanced.moreOptions"/></a>
						</td>
					</tr>
				</table>
				<!-- <ssf:nlt tag="searchForm.searchJoiner"/>: <input type="radio" name="searchJoinerAnd" value="true" id="searchJoinerAnd" checked="true"/><ssf:nlt tag="searchForm.searchJoiner.And"/>
					<input type="radio" name="searchJoinerAnd" id="searchJoinerOr" value="false"/><ssf:nlt tag="searchForm.searchJoiner.Or"/> -->
			</div>
		
			<div id="ss_searchForm_options" style='border: 1px solid gray;padding:5px;margin:5px;'>
				<a href="#" onClick="ss_addOption('workflow');">+ workflows</a>
				<a href="#" onClick="ss_addOption('entry');">+ entry attributes</a>
				<a href="#" onClick="ss_addOption('tag');">+ tags filter</a>
				<a href="#" onClick="ss_addOption('creation_date');">+ creation date filter</a>
				<a href="#" onClick="ss_addOption('modification_date');">+ modification date filter</a>
				<a href="#" onClick="ss_addOption('creator_by_id');">+ author</a>
			</div>
			
			<div id="ss_searchForm_additionalFilters">
			</div>
			
			<div id="ss_buttonBar">
				<input type="hidden" name="operation" value="ss_searchResults"/>
				<input type="hidden" name="searchNumbers" id="searchNumbers" value=""/>		
				<input type="hidden" name="searchTypes" id="searchTypes" value=""/>		
				<input type="submit" name="searchBtn" value="Submit"/>
			</div>
		</form>
		</div>


	</div>