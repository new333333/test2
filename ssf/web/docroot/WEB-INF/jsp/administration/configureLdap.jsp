<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>

<div class="ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="ldap.title"/></span><br/><br/>

<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" 
  action="<portlet:actionURL><portlet:param 
  name="action" value="configure_ldap"/></portlet:actionURL>">
<div class="ss_buttonBarRight">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssLdapConfig.enabled}">checked</c:if>/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.enable"/></span><br/>
</td></tr>
<tr><td>
<input type="checkbox" id="runnow" name="runnow"/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.now"/></span><br/>
</td></tr></table>

<br/>
<ssf:expandableArea title="<%= NLT.get("ldap.schedule") %>">
<c:set var="schedule" value="${ssLdapConfig.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
<div class="ss_divider"></div>
</ssf:expandableArea>
<ssf:expandableArea title="<%= NLT.get("ldap.connection") %>">
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
<tr>
	   <td colspan="2"><ssf:nlt tag="ldap.user.url.title"/></td>
	   </tr>
	   <tr><td>
	   <span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.user.url"/></span>
	   </td><td><input type="text"  size="100" name="userUrl" value="${ssLdapConfig.userUrl}"/>
	   </td>
	   </tr><tr>
	   <td><span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.user.principal"/></span>
	   </td><td><input type="text"  name="userPrincipal" value="${ssLdapConfig.userPrincipal}"/>
	   </td>
	   </tr><tr>
	   <td><span class="ss_labelLeft ss_normal"><ssf:nlt tag="ldap.user.credential"/></span>
	   </td><td><input type="password"  name="userCredentials" value="${ssLdapConfig.userCredential}"/>
	   </td>
	   </tr>
	   </table>
<div class="ss_divider"></div>
</ssf:expandableArea>
<br/>

<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.users" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
		<tr><td>
		<span class="ss_labelAbove ss_normal"><ssf:nlt tag="ldap.user.idmapping"/></span>
	  	<input type="text" name="userIdMapping" size="50" value="${ssLdapConfig.userIdMapping}"/>
	 	</td></tr>
		<tr><td>
		<span class="ss_labelAbove ss_normal"><ssf:nlt tag="ldap.user.mappings"/></span>
	  	<c:set var="mappings" value="${ssLdapConfig.userMappings}"/>
<jsp:useBean id="mappings" type="java.util.Map" scope="page" />
	  	<%
			StringBuffer buf = new StringBuffer();
	  		for (java.util.Iterator iter=mappings.entrySet().iterator(); iter.hasNext();) {
				java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
				buf.append(me.getValue() + "=" + me.getKey() + "\n");
	  		}
	  	%>
	  	<textarea name="userMappings" style="height: 100px; width: 500px;" wrap="hard"><%=buf.toString()%></textarea>
		</td></tr>
		<tr><td><input type="checkbox" name="userSync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.sync"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userRegister" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.register"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userDelete" <c:if test="${ssLdapConfig.userDelete}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.delete"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userWorkspaceDelete" <c:if test="${ssLdapConfig.userWorkspaceDelete}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.workspace.delete"/></span></input>
	   	</td></tr>
	 	</td></tr>
</table>
</fieldset>

<br/>
<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.groups" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
	   <tr>
	   <td><input type="checkbox" name="groupRegister" <c:if test="${ssLdapConfig.groupRegister}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.register"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="membershipSync" <c:if test="${ssLdapConfig.membershipSync}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.membership.sync"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="groupDelete" <c:if test="${ssLdapConfig.groupDelete}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.delete"/></span></input></td>
	   </tr>
</table>
</fieldset>

<br/>
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</form>
</div>
