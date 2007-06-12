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
<div class="ss_style">
<div class="ss_help_style">

<div class="ss_help_title">
<span class="ss_titlebold"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span>
</div>

<p><ssf:nlt tag="help.getStartedProduct.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.content.tools"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.content.portal"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.content.whatToClick"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.header" /></p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel');"><ssf:nlt tag="help.getStartedProduct.subtopic.workspaces"/></a></p>
</div>

<div  class="ss_help_more_pages_section" title="This Help topic has more than one page of information">
<div class="not_last_link" title="No previous page"><span class="no_next_page"><<</span></div>
<div class="not_last_link">1</div> 
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel');">2</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel');">>></a></div>
</div>
