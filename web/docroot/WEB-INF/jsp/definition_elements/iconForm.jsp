<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //Icon form element %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="org.kablink.teaming.util.Utils" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}
%>
<div class="ss_entryContent ${ss_fieldModifyStyle}">
<table>
<tr>
<td valign="top"><div class=" ${ss_fieldModifyStyle}"><%= caption %></div></td>
<td valign="top">
<ul class="ss_icon_list" style="display:inline;">
<%
	String iconValue = (String)request.getAttribute("iconValue");
	if (iconValue.startsWith("/")) iconValue = iconValue.substring(1, iconValue.length());
	String iconListPath = (String)request.getAttribute("iconListPath");
	String[] iconList = SPropsUtil.getCombinedPropertyList(iconListPath, ObjectKeys.CUSTOM_PROPERTY_PREFIX);
	if (iconValue == null) iconValue = "";
	for (int i = 0; i < iconList.length; i++) {
		String iconListValue = iconList[i].trim();
		if (iconListValue.equals("")) continue;
		if (iconListValue.startsWith("/")) iconListValue = iconListValue.substring(1, iconListValue.length());
		iconListValue = Utils.getIconNameTranslated(iconListValue);
		String checked = "";
		if (iconValue.equals(iconListValue)) {
			checked = " checked=\"checked\"";
		}

%>
<li>
	<label for="<%= iconListValue %>">
		<span style="display:none;"><ssf:nlt tag="label.iconListValue"/></span>
	</label>
  	<input type="radio" class="ss_text" name="${property_name}" ${ss_fieldModifyInputAttribute}
  			value="<%= iconListValue %>" id="<%= iconListValue %>" <%= checked %> />
	<img <ssf:alt text="<%= iconListValue %>"/> border="0" src="<html:brandedImagesPath/><%= iconListValue %>" />
</li>

<%
	}
%>
</ul>
</td>
</tr>
</table>
</div>
