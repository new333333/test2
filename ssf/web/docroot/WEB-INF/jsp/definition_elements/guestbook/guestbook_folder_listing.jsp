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
<% //View the listing part of a guestbook folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/guestbook/guestbook_sign.jsp" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<script type="text/javascript">
var ss_showingFolder = true;
</script>

	<c:if test="${empty ssFolderEntries}">
		<jsp:include page="/WEB-INF/jsp/forum/view_no_entries.jsp" />
	</c:if>
	<c:if test="${!empty ssFolderEntries}">
		<c:forEach var="entry" items="${ssFolderEntries}" >
			<jsp:useBean id="entry" type="java.util.HashMap" />
			<table class="ss_guestbook" width="100%">
					<tr>
						<td class="ss_miniBusinessCard" style="padding-bottom: 5px;">
							<ssf:miniBusinessCard user="${entry._principal}"/> 
						</td>
						<td class="ss_guestbookContainer">
							<span class="ss_entryTitle ss_normalprint">
							
			   				<% if (!ssSeenMap.checkIfSeen(entry)) { %>
							    
							  <a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
							  title="<ssf:nlt tag="sunburst.click"/>"
							  onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
							><span 
							  style="display:${ss_sunburstVisibilityHide};"
							  id="ss_sunburstShow${renderResponse.namespace}" 
							  class="ss_fineprint">
							  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0" <ssf:alt tag="alt.new"/> />
							  </span>
							  </a>
								    
							<% } %>
							
							<ssf:titleLink action="view_folder_entry" entryId="${entry._docId}" 
								binderId="${entry._binderId}" entityType="${entry._entityType}" 
								namespace="${renderResponse.namespace}">
								
									<ssf:param name="url" useBody="true">
										<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
										action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
									</ssf:param>
								
									<c:if test="${empty entry.title}">
								    	${entry._principal.title} <ssf:nlt tag="guestbook.author.wrote"/>: 
								    </c:if>
									<c:out value="${entry.title}" escapeXml="false"/>
								</ssf:titleLink>
							</span>
			
							<span class="ss_entryDate"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
							      value="${entry._modificationDate}" type="both" 
								  timeStyle="short" dateStyle="short" /></span>
							
							<c:if test="${!empty entry._desc}">
							<div class="ss_entryContent">
								<span><ssf:markup search="${entry}" >${entry._desc}</ssf:markup></span>
								<div class="ss_clear"></div>
							</div>
							</c:if>
						</td>
					</tr>
			</table>
		</c:forEach>
	</c:if>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
