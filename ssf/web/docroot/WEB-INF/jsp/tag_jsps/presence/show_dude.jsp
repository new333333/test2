<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<c:if test="${empty ss_presence_user && !ss_presence_show_options_inline}">
<a href="javascript: ;"
 onClick="ss_popupPresenceMenu(this, '', '', '-1', '', '', '', '', '', '${ss_presence_component_id}', '${ss_presence_zonBridge}');return false;"
><img border="0" src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"/></a>
</c:if>
<c:if test="${empty ss_presence_user && ss_presence_show_options_inline}">
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
<c:if test="${!ss_presence_show_options_inline}">
<a href="javascript: ;"
 style="background:url(<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>) no-repeat left;
 padding-left:10px; 
 <c:if test="${ss_presence_show_title}">
   padding-right:2px; 
 </c:if>
 <c:if test="${!ss_presence_show_title}">
   padding-right:10px; 
 </c:if>
 text-decoration:none;"
 onClick="ss_popupPresenceMenu(this, 
    '${ss_presence_user.id}', 
    '<%= presenceUserTitle %>', 
    '${ss_presence_userStatus}', 
    '<%= presenceUserZonName %>', 
    '<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${ss_presence_sweep_time}" type="both" timeStyle="short" dateStyle="medium" />', 
    '<%= presenceUserEmailAddress %>', 
    '${ss_presence_vcard}', 
    '${current}', '${ss_presence_component_id}', '${ss_presence_zonBridge}');return false;"
></a>
  <c:if test="${ss_presence_show_title}">
	<ssf:ifadapter>
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ss_presence_user.parentBinder.id}"
	    entryId="${ss_presence_user.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
		onClick="self.parent.location.href = this.href;return false;"
	  ><c:out value="${ss_presence_user.title}"/></a>
	</ssf:ifadapter>
	<ssf:ifnotadapter>
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_ws_listing"/><portlet:param 
	  	name="binderId" value="${ss_presence_user.parentBinder.id}"/><portlet:param 
	  	name="entryId" value="${ss_presence_user.id}"/><portlet:param 
	  	name="newTab" value="1"/></portlet:renderURL>"
	  ><c:out value="${ss_presence_user.title}"/></a>
	</ssf:ifnotadapter>
 
  </c:if>
</c:if>
<c:if test="${ss_presence_show_options_inline}">
<script type="text/javascript">
ss_presenceMenu('${ss_presence_div_id}', this, 
    '${ss_presence_user.id}', 
    '<%= presenceUserTitle %>', 
    '${ss_presence_userStatus}', 
    '<%= presenceUserZonName %>', 
    '<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${ss_presence_sweep_time}" type="both" timeStyle="short" dateStyle="medium" />', 
    '<%= presenceUserEmailAddress %>', 
    '${ss_presence_vcard}', 
    '${current}', 
    '${ss_presence_component_id}', 
    '${ss_presence_zonBridge}');

</script>
</c:if>

</c:if>
