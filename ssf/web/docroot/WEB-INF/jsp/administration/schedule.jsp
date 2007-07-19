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
<table class="ss_style" border ="0" cellspacing="0" cellpadding="0" width="100%">
<tr><td valign="top"> 
   <table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="100%">
   <tr>
   <td >
   <hr shade=noshade size=1/>
   <input type="radio"  name="schedType" id="schedType" value="daily" <c:if test="${schedule.daily}">checked</c:if>/> 
   <span class="ss_labelRight"><ssf:nlt tag="schedule.everyday"/></span>
   <br/>
   <input type="radio"  name="schedType" id="schedType" value="weekly" <c:if test="${!schedule.daily}">checked</c:if>/> 
   <span class="ss_labelRight"><ssf:nlt tag="schedule.weekly"/></span>
   <br/>
   <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
	  <tr><td >&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td ><label>
	<input type="checkbox"  name="onday_sun" id="onday_sun"  <c:if test="${schedule.onSunday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.su"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="onday_mon" id="onday_mon" <c:if test="${schedule.onMonday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.mo"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="onday_tue" id="onday_tue" <c:if test="${schedule.onTuesday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.tu"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="onday_wed" id="onday_wed" <c:if test="${schedule.onWednesday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.we"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="onday_thu" id="onday_thu" <c:if test="${schedule.onThursday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.th"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="onday_fri" id="onday_fri" <c:if test="${schedule.onFriday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.fr"/>
	</label></td>
	<td ><label>
	<input type="checkbox"  name="onday_sat" id="onday_sat" <c:if test="${schedule.onSaturday}">checked</c:if>/><br/>
	<ssf:nlt tag="calendar.day.abbrevs.sa"/>
	</label></td>
	</tr>
	</table>
<hr shade=noshade size=1/>

<input type="radio"  name="hourType" id="hourType" value="absolute"   <c:if test="${!schedule.repeatHours}">checked</c:if>>
	</input>
   
	<span class="ss_labelRight"><ssf:nlt tag="schedule.attime"/></span>
   
	<select name="schedHours" id="schedHours"<c:if test="${!schedule.repeatHours}">value="${schedule.hours}"</c:if>>
		<option <c:if test="${ssSchedule.hours == '0'}">selected=selected</c:if> value="00">00
		<option <c:if test="${ssSchedule.hours == '1'}">selected=selected</c:if> value="01">01
		<option <c:if test="${ssSchedule.hours == '2'}">selected=selected</c:if> value="02">02
		<option <c:if test="${ssSchedule.hours == '3'}">selected=selected</c:if> value="03">03
		<option <c:if test="${ssSchedule.hours == '4'}">selected=selected</c:if> value="04">04
		<option <c:if test="${ssSchedule.hours == '5'}">selected=selected</c:if> value="05">05
		<option <c:if test="${ssSchedule.hours == '6'}">selected=selected</c:if> value="06">06
		<option <c:if test="${ssSchedule.hours == '7'}">selected=selected</c:if> value="07">07
		<option <c:if test="${ssSchedule.hours == '8'}">selected=selected</c:if> value="08">08
		<option <c:if test="${ssSchedule.hours == '9'}">selected=selected</c:if> value="09">09
		<option <c:if test="${ssSchedule.hours == '10'}">selected=selected</c:if> value="10">10
		<option <c:if test="${ssSchedule.hours == '11'}">selected=selected</c:if> value="11">11
		<option <c:if test="${ssSchedule.hours == '12'}">selected=selected</c:if> value="12">12
		<option <c:if test="${ssSchedule.hours == '13'}">selected=selected</c:if> value="13">13
		<option <c:if test="${ssSchedule.hours == '14'}">selected=selected</c:if> value="14">14
		<option <c:if test="${ssSchedule.hours == '15'}">selected=selected</c:if> value="15">15
		<option <c:if test="${ssSchedule.hours == '16'}">selected=selected</c:if> value="16">16
		<option <c:if test="${ssSchedule.hours == '17'}">selected=selected</c:if> value="17">17
		<option <c:if test="${ssSchedule.hours == '18'}">selected=selected</c:if> value="18">18
		<option <c:if test="${ssSchedule.hours == '19'}">selected=selected</c:if> value="19">19
		<option <c:if test="${ssSchedule.hours == '20'}">selected=selected</c:if> value="20">20
		<option <c:if test="${ssSchedule.hours == '21'}">selected=selected</c:if> value="21">21
		<option <c:if test="${ssSchedule.hours == '22'}">selected=selected</c:if> value="22">22
		<option <c:if test="${ssSchedule.hours == '23'}">selected=selected</c:if> value="23">23
	</select>
		:
	<select name="schedMinutes" id="schedMinutes" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>>
		<option <c:if test="${ssSchedule.minutes == '0'}">selected=selected</c:if> value="00">00
		<option <c:if test="${ssSchedule.minutes == '5'}">selected=selected</c:if> value="05">05
		<option <c:if test="${ssSchedule.minutes == '10'}">selected=selected</c:if> value="10">10
		<option <c:if test="${ssSchedule.minutes == '15'}">selected=selected</c:if> value="15">15
		<option <c:if test="${ssSchedule.minutes == '20'}">selected=selected</c:if> value="20">20
		<option <c:if test="${ssSchedule.minutes == '25'}">selected=selected</c:if> value="25">25
		<option <c:if test="${ssSchedule.minutes == '30'}">selected=selected</c:if> value="30">30
		<option <c:if test="${ssSchedule.minutes == '35'}">selected=selected</c:if> value="35">35
		<option <c:if test="${ssSchedule.minutes == '40'}">selected=selected</c:if> value="40">40
		<option <c:if test="${ssSchedule.minutes == '45'}">selected=selected</c:if> value="45">45
		<option <c:if test="${ssSchedule.minutes == '50'}">selected=selected</c:if> value="50">50
		<option <c:if test="${ssSchedule.minutes == '55'}">selected=selected</c:if> value="55">55
	</select>

<br/>


<input type="radio"  name="hourType" id="hourType" value="repeat"   <c:if test="${schedule.repeatHours}">checked</c:if>>
	</input>
   
	<span class="ss_labelRight"><ssf:nlt tag="schedule.repeatinterval"/></span>
   
	<select name="hoursRepeat" id="hoursRepeat"<c:if test="${schedule.repeatHours}">value="${schedule.hours}"</c:if>>
		<option <c:if test="${ssSchedule.hoursRepeat == '1'}">selected=selected</c:if> value="01">01
		<option <c:if test="${ssSchedule.hoursRepeat == '2'}">selected=selected</c:if> value="02">02
		<option <c:if test="${ssSchedule.hoursRepeat == '3'}">selected=selected</c:if> value="03">03
		<option <c:if test="${ssSchedule.hoursRepeat == '4'}">selected=selected</c:if> value="04">04
		<option <c:if test="${ssSchedule.hoursRepeat == '6'}">selected=selected</c:if> value="06">06
		<option <c:if test="${ssSchedule.hoursRepeat == '8'}">selected=selected</c:if> value="08">08
		<option <c:if test="${ssSchedule.hoursRepeat == '12'}">selected=selected</c:if> value="12">12
	</select>

<br/>
<hr shade=noshade size=1/>
  	<br/>
   </td></tr>
</table>
</table>