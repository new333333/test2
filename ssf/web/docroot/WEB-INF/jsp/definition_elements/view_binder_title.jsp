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
<%@ page import="org.kablink.teaming.web.util.BinderHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ssDefEntryId" value="${ssDefinitionEntry.id}" scope="request" />
<jsp:useBean id="ssDefEntryId" type="java.lang.Long" scope="request" />
<%
	String title = BinderHelper.getUserWorkspaceOwnerTitle(ssDefEntryId);//Long.parseLong(ssDefEntryId));
%>
<% //Workspace title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_breadcrumbsShowIdRoutine}">
  <c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowIdNoWS" scope="request" />
</c:if>
<c:if test="${empty ss_breadcrumbsTreeName}">
  <c:set var="ss_breadcrumbsTreeName" value="wsTree" scope="request" />
</c:if>
<c:set var="actionVar" value="view_ws_listing"/>
<c:set var="actionVar2" value="view_folder_listing"/>
<c:if test="${ssConfigJspStyle != 'template'}">
	<c:if test="${ssDefinitionEntry.parentBinder.entityType == 'folder'}">
	  <c:set var="actionVar" value="view_folder_listing"/>
	</c:if>
	<c:if test="${ssDefinitionEntry.parentBinder.entityType == 'profiles'}">
	  <c:if test="${ssDefinitionEntry.definitionType == '12'}">
	    <c:set var="actionVar" value="view_ws_listing"/>
	  </c:if>
	  <c:if test="${ssDefinitionEntry.definitionType != '12'}">
	    <c:set var="actionVar" value="view_profile_listing"/>
	  </c:if>
	  
	</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">
	<c:set var="actionVar" value="configure_configuration"/>
	<c:set var="actionVar2" value="configure_configuration"/>
</c:if>

<div id="ss_profile_box">

<c:if test="${ssDefinitionEntry.entityType == 'workspace'}">
<c:if test="${ssDefinitionEntry.definitionType != '12'}">
	<ul class="ss_horizontal ss_nobullet">
	  <li>
	  <div id="ss_profile_box_h1">
	  <div class="ss_treeWidget">
	  <a href="<ssf:url crawlable="true"
           adapter="true" portletName="ss_forum"
           folderId="${ssDefinitionEntry.id}" 
           action="${actionVar}"/>">
	    <c:if test="${empty ssDefinitionEntry.title}">
          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
        </c:if>
        <span><c:out value="${ssDefinitionEntry.title}" escapeXml="true"/></span>
      </a> 
      </div>
      </div>  
	  </li>
	</ul>
</c:if>

<c:if test="${ssDefinitionEntry.definitionType == '12'}">	

<table>
<tbody>
<tr>
<td align="left" valign="top">
<c:set var="binderId" value="${ssDefinitionEntry.id}" />
<c:set var="userWorkspaceId" value="${ssDefinitionEntry.owner.workspaceId}" />
<c:if test="${binderId == userWorkspaceId}">
<div>
	<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
		value="${ssDefinitionEntry.owner.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
		value="${ssDefinitionEntry.owner.creation.principal.id}"/><ssf:param name="newTab" 
		value="1" /></ssf:url>" <ssf:title tag="title.goto.profile.page" />>
		<ssf:buddyPhoto 
			user="${ssDefinitionEntry.owner}" 
			folderId="${ssDefinitionEntry.owner.parentBinder.id}" entryId="${ssDefinitionEntry.owner.id}"
			scaled="true" />
	</a>
 </div>
</c:if>    
</td>
<td align="left" valign="top">
	<div class="ss_user_info">
	  <div id="ss_profile_box_h1">
	  <div class="ss_profile_title">
	  <a href="<ssf:url crawlable="true"
           adapter="true" portletName="ss_forum"
           folderId="${ssDefinitionEntry.id}" 
           action="${actionVar}"/>">

		<% if ((null != title) && (0 < title.length())) { %>
	        <span><c:out value="<%= title %>" escapeXml="true"/></span>
		<% } else { %>           
		    <c:if test="${empty ssDefinitionEntry.title}">
	          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
	        </c:if>
	        <span><c:out value="${ssDefinitionEntry.title}" escapeXml="true"/></span>
	    <% } %>
      </a>
      <% /* Bugzilla 675543:  The user profile lives as the       */ %>
      <% /*    business card item in the relevance dashboard.  If */ %>
      <% /*    there's no relevance dashboard, don't show the     */ %>
      <% /*    profile link.                                      */ %> 
      <c:if test="${ssConfigJspStyle != 'template' && !empty ssRelevanceDashboardConfigElement}">
	    <a href="<ssf:url crawlable="true"
        	adapter="true" portletName="ss_forum"
           	folderId="${ssDefinitionEntry.id}" 
           	action="${actionVar}" ><ssf:param 
		   	name="profile" value="1" /></ssf:url>">
			<span class="ss_profile"><ssf:nlt tag="relevance.tab.profile"/></span></a>
      </c:if>
      </div>
      </div>  

	  <% // Status %>
	  <jsp:include page="/WEB-INF/jsp/profile/user_status.jsp" />
	</div>
    <div class="ss_clear"></div>
</td>
</tr>
</tbody>
</table>




</c:if>
</c:if>
    
	<c:if test="${ssDefinitionEntry.entityType == 'profiles'}">
	<div id="ss_profile_box_h1">	
  	<ul class="ss_horizontal ss_nobullet">
	  <li>
	  <div class="ss_treeWidget">
	  <a href="<ssf:url crawlable="true"
           adapter="true" portletName="ss_forum"
           folderId="${ssDefinitionEntry.id}" 
           action="${actionVar}"/>">
	    <c:if test="${empty ssDefinitionEntry.title}">
          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
        </c:if>
        <span><c:out value="${ssDefinitionEntry.title}" escapeXml="true"/></span>
      </a>
      </div>
      </li>
      </ul>
    </div>
	    </c:if>
    
	<c:if test="${ssDefinitionEntry.entityType == 'folder'}">
	  <%@ include file="/WEB-INF/jsp/definition_elements/view_binder_title_folder.jsp" %>
    </c:if>
	
	<div class="ss_clear"></div>
<c:set var="ss_binderTitleShown" value="true" scope="request"/>


	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${item}" 
	  configJspStyle="${ssConfigJspStyle}"
	  entry="${ssDefinitionEntry}" />
    
  </div><!-- end of box -->
