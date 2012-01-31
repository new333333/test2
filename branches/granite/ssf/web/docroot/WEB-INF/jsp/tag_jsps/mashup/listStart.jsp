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
<% //list start %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	Map ss_mashupTableItemCount2 = (Map) request.getAttribute("ss_mashupTableItemCount2");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "table");  
	ss_mashupTableDepth = ss_mashupTableDepth + 1;
	ss_mashupTableNumber = ss_mashupTableNumber + 1;
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "");  
	ss_mashupTableItemCount2.put(ss_mashupTableDepth, ss_mashupTableNumber);  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);
	request.setAttribute("ss_mashupTableItemCount2", ss_mashupTableItemCount2);
	request.setAttribute("ss_mashupTableDepth", ss_mashupTableDepth);
	request.setAttribute("ss_mashupTableNumber", ss_mashupTableNumber);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
	ss_mashupListDepth = ss_mashupListDepth + 1;
	request.setAttribute("ss_mashupListDepth", ss_mashupListDepth);
%>
<c:set var="mWidth" value="100%" />
<c:set var="mHeight" value="100%" />
<c:set var="mOverflow" value="hidden" />
<c:if test="${!empty mashup_attributes['width']}">
  <c:set var="mWidth" value="${mashup_attributes['width']}" />
</c:if>
<c:if test="${!empty mashup_attributes['height']}">
  <c:set var="mHeight" value="${mashup_attributes['height']}" />
</c:if>
<c:if test="${!empty mashup_attributes['overflow']}">
  <c:set var="mOverflow" value="${mashup_attributes['overflow']}" />
</c:if>
<% if (ss_mashupListDepth > 1) { %>
<li>
<% } %>

  <div class="ss_mashup_element" 
    <c:if test="${ssConfigJspStyle != 'form'}">
      style="width: ${mWidth}; overflow: hidden;"
    </c:if>
    <c:if test="${!empty mashup_attributes['showBorder']}"></c:if>
  >
    <div class="ss_mashup_round_top"><div></div></div>
	<c:if test="${ssConfigJspStyle == 'form' || !empty mashup_attributes['title']}">
	  <c:set var="divClass" value="ss_mashup_list_header_form" />
	  <c:if test="${ssConfigJspStyle != 'form'}">
		<c:set var="divClass" value="ss_mashup_list_header_view" />
	  </c:if>
	  <div class="${divClass}">
		<c:if test="${ssConfigJspStyle == 'form'}">
		  <span class="ss_bold"><ssf:nlt tag="mashup.type.list"/>: </span>
		</c:if>
		<c:set var="spanClass" value="" />
		<c:if test="${ssConfigJspStyle != 'form'}">
		  <c:set var="spanClass" value="ss_largeprint ss_bold" />
		</c:if>
		<span>${mashup_attributes['title']}</span>
	  </div>
	</c:if>
  
	<div class="ss_mashup_list_content" style="height: ${mHeight}; overflow: ${mOverflow};">
	<ul>
