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

<body>

<script type="text/javascript">
function showUrl(params) {
	if (parent.ss_showUrlInPortlet) {
		alert('Calling parent portlet: '+ params)
		parent.ss_showUrlInPortlet(params)
		return false
	} else if (self.opener && self.opener.ss_showUrlInPortlet) {
		alert('Calling opener portlet: '+ params)
		self.opener.ss_showUrlInPortlet(params)
		setTimeout('self.window.close();', 200)
		return false
	} else {
		alert('no parent or opener found')
		return true
	}
}
</script>

<div class="ss_style ss_portlet" align="left">
<a href="javascript: ;" onClick="return showUrl('action=fragment&')">
return to portlet url
</a>

<br>

<a href="<ssf:url 
    adapter="true" 
    portletName="ss_widgettest" 
    action="fragment" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>" onClick="alert(this.href)">
show url in iframe
</a>

</div>

</body>
</html>
