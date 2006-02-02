<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<jsp:useBean id="ss_presence_user" type="com.sitescape.ef.domain.User" scope="request" />
<%
	String presenceUserTitle = ss_presence_user.getTitle();
	if (presenceUserTitle == null) presenceUserTitle="";
	else presenceUserTitle = presenceUserTitle.replaceAll("'", "\\\\'");
	String presenceUserZonName = ss_presence_user.getZonName();
	if (presenceUserZonName == null) presenceUserZonName="";
	else presenceUserZonName.replaceAll("'", "\\\\'");
	String presenceUserEmailAddress = ss_presence_user.getEmailAddress();
	if (presenceUserEmailAddress == null) presenceUserEmailAddress="";
	else presenceUserEmailAddress.replaceAll("'", "\\\\'");
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
