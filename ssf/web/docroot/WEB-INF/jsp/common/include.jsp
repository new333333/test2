<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<portletadapter:defineObjects1/>

<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>
</c:if>
<portletadapter:defineObjects2/>
</ssf:ifadapter>

<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>
<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
</head>
</c:if>
</ssf:ifadapter>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

