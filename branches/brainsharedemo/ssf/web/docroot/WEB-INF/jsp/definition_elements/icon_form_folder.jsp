<% //Icon form for folders %>
<%@ page import="com.sitescape.team.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="iconListPath" value="icons.folder" scope="request"/>
<c:set var="iconValue" value="${ssDefinitionEntry.iconName}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/iconForm.jsp" %>
