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
<c:if test="${empty ssBinder.parentFolder && !empty ssBinder.parentBinder}">
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />"><strong>${ssBinder.parentBinder.title}</strong></a>
<br/>&nbsp;&nbsp;</c:if>
<c:if test="${!empty ssBinder.parentFolder}">
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinderparentFolder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" 
	actionUrl="false" />"><strong>${ssBinder.parentFolder.title}</strong></a>
<br/>&nbsp;&nbsp;</c:if>
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" 
	actionUrl="false" />"><strong>${ssBinder.title}</strong></a>
<br/>
<br/>
<table class="ss_mobile" cellspacing="0" cellpadding="0" border="0">
<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
  <tr><td align="right" valign="top" style="padding:0px 4px 2px 4px;">${entry1._docNum}.</td>
	<td><a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}"  entryId="${entry1._docId}"
	action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
    <c:if test="${empty entry1.title}">
    	(<ssf:nlt tag="entry.noTitle"/>)
    </c:if>
	<c:out value="${entry1.title}"/>
	</a>
  </td></tr>
</c:forEach>
</table>
<br/>
<c:if test="${empty ssBinder.parentFolder && !empty ssBinder.parentBinder}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />"><ssf:nlt tag="mobile.returnToParentWorkspace"/></a>
<br/>
</c:if>
<c:if test="${!empty ssBinder.parentFolder}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinderparentFolder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" 
	actionUrl="false" />"><ssf:nlt tag="mobile.returnToParentFolder"/></a>
<br/>
</c:if>
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />">
<span class="ss_mobile" style="color:blue;"><ssf:nlt tag="mobile.returnToTop"/></span>
</a>
</div>

</body>
</html>
