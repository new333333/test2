<%@ page import="com.sitescape.util.ParamUtil" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%
// General variables
String menuDivId = ParamUtil.get(request, "menuDivId", "");
%>

<div id="<%= menuDivId %>" class="ss_link_menu">
<ul id="ss_folderMenuShowFileLink" class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.showFile(); return false;"><ssf:nlt 
  tag="linkMenu.showFile"/></a></li></ul>
<ul id="ss_folderMenuShowEntryLink" class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.showEntry(); return false;"><ssf:nlt 
  tag="linkMenu.showEntry"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.currentTab(); return false;"><ssf:nlt tag="linkMenu.currentTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.newTab(); return false;"><ssf:nlt tag="linkMenu.newTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.newWindow(); return false;"><ssf:nlt tag="linkMenu.newWindow"/></a></li></ul>
</div>