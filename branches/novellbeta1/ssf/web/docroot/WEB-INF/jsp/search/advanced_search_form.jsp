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

<form action="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="advanced_search"/>
					<portlet:param name="tabTitle" value=""/>
					<portlet:param name="newTab" value="0"/>
					</portlet:actionURL>" method="post" onSubmit="return ss_prepareAdditionalSearchOptions();" id="advSearchForm">

	<%@ include file="/WEB-INF/jsp/search/advanced_search_form_common.jsp" %>

</form>

	