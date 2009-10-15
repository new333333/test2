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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("mobile.searchResults") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<%
	Map entriesSeen = new HashMap();
%>
<c:set var="ss_pageTitle" value="${ss_windowTitle}" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folders">
    <div class="folder-content">

  	<div align="right">
	  <table>
		<tr>
		  <td>
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
				name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
			  ><img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_.gif"/></a>
			</c:if>
			<c:if test="${empty ss_prevPage}">
			  <img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_g.gif"/>
			</c:if>
		  </td>
		  <td style="padding-left:20px;">
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
				name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
			  ><img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_.gif"/></a>
			</c:if>
			<c:if test="${empty ss_nextPage}">
			  <img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_g.gif"/>
			</c:if>
	      </td>
		</tr>
	  </table>
	</div>

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
					  <div class="entry">
						<div class="entry-title">
						    <c:if test="${!empty entry._docNum}">
						      <span class="entry-docNumber">${entry._docNum}.</span>
						    </c:if>
							<a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${entryBinderId}" entryId="${entry._docId}"
							action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
						    <c:if test="${empty entry.title}">
						    	(<ssf:nlt tag="entry.noTitle"/>)
						    </c:if>
							<c:out value="${entry.title}" escapeXml="true"/>
							</a>
						    <c:if test="${!empty entry.binderTitle}">
							  <div class="entry-binder-title">
							    <span class="entry-binder-title">(${entry.binderTitle})</span>
							  </div>
						    </c:if>
						</div>
					  </div>
						<%
							entriesSeen.put(entry.get("_docId"), "1");
						%>
				    </c:when>
			
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
					  <div class="entry">
						<div class="entry-title">
							<a href="<ssf:fileUrl search="${entry}"/>">
						    <c:if test="${empty entry.title}">
						    	(<ssf:nlt tag="entry.noTitle"/>)
						    </c:if>
							<c:out value="${entry.title}" escapeXml="true"/>
							</a>
							<div style="padding-left:10px;">
							<span style="font-size:9px; color:silver;"><a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${entryBinderId}" entryId="${entry._docId}"
							action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
						    <c:if test="${empty entry.title}">
						    	(<ssf:nlt tag="entry.noTitle"/>)
						    </c:if>
							<c:out value="${entry.title}" escapeXml="true"/>
							</a></span>
							</div>
						</div>
						<c:if test="${!empty entry.binderTitle}">
						  <div class="entry-binder-title">
							<span>${entry.binderTitle}</span>
						  </div>
						</c:if>
					  </div>
				    </c:when>
			
				    <c:when test="${entry._docType == 'binder'}">
					  <div class="entry">
						<div class="entry-title">
						  <c:if test="${entry._entityType == 'folder'}">
							<a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${entry._docId}" 
							action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
							<c:out value="${entry.title}" escapeXml="true"/>
							</a>
						  </c:if>
						  <c:if test="${entry._entityType == 'workspace'}">
							<a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${entry._docId}" 
							action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
							<c:out value="${entry.title}" escapeXml="true"/>
							</a>
						  </c:if>
					      <c:if test="${!empty entry._extendedTitle}">
						    <div class="entry-binder-title">
						      <span>(${entry._extendedTitle})</span>
						    </div>
					      </c:if>
						</div>
					  </div>
					<%
						entriesSeen.put(entry.get("_docId"), "1");
					%>
			      </c:when>

			  	  <c:when test="${entry._entityType == 'user'}">
					  <div class="entry">
						<div class="entry-title">
						  <a href="<ssf:url adapter="true" portletName="ss_forum" 
						    folderId="${entry._workspaceId}"
						    action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
						    <c:out value="${entry.title}" escapeXml="true"/>
						  </a>
						</div>
					  </div>
					  <%
						entriesSeen.put(entry.get("_docId"), "1");
					  %>
				  </c:when>
			    <c:otherwise>
			    </c:otherwise>
			</c:choose>	
			<%
				}
			%>
		</c:forEach>

  <br/>
  <table>
	<tr>
	  <td>
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
			name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
		  ><img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_.gif"/></a>
		</c:if>
		<c:if test="${empty ss_prevPage}">
		  <img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_g.gif"/>
		</c:if>
	  </td>
	  <td style="padding-left:20px;">
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
			name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
		  ><img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_.gif"/></a>
		</c:if>
		<c:if test="${empty ss_nextPage}">
		  <img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_g.gif"/>
		</c:if>
      </td>
	</tr>
  </table>
</div>
</div>
<br/>

<%@ include file="/WEB-INF/jsp/mobile/footer.jsp" %>
</div>

</body>
</html>
