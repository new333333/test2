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
<% //View dashboard canvas component %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="hideDashboardControls" value="false"/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].displayStyle == 'none'}">
  <c:set var="hideDashboardControls" value="true"/>
</c:if>
<!-- Start of component -->
<div id="${renderResponse.namespace}_component_${ss_component_count}"
  <c:if test="${hideDashboardControls}">
    class="ss_content_window_compact" 
  </c:if>
  <c:if test="${!hideDashboardControls}">
    class="ss_content_window" 
  </c:if>
>
<table width="100%" cellspacing="0" cellpadding="0">

<tr>
<td valign="top" class="ss_dashboard_dragHandle">
  <div class="ss_base_title_bar ss_dashboard_component_dragger"
  <c:if test="${hideDashboardControls}">
	id="${renderResponse.namespace}_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
  >

 <c:if test="${!empty ssBinder}">
    <form 
      method="post" 
  	  action="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
  		name="binderId" value="${ssBinder.id}"/></ssf:url>">
     <c:set var="myId" value="binderId=${ssBinder.id}"/> 
</c:if>
<c:if test="${empty ssBinder}">
    <form 
      method="post" 
  	  action="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
  	  	name="dashboardId" value="${ssDashboardId}"/></ssf:url>">
     <c:set var="myId" value="dashboardId=${ssDashboardId}"/> 
</c:if>
	  <input type="hidden" name="_dashboardList" value="${ss_dashboard_dashboardList}"/>
	  <input type="hidden" name="_componentId" value="${ss_dashboard_id}"/>
	  <input type="hidden" name="_scope" value="${ss_dashboard_scope}"/>
	  <input type="hidden" name="_componentScope" value="${ss_dashboard_componentScope}"/>
	  <input type="hidden" name="_operation" value=""/>
	  <input type="hidden" name="_returnView" value="${ss_dashboard_returnView}"/>
			<ul class="ss_title_bar_icons">
				<li class="ss_nobghover"><a href="javascript:;" title="<ssf:nlt tag="button.moveUp"/>"
				  onclick="ss_submitDashboardChange(this, '_moveUp');return false;"
				><img class="ss_moveUp" border="0" src="<html:imagesPath/>pics/1pix.gif" 
				  alt="<ssf:nlt tag="button.moveUp"/>" /></a></li>
				<li class="ss_nobghover"><a href="javascript:;" title="<ssf:nlt tag="button.moveDown"/>"
				  onclick="ss_submitDashboardChange(this, '_moveDown');return false;"
				><img class="ss_moveDown" border="0" src="<html:imagesPath/>pics/1pix.gif" 
				  alt="<ssf:nlt tag="button.moveDown"/>" /></a></li>
			  
			  <c:if test="${ss_dashboard_componentScope == 'local' || ss_dashboard_componentScope == 'global' || ssDashboard.sharedModificationAllowed}">
				<li class="ss_nobghover"><a href="javascript:;" 
				    onclick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}');ss_submitDashboardChange(this, '_modifyComponentData');return false;"
				  ><img border="0" class="ss_accessory_modify" src="<html:imagesPath/>pics/1pix.gif" 
				    title="<ssf:nlt tag="button.modify"/>" <ssf:alt/> /></a></li>
				<li class="ss_nobghover"><a href="javascript:;"
				  onclick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}'); ss_confirmDeleteComponent(this, '${ss_dashboard_id}', '${renderResponse.namespace}_component_${ss_component_count}', '${renderResponse.namespace}_component2_${ss_component_count}', '${myId}', '${renderResponse.namespace}', '${ss_dashboard_scope}'); return false;"
				><img border="0" class="ss_accessory_delete" src="<html:imagesPath/>pics/1pix.gif" 
				  title="<ssf:nlt tag="button.delete"/>" <ssf:alt/> /></a></li>
			  </c:if>
			  <c:if test="${ss_dashboard_visible}">
				<li class="ss_nobghover"><a href="javascript:;"
				  onclick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', '${renderResponse.namespace}_dashboard_component_${ss_component_count}', '${myId}', '${renderResponse.namespace}', '${ss_dashboard_scope}');return false;"
				><img border="0" src="<html:imagesPath/>icons/accessory_hide.gif" 
				  title="<ssf:nlt tag="button.hide"/>" <ssf:alt/> /></a></li>
			  </c:if>
			  <c:if test="${!ss_dashboard_visible}">
				<li class="ss_nobghover"><a href="javascript:;"
				  onclick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', '${renderResponse.namespace}_dashboard_component_${ss_component_count}', '${myId}', '${renderResponse.namespace}', '${ss_dashboard_scope}');return false;"
				><img border="0" src="<html:imagesPath/>icons/accessory_show.gif" 
				  title="<ssf:nlt tag="button.show"/>" <ssf:alt/> /></a></li>
			  </c:if>
			</ul>
	</form>
	<strong><ssf:nlt tag="${ssDashboard.dashboard.components[ssComponentId].title}" checkIfTag="true"/>&nbsp;</strong>
  </div>
</td>
</tr>
<tr>
<td>
	<div 
      <c:if test="${hideDashboardControls}">
	    id="${renderResponse.namespace}_dashboard_border_${ss_dashboard_border_count}"
	      <c:set var="ss_dashboard_border_count" scope="request" 
	         value="${ss_dashboard_border_count + 1}"/>
      </c:if>
      <c:set var="accessoryStyle" value="ss_content_window_content_off" />
      <c:if test="${!hideDashboardControls && ss_dashboard_visible}">
        <c:set var="accessoryStyle" value="ss_content_window_content" />
      </c:if>
      class="${accessoryStyle}">
			<ssf:dashboard id="${ss_dashboard_id}"
			   type="viewData" configuration="${ssDashboard}"
			   initOnly="true" />
<% // this is the div that will be replaced, don't remove setup from init phase %>
		<div id="${renderResponse.namespace}_dashboard_component_${ss_component_count}" 
		    align="left" style="margin:0px; 
		    <c:if test="${!ss_dashboard_visible}">
		      visibility:hidden; display:none;
		    </c:if>
		    padding:2px;">
		<c:set var="ss_component_count" value="${ss_component_count + 1}" scope="request"/>
			<c:if test="${ss_dashboard_visible}">
			  <ssf:dashboard id="${ss_dashboard_id}"
			     type="viewData" configuration="${ssDashboard}"/>
			</c:if>
    	</div>
	</div>
</td>
</tr>
</table>
</div>
<div class="ss_dashboard_spacer"></div>
<!-- End of component -->

