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
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">
<span class="ss_largerprint"><ssf:nlt tag="dashboard.configureDashboard"/></span>
<br/>

<div class="ss_form">
<form class="ss_form" method="post">
<div class="ss_form ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</div>
</form>
</div>

<div class="ss_form">
<c:if test="${ssDashboard.scope == 'local'}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="dashboard.localDashboard" /></span>
  <br/>
</c:if>
<c:if test="${ssDashboard.scope == 'global'}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="dashboard.globalDashboard" /></span>
  <br/>
</c:if>
<c:if test="${ssDashboard.scope == 'binder'}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="dashboard.binderDashboard" /></span>
  <br/>
</c:if>
</div>
<br/>
<br/>

<div style="width:100%;">
<span class="ss_bold"><ssf:nlt tag="dashboard.setTitle" /></span><br/>
<table cellpadding="6" style="width:100%;">
  <tr>
    <td>
	<form method="post" >
	<input type="text" name="title" size="60" value="${ssDashboard.dashboard.title}"/><br/>
	<c:set var="checked" value=""/>
	<c:if test="${ssDashboard.dashboard.includeBinderTitle}">
	  <c:set var="checked" value="checked=checked"/>
	</c:if>
	<input type="checkbox" name="includeBinderTitle" <c:out value="${checked}"/> />
	<span><ssf:nlt tag="dashboard.includeBinderTitle"/></span>
	<br/>
	
	<input type="submit" class="ss_submit" name="set_title" 
	  value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
	<input type="hidden" name="scope" value="${ssDashboard.scope}"/>
	</form>
    </td>
  </tr>
</table>

<br/>
<br/>
<br/>

<div style="width:100%;">
<c:if test="${ssDashboard.scope == 'local'}">
  <span class="ss_bold"><ssf:nlt tag="dashboard.layout" /></span>
</c:if>
<c:if test="${ssDashboard.scope == 'global'}">
  <span class="ss_bold"><ssf:nlt tag="dashboard.setDefaultLayout" /></span>
</c:if>
<c:if test="${ssDashboard.scope == 'binder'}">
  <span class="ss_bold"><ssf:nlt tag="dashboard.setDefaultLayout" /></span>
</c:if>
<br/>
<table cellpadding="6" style="width:100%;">
  <tr>
    <td colspan="2">
    
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="wide_top"/>
		<c:set var="returnView" value="form"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>
    </td>
  </tr>

  <tr>
    <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="narrow_fixed"/>
		<c:set var="returnView" value="form"/>
		<div style="margin:0px; padding:0px; 
		    width:${ssDashboard.narrowFixedWidth + 5}px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>
    </td>

    <td valign="top" width="96%">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="narrow_variable"/>
		<c:set var="returnView" value="form"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="wide_bottom"/>
		<c:set var="returnView" value="form"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>
    </td>
  </tr>
  <c:if test="${empty ssDashboard.wide_top && 
                empty ssDashboard.narrow_fixed && 
                empty ssDashboard.narrow_variable && 
                empty ssDashboard.wide_bottom}">
  <tr>
    <td>
      <span class="ss_italics ss_smallprint">
        <ssf:nlt tag="dashboard.noComponents"/>
      </span>
    </td>
  </tr>
  </c:if>
</table>
</div>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<form method="post">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</form>
</div>

</div>
</div>
</div>
</div>
</div>

