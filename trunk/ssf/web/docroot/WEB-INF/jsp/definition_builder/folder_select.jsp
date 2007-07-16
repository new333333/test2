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
<div>
<span><ssf:nlt tag="definition.selectFolderToMoveTo"/></span>
<input type="hidden" id="propertyId_${propertyId}" 
  name="propertyId_${propertyId}"
  value="${propertyValue}" />
<table cellspacing="0px" cellpadding="0px" width="100%">
  <tbody>
	<tr>
		<td valign="top">
			<iframe frameborder="0" style="height:230px;" id="ss_folderSelectIframe" src="<ssf:url 
			    adapter="true" 
			    actionUrl="true"
			    portletName="ss_forum" 
			    action="__ajax_request">
				  <ssf:param name="operation" value="find_place_form" />
				  <ssf:param name="binderId" value="${ssBinderId}" />
				  <ssf:param name="propertyId" value="propertyId_${propertyId}" />
			    </ssf:url>">xxx</iframe> 
		</td>
	</tr>
  </tbody>
</table>
</div>




