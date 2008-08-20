<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sitescape.team.web.util.DateHelper" %>
<jsp:useBean id="formData" type="java.util.Map" scope="request" />


<%
	Date initDate = (Date)request.getAttribute("initDate");
	Date initDate2 = (Date)request.getAttribute("initDate2");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, MMMM dd, yyyy kk:mm");

        if (formData.containsKey("OK")) {

	  String ed1hidden = "foo";
	  Date entered_date1 = DateHelper.getDateFromMap(formData, "date1");
	  Date entered_date2 = DateHelper.getDateFromMap(formData, "date2");
							 
	  String ed1str = "";
	  if (entered_date1 == null) {
	    ed1str = "Date not selected";
	  } else {
	    ed1str = sdf2.format(entered_date1);
	  };
	  String ed2str = "";
	  if (entered_date2 == null) {
	    ed2str = "Date not selected";
	  } else {
	    ed2str = sdf2.format(entered_date2);
	  };


%>

<center>
<b>You chose:</b>
<br>
<table class="ss_style" border="1">
<tr>
<th>Datepicker 1</th>
<th>Datepicker 2</th>
</tr>
<tr>
<td>
<%= ed1str %>
</td>
<td>
<%= ed2str %>
</td>
</tr>

</table>
</center>
<p>

<%
   }
%>

<form class="ss_style ss_form" method="POST" action="" name="timepickertest" onSubmit="return ss_onSubmit(this);">
<span align="center">SiteScape Forum Widget Tester -  Timepicker Widget
<br />
This test page shows timepickers for the current date and an unspecified date. 
</span>
<br>
<br />

	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" align=center border="0" cellpadding="3"  style="border-spacing: 3px;"">
			<tr>
				<td>
					First timepicker:<br>
					<ssf:datepicker id="date1" 
						formName="timepickertest"
						initDate="<%=initDate%>" />
					<ssf:timepicker id="date1"
						formName="timepickertest"
						initDate="<%=initDate%>" />
				</td>
			</tr>
			<tr>
				<td>
					Second timepicker:<br>
					<ssf:datepicker id="date2" 
						formName="timepickertest"
						 />
					<ssf:timepicker id="date2"
						formName="timepickertest"
						/>
			
			</td>
			</table>
		</td>
	</tr>
	</table>
	<br>
	<hr>
	<input type="submit" class="ss_submit" name="OK" value="OK">
</form>














