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
<table border ="0" cellspacing="0" cellpadding="3">
<tr><td class="content" valign="top"> 
 
   <table border="0" cellpadding="0" cellspacing="2">
   <tr>
   <td class="content">
   
    <input type="radio" class="content" name="minuteType" id="minuteType" value="repeat" <c:if test="${schedule.repeatMinutes}">checked</c:if>> <span class="content"><label for="minuteType">Repeat every</label></span></input>
   <input type="text" class="content" name="minutesRepeat" id="minutesRepeat" size="2" value="${schedule.minutesRepeat}">&nbsp;minutes<br>
    <input type="radio" class="content" name="minuteType" id="minuteType" value="absolute"   <c:if test="${!schedule.repeatMinutes}">checked</c:if> > <span class="content"><label for="minuteType">At minutes(s)</label></span></input> 
   <input type="text" class="content" name="schedMinutes" id="schedMinutes" size="5" <c:if test="${!schedule.repeatMinutes}">value="${schedule.minutes}"</c:if>><br>
 	<br/>
<hr shade=noshade size=1/>
    <input type="radio" class="content" name="hourType" id="hourType" value="repeat" <c:if test="${schedule.repeatHours}">checked</c:if>> <span class="content"><label for="hourType">Repeat every</label></span></input> 
  <input type="text" class="content" name="hoursRepeat" id="hoursRepeat" size="2" value="${schedule.hoursRepeat}">&nbsp;hours<br>
    <input type="radio" class="content" name="hourType" id="hourType" value="absolute"   <c:if test="${!schedule.repeatHours}">checked</c:if> > <span class="content"><label for="hourType">At hour(s)</label></span></input> 
   <input type="text" class="content" name="schedHours" id="schedHours" size="5" <c:if test="${!schedule.repeatHours}">value="${schedule.hours}"</c:if>><br>
 	<br/>
<hr shade=noshade size=1/>
   <input type="radio" class="content" name="schedType" id="schedType" value="daily" <c:if test="${schedule.daily}">checked</c:if>> <span class="content"><label for="schedType">every day</label></span></input> <br>
   <input type="radio" class="content" name="schedType" id="schedType" value="weekly"   <c:if test="${!schedule.daily}">checked</c:if> > <span class="content"><label for="schedType">weekly (on the days selected below)</label></span></input> <br>
   <table border="0" cellpadding="0" cellspacing="2">
	  <tr><td class="content">&nbsp;&nbsp;&nbsp;&nbsp;</td>
	  <td class="content">
	<input type="checkbox" class="content" name="onday_sun" id="onday_sun"  <c:if test="${schedule.onSunday}">checked</c:if>> <span class="content"><label for="onday_sun">Sun</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_mon" id="onday_mon" <c:if test="${schedule.onMonday}">checked</c:if>> <span class="content"><label for="onday_mon">Mon</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_tue" id="onday_tue" <c:if test="${schedule.onTuesday}">checked</c:if>> <span class="content"><label for="onday_tue">Tue</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_wed" id="onday_wed" <c:if test="${schedule.onWednesday}">checked</c:if>> <span class="content"><label for="onday_wed">Wed</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_thu" id="onday_thu" <c:if test="${schedule.onThursday}">checked</c:if>> <span class="content"><label for="onday_thu">Thu</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_fri" id="onday_fri" <c:if test="${schedule.onFriday}">checked</c:if>> <span class="content"><label for="onday_fri">Fri</label></span></input> &nbsp;&nbsp;
	<input type="checkbox" class="content" name="onday_sat" id="onday_sat" <c:if test="${schedule.onSaturday}">checked</c:if>> <span class="content"><label for="onday_sat">Sat</label></span></input> </td>
	
	</tr>
	</table></td></tr>
</table>
</table>