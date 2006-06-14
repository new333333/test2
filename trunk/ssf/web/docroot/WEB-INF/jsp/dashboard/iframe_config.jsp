<%
// The dashboard "iframe" component
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
<br/>
<table>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.url"/></span>
<br/>
<input type="text" name="data.url" size="60" 
  value="${ssDashboard.dashboard.components[ssDashboardId].data.url[0]}"/>
</td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.height"/></span><br/>
<input type="text" name="data.height" size="6" 
  value="${ssDashboard.dashboard.components[ssDashboardId].data.height[0]}"/>
</td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.alignment"/></span>
<br/>
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssDashboardId].data.align[0] || 
    ssDashboard.dashboard.components[ssDashboardId].data.align[0] == 'left'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
&nbsp;&nbsp;&nbsp;<input type="radio" name="data.align" value="left" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.alignment.left"/><br/>
<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].data.align[0] == 'center'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
&nbsp;&nbsp;&nbsp;<input type="radio" name="data.align" value="center" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.alignment.center"/><br/>
<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].data.align[0] == 'right'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
&nbsp;&nbsp;&nbsp;<input type="radio" name="data.align" value="right" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.alignment.right"/><br/>
</td>
</tr>

</table>
