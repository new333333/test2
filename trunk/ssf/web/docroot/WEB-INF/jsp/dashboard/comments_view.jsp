<%
// The dashboard "comments" component
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
Displaying 0 of 0 comments ( View All | Add Comment )
<div style="border-style: solid; border-color: #222222; border-width: 2px; padding: 5px; background-color: #cccccc;">
<c:out value="${ssDashboard.dashboard.components[ssComponentId].data.comment[0]}" escapeXml="false"/>
</div>
Add Comment
