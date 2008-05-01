<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="java.util.TimeZone" %>
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
&nbsp;${schedule.hours}:${schedule.minutes}&nbsp;<%= TimeZone.getDefault().getID() %>
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
   <hr shade=noshade size=1/>
   <input type="radio"  name="${schedPrefix}schedType" id="${schedPrefix}schedType" value="daily" <c:if test="${schedule.daily}">checked</c:if>/> 
   <span class="ss_labelRight"><ssf:nlt tag="schedule.everyday"/></span>
   <br/>
   <input type="radio"  name="${schedPrefix}schedType" id="${schedPrefix}schedType" value="weekly" <c:if test="${!schedule.daily}">checked</c:if>/> 
   <span class="ss_labelRight"><ssf:nlt tag="schedule.weekly"/>&nbsp;<ssf:nlt tag="schedule.weeklyWhen"/></span>
   <br/>
   <table class="ss_style" border="0" cellpadding="0" style="border-spacing: 2px;">
	  <tr><td >&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_sun" id="${schedPrefix}onday_sun"  <c:if test="${schedule.onSunday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.su"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_mon" id="${schedPrefix}onday_mon" <c:if test="${schedule.onMonday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.mo"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_tue" id="${schedPrefix}onday_tue" <c:if test="${schedule.onTuesday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.tu"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_wed" id="${schedPrefix}onday_wed" <c:if test="${schedule.onWednesday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.we"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_thu" id="${schedPrefix}onday_thu" <c:if test="${schedule.onThursday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.th"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_fri" id="${schedPrefix}onday_fri" <c:if test="${schedule.onFriday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.fr"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="${schedPrefix}onday_sat" id="${schedPrefix}onday_sat" <c:if test="${schedule.onSaturday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.sa"/>
	</label></td>
	</tr>
	</table>
<hr shade=noshade size=1/>

<input type="radio"  name="${schedPrefix}hourType" id="${schedPrefix}hourType" value="absolute"   <c:if test="${!schedule.repeatHours}">checked</c:if>>
	</input>
   
	<span class="ss_labelRight"><ssf:nlt tag="schedule.attime"/></span>
   
	<select name="${schedPrefix}schedHours" id="${schedPrefix}schedHours"<c:if test="${!schedule.repeatHours}">value="${schedule.hours}"</c:if>>
		<option <c:if test="${schedule.hours == '0'}">selected="selected"</c:if> value="00">00
		<option <c:if test="${schedule.hours == '1'}">selected="selected"</c:if> value="01">01
		<option <c:if test="${schedule.hours == '2'}">selected="selected"</c:if> value="02">02
		<option <c:if test="${schedule.hours == '3'}">selected="selected"</c:if> value="03">03
		<option <c:if test="${schedule.hours == '4'}">selected="selected"</c:if> value="04">04
		<option <c:if test="${schedule.hours == '5'}">selected="selected"</c:if> value="05">05
		<option <c:if test="${schedule.hours == '6'}">selected="selected"</c:if> value="06">06
		<option <c:if test="${schedule.hours == '7'}">selected="selected"</c:if> value="07">07
		<option <c:if test="${schedule.hours == '8'}">selected="selected"</c:if> value="08">08
		<option <c:if test="${schedule.hours == '9'}">selected="selected"</c:if> value="09">09
		<option <c:if test="${schedule.hours == '10'}">selected="selected"</c:if> value="10">10
		<option <c:if test="${schedule.hours == '11'}">selected="selected"</c:if> value="11">11
		<option <c:if test="${schedule.hours == '12'}">selected="selected"</c:if> value="12">12
		<option <c:if test="${schedule.hours == '13'}">selected="selected"</c:if> value="13">13
		<option <c:if test="${schedule.hours == '14'}">selected="selected"</c:if> value="14">14
		<option <c:if test="${schedule.hours == '15'}">selected="selected"</c:if> value="15">15
		<option <c:if test="${schedule.hours == '16'}">selected="selected"</c:if> value="16">16
		<option <c:if test="${schedule.hours == '17'}">selected="selected"</c:if> value="17">17
		<option <c:if test="${schedule.hours == '18'}">selected="selected"</c:if> value="18">18
		<option <c:if test="${schedule.hours == '19'}">selected="selected"</c:if> value="19">19
		<option <c:if test="${schedule.hours == '20'}">selected="selected"</c:if> value="20">20
		<option <c:if test="${schedule.hours == '21'}">selected="selected"</c:if> value="21">21
		<option <c:if test="${schedule.hours == '22'}">selected="selected"</c:if> value="22">22
		<option <c:if test="${schedule.hours == '23'}">selected="selected"</c:if> value="23">23
	</select>
		:
	<select name="${schedPrefix}schedMinutes" id="${schedPrefix}schedMinutes" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>>
		<option <c:if test="${schedule.minutes == '0'}">selected="selected"</c:if> value="00">00
		<option <c:if test="${schedule.minutes == '5'}">selected="selected"</c:if> value="05">05
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
&nbsp;<span class="ss_bold"><%= TimeZone.getDefault().getID() %></span>
<br/>


<input type="radio"  name="${schedPrefix}hourType" id="${schedPrefix}hourType" value="repeat"   
  <c:if test="${schedule.repeatHours || schedule.repeatMinutes}">checked</c:if>>
</input>
   
	<span class="ss_labelRight">
	<ssf:nlt tag="schedule.repeathours">
   	<ssf:param name="value" useBody="true">

	<select name="${schedPrefix}hoursRepeat" id="${schedPrefix}hoursRepeat" 
	  <c:if test="${schedule.minutesRepeat && schedule.minutesRepeat == '15'}">value="0.25"</c:if>
	  <c:if test="${schedule.minutesRepeat && schedule.minutesRepeat == '30'}">value="0.5"</c:if>
	  <c:if test="${schedule.minutesRepeat && schedule.minutesRepeat == '45'}">value="0.75"</c:if>
	  <c:if test="${schedule.repeatHours}">value="${schedule.hours}"</c:if>
	>
		<option <c:if test="${schedule.minutesRepeat == '15'}">selected="selected"</c:if> value="0.25">0.25
		<option <c:if test="${schedule.minutesRepeat == '30'}">selected="selected"</c:if> value="0.5">0.5
		<option <c:if test="${schedule.minutesRepeat == '45'}">selected="selected"</c:if> value="0.75">0.75
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

<br/>
<hr shade=noshade size=1/>
  	<br/>
   </td></tr>
</table>
</table>
</c:if>