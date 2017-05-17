<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<% //Add/modify a folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String enctype = "application/x-www-form-urlencoded";
	if (item.selectSingleNode(".//item[@name='file']") != null || 
			item.selectSingleNode(".//item[@name='graphic']") != null || 
			item.selectSingleNode(".//item[@name='attachFiles']") != null) {
		enctype = "multipart/form-data";
	}
	String formName = (String) request.getAttribute("property_name");
	if (formName == null || formName.equals("")) {
		formName = WebKeys.DEFINITION_DEFAULT_FORM_NAME;
	}
	request.setAttribute("formName", formName);
	String methodName = (String) request.getAttribute("property_method");
	if (methodName == null || methodName.equals("")) {
		methodName = "post";
	}
%>
<c:set var="ss_form_form_formName" value="<%= formName %>" scope="request"/>
<div class="ss_style ss_portlet" width="100%">
  <ssf:form title="${ssBinder.title}">
	<form class="ss_form" method="<%= methodName %>" 
  			enctype="<%= enctype %>" name="<%= formName %>" 
  			id="<%= formName %>" onSubmit="return ss_onSubmit(this, true);">

		<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  			configElement="<%= item %>" 
  			configJspStyle="${ssConfigJspStyle}"
  			entry="${ssDefinitionEntry}" />

		<br/>
		<c:set var="cb_checked" value=""/>
		<c:if test="${ssDefinitionEntry.library}" >
			<c:set var="cb_checked" value=" checked "/>
		</c:if>

		<div style="display:block">
			<input type="checkbox" name="ss_library"
				<c:if test="${ssDefinitionEntry.mirrored}"> disabled </c:if>
				<c:out value="${cb_checked}"/>
				onClick="if (document.${formName}.ss_library.checked) document.${formName}.library.value='true'; else document.${formName}.library.value='false';">
			&nbsp;
			<span class="ss_labelRight"><ssf:nlt tag="folder.isLibrary"/></span>
			<ssf:showHelp guideName="adv_user" pageId="mngfldrs_webdav" sectionId="mngfldrs_webdav_configure" />
		</div>
		<input type="hidden" name="library" value="${ssDefinitionEntry.library}"/>
		<br/>

		<c:set var="cb_checked" value=""/>
		<c:if test="${ssDefinitionEntry.uniqueTitles}" >
			<c:set var="cb_checked" value=" checked "/>
		</c:if>
		<div style="display:block">
			<input type="checkbox" name="ss_unique" 
			<c:if test="${ssDefinitionEntry.mirrored || 
			  (!ssBinderIsEmpty && ssDefinitionEntry.library && ssDefinitionEntry.uniqueTitles)}"> disabled </c:if>
			<c:out value="${cb_checked}"/> onClick="if (document.${formName}.ss_unique.checked) document.${formName}.uniqueTitles.value='true'; else document.${formName}.uniqueTitles.value='false';">
			&nbsp;
			<span class="ss_labelRight"><ssf:nlt tag="folder.isUniqueTitles"/></span></input>
		</div>
		<input type="hidden" name="uniqueTitles" value="${ssDefinitionEntry.uniqueTitles}"/>
		<br/>

		<ssf:ifAuthorizedByLicense featureName="com.novell.teaming.module.folder.MirroredFolder">
			<c:if test="${ssDefinitionEntry.mirrored}">
				<c:set var="resourceDrivers" value="<%= org.kablink.teaming.fi.connection.ResourceDriverManagerUtil.getAllowedResourceDrivers() %>"/>
				<input type="hidden" name="mirrored" value="${ssDefinitionEntry.mirrored}"/>
				<br/>
	
				<c:if test="${!empty ssDefinitionEntry.resourceDriver}" >
					<span class="ss_labelLeft"><ssf:nlt tag="folder.resource.driver.label"/></span>
					<span>${ssDefinitionEntry.resourceDriver.titleAndMode}</span>
					<c:set var="resourceRootPath" value="${ssDefinitionEntry.resourceDriver.rootPath}"/>
				</c:if>

				<c:if test="${ssBinderIsEmpty && empty ssDefinitionEntry.resourceDriver && !empty resourceDrivers}" >
					<span class="ss_errorLabel"><ssf:nlt tag="folder.resource.driver.select"/></span>
					<br>
					<br>
					<span class="ss_labelLeft"><ssf:nlt tag="folder.resource.driver.label"/></span>
					<c:set var="resourceRootPath" value="${resourceDrivers[0].rootPath}"/>
					<select name="resourceDriverName" onchange="updateResourceRootPath();">
					    <option value="">---</option>option
						<c:forEach var="driver" items="${resourceDrivers}">
							<option value="${driver.name}" id="${driver.rootPath}" <c:if test="${driver.name == ssDefinitionEntry.resourceDriverName}">selected</c:if>>${driver.titleAndMode}</option>
							<c:if test="${driver.name == ssDefinitionEntry.resourceDriverName}"><c:set var="resourceRootPath" value="${driver.rootPath}"/></c:if>
						</c:forEach>
					</select>
				</c:if>				
				<c:if test="${ssDefinitionEntry.mirrored && empty ssDefinitionEntry.resourceDriver && empty resourceDrivers}" >
					<span class="ss_errorLabel"><ssf:nlt tag="folder.resource.driver.configuration.missing"/></span>
				</c:if>				
				<br/>
	
				<c:if test="${ssBinderIsEmpty && !empty resourceDrivers}" >
				  <span class="ss_labelLeft"><ssf:nlt tag="folder.resource.rootpath.label"/></span>
				  <c:if test="${!empty ssDefinitionEntry.resourceDriver}">
				    <span>${resourceRootPath}</span><br/>
				  </c:if>
				  <c:if test="${empty ssDefinitionEntry.resourceDriver}">
				    <input type="text" class="ss_text" size="110" name="rootPath" value="${resourceRootPath}"/><br/>
				  </c:if>
				  <span class="ss_labelLeft"><ssf:nlt tag="folder.resource.path.label"/></span><br>
				  <input type="text" class="ss_text" size="110" name="resourcePath" value="${ssDefinitionEntry.resourcePath}"/><br/>
				</c:if>
				<br/>
			</c:if>
		</ssf:ifAuthorizedByLicense>

		<div class="ss_buttonBarRight">
			<input type="submit" class="ss_submit" name="okBtn" 
  				value="<ssf:nlt tag="button.ok" text="  OK  "/>"  onClick="ss_buttonSelect('okBtn');">
			<input type="submit" class="ss_submit" name="cancelBtn" 
  				value="<ssf:nlt tag="button.cancel" text="Cancel"/>"  onClick="ss_buttonSelect('cancelBtn');">
		</div>
		<sec:csrfInput />
	</form>
  </ssf:form>
</div>

<script type="text/javascript">
function updateResourceRootPath() {
	var selectObjs = document.getElementsByName("resourceDriverName");
	var rootPathObjs = document.getElementsByName("rootPath");
	rootPathObjs[0].value = selectObjs[0].options[selectObjs[0].selectedIndex].id;
}
</script>
