<%
// The dashboard "google widget" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ssDashboard" type="java.util.Map" scope="request" />
<jsp:useBean id="ssComponentId" type="java.lang.String" scope="request" />
<%
	Map components = null;
	Map component = null;
	Map data = null;
	String[] googleScript = null;
	String googleScript0 = "";
	Map dashboard = (Map)ssDashboard.get("dashboard");
	if (dashboard != null) components = (Map)dashboard.get("components");
	if (components != null) component = (Map)components.get(ssComponentId.toString());
	if (component != null) data = (Map)component.get("data");
	if (data != null && data.containsKey("google_widget")) {
		googleScript = (String[])data.get("google_widget");
		googleScript0 = googleScript[0].replaceAll("'", "\\'");
		googleScript0 = googleScript0.replaceAll("<script", "<scr'+'ipt");
		googleScript0 = googleScript0.replaceAll("</script", "</scr'+'ipt");
	}
%>
<iframe id="<portlet:namespace/>_ss_googleWidget${ssComponentId}" 
  name="<portlet:namespace/>_ss_googleWidget${ssComponentId}" style="width: 99%;
  height: ${ssDashboard.dashboard.components[ssComponentId].data.height[0]};
  margin:0px; padding:0px;" frameBorder="no" >xxx</iframe>

<script type="text/javascript">
function ss_initGoogleWidget<portlet:namespace/>${ssComponentId}() {
	var ifObj = document.getElementById('<portlet:namespace/>_ss_googleWidget${ssComponentId}');
	if (ifObj == null) return;
	window.<portlet:namespace/>_ss_googleWidget${ssComponentId}.document.writeln('<%= googleScript0 %>');
}
ss_createOnLoadObj('ss_initGoogleWidget<portlet:namespace/>${ssComponentId}', ss_initGoogleWidget<portlet:namespace/>${ssComponentId});
</script>

