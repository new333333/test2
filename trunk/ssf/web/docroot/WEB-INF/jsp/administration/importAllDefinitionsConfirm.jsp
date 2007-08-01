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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.reload.definitions" /></span>
<br>
<br>

<form class="ss_style ss_form" method="post" 
		  action="<portlet:actionURL><portlet:param 
		  name="action" value="import_definition"/><portlet:param 
		  name="operation" value="reload"/></portlet:actionURL>" 
		  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm">

<span>
<ssf:nlt tag="administration.reload.definitions.warning"/>
</span>
<br/>
<br/>

<input type="submit" class="ss_submit" name="reloadDefinitionsBtn" value="<ssf:nlt tag="button.ok" />">
&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;">

</form>
</div>
</div>
