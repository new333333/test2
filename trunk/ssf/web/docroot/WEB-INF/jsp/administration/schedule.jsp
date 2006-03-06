<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>

<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td valign="top"> 
 
   <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
   <tr>
   <td >
    <input type="radio" name="minuteType" id="minuteType" value="repeat" <c:if test="${schedule.repeatMinutes}">checked</c:if>/>
   <ssf:nlt tag="schedule.repeat"/>
   <input type="text" class="ss_text" name="minutesRepeat" id="minutesRepeat" size="2" value="${schedule.minutesRepeat}"/>
  	&nbsp;<ssf:nlt tag="schedule.minutes"/>
  	<br/>
    <input type="radio" name="minuteType" id="minuteType" value="absolute" <c:if test="${!schedule.repeatMinutes}">checked</c:if>/>
    <ssf:nlt tag="schedule.atminutes"/>
   <input type="text" class="ss_text" name="schedMinutes" id="schedMinutes" size="3" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>/>
   <br/>
<hr shade=noshade size=1/>
    <input type="radio"  name="hourType" id="hourType" value="repeat" <c:if test="${schedule.repeatHours}">checked</c:if>/>
    <ssf:nlt tag="schedule.repeat"/> 
  <input type="text" class="ss_text"  name="hoursRepeat" id="hoursRepeat" size="2" value="${schedule.hoursRepeat}"/>
  &nbsp;<ssf:nlt tag="schedule.hours"/>
  <br/>
   <input type="radio"  name="hourType" id="hourType" value="absolute"   <c:if test="${!schedule.repeatHours}">checked</c:if>/>
   <ssf:nlt tag="schedule.athours"/>
   <input type="text" class="ss_text"  name="schedHours" id="schedHours" size="5" <c:if test="${!schedule.repeatHours}">value="${schedule.hours}"</c:if>/>
   <br/>
<hr shade=noshade size=1/>
   <input type="radio"  name="schedType" id="schedType" value="daily" <c:if test="${schedule.daily}">checked</c:if>/> 
   <ssf:nlt tag="schedule.everyday"/>
   <br/>
   <input type="radio"  name="schedType" id="schedType" value="weekly" <c:if test="${!schedule.daily}">checked</c:if>/> 
   <ssf:nlt tag="schedule.weekly"/>
   <br/>
   <table class="ss_style" border="0" cellpadding="0" cellspacing="2">
	  <tr><td >&nbsp;&nbsp;&nbsp;&nbsp;</td>
	  <td >
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