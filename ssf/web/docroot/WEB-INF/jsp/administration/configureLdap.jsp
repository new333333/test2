<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="ldap.title"/></span><br/><br/>

<form class="ss_style" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_ldap"/>
		</portlet:actionURL>">
<script type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>fm.disabled.checked) {
		document.<portlet:namespace/>fm.enabled.value = "false";
	} else {
		document.<portlet:namespace/>fm.enabled.value = "true";
	}
}
</script>
<input type="hidden" id="enabled" name="enabled" value="${ssLdapConfig.enabled}"/>

<table class="ss_style"  border ="1" cellspacing="0" cellpadding="3">
  <tr>
  <td align="center" class="ss_bold"><ssf:nlt tag="ldap.session"/></td>
  <td colspan="2" align="center" class="ss_bold"><ssf:nlt tag="ldap.schedule"/></td>
  </tr>
  <tr><td  valign="top"> 
   	<table class="ss_style"  border="0" cellpadding="0" cellspacing="2">
	   <tr>
	   <td><input type="checkbox" name="sessionSync" <c:if test="${ssLdapConfig.sessionSync}">checked</c:if>>
	   <span nowrap="nowrap"><ssf:nlt tag="ldap.session.sync"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="sessionRegister" <c:if test="${ssLdapConfig.sessionRegister}">checked</c:if>>
	   <span nowrap="nowrap"><ssf:nlt tag="ldap.session.register"/></span></input></td>
	   </tr>
	 </table>
  </td><td   valign="top">
  	 <table class="ss_style"  border="0" cellpadding="0" cellspacing="2">
	   <tr><td ><input type="checkbox" name="userSync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.user.sync"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="userRegister" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.user.register"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="userDisable" <c:if test="${ssLdapConfig.userDisable}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.user.disable"/></span></input></td>
	   </tr>
	   <tr>
	   <td><input type="checkbox" name="groupSync" <c:if test="${ssLdapConfig.groupSync}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.group.sync"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="groupRegister" <c:if test="${ssLdapConfig.groupRegister}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.group.register"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="groupDisable" <c:if test="${ssLdapConfig.groupDisable}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.group.disable"/></span></input></td>
	   <tr>
	   <td><input type="checkbox" name="membershipSync" <c:if test="${ssLdapConfig.membershipSync}">checked</c:if>>
	   <span  nowrap="nowrap"><ssf:nlt tag="ldap.schedule.membership.sync"/></span></input></td>
	   </tr><tr>
	   </tr>
     </table>
  </td><td valign="top">
  	 <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
	   <tr>
	   <td><input type="checkbox"  id="disabled" name="disabled" onClick="<portlet:namespace/>setEnable();" <c:if test="${!ssLdapConfig.enabled}">checked</c:if>>
	   <span wrap="nowrap"><ssf:nlt tag="ldap.schedule.disable"/></span></input><br/>
   <c:set var="schedule" value="${ssLdapConfig.schedule}"/>
   
	<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
	 	</td></tr>
		</table>
</td></tr>
</table>

	<br/>
	<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
	<input type="submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
<form>
</div>
