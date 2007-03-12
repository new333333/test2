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
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
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
%>

<a 
class="ss_title_menu" 
href="<ssf:url     
adapter="<%= blnAdapter %>" 
portletName="ss_forum" 
folderId="<%= folderId %>" 
action="view_folder_entry" 
entryId="<%= entryId %>" actionUrl="true" />" 
onClick="ss_loadEntry(this, '<%= entryId %>');return false;" 
onMouseOver="ss_linkMenu.showButton(this);"
onMouseOut="ss_linkMenu.hideButton(this);"
><img border="0" class="ss_title_menu"
onClick="ss_linkMenu.showMenu(this, '<%= entryId %>', '<%= binderId %>', '<%= entityType %>');"
src="<html:imagesPath/>pics/downarrow_off.gif"/><c:if test="<%= (title == null || title.equals("")) %>">
<span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span></c:if><span <%= seenStyle %>><%= title %></span></a>