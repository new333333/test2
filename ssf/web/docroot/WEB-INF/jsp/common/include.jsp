
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<portletadapter:defineObjects1/>

<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
  <c:if test="${empty ssf_snippet}">
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
	<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
	<head>
  </c:if>
</c:if>
<portletadapter:defineObjects2/>
</ssf:ifadapter>

<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${empty ssf_snippet}">
  <%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>
</c:if>
<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
  <c:if test="${empty ssf_snippet}">
    </head>
  </c:if>
</c:if>
</ssf:ifadapter>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

