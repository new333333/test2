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

<form class="ss_style ss_form" method="POST" action="" name="datepickertest" onSubmit="return ssf_onSubmit(this);">
<span align="center">SiteScape Forum Widget Tester -  Datepicker Widget
<br />
This test page shows datepickers for the current date and an unspecified date. 
</span>
<br>
<br />

	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" align=center border="1" cellpadding="3" cellspacing="3">
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
