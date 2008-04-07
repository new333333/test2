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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%
	java.util.Set ss_showUserGroupMembers = (java.util.Set) request.getAttribute("ss_showUserGroupMembers");
%>

<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${ssConfigJspStyle != 'mobile'}">

	<c:choose>
		<c:when test="${!ss_showUserIsGroup}">
			<c:if test="${ss_showUserShowPresence}">
				<ssf:presenceInfo user="${ss_showUserUser}" showTitle="true" titleStyle="${ss_showUserTitleStyle}" /> 
			</c:if>		
		</c:when>
		<c:otherwise>
			<img border="0" src="<html:imagesPath/>pics/group_icon_small.gif" />
		</c:otherwise>
	</c:choose>
	

	<c:if test="${!ss_showUserShowPresence || ss_showUserIsGroup}">
		<c:if test="${ss_showUserUser.active}">
			<ssf:ifadapter>
				<c:if test="${!empty ss_showUserUser.workspaceId}">
				  	<c:if test="${!ss_showUserIsGroup}">
					  <a 
				  		href="<ssf:url adapter="true" portletName="ss_forum" 
						    action="view_permalink"
						    binderId="${ss_showUserUser.workspaceId}">
						    <ssf:param name="entityType" value="workspace" />
						    <ssf:param name="newTab" value="1" />
							</ssf:url>"
				  		onclick="ss_openUrlInParentWorkarea(this.href, '${ss_showUserUser.workspaceId}', 'view_ws_listing');return false;"
					  >
			  		</c:if>
					  <span id="${ss_showUserUser.id}" 
					    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
			  	<c:choose>
				  	<c:when test="${!ss_showUserIsGroup}">
					  </a>
			  		</c:when>
			  		<c:otherwise>
				  		<a href="javascript: //"
				  		onclick="dojo.html.toggleShowing('ss_show_user_${ss_showUserInstanceCount}'); return false;" class="ss_fineprint"><ssf:nlt tag="showUser.group.members"><ssf:param name="value" value="${fn:length(ss_showUserGroupMembers)}"/></ssf:nlt></a>
				  		<div id="ss_show_user_${ss_showUserInstanceCount}" style="display: none;">
					  		<ul>
								<c:forEach var="member" items="${ss_showUserGroupMembers}" >
							 	 <li><ssf:showUser user="${member}" showPresence="${ss_showUserShowPresence}"/></li>
								</c:forEach>
					  		</ul>
				  		</div>
			  		</c:otherwise>
			  	</c:choose>	
         	  </c:if>
         	  <c:if test="${empty ss_showUserUser.workspaceId}">
          	      <span id="${ss_showUserUser.id}" 
            	       class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
              </c:if>		  	
			</ssf:ifadapter>
			<ssf:ifnotadapter>
			  <c:if test="${!empty ss_showUserUser.workspaceId}">
			  <a 
		  	  	<c:choose>
				  	<c:when test="${!ss_showUserIsGroup}">
					  	href="<ssf:url windowState="maximized"><ssf:param 
						  	name="action" value="view_ws_listing"/><ssf:param 
						  	name="binderId" value="${ss_showUserUser.workspaceId}"/><ssf:param 
						  	name="newTab" value="1"/></ssf:url>"
						 onclick="ss_openUrlInWorkarea(this.href, '${ss_showUserUser.workspaceId}', 'view_ws_listing');return false;"
			  		</c:when>
			  		<c:otherwise>
				  		href="javascript: //"
			  		</c:otherwise>
			  	</c:choose>
			  ><span id="${ss_showUserUser.id}"  
			    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span></a>
			    <c:if test="${ss_showUserIsGroup}">
			  		<a href="javascript: //"
			  		onclick="dojo.html.toggleShowing('ss_show_user_${ss_showUserInstanceCount}'); return false;" class="ss_fineprint"><ssf:nlt tag="showUser.group.members"><ssf:param name="value" value="${fn:length(ss_showUserGroupMembers)}"/></ssf:nlt></a>
			  		<div id="ss_show_user_${ss_showUserInstanceCount}" style="display: none;">
				  		<ul>
							<c:forEach var="member" items="${ss_showUserGroupMembers}" >
						 	 <li><ssf:showUser user="${member}" showPresence="${ss_showUserShowPresence}"/></li>
							</c:forEach>
				  		</ul>
			  		</div>
			    </c:if>
			  </c:if>
			  <c:if test="${empty ss_showUserUser.workspaceId}">
				  <span id="${ss_showUserUser.id}"  
				    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
			  </c:if>
			</ssf:ifnotadapter>
		</c:if>
		<c:if test="${!ss_showUserUser.active}">
		  <span id="${ss_showUserUser.id}" 
		    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
		</c:if>
	</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'mobile'}">
  <c:if test="${!empty ss_showUserUser.workspaceId}">
  	  	<c:choose>
		  	<c:when test="${!ss_showUserIsGroup}">  
		  	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${ss_showUserUser.workspaceId}" />"><span>${ss_showUserUser.title}</span></a>
			</c:when>
	  		<c:otherwise>
		  		<ul>
					<c:forEach var="member" items="${ss_showUserGroupMembers}" >
						<li><ssf:showUser user="${member}" showPresence="${ss_showUserShowPresence}"/></li>
					</c:forEach>
		  		</ul>
	  		</c:otherwise>
	  	</c:choose>
  </c:if>
  <c:if test="${empty ss_showUserUser.workspaceId}">
  <span>${ss_showUserUser.title}</span>
  </c:if>
</c:if>
