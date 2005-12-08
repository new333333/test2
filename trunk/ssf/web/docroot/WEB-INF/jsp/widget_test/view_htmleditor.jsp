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
<%@ include file="/html/common/init.jsp" %>
<%@ page import="java.util.Map" %>


<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"view-htmleditor\") %>" />


<%
	String background = GetterUtil.get(request.getParameter("body_background"), skin.getGamma().getBackground());

	String textfield1 = (String)request.getAttribute("textfield1");
	String textfield2 = (String)request.getAttribute("textfield2");
    Map formData = request.getParameterMap();

    if (formData.containsKey("OK")) {
	  String ed1hidden = ((String[])formData.get("editor1"))[0];
	  String ed2hidden = ((String[])formData.get("editor2"))[0];
	  textfield1 = ed1hidden;
	  textfield2 = ed2hidden;
	

%>

	You entered: 
	<table border="1">
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
	<p>
	Care to try again?
<%
	}
%>

<form method="POST" action="" name="htmleditortest">
<span align="center">SiteScape Forum Widget Tester -  HTML Editor widget
Two HTML Editors on one form. <br>
First editor has height=150, second one has no size specified, so defaults to 250 (pixels).
<br />
</span>


	<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table align=center border="0" cellpadding="3" cellspacing="3">
			<tr>
				<td>
					First editor:
					<div>
					<sitescape:htmleditor id="editor1" 
						formName="editortest"
						height="150" color="<%= background %>"
						initText="<%= textfield1 %>" />
					</div>
				</td>
			</tr>
			<tr>
				<td>
					Second editor:
					<div>
					<sitescape:htmleditor id="editor2" 
						formName="editortest"  color="<%= background %>"
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
	<input type="submit" name="OK" value="OK">
</form>
</liferay:box>

