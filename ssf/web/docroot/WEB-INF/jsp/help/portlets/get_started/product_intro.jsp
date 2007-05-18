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
<%@ include file="/WEB-INF/jsp/help/hide_moreinfo_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span>

<p><ssf:nlt tag="help.getStartedProduct.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.tools"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.portal"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.whatToClick"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.moreInfo"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<div style="margin-left:25px;">

<a href="javascript: ss_helpSystem.showHelpPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.getStartedProduct.workspaces.title"/></a><br />

<a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Quick Tips.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>

</div>

</div>

<br/>

<div align="center">
<img 
    border="0" style="padding-left: 10px;" 
    <ssf:alt tag="general.previous"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif"/> 
 1 <a href="javascript: ss_helpSystem.showHelpPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel', '', '');">2</a> 
 <a href="javascript: ss_helpSystem.showHelpPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel', '', '');"><img 
    border="0" style="padding-left: 10px;" 
    <ssf:alt tag="general.next"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
    
</div>
