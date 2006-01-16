<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<%@ page import="com.sitescape.util.ParamUtil" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:set var="current" value=""/>
<c:if test="${ssUser.zonName == ss_presence_user.zonName}">
<c:set var="current" value="current"/>
</c:if>
<a href="javascript: ;"
 onClick="popupPresenceMenu(this, '<c:out 
     value="${ss_presence_user.id}"/>', '<c:out 
     value="${ss_presence_user.title}"/>', '<c:out 
     value="${ss_presence_userStatus}"/>', '<c:out 
     value="${ss_presence_user.zonName}"/>', '<c:out 
     value="${ss_presence_sweep_time}"/>', '<c:out 
     value="${ss_presence_user.emailAddress}"/>', '<c:out 
     value="${ss_presence_vcard}"/>', '<c:out 
     value="${current}"/>');return false;"
><img border="0" src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"></a>
