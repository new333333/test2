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
<c:set var="ss_dashboard_config_form_name" value="<%= renderResponse.getNamespace() + "searchfm" %>"/>

<form method="post" class="ss_style ss_form"  name="${ss_dashboard_config_form_name}" id="${ss_dashboard_config_form_name}"
	action="<portlet:actionURL/>"  onSubmit="return ss_onSubmit(this);">

<input type="hidden" name="componentName" value="search"/>
<%@ include file="/WEB-INF/jsp/dashboard/search_config.jsp" %>

<br/>
<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"

</form>