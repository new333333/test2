<%
/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.DecimalFormatSymbols" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%
	User ssUser = (User) request.getAttribute("ssUser");
	Date now = new Date();
	TimeZone tz = ssUser.getTimeZone();
	int offset = tz.getOffset(now.getTime());
	int offsetHour = offset / (1000*60*60);
	
	DecimalFormat df = ((DecimalFormat) DecimalFormat.getInstance(ssUser.getLocale()));
	DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
	char decimalChar = dfs.getDecimalSeparator();
%>
<c:set var="offsetHour" value="<%= offsetHour %>"/>

<script type="text/javascript">
/**
 * This function gets called when the "Repeat Every" select control gets the focus.
 * We will select the radio button association with this control.
 */
function handleRepeatEveryOnFocus()
{
	var radioBtn;

	radioBtn = document.getElementById( 'repeat' );
	radioBtn.checked = true;
}// end handleRepeatEveryOnFocus()
</script>

<c:if test="${scheduleStringOnly}">
<c:if test="${schedule.daily}">
<ssf:nlt tag="schedule.everyday"/>
</c:if>
<c:if test="${!schedule.daily}">
<ssf:nlt tag="schedule.weekly"/>&nbsp;(
<c:if test="${schedule.onSunday}"><ssf:nlt tag="calendar.day.abbrevs.su"/>&nbsp;</c:if>
<c:if test="${schedule.onMonday}"><ssf:nlt tag="calendar.day.abbrevs.mo"/>&nbsp;</c:if>
<c:if test="${schedule.onTuesday}"><ssf:nlt tag="calendar.day.abbrevs.tu"/>&nbsp;</c:if>
<c:if test="${schedule.onWednesday}"><ssf:nlt tag="calendar.day.abbrevs.we"/>&nbsp;</c:if>
<c:if test="${schedule.onThursday}"><ssf:nlt tag="calendar.day.abbrevs.th"/>&nbsp;</c:if>
<c:if test="${schedule.onFriday}"><ssf:nlt tag="calendar.day.abbrevs.fr"/>&nbsp;</c:if>
<c:if test="${schedule.onSaturday}"><ssf:nlt tag="calendar.day.abbrevs.sa"/>&nbsp;</c:if>
)</c:if>
,&nbsp;
<c:if test="${!schedule.repeatMinutes && !schedule.repeatHours}">
  <span id='${schedPrefix}userTime'>${(schedule.hours + offsetHour +24) % 24}:${schedule.minutes} <fmt:formatDate value="<%= now %>" 
					    pattern="z" timeZone="${ssUser.timeZone.ID}"/></span>
</c:if>
<c:if test="${schedule.repeatMinutes}">
	<ssf:nlt tag="schedule.repeathours">
   	<ssf:param name="value" useBody="true">
	  <c:if test="${schedule.minutesRepeat == '15'}">0.25</c:if>
	  <c:if test="${schedule.minutesRepeat == '30'}">0.5</c:if>
	  <c:if test="${schedule.minutesRepeat == '45'}">0.75</c:if>
	  </ssf:param>
	  </ssf:nlt>
</c:if>
<c:if test="${schedule.repeatHours && !schedule.repeatMinutes}">
	<ssf:nlt tag="schedule.repeathours">
   	<ssf:param name="value" value="${schedule.hoursRepeat}"/>
	  </ssf:nlt>
	
</c:if>

</c:if>

