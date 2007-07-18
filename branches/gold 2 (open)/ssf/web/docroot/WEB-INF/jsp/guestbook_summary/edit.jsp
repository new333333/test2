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

<form class="ss_style ss_form" action="<portlet:actionURL/>" 
	method="post" name="<portlet:namespace />fm"
	onSubmit="return ss_onSubmit(this);">
<input type="hidden" name="componentName" value="guestbook"/>
<table class="ss_style" width="100%"><tr><td>
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_config.jsp" %>
<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"
</td></tr></table>
</form>
<br>

