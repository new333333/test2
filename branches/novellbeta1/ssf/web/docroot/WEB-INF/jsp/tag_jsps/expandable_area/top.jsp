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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
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
String openAction = ParamUtil.get(request, "openAction", "");
boolean initOpen = ParamUtil.getBoolean(request, "initOpen", false);

%>
<jsp:useBean id="ss_expandable_area_name_count" type="java.lang.Integer" scope="request" />
<div class="ss_expandable_area_title">${openAction}
<script type="text/javascript">
function <portlet:namespace/>_toggleExpandableArea(divName, imgName) {
    var _divName = '<portlet:namespace/>' + divName
    var _imgName = '<portlet:namespace/>' + imgName
    var openAction = '<%= openAction %>';
    if (openAction == 'wipe') {
	    if (self.document.getElementById(_divName).style.display == 'none' || 
	    		self.document.getElementById(_divName).style.visibility == 'hidden' || 
	    		self.document.getElementById(_divName).style.display == '') {
		    ss_replaceImage(_imgName, '<html:imagesPath />pics/sym_s_collapse.gif');
        	ss_showDivWipe(_divName);
		} else {
        	ss_hideDivWipe(_divName);
		    ss_replaceImage(_imgName, '<html:imagesPath />pics/sym_s_expand.gif');
	    }
    } else {        
	    if (self.document.getElementById(_divName).style.display == 'none' || 
	    		self.document.getElementById(_divName).style.visibility == 'hidden' || 
	    		self.document.getElementById(_divName).style.display == '') {
		    ss_showHideObj(_divName, 'visible', 'block');
		    ss_replaceImage(_imgName, '<html:imagesPath />pics/sym_s_collapse.gif');
		} else {
		    ss_showHideObj(_divName, 'hidden', 'none');
		    ss_replaceImage(_imgName, '<html:imagesPath />pics/sym_s_expand.gif');
	    }
	}
}
</script>
<table class="ss_style" cellspacing="0" cellpadding="2">
<tr>
<td valign="middle"><a href="javascript: ;" 
onClick="<portlet:namespace/>_toggleExpandableArea('<%= name %>', 'img_<%= name %>'); return false;"><img 
border="0" 
<%
	if (initOpen) {
%>
src="<html:imagesPath />pics/sym_s_collapse.gif" 
<%
	} else {
%>
src="<html:imagesPath />pics/sym_s_expand.gif" 
<%
	}
%>
id="<portlet:namespace/>img_<%= name %>" name="<portlet:namespace/>img_<%= name %>" /></td>
<td valign="middle"><a href="javascript: ;" 
  onClick="<portlet:namespace/>_toggleExpandableArea('<%= name %>', 'img_<%= name %>'); return false;"
><span class="ss_bold"><%= title %></span></a></td>
</tr>
</table>
</div>
<div id="<portlet:namespace/><%= name %>" class="ss_expandable_area_content"
<%
	if (initOpen) {
%>
style="display:block; visibility:visible;"
<%
	}
%>
>
