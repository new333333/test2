<%
// Portlet not configured message
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
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<div align="right">
  <a class="ss_linkButton ss_smallprint" 
		    href="<portlet:renderURL 
		      portletMode="edit" 
		      windowState="maximized" />"><ssf:nlt tag="portlet.configure"/></a>
</div>
<div>
 <ssf:nlt tag="portlet.notConfigured"/>
</div>
