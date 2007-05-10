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
<div class="ss_rounded">
<div style="margin:6px;">
<h3><ssf:nlt tag="administration.configure_cfg" text="Configurations"/></h3>
<h3><ssf:nlt tag="administration.configure_cfg.add"/></h3>
<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:renderURL>
			<portlet:param name="action" value="configure_configuration"/>
			<portlet:param name="operation" value="add"/>
		</portlet:renderURL>" >
		
	 <input type="radio" name="cfgType" value="8"><ssf:nlt tag="general.type.workspace"/><br/>
	 <input type="radio" name="cfgType" value="5" ><ssf:nlt tag="general.type.folder"/><br/>
	 <input type="radio" name="cfgType" value="-1" checked><ssf:nlt tag="administration.configure_cfg.clone"/><br/>
	<br/><br/>

	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_cfg.existing" text="Currently defined configurations"/></h3>
<span class="labelLeft"><ssf:nlt tag="administration.configure_cfg.standardTemplates"/></span>
<ul class="ss_square">
<c:forEach var="bconfig" items="${ssBinderConfigs}">
<jsp:useBean id="bconfig" type="com.sitescape.team.domain.TemplateBinder"/>
	<c:if test="${bconfig.reserved}">
	<li><a href="" onClick="return <%=cTreeName%>_showId('${bconfig.id}', this, 'configure_configuration');">
	<ssf:nlt tag="${bconfig.templateTitle}" checkIfTag="true"/></a>
		<c:if test="${!empty bconfig.templateDescription.text}">
		<c:out value="<%= NLT.getDef(bconfig.getTemplateDescription().getText()) %>" escapeXml="false" />
		</c:if>
		</li>
	</c:if>

</c:forEach>
</ul>
<span class="labelLeft"><ssf:nlt tag="administration.configure_cfg.customTemplates"/></span>
<ul class="ss_square">
<c:forEach var="cconfig" items="${ssBinderConfigs}">
<jsp:useBean id="cconfig" type="com.sitescape.team.domain.TemplateBinder"/>
	<c:if test="${!cconfig.reserved}">
	<li><a href="" onClick="return <%=cTreeName%>_showId('${cconfig.id}', this, 'configure_configuration');">
	<ssf:nlt tag="${cconfig.templateTitle}" checkIfTag="true"/></a>
		<c:if test="${!empty cconfig.templateDescription.text}">
		<c:out value="<%= NLT.getDef(cconfig.getTemplateDescription().getText()) %>" escapeXml="false" />
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
</div>

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

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
<div class="ss_clear"></div>
<div class="ss_tab_canvas">
<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
			    <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">
				
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

</div>
			    </div>
			</div>
		</div>
	</div>
<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>


</div>
</div>

<div class="ss_formBreak"/>
<form method="post" action="<portlet:renderURL><portlet:param 
		name="action" value="configure_configuration"/>
		</portlet:renderURL>" >
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</form>
</div>
</c:if>

<c:if test="${ssOperation == 'add_folder' or ssOperation == 'add_workspace'}">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">
<form method="post" action="<portlet:actionURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param name="operation" value="${ssOperation}"/><portlet:param 
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
</div>

</c:if>


</c:if>
</div>

