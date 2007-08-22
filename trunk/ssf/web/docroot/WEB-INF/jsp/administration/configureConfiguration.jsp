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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">

<%
String cTreeName = renderResponse.getNamespace() + "_cTree";
%>
<c:if test="${empty ssBinderConfig}">
<c:if test="${ssOperation != 'export'}">

<script type="text/javascript">

function <%=cTreeName%>_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="ssActionPlaceHolder"/><portlet:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
} 
</script>
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">
<h3><ssf:nlt tag="administration.configure_configurations" /></h3>
<br/>
<br/>
<h3><ssf:nlt tag="administration.configure_cfg.add"/></h3>
<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>add" method="post" 
	action="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_configuration"/><portlet:param 
	name="operation" value="add"/></portlet:actionURL>" >
		
	 <input type="radio" name="cfgType" value="8"><ssf:nlt tag="general.type.workspace"/><br/>
	 <input type="radio" name="cfgType" value="5" checked><ssf:nlt tag="general.type.folder"/><br/>
	 <input type="radio" name="cfgType" value="-1" ><ssf:nlt tag="administration.configure_cfg.clone"/> 
	   <ssf:inlineHelp tag="ihelp.designers.new_template"/><br/>
	<br/>

	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>

<br>
<hr>
<br/>
<h3><ssf:nlt tag="administration.configure_cfg.import"/></h3>
<div class="ss_indent_medium">
<ul class="ss_square">
<li><a target="_blank" href="<ssf:url adapter="true" 
			portletName="ss_administration" 
			action="configure_configuration" 
			actionUrl="true" ><ssf:param 
		    name="operation" value="add"/><ssf:param 
		    name="cfgType" value="-2"/></ssf:url>"><ssf:nlt tag="administration.configure_cfg.import"/></a> 
<ssf:inlineHelp tag="ihelp.designers.import_definitions"/>
</li>
<li><a href="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_configuration"/><portlet:param 
	name="operation" value="add"/><portlet:param 
	name="cfgType" value="-3"/></portlet:actionURL>"><ssf:nlt tag="administration.configure_cfg.reload"/></a> 
</li>
</ul>
</div> 

<br>
<hr>
<br/>
<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>export" method="post" 
	action="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="operation" value="export"/></portlet:renderURL>" >
<h3><ssf:nlt tag="administration.export.templates"/></h3>
<br>
	<input type="submit" class="ss_submit" name="exportBtn" value="<ssf:nlt tag="button.export" text="Export"/>">
</form>
<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_cfg.existing" text="Currently defined templates"/> <ssf:inlineHelp tag="ihelp.designers.current_templates"/></h3>
<div style="padding-top: 10px; padding-bottom: 2px;"><span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.standardTemplates"/></span></div>
<ul class="ss_square">
<c:forEach var="bconfig" items="${ssBinderConfigs}">
<jsp:useBean id="bconfig" type="com.sitescape.team.domain.TemplateBinder"/>
	<c:if test="${bconfig.reserved}">
	<li><a class="ss_bold ss_style ss_title_link" href="" onClick="return <%=cTreeName%>_showId('${bconfig.id}', this, 'configure_configuration');">
	<ssf:nlt tag="${bconfig.templateTitle}" checkIfTag="true"/></a>
		<c:if test="${!empty bconfig.templateDescription.text}">
		- <c:out value="<%= NLT.getDef(bconfig.getTemplateDescription().getText()) %>" escapeXml="false" />
		</c:if>
		</li>
	</c:if>

</c:forEach>
</ul>
<div style="padding-top: 10px; padding-bottom: 2px;"><span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.customTemplates"/></span></div>
<ul class="ss_square">
<c:forEach var="cconfig" items="${ssBinderConfigs}">
<jsp:useBean id="cconfig" type="com.sitescape.team.domain.TemplateBinder"/>
	<c:if test="${!cconfig.reserved}">
	<li><a class="ss_bold ss_style ss_title_link" href="" onClick="return <%=cTreeName%>_showId('${cconfig.id}', this, 'configure_configuration');">
	<ssf:nlt tag="${cconfig.templateTitle}" checkIfTag="true"/></a>
		<c:if test="${!empty cconfig.templateDescription.text}">
		- <c:out value="<%= NLT.getDef(cconfig.getTemplateDescription().getText()) %>" escapeXml="false" />
		</c:if>
	</li>
	</c:if>
