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
<%@ page import="java.util.ArrayList" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" action="<portlet:actionURL>
			  <portlet:param name="action" value="export_definition"/>
		      </portlet:actionURL>" method="post" name="<portlet:namespace />fm">

<br>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.export.definitions.select"/></span>
<%@include file="/WEB-INF/jsp/administration/commonSelectTree.jsp" %>

</form>
<br>
</td></tr></table>

