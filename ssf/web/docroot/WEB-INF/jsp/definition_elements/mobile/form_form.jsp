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
<% // Form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	//Get the name of the definition
	Document defDoc = (Document) request.getAttribute("ssConfigDefinition");
	String defName = (String) defDoc.getRootElement().attributeValue("caption");
	String enctype = "application/x-www-form-urlencoded";
	String formName = (String) request.getAttribute("property_name");
	if (formName == null || formName.equals("")) {
		formName = WebKeys.DEFINITION_DEFAULT_FORM_NAME;
	}
	request.setAttribute("formName", formName);
	String methodName = (String) request.getAttribute("property_method");
	if (methodName == null || methodName.equals("")) {
		methodName = "post";
	}

	//Get the entry type of this definition (folder, file, or event)
	String formViewStyle = "form";
	Element formViewTypeEle = (Element)item.selectSingleNode("properties/property[@name='type']");
	if (formViewTypeEle != null) formViewStyle = formViewTypeEle.attributeValue("value", "form");

%>
<ssf:form title="<%= defName %>">
<c:set var="ss_form_form_formName" value="<%= formName %>" scope="request"/>
<c:set var="ss_formViewStyle" value="<%= formViewStyle %>" scope="request" />
<jsp:useBean id="ss_formViewStyle" type="String" scope="request" />

<c:choose>
  <c:when test="${ss_formViewStyle == 'guestbook'}">
		<jsp:include page="/WEB-INF/jsp/definition_elements/guestbook/guestbook_form.jsp" />
  </c:when>

  <c:when test="${ss_formViewStyle == 'survey'}">
		<jsp:include page="/WEB-INF/jsp/definition_elements/survey/survey_form.jsp" />
  </c:when>

  <c:when test="${ss_formViewStyle == 'profile'}">
		<jsp:include page="/WEB-INF/jsp/definition_elements/profile_entry_form.jsp" />
  </c:when>

  <c:otherwise>
	<form style="background: transparent;" 
	  method="<%= methodName %>" 
	  enctype="<%= enctype %>" 
	  name="<%= formName %>" 
	  id="<%= formName %>" 
	  action="">
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="<%= item %>" 
	  configJspStyle="${ssConfigJspStyle}" />
	</form>  
  </c:otherwise>  
  
</c:choose>
</ssf:form>
