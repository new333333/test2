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
<form action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		name="binderId" value="${ssBinder.id}"/><ssf:param 
		name="tabTitle" value=""/><ssf:param 
		name="newTab" value="1"/></ssf:url>" method="post" id="advSearchForm">
	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<div class="n-buttonright"><ssf:showHelp guideName="user" pageId="informed_search" sectionId="informed_search_basic" /></div>
								
				<table>
					<tr>
						<th class="ss_nowrap ss_size_15px ss_bold" style="vertical-align: middle;"><ssf:nlt tag="searchForm.quicksearch.Title"/></th>
						<td colspan="2"><input type="text" name="searchText" value="<ssf:escapeQuotes>${ss_filterMap.searchText}</ssf:escapeQuotes>" id="searchText_adv"/></td>
						<td width="100%" style="vertical-align: middle;">
							<a class="ss_tinyButton ss_bold" href="javascript: document.getElementById('advSearchForm').submit();" >
							  <ssf:nlt tag="searchForm.button.label"/>
							</a> 
							<input type="hidden" name="quickSearch" value="true"/>
							<input type="hidden" name="operation" value="ss_searchResults"/>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
							<div class="ss_size_11px" style="color: #666;">			
								<c:choose>
								  <c:when test="${ssTotalRecords == '0'}">
									[<ssf:nlt tag="search.NoResults" />]
								  </c:when>
								  <c:otherwise>
									<ssf:nlt tag="search.results">
									<ssf:param name="value" value="${ssPageStartIndex}"/>
									<ssf:param name="value" value="${ssPageEndIndex}"/>
									<ssf:param name="value" value="${ssTotalRecords}"/>
									</ssf:nlt>
								  </c:otherwise>
								</c:choose>
							</div>
						</td>
						<td style="text-align: right;">
							<a href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
								name="tabTitle" value=""/><ssf:param 
								name="newTab" value="1"/><ssf:param 
								name="searchText" value="${ss_filterMap.searchText}"/><ssf:param 
								name="operation" value="ss_searchResults"/><ssf:param 
								name="showAdvancedSearchForm" value="true"/></ssf:url>" >
								<ssf:nlt tag="searchForm.advanced.Title"/>
							</a>
						</td>
						<td></td>
					</tr>	
				</table>
			</div>
		</div>
	</div>
</form>
