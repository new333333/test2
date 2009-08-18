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
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>

<jsp:useBean id="ss_formData" type="java.util.Map" scope="request" />

<%
	Date initDate = (Date)request.getAttribute("initDate");
	Date initDate2 = (Date)request.getAttribute("initDate2");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        Map formData = request.getParameterMap();

        if (formData.containsKey("OK")) {
	  String ed1hidden = ((String[])formData.get("date1"))[0];
	  Date entered_date1;
	  Date entered_date2;
	  String ed1str = "";
	  if (ed1hidden.equals("")) {
	  	entered_date1 = new Date();
	  	ed1hidden = "Date not selected";
	    
	  } else {
		entered_date1 = sdf.parse(ed1hidden);
	    ed1str = sdf2.format(entered_date1);
	  };
	  String ed2hidden = ((String[])formData.get("date2"))[0];
	  String ed2str = "";
	  if (ed2hidden.equals("")) {
	  	entered_date2 = new Date();
	  	ed2hidden = "Date not selected";
	  } else {
	  	entered_date2 = sdf.parse(ed2hidden);
        ed2str = sdf2.format(entered_date2);
	  };
%>

<b>You chose:</b>
<br>
<table class="ss_style" border="1">
<tr>
<th>Datepicker 1</th>
<th>Datepicker 2</th>
</tr>
<tr>
<td>
<%= ed1str %> (<%=ed1hidden%>)
</td>
<td>
<%= ed2str %> (<%=ed2hidden%>)
</td>
</tr>

</table>
<p>

<%
   }
%>

<form class="ss_style ss_form" method="POST" action="" name="datepickertest" onSubmit="return ss_onSubmit(this);">
<span align="center">SiteScape Forum Widget Tester -  Datepicker Widget
<br />
This test page shows datepickers for the current date and an unspecified date. 
</span>
<br>
<br />

	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" align=center border="1" cellpadding="3" style="border-spacing: 3px;">
			<tr>
				<td>
					First datepicker:
					<div>
					<ssf:datepicker id="date1" 
						formName="datepickertest"
						initDate="<%=initDate%>" />
					</div>
				</td>
				<td>
					Second datepicker:
					<div>
					<ssf:datepicker id="date2" 
						formName="datepickertest"
						 />
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
