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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">
<%@ include file="/WEB-INF/jsp/help/hide_help_panel_button.jsp" %>

<span class="ss_bold"><ssf:nlt tag="administration.portlet"/></span>
<br/>
<ssf:nlt tag="administration.selectHelp"/>
<br/>
<br/>
<ul style="list-style-type: square;">
<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/form_and_view_designer', 'ss_help_panel', '', '');"
><ssf:nlt tag="administration.definition_builder_designers"/></a></li>
</ul>
</div>
