<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<!-- h1> MODIFY DASHBOARD COMPONENT </h1 -->

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">

<div class="ss_form">
<form class="ss_form" method="post" 
  action="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
  	name="action" value="modify_dashboard"/><ssf:param 
  	name="binderId" value="${ssBinder.id}"/></ssf:url>" 
  	name="${ssSearchFormForm}" id="${ssSearchFormForm}">

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
<c:set var="ss_dashboard_config_form_name" value="${ssDashboard.ssComponentId}${renderResponse.namespace}form1" scope="request"/>
<form method="post" name="${ss_dashboard_config_form_name}" 
  id="${ss_dashboard_config_form_name}" 
  action="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
  	name="action" value="modify_dashboard"/><ssf:param 
  	name="binderId" value="${ssBinder.id}"/></ssf:url>"
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
			<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
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
	<input type="submit" class="ss_submit" name="_saveConfigData" value="<ssf:nlt tag="button.apply"/>" />
	&nbsp;&nbsp;
	<input type="submit" class="ss_submit" name="closeBtn"   value="<ssf:nlt tag="button.close" text="Close"/>"/>
	&nbsp;&nbsp;
	<input type="submit" class="ss_submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>"/>
</form>
</div>

</div>
</div>
</div>
</div>

