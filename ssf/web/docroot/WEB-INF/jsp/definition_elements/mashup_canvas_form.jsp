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
<% //div %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_mashupItemId" value="0" scope="request"/>
<%  
	Long ss_mashupTableDepth = Long.valueOf(0);
	Long ss_mashupTableNumber = Long.valueOf(0);
	Map ss_mashupTableItemCount = new HashMap(); 
	Map ss_mashupTableItemCount2 = new HashMap(); 
	ss_mashupTableItemCount.put(ss_mashupTableDepth, "");
	ss_mashupTableItemCount2.put(ss_mashupTableDepth, ss_mashupTableNumber);
	request.setAttribute("ss_mashupTableDepth", ss_mashupTableDepth);
	request.setAttribute("ss_mashupTableNumber", ss_mashupTableNumber);
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);
	request.setAttribute("ss_mashupTableItemCount2", ss_mashupTableItemCount2);

	Long ss_mashupListDepth = Long.valueOf(0);
	request.setAttribute("ss_mashupListDepth", ss_mashupListDepth);
%>
<c:set var="ss_mashupPropertyName" value="${property_name}" scope="request"/>
<c:if test="${ssConfigJspStyle == 'form'}">
<script type="text/javascript">
function ss_mashup_deleteAll_${renderResponse.namespace}() {
	if (confirm("<ssf:nlt tag="mashup.deleteEverythingConfirm"/>")) {
		var obj = self.document.getElementById("${ss_mashupPropertyName}__deleteEverything");
		obj.value = "true";
		return true;
	} else {
		return false;
	}
}
</script>
  <div style="padding: 20px 0px 20px 0px;">
    <div><span class="ss_bold">${property_caption}</span></div>
    <div style="padding:6px 0px 6px 20px;">
      <input type="checkbox" name="${ss_mashupPropertyName}__hideMasthead"
      	id="${ss_mashupPropertyName}__hideMasthead"
        <c:if test="${ss_mashupHideMasthead}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__hideMasthead">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.hideMasthead"/></span>
      </label>
      <br/>
      <input type="checkbox" name="${ss_mashupPropertyName}__hideSidebar"
      	id="${ss_mashupPropertyName}__hideSidebar"
        <c:if test="${ss_mashupHideSidebar}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__hideSidebar">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.hideSidebar"/></span>
      </label>
      <br/>
      <input type="checkbox" name="${ss_mashupPropertyName}__hideToolbar"
      	id="${ss_mashupPropertyName}__hideToolbar"
        <c:if test="${ss_mashupHideToolbar}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__hideToolbar">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.hideToolbar"/></span>
      </label>
      <br/>
      <input type="checkbox" name="${ss_mashupPropertyName}__hideFooter"
      	id="${ss_mashupPropertyName}__hideFooter"
        <c:if test="${ss_mashupHideFooter}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__hideFooter">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.hideFooter"/></span>
      </label>
      <br/>
      <input type="checkbox" name="${ss_mashupPropertyName}__showBranding"
      	id="${ss_mashupPropertyName}__showBranding"
        <c:if test="${ss_mashupShowBranding}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__showBranding">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.showBranding"/></span>
      </label>
      <br/>
      <br/>
      <span class="ss_labelAbove"><ssf:nlt tag="mashup.style"/></span>
      <input type="radio" name="${ss_mashupPropertyName}__style" value="mashup.css"
      	id="${ss_mashupPropertyName}__style"
        <c:if test="${ss_mashupStyle == 'mashup.css'}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__style">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.style_light"/></span>
      </label>
      <input type="radio" name="${ss_mashupPropertyName}__style" value="mashup_dark.css"
      	id="${ss_mashupPropertyName}__style_dark"
        <c:if test="${ss_mashupStyle == 'mashup_dark.css'}">checked</c:if> >
      <label for="${ss_mashupPropertyName}__style_dark">
      	<span class="ss_labelRight"><ssf:nlt tag="mashup.style_dark"/></span>
      </label>
      <br/>
    </div>
</c:if>
<div 
  <c:if test="${ssConfigJspStyle == 'form'}"> class="ss_mashup_canvas_form" </c:if>
  <c:if test="${ssConfigJspStyle != 'form'}"> class="ss_mashup_canvas_view" </c:if>
>
  <c:if test="${ssConfigJspStyle == 'form'}">
    <jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/add.jsp" />
    <c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
    <c:set var="mashupValue" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
    <jsp:useBean id="mashupValue" type="java.lang.String" />
    <%
    	if (mashupValue == null) mashupValue = "";
    	String[] mashupValues = mashupValue.split(";");
    	Map inputElements = new HashMap();
    %>
    <% if (mashupValues != null && mashupValues.length > 0) { %>
    <c:forEach var="mashupItem" items="<%= mashupValues %>">
      <c:if test="${!empty mashupItem}">
	      <jsp:useBean id="mashupItem" type="java.lang.String" />
	      <%
	    	  String[] mashupItemValues = mashupItem.split(",");
	    	  String type = "";
	    	  if (mashupItemValues.length > 0) type = mashupItemValues[0];
	      %>
	 	  <c:if test="${ssConfigJspStyle == 'form'}">
	  	    <%
	  	    	inputElements.put(request.getAttribute("ss_mashupItemId"), mashupItem);
	  	    %>
	  	  </c:if>
	      <ssf:mashup id="${ss_mashupItemId}" value="${mashupItem}" view="${ssConfigJspStyle}"/>
	      <c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
		  <% if (!type.equals("") && !type.equals("tableStart")) { %>
			  <c:if test="${ssConfigJspStyle == 'form'}">
	      		<c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
				<jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/add.jsp" />
	      		<c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
			  </c:if>
		  <% } %>
	    </c:if>
     </c:forEach>
     <% } %>
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
  <c:if test="${ssConfigJspStyle == 'form'}">
   <input type="hidden" name="${ss_mashupPropertyName}__deleteEverything" id="${ss_mashupPropertyName}__deleteEverything" />
   <br/>
   <br/>
   <input type="submit" value="<ssf:nlt tag="mashup.deleteEverything"/>" name="applyBtn" 
    class="ss_linkButton ss_fineprint"
	onClick="return ss_mashup_deleteAll_${renderResponse.namespace}();" />
  </c:if>
</div>
<c:if test="${ssConfigJspStyle == 'form'}">
  </div>
  <div id="ss_mashupDataValue" style="display:none;">
  ${ssDefinitionEntry.customAttributes[property_name].value}
  </div>
</c:if>
