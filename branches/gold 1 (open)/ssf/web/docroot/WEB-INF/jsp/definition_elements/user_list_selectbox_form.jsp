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
<% // The user list selectbox form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	request.setAttribute("selectboxName", elementName);
	String caption = (String) request.getAttribute("property_caption");
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
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>

<div class="ss_entryContent">
<div class="ss_labelLeft"><%= caption %><%= required %></div>
<select 
  name="<%= elementName %>" <%= multiple %> <%= size %>>
  
<c:if test="${!empty ssFolder.customAttributes[property_source].valueSet}">
<%
	String propertySource = (String) request.getAttribute("property_source");
%>
  <c:set var="folder" value="${ssFolder}"/>
  <jsp:useBean id="folder" type="com.sitescape.team.domain.DefinableEntity" />
	<c:forEach var="selection" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(folder.getCustomAttribute(propertySource)) %>" >
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
</div>
