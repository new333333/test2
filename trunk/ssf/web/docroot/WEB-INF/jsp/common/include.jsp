<%@ page session="false" %>
<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

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
<%@ include file="/WEB-INF/jsp/forum/view_css.jsp" %>
<c:if test="${empty ssf_support_files_loaded}">
<script language="javascript">
//See if this page is in the process of logging in while viewing a mail notification page
if (self.parent && self.parent.ss_transferUrl) {
	//The parent frame has specified a url to transfer to. Go do it.
	self.parent.location.href = self.parent.ss_transferUrl;
}
</script>
</c:if>
<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
</head>
</c:if>
</ssf:ifadapter>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

