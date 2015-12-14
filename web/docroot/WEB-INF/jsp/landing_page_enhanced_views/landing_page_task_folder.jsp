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
<%
/**
 * This is an example of a custom landing page folder jsp 
 * 
 * These beans are set up as request attributes:
	 *   ss_mashupBinderEntries - Map<String, List<Map>> indexed by binderId
	 *     The List contains a list of Maps, one for each entry as returned by the search function
	 *   ss_mashupEntryReplies - Map<String, Map> indexed by entryId
	 *     ss_mashupEntryReplies[entryId][folderEntryDescendants] is a list of reply objects
	 *     ss_mashupEntryReplies[entryId][folderEntryAncestors] is a list of parent entry objects
 */
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "folder");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<c:set var="mashupBinderId" value="${mashup_attributes['folderId']}"/>
<c:set var="mashupBinder" value="${ss_mashupBinders[mashupBinderId]}"/>
<c:if test="${!empty mashup_attributes['zoneUUID']}">
  <c:set var="zoneBinderId" value="${mashup_attributes['zoneUUID']}.${mashup_attributes['folderId']}" />
  <c:if test="${!empty ss_mashupBinders[zoneBinderId]}">
    <c:set var="mashupBinder" value="${ss_mashupBinders[zoneBinderId]}"/>
    <c:set var="mashupBinderId" value="${mashupBinder.id}"/>
  </c:if>
</c:if>
<style>
.tasks {
	background: #FFF;
	border-collapse: collapse;
	color: #000;
	margin: 1em 0 1em;
	table-layout: fixed;
}

.tasks td {
	padding: .3em .5em;
	text-align: left;
	vertical-align: top;
	border: 1px solid #cecece;
	color: #000;
}

