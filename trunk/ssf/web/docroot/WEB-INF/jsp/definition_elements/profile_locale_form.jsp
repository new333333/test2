<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<% //Name form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ page import="org.kablink.teaming.util.LocaleUtils" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<div class="ss_entryContent">
<c:if test="${!empty property_caption}">
<label for="${property_name}">
	<span class="ss_labelAbove"><c:out value="${property_caption}"/></span>
</label>
</c:if>
<c:if test="${empty ssReadOnlyFields[property_name]}">
<select name="${property_name}" id="${property_name}">
<%
	String s_locale = (String) request.getAttribute("property_default");
	User user = (User)request.getAttribute("ssDefinitionEntry");
	User currentUser = (User)request.getAttribute("ssUser");
	TreeMap<String,Locale> map = NLT.getSortedLocaleList(currentUser); //sorted list of locales

	Locale userLocale = null;
	if (user != null) {
		userLocale = user.getLocale();
	}
	if (userLocale == null) {
		userLocale = LocaleUtils.getDefaultLocale(s_locale);
	}

	for (Map.Entry<String, Locale> me: map.entrySet()) {
		String checked = "";
		if (me.getValue().toString().toLowerCase().equals(userLocale.toString().toLowerCase()))
			checked="selected=\"selected\"";
		
%>
<option value="<%= me.getValue().toString() %>" <%= checked %>><%= me.getKey() %></option>
<%
};
%>
</select>
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}">
<%
	org.kablink.teaming.domain.User user = (org.kablink.teaming.domain.User)request.getAttribute("ssDefinitionEntry");
	org.kablink.teaming.domain.User currentUser = (org.kablink.teaming.domain.User)request.getAttribute("ssUser");
	if (user != null) {	
%>
<%= user.getLocale().getDisplayName(currentUser.getLocale()) %>
<%
};
%>
</c:if>

</div>