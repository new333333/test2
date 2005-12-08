<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>

<body>

<h1><%= request.getAttribute("header") %></h1>

<form method="post" enctype="multipart/form-data" action="<html:adapterPath/>do?p_a_name=ss_employees&p_a_action=1&action=uploadFile"">

	<table border="0" cellpadding="4">
		<tr>
			<th>Title</th>
			<td><input type="text" name="title" size="30" maxlength="80"/></td>
		</tr>
		<tr>
			<th>Server Directory</th>
			<td><input type="text" name="serverDir" value="C:/junk" size="30" maxlength="80"/></td>
		</tr>
		<tr>
			<th>File</th>
			<td><input type="file" name="file"/></td>
		</tr>
		<tr>
			<th colspan="2">
				<button type="submit" name="submit">Submit</button>
			</th>
		</tr>
	</table>
</form>

<p style="text-align:center;"><a href="javascript:window.close()">Close</a></p>

</body>
</html>