<c:if test="${!scheduleStringOnly}">
<table class="ss_style" border ="0" cellspacing="0" cellpadding="0" width="100%">
<tr><td valign="top"> 
   <table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr>
   <td >
	<div class="marginleft2 margintop2">
   <input type="radio"  name="${schedPrefix}schedType" id="daily" value="daily" <c:if test="${schedule.daily}">checked</c:if>/> 
   <label for="daily"><span class="ss_labelRight"><ssf:nlt tag="schedule.everyday"/></span></label>
   </div>
   	<div class="marginleft2 margintop2">
   <input type="radio"  name="${schedPrefix}schedType" id="weekly" value="weekly" <c:if test="${!schedule.daily}">checked</c:if>/> 
   <label for="weekly"><span class="ss_labelRight"><ssf:nlt tag="schedule.weekly"/></span>&nbsp;<span class="ss_normal"><ssf:nlt tag="schedule.weeklyWhen"/></span></label>
   </div>
   <table class="ss_style marginleft4" border="0" cellpadding="4">
	  <tr>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_sun" id="${schedPrefix}onday_sun"  <c:if test="${schedule.onSunday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.su"/></div>
	</label></td>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_mon" id="${schedPrefix}onday_mon" <c:if test="${schedule.onMonday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.mo"/></div>
	</label></td>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_tue" id="${schedPrefix}onday_tue" <c:if test="${schedule.onTuesday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.tu"/></div>
	</label></td>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_wed" id="${schedPrefix}onday_wed" <c:if test="${schedule.onWednesday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.we"/></div>
	</label></td>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_thu" id="${schedPrefix}onday_thu" <c:if test="${schedule.onThursday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.th"/></div>
	</label></td>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_fri" id="${schedPrefix}onday_fri" <c:if test="${schedule.onFriday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.fr"/></div>
	</label></td>
	<td align="center"><label>
	<input type="checkbox"  name="${schedPrefix}onday_sat" id="${schedPrefix}onday_sat" <c:if test="${schedule.onSaturday}">checked</c:if>/>
	<div><ssf:nlt tag="calendar.day.abbrevs.sa"/></div>
	</label></td>
	</tr>
	</table>

	<div class="marginleft2 margintop2" style="border-top: 1px solid #cccccc; padding-top: 10px;">
		<input type="radio"  name="${schedPrefix}hourType" id="absolute" value="absolute"   <c:if test="${!schedule.repeatHours}">checked</c:if>/>   
		<label for="absolute"><span class="ss_labelRight"><ssf:nlt tag="schedule.attime"/></span></label>
	
	   <label for="${schedPrefix}schedHours"><span style="display:none;"><ssf:nlt tag="label.selectHours"/></span></label>
		<select name="${schedPrefix}schedHours" id="${schedPrefix}schedHours" <c:if test="${!schedule.repeatHours}">value="${schedule.hours}"</c:if>>
			
	<%  for (int i = 0; i < 24; i++) {
			int hour = i - offsetHour;
	%>
			<c:set var="schedHour"><%= (hour % 24) %></c:set>
	    	<option <c:if test="${schedule.hours == schedHour}">selected="selected"</c:if> 
	    	    value="<fmt:formatNumber type="number" minIntegerDigits="2" value="<%= (hour % 24) %>"/>"
	    	><fmt:formatNumber type="number" minIntegerDigits="2" value="<%= (i % 24) %>"/></option>
	<%  }  
	%>
	
		</select>
		<span> : </span>
		<select name="${schedPrefix}schedMinutes" id="${schedPrefix}schedMinutes" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>>
			<option <c:if test="${schedule.minutes == '0' || schedule.minutes == '00'}">selected="selected"</c:if> value="00">00
			<option <c:if test="${schedule.minutes == '5' || schedule.minutes == '05'}">selected="selected"</c:if> value="05">05
			<option <c:if test="${schedule.minutes == '10'}">selected="selected"</c:if> value="10">10
			<option <c:if test="${schedule.minutes == '15'}">selected="selected"</c:if> value="15">15
			<option <c:if test="${schedule.minutes == '20'}">selected="selected"</c:if> value="20">20
			<option <c:if test="${schedule.minutes == '25'}">selected="selected"</c:if> value="25">25
			<option <c:if test="${schedule.minutes == '30'}">selected="selected"</c:if> value="30">30
			<option <c:if test="${schedule.minutes == '35'}">selected="selected"</c:if> value="35">35
			<option <c:if test="${schedule.minutes == '40'}">selected="selected"</c:if> value="40">40
			<option <c:if test="${schedule.minutes == '45'}">selected="selected"</c:if> value="45">45
			<option <c:if test="${schedule.minutes == '50'}">selected="selected"</c:if> value="50">50
			<option <c:if test="${schedule.minutes == '55'}">selected="selected"</c:if> value="55">55
		</select>


	<label for="${schedPrefix}schedMinutes">
	  <span style="display:none;"><ssf:nlt tag="label.selectMinutes"/></span>
	</label>&nbsp;<span class="ss_bold"><fmt:formatDate value="<%= now %>" 
					    pattern="z" timeZone="${ssUser.timeZone.ID}"/></span>
	
</div>

	<div class="marginleft2 margintop2"">
		<input type="radio"  name="${schedPrefix}hourType" id="repeat" value="repeat"   
		  <c:if test="${schedule.repeatHours || schedule.repeatMinutes}">checked</c:if>/>
		   
		  <label for="hoursRepeat">
			<span class="ss_labelRight">
				<ssf:nlt tag="schedule.repeathours">
					<ssf:param name="value" useBody="true">
		
						<label for="${schedPrefix}hoursRepeat">
							<span style="display:none;"><ssf:nlt tag="label.selectRepeat"/></span>
						</label>
			
						<select name="${schedPrefix}hoursRepeat" id="${schedPrefix}hoursRepeat" onfocus="handleRepeatEveryOnFocus();"
						  <c:if test="${schedule.minutesRepeat && schedule.minutesRepeat == '15'}">value="0.25"</c:if>
						  <c:if test="${schedule.minutesRepeat && schedule.minutesRepeat == '30'}">value="0.5"</c:if>
						  <c:if test="${schedule.minutesRepeat && schedule.minutesRepeat == '45'}">value="0.75"</c:if>
						  <c:if test="${schedule.repeatHours}">value="${schedule.hours}"</c:if>
						>
							<option <c:if test="${schedule.minutesRepeat == '15'}">selected="selected"</c:if> value="0.25">0<%= decimalChar %>25
							<option <c:if test="${schedule.minutesRepeat == '30'}">selected="selected"</c:if> value="0.5">0<%= decimalChar %>5
							<option <c:if test="${schedule.minutesRepeat == '45'}">selected="selected"</c:if> value="0.75">0<%= decimalChar %>75
							<option <c:if test="${schedule.hoursRepeat == '1'}">selected="selected"</c:if> value="01">1
							<option <c:if test="${schedule.hoursRepeat == '2'}">selected="selected"</c:if> value="02">2
							<option <c:if test="${schedule.hoursRepeat == '3'}">selected="selected"</c:if> value="03">3
							<option <c:if test="${schedule.hoursRepeat == '4'}">selected="selected"</c:if> value="04">4
							<option <c:if test="${schedule.hoursRepeat == '6'}">selected="selected"</c:if> value="06">6
							<option <c:if test="${schedule.hoursRepeat == '8'}">selected="selected"</c:if> value="08">8
							<option <c:if test="${schedule.hoursRepeat == '12'}">selected="selected"</c:if> value="12">12
						</select>
					</ssf:param>
				</ssf:nlt>
			</span>
		  </label>
		
	</div>

   </td></tr>
</table>
</table>
</c:if>
