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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div id="ss_para">

	<div id="ss_title" class="ss_pt_title ss_green"><ssf:nlt tag="relevance.sharedItems"/>
		 <span class="col-nextback-but">
			<c:if test="${ss_sharedEntitiesPage > '0'}">
				<a href="javascript: ;" 
					onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'shared', '${ss_sharedEntitiesPage}', 'previous', 'ss_dashboardShared', '${ss_relevanceDashboardNamespace}');return false;">
					<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_.png" 
					title="<ssf:nlt tag="general.previousPage"/>" <ssf:alt/>/>
				</a>
			</c:if>
			<c:if test="${empty ss_sharedEntitiesPage || ss_sharedEntitiesPage <= '0'}">
				<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_g.png" <ssf:alt/>/>
			</c:if>
			<c:if test="${!empty ss_sharedEntities}">
				<a href="javascript: ;" 
					onclick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'shared', '${ss_sharedEntitiesPage}', 'next', 'ss_dashboardShared', '${ss_relevanceDashboardNamespace}');return false;">
					<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_.png"
					title="<ssf:nlt tag="general.nextPage"/>" <ssf:alt/>/>
				</a>
			</c:if>
			<c:if test="${empty ss_sharedEntities}">
				<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_g.png" <ssf:alt/>/>
			</c:if>
		</span>
	</div><!-- end of ss_nextPage -->

	<div id="ss_today">
		<div class="ss_shared_para">
	
			  <c:forEach var="sharedItem" items="${ss_sharedEntities}">
			   
				 <div class="item">
				 	<ssf:nlt tag="relevance.sharedEntityLine">
				 
					  <ssf:param name="value" useBody="true">
						<ssf:nlt tag="relevance.sharedItemsLabel" />
							<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
									  value="${sharedItem.sharedDate}" type="both" 
									  timeStyle="short" dateStyle="short" />
						<div>
							<ssf:showUser user="${sharedItem.referer}" titleStyle="ss_link_2" />
					  	</div>
					  </ssf:param>
					  			
					  	<ssf:param name="value" useBody="true">
					   		<div class="item-sub list-indent">
								<c:if test="${sharedItem.entity.entityType == 'workspace' || sharedItem.entity.entityType == 'folder'}">
								  <a href="javascript: ;"
									onclick="return ss_gotoPermalink('${sharedItem.entity.id}', '${sharedItem.entity.id}', '${sharedItem.entity.entityType}', '${ss_namespace}', 'yes');"
									>
									<span>${sharedItem.entity.title}</span>
								  </a>
								</c:if>
			
								<c:if test="${sharedItem.entity.entityType == 'folderEntry'}">
									<c:set var="isDashboard" value="yes"/>
									  <ssf:titleLink hrefClass="ss_link_2"
										entryId="${sharedItem.entity.id}" binderId="${sharedItem.entity.parentBinder.id}" 
										entityType="${sharedItem.entity.entityType}" 
										namespace="${ss_namespace}" 
										isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										<ssf:param name="url" useBody="true">
											<ssf:url adapter="true" portletName="ss_forum" folderId="${sharedItem.entity.parentBinder.id}" 
											  action="view_folder_entry" entryId="${sharedItem.entity.id}" actionUrl="true" />
										</ssf:param>
										<c:out value="${sharedItem.entity.title}" escapeXml="false"/>
									  </ssf:titleLink>
								</c:if>
							</div>
						</ssf:param>
					</ssf:nlt>
				</div>
			  </c:forEach>
			
			  <c:if test="${empty ss_sharedEntities && ss_pageNumber > '0'}">
				<span class="ss_italic" style="padding:5px;"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
			  </c:if>
			
		</div><!-- end of ss_shared_para -->
	</div><!-- end of ss_today -->
</div><!-- end of ss_para -->
