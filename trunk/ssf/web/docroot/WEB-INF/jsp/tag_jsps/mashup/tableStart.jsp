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
<% //table start %>
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
%>
<% if (ss_mashupListDepth > 0) { %>
<li>
<% } %>

<c:set var="ss_mashupColStarted" value="false" scope="request"/>
<table class="ss_mashup 
  <c:if test="${!empty mashup_attributes['showBorder'] || ssConfigJspStyle == 'form'}"> ss_mashup_border </c:if>
">
<tr>
<c:set var="ss_mashupColStarted" value="false" scope="request"/>
