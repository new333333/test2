<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<%@ page import="com.sitescape.util.ParamUtil" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<img border="0" src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>">
