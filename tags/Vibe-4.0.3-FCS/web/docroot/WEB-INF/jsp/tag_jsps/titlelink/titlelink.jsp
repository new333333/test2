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
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page import="org.kablink.util.ParamUtil" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%
// General variables
String title = ParamUtil.get(request, "title", "");
String action = ParamUtil.get(request, "action", "");
String isAccessible = ParamUtil.get(request, "isAccessible", "");


String entryId = ParamUtil.get(request, "entryId", "");
String binderId = ParamUtil.get(request, "binderId", "");
String entityType = ParamUtil.get(request, "entityType", "");
String seenStyle = ParamUtil.get(request, "seenStyle", "");
String seenStyleFine = ParamUtil.get(request, "seenStyleFine", "class=\"ss_light\"");
String namespace = ParamUtil.get(request, "namespace", "");
String url = ParamUtil.get(request, "url", "");
String onClick = ParamUtil.get(request, "onClick", "");
String isDashboard = ParamUtil.get(request, "isDashboard", "no");
String useBinderFunction = ParamUtil.get(request, "useBinderFunction", "no");
String dashboardType = ParamUtil.get(request, "dashboardType", "");
String isFile = ParamUtil.get(request, "isFile", "no");
String hrefClass = ParamUtil.get(request, "hrefClass", "ss_title_menu");
%>

<% if (isAccessible.equals("false")) { %><%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

<c:if test="${empty ss_menuLinkHelpShown}">
	<ssHelpSpot 
  		helpId="workspaces_folders/entries/display_entry_control" 
	  	offsetX="-28" offsetY="0"  
  		title="<ssf:nlt tag="helpSpot.displayEntryControl"/>">
	</ssHelpSpot>
</c:if><c:set 
  var="ss_menuLinkHelpShown" value="1" scope="request"/><a href="<%= url %>" 
<% 
if (!"".equals(onClick)) {
	%> onClick="<%= onClick %>" <%
}

if ( useBinderFunction.equals("no") ) {  %>
	onClick="ss_loadEntryFromMenu(this,  '<%= entryId %>', '<%= binderId %>', '<%= entityType %>', '<%= namespace %>', '<%= isDashboard %>', '<%= isFile %>');return false;" 
<% } else if (useBinderFunction.equals("permalink")) { %>
	onClick="return ss_gotoPermalink('<%= binderId %>','<%= entryId %>', '<%= entityType %>', '<%= namespace %>', 'yes');" 
<% //if useBinderFunction == yes, just use href
 }%>
><c:if test="<%= org.kablink.util.Validator.isNull(title) %>">
&nbsp;<span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span></c:if><span <%= seenStyle %>><%= title %></span></a>

<% } else { %>
<a class="<%= hrefClass %>" 
	<% if ("yes".equals(isFile)) { %>
		<c:choose>
			<c:when test="<%= !org.kablink.util.Validator.isNull(title) %>">
				<ssf:titleForEntityType entityType="file" text="<%= title %>" />
			</c:when>
			<c:otherwise>
				<ssf:titleForEntityType entityType="file" text='<%= "--" + NLT.get("entry.noTitle") + "--" %>' />
			</c:otherwise>
		</c:choose>
		href="<%= url %>" target="_blank"
	<% } else { %>
		<c:choose>
			<c:when test="<%= !org.kablink.util.Validator.isNull(title) %>">
				<ssf:titleForEntityType entityType="<%= entityType %>" text="<%= title %>" />
			</c:when>
			<c:otherwise>
				<ssf:titleForEntityType entityType="<%= entityType %>" text='<%= "--" + NLT.get("entry.noTitle") + "--" %>' />
			</c:otherwise>
		</c:choose>
		href="javascript:;" onClick="return ss_gotoPermalink('<%= binderId %>','<%= entryId %>', '<%= entityType %>', '<%= namespace %>', 'yes');"
	<% } 
	%>
	
><c:if test='<%= (title == null || title.equals("")) %>'>
<span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span></c:if><span <%= seenStyle %>><%= title %></span></a>
<% } %>