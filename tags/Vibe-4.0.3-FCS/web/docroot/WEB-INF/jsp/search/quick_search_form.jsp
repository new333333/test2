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
	<input type="hidden" name="context" value="${ss_searchContext}"/>
	<input type="hidden" name="contextCollection" value="${ss_searchContextCollection}"/>
	<input type="hidden" name="contextBinderId" value="${ss_searchContextBinderId}"/>
	<input type="hidden" name="contextEntryId" value="${ss_searchContextEntryId}"/>
	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<div class="n-buttonright"><ssf:showHelp guideName="user" pageId="informed_search" sectionId="informed_search_basic" /></div>
				  <c:if test="${!empty ss_searchError}">
				    <div style="padding:20px;">
				      <span class="ss_errorLabel ss_bold ss_largestprint">${ss_searchError}</span>
				    </div>
				  </c:if>
								
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
						<td colspan="2">
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
								<input type="radio" name="scope" value="current" 
								  <c:if test="${ss_searchScope == 'current'}"> checked="checked" </c:if>
								  style="width:20px;"
								>
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
					
					<tr>
						<td></td>
						<td>
							<div class="ss_size_11px" style="color: #666;">			
								<c:choose>
								  <c:when test="${ssTotalRecords == '0'}">
									[<ssf:nlt tag="search.NoResults" />]
								  </c:when>
								  <c:otherwise>
								    <c:if test="${searchCountTotalApproximate}">
									  <ssf:nlt tag="search.resultsApproximate">
									    <ssf:param name="value" value="${ssPageStartIndex}"/>
									    <ssf:param name="value" value="${ssPageEndIndex}"/>
									  </ssf:nlt>
									</c:if>
								    <c:if test="${!searchCountTotalApproximate}">
									  <ssf:nlt tag="search.results">
									    <ssf:param name="value" value="${ssPageStartIndex}"/>
									    <ssf:param name="value" value="${ssPageEndIndex}"/>
									    <ssf:param name="value" value="${ssTotalRecords}"/>
									  </ssf:nlt>
									</c:if>
								  </c:otherwise>
								</c:choose>
							</div>
						</td>
						<td style="text-align: right;">
						  <ssf:ifNotFilr>
							<a href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
								name="tabTitle" value=""/><ssf:param 
								name="newTab" value="1"/><ssf:param 
								name="searchText" useBody="true">${ss_filterMap.searchText}</ssf:param><ssf:param 
								name="operation" value="ss_searchResults"/><ssf:param 
								name="showAdvancedSearchForm" value="true"/><ssf:param 
								name="context" value="${ss_searchContext}"/><ssf:param 
								name="contextCollection" value="${ss_searchContextCollection}"/><ssf:param 
								name="contextBinderId" value="${ss_searchContextBinderId}"/><ssf:param 
								name="contextEntryId" value="${ss_searchContextEntryId}"/><ssf:param 
								name="includeNestedBinders" value="${ss_searchIncludeNestedBinders}"/></ssf:url>" >
								<ssf:nlt tag="searchForm.advanced.Title"/>
							</a>
						  </ssf:ifNotFilr>
						</td>
						<td></td>
					</tr>	
				</table>
			</div>
		</div>
	</div>
	<sec:csrfInput />
</form>
