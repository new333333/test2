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
<c:if test="${!empty ssEntry}">
<div>
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}"
	action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
<span style="color:blue;">${ssBinder.title}</span>
</a>
</div>
<div style="padding:0px 0px 6px 8px;">
<span>${ssEntry.docNumber}.&nbsp;
<c:if test="${!empty ssEntry.title}">
<b><c:out value="${ssEntry.title}"/></b>
</c:if>
<c:if test="${empty ssEntry.title}">
--<ssf:nlt tag="entry.noTitle"/>--
</c:if>
</span>
</div>
<c:if test="${!empty ssEntry.description}">
<div style="padding:0px 0px 10px 8px;">
 <span><ssf:markup type="view" entity="${ssEntry}"><c:out 
   value="${ssEntry.description.text}" escapeXml="false"/></ssf:markup></span>
</div>
</c:if> 
</c:if> 

<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}"
	action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
<span style="color:blue;"><ssf:nlt tag="mobile.returnToParentFolder"/></span>
</a>

</body>
</html>
