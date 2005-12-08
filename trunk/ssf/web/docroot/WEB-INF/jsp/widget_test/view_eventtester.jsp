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
<%@ page import="java.util.Calendar" %>
<%@ page import="com.sitescape.ef.domain.Event" %>
<%@ page import="com.sitescape.ef.web.util.EventHelper" %>


<%
    Boolean hasdur = new Boolean("true");
    Boolean hasrecur = new Boolean("true");
    Boolean nodur = new Boolean("false");
    Boolean norecur = new Boolean("false");
    Map formData = request.getParameterMap();
    Event e1 = null;
    Event e2 = null;
    SimpleDateFormat sdf = new SimpleDateFormat();
    String event1start = new String();
    String event1end = new String();
    String event2when = new String();

    if (formData.containsKey("OK")) {
      e1 = EventHelper.getEventFromMap(formData, "eventtestertest", "et");
      e2 = EventHelper.getEventFromMap(formData, "eventtestertest", "et2", nodur, hasrecur);
 
      if (e1 == null) {
	event1start = "Nothing selected";
	event1end = "";
      } else {
	Calendar cal1start  = e1.getDtStart();
	Calendar cal1end = e1.getDtEnd();
	event1start = sdf.format(cal1start.getTime());
	event1end = "End: " + sdf.format(cal1end.getTime());
      }
      if (e2 == null) {
	event2when = "Nothing selected";
      } else {
	Calendar cal2when = e2.getDtStart();
	event2when = sdf.format(cal2when.getTime());
      }
    %>

<center>
<b>You chose:</b>
<br>
<table border="1">
<tr>
<td align="center">First event </td></tr>
<tr>
<td>
<%
    if (e1 == null) {
%>
Nothing selected
<%
    } else {
%>
<ssf:eventtext event="<%=e1%>" />
<% 
    }
%>
</td>
</tr>
<p>

<tr>
<td align="center">Second event </td></tr>
<tr>
<td>
<%
    if (e2 == null) {
%>
Nothing selected
<%
    } else {
%>
<ssf:eventtext event="<%=e2%>" />
<% 
    }
%>
</td>
</tr>
</table>

</center>
<p>
<%
    }
%>

<form method="POST" action="" name="eventtestertest" onSubmit="return ssf_onSubmit(this);">
<span align="center">SiteScape Forum Event tester
<br />
</span>
<center>
<table border="0">
<tr><td>
<br />
This one has a duration
</td></tr>
<tr><td>
<ssf:eventeditor id="et" 
    formName="eventtestertest" 
    initEvent="<%= e1 %>"
    hasDuration="<%= hasdur %>"
    />
</td></tr>
<tr><td>
<br />This one has just a date/time, no duration, no recurrence</p><p></p>
</td></tr>
<tr><td>
<ssf:eventeditor id="et2"
    formName="eventtestertest"
    initEvent="<%= e2 %>"
    hasDuration="<% nodur %>"
    />
</td></tr>
</table>
	<br>
	<hr>
	<input type="submit" name="OK" value="OK">
</center>
</form>

