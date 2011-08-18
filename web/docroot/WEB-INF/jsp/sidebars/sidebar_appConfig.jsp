<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil, org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ page import="org.kablink.teaming.web.util.MiscUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript">
	<% /* Load the localized strings from the resource file. */ %>
	var	g_appConfigStrings = new Array();
		g_appConfigStrings["sidebar.appConfig.Banner"]						= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Banner"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Banner.Alt.Help"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Banner.Alt.Help"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Button.Cancel"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Button.Cancel"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Button.OK"]					= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Button.OK"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Caption"]						= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Caption"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Column.Application"]			= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Column.Application"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Column.Extension"]			= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Column.Extension"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Confirm.Overwrite"]			= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Confirm.Overwrite"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Error.ApplicationMissing"]	= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Error.ApplicationMissing"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Error.DuplicateExtension"]	= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Error.DuplicateExtension"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Error.NoDelete"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Error.NoDelete"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Error.SelectAnExtension"]		= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Error.SelectAnExtension"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Info"]						= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Info"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Add"]					= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Add"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Alt.Close"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Alt.Close"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Alt.Open"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Alt.Open"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Delete"]					= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Delete"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Use"]					= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Use"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Use.MSO"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Use.MSO"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Use.OO"]					= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Use.OO"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Menu.Use.SO"]					= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Menu.Use.SO"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Message.NoData"]				= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Message.NoData"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.SelectAnExtension"]			= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.SelectAnExtension"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.Warning.DuplicateExtension"]	= "<ssf:escapeJavaScript><ssf:nlt tag="sidebar.appConfig.Warning.DuplicateExtension"/></ssf:escapeJavaScript>";
		g_appConfigStrings["sidebar.appConfig.helpUrl"]						= "<ssf:escapeJavaScript><%= MiscUtil.getHelpUrl( "user", "trouble_editoroverrides", null ) %></ssf:escapeJavaScript>";


	<% /* Load the defined edit-in-place extensions from the */ %>
	<% /* servlet.                                           */ %>
	var	g_appEditInPlaceExtensions = new Array();
	<%
		String[]	editInPlaceExtensions = SsfsUtil.getEditInPlaceExtensions(BrowserSniffer.is_ie(request));
		int			extensions            = ((null == editInPlaceExtensions) ? 0 : editInPlaceExtensions.length);
		for (int i = 0; i < extensions; i += 1) {
			%>g_appEditInPlaceExtensions[g_appEditInPlaceExtensions.length] = "<%= editInPlaceExtensions[i] %>";<%
		}
	%>
</script>

<% if ( GwtUIHelper.isGwtUIActive( request ) == false ) { %>
<a class="ss_sideLink" href="javascript: ;" onclick="ss_editAppConfig(); return false;">
	<ssf:nlt tag="sidebar.appConfig.Caption"/>
</a>
<% } %>
