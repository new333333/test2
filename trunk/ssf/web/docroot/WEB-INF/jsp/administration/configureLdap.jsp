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

<h3>Configure Ldap Synchronization</h3>
<form class="ss_style" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_ldap"/>
		</portlet:actionURL>">
<script language="javascript" type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>fm.disabled.checked) {
		document.<portlet:namespace/>fm.enabled.value = "false";
	} else {
		document.<portlet:namespace/>fm.enabled.value = "true";
	}
}
</script>
<input type="hidden" id="enabled" name="enabled" value="${ssLdapConfig.enabled}"/>

<table class="ss_style" border ="1" cellspacing="0" cellpadding="3">
  <tr>
  <td align="center" class="contentbold">Session synchronization </td>
  <td colspan="2" align="center" class="contentbold">Scheduled synchronization</td>
  </tr>
  <tr><td class="content" valign="top"> 
   	<table class="ss_style" border="0" cellpadding="0" cellspacing="2">
	   <tr>
	   <td class="content"><input type="checkbox" name="sessionSync" <c:if test="${ssLdapConfig.sessionSync}">checked</c:if>>
	   <span class="content" nowrap="nowrap">User account synchronization</span></input></td>
	   </tr><tr>
	   <td class="content"><input type="checkbox" name="sessionRegister" <c:if test="${ssLdapConfig.sessionRegister}">checked</c:if>>
	   <span class="content" nowrap="nowrap">Automatic registration of accounts</span></input></td>
	   </tr>
	 </table>
  </td><td class="content"  valign="top">
  	 <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
	   <tr><td class="content"><input type="checkbox" name="userSync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
	   <span class="content" nowrap="nowrap">User account synchronization</span></input></td>
	   </tr><tr>
	   <td class="content"><input type="checkbox" name="userRegister" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
	   <span class="content" nowrap="nowrap">Automatic registration of accounts</span></input></td>
	   </tr><tr>
	   <td class="content"><input type="checkbox" name="userDisable" <c:if test="${ssLdapConfig.userDisable}">checked</c:if>>
	   <span class="content" nowrap="nowrap">Disable accounts not in LDAP</span></input></td>
	   </tr>
	   <tr>
	   <td class="content"><input type="checkbox" name="membershipSync" <c:if test="${ssLdapConfig.membershipSync}">checked</c:if>>
	   <span class="content" nowrap="nowrap">Synchronization of group membership</span></input></td>
	   </tr><tr>
	   <td class="content"><input type="checkbox" name="groupRegister" <c:if test="${ssLdapConfig.groupRegister}">checked</c:if>>
	   <span class="content" nowrap="nowrap">Automatic registration of groups</span></input></td>
	   </tr><tr>
	   <td class="content"><input type="checkbox" name="groupDisable" <c:if test="${ssLdapConfig.groupDisable}">checked</c:if>>
	   <span class="content" nowrap="nowrap">Disable groups not in LDAP</span></input></td>
	   </tr>
     </table>
  </td><td class="content"  valign="top">
  	 <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
	   <tr>
	   <td class="content">	<input type="checkbox" class="content" id="disabled" name="disabled" onClick="<portlet:namespace/>setEnable();" <c:if test="${!ssLdapConfig.enabled}">checked</c:if>>
	   <span class="content">Disable schedule</span></input><br/>
   <c:set var="schedule" value="${ssLdapConfig.schedule}"/>
   
	<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
	 	</td></tr>
		</table>
</td></tr>
</table>

	<br/>
	<input type="submit" name="okBtn" value="Ok">
	<input type="submit" name="cancelBtn" value="Cancel">
<form>

