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
<%@ page import="java.util.Map" %>

<%

	String textfield1 = (String)request.getAttribute("textfield1");
	String textfield2 = (String)request.getAttribute("textfield2");
    Map formData = request.getParameterMap();

    if (formData.containsKey("OK")) {
	  String ed1hidden = ((String[])formData.get("editor1"))[0];
	  String ed2hidden = ((String[])formData.get("editor2"))[0];
	  textfield1 = ed1hidden;
	  textfield2 = ed2hidden;
	

%>
<div class="ss_portlet ss_portlet_style">

<center>	You entered: 
	<table class="ss_style" border="1">
	<th>
	First editor:
	</th>
	<th>
	Second editor:
	</th>
	</tr>
	<tr>
	<td>
	<%= ed1hidden %>
	</td>
	<td>
	<%= ed2hidden %>
	</td>
	</tr>
	</table>
	<br>
	Care to try again?
	</center>
<%
	}
%>

<form class="ss_style ss_form" method="POST" action="" name="htmleditortest">
<span align="center">SiteScape Forum Widget Tester -  HTML Editor widget <br/>
Two HTML Editors on one form. <br>
First editor has height=150, second one has no size specified, so defaults to 250 (pixels).
<br />
</span>


	<table cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table align=center border="0" cellpadding="3" cellspacing="3">
			<tr>
				<td>
					First editor:
					<div >
					<ssf:htmleditor id="editor1" name="editor1" 
						height="150" color="tan"
						initText="<%= textfield1 %>" />
					</div>
				</td>
			</tr>
			<tr>
				<td>
					Second editor:
					<div>
					<ssf:htmleditor id="editor2" name="editor2" 
						initText="<%= textfield2 %>" />
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
	<br>
	<hr>
	<input type="submit" class="ss_submit" name="OK" value="OK">
</form>

</div>

