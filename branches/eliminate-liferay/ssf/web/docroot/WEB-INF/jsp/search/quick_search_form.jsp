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
<form action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		name="tabTitle" value=""/><ssf:param 
		name="newTab" value="1"/></ssf:url>" method="post" id="advSearchForm">
	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<h4><ssf:nlt tag="searchForm.quicksearch.Title"/></h4>
				<a href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
					name="tabTitle" value=""/><ssf:param 
					name="newTab" value="1"/><ssf:param 
					name="searchText" value="${ss_filterMap.searchText}"/><ssf:param 
					name="operation" value="ss_searchResults"/></ssf:url>" 
					class="ss_advanced"><ssf:nlt tag="navigation.search.advanced"/></a>
				<div class="ss_clear"></div>
								
				<table>
					<tr><th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" value="${ss_filterMap.searchText}" id="searchText_adv"/></td>
						<td>
							<a class="ss_searchButton" 
							  href="javascript: document.getElementById('advSearchForm').submit();" ><img 
							  src="<html:imagesPath/>pics/1pix.gif" <ssf:alt tag="alt.search"/> /> <ssf:nlt tag="searchForm.button.label"/></a> 
							<input type="hidden" name="quickSearch" value="true"/>
							<input type="hidden" name="operation" value="ss_searchResults"/>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</form>
