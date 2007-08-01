<%
// The dashboard "google widget" component
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
		googleScript = (String)data.get("google_widget");
		googleScript0 = googleScript0.replaceAll("'", "\\'");
		googleScript0 = googleScript0.replaceAll("<script", "<scr'+'ipt");
		googleScript0 = googleScript0.replaceAll("</script", "</scr'+'ipt");
	}
%>
<iframe id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_ss_googleWidget${ssComponentId}" 
  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_ss_googleWidget${ssComponentId}" style="width: 99%;
  height: ${ssDashboard.dashboard.components[ssComponentId].data.height};
  margin:0px; padding:0px;" frameBorder="0" >xxx</iframe>

<script type="text/javascript">
function ss_initGoogleWidget<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>${ssComponentId}() {
	var ifObj = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_ss_googleWidget${ssComponentId}');
	if (ifObj == null) return;
	window.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_ss_googleWidget${ssComponentId}.document.writeln('<%= googleScript0 %>');
}
ss_createOnLoadObj('ss_initGoogleWidget<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>${ssComponentId}', ss_initGoogleWidget<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>${ssComponentId});
</script>

