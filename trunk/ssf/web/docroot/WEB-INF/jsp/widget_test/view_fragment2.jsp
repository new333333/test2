<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>

<body>

<script language="javascript">
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

<div class="ss_portlet" align="left">
<a href="javascript: ;" onClick="return showUrl('action=fragment&operation=showFragment&')">
show url in portlet
</a>

<br>

<a href="<ssf:url 
    webPath="viewFragment" >
	<ssf:param name="operation" value="viewFragment" />
    </ssf:url>" onClick="alert(this.href)">
show url in iframe
</a>

</div>

</body>
</html>
