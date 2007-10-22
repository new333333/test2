<%
// The dashboard "google widget" component
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

