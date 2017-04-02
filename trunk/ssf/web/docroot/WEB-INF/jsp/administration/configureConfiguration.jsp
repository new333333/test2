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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_configurations") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
		ss_cancelButtonCloseWindow();
			return true;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.configure_configurations" >
<c:if test="${!empty ssErrorList}">
<br/>
<span class="ss_bold ss_largerprint ss_errorLabel"><ssf:nlt tag="administration.errors"/></span>
<ul>
<c:forEach var="err" items="${ssErrorList}">
	<li>${err}</li>
</c:forEach>
</ul>
</c:if>
<%
String cTreeName = renderResponse.getNamespace() + "_cTree";
%>
<c:if test="${empty ssBinderConfig}">
<c:if test="${ssOperation != 'export'}">

<script type="text/javascript">

function <%=cTreeName%>_showId(id, obj, action) {
	//Build a url to go to
	var params = {binderId:id};
	self.location.href = ss_buildAdapterUrl(ss_AjaxBaseUrl, params, action);
	return false;
} 
</script>

<br/>
<ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar5 ss_actions_bar" />
<br/>

<c:if test="${!ssConfigureConfigurationLocal}">
<div style="padding-top: 10px; padding-bottom: 2px;">
  <span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.standardTemplates"/></span>
</div>
<ul class="ss_square">
<c:forEach var="bconfig" items="${ssBinderConfigs}">
<jsp:useBean id="bconfig" type="org.kablink.teaming.domain.TemplateBinder"/>
	<c:if test="${bconfig.reserved}">
	<li>
	  <a class="ss_bold ss_style ss_title_link" href="javascript:" 
	    onClick="return <%=cTreeName%>_showId('${bconfig.id}', this, 'configure_configuration');">
	    <ssf:nlt tag="${bconfig.templateTitle}" checkIfTag="true"/></a>
		<c:if test="${!empty bconfig.templateDescription.text}">
		- <c:out value='<%= NLT.getDef(bconfig.getTemplateDescription().getText()) %>' escapeXml="false" />
		</c:if>
		</li>
	</c:if>

</c:forEach>
</ul>
</c:if>

<c:if test="${!ssConfigureConfigurationLocal}">
<div style="padding-top: 10px; padding-bottom: 2px;">
  <span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.customTemplates"/></span>
</div>
</c:if>
<c:if test="${ssConfigureConfigurationLocal}">
<div style="padding-top: 10px; padding-bottom: 2px;">
  <span class="ss_labelLeft"><ssf:nlt tag="administration.configure_cfg.localTemplates"/></span>
</div>
</c:if>
<ul class="ss_square">
<c:forEach var="cconfig" items="${ssBinderConfigs}">
<jsp:useBean id="cconfig" type="org.kablink.teaming.domain.TemplateBinder"/>
	<c:if test="${!cconfig.reserved}">
	<li>
	  <a class="ss_bold ss_style ss_title_link" 
	    href="javascript:" 
	    onClick="return <%=cTreeName%>_showId('${cconfig.id}', this, 'configure_configuration');">
	<ssf:nlt tag="${cconfig.templateTitle}" checkIfTag="true"/></a>
		<c:if test="${!empty cconfig.templateDescription.text}">
		- <c:out value='<%= NLT.getDef(cconfig.getTemplateDescription().getText()) %>' escapeXml="false" />
		</c:if>
	</li>
	</c:if>
</c:forEach>
</ul>
<br/>

<form class="ss_style ss_form" >

	<input type="submit" class="ss_submit" name="closeBtn" onClick="return handleCloseBtn();" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>

</c:if>

<c:if test="${ssOperation == 'export'}">
<span class="ss_titlebold"><ssf:nlt tag="administration.export.templates.select"/></span>
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" action="<ssf:url webPath="templateDownload"/>" 
	method="post" name="${renderResponse.namespace}fm">

