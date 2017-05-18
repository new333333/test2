<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
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
			<table align=center border="0" cellpadding="3"  style="border-spacing: 3px;"">
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
	<sec:csrfInput />
</form>

</div>

