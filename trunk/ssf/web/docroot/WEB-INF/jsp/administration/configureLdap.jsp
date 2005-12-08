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
<form name="<portlet:namespace/>ldapForm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_ldap"/>
		</portlet:actionURL>">
<script language="javascript" type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>ldapForm.disableSchedule.checked) {
		document.<portlet:namespace/>ldapForm.enableSchedule.value = "false";
	} else {
		document.<portlet:namespace/>ldapForm.enableSchedule.value = "true";
	}
}
</script>
<input type="hidden" id="enableSchedule" name="ldap.schedule.enable" value="${ssLdapConfig.scheduleEnabled}"/>

<table border ="1" cellspacing="0" cellpadding="3">
  <tr>
  <td align="center" class="contentbold">Session synchronization </td>
  <td colspan="2" align="center" class="contentbold">Scheduled synchronization</td>
  </tr>
<tr><td class="content" valign="top"> 
  <table border="0" cellpadding="0" cellspacing="2">
   <tr>
   <td class="content"><input type="checkbox" name="ldap.session.sync" <c:if test="${ssLdapConfig.sessionSync}">checked</c:if>>
   <span class="content" nowrap="nowrap">User account synchronization</span></input></td>
   </tr><tr>
   <td class="content"><input type="checkbox" name="ldap.session.register" <c:if test="${ssLdapConfig.sessionRegister}">checked</c:if>>
   <span class="content" nowrap="nowrap">Automatic registration of accounts</span></input></td>
   </tr>

   </table>
</td><td class="content"  valign="top">
   <table border="0" cellpadding="0" cellspacing="2">
   <tr><td class="content"><input type="checkbox" name="ldap.users.sync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
   <span class="content" nowrap="nowrap">User account synchronization</span></input></td>
   </tr><tr>
   <td class="content"><input type="checkbox" name="ldap.users.register" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
   <span class="content" nowrap="nowrap">Automatic registration of accounts</span></input></td>
   </tr><tr>
   <td class="content"><input type="checkbox" name="ldap.users.disable" <c:if test="${ssLdapConfig.userDisable}">checked</c:if>>
   <span class="content" nowrap="nowrap">Disable accounts not in LDAP</span></input></td>
   </tr>
   <tr>
   <td class="content"><input type="checkbox" name="ldap.membership.sync" <c:if test="${ssLdapConfig.membershipSync}">checked</c:if>>
   <span class="content" nowrap="nowrap">Synchronization of group membership</span></input></td>
   </tr><tr>
   <td class="content"><input type="checkbox" name="ldap.groups.register" <c:if test="${ssLdapConfig.groupRegister}">checked</c:if>>
   <span class="content" nowrap="nowrap">Automatic registration of groups</span></input></td>
   </tr><tr>
   <td class="content"><input type="checkbox" name="ldap.groups.disable" <c:if test="${ssLdapConfig.groupDisable}">checked</c:if>>
   <span class="content" nowrap="nowrap">Disable groups not in LDAP</span></input></td>
   </tr>
   </table>
</td><td class="content"  valign="top">
   <table border="0" cellpadding="0" cellspacing="2">
   <tr>
   <td class="content">	<input type="checkbox" class="content" id="disableSchedule" name="disableSchedule" onClick="<portlet:namespace/>setEnable();" <c:if test="${!ssLdapConfig.scheduleEnabled}">checked</c:if>>
   <span class="content">Disable schedule</span></input><br/>
   
    <label for="schedTime">At time (hh:mm)</label>
   <input type="text" class="content" name="schedTime" id="schedTime" size="6" value="${ssLdapConfig.schedule.hoursMinutes}"><br><br>
   <input type="radio" class="content" name="schedType" id="schedType" value="daily" <c:if test="${ssLdapConfig.schedule.daily}">checked</c:if>> <span class="content"><label for="schedType">every day</label></span></input> <br>
   <input type="radio" class="content" name="schedType" id="schedType" value="weekly"   <c:if test="${!ssLdapConfig.schedule.daily}">checked</c:if> > <span class="content"><label for="schedType">weekly (on the days selected below)</label></span></input> <br>
   <table border="0" cellpadding="0" cellspacing="2">
	  <tr><td class="content">&nbsp;&nbsp;&nbsp;&nbsp;</td>
	  <td class="content">
	<input type="checkbox" class="content" name="onday_sun" id="onday_sun"  <c:if test="${ssLdapConfig.schedule.onSunday}">checked</c:if>> <span class="content"><label for="onday_sun">Sun</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_mon" id="onday_mon" <c:if test="${ssLdapConfig.schedule.onMonday}">checked</c:if>> <span class="content"><label for="onday_mon">Mon</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_tue" id="onday_tue" <c:if test="${ssLdapConfig.schedule.onTuesday}">checked</c:if>> <span class="content"><label for="onday_tue">Tue</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_wed" id="onday_wed" <c:if test="${ssLdapConfig.schedule.onWednesday}">checked</c:if>> <span class="content"><label for="onday_wed">Wed</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_thu" id="onday_thu" <c:if test="${ssLdapConfig.schedule.onThursday}">checked</c:if>> <span class="content"><label for="onday_thu">Thu</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_fri" id="onday_fri" <c:if test="${ssLdapConfig.schedule.onFriday}">checked</c:if>> <span class="content"><label for="onday_fri">Fri</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_sat" id="onday_sat" <c:if test="${ssLdapConfig.schedule.onSaturday}">checked</c:if>> <span class="content"><label for="onday_sat">Sat</label></span></input> </td>
	
	</tr>
	</table></td></tr>
	</table></td></tr>
</table>

	<br/>
	<input type="submit" name="okBtn" value="Ok">
	<input type="submit" name="cancelBtn" value="Cancel">
<form>

