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

<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>

<%
Calendar calendarPrevDate = (java.util.Calendar) request.getAttribute("ssPrevDate");
Calendar calendarNextDate = (java.util.Calendar) request.getAttribute("ssNextDate");
Calendar calendarCurrDate = (java.util.Calendar) request.getAttribute("ssCurrDate");
Calendar calendarRangeEndDate = (java.util.Calendar) request.getAttribute("ssRangeEndDate");

Integer gridSize = (java.lang.Integer) request.getAttribute("ssGridSize");
String strGridSize = "";
if (gridSize != null) {
	strGridSize = gridSize.toString();;
}

String strPrevDay = "" + calendarPrevDate.get(Calendar.DAY_OF_MONTH);
int intPrevMonth = calendarPrevDate.get(Calendar.MONTH) + 1;
String strPrevMonth = "" + intPrevMonth;
String strPrevYear = "" + calendarPrevDate.get(Calendar.YEAR);

String strNextDay = "" + calendarNextDate.get(Calendar.DAY_OF_MONTH);
int intNextMonth = calendarNextDate.get(Calendar.MONTH) + 1;
String strNextMonth = "" + intNextMonth;
String strNextYear = "" + calendarNextDate.get(Calendar.YEAR);

String strCurrDay = "" + calendarCurrDate.get(Calendar.DAY_OF_MONTH);
int intCurrMonth = calendarCurrDate.get(Calendar.MONTH) + 1;
String strCurrMonth = "" + intCurrMonth;
String strCurrYear = "" + calendarCurrDate.get(Calendar.YEAR);

Date currDate = calendarCurrDate.getTime();
Date nextDate = calendarNextDate.getTime();
Date rangeEndDate = calendarRangeEndDate.getTime();
%>

<c:set var="ssGridSize" value="<%= strGridSize %>" />
<c:set var="ssCurrDateFormat" value="<%= currDate %>" />
<c:set var="ssNextDateFormat" value="<%= nextDate %>" />
<c:set var="ssRangeEndDateFormat" value="<%= rangeEndDate %>" />

<div class="ss_clear"></div>
<div class="ss_calendarNaviBarAccessible">

	&nbsp;

	<a class="ss_calDaySelectButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strCurrDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strCurrDay %>" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="day" />
			<ssf:param name="ssGridSize" value="1" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.view1Day"/> title="<ssf:nlt tag="alt.view1Day"/>" 
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<a class="ss_cal3DaysSelectButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strCurrDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strCurrDay %>" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="day" />
			<ssf:param name="ssGridSize" value="3" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.view3Days"/> title="<ssf:nlt tag="alt.view3Days"/>" 
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<a class="ss_cal5DaysSelectButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strCurrDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strCurrDay %>" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="day" />
			<ssf:param name="ssGridSize" value="5" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.view5Days"/> title="<ssf:nlt tag="alt.view5Days"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<a class="ss_cal7DaysSelectButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strCurrDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strCurrDay %>" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="day" />
			<ssf:param name="ssGridSize" value="7" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.view1Week"/> title="<ssf:nlt tag="alt.view1Week"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<a class="ss_cal14DaysSelectButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strCurrDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strCurrDay %>" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="day" />
			<ssf:param name="ssGridSize" value="14" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.view2Weeks"/> title="<ssf:nlt tag="alt.view2Weeks"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<a class="ss_calMonthSelectButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strCurrDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strCurrDay %>" />
			<ssf:param name="month" value="<%= strCurrMonth %>" />
			<ssf:param name="year" value="<%= strCurrYear %>" />
			<ssf:param name="ssGridType" value="month" />
			<ssf:param name="ssGridSize" value="1" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.view1Month"/> title="<ssf:nlt tag="alt.view1Month"/>" 
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<a class="ss_calDateDownButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strPrevDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strPrevDay %>" />
			<ssf:param name="month" value="<%= strPrevMonth %>" />
			<ssf:param name="year" value="<%= strPrevYear %>" />
		  </ssf:url>" >
		<img <ssf:alt tag="alt.viewCalPrev"/> title="<ssf:nlt tag="alt.viewCalPrev"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;
	
	<span>
		&nbsp;&nbsp;
		<c:if test="${ssGridType == 'month'}">
			<fmt:formatDate value="${ssCurrDateFormat}" pattern="MMMM, yyyy" />
		</c:if>
		<c:if test="${ssGridType == 'day'}">
			<c:choose>
				<c:when test="${ssGridSize == '1' || ssGridSize == '' || ssGridSize == '-1'}">
					<fmt:formatDate value="${ssCurrDateFormat}" pattern="d MMM yyyy" />		
				</c:when>
				<c:otherwise>
					<fmt:formatDate value="${ssCurrDateFormat}" pattern="d MMM yyyy" />&nbsp;-&nbsp;
					<fmt:formatDate value="${ssRangeEndDateFormat}" pattern="d MMM yyyy" />
				</c:otherwise>
			</c:choose>
		</c:if>
		&nbsp;&nbsp;
	</span>
	
	&nbsp;
	
	<a class="ss_calDateUpButton" href="<ssf:url portletName="ss_forum" folderId="${ssBinder.id}" action="view_folder_listing" actionUrl="true">
			<ssf:param name="day" value="<%= strNextDay %>" />
			<ssf:param name="dayOfMonth" value="<%= strNextDay %>" />
			<ssf:param name="month" value="<%= strNextMonth %>" />
			<ssf:param name="year" value="<%= strNextYear %>" />
		  </ssf:url>">
		<img <ssf:alt tag="alt.viewCalNext"/> title="<ssf:nlt tag="alt.viewCalNext"/>"
		src="<html:imagesPath/>pics/1pix.gif" />
	</a>
	
	&nbsp;&nbsp;
	
	<form name="ssCalNavBar${prefix}" id="ssCalNavBar${prefix}" action="${goto_form_url}" 
	  class="ss_toolbar_color"
	  method="post" style="display:inline;"><div class="ss_toolbar_color" style="display:inline;">
		<ssf:datepicker formName="ssCalNavBar${prefix}" showSelectors="true" 
		 popupDivId="ss_calDivPopup${prefix}" id="ss_goto${prefix}" initDate="${ssCurrentDate}"
		 callbackRoutine="ss_getMonthCalendarEvents${prefix}" immediateMode="true" 
		 altText='<%= NLT.get("calendar.view.popupAltText") %>'
		 /></div></form>
		 
	<div id="ss_calDivPopup${prefix}" class="ss_calPopupDiv"></div>

</div>