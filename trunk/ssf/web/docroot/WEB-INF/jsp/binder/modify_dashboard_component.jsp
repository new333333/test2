<%
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

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">

<div class="ss_form">
<form class="ss_form" method="post" 
  action="<portlet:actionURL>
      <portlet:param name="action" value="modify_dashboard"/>
      <portlet:param name="binderId" value="${ssBinder.id}"/>
      </portlet:actionURL>">

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
<c:set var="ss_dashboard_config_form_name" value="${renderResponse.namespace}form1" scope="request"/>
<form method="post" name="${ss_dashboard_config_form_name}" 
  id="${ss_dashboard_config_form_name}" 
  action="<portlet:actionURL>
      <portlet:param name="action" value="modify_dashboard"/>
      <portlet:param name="binderId" value="${ssBinder.id}"/>
      </portlet:actionURL>"
  onSubmit="return ss_onSubmit(this);">
<div style="width:100%;">
	<span class="ss_bold"><ssf:nlt tag="dashboard.componentLayout" /></span>
	<br/>
	<span class="ss_fineprint" style="padding-left:10px;">
	  <ssf:nlt tag="dashboard.componentScope"/>: 
	  <c:if test="${ssDashboard.scope == 'local'}">
	    <ssf:nlt tag="dashboard.componentScope.local"/>
	  </c:if>
	  <c:if test="${ssDashboard.scope == 'global'}">
	    <ssf:nlt tag="dashboard.componentScope.global"/>
	  </c:if>
	  <c:if test="${ssDashboard.scope == 'binder'}">
	    <ssf:nlt tag="dashboard.componentScope.binder"/>
	  </c:if>
	</span>
	<c:set var="id" value="${ssDashboard.ssComponentId}"/>
	<div class="ss_dashboard_config">
		<p class="ss_bold"><ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></p>
		<div style="margin:5px;">
			<span class="ss_bold"><ssf:nlt tag="dashboard.componentTitle"/></span><br/>
			<input type="text" name="title" size="80" value="${ssDashboard.dashboard.components[id].title}"/><br/>
			<span class="ss_bold"><ssf:nlt tag="dashboard.componentStyle"/></span><br/>
			<div class="ss_indent_medium">
				<c:set var="checked" value=""/>
				<c:if test="${empty ssDashboard.dashboard.components[id].displayStyle || 
				              ssDashboard.dashboard.components[id].displayStyle != 'none'}">
				  <c:set var="checked" value="checked=\"checked\""/>
				</c:if>
				<input type="radio" name="displayStyle" value="border" ${checked}>
				<span><ssf:nlt tag="dashboard.componentStyle.border"/></span>
				<br>
				<c:set var="checked" value=""/>
				<c:if test="${ssDashboard.dashboard.components[id].displayStyle == 'none'}">
				  <c:set var="checked" value="checked=\"checked\""/>
				</c:if>
				<input type="radio" name="displayStyle" value="none" ${checked}>
				<span><ssf:nlt tag="dashboard.componentStyle.noBorder"/></span>
			</div>
			<!-- BEFORE DASHBOARD TAG -->
			<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
			<!-- AFTER DASHBOARD TAG -->
			<input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}"/>
			<input type="hidden" name="_componentId" value="${id}"/>
			<input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
			<input type="hidden" name="_returnView" value="${ssDashboard.returnView}"/>
		</div>
	</div>
</div>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="_saveConfigData" value="<ssf:nlt tag="button.ok"/>" />
	&nbsp;&nbsp;
	<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>"/>
	&nbsp;&nbsp;
	<input type="submit" class="ss_submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>"/>
</form>
</div>

</div>
</div>
</div>
</div>
</div>

