<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
// General variables
String title = ParamUtil.get(request, "title", "");
String action = ParamUtil.get(request, "action", "");
String adapter = ParamUtil.get(request, "adapter", "");
String isAccessible = ParamUtil.get(request, "isAccessible", "");

boolean blnAdapter = true;
if ("false".equals(adapter)) {
	blnAdapter = false;
}
String entryId = ParamUtil.get(request, "entryId", "");
String binderId = ParamUtil.get(request, "binderId", "");
String entityType = ParamUtil.get(request, "entityType", "");
String seenStyle = ParamUtil.get(request, "seenStyle", "");
String seenStyleFine = ParamUtil.get(request, "seenStyleFine", "");
String imageId = ParamUtil.get(request, "imageId", "");
String linkMenuIdx = ParamUtil.get(request, "linkMenuObjIdx", "");
String linkMenuObj = "ss_linkMenu_arr['"+ linkMenuIdx +"']";
String menuDivId = ParamUtil.get(request, "menuDivId", "");
String namespace = ParamUtil.get(request, "namespace", "");
String entryCallbackRoutine = ParamUtil.get(request, "entryCallbackRoutine", "");
String url = ParamUtil.get(request, "url", "");
String isDashboard = ParamUtil.get(request, "isDashboard", "no");
String useBinderFunction = ParamUtil.get(request, "useBinderFunction", "no");
String dashboardType = ParamUtil.get(request, "dashboardType", "");
String isFile = ParamUtil.get(request, "isFile", "no");
%>

<% if (isAccessible.equals("false")) { %>

<ssHelpSpot helpId="tools/display_entry_control" offsetX="0" 
  title="<ssf:nlt tag="helpSpot.displayEntryControl"/>"></ssHelpSpot>
<a class="ss_title_menu" href="<%= url %>" 
<% if ( useBinderFunction.equals("no") && !dashboardType.equals("portlet") ) {  %>
	onClick="ss_loadEntryFromMenu(this, '<%= linkMenuIdx %>', '<%= entryId %>', '<%= binderId %>', '<%= entityType %>', '<%= entryCallbackRoutine %>', '<%= isDashboard %>', '<%= isFile %>');return false;" 
<% } else if ( useBinderFunction.equals("no") && dashboardType.equals("portlet") ) { %>
	onClick="return ss_loadEntryFromMenuSearchPortlet(this, '<%= linkMenuIdx %>', '<%= entryId %>', '<%= binderId %>', '<%= entityType %>', '<%= entryCallbackRoutine %>', '<%= isDashboard %>');" 
<% } else if (useBinderFunction.equals("yes")) { %>
	onClick="ss_loadBinderFromMenu(this, '<%= linkMenuIdx %>', '<%= entryId %>', '<%= entityType %>'); return false;" 
<% } else if (useBinderFunction.equals("permalink")) { %>
	onClick="ss_loadPermaLinkFromMenu('<%= linkMenuIdx %>', '<%= binderId %>','<%= entryId %>', '<%= entityType %>', '<%= namespace %>'); return false;" 
<% }%>
onMouseOver="checkAndCreateMenuObject('<%= linkMenuIdx %>');<%= linkMenuObj %>.showButton(this, '<%= imageId %>'); ss_setMenuGeneratedURLs('<%= linkMenuIdx %>', '<%= binderId %>','<%= entryId %>', '<%= entityType %>', '<%= namespace %>', '<%= url %>');"
onMouseOut="<%= linkMenuObj %>.hideButton(this, '<%= imageId %>');"
><img <ssf:alt tag="alt.showMenu"/> border="0" class="ss_title_menu" id="<%= imageId %>" name="<%= imageId %>" 
onClick="setMenuGenericLinks('<%= linkMenuIdx %>', '<%= menuDivId %>', '<%= namespace %>', '<%= url %>', '<%= isDashboard %>', '<%= isFile %>');<%= linkMenuObj %>.showMenu(this, '<%= entryId %>', '<%= binderId %>', '<%= entityType %>', '<%= dashboardType %>');"
src="<html:imagesPath/>pics/downarrow_off.gif"/><c:if test="<%= (title == null || title.equals("")) %>">
<span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span></c:if><span <%= seenStyle %>><%= title %></span></a>

<% } else { %>
<a class="ss_title_menu" 
	<% if ("yes".equals(isFile)) { %>
		<ssf:titleForEntityType entityType="file" text="<%= title %>" />
		href="<%= url %>" target="_blank"
	<% } else { %>
		<ssf:titleForEntityType entityType="<%= entityType %>" text="<%= title %>" />
		href="#" onClick="ss_gotoPermalink('<%= binderId %>','<%= entryId %>', '<%= entityType %>', '<%= namespace %>', 'yes');"
	<% } %>
><c:if test="<%= (title == null || title.equals("")) %>">
<span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span></c:if><span <%= seenStyle %>><%= title %></span></a>
<% } %>