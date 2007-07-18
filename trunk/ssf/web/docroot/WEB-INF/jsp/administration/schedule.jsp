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
		<option value="00">00
		<option value="01">01
		<option value="02">02
		<option value="03">03
		<option value="04">04
		<option value="05">05
		<option value="06">06
		<option value="07">07
		<option value="08">08
		<option value="09">09
		<option value="10">10
		<option value="11">11
		<option value="12">12
		<option value="13">13
		<option value="14">14
		<option value="15">15
		<option value="16">16
		<option value="17">17
		<option value="18">18
		<option value="19">19
		<option value="20">20
		<option value="21">21
		<option value="22">22
		<option value="23">23
	</select>
		:
	<select name="schedMinutes" id="schedMinutes" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>>
		<option value="00">00
		<option value="05">05
		<option value="10">10
		<option value="15">15
		<option value="20">20
		<option value="25">25
		<option value="30">30
		<option value="35">35
		<option value="40">40
		<option value="45">45
		<option value="50">50
		<option value="55">55
	</select>

<br/>


<input type="radio"  name="hourType" id="hourType" value="repeat"   <c:if test="${schedule.repeatHours}">checked</c:if>>
	</input>
   
	<span class="ss_labelRight"><ssf:nlt tag="schedule.repeat"/></span>
   
	<select name="hoursRepeat" id="hoursRepeat"<c:if test="${schedule.repeatHours}">value="${schedule.hours}"</c:if>>
		<option value="01">01
		<option value="02">02
		<option value="03">03
		<option value="04">04
		<option value="06">06
		<option value="08">08
		<option value="12">12
	</select>

<span class="ss_labelRight"><ssf:nlt tag="schedule.hours"/></span>

<br/>
<hr shade=noshade size=1/>
  	<br/>
   </td></tr>
</table>
</table>