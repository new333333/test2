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

<script language="javascript">
function showUrl(params) {
	if (parent.ss_showUrlInPortlet) {
		alert('Calling parent portlet: '+ params)
		parent.ss_showUrlInPortlet(params)
		return false
	} else {
		alert('no parent found')
		return true
	}
}
</script>

<div class="ss_portlet" align="left">
<a href="<portlet:renderURL/>" onClick="return showUrl('action=fragment&operation=showFragment&')">
show url in portlet
</a>

<br>

<a href="<portlet:renderURL>
         <portlet:param name="action" value="fragment"/>
         <portlet:param name="operation" value="showFragment"/>
         </portlet:renderURL>" onClick="alert(this.href)">
show url in iframe
</a>

</div>