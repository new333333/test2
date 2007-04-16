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
<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>

<%
String iframeBoxId = renderResponse.getNamespace() + "_dashboard_iframe_box_div";
%>

<script type="text/javascript">
//Define the variables needed by the javascript routines
var ss_iframe_box_div_name = '<portlet:namespace/>_iframe_box_div';

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

var ss_saveEntryWidthUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_entry_width" />
	</ssf:url>"

var ss_forumRefreshUrl = "<html:rootPath/>js/forum/refresh.html";
<c:if test="${empty ss_entryWindowWidth}">
var ss_entryWindowWidth = 0;
</c:if>
<c:if test="${!empty ss_entryWindowWidth}">
var ss_entryWindowWidth = "${ss_entryWindowWidth}";
</c:if>
var ss_entryBackgroundColor = "${ss_style_background_color}";


var ss_dashboardViewEntryUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="ssActionPlaceHolder"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
		name="newTab" value="1"/></portlet:renderURL>';
var ss_dashboardViewBinderUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="ssActionPlaceHolder"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="newTab" value="1"/></portlet:renderURL>';
		

var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
function ss_showForumEntryInIframe_Popup(url) {
    ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')
	if (ss_viewEntryPopupWidth == "0px") ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
	if (ss_viewEntryPopupHeight == "0px") ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
    self.window.open(url, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars');
    return false;
}
		
function ss_showForumEntryInIframe_Overlay(url) {
	//ss_debug('show url in frame = '+url)
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    
	if (wObj1 == null) return true;
	
    ss_hideSpannedAreas();
    wObj1.style.display = "block";
    wObj1.style.zIndex = ssEntryZ;
    wObj1.style.visibility = "visible";
    //wObj.style.height = parseInt(wObj1.style.height) - ss_entryDivBottomDelta + "px";
    
    if (wObj.src && wObj.src == url) {
    	ss_nextUrl = url
    	wObj.src = ss_forumRefreshUrl;
    } else if (wObj.src && wObj.src == ss_forumRefreshUrl && ss_nextUrl == url) {
    	wObj.src = ss_forumRefreshUrl;
    } else {
    	wObj.src = url
    }

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}
</script>
  
  <!-- Start of dashboard "Add penlet" form -->
  <c:if test="${empty ssBinderConfig}">
  <div id="<portlet:namespace/>_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
    <form method="post" action="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard"/>
	    <portlet:param name="binderId" value="${ssBinder.id}"/>
        </portlet:actionURL>">
	  <div style="margin:10px;">
        <span class="ss_bold"><ssf:nlt tag="dashboard.componentScope"/></span><br>
        <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.componentScope.local"/><br>
        <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.componentScope.global"/><br>
        <c:if test="${ssDashboard.sharedModificationAllowed}">
          <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.componentScope.binder"/><br>
        </c:if>
        <br/>
        <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
        <c:forEach var="component" items="${ssDashboard.components_list}">
           <input type="radio" name="componentName" value="${component}">
             <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
           <br>
        </c:forEach>
        <br>
	    <input class="ss_form" type="submit" name="add_wideTop" 
	      value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	    <input class="ss_form" type="submit" name="cancel" 
	      value="<ssf:nlt tag="button.cancel"/>" 
	      onClick="ss_hideDashboardMenu(this);return false;">
	    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	    <input type="hidden" name="_componentId" value="">
	    <input type="hidden" name="_returnView" value="binder"/>
	  </div>
    </form>
  </div>
  </c:if>
  <c:if test="${!empty ssBinderConfig}">
  <div id="<portlet:namespace/>_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
    <form method="post" action="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard"/>
	    <portlet:param name="binderId" value="${ssBinder.id}"/>
        </portlet:actionURL>">
         <input type="hidden" name="_scope" value="binder"/>
 	  <div style="margin:10px;">
        <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
        <c:forEach var="component" items="${ssDashboard.components_list}">
           <input type="radio" name="componentName" value="${component}">
             <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
           <br>
        </c:forEach>
        <br>
	    <input class="ss_form" type="submit" name="add_wideTop" 
	      value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	    <input class="ss_form" type="submit" name="cancel" 
	      value="<ssf:nlt tag="button.cancel"/>" 
	      onClick="ss_hideDashboardMenu(this);return false;">
	    <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	    <input type="hidden" name="_componentId" value="">
	    <input type="hidden" name="_returnView" value="binder"/>
	  </div>
    </form>
  </div>
  </c:if>
  <!-- End of dashboard "add penlet" form -->
<c:if test="${!empty ssDashboardToolbar}">  
<div class="ss_dashboardContainer" >
	<div class="ss_utils_bar" >
	<ssf:toolbar toolbar="${ssDashboardToolbar}" style="ss_utils_bar" />
	</div>
	<div class="ss_clear"></div>
</c:if>

<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

  <div id="<portlet:namespace/>_dashboardConfigurationMenu" class="ss_dashboard_menu" align="left">
	<ul class="ss_dropdownmenu" 
	  style="list-style: outside; margin:2px 2px 2px 18px; padding:2px;">
	  <li><a href="<portlet:renderURL>
	  <portlet:param name="action" value="modify_dashboard"/>
	  <portlet:param name="binderId" value="${ssBinder.id}"/>
	  <portlet:param name="_scope" value="local"/>
	  <portlet:param name="operation" value="set_dashboard_title"/>
	  </portlet:renderURL>"><ssf:nlt tag="dashboard.setTitle"/></a></li>
	  <li><a href="<portlet:actionURL>
	  <portlet:param name="action" value="modify_dashboard"/>
	  <portlet:param name="binderId" value="${ssBinder.id}"/>
	  <portlet:param name="_scope" value="global"/>
	  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.global"/></a></li>
	  <c:if test="${ssDashboard.sharedModificationAllowed}">
	    <li><a href="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard"/>
	    <portlet:param name="binderId" value="${ssBinder.id}"/>
	    <portlet:param name="_scope" value="binder"/>
	    </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.binder"/></a></li>
	  </c:if>
	</ul>
  </div>

 <!-- Start of dashboard canvas -->
  <div id="<portlet:namespace/>_dashboardComponentCanvas"
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

		<div id="<portlet:namespace/>_dashboard_toolbar_${ss_toolbar_count}"
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