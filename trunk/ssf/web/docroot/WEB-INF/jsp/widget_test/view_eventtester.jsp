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
<%@ page import="java.util.Calendar" %>
<%@ page import="org.kablink.teaming.domain.Event" %>
<%@ page import="org.kablink.teaming.web.util.EventHelper" %>


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
<sec:csrfInput />
</form>

