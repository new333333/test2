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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!empty ssBinder.title}">
  <c:set var="ss_windowTitle" value="${ssBinder.title}" scope="request"/>
</c:if>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<div class="ss_mobile">
<c:if test="${!empty ssBinder.parentBinder}">
<div class="ss_mobile_breadcrumbs">
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />">${ssBinder.parentBinder.title}</a>
<br/>&nbsp;&nbsp;</c:if>
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />">${ssBinder.title}</a>
</div>
<br/>
<c:if test="${ssBinder.definitionType == '12' && !empty ssWorkspaceCreator}">
 <%-- This is a user workspace --%>
 <c:if test="${!empty ssWorkspaceCreator.customAttributes['picture']}">
  <div>
  <c:set var="selections" value="${ssWorkspaceCreator.customAttributes['picture'].value}" />
  <c:set var="pictureCount" value="0"/>
  <c:forEach var="selection" items="${selections}">
   <c:if test="${pictureCount == 0}">
	<img 
	  align="middle" id="ss_profilePicture"
	  border="0" 
	  src="<ssf:fileUrl webPath="readScaledFile" file="${selection}"/>"
	  alt="${property_caption}" /></a>
   </c:if>
   <c:set var="pictureCount" value="${pictureCount + 1}"/>
  </c:forEach>
  </div>
 </c:if>
 <table cellspacing="0" cellpadding="0">
 <c:if test="${!empty ssWorkspaceCreator.phone}">
  <tr>
   <td valign="top" align="left">
     <span>${ssWorkspaceCreator.phone}</span>
    <span class="ss_mobile_small ss_mobile_light">(<ssf:nlt tag="profile.abv.element.phone"/>)</span>
   </td>
  </tr>
 </c:if>
 <c:if test="${!empty ssWorkspaceCreator.emailAddress}">
  <tr>
   <td valign="top" align="left">
     <span>${ssWorkspaceCreator.emailAddress}</span>
    <span class="ss_mobile_small ss_mobile_light">(<ssf:nlt tag="profile.abv.element.emailAddress"/>)</span>
   </td>
  </tr>
 </c:if>
 <c:if test="${!empty ssWorkspaceCreator.mobileEmailAddress}">
  <tr>
   <td valign="top" align="left">
     <span>${ssWorkspaceCreator.mobileEmailAddress}</span>
    <span class="ss_mobile_small ss_mobile_light">(<ssf:nlt tag="profile.abv.element.mobileEmailAddress"/>)</span>
   </td>
  </tr>
 </c:if>
 <c:if test="${!empty ssWorkspaceCreator.txtEmailAddress}">
  <tr>
   <td valign="top" align="left">
     <span>${ssWorkspaceCreator.txtEmailAddress}</span>
    <span class="ss_mobile_small ss_mobile_light">(<ssf:nlt tag="profile.abv.element.txtEmailAddress"/>)</span>
   </td>
  </tr>
 </c:if>
 </table> 
 <br/>
</c:if>
<div style="padding-left:6px;">
<c:if test="${!empty ssWorkspaces}">
<table class="ss_mobile" cellspacing="0" cellpadding="0" border="0">
<th colspan="2" align="left"><ssf:nlt tag="administration.initial.workspace.title"/></th>
<c:forEach var="workspace" items="${ssWorkspaces}" >
	<tr><td><a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${workspace.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />">
    <c:if test="${empty workspace.title}">
    	(<ssf:nlt tag="workspace.noTitle"/>)
    </c:if>
	<c:out value="${workspace.title}"/>
	</a>
  </td></tr>
</c:forEach>
<c:if test="${!empty ss_nextPage || !empty ss_prevPage}">
<tr><td></td></tr>
<tr><td>
<table><tr><td>
<c:if test="${!empty ss_prevPage}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" 
	operation="mobile_show_workspace" 
	actionUrl="false" ><ssf:param name="pageNumber" value="${ss_prevPage}"/></ssf:url>">&lt;&lt;&lt;</a>
</c:if>
</td><td style="padding-left:30px;">
<c:if test="${!empty ss_nextPage}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" 
	operation="mobile_show_workspace" 
	actionUrl="false" ><ssf:param name="pageNumber" value="${ss_nextPage}"/></ssf:url>">&gt;&gt;&gt;</a>
</c:if>
</td></tr></table>
</td></tr>
</c:if>
</table>
<br/>
</c:if>

<c:if test="${!empty ssFolders}">
<table class="ss_mobile" cellspacing="0" cellpadding="0" border="0">
<th colspan="2" align="left"><ssf:nlt tag="search.Folders"/></th>
<c:forEach var="folder" items="${ssFolders}" >
	<tr><td><a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${folder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
    <c:if test="${empty folder.title}">
    	(<ssf:nlt tag="workspace.noTitle"/>)
    </c:if>
	<c:out value="${folder.title}"/>
	</a>
  </td></tr>
</c:forEach>
</table>
</c:if>
</div>

<br/>
<div class="ss_mobile_breadcrumbs ss_mobile_small">
<c:if test="${!empty ssBinder.parentBinder}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />"><ssf:nlt tag="mobile.returnToParentWorkspace"/></a>
<br/>
</c:if>
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
	><ssf:nlt tag="mobile.returnToTop"/></a>
</div>
</div>

</body>
</html>
