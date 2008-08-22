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
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<%
	Map entriesSeen = new HashMap();
%>
<div class="ss_mobile">
<div class="ss_mobile_breadcrumbs">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
	><ssf:nlt tag="mobile.returnToTop"/></a>
</div>
<br/>
  <table class="ss_mobile" cellspacing="0" cellpadding="0" border="0">
		<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
			<jsp:useBean id="entry" type="java.util.HashMap" />
			<%
				if (!entriesSeen.containsKey(entry.get("_docId")) || 
						(entry.get("_entityType").equals("folderEntry") && 
						entry.get("_docType").equals("attachment"))) {
			%>
			<c:set var="entryBinderId" value="${entry._binderId}"/>
			<c:set var="entryDocId" value="${entry._docId}"/>	
			<c:if test="${entry._entityType == 'folder' || entry._entityType == 'workspace'}">
			  <c:set var="entryBinderId" value="${entry._docId}"/>
			  <c:set var="entryDocId" value=""/>
			</c:if>
			
				<c:choose>
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'entry'}">
					    <tr><td>
					    <c:if test="${!empty entry._docNum}">
					      ${entry._docNum}&nbsp;&nbsp;
					    </c:if>
						<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entryBinderId}" entryId="${entry._docId}"
						action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
					    <c:if test="${empty entry.title}">
					    	(<ssf:nlt tag="entry.noTitle"/>)
					    </c:if>
						<c:out value="${entry.title}"/>
						</a>
					    <c:if test="${!empty entry.binderTitle}">
						  <div style="padding-left:10px;">
						    <span style="font-size:9px; color:silver;">(${entry.binderTitle})</span>
						  </div>
					    </c:if>
					    </td></tr>
						<%
							entriesSeen.put(entry.get("_docId"), "1");
						%>
				  </c:when>
			
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
					    <tr><td>
						<a href="<ssf:fileUrl search="${entry}"/>">
					    <c:if test="${empty entry.title}">
					    	(<ssf:nlt tag="entry.noTitle"/>)
					    </c:if>
						<c:out value="${entry.title}"/>
						</a>
						<div style="padding-left:10px;">
						<span style="font-size:9px; color:silver;"><a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entryBinderId}" entryId="${entry._docId}"
						action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
					    <c:if test="${empty entry.title}">
					    	(<ssf:nlt tag="entry.noTitle"/>)
					    </c:if>
						<c:out value="${entry.title}"/>
						</a></span>
						</div>
					    <c:if test="${!empty entry.binderTitle}">
						  <div style="padding-left:10px;">
						    <span style="font-size:9px; color:silver;">(${entry.binderTitle})</span>
						  </div>
					    </c:if>
					    </td></tr>
				  </c:when>
			
				  <c:when test="${entry._docType == 'binder'}">
					<tr><td>
					<c:if test="${entry._entityType == 'folder'}">
						<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entry._docId}" 
						action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
						<c:out value="${entry.title}"/>
						</a>
					</c:if>
					<c:if test="${entry._entityType == 'workspace'}">
						<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entry._docId}" 
						action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
						<c:out value="${entry.title}"/>
						</a>
					</c:if>
				    <c:if test="${!empty entry._extendedTitle}">
					  <div style="padding-left:10px;">
					    <span style="font-size:9px; color:silver;">(${entry._extendedTitle})</span>
					  </div>
				    </c:if>
					</td></tr>
					<%
						entriesSeen.put(entry.get("_docId"), "1");
					%>
			      </c:when>

			  	  <c:when test="${entry._entityType == 'user'}">
					    <tr><td>
						<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entry._workspaceId}"
						action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
						<c:out value="${entry.title}"/>
						</a>
					    </td></tr>
						<%
							entriesSeen.put(entry.get("_docId"), "1");
						%>
				  </c:when>
			    <c:otherwise>
			    </c:otherwise>
			</c:choose>	
			  </td></tr>
			<%
				}
			%>
		</c:forEach>
	<tr><td></td><td></td></tr>
	<tr><td colspan="2">
	<table><tr><td width="30">
		<c:if test="${!empty ss_prevPage}">
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
			folderId="${ssBinder.id}" 
			action="__ajax_mobile" 
			operation="mobile_show_search_results" 
			actionUrl="false" ><ssf:param 
			name="quickSearch" value="true"/><ssf:param 
			name="searchText" value="${ss_searchText}"/><ssf:param 
			name="tabId" value="${ss_tab_id}"/><ssf:param 
			name="pageNumber" value="${ss_pageNumber-1}"/><ssf:param 
			name="ss_queryName" value="${ss_queryName}" /></ssf:url>">&lt;&lt;&lt;</a>
		</c:if>
	</td><td style="padding-left:30px;">
		<c:if test="${!empty ss_nextPage}">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			folderId="${ssBinder.id}" 
			action="__ajax_mobile" 
			operation="mobile_show_search_results" 
			actionUrl="false" ><ssf:param 
			name="quickSearch" value="true"/><ssf:param 
			name="searchText" value="${ss_searchText}"/><ssf:param 
			name="tabId" value="${ss_tab_id}"/><ssf:param 
			name="pageNumber" value="${ss_pageNumber+1}"/><ssf:param 
			name="ss_queryName" value="${ss_queryName}" /></ssf:url>">&gt;&gt;&gt;</a>
		</c:if>
	</td></tr></table>
	</td></tr>
  </table>

			
<br/>
<div class="ss_mobile_breadcrumbs">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
	><ssf:nlt tag="mobile.returnToTop"/></a>
</div>
</div>

</body>
</html>
