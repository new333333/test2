<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div id="ss_para">
	<div id="ss_viewedItems">
	
		<div id="ss_title" class="ss_pt_title ss_green">
			<ssf:nlt tag="relevance.visitedEntries">
				<ssf:param name="value" useBody="true"><ssf:userTitle user="${ssBinder.owner}"/></ssf:param>
			</ssf:nlt>

		<span class="col-nextback-but">
				<c:if test="${ssEntriesViewedPage > '0'}">
				<a href="javascript: ;" 
				  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'entriesViewed', '${ssEntriesViewedPage}', 'previous', 'ss_dashboardEntriesViewed', '${ss_relevanceDashboardNamespace}');return false;">
				  <img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_.png" 
				  title="<ssf:nlt tag="general.previousPage"/>" <ssf:alt/>/>
				</a>
				</c:if>
	
				<c:if test="${empty ssEntriesViewedPage || ssEntriesViewedPage <= '0'}">
				<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_g.png" <ssf:alt/>/>
				</c:if>
	
				<c:if test="${!empty ssEntriesViewed}">
				<a href="javascript: ;" 
				  onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'entriesViewed', '${ssEntriesViewedPage}', 'next', 'ss_dashboardEntriesViewed', '${ss_relevanceDashboardNamespace}');return false;">
				  <img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_.png"
				  title="<ssf:nlt tag="general.nextPage"/>" <ssf:alt/>/>
				</a>
				</c:if>
	
				<c:if test="${empty ssEntriesViewed}">
				<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_g.png" <ssf:alt/>/>
				</c:if>
				
			</span>
		</div>

		<c:set var="count" value="0"/>
		<c:forEach var="entryMap" items="${ssEntriesViewed}">

		<c:if test="${entryMap.type == 1}">
			<c:set var="entry" value="${entryMap.entity}"/>
			<jsp:useBean id="entry" type="org.kablink.teaming.domain.Entry" />
    
			<div class="ss_v_entries">
				<div class="item">
					<b>
					<c:set var="isDashboard" value="yes"/>
					<ssf:titleLink hrefClass="ss_link_2"
						entryId="${entry.id}" binderId="${entry.parentBinder.id}" 
						entityType="${entry.entityType}" 
						namespace="${ss_namespace}" 
						isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entry.parentBinder.id}" 
							  action="view_folder_entry" entryId="${entry.id}" actionUrl="true" />
						</ssf:param>
						<c:out value="${entry.title}" escapeXml="false"/>
					</ssf:titleLink>
					</b>
					<div class="item-sub margintop1">	  
						<span>
							<ssf:showUser user="${entry.creation.principal}" titleStyle="ss_link_1"/>
						</span>
						<span class="ss_entryDate">
							<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
						  value="${entry.modification.date}" type="both" 
						  timeStyle="short" dateStyle="medium" />
						</span>
						   
						<div class="ss_link_2 list-indent">
							<a href="javascript: ;"
								onclick="return ss_gotoPermalink('${entry.parentBinder.id}', '${entry.parentBinder.id}', 'folder', '${ss_namespace}', 'yes');">
								<span>${entry.parentBinder.parentBinder.title} // ${entry.parentBinder.title}</span>
							</a>
						</div> 
						
						<c:if test="${!empty entry.description}">
							<div class="ss_summary list-indent">
								<ssf:textFormat 
								formatAction="limitedDescription" 
								textMaxWords="15"><ssf:markup entity="${entry}">${entry.description}</ssf:markup></ssf:textFormat>
							</div>
					    </c:if>
					</div>
				<c:set var="count" value="${count + 1}"/>
			</div>
	    </c:if>
	</div><!-- end of viewed entries -->
	

    <c:if test="${entryMap.type == 9}">
    <c:set var="entry2" value="${entryMap.entity}"/>
    <jsp:useBean id="entry2" type="org.kablink.teaming.domain.Entry" />

	<div id="ss_viewedItems" class="ss_v_attachments">
	    <div class="item">  
			<span class="ss_link_3">
				<a target="_blank" href="<ssf:fileUrl entity="${entry2}" fileId="${entryMap.file_id}"/>">${entryMap.description}</a>
			</span>

			<div class="item-sub margintop1">	  

				  <span>
					<ssf:showUser user="${entry2.creation.principal}" titleStyle="ss_link_1"/>
				  </span>
				  <span class="ss_entryDate">
					<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				  value="${entry2.modification.date}" type="both" 
				  timeStyle="short" dateStyle="medium" />
				  </span>
			
					<c:set var="isDashboard" value="yes"/>
					<div class="ss_link_2 list-indent">
						<ssf:titleLink hrefClass="ss_link_2"
							entryId="${entry2.id}" binderId="${entry2.parentBinder.id}" 
							entityType="${entry2.entityType}" 
							namespace="${ss_namespace}" 
							isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
							<ssf:param name="url" useBody="true">
								<ssf:url adapter="true" portletName="ss_forum" folderId="${entry2.parentBinder.id}" 
								  action="view_folder_entry" entryId="${entry2.id}" actionUrl="true" />
							</ssf:param>
							<c:out value="${entry2.title}" escapeXml="false"/>
						</ssf:titleLink>
					</div>
				  
				  <div class="ss_link_2 list-indent">
					<a href="javascript: ;"
						onclick="return ss_gotoPermalink('${entry2.parentBinder.id}', '${entry2.parentBinder.id}', 'folder', '${ss_namespace}', 'yes');"
						><span>${entry2.parentBinder.parentBinder.title} // ${entry2.parentBinder.title}</span></a>
				  </div> 
				  <c:if test="${!empty entry2.description}">
					<div class="ss_summary list-indent">
						<span class="ss_summary"><ssf:textFormat 
						  formatAction="limitedDescription" 
						  textMaxWords="10"><ssf:markup entity="${entry2}">${entry2.description}</ssf:markup></ssf:textFormat></span>
					</div>
				  </c:if>
			</div>	
			
	<c:set var="count" value="${count + 1}"/>
	</div>
	</div><!-- end of viewed attachments -->

    </c:if>
  </c:forEach>

  <c:if test="${empty ssEntriesViewed && ss_pageNumber > '0'}">
    <span class="ss_italic" style="padding: 5px;"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
  </c:if>

</div> <!-- end of viewed items -->
</div> <!-- end of ss_para -->
