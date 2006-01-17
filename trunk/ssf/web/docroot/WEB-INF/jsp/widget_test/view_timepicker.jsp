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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sitescape.ef.web.util.DateHelper" %>
<jsp:useBean id="formData" type="java.util.Map" scope="request" />


<%
	Date initDate = (Date)request.getAttribute("initDate");
	Date initDate2 = (Date)request.getAttribute("initDate2");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, MMMM dd, yyyy kk:mm");

        if (formData.containsKey("OK")) {

	  String ed1hidden = "foo";
	  Date entered_date1 = DateHelper.getDateFromMap(formData, 
							 "timepickertest",
							 "date1");
	  Date entered_date2 = DateHelper.getDateFromMap(formData, 
							 "timepickertest",
							 "date2");
							 
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

<form class="ss_style" method="POST" action="" name="timepickertest" onSubmit="return ssf_onSubmit(this);">
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
	<input type="submit" name="OK" value="OK">
</form>














