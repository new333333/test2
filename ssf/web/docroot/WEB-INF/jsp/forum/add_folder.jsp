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
<span class="ss_titlebold"><c:out value="${ssBinder.title}"/>&nbsp;-&nbsp;<ssf:nlt tag="folder.add.title"/></span><br/><br/>

<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="add_binder"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
			<portlet:param name="operation" value="${operation}"/>
		</portlet:actionURL>">

<% // Show the workspace according to its definition %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />

<br/>
	
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">

</form>
</div>
