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
<c:if test="${empty ss_portletInitialization}">
<c:set var="adminTreeName" value="${renderResponse.namespace}_adminDomTree"/>
  <div class="ss_portlet_style ss_portlet">
	<table border="0" width="100%">
	<tr>
	  <td>
	    <ssHelpSpot helpId="admin_portlet/admin_portlet" 
	      title="<ssf:nlt tag="helpSpot.adminPortlet"/>"
	      offsetY="-40">
	    </ssHelpSpot>
	  </td>
	</tr>
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div>
						<c:if test="${ssUser.displayStyle != 'accessible'}" >
						  <ssf:tree treeName="${adminTreeName}" 
						    treeDocument="${ssAdminDomTree}" 
						    rootOpen="true" />
						</c:if>
						<c:if test="${ssUser.displayStyle == 'accessible'}" >
						<ssf:tree treeName="${adminTreeName}" 
						  treeDocument="${ssAdminDomTree}" 
						  flat="true"
						  rootOpen="true" />
						</c:if>
					</div>
				</td>
			</tr>
			</table>
		</td>
		<td align="right" width="30" valign="top">
		<a href="#" onClick="ss_helpSystem.run();return false;"><img border="0" 
  		  src="<html:imagesPath/>icons/help.png" 
  		  alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
		</td>
	</tr>
	</table>
  </div>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</c:if>
