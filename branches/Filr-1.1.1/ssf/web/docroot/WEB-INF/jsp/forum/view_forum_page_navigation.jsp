<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // Common folder page number navigation %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<c:if test="${!empty ssPageCount}">
<c:if test="${ssPageLast == '0'}"><c:set var="ssPageLast" value="1" scope="request"/></c:if>

<ssf:skipLink tag='<%= NLT.get("skip.paging.links") %>' id="navigationLinks_${renderResponse.namespace}">

<script type="text/javascript">
//Routine called when "find photo" is clicked
function ss_clickGoToEntry_${renderResponse.namespace}(id) {
	var url = "<ssf:url     
	    adapter="true" 
	    portletName="ss_forum" 
	    folderId="${ssBinder.id}" 
	    action="view_folder_entry" 
	    entryId="ss_entryIdPlaceholder" 
	    actionUrl="true" ></ssf:url>";
	url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
	ss_loadEntryUrl(url, id, '${ssBinder.id}', 'folderEntry', '${renderResponse.namespace}', 'no')
	return false;
}
</script>

<c:if test="${ssConfigJspStyle != 'template'}">
<div class="ss_pagination ss_style">
		<ssHelpSpot helpId="workspaces_folders/menus_toolbars/more_folder_navigation" offsetX="0" offsetY="18" 
	    			title="<ssf:nlt tag="helpSpot.moreFolderNavigation"/>">
		</ssHelpSpot>

	<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tbody>
		<tr>
			<td valign="middle">
				<% // goto entry option %>
				<div class="ss_goBox">
				  <c:if test="${empty ssForumPageNav_HideGoBox || 'true' != ssForumPageNav_HideGoBox}">
				  <table border="0" cellpadding="0" cellspacing="0" class="ss_pagination_goTable">
					<tbody>
					<tr>
						<td class="ss_page_IE2" valign="middle" align="right">
						  <span>
							<label for="ssGoToEntry${renderResponse.namespace}"><ssf:nlt tag="entry.go"/></label>
						  </span>
						</td>

						<td valign="middle" class="ss_paginationGo ss_page_IE">
						  <form name="ss_goToEntryForm_${renderResponse.namespace}" style="display:inline;"
							id="ss_goToEntryForm_${renderResponse.namespace}" method="post" 
							action="<ssf:url action="view_folder_entry" 
							actionUrl="true"> 
							<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
								<ssf:param name="showTrash" value="true"/> 
							</c:if>
							<ssf:param name="binderId" value="${ssFolder.id}"/><ssf:param 
							name="entryViewStyle" value="full"/><ssf:param 
							name="operation" value="go_to_entry"/></ssf:url>" >
							<c:if test="${ssBinder.entityType != 'profiles'}">
							   <ssf:find formName="ss_goToEntryForm_${renderResponse.namespace}" 
							    formElement="searchTitle" 
							    type="entries"
							    width="160px" 
							    binderId="${ssBinder.id}"
							    searchSubFolders="false"
								showFolderTitles="false"
							    singleItem="true"
							    clickRoutine="ss_clickGoToEntry_${renderResponse.namespace}"/> 
							</c:if>
							<c:if test="${ssBinder.entityType == 'profiles'}">
							  <ssf:find type="user"
								width="60px" 
								singleItem="true"/> 
							</c:if>
						  </form>
						 </td>				
					</tr>
				</tbody>
				</table>
	  		</c:if>
			</div>
			
			</td>
			<td align="center" class="ss_paginationDiv">
				<div>
				<table border="0" cellpadding="1" cellspacing="0" class="ss_pagination_table">
					<tbody>
					<tr>
						<td class="ss_pagination_arrows">

						<a href="<ssf:url action="${action}" actionUrl="true"> 
				  			<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
								<ssf:param name="showTrash" value="true"/> 
							</c:if>
							<ssf:param name="operation" value="save_folder_page_info"/><ssf:param 
							name="binderId" value="${ssFolder.id}"/><ssf:param 
							name="ssPageStartIndex" value="0"/><c:if test="${!empty cTag}"><ssf:param 
							name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
							name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
							name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
							name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						  title="<ssf:nlt tag="title.goto.first.page"/>"
						   <ssf:title tag="title.goto.first.page"
						  ><ssf:param name="value" value="0" /></ssf:title> 
						><img src="<html:rootPath/>images/pics/sym_arrow_gotoStart.png" border="0" id="back" <ssf:alt tag="title.goto.first.page"/> align="absmiddle" />
					</a>
					</td>
					<td class="ss_paginationFont">
					<a href="<ssf:url action="${action}" actionUrl="true"> 
			  			<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
							<ssf:param name="showTrash" value="true"/> 
						</c:if>
						<ssf:param name="operation" value="save_folder_page_info"/><ssf:param 
						name="binderId" value="${ssFolder.id}"/><ssf:param 
						name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
						name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						title="<ssf:nlt tag="title.goto.prev.page"/>"
						><img src="<html:rootPath/>images/pics/sym_arrow_left_.png" border="0" id="previous" align="absmiddle" />
					</a>
					</td>
					<td class="ss_paginationFont ss_pageActive" valign="top" align="center">
					<ssf:nlt tag="title.page.n_of_m">
					  <ssf:param name="value" value="${ssPageCurrent}"/>
					  <ssf:param name="value" value="${ssPageLast}"/>
					</ssf:nlt>
					</td>
					<td class="ss_paginationFont">
						<c:choose>
				  			<c:when test="${ssPageNext.ssPageNoLink == 'true'}">
							<span class="ss_pageNext" style="padding-left: 8px;"><img src="<html:rootPath/>images/pics/sym_arrow_right_g.png" title="<ssf:nlt tag="general.Next"/>" border="0" id="nextdisabled" align="absmiddle" /></span>
				  			</c:when>
				  		<c:otherwise>
						<a href="<ssf:url action="${action}" actionUrl="true"> 
				  			<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
								<ssf:param name="showTrash" value="true"/> 
							</c:if>
							<ssf:param name="operation" value="save_folder_page_info"/><ssf:param 
							name="binderId" value="${ssFolder.id}"/><ssf:param 
							name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
							name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
							name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
							name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
							name="endDate" value="${endDate}"/></c:if></ssf:url>" 
							title="<ssf:nlt tag="title.goto.next.page"/>"
							><img src="<html:rootPath/>images/pics/sym_arrow_right_.png" border="0" id="next" align="absmiddle" />
						</a>
				  		</c:otherwise>
						</c:choose>
					</td>
					<td class="ss_pagination_arrows">
						<c:choose>
				  			<c:when test="${ssPageNext.ssPageNoLink == 'true'}">
							<span class="ss_pageNext" style="padding-left: 8px;"><img src="<html:rootPath/>images/pics/sym_arrow_gotoEnd_g.png" title="<ssf:nlt tag="general.Next"/>" border="0" id="nextdisabled" align="absmiddle" /></span>
				  			</c:when>
				  		<c:otherwise>
					<a href="<ssf:url action="${action}" actionUrl="true"> 
			  			<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
							<ssf:param name="showTrash" value="true"/> 
						</c:if>
						<ssf:param name="operation" value="save_folder_page_info"/><ssf:param 
						name="binderId" value="${ssFolder.id}"/><ssf:param 
						name="ssPageStartIndex" value="${ssPageLastStartingIndex}"/><c:if test="${!empty cTag}"><ssf:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
						name="endDate" value="${endDate}"/></c:if></ssf:url>" 
						title="<ssf:nlt tag="title.goto.last.page"/>"
						><img src="<html:rootPath/>images/pics/sym_arrow_gotoEnd.png" border="0" id="last" <ssf:alt tag="title.goto.last.page"/> align="absmiddle" />
					</a>
				  		</c:otherwise>
						</c:choose>
			</td></tr></tbody></table>	</div>
			</td>
			<td align="right">
				<% // goto page option %>
				<div class="ss_goBox">
					<table border="0" cellpadding="0" cellspacing="0" class="ss_pagination_goTable">
					<tbody>
						<tr>
							<td class="ss_page_IE2" valign="middle" align="right">
								<span><label for="ssGoToPage${renderResponse.namespace}"><ssf:nlt tag="folder.GoPage"/></label></span>
							</td>
							<td valign="middle" class="ss_paginationGo ss_page_IE">
							<form name="ss_goToPageForm_${renderResponse.namespace}" 
							  style="display:inline;"
							  id="ss_goToPageForm_${renderResponse.namespace}" method="post" 
							  action="<ssf:url action="${action}" actionUrl="true"> 
								<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
									<ssf:param name="showTrash" value="true"/> 
								</c:if>
								<ssf:param name="binderId" value="${ssFolder.id}"/><c:if test="${!empty cTag}"><ssf:param 
								name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
								name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><ssf:param 
								name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
								name="endDate" value="${endDate}"/></c:if><ssf:param 
								name="operation" value="save_folder_goto_page_info"/></ssf:url>" 
							  onSubmit="return(ss_submitPage_${renderResponse.namespace}(this))">
							<input name="ssGoToPage" id="ssGoToPage${renderResponse.namespace}" size="7" type="text" 
							  class="ss_paginationTextBox" />&nbsp;
							<a class="ss_tinyButton" href="javascript: ;" 
							<ssf:title tag="title.goto.page" />
							onClick="ss_clickGoToPage_${renderResponse.namespace}('ss_goToPageForm_${renderResponse.namespace}'); return false;"
							><ssf:nlt tag="button.go"/></a>
						</form>
						</td>
					</tr>
				</tbody>
				</table>
			</div>

			</td>
		</tr>
		
		</tbody>
		</table>
</div>

</c:if>

</ssf:skipLink>
</c:if>
