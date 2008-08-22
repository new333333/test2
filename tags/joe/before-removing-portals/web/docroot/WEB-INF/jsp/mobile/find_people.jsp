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
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<div class="ss_mobile">
<table class="ss_mobile" cellspacing="0" cellpadding="0" border="0">
<c:forEach var="user" items="${ssUsers}" >
  <tr>
	<td>
	<c:if test="${!empty user['_workspaceId']}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    folderId="${user['_workspaceId']}"
	    action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
	  <c:out value="${user.title}"/>
	  </a>
	</c:if>
	<c:if test="${empty user['_workspaceId']}">
	  <c:out value="${user.title}"/>
	</c:if>
  </td></tr>
</c:forEach>
<tr><td></td></tr>
<tr><td>
<table><tr><td width="30">
<c:if test="${!empty ss_prevPage}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" 
	operation="mobile_find_people" 
	actionUrl="false" ><ssf:param 
	name="searchText" value="${ss_searchText}"/><ssf:param 
	name="pageNumber" value="${ss_prevPage}"/></ssf:url>">&lt;&lt;&lt;</a>
</c:if>
</td><td style="padding-left:30px;">
<c:if test="${!empty ss_nextPage}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" 
	operation="mobile_find_people" 
	actionUrl="false" ><ssf:param 
	name="searchText" value="${ss_searchText}"/><ssf:param 
	name="pageNumber" value="${ss_nextPage}"/></ssf:url>">&gt;&gt;&gt;</a>
</c:if>
</td></tr></table>
</td></tr>
</table>
<br/>
<div class="ss_mobile">
<form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="true" 
					operation="mobile_find_people" />">
<span class="ss_bold"><ssf:nlt tag="navigation.findUser"/></span>
<br/>
<input type="text" size="15" name="searchText" value="${ss_searchText}"/><input 
  type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"/>
</form>
</div>
<br/>
<div class="ss_mobile_breadcrumbs ss_mobile_small">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
	><ssf:nlt tag="mobile.returnToTop"/></a>
</div>
</div>

</body>
</html>
