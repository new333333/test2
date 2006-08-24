<%
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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
<div class="ss_style ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px; width:100%;">
<h3><ssf:nlt tag="presence.configure" text="Configure buddy list"/></h3>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="presence.configure.buddies" 
    text="Buddies"/></legend>
 <form class="ss_style" name="${renderResponse.namespace}fm" method="post" 
  onSubmit="return ss_onSubmit(this);"
  action="<portlet:actionURL>
		  <portlet:param name="action" value="configure"/>
		  </portlet:actionURL>">

<table cellspacing="10px" cellpadding="10px" width="100%">

<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" 
 text="Users"/></td>
<td valign="top">
  <ssf:findUsers formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" 
 text="Groups"/></td>
<td valign="top">
  <ssf:findUsers formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>

<tr>
<td colspan="2">
<input type="submit" class="ss_submit" name="applyBtn"
 value="<ssf:nlt tag="button.apply" text="Apply"/>">
</td>
</tr>
</table>

</form>
</fieldset>


</div>
</div>
</div>
</div>
