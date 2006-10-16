<%
// The dashboard "workspace tree" component
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
<span class="ss_bold"><ssf:nlt tag="dashboard.startingPoint"/></span>
<br/>
<div class="ss_indent_medium">
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.start[0] || 
    ssDashboard.dashboard.components[ssComponentId].data.start[0]== 'this'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" value="this" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.startingPoint.current"/><br/>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.align[0] == 'select'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" value="select" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.startingPoint.select"/><br/>
</div>
</td>
</tr>

<tr>
<td valign="top"><br/></td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.rootOpen"/></span>
<br/>
<div class="ss_indent_medium">
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.rootOpen[0] || 
    ssDashboard.dashboard.components[ssComponentId].data.rootOpen[0]== 'true'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" value="true" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="yes"/><br/>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.rootOpen[0] == 'false'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" value="false" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="no"/><br/>
</div>
</td>
</tr>

</table>
