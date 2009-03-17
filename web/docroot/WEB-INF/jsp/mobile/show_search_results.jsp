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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("mobile.searchResults") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<%
	Map entriesSeen = new HashMap();
%>
<div id="wrapper">
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>
<div id="pagebody">

<div class="pagebody">
	<h3 align="center"><ssf:nlt tag="mobile.searchResults"/></h3>
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

	<table cellspacing="2" cellpadding="4">
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
					    <tr>
					      <td valign="top" align="right">
						    <c:if test="${!empty entry._docNum}">
						      <span>${entry._docNum}</span>
						    </c:if>
						  </td>
						  <td style="padding-left: 4px;">
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
					      </td>
					    </tr>
						<%
							entriesSeen.put(entry.get("_docId"), "1");
						%>
				    </c:when>
			
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
					    <tr>
					      <td></td>
					      <td style="padding-left: 4px;">
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
					      </td>
					    </tr>
				  </c:when>
			
				  <c:when test="${entry._docType == 'binder'}">
					<tr>
					  <td></td>
					  <td style="padding-left: 4px;">
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
					  </td>
					</tr>
					<%
						entriesSeen.put(entry.get("_docId"), "1");
					%>
			      </c:when>

			  	  <c:when test="${entry._entityType == 'user'}">
					<tr>
					  <td></td>
					  <td style="padding-left: 4px;">
						<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entry._workspaceId}"
						action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
						<c:out value="${entry.title}"/>
						</a>
					  </td>
					</tr>
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
	</table>

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
