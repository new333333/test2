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
<% //View the listing in the search view format %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />

<%
	Map entriesSeen = new HashMap();
%>
			<ul class="ss_searchResult">
				<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
					<li>
					<jsp:useBean id="entry" type="java.util.HashMap" />
					<%
						if (!entriesSeen.containsKey(entry.get("_docId"))) {
					%>
					<c:set var="entryBinderId" value="${entry._binderId}"/>
					<c:set var="entryDocId" value="${entry._docId}"/>	
					<c:if test="${entry._entityType == 'folder' || entry._entityType == 'workspace'}">
					  <c:set var="entryBinderId" value="${entry._docId}"/>
					  <c:set var="entryDocId" value=""/>
					</c:if>
					
					<div class="ss_thumbnail">
						<img <ssf:alt tag="alt.entry"/> src="<html:imagesPath/>pics/entry_icon.gif"/>
					</div>
					<div class="ss_entry_folderListView">
						<div class="ss_entryHeader">
							<h3 class="ss_entryTitle">
							
								<%
									if (!ssSeenMap.checkIfSeen(entry)) {
										%><img <ssf:alt tag="alt.unseen"/> border="0" 
										src="<html:imagesPath/>pics/sym_s_unseen.png"><%
									}
								%>
						
								<c:out value="${entry._docNum}" escapeXml="false"/>.
								<ssf:titleLink 
									action="view_folder_entry" 
									entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}"  
									namespace="${renderResponse.namespace}">
									<ssf:param name="url" useBody="true">
										<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
										action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
									</ssf:param>
									<c:out value="${entry.title}" escapeXml="false"/>
								</ssf:titleLink>
							</h3>
							<div class="ss_clear">&nbsp;</div>
						</div>
			
						<p id="summary_${status.count}">
							<c:if test="${!empty entry._desc}">								
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="100">
										<ssf:markup search="${entry}">${entry._desc}</ssf:markup>
								</ssf:textFormat>
								<div class="ss_clear"></div>
							</c:if>
						</p>
					</div>
					<div class="ss_clear">&nbsp;</div>
													
					<div id="details_${status.count}" class="ss_entryDetails">
						<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
						   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
						   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
							<c:if test="${!empty entry._totalReplyCount}">
							    <span style="padding-left: 10px;" class="ss_label"><ssf:nlt 
							      tag="popularity.CommentsOrReplies"/>: ${entry._totalReplyCount}</span>
							</c:if>
						 </p>
						<c:if test="${!empty entry._workflowStateCaption}">
							<p><span class="ss_label"><ssf:nlt tag="entry.workflowState" />:</span> 
							<ssf:nlt tag="${entry._workflowStateCaption}" checkIfTag="true"/></p>
						</c:if>
					</div>
					<%
						}
						entriesSeen.put(entry.get("_docId"), "1");
					%>
					</li>
				</c:forEach>
			</ul>
			

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
