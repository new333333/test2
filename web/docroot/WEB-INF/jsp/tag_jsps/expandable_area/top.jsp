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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%

// General variables
Integer nameCount = (Integer) renderRequest.getAttribute("ss_expandable_area_name_count");
if (nameCount == null) {
	nameCount = new Integer(0);
}
nameCount = new Integer(nameCount.intValue() + 1);
renderRequest.setAttribute("ss_expandable_area_name_count", new Integer(nameCount.intValue()));

String name = "ss_expandableAreaDiv" + nameCount.toString();
%>

<div class="ss_expandable_area_title">

<table class="ss_style" cellspacing="0" cellpadding="2">
<tr>
<td valign="middle"><a href="javascript: ;" 
onClick="ss_toggleImage('${renderResponse.namespace}img_<%= name %>', 'sym_s_expand.gif', 'sym_s_collapse.gif');ss_showHide('${renderResponse.namespace}<%= name %>'); return false;"><img 
border="0" 
<c:if test="${initOpen}"><ssf:alt tag="alt.hide"/> src="<html:imagesPath />pics/sym_s_collapse.gif"</c:if>
<c:if test="${!initOpen}"><ssf:alt tag="alt.expand"/> src="<html:imagesPath />pics/sym_s_expand.gif"</c:if>

id="${renderResponse.namespace}img_<%= name %>" name="${renderResponse.namespace}img_<%= name %>" /></a></td>
<td valign="middle"><a href="javascript: ;" 
onClick="ss_toggleImage('${renderResponse.namespace}img_<%= name %>', 'sym_s_expand.gif', 'sym_s_collapse.gif');ss_showHide('${renderResponse.namespace}<%= name %>'); return false;"
><span class="${titleClass}">${title}</span></a></td>
</tr>
</table>
</div>
<div id="${renderResponse.namespace}<%= name %>" class="ss_expandable_area_content"
<c:if test="${initOpen}">style="display:block; visibility:visible;"</c:if>

>
