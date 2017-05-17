<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<% //View dashboard canvas %>
		<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
		<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
		<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
		<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
		
<!-- The accessory panel can only be displayed once in a page. -->
<!-- If the accessory panel has already been loaded then don't display it again. -->
<c:set var="ss_dashboardComponentNumber" value="0" scope="request"/>
<ssf:ifNotFilr>
  <c:choose>
	<c:when test="${empty accessory_panel_loaded}">
		<c:set var="accessory_panel_loaded" value="true" scope="request"/>

		<%
		String displayStyle = ssUser.getDisplayStyle();
		if (displayStyle == null) displayStyle = "";
		
		
		int entryWindowWidth = 0;
		if (ssUserProperties.containsKey("folderEntryWidth")) {
			try {
				entryWindowWidth = Integer.parseInt((String) ssUserProperties.get("folderEntryWidth"));
			} catch(Exception e) {}
		}
		int entryWindowTop = 0;
		if (ssUserProperties.containsKey("folderEntryTop")) {
			try {
				entryWindowTop = Integer.parseInt((String) ssUserProperties.get("folderEntryTop"));
			} catch(Exception e) {}
		}
		int entryWindowLeft = 0;
		if (ssUserProperties.containsKey("folderEntryLeft")) {
			try {
				entryWindowLeft = Integer.parseInt((String) ssUserProperties.get("folderEntryLeft"));
			} catch(Exception e) {}
		}
		int entryWindowHeight = 0;
		if (ssUserProperties.containsKey("folderEntryHeight")) {
			try {
				entryWindowHeight = Integer.parseInt((String) ssUserProperties.get("folderEntryHeight"));
			} catch(Exception e) {}
		}
		String autoScroll = "true";
		request.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
		request.setAttribute("ss_entryWindowTop", new Integer(entryWindowTop));
		request.setAttribute("ss_entryWindowLeft", new Integer(entryWindowLeft));
		request.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
		%>
		
		<script type="text/javascript">
		//Define the variables needed by the javascript routines
		var ss_iframe_box_div_name = 'ss_iframe_box_div';
		
		<c:if test="${!empty ss_entryWindowTop && !empty ss_entryWindowLeft}">
			var ss_entryWindowTopOriginal = ${ss_entryWindowTop};
			var ss_entryWindowTop = ${ss_entryWindowTop};
			var ss_entryWindowLeft = ${ss_entryWindowLeft};
		</c:if>
		<c:if test="${empty ss_entryWindowTop || empty ss_entryWindowLeft}">
			var ss_entryWindowTopOriginal = -1;
			var ss_entryWindowTop = -1;
			var ss_entryWindowLeft = -1;
		</c:if>
		
		
		var ss_forumRefreshUrl = "<html:rootPath/>js/forum/refresh.html";
		<c:if test="${empty ss_entryWindowWidth}">
		var ss_entryWindowWidth = 0;
		</c:if>
		<c:if test="${!empty ss_entryWindowWidth}">
		var ss_entryWindowWidth = "${ss_entryWindowWidth}";
		</c:if>
		var ss_entryBackgroundColor = "${ss_style_background_color}";
		var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
		var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
		</script>


		  <!-- Start of dashboard "Add penlet" form -->
		  <c:if test="${empty ssBinderConfig && (!ssUser.shared || ssDashboard.sharedModificationAllowed)}">
		  <div id="${renderResponse.namespace}_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
		    <form method="post" action="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
		    	name="binderId" value="${ssBinder.id}"/></ssf:url>">
			  <div class="ss_style" style="margin:10px;">
		        <span class="ss_bold"><ssf:nlt tag="dashboard.componentScope"/></span><br>
		 		<table cellspacing="0" cellpadding="0">
		 		<c:if test="${!ssUser.shared}">
		        <tr><td valign="top"><input type="radio" name="_scope" value="local" id="local" checked="checked" /></td>
		        	<td><label for="local"><ssf:nlt tag="dashboard.componentScope.local"/></label></td></tr>
		        <tr><td valign="top"><input type="radio" name="_scope" value="global" id="global"/></td>
		        	<td><label for="global"><ssf:nlt tag="dashboard.componentScope.global"/> 
		        	  <ssf:inlineHelp tag="ihelp.other.global_accessories"/></label></td></tr>
		        <c:if test="${ssDashboard.sharedModificationAllowed}">
		          <tr><td valign="top"><input type="radio" name="_scope" value="binder" id="binder"/></td>
		          	<td><label for="binder"><ssf:nlt tag="dashboard.componentScope.binder"/> 
		          	 <ssf:inlineHelp tag="ihelp.other.community_accessories"/></label></td></tr>
		        </c:if>
				</c:if>
		 		<c:if test="${ssUser.shared}">
		          <tr><td valign="top"><input type="radio" name="_scope" value="binder" id="binder" checked/></td>
		          	<td><label for="binder"><ssf:nlt tag="dashboard.componentScope.binder"/> 
		          	 <ssf:inlineHelp tag="ihelp.other.community_accessories"/></label></td></tr>
				</c:if>
		        </table>
		        <br/>
		        <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
		        <table cellspacing="0" cellpadding="0">
		        <c:forEach var="component" items="${ssDashboard.components_list}">
		           <c:set var="ss_dashboardComponentNumber" value="${ss_dashboardComponentNumber + 1}" scope="request"/>
		           <tr><td valign="top"><input type="radio" name="componentName" id="componentName${ss_dashboardComponentNumber}" 
		             value="${component}"></td>
		             <td><label for="componentName${ss_dashboardComponentNumber}"
		             ><ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/></label></td></tr>
		        </c:forEach>
		        </table>
		        <br>
			    <input class="ss_submit" type="submit" name="add_wideTop" 
			      value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
			    <input class="ss_submit" type="submit" name="cancel" 
			      value="<ssf:nlt tag="button.cancel"/>" 
			      onclick="ss_hideDashboardMenu(this);return false;">
			    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
			    <input type="hidden" name="_componentId" value="">
			    <input type="hidden" name="_returnView" value="binder"/>
			  </div>
					<sec:csrfInput />
				</form>
		  </div>
		  </c:if>
		  <c:if test="${!empty ssBinderConfig}">
		  <div id="${renderResponse.namespace}_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
		    <form method="post" action="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
		    	name="binderId" value="${ssBinder.id}"/></ssf:url>">
		         <input type="hidden" name="_scope" value="binder"/>
		 	  <div class="ss_style" style="margin:10px;">
		        <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
		        <table cellspacing="0" cellpadding="0">
		        <c:forEach var="component" items="${ssDashboard.components_list}">
		           <c:set var="ss_dashboardComponentNumber" value="${ss_dashboardComponentNumber + 1}" scope="request"/>
		           <tr><td valign="top"><input type="radio" name="componentName" id="componentName${ss_dashboardComponentNumber}" 
		             value="${component}"></td>
		             <td><label for="componentName${ss_dashboardComponentNumber}"
		             ><ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/></label></td></tr>
		        </c:forEach>
		        </table>
		        <br>
			    <input class="ss_submit" type="submit" name="add_wideTop" 
			      value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
			    <input class="ss_submit" type="submit" name="cancel" 
			      value="<ssf:nlt tag="button.cancel"/>" 
			      onclick="ss_hideDashboardMenu(this);return false;">
			    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
			    <input type="hidden" name="_componentId" value="">
			    <input type="hidden" name="_returnView" value="binder"/>
			  </div>
					<sec:csrfInput />
				</form>
		  </div>
		  </c:if>
		  <!-- End of dashboard "add penlet" form -->
		<c:if test="${!empty ssDashboardToolbar}">  
		<div class="ss_dashboardContainer" >
			<div class="ss_utils_bar"  align="right">
			<ssf:toolbar toolbar="${ssDashboardToolbar}" style="ss_utils_bar" />
			</div>
			<div class="ss_clear"></div>
		</c:if>
		
		<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">
		
		  <div id="${renderResponse.namespace}_dashboardConfigurationMenu" class="ss_dashboard_menu" align="left">
			<ul class="ss_dropdownmenu" 
			  style="list-style: outside; margin:2px 2px 2px 18px; padding:2px;">
			  <li><a href="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
			  	    name="binderId" value="${ssBinder.id}"/><ssf:param 
			  	    name="_scope" value="local"/><ssf:param 
			  	    name="operation" value="set_dashboard_title"/></ssf:url>"
			      ><ssf:nlt tag="dashboard.setTitle"/></a></li>
			  <li><a href="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
			  	name="binderId" value="${ssBinder.id}"/><ssf:param 
			  	name="_scope" value="global"/></ssf:url>"><ssf:nlt 
			  	tag="dashboard.configure.global"/></a></li>
			  <c:if test="${ssDashboard.sharedModificationAllowed}">
			    <li><a href="<ssf:url action="modify_dashboard" actionUrl="true"><ssf:param 
			    	name="binderId" value="${ssBinder.id}"/><ssf:param 
			    	name="_scope" value="binder"/></ssf:url>"><ssf:nlt 
			    	tag="dashboard.configure.binder"/></a></li>
			  </c:if>
			</ul>
		  </div>
		
		 <!-- Start of dashboard canvas -->
		  <div id="${renderResponse.namespace}_dashboardComponentCanvas"
			<c:if test="${ss_show_all_dashboard_components}">
			  style="visibility:visible; display:block;"
			</c:if>
			<c:if test="${!ss_show_all_dashboard_components}">
			  style="visibility:hidden; display:none;"
			</c:if>
		 >
		
			<div class="ss_content_window">
				<span class="ss_bold"><c:out value="${ssDashboard.title}"/> 
				  <c:if test="${ssDashboard.includeBinderTitle}">
				    <c:out value="${ssBinder.title}"/>
				  </c:if>
				</span>
		
				<div id="${renderResponse.namespace}_dashboard_toolbar_${ss_toolbar_count}"
				   style="visibility:hidden; display:none;">
				  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
				</div>
		
		
				<!-- Start of dashboard components -->
				<div>
		
		<c:set var="ss_dashboard_table_scope" value="local" scope="request"/>
		<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_table.jsp" %>
		
			  	</div>
				<!-- End of dashboard components -->
			</div>
		  </div>
		<!-- End of dashboard canvas -->
		
		</c:if>
		<c:if test="${!empty ssDashboardToolbar}">
		</div>
		</c:if>
<script type="text/javascript">
ss_createOnLayoutChangeObj('ss_resizeDashboardCanvas',
	function() {
		if (window.top.ss_jspLayoutChanged) {
			window.top.ss_jspLayoutChanged('${ssBinder.id}');
		}
	});
</script>
		
	</c:when>
	<c:otherwise>
		<!-- Add some text that tells the user they can't have more than one accessory panel on a page. -->
		<ssf:nlt tag="workspace.accessoryPanel.moreThanOne" />
	</c:otherwise>
  </c:choose>
</ssf:ifNotFilr>