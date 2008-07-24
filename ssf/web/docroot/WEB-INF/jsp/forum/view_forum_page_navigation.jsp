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
<% // Common folder page number navigation %>
<%@ page import="com.sitescape.team.util.NLT" %>

<ssf:skipLink tag="<%= NLT.get("skip.paging.links") %>" id="navigationLinks_${renderResponse.namespace}">

<c:if test="${ssConfigJspStyle != 'template'}">
	  <ssHelpSpot helpId="workspaces_folders/menus_toolbars/more_folder_navigation" offsetX="-5" offsetY="3" 
	    title="<ssf:nlt tag="helpSpot.moreFolderNavigation"/>"></ssHelpSpot>
<div class="ss_pagination ss_style">
<table border="0" cellspacing="0px" cellpadding="0px" width="100%">
		<tbody>
		<tr>
			<td valign="top" align="right" width="25%">
			<% // this should be view entry %>
			<div id="ss_goBox">
			<table border="0" cellpadding="0" cellspacing="0" class="ss_pagination_goTable">
				<tbody><tr>
				<td class="ss_page_IE2" valign="middle">
			
				
			<c:if test="${ssPageCount > '1.0'}">
				<ssf:ifnotaccessible>
			    	Go to EntryXX
			    </ssf:ifnotaccessible>
			    
			    <ssf:ifaccessible>
			    	<span><label for="entry.goTo"><ssf:nlt tag="entry.goTo"/></label></span>
			    </ssf:ifaccessible>
			
			    </td>
			    <td valign="middle"  class="ss_paginationGo ss_page_IE">
			        <form name="ss_goToPageForm_${renderResponse.namespace}" id="ss_goToPageForm_${renderResponse.namespace}" method="post" 
			    action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/><c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if><ssf:param 
				name="operation" value="save_folder_goto_page_info"/></ssf:url>" onSubmit="return(ss_submitPage_${renderResponse.namespace}(this))">
				
			    <input name="ssGoToEntry" id="ssGoToEntry" size="7" type="text" class="ss_paginationTextBox" />&nbsp;
				<a href="javascript: ;" 
				<ssf:title tag="entry.goTo" />
				onClick="ss_clickGoToPage_${renderResponse.namespace}('ss_goToPageForm_${renderResponse.namespace}'); return false;">
				<img src="<html:rootPath/>images/pics/page/go.png" width="17" height="12" border="0" align="absmiddle" /></a>
				
			</c:if>
			<c:if test="${ssPageCount <= '1.0'}">
				<ssf:ifnotaccessible>
			    	<ssf:nlt tag="entry.goTo"/>
			    </ssf:ifnotaccessible>
			    
			    <ssf:ifaccessible>
			    	<span><label for="ssGoToPage"><ssf:nlt tag="folder.GoToPage"/></label></span>
			    </ssf:ifaccessible>
			    </td>
			    <td align="right" valign="top" class="ss_paginationGo">
			    <input name="ssGoToPage" id="ssGoToPage" size="7" type="text" class="ss_pTB_no" />&nbsp;
				<a href="" 
				<ssf:title tag="entry.goTo" />
				>
				<img src="<html:rootPath/>images/pics/page/go.png" width="17" height="12" border="0" align="absmiddle" /></a>
				
			</c:if>
			</form>
			</td></tr></tbody></table>
			</div>
		
			</td>
			<td width="50%" valign="top" align="center" class="ss_paginationDiv">
			<div width="100%" >
			<table valign="top" border="0" cellpadding="1" cellspacing="0" class="ss_pagination_table">
				<tbody><tr>
					<td bgcolor="#E9F1F1" class="ss_pagination_arrows">

						<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
							name="operation" value="save_folder_page_info"/><ssf:param 
							name="binderId" value="${ssFolder.id}"/><ssf:param 
							name="ssPageStartIndex" value="0"/><c:if test="${!empty cTag}"><ssf:param 
							name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
							name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
							name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
							name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						  title="<ssf:nlt tag="title.goto.first.page"/>"
						  onClick="ss_showFolderPage(this, '${ssFolder.id}', '1', 'ss_folder_view_common${renderResponse.namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
						   <ssf:title tag="title.goto.first.page"
						  ><ssf:param name="value" value="1" /></ssf:title> 
						>
						<img src="<html:rootPath/>images/pics/page/back.gif" width="15" height="10" border="0" id="back" <ssf:alt tag="title.goto.first.page"/> align="absmiddle" /></a>&nbsp;&nbsp;
					</td>
					<td bgcolor="#E9F1F1" class="ss_paginationFont">
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
						><ssf:nlt tag="general.Previous"/>&nbsp;&nbsp;
					</a>&nbsp;&nbsp;
					</td>
					<td class="ss_paginationFont ss_pageActive" bgcolor="#E9F1F1" valign="top">
					<ssf:nlt tag="title.page.n_of_m">
					  <ssf:param name="value" value="${ssPageCurrent}"/>
					  <ssf:param name="value" value="${ssPageLast}"/>
					</ssf:nlt>&nbsp;&nbsp;
					</td>
					<td bgcolor="#E9F1F1" class="ss_paginationFont">
						<c:choose>
				  			<c:when test="${ssPageNext.ssPageNoLink == 'true'}">
							<span class="ss_pageNext"><ssf:nlt tag="general.Next"/>&nbsp;&nbsp;</span>&nbsp;&nbsp;
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
							><ssf:nlt tag="general.Next"/>&nbsp;&nbsp;
						</a>&nbsp;&nbsp;
				  		</c:otherwise>
						</c:choose>
					</td>
					<td bgcolor="#E9F1F1" class="ss_pagination_arrows">
					<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
						name="operation" value="save_folder_page_info"/><ssf:param 
						name="binderId" value="${ssFolder.id}"/><ssf:param 
						name="ssPageStartIndex" value="${ssPageLastStartingIndex}"/><c:if test="${!empty cTag}"><ssf:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
						name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						title="<ssf:nlt tag="title.goto.last.page"/>"
						onClick="ss_showFolderPage(this, '${ssFolder.id}', '${ssPageLastStartingIndex}', 'ss_folder_view_common${renderResponse.namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
						><img src="<html:rootPath/>images/pics/page/next.gif" width="15" height="10" border="0" id="next" <ssf:alt tag="title.goto.last.page"/> align="absmiddle" />&nbsp;&nbsp;
					</a>
			</td></tr></tbody></table>	</div>
			</td>
			<% // goto page option %>
			<td valign="top" width="25%">
			<div id="ss_goBox">
			<table border="0" cellpadding="0" cellspacing="0" class="ss_pagination_goTable">
				<tbody><tr>
				<td class="ss_page_IE2" valign="middle">
			
			<c:if test="${ssPageCount > '1.0'}">
				<ssf:ifnotaccessible>
			    	<ssf:nlt tag="folder.GoToPage"/>
			    </ssf:ifnotaccessible>
			    
			    <ssf:ifaccessible>
			    	<span><label for="ssGoToPage"><ssf:nlt tag="folder.GoToPage"/></label></span>
			    </ssf:ifaccessible>
			    </td>
			    <td valign="middle"  class="ss_paginationGo ss_page_IE">
			    <form name="ss_goToPageForm_${renderResponse.namespace}" id="ss_goToPageForm_${renderResponse.namespace}" method="post" 
			    action="<ssf:url action="${action}" actionUrl="true"><ssf:param 
				name="binderId" value="${ssFolder.id}"/><c:if test="${!empty cTag}"><ssf:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
				name="endDate" value="${endDate}"/></c:if><ssf:param 
				name="operation" value="save_folder_goto_page_info"/></ssf:url>" onSubmit="return(ss_submitPage_${renderResponse.namespace}(this))">
				
				
			    <input name="ssGoToPage" id="ssGoToPage" size="7" type="text" class="ss_paginationTextBox" />&nbsp;
				<a href="javascript: ;" 
				<ssf:title tag="title.goto.page" />
				onClick="ss_clickGoToPage_${renderResponse.namespace}('ss_goToPageForm_${renderResponse.namespace}'); return false;">
				<img src="<html:rootPath/>images/pics/page/go.png" width="17" height="12" border="0" align="absmiddle" /></a>
				
			</c:if>
			<c:if test="${ssPageCount <= '1.0'}">
				<ssf:ifnotaccessible>
			    	<ssf:nlt tag="folder.GoToPage"/>
			    </ssf:ifnotaccessible>
			    
			    <ssf:ifaccessible>
			    	<span><label for="ssGoToPage"><ssf:nlt tag="folder.GoToPage"/></label></span>
			    </ssf:ifaccessible>
			    </td></tr><tr>
			    <td valign="middle" class="ss_paginationGo ss_page_IE">
			    <input name="ssGoToPage" id="ssGoToPage" size="7" type="text" class="ss_pTB_no" />&nbsp;
				<a href="" 
				<ssf:title tag="title.goto.page" />
				>
				<img src="<html:rootPath/>images/pics/page/go.png" width="17" height="12" border="0" align="absmiddle" /></a>
				
			</c:if>
			</form>
			</td></tr></tbody></table></div>

			</td>
		</tr>
		
		</tbody>
		</table>
</div>
		
</c:if>

</ssf:skipLink>