<%
// The dashboard "iframe" component
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<br/>
<table>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.url"/></span>
<br/>
<input type="text" name="data_url" size="60" 
  value="${ssDashboard.dashboard.components[ssComponentId].data.url[0]}"/>
</td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.height"/></span><br/>
<input type="text" name="data_height" size="6" 
  value="${ssDashboard.dashboard.components[ssComponentId].data.height[0]}"/>
</td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.alignment"/></span>
<br/>
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.align[0] || 
    ssDashboard.dashboard.components[ssComponentId].data.align[0] == 'left'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
&nbsp;&nbsp;&nbsp;<input type="radio" name="data_align" value="left" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.alignment.left"/><br/>
<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.align[0] == 'center'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
&nbsp;&nbsp;&nbsp;<input type="radio" name="data_align" value="center" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.alignment.center"/><br/>
<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.align[0] == 'right'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
&nbsp;&nbsp;&nbsp;<input type="radio" name="data_align" value="right" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.alignment.right"/><br/>
</td>
</tr>

</table>