</c:forEach>
</ul>
<br/>

<form class="ss_style ss_form" method="post" 
	action="<portlet:renderURL windowState="normal" portletMode="view"></portlet:renderURL>">

	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
</div>
</c:if>

<c:if test="${ssOperation == 'export'}">
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" action="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_configuration"/><portlet:param 
	name="operation" value="export"/></portlet:actionURL>" 
	method="post" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm">

<br>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.export.templates.select"/></span>
<%@include file="/WEB-INF/jsp/administration/commonSelectTree.jsp" %>

</form>
<br>
</td></tr></table>
</c:if>

</c:if>

<c:if test="${!empty ssBinderConfig}">

<c:if test="${empty ssOperation || ssOperation == ''}">
<span>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.workspaceTemplate.title"/>
</c:if>
<c:if test="${ssBinderConfig.entityType != 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.folderTemplate.title"/>
</c:if>
<ssf:nlt tag="${ssBinderConfig.templateTitle}" checkIfTag="true"/></span>
<br/>
<script type="text/javascript">
var ss_reloadUrl = "${ss_reloadUrl}";

function <%=cTreeName%>_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="ssActionPlaceHolder"/><portlet:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
} 
function ss_confirmDeleteConfig() {
	if (confirm('<ssf:nlt tag="administration.configure_cfg.confirmDelete"/>')) {
		return true
	} else {
		return false
	}
}
</script>
<c:set var="showEntryCallbackRoutine" value="none" scope="request"/>

<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="ss_view_sidebar">

	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

	<% // Folder Sidebar %>

    <%@ include file="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" %>

    <ssf:sidebarPanel title="__definition_default_workspace" id="ss_workspace_sidebar"
        initOpen="true" sticky="true">
		<c:if test="${!empty ssSidebarWsTree}">
		<ssf:tree treeName="sidebarWsTree" 
		  treeDocument="${ssSidebarWsTree}" 
		  highlightNode="${ssBinder.id}" 
		  showIdRoutine="ss_tree_showId"
		  rootOpen="true"
		  nowrap="true"/>
		</c:if>
	</ssf:sidebarPanel>

	</div>


	</td>
	<td valign="top" class="ss_view_info">

	<% // Folder toolbar %>
	<div class="ss_content_inner">
	<ssf:toolbar toolbar="${ssForumToolbar}" style="ss_actions_bar2 ss_actions_bar"/>
	</div>
	
	<div class="ss_content_inner">
	<% // Navigation links %>
	<c:if test="${!empty ssNavigationLinkTree}">
	<c:set var="ss_breadcrumbsShowIdRoutine" value="<%= cTreeName + "_showId"%>" scope="request" />
	<c:set var="ss_breadcrumbsTreeName"  value="<%=cTreeName%>" scope="request" />
	
	<div class="ss_content_inner">
	<%@ include file="/WEB-INF/jsp/administration/config_navigation_links.jsp" %>
	</div>
	</c:if>
	<br/>
	<% // Show the workspace according to its definition %>
	<c:if test="${ssBinder.entityType == 'workspace'}">
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  processThisItem="true"
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  entry="${ssBinder}" />
	</c:if>
	<c:if test="${ssBinder.entityType == 'folder'}">
	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}" />
	  </c:if>
	</td>
	</tr></tbody></table>

</div>

<div class="ss_formBreak" align="left"/>
<form method="post" action="<portlet:renderURL><portlet:param 
		name="action" value="configure_configuration"/></portlet:renderURL>" >
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</form>
</div>
</c:if>

<c:if test="${ssOperation == 'add_folder' or ssOperation == 'add_workspace'}">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">
<form method="post" action="<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="configure_configuration"/><portlet:param 
		name="operation" value="${ssOperation}"/><portlet:param 
		name="binderId" value="${ssBinderConfig.id}"/></portlet:actionURL>" >

<h3><ssf:nlt tag="administration.configure_cfg.existing"/></h3>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <input type="radio" name="binderConfigId" value="${config.id}" <c:if test="${status.first}">checked="checked"</c:if>><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/><br/>
  </c:forEach>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>

</form>
</div>
</div>

</c:if>


</c:if>
</div>

