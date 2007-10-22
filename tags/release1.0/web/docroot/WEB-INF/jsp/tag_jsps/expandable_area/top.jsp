<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
String titleClass = ParamUtil.get(request, "titleClass", "");
String openAction = ParamUtil.get(request, "openAction", "");
boolean initOpen = ParamUtil.getBoolean(request, "initOpen", false);

%>
<jsp:useBean id="ss_expandable_area_name_count" type="java.lang.Integer" scope="request" />
<div class="ss_expandable_area_title">${openAction}
<script type="text/javascript">
function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_toggleExpandableArea(divName, imgName) {
    var _divName = '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>' + divName
    var _imgName = '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>' + imgName
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
onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_toggleExpandableArea('<%= name %>', 'img_<%= name %>'); return false;"><img 
border="0" 
<%
	if (initOpen) {
%>
<ssf:alt tag="alt.hide"/> src="<html:imagesPath />pics/sym_s_collapse.gif" 
<%
	} else {
%>
<ssf:alt tag="alt.expand"/> src="<html:imagesPath />pics/sym_s_expand.gif" 
<%
	}
%>
id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>img_<%= name %>" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>img_<%= name %>" /></td>
<td valign="middle"><a href="javascript: ;" 
  onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_toggleExpandableArea('<%= name %>', 'img_<%= name %>'); return false;"
><span class="<%= titleClass %>"><%= title %></span></a></td>
</tr>
</table>
</div>
<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter><%= name %>" class="ss_expandable_area_content"
<%
	if (initOpen) {
%>
style="display:block; visibility:visible;"
<%
	}
%>
>
