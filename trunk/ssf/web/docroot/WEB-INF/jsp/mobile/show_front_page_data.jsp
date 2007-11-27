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

<div class="ss_mobile">
<c:if test="${!empty ss_mobileBinderList}">
<div>
  <scan class="ss_bold"><ssf:nlt tag="portlet.title.bookmarks"/></span>
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
			<span class="ss_smallprint ss_light">(${binder.parentBinder.title})</span></a>
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
</c:if>
