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
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("mobile.searchResults") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<%
	Map entriesSeen = new HashMap();
%>
<c:set var="ss_pageTitle" value="${ss_windowTitle}" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<c:set var="ss_hideMiniBlog" value="true" scope="request" />
<c:set var="ss_showSearchResultsNextPrev" value="true" scope="request" />
<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folders">
    <div class="folder-content">

	<div class="entry-actions" style="text-align: left;">
	  <form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="true" 
					binderId="${ssBinder.id}"
					operation="mobile_show_search_results" />">
	    <label for="searchText">
	      <div class="ss_bold"  style="padding-left: 5px;">
	        <c:if test="${ss_searchScope != 'local'}">
	          <ssf:nlt tag="mobile.searchTheSite"/>
	        </c:if>
		    <c:if test="${ss_searchScope == 'local' && ssBinder.entityType == 'folder'}">
		      <ssf:nlt tag="mobile.searchThisFolder"/>
		    </c:if>
		    <c:if test="${ss_searchScope == 'local' && ssBinder.entityType != 'folder'}">
		      <ssf:nlt tag="mobile.searchThisWorkspace"/>
		    </c:if>
          </div>
        </label>
	    <input type="text" size="25" name="searchText" id="searchText" 
	      value="<ssf:escapeQuotes>${ss_searchText}</ssf:escapeQuotes>"/><input 
	      type="submit" name="searchBtn" value="<ssf:nlt tag="button.search"/>"/>
	    <input type="hidden" name="scope" value="${ss_searchScope}"/>
			<sec:csrfInput />
		</form>
	</div>

<c:if test="${!empty ssFolderEntries}">
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
						    <% if (!ssSeenMap.checkIfSeen(entry)) { %>
								<span><img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
								  	align="absmiddle" border="0" <ssf:alt tag="alt.unseen"/> /></span>
			  				<% } %>
						    <c:if test="${0 == 1 && !empty entry._docNum}">
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
						</div>
						<div class="entry-signature">
						  <span class="entry-author">
						    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  				binderId="${entry._principal.workspaceId}"
				  				action="__ajax_mobile" 
				  				operation="mobile_show_workspace" />">${entry._principal.title}</a>
						  </span>
						  <span class="entry-date">
							<fmt:formatDate timeZone="${ssUser.timeZone.ID}" 
							  value="${entry._modificationDate}" type="both" 
							  timeStyle="short" dateStyle="medium" />
						  </span>
						</div>
						<c:if test="${!empty entry.binderPathName}">
						  <div class="entry-type">
							  <span>
							    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  				  binderId="${entry._binderId}"
				  				  action="__ajax_mobile" 
				  				  operation="mobile_show_folder" />">${entry.binderPathName}</a>
							  </span>
						  </div>
						</c:if>
						<c:if test="${!empty entry._desc}">
			    		  <c:set var="truncatedDescription" ><ssf:textFormat 
			      	  		formatAction="limitedDescription" 
			          		textMaxWords="20"><ssf:markup 
			          		search="${entry}" mobile="true">${entry._desc}</ssf:markup></ssf:textFormat></c:set>
			          	  <c:if test="${!empty truncatedDescription}">
						    <div class="entry-content">
							  ${truncatedDescription}
							  <div class="ss_clear"></div>
						    </div>
						  </c:if>
						</c:if>
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
						</div>
						<div class="entry-signature">
						  <span class="entry-author">
						    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  				binderId="${entry._principal.workspaceId}"
				  				action="__ajax_mobile" 
				  				operation="mobile_show_workspace" />">${entry._principal.title}</a>
						  </span>
						  <span class="entry-date">
							<fmt:formatDate timeZone="${ssUser.timeZone.ID}" 
							  value="${entry._modificationDate}" type="both" 
							  timeStyle="short" dateStyle="medium" />
						  </span>
						</div>
						<div class="entry-type">
						  <div>
							  <span>
							    <a href="<ssf:url adapter="true" portletName="ss_forum" 
							      folderId="${entryBinderId}" entryId="${entry._docId}"
							      action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
						        <c:if test="${empty entry.title}">
						    	  (<ssf:nlt tag="entry.noTitle"/>)
						        </c:if>
							    <c:out value="${entry.title}" escapeXml="true"/>
							    </a>
							  </span>
							</div>
						  <c:if test="${!empty entry.binderPathName}">
						    <div>
							  <span>
							    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  				  binderId="${entry._binderId}"
				  				  action="__ajax_mobile" 
				  				  operation="mobile_show_folder" />">${entry.binderPathName}</a>
							  </span>
						    </div>
						  </c:if>
						</div>
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
						</div>
					      <c:if test="${!empty entry._entityPath}">
						    <div class="entry-type">
						     <span>
						      <c:if test="${entry._entityType == 'folder'}">
							    <a href="<ssf:url adapter="true" portletName="ss_forum" 
							      folderId="${entry._docId}" 
							      action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
							    >${entry._entityPath}</a>
						      </c:if>
						      <c:if test="${entry._entityType == 'workspace'}">
							    <a href="<ssf:url adapter="true" portletName="ss_forum" 
							      folderId="${entry._docId}" 
							      action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
							    >${entry._entityPath}</a>
						      </c:if>
						     </span>
						    </div>
					      </c:if>
					  </div>
					<%
						entriesSeen.put(entry.get("_docId"), "1");
					%>
			      </c:when>

			  	  <c:when test="${entry._entityType == 'user'}">
					  <div class="entry">
						<div class="entry-title">
						  <a 
						    <c:if test="${empty entry._workspaceId}">
						      href="<ssf:url adapter="true" portletName="ss_forum" 
						        entryId="${entry._docId}"
						        binderId="${entry._binderId}"
						        action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
						    </c:if>
						    <c:if test="${!empty entry._workspaceId}">
						      href="<ssf:url adapter="true" portletName="ss_forum" 
						        binderId="${entry._workspaceId}"
						        action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
						    </c:if>
						  ><c:out value="${entry.title}" escapeXml="true"/></a>
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

  <div class="entry-actions">
    <%@ include file="/WEB-INF/jsp/mobile/search_results_next_prev.jsp" %>
  </div>
</c:if>

<c:if test="${empty ssFolderEntries}">
  <div class="entry-content">
    <ssf:nlt tag="search.noneFound"/>
  </div>
</c:if>

</div>
</div>
</div>

</body>
</html>
