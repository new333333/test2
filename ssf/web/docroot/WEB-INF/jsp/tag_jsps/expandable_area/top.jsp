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
Integer nameCount = (Integer) renderRequest.getAttribute("ss_expandable_area_name_count");
if (nameCount == null) {
	nameCount = new Integer(0);
}
nameCount = new Integer(nameCount.intValue() + 1);
renderRequest.setAttribute("ss_expandable_area_name_count", new Integer(nameCount.intValue()));

String name = "ss_expandableAreaDiv" + nameCount.toString();
String title = ParamUtil.get(request, "title", "");

%>
<jsp:useBean id="ss_expandable_area_name_count" type="java.lang.Integer" scope="request" />
<div class="ss_expandable_area_title">
<script type="text/javascript">
function <portlet:namespace/>_toggleExpandableArea(divName, imgName) {
    var _divName = '<portlet:namespace/>' + divName
    var _imgName = '<portlet:namespace/>' + imgName
    if (self.document.getElementById(_divName).style.visibility == 'visible') {
        ss_showHideObj(_divName, 'hidden', 'none');
        ss_replaceImage(_imgName, '<html:imagesPath />pics/sym_s_expand.gif');
    } else {
        ss_showHideObj(_divName, 'visible', 'block');
        ss_replaceImage(_imgName, '<html:imagesPath />pics/sym_s_collapse.gif');
    }
}
</script>
<table class="ss_style" cellspacing="0" cellpadding="2">
<tr>
<td valign="middle"><a href="javascript: ;" 
onClick="<portlet:namespace/>_toggleExpandableArea('<%= name %>', 'img_<%= name %>'); return false;"><img 
border="0" src="<html:imagesPath />pics/sym_s_expand.gif" 
name="<portlet:namespace/>img_<%= name %>" /></td>
<td valign="middle"><span class="ss_bold"><%= title %></span></a></td>
</tr>
</table>
</div>
<div id="<portlet:namespace/><%= name %>" class="ss_expandable_area_content">
