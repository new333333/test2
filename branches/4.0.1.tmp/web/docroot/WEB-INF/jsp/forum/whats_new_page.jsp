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
<c:set var="ids" value=""/>
<c:forEach var="entryWn" items="${ss_whatsNewBinder}">
  <c:if test="${!empty ids}"><c:set var="ids" value="${ids} "/></c:if>
  <c:set var="ids" value="${ids}${entryWn._docId}"/>
</c:forEach>

<c:set var="binderCounter" value="0"/> <% // set binder counter to zero (first pass) %>

<c:set var="binderCounter" value="${fn:length(ss_whatsNewBinder) }"/>

<c:set var="binderCounter2" value="0"/>  <% // set binder counter to zero (second pass) %>
<c:set var="column2Seen" value="0"/>
<c:set var="actionVar" value="view_ws_listing"/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:set var="actionVar" value="view_folder_listing"/>
</c:if>

<div>
	<c:choose>
		<c:when test="${ss_whatsUnseenType && !empty ss_whatsNewBinder}">
			<div id="ss_profile_box_h1" style="padding:5px 10px;">
			  <c:if test="${ssBinder.entityType == 'workspace'}">
			    <ssf:nlt tag="mobile.whatsUnreadWorkspace"/>
			  </c:if>
			  <c:if test="${ssBinder.entityType != 'workspace'}">
			    <ssf:nlt tag="mobile.whatsUnreadFolder"/>
			  </c:if>
			<span style="padding-left:10px;">
				<a class="ss_linkButton ss_smallprint" href="<ssf:url 
				  actionUrl="true"
				  action="${actionVar}" binderId="${ssBinder.id}"><ssf:param
				  name="operation" value="clear_unseen"/><ssf:param
				  name="type" value="${ss_type}"/><ssf:param
				  name="page" value="${ss_pageNumber - 1}"/><ssf:param
				  name="ids" value="${ids}"/><ssf:param
				  name="namespace" value="${ss_namespace}"/></ssf:url>" 
				  onClick="ss_clearWhatsUnseen(this, '${ssBinder.id}', '${ids}', '${ss_type}', '${ss_pageNumber}', 'previous', 'ss_whatsNewDiv', '${ss_namespace}');return false;"
				  title="<ssf:nlt tag="binder.markAsReadAlt"/>"
				><ssf:nlt tag="binder.markAsRead"/></a>
			</span>
		</c:when>
		<c:otherwise>
			<div id="ss_profile_box_h1" style="padding:5px 10px;">
			  <c:if test="${ssBinder.entityType == 'workspace'}">
			    <c:if test="${ss_whatsUnseenType}"><ssf:nlt tag="mobile.whatsUnreadWorkspace"/></c:if>
			    <c:if test="${!ss_whatsUnseenType}"><ssf:nlt tag="mobile.whatsNewWorkspace"/></c:if>
			  </c:if>
			  <c:if test="${ssBinder.entityType != 'workspace'}">
			    <c:if test="${ss_whatsUnseenType}"><ssf:nlt tag="mobile.whatsUnreadFolder"/></c:if>
			    <c:if test="${!ss_whatsUnseenType}"><ssf:nlt tag="mobile.whatsNewFolder"/></c:if>
			  </c:if>
		</c:otherwise>
	</c:choose>
	<span style="padding-left: 10px;">
		<c:if test="${ss_pageNumber > '0'}">
			<a href="<ssf:url 
			  action="${actionVar}" binderId="${ssBinder.id}"><ssf:param
			  name="type" value="${ss_type}"/><ssf:param
			  name="page" value="${ss_pageNumber - 1}"/><ssf:param
			  name="namespace" value="${ss_namespace}"/></ssf:url>" 
			  onClick="ss_showWhatsNewPage(this, '${ssBinder.id}', '${ss_type}', '${ss_pageNumber}', 'previous', 'ss_whatsNewDiv', '${ss_namespace}');return false;"
			>
			  <img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_.png" 
				  <ssf:alt tag=""/> title="<ssf:nlt tag="general.previousPage"/>"/>
			</a>
		</c:if>
		<c:if test="${empty ss_pageNumber || ss_pageNumber <= '0'}">
		  <img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_g.png"
			  <ssf:alt tag=""/>/>
		</c:if>
		<c:if test="${!empty ss_whatsNewBinder}">
		  <a href="<ssf:url 
			action="${actionVar}" binderId="${ssBinder.id}"><ssf:param
			name="type" value="${ss_type}"/><ssf:param
			name="page" value="${ss_pageNumber + 1}"/><ssf:param
			name="namespace" value="${ss_namespace}"/></ssf:url>" 
			onClick="ss_showWhatsNewPage(this, '${ssBinder.id}', '${ss_type}', '${ss_pageNumber}', 'next', 'ss_whatsNewDiv', '${ss_namespace}');return false;"
		  >
			<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_.png" 
				<ssf:alt tag=""/> title="<ssf:nlt tag="general.nextPage"/>"/>
		  </a>
		</c:if>
		<c:if test="${empty ss_whatsNewBinder}">
			<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_g.png"
			  <ssf:alt tag=""/>/>
		</c:if>
		<div style="position:absolute; top:12px; right:30px;">
			<a
			  onClick="ss_hideDivNone('ss_whatsNewDiv${ss_namespace}'); return false;"><img 
			  <ssf:alt tag="alt.hide"/> title="<ssf:nlt tag="button.close"/>" border="0" src="<html:imagesPath/>icons/close_gray16.png"/>
			</a>
		</div>
	</span>
	</div>
	
 <div id="ss_dashboard_content" class="marginleft1" style="padding: 5px;">

	<table cellpadding="0" cellspacing="0">
		<tr>
			<td width="50%" style="padding: 10px; vertical-align: top;">
			<!-- Start Left Column --> 
				<c:if test="${empty ss_whatsNewBinder && ss_pageNumber > '0'}">
				  <div class="ss_italic" style="padding:5px; color: #fff;"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></div>
				</c:if>
				<c:if test="${empty ss_whatsNewBinder && (empty ss_pageNumber || ss_pageNumber <= '0')}">
				  <div class="ss_italic" style="padding: 20px; color: #c4c4c4;"><ssf:nlt tag="whatsnew.noEntriesFound"/></div>
				</c:if>
				<c:forEach var="entryWn" items="${ss_whatsNewBinder}">
				  <c:if test="${binderCounter2 >= (binderCounter/2) && column2Seen == '0'}">
					<c:set var="column2Seen" value="1"/>
					</td>
					<td width="50%" style="padding: 10px; vertical-align: top;">
					<!-- Start Right Column -->
				  </c:if>
				  <jsp:useBean id="entryWn" type="java.util.Map" />
				  
				<div class="ss_newinbinder">	
				  <div class="item">
				  		<b>
					  <c:set var="isDashboard" value="yes"/>
					  <ssf:titleLink hrefClass="ss_link_2 header"
						entryId="${entryWn._docId}" binderId="${entryWn._binderId}" 
						entityType="${entryWn._entityType}" 
						namespace="${ss_namespace}" 
						isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entryWn._binderId}" 
							  action="view_folder_entry" entryId="${entryWn._docId}" actionUrl="true" />
						</ssf:param>
						<c:out value="${entryWn.title}" escapeXml="false"/>
					  </ssf:titleLink>
			 			</b>
						  <div class="item-sub margintop1">
							<ssf:showUser user='<%=(org.kablink.teaming.domain.User)entryWn.get("_principal")%>'
							titleStyle="ss_link_1" /> 
					  
							  <span class="ss_entryDate">
								<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
								value="${entryWn._modificationDate}" type="both" 
								timeStyle="short" dateStyle="medium" />
							  </span>
							   
							  <div class="ss_link_2 list-indent">
									<c:set var="path" value=""/>
									<c:if test="${!empty ss_whatsNewBinderFolders[entryWn._binderId]}">
									  <c:set var="path" value="${ss_whatsNewBinderFolders[entryWn._binderId]}"/>
									  <c:set var="title" value="${ss_whatsNewBinderFolders[entryWn._binderId].parentBinder.title} // ${ss_whatsNewBinderFolders[entryWn._binderId].title}"/>
									</c:if>
									<c:set var="isDashboard" value="yes"/>
									<c:if test="${!empty path}">
										<a href="javascript: ;"
										  onClick="return ss_gotoPermalink('${entryWn._binderId}', '${entryWn._binderId}', 'folder', '${ss_namespace}', 'yes');"
										  title="${path}"
										 >
										 <span>${title}</span></a>
									</c:if>
								</div>	
				  
						  <c:if test="${!empty entryWn._desc}">
							<div class="ss_summary list-indent"><ssf:textFormat 
							  formatAction="limitedDescription" 
							  textMaxWords="15"><ssf:markup search="${entryWn}">${entryWn._desc}</ssf:markup></ssf:textFormat>
							</div>
						  </c:if>
					  </div>
				</div>
				</div>
				<c:set var="binderCounter2" value="${binderCounter2 + 1}"/>
			   </c:forEach>
		
			</td>
		</tr>
	</table>
</div><!-- end of inset -->
</div><!-- end of center -->
</div><!-- end of content -->
</div><!-- end of ss_para -->

