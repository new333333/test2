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
boolean blnAdapter = true;
if ("false".equals(adapter)) {
	blnAdapter = false;
}
String entryId = ParamUtil.get(request, "entryId", "");
String folderId = ParamUtil.get(request, "folderId", "");
String binderId = ParamUtil.get(request, "binderId", "");
String entityType = ParamUtil.get(request, "entityType", "");
String seenStyle = ParamUtil.get(request, "seenStyle", "");
String seenStyleFine = ParamUtil.get(request, "seenStyleFine", "");
String imageId = ParamUtil.get(request, "imageId", "");
String linkMenuObj = ParamUtil.get(request, "linkMenuObj", "");
String menuDivId = ParamUtil.get(request, "menuDivId", "");
String namespace = ParamUtil.get(request, "namespace", "");
String entryCallbackRoutine = ParamUtil.get(request, "entryCallbackRoutine", "");
String url = ParamUtil.get(request, "url", "");
String isDashboard = ParamUtil.get(request, "isDashboard", "no");
%>

<a class="ss_title_menu" href="<%= url %>" 
onClick="ss_loadEntryFromMenu(this, '<%= linkMenuObj %>', '<%= entryId %>', '<%= binderId %>', '<%= entityType %>', '<%= entryCallbackRoutine %>', '<%= isDashboard %>');return false;" 
onMouseOver="<%= linkMenuObj %>.showButton(this, '<%= imageId %>');"
onMouseOut="<%= linkMenuObj %>.hideButton(this, '<%= imageId %>');"
><img border="0" class="ss_title_menu" id="<%= imageId %>" name="<%= imageId %>" 
onClick="setMenuGenericLinks('<%= linkMenuObj %>', '<%= menuDivId %>', '<%= namespace %>', '<%= url %>');<%= linkMenuObj %>.showMenu(this, '<%= entryId %>', '<%= binderId %>', '<%= entityType %>');"
src="<html:imagesPath/>pics/downarrow_off.gif"/><c:if test="<%= (title == null || title.equals("")) %>">
<span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span></c:if><span <%= seenStyle %>><%= title %></span></a>