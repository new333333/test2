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
<c:if test="${empty ss_portletInitialization}">
<c:set var="adminTreeName" value="${renderResponse.namespace}_adminDomTree"/>
  <div class="ss_portlet_style ss_portlet">
  <div class="ss_style">
    <c:out value="${releaseInfo}"/>
	<table border="0" width="100%">
	<tr>
	  <td>
	    <ssHelpSpot helpId="admin_portlet/admin_portlet" 
	      title="<ssf:nlt tag="helpSpot.adminPortlet"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt>"
	      offsetY="5" offsetX="-13">
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
  </div>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
</c:if>
