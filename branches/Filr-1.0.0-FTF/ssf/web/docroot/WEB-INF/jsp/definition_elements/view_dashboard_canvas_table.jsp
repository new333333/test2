<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //View dashboard canvas table %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>  
  
<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

<c:set var="ss_toolbar_count" value="0"/>
<c:set var="ss_dashboard_control_count" value="0" scope="request"/>
<c:set var="ss_dashboard_border_count" value="0" scope="request"/>
<c:set var="ss_component_count" value="0" scope="request"/>

			<div id="wide_top">
			  <c:forEach var="component" items="${ssDashboard.wide_top}">
			  	  <c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				  <c:if test="${empty ss_dashboard_scope}"><c:set var="ss_dashboard_scope" value="local" scope="request"/></c:if>
				  <c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				  <c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				  <c:set var="ss_dashboard_dashboardList" value="wide_top" scope="request"/>
				  <c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				  <div id="${renderResponse.namespace}_dashboard_component_${component.id}" 
				    class="ss_dashboard_component" style="margin:0px; padding:0px;">
				    <ssf:dashboard id="${component.id}" 
				      type="viewComponent" configuration="${ssDashboard}"/>
				  </div>
			  </c:forEach>
			</div>

			<div id="narrow_fixed">
			  <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
				<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				<c:if test="${empty ss_dashboard_scope}"><c:set var="ss_dashboard_scope" value="local" scope="request"/></c:if>
				<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				<c:set var="ss_dashboard_dashboardList" value="narrow_fixed" scope="request"/>
				<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				<div id="${renderResponse.namespace}_dashboard_component_${component.id}"
				  class="ss_dashboard_component" style="margin:0px; padding:0px;">
				  <ssf:dashboard id="${component.id}" 
				    type="viewComponent" configuration="${ssDashboard}"/>
				</div>
			  </c:forEach>
			</div>

			<div id="narrow_variable">
			  <c:forEach var="component" items="${ssDashboard.narrow_variable}">
				<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				<c:if test="${empty ss_dashboard_scope}"><c:set var="ss_dashboard_scope" value="local" scope="request"/></c:if>
				<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				<c:set var="ss_dashboard_dashboardList" value="narrow_variable" scope="request"/>
				<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				<div id="${renderResponse.namespace}_dashboard_component_${component.id}"
				  class="ss_dashboard_component" style="margin:0px; padding:0px;">
				  <ssf:dashboard id="${component.id}" 
				    type="viewComponent" configuration="${ssDashboard}"/>
				</div>
			  </c:forEach>
			</div>

			<div id="wide_bottom">
			  <c:forEach var="component" items="${ssDashboard.wide_bottom}">
				<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				<c:if test="${empty ss_dashboard_scope}"><c:set var="ss_dashboard_scope" value="local" scope="request"/></c:if>
				<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				<c:set var="ss_dashboard_dashboardList" value="wide_bottom" scope="request"/>
				<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				<div id="${renderResponse.namespace}_dashboard_component_${component.id}"
				  class="ss_dashboard_component" style="margin:0px; padding:0px;">
				  <ssf:dashboard id="${component.id}" 
				    type="viewComponent" configuration="${ssDashboard}"/>
				</div>
			  </c:forEach>
			</div>

<form name="ss_dashboard_layout_form" id="ss_dashboard_layout_form" 
  style="display:inline;">
<input type="hidden" name="binderId" value="${ssBinder.id}"/>
<input type="hidden" name="_scope" value="${ss_dashboard_table_scope}"/>
<input type="hidden" name="dashboard_layout"/>
</form>

<script type="text/javascript">
${renderResponse.namespace}_toolbar_count = <c:out value="${ss_toolbar_count}"/>;
${renderResponse.namespace}_dashboard_control_count = <c:out value="${ss_dashboard_control_count}"/>;
${renderResponse.namespace}_dashboard_border_count = <c:out value="${ss_dashboard_border_count}"/>;
ss_createOnLoadObj("${renderResponse.namespace}_dashboardInitialization", ${renderResponse.namespace}_dashboardInitialization);
function ${renderResponse.namespace}_dashboardInitialization() {
//	ss_dashboardInitialization('${renderResponse.namespace}_dashboardTable');
//leave this until peter fixes the dojo stuff?? to user namespaces
	ss_dashboardInitialization('ss_dashboardTable');
}
</script>

</c:if>
