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
<% //Mashup iFrame view %>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "iframe");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<% if (ss_mashupListDepth > 0) { %>
	<li>
<% } %>

<c:if test="${ssConfigJspStyle != 'form'}">
	<div class="ss_mashup_url_content">
		<c:set var="url" value="" />
		<c:if test="${!empty mashup_attributes['url']}">
			<c:set var="url" value="${mashup_attributes['url']}" />
		</c:if>
		
		<c:set var="frameBorder" value="0" />
		<c:if test="${!empty mashup_attributes['frameBorder']}">
			<c:set var="frameBorder" value="${mashup_attributes['frameBorder']}" />
		</c:if>
		
		<c:set var="height" value="200" />
		<c:if test="${!empty mashup_attributes['height']}">
			<c:set var="height" value="${mashup_attributes['height']}" />
		</c:if>
		
		<c:set var="name" value="" />
		<c:if test="${!empty mashup_attributes['name']}">
			<c:set var="name" value="${mashup_attributes['name']}" />
		</c:if>
		
		<c:set var="scrolling" value="" />
		<c:if test="${!empty mashup_attributes['scrolling']}">
			<c:set var="scrolling" value="scrolling='${mashup_attributes['scrolling']}'" />
		</c:if>
		
		<c:set var="width" value="400" />
		<c:if test="${!empty mashup_attributes['width']}">
			<c:set var="width" value="${mashup_attributes['width']}" />
		</c:if>
		
		<iframe src="${url}" 
				frameborder="${frameBorder}"
				height="${height}"
				name="${name}"
				id="lpIFrame"
				${scrolling}
				width="${width}">
		</iframe>
	</div>
</c:if>

<% if (ss_mashupListDepth > 0) { %>
	</li>
<% } %>
