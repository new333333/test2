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
<% //View dashboard canvas table %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>  
  
<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

<c:set var="ss_toolbar_count" value="0"/>
<c:set var="ss_dashboard_control_count" value="0" scope="request"/>
<c:set var="ss_dashboard_border_count" value="0" scope="request"/>
<c:set var="ss_component_count" value="0" scope="request"/>

<table id="ss_dashboardTable" class="ss_dashboardTable_off" cellspacing="0" cellpadding="0" 
  frame="border" rules="all">
<tbody>
<col width="${ssDashboard.narrowFixedWidth}">
<col width="100%">
<tr>
  <td colspan="2" class="ss_dashboardTable_off">
			<div id="wide_top">
			  <div class="ss_dashboardProtoDropTarget"><img border="0" <ssf:alt/>
			    src="<html:imagesPath/>pics/1pix.gif"></div>
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
			      <div class="ss_dashboardProtoDropTarget"><img border="0"
			        src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>></div>
			  </c:forEach>
			</div>
			
			<div style="width:${ssDashboard.narrowFixedWidth}px;"><img border="0"
			  <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif" /></div>
	
  </td>
</tr>

<tr>
  <td valign="top" class="ss_dashboardTable_off">
			<div id="narrow_fixed">
			  <div class="ss_dashboardProtoDropTarget"><img border="0"
			    <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div>
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
			    <div class="ss_dashboardProtoDropTarget"><img border="0"
			    <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>

			<div style="visibility:hidden; 
			  width:${ssDashboard.narrowFixedWidth}px;"><img border="0"
			  <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div></td>
  
  
  <td valign="top" class="ss_dashboardTable_off">
			<div id="narrow_variable">
			  <div class="ss_dashboardProtoDropTarget"><img border="0"
			    <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div>
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
			    <div class="ss_dashboardProtoDropTarget"><img border="0"
			      <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>
  </td>
</tr>

<tr>
  <td colspan="2" class="ss_dashboardTable_off">
			<div id="wide_bottom">
			  <div class="ss_dashboardProtoDropTarget"><img border="0"
			    <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div>
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
			    <div class="ss_dashboardProtoDropTarget"><img border="0"
			      <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>
  </td>
</tr>

</tbody>
</table>

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
