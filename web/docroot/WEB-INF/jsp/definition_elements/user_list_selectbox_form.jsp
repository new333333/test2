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
<% // The user list selectbox form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	request.setAttribute("selectboxName", elementName);
	String caption = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption});
	String multiple = (String) request.getAttribute("property_multipleAllowed");
	if (multiple != null && multiple.equals("true")) {
		multiple = "multiple";
	} else {
		multiple = "";
	}
	String size = (String)request.getAttribute("property_size");
	if (size == null || size.equals("")) {
		size = "";
	} else {
		size = "size='" + size + "'";
	}
	if (caption != null && !caption.equals("")) {
		caption += "<br>\n";
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span id=\"ss_required_"+elementName+"\" title=\""+caption2+"\" class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>

<div class="ss_entryContent ${ss_fieldModifyStyle}">
<div class="ss_labelLeft"><%= caption %><%= required %></div>
<c:if test="${empty ssReadOnlyFields[property_name]}">
<select ${ss_fieldModifyInputAttribute}
  name="<%= elementName %>" <%= multiple %> <%= size %>>
  
<c:if test="${!empty ssBinder.customAttributes[property_source].valueSet}">
<%
	String propertySource = (String) request.getAttribute("property_source");
%>
  <c:set var="binder" value="${ssBinder}"/>
  <jsp:useBean id="binder" type="org.kablink.teaming.domain.DefinableEntity" />
	<c:forEach var="selection" items='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(binder.getCustomAttribute(propertySource)) %>' >
	  <c:set var="selected" value=""/>
	  <c:forEach var="selection2" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}" >
  		<c:if test="${selection2 == selection.id}">
	      <c:set var="selected" value="selected"/>
	    </c:if>
	  </c:forEach>
	  <option value="${selection.id}" ${selected} >${selection.title}</option>
	</c:forEach>
</c:if>
</select>
</c:if>
<c:if test="${!empty ssReadOnlyFields[property_name]}">
<c:if test="${!empty ssBinder.customAttributes[property_source].valueSet}">

  <jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.DefinableEntity" />
	<c:forEach var="selection" items='<%= org.kablink.teaming.util.ResolveIds.getPrincipals(ssBinder.getCustomAttribute((String) request.getAttribute("property_source"))) %>' >
	  <c:set var="selected" value=""/>
	  <c:forEach var="selection2" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}" >
  		<c:if test="${selection2 == selection.id}">
	      ${selection.title}<br/>
	    </c:if>
	  </c:forEach>
	</c:forEach>
	&nbsp;
</c:if>
</c:if>
</div>
