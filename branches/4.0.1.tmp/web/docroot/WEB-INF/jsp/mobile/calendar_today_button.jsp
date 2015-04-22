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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
 
<%
Calendar calendarPrevDate = (java.util.Calendar) request.getAttribute("ssPrevDate");
Calendar calendarNextDate = (java.util.Calendar) request.getAttribute("ssNextDate");
Calendar calendarCurrDate = (java.util.Calendar) request.getAttribute("ssCurrDate");

User user = (User) request.getAttribute("ssUser");
TimeZone timeZone = user.getTimeZone();
Calendar calendarTodayDate = new GregorianCalendar(timeZone);

Integer gridSize = (java.lang.Integer) request.getAttribute("ssGridSize");
String strGridSize = "";
if (gridSize != null) {
	strGridSize = gridSize.toString();;
}

String strPrevDay = "" + calendarPrevDate.get(Calendar.DAY_OF_MONTH);
int intPrevMonth = calendarCurrDate.get(Calendar.MONTH) ;
String strPrevMonth = "" + intPrevMonth;
String strPrevYear = "" + calendarPrevDate.get(Calendar.YEAR);

String strNextDay = "" + calendarNextDate.get(Calendar.DAY_OF_MONTH);
int intNextMonth = calendarCurrDate.get(Calendar.MONTH) + 2;
String strNextMonth = "" + intNextMonth;
String strNextYear = "" + calendarNextDate.get(Calendar.YEAR);

int intCurrDay = calendarCurrDate.get(Calendar.DAY_OF_MONTH);
String strCurrDay = "" + calendarCurrDate.get(Calendar.DAY_OF_MONTH);
int intCurrMonth = calendarCurrDate.get(Calendar.MONTH) + 1;
String strCurrMonth = "" + intCurrMonth;
String strCurrYear = "" + calendarCurrDate.get(Calendar.YEAR);
int intCurrYear = calendarCurrDate.get(Calendar.YEAR);

String[] monthNames = new String[] {"calendar.january", "calendar.february", "calendar.march", "calendar.april", "calendar.may", "calendar.june", "calendar.july", "calendar.august", "calendar.september", "calendar.october", "calendar.november", "calendar.december"};
String strMonthName = NLT.get(monthNames[intCurrMonth - 1]);	

String strTodayDay = "" + calendarTodayDate.get(Calendar.DAY_OF_MONTH);
int intTodayDay = calendarTodayDate.get(Calendar.DAY_OF_MONTH);
int intTodayMonth = calendarTodayDate.get(Calendar.MONTH) + 1;
String strTodayMonth = "" + intTodayMonth;
String strTodayYear = "" + calendarTodayDate.get(Calendar.YEAR);

Date currDate = calendarCurrDate.getTime();
Date todayDate = calendarTodayDate.getTime();
Date nextDate = calendarNextDate.getTime();
%>

<c:set var="ssCurrDateFormat" value="<%= currDate %>" />
<c:set var="currMonthDate"><fmt:formatDate value="<%= currDate %>" pattern="MMM" /></c:set>
<c:set var="todayMonthDate"><fmt:formatDate value="<%= todayDate %>" pattern="MMM" /></c:set>

<%
 GregorianCalendar cal = new GregorianCalendar (intCurrYear, calendarCurrDate.get(Calendar.MONTH), 1); 
 int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
 int weekStartDay=cal.get(Calendar.DAY_OF_WEEK);
 
 cal = new GregorianCalendar (intCurrYear, calendarCurrDate.get(Calendar.MONTH), days); 
 int iTotalweeks=cal.get(Calendar.WEEK_OF_MONTH);
%>

	<a class="actions-a" href="<ssf:url adapter="true" portletName="ss_forum" 
          folderId="${ssBinder.id}" action="__ajax_mobile" 
          operation="mobile_show_folder" actionUrl="false">
			<ssf:param name="day" value="<%= strTodayDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strTodayDay %>" />
			<ssf:param name="month" value="<%= strTodayMonth %>" />
			<ssf:param name="year" value="<%= strTodayYear %>" />
			<ssf:param name="ssGridType" value="day" />
			<ssf:param name="ssGridSize" value="1" />
		  </ssf:url>"><img class="actionbar-img" src="<html:rootPath/>css/images/mobile/today_50.png" border="0" align="absmiddle"/></a>
