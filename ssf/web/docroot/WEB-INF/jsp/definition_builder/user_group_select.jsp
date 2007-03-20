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

<div>
<iframe id="ss_userGroupSelectIframe"
	name="ss_userGroupSelectIframe"
	style="height:350; width:350; margin:0px; padding:0px;" 
	frameBorder="0"
	src="<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="get_users_and_groups" />
		<ssf:param name="elementName" value="propertyId_${propertyId}" />
		<ssf:param name="prefix" value="${ssPrefix}" />
		<ssf:param name="userList" value="${propertyValue}" />
    	</ssf:url>">xxx</iframe>
</div>
<table cellspacing="10px" cellpadding="10px" width="100%">
<tbody>
	<tr>
		<td valign="top">
			<input type="text" class="ss_text" id="propertyId_${propertyId}" 
			  name="propertyId_${propertyId}" size="40"
			  value="${propertyValue}" />
		</td>
	</tr>
</tbody>
</table>





