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
						<td><img src="<html:imagesPath/>pics/search_icon.gif" onClick="ss_search();"/>
							<a href="#"><ssf:nlt tag="searchForm.advancedSearch"/></a>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</form>
