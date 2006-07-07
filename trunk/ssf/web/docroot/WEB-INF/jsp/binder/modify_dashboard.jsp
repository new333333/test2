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
<h3><ssf:nlt tag="dashboard.configure" text="Configure dashboard options"/></h3>

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

  <table><tr><td>
  <ul style="list-style: inside; margin:2px; padding:2px;">
  <span class="ss_bold"><ssf:nlt tag="dashboard.addModifyDelete"/></span>
  <li><a href="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  <portlet:param name="_scope" value="local"/>
  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.local"/></a></li>
  <li><a href="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  <portlet:param name="_scope" value="global"/>
  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.global"/></a></li>
  <li><a href="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  <portlet:param name="_scope" value="binder"/>
  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.binder"/></a></li>
  </ul>
  </td></tr></table>
  
<form method="post" >
<span class="ss_bold"><ssf:nlt tag="dashboard.title" /></span><br/>
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
<br/>
<br/>

<div style="width:100%;">
<span class="ss_bold"><ssf:nlt tag="dashboard.layout" /></span><br/>
<table border="1" cellpadding="6" style="width:100%;">
  <tr>
    <td colspan="2">
    
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="dashboardList" value="wide_top"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

      <br/>
      <br/>
	  <form method="post">
      <select name="name">
        <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
        <c:forEach var="component" items="${ssDashboard.components_wide}">
          <option value="${component}"><ssf:nlt checkIfTag="true"
            tag="${ssDashboard.component_titles[component]}"/></option>
        </c:forEach>
      </select>
      <input type="submit" name="add_wideTop" 
        value="<ssf:nlt tag="button.add"/>"/>
	  <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	  <input type="hidden" name="_componentId" value="">
	  <input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
	  <input type="hidden" name="_returnView" value="form"/>
      </form>
    </td>
  </tr>

  <tr>
    <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="dashboardList" value="narrow_fixed"/>
		<div style="margin:0px; padding:0px; 
		    width:${ssDashboard.narrowFixedWidth + 5}px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

        <br/>
        <br/>
        <form method="post">
        <select name="name">
          <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
          <c:forEach var="component" items="${ssDashboard.components_narrow_fixed}">
            <option value="${component}"><ssf:nlt checkIfTag="true"
              tag="${ssDashboard.component_titles[component]}"/></option>
          </c:forEach>
        </select>
        <input type="submit" name="add_narrowFixed" 
          value="<ssf:nlt tag="button.add"/>"/>
	    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	    <input type="hidden" name="_componentId" value="">
	    <input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
	    <input type="hidden" name="_returnView" value="form"/>
        </form>
    </td>

    <td valign="top" width="96%">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="dashboardList" value="narrow_variable"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

        <br/>
        <br/>
        <form method="post">
        <select name="name">
          <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
          <c:forEach var="component" items="${ssDashboard.components_narrow_variable}">
            <option value="${component}"><ssf:nlt checkIfTag="true"
              tag="${ssDashboard.component_titles[component]}"/></option>
          </c:forEach>
        </select>
        <input type="submit" name="add_narrowVariable" 
          value="<ssf:nlt tag="button.add"/>"/>
	    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	    <input type="hidden" name="_componentId" value="">
	    <input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
	    <input type="hidden" name="_returnView" value="form"/>
        </form>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="dashboardList" value="wide_bottom"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

        <br/>
        <br/>
	    <form method="post">
        <select name="name">
          <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
          <c:forEach var="component" items="${ssDashboard.components_wide}">
            <option value="${component}"><ssf:nlt checkIfTag="true"
              tag="${ssDashboard.component_titles[component]}"/></option>
          </c:forEach>
        </select>
        <input type="submit" name="add_wideBottom" 
          value="<ssf:nlt tag="button.add"/>"/>
	    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	    <input type="hidden" name="_componentId" value="">
	    <input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
	    <input type="hidden" name="_returnView" value="form"/>
        </form>
    </td>
  </tr>
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