<%@include file="/WEB-INF/jsp/administration/commonSelectTree.jsp" %>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
		  onClick='self.location.href="<ssf:url action="configure_configuration" 
			actionUrl="false"><ssf:param 
			name="binderParentId" value="${binderParentId}"/></ssf:url>";return false;'/>
<script type="text/javascript">
document.${renderResponse.namespace}fm.onsubmit=function() { return ss_selectAllIfNoneSelected.call(this,"id_");};
</script>
	<sec:csrfInput />
</form>
<br>
</td></tr></table>
</c:if>

</c:if>

<c:if test="${!empty ssBinderConfig}">

<c:if test="${empty ssOperation || ssOperation == ''}">
<%@ page import="org.kablink.teaming.module.definition.DefinitionUtils" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = DefinitionUtils.getViewType(ssConfigDefinition);
if (folderViewStyle == null || folderViewStyle.equals("")) folderViewStyle = "folder";
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
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
var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;

function <%=cTreeName%>_showId(id, obj, action) {
	//Build a url to go to
	var url = "<ssf:url action="ssActionPlaceHolder" actionUrl="true"><ssf:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
		name="parentBinderId" value="ssParentBinderIdPlaceHolder"/></ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssParentBinderIdPlaceHolder", "${ssBinder.templateOwningBinderId}");
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
} 
function ss_confirmDeleteConfig(obj) {
	if (confirm('<ssf:nlt tag="administration.configure_cfg.confirmDelete"/>')) {
		var actionUrl = obj.href;
		var formObj = document.getElementById('deleteForm');
		formObj.action = actionUrl;
		formObj.submit();
		return false
	} else {
		return false
	}
}
</script>

<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">

<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="ss_view_sidebar">
		<% // BEGIN SIDEBAR LAYOUT  %>
		<div id="ss_sidebarDiv${renderResponse.namespace}" style="display:${ss_sidebarVisibility};">
			<div id="ss_sideNav_wrap"> <% // new sidebar format %>
				<% // Folder Tools %>
				<jsp:include page="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" />
			</div> <% // end of new sidebar format %>
		</div> <% // end of ss_sidebarDiv %>
	</td>
	<td valign="top" class="ss_view_info">

	<% // Folder toolbar %>
	<div class="ss_content_inner">
	<ssf:toolbar toolbar="${ssForumToolbar}" style="ss_actions_bar5 ss_actions_bar"/>
	</div>
	
	<div class="ss_content_inner">
	<% // Navigation links %>
	<c:if test="${!empty ssNavigationLinkTree}">
	<c:set var="ss_breadcrumbsShowIdRoutine" value='<%= cTreeName + "_showId"%>' scope="request" />
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
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>
<div class="ss_formBreak" align="left">
<form method="post" action="<ssf:url><ssf:param 
		name="action" value="configure_configuration"/><ssf:param 
		name="binderParentId" value="${binderParentId}"/></ssf:url>" >
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onclick="return handleCloseBtn();">
</div>
	<sec:csrfInput />
</form>
</div>
</c:if>

<c:if test="${ssOperation == 'add_folder' or ssOperation == 'add_workspace'}">

<form method="post" action="<ssf:url action="configure_configuration" actionUrl="true"><ssf:param 
		name="operation" value="${ssOperation}"/><ssf:param 
		name="binderId" value="${ssBinderConfig.id}"/><ssf:param 
		name="binderParentId" value="${binderParentId}"/></ssf:url>" >

<h3><ssf:nlt tag="administration.configure_cfg.existing"/></h3>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <input type="radio" name="binderConfigId" value="${config.id}" <c:if test="${status.first}">checked="checked"</c:if>><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/><br/>
  </c:forEach>

<div class="ss_formBreak"></div>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.add"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</div>

	<sec:csrfInput />
</form>

</c:if>


</c:if>
</ssf:form>

<div style="visibility: hidden;">
  <form id="deleteForm" method="post" >
		<sec:csrfInput />
  </form>
</div>

</div>
</div>
</body>
</html>

