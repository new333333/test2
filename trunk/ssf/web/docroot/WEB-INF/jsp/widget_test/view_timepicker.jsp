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
			<table class="ss_style" align=center border="0" cellpadding="3" cellspacing="3">
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














