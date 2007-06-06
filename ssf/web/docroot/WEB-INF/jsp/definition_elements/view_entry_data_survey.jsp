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

<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>

<p>TEST DRAW CHART:
<ssf:drawChart count="30" total="100"/>
</p>

<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>



<c:forEach var="question" items="${ssDefinitionEntry.customAttributes[property_name].value.questions}" >
	<div class="ss_questionContainer">
		<p><c:out value="${question.question}" escapeXml="false"/></p>
		<c:if test="${question.overdued || question.alreadyVoted}">
			<c:if test="${question.type == 'multiple' || question.type == 'single'}">
				<ol>
				<c:forEach var="answer" items="${question.answers}">
					<li><ssf:drawChart count="${answer.votesCount}" total="${question.totalResponses}"/><c:out value="${answer.text}" escapeXml="false"/></li>
				</c:forEach>
				</ol>
			</c:if>
		</c:if>
		<c:if test="${!question.overdued && !question.alreadyVoted}">
			<c:if test="${question.type == 'multiple'}">
				<ol>
				<c:forEach var="answer" items="${question.answers}">
					<li><input type="checkbox" name="${questionNo}"><c:out value="${answer.text}" escapeXml="false"/></li>
				</c:forEach>
				</ol>
			</c:if>
			<c:if test="${question.type == 'single'}">
				<ol>
				<c:forEach var="answer" items="${question.answers}">
					<li><input type="radio" name="${questionNo}"><c:out value="${answer.text}" escapeXml="false"/></li>
				</c:forEach>
				</ol>
			</c:if>
			<c:if test="${question.type == 'input'}">
				<input type="text" name="${questionNo}">
			</c:if>
		</c:if>
	</div>
</c:forEach>

</div>