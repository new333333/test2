<%
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
<% //Liferay uses ajax to add content.  This is the first page displayed.
   //Don't want to include any other aspen stuff - messes up ajax. 
%>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<portlet:defineObjects/>

<div class="ss_style ss_portlet">
<form class="ss_style ss_form" action="<portlet:actionURL portletMode="edit" windowState="maximized"/>" method="post" name="<portlet:namespace />fm">
<h1><ssf:nlt tag="portlet.notConfigured"/></h1>
<table>
<tr><td><span class="ss_labelLeft"><ssf:nlt tag="portlet.title"/></span>
</td><td><input class="ss_text" name="title" size="20"/>
</td></tr>
<tr><td><span class="ss_labelLeft"><ssf:nlt tag="portlet.type"/></span>
</td><td><select name="displayType">
<option value="ss_forum"><ssf:nlt tag="portlet.config.forum"/></option>
<option value="ss_workspace"><ssf:nlt tag="portlet.config.workspace"/></option>
<option value="ss_presence"><ssf:nlt tag="portlet.config.presence"/></option>
<option value="ss_profile"><ssf:nlt tag="portlet.config.profile"/></option>
</select></td></tr></table>

<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply"/>">
</form>
</div>