.tasks th {
	font-weight: normal;
	padding: .3em .5em;
	text-align: left;
	vertical-align: top;
	border: 1px solid #cecece;
	color: #000;
}
</style>
<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
<li>
</c:if>
<% } %>
<div class="ss_mashup_element">
    <div class="ss_mashup_round_top"><div></div></div>
	<div class="ss_mashup_folder_header_view">
	  <c:if test="${ssConfigJspStyle != 'mobile'}">
		<a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
		  action="view_permalink" 
		  binderId="${mashupBinder.id}">
		  <ssf:param name="entityType" value="${mashupBinder.entityType}"/>
		  </ssf:url>"><span>${mashupBinder.title}</span></a>

		<c:if test="${!empty mashupBinder.description.text}">
		  <div class="ss_mashup_folder_description">
			<ssf:markup entity="${mashupBinder}">${mashupBinder.description.text}</ssf:markup>
			<div class="ss_clear"></div>
		  </div>
		</c:if>
	  </c:if>

	  <c:if test="${ssConfigJspStyle == 'mobile'}">
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
							folderId="${mashupBinder.id}" 
							action="__ajax_mobile" 
							operation="mobile_show_folder" 
							actionUrl="false" />"
		  ><span><c:if test="${empty mashupBinder.title}" >
		  (<ssf:nlt tag="entry.noTitle" />)</c:if>
		  <c:out value="${mashupBinder.title}" escapeXml="true" /></span></a>

		<c:if test="${!empty mashupBinder.description.text}">
		  <div class="ss_mashup_folder_description">
			<ssf:markup entity="${mashupBinder}" mobile="true">${mashupBinder.description.text}</ssf:markup>
			<div class="ss_clear"></div>
		  </div>
		</c:if>
	  </c:if>
	
	</div>

	<div class="ss_mashup_folder_list_open">


  <div class="folders">
    
	  <c:if test="${!empty ss_mashupBinderEntries[mashupBinderId]}">
			  <div class="entry-content">
			    <table class="tasks" cellspacing="2" cellpadding="5">
				  <tr>
					<th class="entry-caption" valign="top" nowrap>
					  <ssf:nlt tag="general.title"/>
					</th>

					<th class="entry-caption" valign="top" nowrap>
					  <ssf:nlt tag="task.dueDate"/>
					</th>

					<th class="entry-caption" valign="top" nowrap>
					  <ssf:nlt tag="task.priority"/>
					</th>

					<th class="entry-caption" valign="top" nowrap>
					  <ssf:nlt tag="task.status"/>
					</th>

					<th class="entry-caption" valign="top" nowrap>
					  <ssf:nlt tag="task.done"/>
					</th>

					<th class="entry-caption" valign="top" nowrap>
					  <ssf:nlt tag="task.assigned"/>
					</th>

				  </tr>
	    		<c:forEach var="entryFol2" items="${ss_mashupBinderEntries[mashupBinderId]}">
	    			<jsp:useBean id="entryFol2" type="java.util.Map" />
			  		  	  
				  <tr>
					<td class="entry-element" valign="top">
					  <div class="entry">
			  			<div class="entry-title">
						<ssf:titleLink 
							action="view_folder_entry" 
							entryId="${entryFol2._docId}" binderId="${entryFol2._binderId}" 
							entityType="${entryFol2._entityType}"  
							namespace="${renderResponse.namespace}" >		
							
							<ssf:param name="url" useBody="true">
								<ssf:url crawlable="true" adapter="true" 
								portletName="ss_forum" folderId="${entryFol2._binderId}" 
								action="view_folder_entry" entryId="${entryFol2._docId}" actionUrl="true" />
							</ssf:param>
							
							<c:out value="${entryFol2.title}" escapeXml="false"/>
						</ssf:titleLink>
						<c:if test="${!empty entryFol2._totalReplyCount}">
			    		          <span style="padding-left:6px;">(${entryFol2._totalReplyCount})</span>
			  		        </c:if>
			  		  </div>
			  		  
			  		  <c:if test="${!empty entryFol2._desc}">
			    		    <div style="padding-left:10px;" class="ss_smallprint">
			    		      <ssf:textFormat 
			      	  		formatAction="limitedDescription" 
			          		textMaxWords="10"><ssf:markup search="${entryFol2}" >${entryFol2._desc}</ssf:markup></ssf:textFormat>
		  	    		      <div class="ss_clear"></div>
		  	    		    </div>
			  		  </c:if>
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

					<td class="entry-element" valign="top">
					  <c:if test="${entryFol2.priority == 'p1'}">
					    <span><ssf:nlt tag="__task_priority_critical"/></span>
					  </c:if>
					  <c:if test="${entryFol2.priority == 'p2'}">
					    <span><ssf:nlt tag="__task_priority_high"/></span>
					  </c:if>
					  <c:if test="${entryFol2.priority == 'p3'}">
					    <span><ssf:nlt tag="__task_priority_medium"/></span>
					  </c:if>
					  <c:if test="${entryFol2.priority == 'p4'}">
					    <span><ssf:nlt tag="__task_priority_low"/></span>
					  </c:if>
					  <c:if test="${entryFol2.priority == 'p5'}">
					    <span><ssf:nlt tag="__task_priority_least"/></span>
					  </c:if>
					</td>

					<td class="entry-element" valign="top">
					  <c:if test="${entryFol2.status == 's1'}">
					    <span><ssf:nlt tag="__task_status_needs_action"/></span>
					  </c:if>
					  <c:if test="${entryFol2.status == 's2'}">
					    <span><ssf:nlt tag="__task_status_in_process"/></span>
					  </c:if>
					  <c:if test="${entryFol2.status == 's3'}">
					    <span><ssf:nlt tag="__task_status_completed"/></span>
					  </c:if>
					  <c:if test="${entryFol2.status == 's4'}">
					    <span><ssf:nlt tag="__task_status_cancelled"/></span>
					  </c:if>
					</td>

					<td class="entry-element" valign="top">
					  <c:if test="${entryFol2.completed == 'c000'}">
					    <span>0%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c010'}">
					    <span>10%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c020'}">
					    <span>20%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c030'}">
					    <span>30%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c040'}">
					    <span>40%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c050'}">
					    <span>50%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c060'}">
					    <span>60%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c070'}">
					    <span>70%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c080'}">
					    <span>80%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c090'}">
					    <span>90%</span>
					  </c:if>
					  <c:if test="${entryFol2.completed == 'c100'}">
					    <span>100%</span>
					  </c:if>
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
		 
		  	  		
	    	</div>
		</c:forEach>
			    </table>
			  </div>
	  </c:if>
	  <c:if test="${empty ss_mashupBinderEntries[mashupBinderId]}">
		<div style="padding:20px;"><ssf:nlt tag="folder.NoResults"/></div>
	  </c:if>

	</div>
  </div>





</div>
<div class="ss_mashup_round_bottom"><div></div></div>
<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
</li>
</c:if>
<% } %>
