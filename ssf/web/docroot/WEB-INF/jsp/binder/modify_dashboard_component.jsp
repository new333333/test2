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

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">

<div class="ss_form">
<form class="ss_form" method="post">

<div class="ss_form ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>"/>
<input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}"/>
<input type="hidden" name="_componentId" value=""/>
<input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
<input type="hidden" name="_returnView" value="${ssDashboard.returnView}"/>
</div>
</form>
</div>

<br/>

<c:set var="ss_dashboard_config_form_name" value="form1" scope="request"/>
<form method="post" name="${ss_dashboard_config_form_name}" 
  id="${ss_dashboard_config_form_name}" 
  onSubmit="return ss_onSubmit(this);">
<div style="width:100%;">
	<span class="ss_bold"><ssf:nlt tag="dashboard.componentLayout" /></span>
	<br/>
	<br/>
	<c:set var="id" value="${ssDashboard.ssComponentId}"/>
	<div class="ss_dashboard_config">
		<span class="ss_bold"><ssf:nlt checkIfTag="true"
		  tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></span>
		<br/>
		<br/>
		<div style="margin:5px;">
		<span class="ss_bold"><ssf:nlt tag="dashboard.componentTitle"/></span>
		<br/>
		<input type="text" name="title" size="80" 
		  value="${ssDashboard.dashboard.components[id].title}"/>
		<br/>
		<span class="ss_bold"><ssf:nlt tag="dashboard.componentStyle"/></span>
		<br/>
		<div class="ss_indent_medium">
		<c:set var="checked" value=""/>
		<c:if test="${empty ssDashboard.dashboard.components[id].displayStyle || 
		              ssDashboard.dashboard.components[id].displayStyle == 'shadow'}">
		  <c:set var="checked" value="checked=\"checked\""/>
		</c:if>
		<input type="radio" name="displayStyle" value="shadow" ${checked}>
		<span><ssf:nlt tag="dashboard.componentStyle.shadow"/></span>
		<br>
		<c:set var="checked" value=""/>
		<c:if test="${ssDashboard.dashboard.components[id].displayStyle == 'simple'}">
		  <c:set var="checked" value="checked=\"checked\""/>
		</c:if>
		<input type="radio" name="displayStyle" value="simple" ${checked}>
		<span><ssf:nlt tag="dashboard.componentStyle.simple"/></span>
		<br>
		<c:set var="checked" value=""/>
		<c:if test="${ssDashboard.dashboard.components[id].displayStyle == 'none'}">
		  <c:set var="checked" value="checked=\"checked\""/>
		</c:if>
		<input type="radio" name="displayStyle" value="none" ${checked}>
		<span><ssf:nlt tag="dashboard.componentStyle.none"/></span>
		</div>
		<br/>
		<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
		<input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}"/>
		<input type="hidden" name="_componentId" value="${id}"/>
		<input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
		<input type="hidden" name="_returnView" value="${ssDashboard.returnView}"/>
	</div>
	</div>

</div>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" name="_saveConfigData" value="<ssf:nlt tag="button.ok"/>">
&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" 
  value="<ssf:nlt tag="button.cancel" text="Cancel"/>"/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>"/>
</form>
</div>

</div>
</div>
</div>
</div>
</div>

