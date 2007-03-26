<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
// General variables
String menuDivId = ParamUtil.get(request, "menuDivId", "");
String linkMenuObj = ParamUtil.get(request, "linkMenuObj", "");
String namespace = ParamUtil.get(request, "namespace", "");
%>

<div id="<%= menuDivId %>" class="ss_link_menu">
<ul id="ss_folderMenuShowFileLink_<%= namespace %>" class="ss_title_menu"><li><a href="#" onClick="<%= linkMenuObj %>.showFile(); return false;">
	<ssf:nlt tag="linkMenu.showFile"/></a></li></ul>
<ul id="ss_folderMenuShowEntryLink_<%= namespace %>" class="ss_title_menu"><li><a href="#" onClick="<%= linkMenuObj %>.showEntry(); return false;">
	<ssf:nlt tag="linkMenu.showEntry"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="<%= linkMenuObj %>.currentTab(); return false;"><ssf:nlt tag="linkMenu.currentTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="<%= linkMenuObj %>.newTab(); return false;"><ssf:nlt tag="linkMenu.newTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="<%= linkMenuObj %>.newWindow(); return false;"><ssf:nlt tag="linkMenu.newWindow"/></a></li></ul>
</div>