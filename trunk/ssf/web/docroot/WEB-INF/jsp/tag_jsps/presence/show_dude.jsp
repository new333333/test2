<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<c:if test="${empty ss_presence_user}">
<a href="javascript: ;"
 onClick="popupPresenceMenu(this, '', '', '-1', '', '', '', '', '');return false;"
><img border="0" src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"></a>
</c:if>

<c:if test="${!empty ss_presence_user}">
<c:set var="presence_user_title" value=""/>
<c:if test="${!empty ss_presence_user.title}">
<c:set var="presence_user_title" value="${ss_presence_user.title}"/>
</c:if>
<c:set var="presence_user_zonName" value=""/>
<c:if test="${!empty ss_presence_user.zonName}">
<c:set var="presence_user_zonName" value="${ss_presence_user.zonName}"/>
</c:if>
<c:set var="presence_user_emailAddress" value=""/>
<c:if test="${!empty ss_presence_user.emailAddress}">
<c:set var="presence_user_emailAddress" value="${ss_presence_user.emailAddress}"/>
</c:if>
<jsp:useBean id="presence_user_title" type="java.lang.String" />
<jsp:useBean id="presence_user_zonName" type="java.lang.String" />
<jsp:useBean id="presence_user_emailAddress" type="java.lang.String" />

<%
	String presenceUserTitle = presence_user_title;
	presenceUserTitle = presenceUserTitle.replaceAll("'", "\\\\'");
	String presenceUserZonName = presence_user_zonName;
	presenceUserZonName.replaceAll("'", "\\\\'");
	String presenceUserEmailAddress = presence_user_emailAddress;
	presenceUserEmailAddress.replaceAll("'", "\\\\'");
%>
<c:set var="current" value=""/>
<c:if test="${ssUser.zonName == ss_presence_user.zonName}">
<c:set var="current" value="current"/>
</c:if>
<a href="javascript: ;"
 onClick="popupPresenceMenu(this, 
    '${ss_presence_user.id}', 
    '<%= presenceUserTitle %>', 
    '${ss_presence_userStatus}', 
    '<%= presenceUserZonName %>', 
    '<fmt:formatDate value="${ss_presence_sweep_time}" type="both" pattern="EEEE, HH:mm" />', 
    '<%= presenceUserEmailAddress %>', 
    '${ss_presence_vcard}', 
    '${current}');return false;"
><img border="0" src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"></a>
</c:if>
