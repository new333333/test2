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

<span class="ss_titlebold"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span><br />
<span style="font-weight:bold;"><ssf:nlt tag="help.getStartedProduct.workspaces.title"/></span>

<p>tbd</p>

</div>

<br/>

<div align="center">
<a href="javascript: ss_helpSystem.showHelpPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel', '', '');"><img 
    border="0" style="padding-left: 10px;" 
    <ssf:alt tag="general.previous"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a> 
 <a href="javascript: ss_helpSystem.showHelpPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel', '', '');">1</a> 2  
 <img 
    border="0" style="padding-left: 10px;" 
    <ssf:alt tag="general.next"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/> 
    
</div>
