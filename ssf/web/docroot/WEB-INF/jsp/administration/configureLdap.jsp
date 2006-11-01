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
<%@ page import="com.sitescape.ef.util.NLT" %>

<div class="ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="ldap.title"/></span><br/><br/>

<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_ldap"/>
		</portlet:actionURL>">
<div class="ss_buttonBarRight">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssLdapConfig.enabled}">checked</c:if>/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.enable"/></span><br/>
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
		<span class="ss_labelAbove ss_normal"><ssf:nlt tag="ldap.user.mappings"/></span><br/>
	  	<c:set var="mappings" value="${ssLdapConfig.userMappings}"/>
<jsp:useBean id="mappings" type="java.util.Map" scope="page" />
	  	<%
			StringBuffer buf = new StringBuffer();
	  		for (java.util.Iterator iter=mappings.entrySet().iterator(); iter.hasNext();) {
				java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
				buf.append(me.getValue() + "=" + me.getKey() + "\n");
	  		}
	  		buf.toString();
	  	%>
	  	<textarea name="userMappings" style="height: 100px; width: 500px;" wrap="hard"><%=buf.toString()%></textarea>
		</td></tr>
		<tr><td><input type="checkbox" name="userSync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.sync"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userRegister" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.register"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userDisable" <c:if test="${ssLdapConfig.userDisable}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.disable"/></span></input>
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
	   <td><input type="checkbox" name="groupDisable" <c:if test="${ssLdapConfig.groupDisable}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.disable"/></span></input></td>
	   </tr>
</table>
</fieldset>

<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
</div>
