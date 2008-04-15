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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div id="ss_para" "ss_link_5">
<div id="ss_hints"><em><ssf:nlt tag="relevance.foldersBeingTracked"/></em></div>
<c:forEach var="binder" items="${ss_trackedBinders}">
  <c:if test="${binder.entityType == 'folder'}">
	  <c:set var="showThisBinder" value="true"/>
	  <c:forEach var="calendar" items="${ss_trackedCalendars}">
	    <c:if test="${calendar.id == binder.id}"><c:set var="showThisBinder" value="false"/></c:if>
	  </c:forEach>
	
	  <c:if test="${showThisBinder}">
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
	    <a class="ss_link_5"
	      href="<ssf:url adapter="true" portletName="ss_forum" 
	    	action="view_permalink"
	    	binderId="${binder.id}">
	    	<ssf:param name="entityType" value="folder" />
	    	<ssf:param name="newTab" value="1" />
			</ssf:url>"
		  onClick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'folder', '${ss_namespace}', 'yes');"
	    ><span>${binder.title} (${binder.parentBinder.title})</span> </a>
	    &nbsp;<img src="<html:rootPath/>images/icons/folder_green_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" />
	    <img style="padding:4px 0px 0px 2px;" align="texttop"
          src="<html:rootPath/>images/pics/delete.gif"
          onClick="ss_trackedItemsDelete(this, '${binder.id}');"/>
	    </td></tr></tbody></table>
	  </c:if>
  </c:if>
</c:forEach>
<br/>
<br/>
<div id="ss_hints"><em><ssf:nlt tag="relevance.workspacesBeingTracked"/></em></div>
<c:forEach var="binder" items="${ss_trackedBinders}">
  <c:if test="${binder.entityType == 'workspace' && binder.definitionType == 8}">
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
	    <a class="ss_link_5"
	      href="<ssf:url adapter="true" portletName="ss_forum" 
	    	action="view_permalink"
	    	binderId="${binder.id}">
	    	<ssf:param name="entityType" value="workspace" />
	    	<ssf:param name="newTab" value="1" />
			</ssf:url>"
		  onClick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'workspace', '${ss_namespace}', 'yes');"
	    ><span>${binder.title} (${binder.parentBinder.title})</span> </a><img src="<html:rootPath/>images/icons/folder_cyan_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" />&nbsp;
	    <img style="padding:4px 0px 0px 2px;" align="texttop" src="<html:rootPath/>images/pics/delete.gif"
      onClick="ss_trackedItemsDelete(this, '${binder.id}');"/>
      </td></tr></tbody></table>
  </c:if>
</c:forEach>
	</div> <!-- end of ss_para -->

<div class="ss_clear_float"></div>
