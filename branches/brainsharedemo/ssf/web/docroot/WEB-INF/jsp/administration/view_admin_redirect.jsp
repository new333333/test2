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
<c:if test="${empty ssErrorList}">
<script type="text/javascript">
self.location.replace('<portlet:renderURL windowState="normal" portletMode="view"/>')
</script>
</c:if>
<c:if test="${!empty ssErrorList}">
<form class="ss_style ss_form" action="<portlet:renderURL windowState="normal" portletMode="view"/>"
		 method="post" name="<portlet:namespace />fm">
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="administration.errors"/></span>
<br/>
<br/>
<ul>
<c:forEach var="err" items="${ssErrorList}">
	<li>${err}</li>
</c:forEach>
</ul>
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">

</form>
</c:if>
