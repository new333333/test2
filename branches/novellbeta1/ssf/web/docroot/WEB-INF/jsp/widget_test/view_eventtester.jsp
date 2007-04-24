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
<%@ page import="java.util.Calendar" %>
<%@ page import="com.sitescape.team.domain.Event" %>
<%@ page import="com.sitescape.team.web.util.EventHelper" %>


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
      e1 = EventHelper.getEventFromMap(formData, "et");
      e2 = EventHelper.getEventFromMap(formData, "et2", nodur, hasrecur);
 
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
<table class="ss_style" border="1">
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

<form class="ss_style ss_form" method="POST" action="" name="eventtestertest" onSubmit="return ss_onSubmit(this);">
<span align="center">SiteScape Forum Event tester
<br />
</span>
<br />
This one has a duration
<ssf:eventeditor id="et" 
    formName="eventtestertest" 
    initEvent="<%= e1 %>"
    hasDuration="<%= hasdur %>"
    />
<br />This one has just a date/time, no duration, no recurrence</p><p></p>
<ssf:eventeditor id="et2"
    formName="eventtestertest"
    initEvent="<%= e2 %>"
    hasDuration="<% nodur %>"
    />
	<br>
	<hr>
	<input type="submit" class="ss_submit" name="OK" value="OK">
</form>

