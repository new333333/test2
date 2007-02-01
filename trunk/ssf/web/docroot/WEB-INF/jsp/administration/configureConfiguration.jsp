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
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">
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

<h3><ssf:nlt tag="administration.configure_cfg" text="Configurations"/></h3>
<ssf:expandableArea title="<%= NLT.get("administration.configure_cfg.add") %>"  initOpen="true">
<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:renderURL>
			<portlet:param name="action" value="configure_configuration"/>
			<portlet:param name="operation" value="add"/>
		</portlet:renderURL>" >
		
	 <input type="radio" name="cfgType" value="8"><ssf:nlt tag="general.type.workspace" 
		  text="Workspace" /><br/>
	 <input type="radio" name="cfgType" value="12"><ssf:nlt tag="general.type.userWorkspace" 
		  text="User workspace" /><br/>
	 <input type="radio" name="cfgType" value="5" checked><ssf:nlt tag="general.type.folder" 
		  text="Folder" /><br/>
	<br/><br/>

	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_cfg.existing" text="Currently defined configurations"/></h3>
<span class="labelLeft"><ssf:nlt tag="administration.configure_cfg.standardTemplates"/></span>
<ul class="ss_square">
<c:forEach var="bconfig" items="${ssBinderConfigs}">
<jsp:useBean id="bconfig" type="com.sitescape.ef.domain.TemplateBinder"/>
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
<jsp:useBean id="cconfig" type="com.sitescape.ef.domain.TemplateBinder"/>
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
</c:if>

<c:if test="${!empty ssBinderConfig}">

<c:if test="${empty ssOperation || ssOperation == ''}">
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
<h3>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.workspaceTemplate.title"/>
</c:if>
<c:if test="${ssBinderConfig.entityType != 'workspace'}">
<ssf:nlt tag="administration.configure_cfg.folderTemplate.title"/>
</c:if>
<ssf:nlt tag="${ssBinderConfig.templateTitle}" checkIfTag="true"/></h3>
<% // manage toolbar %>
<c:if test="${!empty ssForumToolbar}">
<ssf:toolbar toolbar="${ssForumToolbar}" style="ss_actions_bar"/>
</c:if>
<c:if test="${!empty ssNavigationLinkTree}">
<c:set var="ss_breadcrumbsShowIdRoutine" value="<%= cTreeName + "_showId"%>" scope="request" />
<c:set var="ss_breadcrumbsTreeName"  value="<%=cTreeName%>" scope="request" />

<div class="ss_content_inner">
<%@ include file="/WEB-INF/jsp/administration/config_navigation_links.jsp" %>
</div>
</c:if>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas.jsp" %>
<br/>

<table>
<tr><td>
<c:if test="${ssBinderConfig.entityType == 'workspace'}">
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.workspaceTarget.label"/></span>
</c:if>
<c:if test="${ssBinderConfig.entityType != 'workspace'}">
<span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.folderTarget.label"/></span>
</c:if>
<ssf:nlt tag="${ssBinderConfig.title}" checkIfTag="true"/>
</td></tr>
<tr><td>
<span class="ss_labelAbove"><ssf:nlt tag="administration.configure_cfg.description"/></span>
    <div align="left">
    <c:if test="${!empty ssBinderConfig.description.text}">
	<c:out value="${ssBinderConfig.description.text}" escapeXml="false"/>
	</c:if>
    <c:if test="${empty ssBinderConfig.description.text}">
	<ssf:nlt tag="common.select.none"/>
	</c:if>
	
  	</div>
</td></tr>

<tr><td>

<input type="checkbox" name="defsI" <c:if test="${ssBinderConfig.definitionsInherited}">checked="checked"</c:if> disabled="disabled"/>
<span class="ss_labelRight"><ssf:nlt tag="administration.configure_cfg.definitionsInherited"/></span>
</td></tr>
<tr><td>
<input type="checkbox" name="funcI" <c:if test="${ssBinderConfig.functionMembershipInherited}">checked="checked"</c:if> disabled="disabled"/>
<span class="ss_labelRight"><ssf:nlt tag="administration.configure_cfg.functionMembershipInherited"/></span>
</td></tr>

<c:if test="${ssBinderConfig.entityType == 'folder'}">
<tr><td>
<input type="checkbox" name="library" <c:if test="${ssBinderConfig.library}">checked="checked"</c:if> disabled="disabled"/>
<span class="ss_labelRight"><ssf:nlt tag="administration.configure_cfg.library"/></span>
</td></tr>
<tr><td>
<input type="checkbox" name="uniqueTitles" <c:if test="${ssBinderConfig.uniqueTitles}">checked="checked"</c:if> disabled="disabled"/>
<span class="ss_labelRight"><ssf:nlt tag="administration.configure_cfg.uniqueTitles"/></span>
</td></tr>
</c:if>
</table>

<div class="ss_formBreak"/>
<form method="post" action="<portlet:renderURL><portlet:param 
		name="action" value="configure_configuration"/>
		</portlet:renderURL>" >
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</form
</c:if>


<c:if test="${ssOperation == 'add_folder' or ssOperation == 'add_workspace'}">
<form method="post" action="<portlet:actionURL><portlet:param 
		name="action" value="configure_configuration"/>
		<portlet:param name="operation" value="${ssOperation}"/><portlet:param 
		name="binderId" value="${ssBinderConfig.id}"/></portlet:actionURL>" >

<h3><ssf:nlt tag="administration.configure_cfg.existing"/></h3>
  <c:forEach var="config" items="${ssBinderConfigs}">
      <input type="radio" name="binderConfigId" value="${config.id}" ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/><br/>
  </c:forEach>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>

</form>
</c:if>


</c:if>
</div>
</div>
</div>
</div>

