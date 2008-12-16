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
<% //Guest name form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption});
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size=\""+width+"\" maxlength=\""+width+"\"";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<span class=\"ss_bold\">"+caption+"</span><br/>";
	}
	String required = (String) request.getAttribute("property_required");
	Boolean req = Boolean.parseBoolean(required);
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span id=\"ss_required_"+elementName+"\" title=\""+caption2+"\" class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<c:if test="${ssOperation == 'add_folder_entry'}">
<c:if test="${ssUser.internalId == guestInternalId}">
<div class="ss_entryContent">
<div class="ss_labelAbove">${property_caption}<%= required %></div>
 <div style="padding-left:10px;">
  <span class="ss_bold"><ssf:nlt tag="guest.user.name"/></span><br/>
  <input type="text" class="ss_text" name="${property_name}" <%= width %> /><br/>
  <span class="ss_bold"><ssf:nlt tag="guest.user.email"/></span><br/>
  <input type="text" class="ss_text" name="${property_name}" <%= width %> />
 </div>
</div>
<br/>
</c:if>
</c:if>
