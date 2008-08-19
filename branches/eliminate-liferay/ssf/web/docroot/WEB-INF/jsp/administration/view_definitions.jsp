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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<c:if test="${!empty ss_configErrorMessage}">
<div class="ss_labelLeftError">
<span><c:out value="${ss_configErrorMessage}"/></span>
</div>
<br/>
<br/>
</c:if>
<c:if test="${empty ssOperation}">
<c:set var="adminTreeName" value="${renderResponse.namespace}_adminDomTree"/>
<script type="text/javascript">
function ss_treeBreadCrumb${renderResponse.namespace}(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<ssf:url ><ssf:param 
		name="action" value="manage_definitions"/><ssf:param 
		name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}


function ${adminTreeName}_showId(id, obj, action, namespace) {
	//Build a url to go to
	var params = {binderId:"${ssBinderId}", sourceDefinitionId:id};

	self.location.href = ss_buildAdapterUrl(ss_AjaxBaseUrl, params, "definition_builder");
	return false;
}

</script>


<c:if test="${empty ssBinderId}">
<span class="ss_titlebold"><ssf:nlt tag="administration.definition.public.existing" /></span>
</c:if>
<c:if test="${!empty ssBinderId}">
<span class="ss_titlebold"><ssf:nlt tag="administration.definition.binder.existing"><ssf:param 
name="value" value="${ssBinder.title}"/></ssf:nlt></span>
</c:if>
<br/>
<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeBreadCrumb${renderResponse.namespace}" 
  scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<br/>
<ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar2 ss_actions_bar" />
<br/>

		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div>
						<ssf:ifnotaccessible>
						  <ssf:tree treeName="${adminTreeName}" 
						    treeDocument="${ssAdminDomTree}" 
						    callbackUrl="${callbackUrl}"
		 					 showIdRoutine="${adminTreeName}_showId"
						    rootOpen="true" />
						</ssf:ifnotaccessible>
						<ssf:ifaccessible>
						<ssf:tree treeName="${adminTreeName}" 
						  treeDocument="${ssAdminDomTree}" 
						  flat="true"
						  callbackUrl="${callbackUrl}"
		 				  showIdRoutine="${adminTreeName}_showId"
						  rootOpen="true" />
						</ssf:ifaccessible>
					</div>
				</td>
			</tr>
			</table>


<br/>

<form class="ss_style ss_form" >

	<input type="submit" class="ss_submit" name="closeBtn" onClick="self.window.close();return false;" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>

</c:if>


<c:if test="${ssOperation == 'add'}">

<table class="ss_style" width="100%"><tr><td>
<span class="ss_titlebold"><ssf:nlt tag="administratinn.add.definition"/></span>
<form class="ss_style ss_form" action="<ssf:url action="definition_builder" actionUrl="true"><ssf:param 
	name="operation" value="addDefinition"/><ssf:param 
	name="binderId" value="${ssBinderId}"/><ssf:param 
	name="definitionType_" value="${definitionType}"/></ssf:url>"
	method="post" name="${renderResponse.namespace}addfm">

<br>
<c:if test="${!empty ssBinderId}">
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="${ssConfigDefinition}"
		  configDocument="${ssConfigDefinition}"
		  option="properties" 
		  itemName="${itemName}"
		  owningBinderId="${ssBinderId}" 
		/>
</c:if>
<c:if test="${empty ssBinderId}">
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="${ssConfigDefinition}"
		  configDocument="${ssConfigDefinition}"
		  option="properties" 
		  itemName="${itemName}"
		/>
</c:if>
<br>		
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
<br>
</td></tr></table>

</c:if>
<c:if test="${ssOperation == 'copy'}">

<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDomTree}">
<span class="ss_titlebold"><ssf:nlt tag="administration.copy.definition" /></span>
<script type="text/javascript">
function ${renderResponse.namespace}_showId(forum, obj) {
	return ss_checkTree(obj, "ss_tree_radio${renderResponse.namespace}sourceDefinitionId" + forum);
}
</script>
<form class="ss_style ss_form" action="<ssf:url action="manage_definitions" actionUrl="true"><ssf:param 
	name="operation" value="copy"/><ssf:param 
	name="binderId" value="${ssBinderId}"/></ssf:url>"
	method="post" name="${renderResponse.namespace}copyfm">

<br>
<ssf:tree treeName="${renderResponse.namespace}"  treeDocument="${ssDomTree}" 
 	 rootOpen="true"
	 singleSelectName="sourceDefinitionId" />
<br>	 
	 <input type="submit" class="ss_submit" name="copyBtn" value="<ssf:nlt tag="button.ok" />">
&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</c:if>
<c:if test="${empty ssDomTree}">
<span class="ss_titlebold"><ssf:nlt tag="administration.copy.definition.rename" /></span>
<form class="ss_style ss_form" action="<ssf:url action="definition_builder" actionUrl="true"><ssf:param 
	name="operation" value="copyDefinition"/><ssf:param 
	name="binderId" value="${ssBinderId}"/><ssf:param 
	name="sourceDefinitionId" value="${ssDefinition.id}"/></ssf:url>"
	method="post" name="${renderResponse.namespace}copyfm">

<br>
		<span><ssf:nlt tag="definition.name"/></span><br/>
		<input type="text" class="ss_text" name="propertyId_name" size="40" value="<c:out value="${ssDefinition.name}-2" escapeXml="true"/>"/><br/>
		<span><ssf:nlt tag="definition.caption"/></span><br/>
		<input type="text" class="ss_text" name="propertyId_caption" size="40" value="<c:out value="${ssDefinition.title}-2" escapeXml="true"/>"/><br/><br/>
<br>	 
	 <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</c:if>
</td></tr></table>

</c:if>
</div>
</div>
</body>
</html>
