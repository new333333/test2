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
<% // Common folder page number navigation %>
<%@ page import="com.sitescape.team.util.NLT" %>

<ssf:skipLink tag="<%= NLT.get("skip.paging.links") %>" id="navigationLinks_${renderResponse.namespace}">

<c:if test="${ssConfigJspStyle != 'template'}">
	  <ssHelpSpot helpId="workspaces_folders/menus_toolbars/more_folder_navigation" offsetX="-5" offsetY="3" 
	    title="<ssf:nlt tag="helpSpot.moreFolderNavigation"/>"></ssHelpSpot>

		<table border="0" cellspacing="0px" cellpadding="0px" width="100%">
		<tbody>
		<tr>


			<td valign="top" width="18%">
			<form name="ss_goToPageForm_${renderResponse.namespace}" id="ss_goToPageForm_${renderResponse.namespace}" method="post" 
			    action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/><c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if><ssf:param 
				name="operation" value="save_folder_goto_page_info"/></ssf:url>" onSubmit="return(ss_submitPage_${renderResponse.namespace}(this))">
				&nbsp;&nbsp;
			<c:if test="${ssPageCount > '1.0'}">
				<ssf:ifnotaccessible>
			    	<span class="ss_go_to_page"><ssf:nlt tag="folder.GoToPage"/></span>
			    </ssf:ifnotaccessible>
			    
			    <ssf:ifaccessible>
			    	<span class="ss_go_to_page"><label for="ssGoToPage"><ssf:nlt tag="folder.GoToPage"/></label></span>
			    </ssf:ifaccessible>
			    <input name="ssGoToPage" id="ssGoToPage" size="1" type="text" class="form-text" />
				<a class="ss_linkButton" href="javascript: ;" 
				<ssf:title tag="title.goto.page" />
				onClick="ss_clickGoToPage_${renderResponse.namespace}('ss_goToPageForm_${renderResponse.namespace}'); return false;"><ssf:nlt tag="button.go"/></a>
			</c:if>
			</form>
			<br/>
			<span class="ssVisibleEntryNumbers">
				
					<c:choose>
					  <c:when test="${ssTotalRecords == '0'}">
						[<ssf:nlt tag="folder.NoResults" />]
					  </c:when>
					  <c:otherwise>
						&nbsp;&nbsp;&nbsp;<ssf:nlt tag="entry.showing"/>: [<ssf:nlt tag="folder.Results">
						<ssf:param name="value" value="${ssPageStartIndex}"/>
						<ssf:param name="value" value="${ssPageEndIndex}"/>
						<ssf:param name="value" value="${ssTotalRecords}"/>
						</ssf:nlt>]
					  </c:otherwise>
					</c:choose>
				</span>
				&nbsp;&nbsp;

			</td>

			<td valign="top">
				<c:set var="ssCurrentPage" value="0"/>
				<c:forEach var="currentEntryPage" items="${ssPageNumbers}" >
				    <jsp:useBean id="currentEntryPage" type="java.util.HashMap" />
					<c:if test="${!empty currentEntryPage.ssPageIsCurrent && currentEntryPage.ssPageIsCurrent == 'true'}">
					  <c:set var="ssCurrentPage" value="${currentEntryPage.ssPageDisplayValue}"/>
					</c:if>
				</c:forEach>

				<c:choose>
				  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}">
					
				  </c:when>
				  <c:otherwise>
					<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
						name="operation" value="save_folder_page_info"/><ssf:param 
						name="binderId" value="${ssFolder.id}"/><ssf:param 
						name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
						name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						title="<ssf:nlt tag="title.goto.prev.page"/>"
						onClick="ss_showFolderPage(this, '${ssFolder.id}', '${ssPagePrevious.ssPageInternalValue}', 'ss_folder_view_common${renderResponse.namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
						> &lt;&lt;
					</a>&nbsp;&nbsp;
				  </c:otherwise>
				</c:choose>
				
				<c:choose>
				  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
					
				  </c:when>
				  <c:otherwise>
					<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
						name="operation" value="save_folder_page_info"/><ssf:param 
						name="binderId" value="${ssFolder.id}"/><ssf:param 
						name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
						name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						title="<ssf:nlt tag="title.goto.next.page"/>"
						onClick="ss_showFolderPage(this, '${ssFolder.id}', '${ssPageNext.ssPageInternalValue}', 'ss_folder_view_common${renderResponse.namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
						> 
						&gt;&gt;
					</a>
				  </c:otherwise>
				</c:choose>

				<c:forEach var="entryPage" items="${ssPageNumbers}" >
				<jsp:useBean id="entryPage" type="java.util.HashMap" />
					<c:if test="${!empty entryPage.ssPageIsCurrent && entryPage.ssPageIsCurrent == 'true'}">
						<span class="ssCurrentPage">
							<c:if test="${ssPageCount > '1.0'}">
							<c:out value="${entryPage.ssPageDisplayValue}"/>
							</c:if>
						</span>
					</c:if>
					
					<c:if test="${empty entryPage.ssPageIsCurrent}">
						<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
							name="operation" value="save_folder_page_info"/><ssf:param 
							name="binderId" value="${ssFolder.id}"/><ssf:param 
							name="ssPageStartIndex" value="${entryPage.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
							name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
							name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
							name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
							name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						  onClick="ss_showFolderPage(this, '${ssFolder.id}', '${entryPage.ssPageInternalValue}', 'ss_folder_view_common${renderResponse.namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
						  class="ssPageNumber" <ssf:title tag="title.goto.page.number"
						  ><ssf:param name="value" value="${entryPage.ssPageDisplayValue}" /></ssf:title> 
						>
						<span><c:out value="${entryPage.ssPageDisplayValue}"/></span><%--
						--%></a>
					</c:if>
				</c:forEach>

			<% // Show number of entries per page-- only shows here in accessible mode %>
			<ssf:ifaccessible>
			<br/><br/>
			<form name="ss_recordsPerPage_${renderResponse.namespace}" id="ss_recordsPerPage_${renderResponse.namespace}" method="post" 
			    action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/>
				<c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if><ssf:param 
				name="operation" value="change_entries_on_page"/></ssf:url>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
			
				<div class="ss_results_pro_page">
				  <span class="ss_tabs_title">
	
				  <ssf:menu title="${ssPageMenuControlTitle}" 
				    titleId="ss_selectEntriesTitle${renderResponse.namespace}" 
				    titleClass="ss_compact" menuClass="ss_actions_bar4 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
				
				    
				
				    

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '5');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '10');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '25');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '50');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_${renderResponse.namespace}('ss_recordsPerPage_${renderResponse.namespace}', '100');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
					</a><br/>

				    
					
				  </ssf:menu>

			    </span>
			    </div>
			</form>
			</ssf:ifaccessible>
			</td>
		</tr>
		</tbody>
		</table>
</c:if>

</ssf:skipLink>