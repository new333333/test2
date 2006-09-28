<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_portlet_style ss_portlet">
<ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar" />

<table width="100%">
<tr><td>
 <a href="<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="view_ws_listing"/>
				<portlet:param name="binderId" value="${ssUser.parentBinder.id}"/>
				<portlet:param name="entryId" value="${ssUser.id}"/>
				</portlet:renderURL>">

<c:out value="${ssUser.title}"/></a></td></tr>
</table>
</div>
