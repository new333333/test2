</div>

<%@ page session="false" %>
<%@ page contentType="text/html" isELIgnored="false" %>
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
Integer nameCount = (Integer) renderRequest.getAttribute("ss_menu_tag_name_count");
String menuTagDivId = "ss_menuTagDiv" + nameCount.toString();
String title = ParamUtil.get(request, "title", "");
String titleId = ParamUtil.get(request, "titleId", "");
String titleClass = ParamUtil.get(request, "class", "ss_toolbar_item");
String openStyle = ParamUtil.get(request, "openStyle", "");
String anchor = ParamUtil.get(request, "anchor", "");
String offsetTop = ParamUtil.get(request, "offsetTop", "");
String offsetLeft = ParamUtil.get(request, "offsetLeft", "");

%>
<div id="parent_<%= menuTagDivId %>" style="display:inline;">
<a id="<%= titleId %>" class="<%= titleClass %>" href="javascript: ;" 
	  onClick="activateMenuLayerClone('<%= menuTagDivId %>', 'parent_<%= menuTagDivId %>');"
><%= title %></a>
</div>
