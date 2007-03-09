<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>

<body>

<h1><%= request.getAttribute("header") %></h1>

<form class="ss_style ss_form" method="post" enctype="multipart/form-data" action="<ssf:servletrooturl/>uploadFile">

	<table class="ss_style" border="0" cellpadding="4">
		<tr>
			<th>Title</th>
			<td><input type="text" class="ss_text" name="title" size="30" maxlength="80"/></td>
		</tr>
		<tr>
			<th>Server Directory</th>
			<td><input type="text" class="ss_text" name="serverDir" value="C:/junk" size="30" maxlength="80"/></td>
		</tr>
		<tr>
			<th>File</th>
			<td><input type="file" class="ss_text" name="file"/></td>
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
