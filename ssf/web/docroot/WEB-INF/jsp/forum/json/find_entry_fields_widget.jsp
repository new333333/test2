<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // This is JSON type AJAX response  %>
[
<c:set var="first" value="1"/>
<c:forEach var="field" items="${ssEntryDefinitionElementData}">
	<c:if test="${field.value.type != 'textarea'}">
		<c:if test="${first!=1}">,</c:if><c:set var="first" value="0"/>
		["<ssf:escapeJavaScript value="${field.key}"/>", "<ssf:escapeJavaScript value="${field.value.caption}"/>", "<ssf:escapeJavaScript value="${field.value.type}"/>"]
	</c:if>
</c:forEach>
]