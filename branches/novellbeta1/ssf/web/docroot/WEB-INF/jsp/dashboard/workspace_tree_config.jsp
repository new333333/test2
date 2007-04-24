<%
// The dashboard "workspace tree" component
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
