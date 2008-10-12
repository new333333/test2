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
  <table cellspacing="0" cellpadding="0">
    <c:forEach var="entry1" items="${ssFolderEntries}" >
	<jsp:useBean id="entry1" type="java.util.HashMap" />
	<%
		String folderLineId = "folderLine_" + (String) entry1.get("_docId");
		String seenStyle = "";
		String seenStyleFine = "ss_finePrint";
		if (!ssSeenMap.checkIfSeen(entry1)) {
			seenStyle = "ss_unseen";
			seenStyleFine = "ss_unseen ss_fineprint";
		}
	%>
	    <tr><td><div style="padding:0px 4px 4px 8px;">
	    <a 
	    href="<ssf:url     
	    adapter="true" 
	    portletName="ss_forum" 
	    folderId="${ssFolder.id}" 
	    action="view_folder_entry" 
	    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true"><ssf:param
	    name="entryViewStyle" value="popup"/><ssf:param
	    name="namespace" value="${renderResponse.namespace}"/><ssf:ifaccessible><ssf:param name="newTab" value="1" /></ssf:ifaccessible></ssf:url>" 
	
	    <ssf:title tag="title.open.folderEntry">
		    <ssf:param name="value" useBody="true"><c:choose><c:when test="${!empty entry1.title}">${entry1.title}</c:when><c:otherwise>--<ssf:nlt tag="entry.noTitle"/>--</c:otherwise></c:choose></ssf:param>
	    </ssf:title>
	
	    <ssf:ifnotaccessible>
	    	onClick="ss_loadWikiEntry(this, '${entry1._docId}');return false;" 		    	
	    </ssf:ifnotaccessible>
	    
	    <ssf:ifaccessible>
		    onClick="ss_loadWikiEntryInParent(this, '${entry1._docId}');return false;" 
	    </ssf:ifaccessible>
	    
	    ><c:if test="${empty entry1.title}"
	    ><span id="folderLine_${entry1._docId}" class="ss_smallprint <%= seenStyleFine %>"
	      >--<ssf:nlt tag="entry.noTitle"/>--</span
	    ></c:if><span id="folderLine_${entry1._docId}" class="ss_smallprint <%= seenStyle %>"
	      ><c:out value="${entry1.title}"/></span></a>
	    </td></tr>
    </c:forEach>
  </table>
  <table cellspacing="0" cellpadding="0" width="100%">
	<tr>
	<td width="50%">
	<c:choose>
	  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}"></c:when>
	  <c:otherwise>
		<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			name="operation" value="save_folder_page_info"/><ssf:param 
			name="binderId" value="${ssFolder.id}"/><ssf:param 
			name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
			name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
			name="pTag" value="${pTag}"/></c:if><c:if test="${!empty ss_yearMonth}"><ssf:param 
			name="yearMonth" value="${ss_yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
			name="endDate" value="${endDate}"/></c:if></ssf:url>" 
			<ssf:title tag="title.goto.prev.page" /> 
			onClick="ss_showWikiFolderPage(this.href, '${ssFolder.id}', '${ssPagePrevious.ssPageInternalValue}', 'ss_wikiFolderList${ss_namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
			>&lt;&lt;
		</a>
	  </c:otherwise>
	</c:choose>
	</td><td align="right" width="50%">
	<c:choose>
	  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
		
	  </c:when>
	  <c:otherwise>
		<a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
			name="operation" value="save_folder_page_info"/><ssf:param 
			name="binderId" value="${ssFolder.id}"/><ssf:param 
			name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/><c:if test="${!empty cTag}"><ssf:param 
			name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
			name="pTag" value="${pTag}"/></c:if><c:if test="${!empty ss_yearMonth}"><ssf:param 
			name="yearMonth" value="${ss_yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
			name="endDate" value="${endDate}"/></c:if></ssf:url>" 
			<ssf:title tag="title.goto.next.page" />
			onClick="ss_showWikiFolderPage(this.href, '${ssFolder.id}', '${ssPageNext.ssPageInternalValue}', 'ss_wikiFolderList${renderResponse.namespace}', '${cTag}', '${pTag}', '${yearMonth}', '${endDate}');return false;"
			>&gt;&gt;
		</a>
	  </c:otherwise>
	</c:choose>
	</td>
	</tr>
  </table>
