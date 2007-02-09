<%
// Portlet not configured message
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>	
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
 <ssf:nlt tag="portlet.noPreferences" text="The portlet preferences are not set.  Choose the edit button to configure the portlet."/>
  <a class="ss_linkButton ss_smallprint" 
		    href="<portlet:renderURL 
		      portletMode="edit" 
		      windowState="maximized" />"><ssf:nlt tag="portlet.setPreferences"/></a>
