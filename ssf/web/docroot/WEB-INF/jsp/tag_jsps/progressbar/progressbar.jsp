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

<div class="ss_completedContainer" id="ss_completedContainer_${namespace}_${entryId}" 
		<c:if test="${!readOnly}">
			onmouseout="myTasks_${namespace}.ss_changeValue({'id' : ${entryId}, this, document.getElementById('ss_completedContainer_status_${namespace}_${entryId}'), '${currentValue}');"
		</c:if>
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
			<c:if test="${!readOnly}">
				onclick="myTasks_${namespace}.ss_saveValue({'id' : ${entryId}, document.getElementById('ss_completedContainer_${namespace}_${entryId}'), document.getElementById('ss_completedContainer_status_${namespace}_${entryId}'), '${fullValue}')"
				onmouseover="myTasks_${namespace}.ss_changeValue({'id' : ${entryId}, document.getElementById('ss_completedContainer_${namespace}_${entryId}'), document.getElementById('ss_completedContainer_status_${namespace}_${entryId}'), '${fullValue}');"
			</c:if>
			></div>
	</c:forEach>
</div>
<div class="ss_bar_status" id="ss_completedContainer_status_${namespace}_${entryId}">${valuesMap[currentValue]}</div>

