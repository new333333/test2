<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<jsp:useBean id="ss_presence_user" type="com.sitescape.ef.domain.User" scope="request" />
<%
	String presenceUserTitle = "";
	if (ss_presence_user.getTitle() != null) presenceUserTitle = ss_presence_user.getTitle().replaceAll("'", "\\\\'");
	String presenceUserZonName = "";
	if (ss_presence_user.getZonName() != null) presenceUserZonName = ss_presence_user.getZonName().replaceAll("'", "\\\\'");
	String presenceUserEmailAddress = "";
	if (ss_presence_user.getEmailAddress() != null) presenceUserEmailAddress = ss_presence_user.getEmailAddress().replaceAll("'", "\\\\'");
%>
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
     value="${ss_presence_user.id}"/>', '<%= presenceUserTitle %>', '<c:out 
     value="${ss_presence_userStatus}"/>', '<%= presenceUserZonName %>', '<fmt:formatDate 
     value="${ss_presence_sweep_time}" type="both" 
	 pattern="EEEE, HH:mm" />', '<%= presenceUserEmailAddress %>', '<c:out 
     value="${ss_presence_vcard}"/>', '<c:out 
     value="${current}"/>');return false;"
><img border="0" src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"></a>
