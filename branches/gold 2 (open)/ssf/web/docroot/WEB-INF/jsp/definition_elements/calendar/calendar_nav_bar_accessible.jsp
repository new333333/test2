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

<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>

<%
Calendar calendarPrevDate = (java.util.Calendar) request.getAttribute("ssPrevDate");
Calendar calendarNextDate = (java.util.Calendar) request.getAttribute("ssNextDate");
Calendar calendarCurrDate = (java.util.Calendar) request.getAttribute("ssCurrDate");
Calendar calendarRangeEndDate = (java.util.Calendar) request.getAttribute("ssRangeEndDate");

Integer gridSize = (java.lang.Integer) request.getAttribute("ssCurrentGridSize");
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
		<c:if test="${ssCurrentGridType == 'month'}">
			<fmt:formatDate value="${ssCurrDateFormat}" pattern="MMMM, yyyy" />
		</c:if>
		<c:if test="${ssCurrentGridType == 'day'}">
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
		 callbackRoutine="ss_getMonthCalendarEvents${prefix}" immediateMode="true" altText="<%= NLT.get("calendar.view.popupAltText") %>"
		 /></div></form>
		 
	<div id="ss_calDivPopup${prefix}" class="ss_calPopupDiv"></div>

</div>