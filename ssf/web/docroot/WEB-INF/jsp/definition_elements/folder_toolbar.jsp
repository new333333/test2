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
<% // Folder level toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_actions_bar1_pane" align="right">
<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
<td class="ss_actions_bar1" style="white-space:nowrap;">
<c:if test="${!empty ssFolderToolbar}">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar1 ss_actions_bar"/>
</c:if>
</td>
<td class="ss_actions_bar1"  valign="middle" style="height: 23px; width: 50px; white-space: nowrap;">
<a href="javascript: window.print();"><img border="0" 
    class="ss_print_button"
    alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
    src="<html:imagesPath/>pics/1pix.gif" /></a><a
    href="javascript: ss_helpSystem.run();"><img border="0" style="margin-left: 10px; margin-right: 5px;"
    <ssf:alt tag="navigation.help"/> src="<html:imagesPath/>icons/help.png" /></a></td>
</tr></tbody></table>
</div>
