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
<% //div %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_mashupItemId" value="0" scope="request"/>
<%  
	Long ss_mashupTableNumber = Long.valueOf(0);
	Map ss_mashupTableItemCount = new HashMap(); 
	request.setAttribute("ss_mashupTableNumber", ss_mashupTableNumber);
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);
%>

<c:set var="ss_mashupPropertyName" value="${property_name}" scope="request"/>
<c:if test="${ssConfigJspStyle == 'form'}">
  <div style="padding: 20px 0px 20px 0px;">
    <div><span class="ss_bold">${property_caption}</span></div>
</c:if>
<div <c:if test="${ssConfigJspStyle == 'form'}"> style="border: 1px silver solid; padding: 30px;" </c:if> >
  <c:if test="${ssConfigJspStyle == 'form'}">
    <%@ include file="/WEB-INF/jsp/tag_jsps/mashup/add.jsp" %>
    <c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
    <c:set var="mashupValue" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
    <jsp:useBean id="mashupValue" type="java.lang.String" />
    <%
    	String[] mashupValues = mashupValue.split(";");
    	Map inputElements = new HashMap();
    %>
    <c:forEach var="mashupItem" items="<%= mashupValues %>">
      <c:if test="${!empty mashupItem}">
	      <jsp:useBean id="mashupItem" type="java.lang.String" />
	      <%
	    	  String[] mashupItemValues = mashupItem.split(",");
	    	  String type = mashupItemValues[0];
	    	  String value1 = "";
	    	  String value2 = "";
	    	  if (mashupItemValues.length >= 2) value1 = mashupItemValues[1];
	    	  if (mashupItemValues.length >= 3) value2 = mashupItemValues[2];
	      %>
	 	  <c:if test="${ssConfigJspStyle == 'form'}">
	  	    <%
	  	    	inputElements.put(request.getAttribute("ss_mashupItemId"), type + "," + value1 + "," + value2);
	  	    %>
	  	  </c:if>
	      <ssf:mashup id="${ss_mashupItemId}" type="<%= type %>" value1="<%= value1 %>" value2="<%= value2 %>" view="${ssConfigJspStyle}"/>
	      <c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
		  <% if (!type.equals("tableStart")) { %>
			  <c:if test="${ssConfigJspStyle == 'form'}">
	      		<c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
			    <%@ include file="/WEB-INF/jsp/tag_jsps/mashup/add.jsp" %>
	      		<c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
			  </c:if>
		  <% } %>
	    </c:if>
     </c:forEach>
     <%
     	Iterator itInputElements = inputElements.entrySet().iterator();
     	while (itInputElements.hasNext()) {
     		Map.Entry me = (Map.Entry) itInputElements.next();
     		%><input type="hidden" 
     		  name="${ss_mashupPropertyName}__<%= me.getKey().toString() %>" 
     		  value="<%= me.getValue().toString() %>"/>
     		<%
     	}
     %>
    
  </c:if>
  <input type="hidden" name="${ss_mashupPropertyName}__idCounter" value="${ss_mashupItemId}"/>
</div>
<c:if test="${ssConfigJspStyle == 'form'}">
  </div>
</c:if>
