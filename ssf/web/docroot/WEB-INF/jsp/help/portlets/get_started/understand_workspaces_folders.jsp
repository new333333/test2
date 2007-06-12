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
<span class="ss_titlebold"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span><br />
<span class="subtitle"><ssf:nlt tag="help.getStartedProduct.subtopic.workspaces"/></span>
</div>

<p><ssf:nlt tag="help.getStartedProduct.content.workspaces.intro"/> <ssf:nlt tag="help.getStartedProduct.content.workspaces.defaults"/></p>

<p><ssf:nlt tag="help.getStartedProduct.content.workspaces.defined"/></p>

<p><ssf:nlt tag="help.getStartedProduct.content.workspaces.folders.defined"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.content.workspaces.access"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<div class="ss_help_more_pages_section" title="This Help topic has more than one page of information">
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');"><<</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');">1</a></div> 
<div class="not_last_link">2</div>
<div title="No next page"><span class="no_next_page">>></span></div>
</div>