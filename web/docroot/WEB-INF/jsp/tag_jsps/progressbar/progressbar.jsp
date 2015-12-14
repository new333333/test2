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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	Integer currentValueInt = (Integer)request.getAttribute("currentValueInt"); 
	String currentValue = (String)request.getAttribute("currentValue"); 
	java.util.List valuesInt = (java.util.List)request.getAttribute("valuesInt"); 
	java.util.Map valuesMap = (java.util.Map)request.getAttribute("valuesMap");
	String namespace = (String)request.getAttribute("namespace"); 
	String entryId = (String)request.getAttribute("entryId"); 
	Boolean readOnly = (Boolean)request.getAttribute("readOnly"); 
%>
<c:set var="currentValueInt" value="<%= currentValueInt %>" />
<c:set var="currentValue" value="<%= currentValue %>" />
<c:set var="valuesInt" value="<%= valuesInt %>" />
<c:set var="valuesMap" value="<%= valuesMap %>" />
<c:set var="namespace" value="<%= namespace %>" />
<c:set var="entryId" value="<%= entryId %>" />
<c:set var="readOnly" value="<%= readOnly %>" />

<c:if test="${!readOnly}">
<div class="ss_completedContainer" id="ss_completedContainer_${namespace}_${entryId}" 
			onmouseout="myTasks_${namespace}.ss_changeValue({'id' : ${entryId}, this, document.getElementById('ss_completedContainer_status_${namespace}_${entryId}'), '${currentValue}');"
	>
	<c:forEach var="cv" items="<%= valuesInt %>">
		<c:set var="fullValue" value="c${cv}" />
		<div title="${valuesMap[fullValue]}" 
			<c:choose>
				<c:when test="${cv <= currentValueInt && currentValueInt != 0}">
					class="ss_bar_on"
				</c:when> 
				<c:otherwise>
					class="ss_bar_off"
				</c:otherwise> 
			</c:choose>
				onclick="myTasks_${namespace}.ss_saveValue({'id' : ${entryId}, document.getElementById('ss_completedContainer_${namespace}_${entryId}'), document.getElementById('ss_completedContainer_status_${namespace}_${entryId}'), '${fullValue}')"
				onmouseover="myTasks_${namespace}.ss_changeValue({'id' : ${entryId}, document.getElementById('ss_completedContainer_${namespace}_${entryId}'), document.getElementById('ss_completedContainer_status_${namespace}_${entryId}'), '${fullValue}');"
			></div>
	</c:forEach>
</div>
<div class="ss_bar_status" id="ss_completedContainer_status_${namespace}_${entryId}">${valuesMap[currentValue]}</div>
</c:if>
<c:if test="${readOnly}">
  <!-- <div style="height: 12px; width: ${currentValueInt}px; background-color: green;"><img src="<html:imagesPath/>pics/1pix.gif"/></div> -->
  ${currentValueInt}%
</c:if>

