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

<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td valign="top"> 
 
   <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
   <tr>
   <td >
    <input type="radio" name="minuteType" id="minuteType" value="repeat" <c:if test="${schedule.repeatMinutes}">checked</c:if>/>
   <span class="ss_labelRight"><ssf:nlt tag="schedule.repeat"/></span>
   <input type="text" class="ss_text" name="minutesRepeat" id="minutesRepeat" size="2" value="${schedule.minutesRepeat}"/>
  	&nbsp;<span class="ss_bold"><ssf:nlt tag="schedule.minutes"/> <ssf:inlineHelp tag="ihelp.schedule.repeat_minutes"/></span>
  	<br/>
    <input type="radio" name="minuteType" id="minuteType" value="absolute" <c:if test="${!schedule.repeatMinutes}">checked</c:if>/>
    <span class="ss_labelRight"><ssf:nlt tag="schedule.atminutes"/> </span>
   <input type="text" class="ss_text" name="schedMinutes" id="schedMinutes" size="3" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>/> <ssf:inlineHelp tag="ihelp.schedule.at_minutes"/>
   <br/>
<hr shade=noshade size=1/>
    <input type="radio"  name="hourType" id="hourType" value="repeat" <c:if test="${schedule.repeatHours}">checked</c:if>/>
    <span class="ss_labelRight"><ssf:nlt tag="schedule.repeat"/></span>
  <input type="text" class="ss_text"  name="hoursRepeat" id="hoursRepeat" size="2" value="${schedule.hoursRepeat}"/>
  &nbsp;<span class="ss_bold"><ssf:nlt tag="schedule.hours"/>  <ssf:inlineHelp tag="ihelp.schedule.repeat_hours"/></span>
  <br/>
   <input type="radio"  name="hourType" id="hourType" value="absolute"   <c:if test="${!schedule.repeatHours}">checked</c:if>/>
   <span class="ss_labelRight"><ssf:nlt tag="schedule.athours"/></span>
   <input type="text" class="ss_text"  name="schedHours" id="schedHours" size="5" <c:if test="${!schedule.repeatHours}">value="${schedule.hours}"</c:if>/>  <ssf:inlineHelp tag="ihelp.schedule.at_hours"/>
   <br/>
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
	</table></td></tr>
</table>
</table>