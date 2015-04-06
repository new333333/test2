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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="org.kablink.teaming.domain.FileAttachment" %>
<%@ page import="org.kablink.teaming.domain.FileItem" %>

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

<script type="text/javascript">

<c:if test="${!empty ss_mashupBGColor}">
  document.body.style.backgroundColor = '${ss_mashupBGColor}';
</c:if>
<c:if test="${!empty ss_mashupBGImg}">
  document.body.style.backgroundImage = "url( '${ss_mashupBGImg}' )";
</c:if>
<c:if test="${!empty ss_mashupBGImgRepeat}">
  document.body.style.backgroundRepeat = '${ss_mashupBGImgRepeat}';
</c:if>

</script>

<div 
<c:if test="${empty ss_mashupBGColor}">class="ss_mashup_canvas_view"</c:if>
>
  <c:if test="${ssConfigJspStyle != 'mobile'}">
	<div id="ss_mashup_canvas_print" style="position: absolute; right: 7px; top: 7px;">
		&nbsp;
		<a style="background: transparent !important;" class="ss_actions_bar13_pane_none" href="javascript: window.print();">
			<img border="0" 
	      		 alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
	      		 src="<html:rootPath/>images/pics/masthead/masthead_printer.png" border="0" align="absmiddle" />
		</a>
	</div>
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
	      <ssf:mashup entity="${ss_mashupBinder}" id="${ss_mashupItemId}" value="${mashupItem}" view="${ssConfigJspStyle}"/>
	      <c:set var="ss_mashupItemId" value="${ss_mashupItemId + 1}" scope="request"/>
	    </c:if>
     </c:forEach>
     <% } %>
    
  </c:if>
</div>
