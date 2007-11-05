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

<c:set var="folderIdList" value=""/>
<jsp:useBean id="folderIdList" type="java.lang.String" />
<c:if test="${!empty ss_mobileBinderList}">
<table cellspacing="0" cellpadding="0">
<c:forEach var="binder" items="${ss_mobileBinderList}">
<jsp:useBean id="binder" type="com.sitescape.team.domain.Binder" />
  <tr>
  <td>
      <span style="color:silver;">-</span>
  </td>
  <td>&nbsp;&nbsp;&nbsp;</td>
  <td>
	<c:if test="${binder.entityIdentifier.entityType == 'folder'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobil_show_folder" />"><span>${binder.title}</span></a>
	  <c:if test="${binder.parentBinder.entityIdentifier.entityType == 'folder'}">
	    <a style="padding-left:20px;" 
	    	href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.parentBinder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobil_show_folder" />">
			<span class="ss_smallprint ss_light">(${binder.parentBinder.title})</span></a>
	  </c:if>
	  <c:if test="${binder.parentBinder.entityIdentifier.entityType != 'folder'}">
	    <a style="padding-left:20px;" 
	    	href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.parentBinder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobil_show_workspace" />">
			<span  class="ss_smallprint ss_light">(${binder.parentBinder.title})</span></a>
	  </c:if>
	</c:if>
	<c:if test="${binder.entityIdentifier.entityType == 'workspace'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobil_show_workspace" />"><span>${binder.title}</span></a>
	</c:if>
	<c:if test="${binder.entityIdentifier.entityType == 'profiles'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    			folderId="${binder.id}" 
					action="__ajax_mobile" actionUrl="false" 
					operation="mobil_show_profiles" />"><span>${binder.title}</span></a>
	</c:if>
  </td>
  </tr>
  <%
  	if (!folderIdList.equals("")) folderIdList += " ";
  	folderIdList += binder.getId().toString();
  %>
</c:forEach>
</table>
</c:if>
</body>
</html>
