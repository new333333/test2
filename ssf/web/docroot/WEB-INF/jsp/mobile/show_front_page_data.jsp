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
<%@ page import="com.sitescape.team.ObjectKeys" %>

<div class="ss_mobile">
<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<c:if test="${ssUser.internalId == guestInternalId}">
  <c:if test='<%= !com.sitescape.team.util.SPropsUtil.getBoolean("form.login.auth.disallowed",false) %>' >
    <div>
    <a href="<ssf:url action="__ajax_mobile" actionUrl="false" 
					operation="mobile_login" />"
    >
    <span><ssf:nlt tag="login"/></span>
    </a>
    </div>
  </c:if>
</c:if>

<c:if test="${ssUser.internalId != guestInternalId}">
<div>
  <span>
    <ssf:nlt tag="mobile.welcome">
      <ssf:param name="value" useBody="true">
        <a href="<ssf:url adapter="true" portletName="ss_forum" 
			    action="__ajax_mobile"
			    operation="mobile_show_workspace"
			    binderId="${ssUser.workspaceId}" />">${ssUser.title}</a>
      </ssf:param>
    </ssf:nlt>
  </span>
</div>
<br/>
</c:if>

<div>
  <span class="ss_bold"><ssf:nlt tag="toolbar.menu.whatsNew"/></span>
  <br/>
  <div style="padding-left:10px;">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_whats_new" ><ssf:param
					name="type" value="whatsNewTracked"/></ssf:url>">
	    <span class="ss_bold"><ssf:nlt tag="mobile.whatsNewTracked"/></span>
	  </a>
	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_whats_new" ><ssf:param
					name="type" value="whatsNew"/></ssf:url>">
	    <span class="ss_bold"><ssf:nlt tag="mobile.whatsNewSite"/></span>
	  </a>
  </div>
</div>

<br/>

<%@ include file="/WEB-INF/jsp/mobile/miniblog.jsp" %>

<br/>

<c:if test="${!empty ss_mobileBinderList}">
<div>
  <span class="ss_bold"><ssf:nlt tag="portlet.title.bookmarks"/></span>
</div>
<table class="ss_mobile" cellspacing="0" cellpadding="0">
<c:forEach var="binder" items="${ss_mobileBinderList}">
<jsp:useBean id="binder" type="com.sitescape.team.domain.Binder" />
 <tr>
  <td valign="top">
   <span style="color:silver;">
	  <c:set var="folderIdFound" value="0"/>
	  <c:forEach var="entry" items="${ss_unseenCounts}">
	    <c:if test="${entry.key.id == binder.id}"><%--
	      --%>${entry.value}<%--
	      --%><c:set var="folderIdFound" value="1"/><%--
	    --%></c:if>
	  </c:forEach>
   </span>&nbsp;&nbsp;</td>
  <td>
	<c:if test="${binder.entityIdentifier.entityType == 'folder'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_show_folder" />"><span>${binder.title}</span></a>
	  <c:if test="${binder.parentBinder.entityIdentifier.entityType == 'folder'}">
	    <a style="padding-left:20px;" 
	    	href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.parentBinder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_show_folder" />">
			<span class="ss_mobile_small ss_mobile_light">(${binder.parentBinder.title})</span></a>
	  </c:if>
	</c:if>
	<c:if test="${binder.entityIdentifier.entityType == 'workspace'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_show_workspace" />"><span>${binder.title}</span></a>
	</c:if>
	<c:if test="${binder.entityIdentifier.entityType == 'profiles'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_show_workspace" />"><span>${binder.title}</span></a>
	</c:if>
  </td>
 </tr>
</c:forEach>
</table>
</c:if>

</div>

<br/>

<c:if test="${!empty ss_UserQueries}">
<div class="ss_mobile">
<span class="ss_bold"><ssf:nlt tag="searchResult.savedSearchTitle"/></span>
<br/>
<c:forEach var="query" items="${ss_UserQueries}" varStatus="status">
  <a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobile_show_search_results"><ssf:param 
					name="ss_queryName" value="${query.key}" /></ssf:url>">${query.key}</a>
  <c:if test="${!status.last}"><br/></c:if>
</c:forEach>
</div>
<br/>
</c:if>

<c:if test="${ss_accessControlMap['ss_canViewUserProfiles'] == true}">
<div class="ss_mobile">
<form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="true" 
					operation="mobile_find_people" />">
<span class="ss_bold"><ssf:nlt tag="navigation.findUser"/></span>
<br/>
<input type="text" size="15" name="searchText" class="ss_mobile_small" />&nbsp;<input 
  type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" class="ss_mobile_small" />
</form>
</div>
<br/>
</c:if>

<div class="ss_mobile">
<form method="post"
	action="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="true" 
					operation="mobile_show_search_results" />">
<span class="ss_bold"><ssf:nlt tag="searchForm.button.label"/></span>
<br/>
<input name="searchText" type="text" size="15" class="ss_mobile_small" />&nbsp;<input type="submit" 
  name="searchBtn" value="<ssf:nlt tag="button.ok"/>" class="ss_mobile_small" />
<input type="hidden" name="quickSearch" value="true"/>
</form>
<br/>
<br/>
<%@ include file="/WEB-INF/jsp/mobile/footer.jsp" %>
</div>
