<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript">
function doIt(op) {
	var url = "/remoteapp/addentry/form?operation="+op;
	self.location.href = url;
}
</script>

<div style="border:1px solid black;margin:6px;padding:6px;">
<h3>Remote application demonstration - Choose an operation</h3>

<a href="javascript: ;" onClick="doIt("view");return false;">View an entry</a><br/><br/>
<a href="javascript: ;" onClick="doIt("form");return false;">Add an entry</a>
</div>
<br/>
<br/>
