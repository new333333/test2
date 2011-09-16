<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<% //Folder list.  %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
  <c:if test="${!empty ssFolders}">
  <div class="folders">
    <div class="folder-head">
      <ssf:nlt tag="mobile.folders"/>
    </div>
	<c:forEach var="folder" items="${ssFolders}" >
      <div class="folder-item">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${folder.id}" 
				action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
		  <c:if test="${empty folder.title}">
		    (<ssf:nlt tag="folder.noTitle"/>)
		  </c:if>
		  <c:out value="${folder.title}" escapeXml="true"/>
		</a>
	  </div>
	</c:forEach>
	
  </div>
  </c:if>

<c:if test="${ssDefinitionFamily == 'task'}">
  <div class="folders">
    <div class="folder-head">
       <ssf:nlt tag="mobile.entries"/>
    </div>
    
	  <c:if test="${!empty ssFolderEntries}">
	    <c:forEach var="entryFol2" items="${ssFolderEntries}">
	    	<jsp:useBean id="entryFol2" type="java.util.Map" />
			<div class="entry">
			  <div class="entry-title">
			    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  folderId="${entryFol2._binderId}"  entryId="${entryFol2._docId}"
				  action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />"
			    >
			  	  <span><c:if test="${empty entryFol2.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>
			  	    <ssf:makeWrapable><c:out value="${entryFol2.title}" escapeXml="true"/></ssf:makeWrapable>
			  	  </span>
			    </a>
			  </div>
			  <c:if test="${!empty entryFol2._totalReplyCount}">
			    <div class="entry-comment-label">${entryFol2._totalReplyCount}</div>
			  </c:if>
			  		  	  
			  <div class="entry-content">
			    <table cellspacing="0" cellpadding="0">
				  <tr>
					<td class="entry-caption" valign="top">
					  <ssf:nlt tag="task.dueDate"/>
					</td>
					<td class="entry-element" valign="top">
						<c:if test="${!empty entryFol2['start_end#LogicalEndDate']}">
							<c:choose>
								<c:when test="${!empty entryFol2['start_end#TimeZoneID']}">
									<span><fmt:formatDate 
											timeZone="${ssUser.timeZone.ID}"
											value="${entryFol2['start_end#LogicalEndDate']}" type="both" 
											dateStyle="short" timeStyle="short" /></span>				
								</c:when>	
								<c:otherwise>
									<span><fmt:formatDate 
											timeZone="GMT"
											value="${entryFol2['start_end#LogicalEndDate']}" type="date" 
											dateStyle="short"/></span>
								</c:otherwise>
							</c:choose>
							<c:if test="${overdue}">
								<span><ssf:nlt tag="milestone.overdue"/></span>
							</c:if>
						</c:if>
					</td>
				  </tr>
			      <tr>
					<td class="entry-caption" valign="top">
					  <ssf:nlt tag="task.priority"/>
					</td>
					<td class="entry-element" valign="top">
						<c:if test="${!empty entryFol2.priority}">
								<c:forEach var="prio" items="${entryFol2.ssEntryDefinitionElementData.priority.values}">
								  <c:if test="${entryFol2.priority == prio.key}">
								    <span>${prio.value}</span>
								  </c:if>
								</c:forEach>
						</c:if>
					</td>
				  </tr>
				  <tr>
					<td class="entry-caption" valign="top">
					  <ssf:nlt tag="task.status"/>
					</td>
					<td class="entry-element" valign="top">
					  <c:if test="${!empty entryFol2.status}">
						<c:forEach var="status" items="${entryFol2.ssEntryDefinitionElementData.status.values}">
						  <c:if test="${entryFol2.status == status.key}">
						    <div><span>${status.value}</span></div>
						  </c:if>
						</c:forEach>
					  </c:if>
					</td>
				  </tr>
				  <tr>
					<td class="entry-caption" valign="top">
					  <ssf:nlt tag="task.done"/>
					</td>
					<td class="entry-element" valign="top">
					  <c:if test="${! empty entryFol2.completed}">
						<c:forEach var="completed" items="${entryFol2.ssEntryDefinitionElementData.completed.values}">
						  <c:if test="${entryFol2.completed == completed.key}">
						    <div><span>${completed.value}</span></div>
						  </c:if>
						</c:forEach>
					  </c:if>
					</td>
			      </tr>
				  <tr>
					<td class="entry-caption" valign="top">
					  <ssf:nlt tag="task.assigned"/>
					</td>
					<td class="entry-element" valign="top">
						<c:set var="assignment" value='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(entryFol2.get("assignment"), false) %>' />
						<c:if test="${!empty assignment}">
								<c:forEach var="assigned" items="${assignment}">
									<div><ssf:showUser user="${assigned}"/></div>
								</c:forEach>
						</c:if>
						
						<c:set var="assignment_groups" value='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(entryFol2.get("assignment_groups"), false) %>' />
						<c:if test="${!empty assignment_groups}">
								<c:forEach var="assigned" items="${assignment_groups}">
									<div>${assigned.title} 
									  <span class="ss_mobile_small">(<ssf:nlt tag="__definition_default_group"/>)</span>
									</div>
								</c:forEach>
						</c:if>		
						
						<c:set var="assignment_teams" value='<%= org.kablink.teaming.util.ResolveIds.getBinders(entryFol2.get("assignment_teams")) %>' />
						<c:if test="${!empty assignment_teams}">
								<c:forEach var="assigned" items="${assignment_teams}">
									<div><ssf:showTeam team="${assigned}"/></div>
								</c:forEach>
						</c:if>									
					</td>
					
				  </tr>
			    </table>
			  </div>
		 
		  	  
			  <c:if test="${!empty entryFol2._desc}">
			    <div class="entry-content">
			    	<ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="10"><ssf:markup search="${entryFol2}" mobile="true">${entryFol2._desc}</ssf:markup></ssf:textFormat>
		  	    <div class="ss_clear"></div>
		  	    </div>
			  </c:if>
		
	    	</div>
		</c:forEach>
	    <div class="entry-actions">
	      <%@ include file="/WEB-INF/jsp/mobile/folder_next_prev.jsp" %>
	    </div>
	  </c:if>
	  <c:if test="${empty ssFolderEntries}">
		<div style="padding:20px;"><ssf:nlt tag="folder.NoResults"/></div>
	  </c:if>

	</div>
  </div>
</c:if>
		
<c:set var="ss_mobileBinderListShown" value="true" scope="request"/>
