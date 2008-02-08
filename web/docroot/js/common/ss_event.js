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


function ssEventEditor(prefix, frequency, interval, weekDays, monthDays) {
	
	var prefix = prefix;
	
	var that = this;
	
	var interval = typeof interval !== undefined?interval:1;
	
	var frequency = typeof frequency !== undefined?frequency:"none";
	
	var weekDays = typeof weekDays !== undefined?weekDays:{};
	
	var monthDays = typeof monthDays !== undefined?monthDays:{};
	
	this.locale = {
		every: "Every",
		days: "day(s)",
		weeks: "week(s)",
		weeksOccurson: "on",
		months: "month(s)",
		years: "year(s)",
		dayNamesShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
		monthOnWeeksTitle : "select which week in the month on which this calendar entry will occur",
		monthOnDaysTitle: "select the day of the week on which the repeated entry will occur",
		pleaseSelect: "--select one--",
		weekFirst: "first", 
		weekSecond: "second", 
		weekThird: "third", 
		weekFourth: "fourth", 
		weekLast: "last",
		weekday: "weekday",
		weekendday: "weekend day"
	}
	
	this.toggleAllDay = function (checkboxObj, timeFormObjIds) {
		if (!checkboxObj.checked) {
			ss_show(prefix + "eventStartTime");
			ss_show(prefix + "eventEndTime");
			
			for (var i = 0; i < timeFormObjIds.length; i++) {
				var timeFormObj = document.getElementById(timeFormObjIds[i]);
				if (timeFormObj) {
					timeFormObj.value = "false";
				}
			}
			
		} else {
			ss_hide(prefix + "eventStartTime");
			ss_hide(prefix + "eventEndTime");	
			
			for (var i = 0; i < timeFormObjIds.length; i++) {
				var timeFormObj = document.getElementById(timeFormObjIds[i]);
				if (timeFormObj) {
					timeFormObj.value = "true";
				}
			}
		}
	}

	this.setFrequency = function (selectObj) {
		if (!document.getElementById) {
			return;
		}
		var frequencyContainer = document.getElementById(prefix + "RequrencyDefinitions");
		if (!frequencyContainer) {
			return;
		}
		if (selectObj) {
			frequency = selectObj.options[selectObj.selectedIndex].value;
		}
		var rangesId = prefix + "Range";
		if (frequency == "none") {
			ss_hide(prefix + "RequrencyDefinitions");
			dispayNoneMask(frequencyContainer);
			ss_hide(rangesId);
		} else if (frequency == "day") {
			ss_show(prefix + "RequrencyDefinitions");
			dispayDayMask(frequencyContainer);
			ss_show(rangesId);
		} else if (frequency == "week") {
			ss_show(prefix + "RequrencyDefinitions");
			dispayWeekMask(frequencyContainer);
			ss_show(rangesId);
		} else if (frequency == "month") {
			ss_show(prefix + "RequrencyDefinitions");
			dispayMonthMask(frequencyContainer);
			ss_show(rangesId);
		} else if (frequency == "year") {
			ss_show(prefix + "RequrencyDefinitions");
			dispayYearMask(frequencyContainer);
			ss_show(rangesId);
		}
	}
	
	function dispayNoneMask(frequencyContainer) {
		frequencyContainer.innerHTML = "";
	}	
	
	function dispayDayMask(frequencyContainer) {
		frequencyContainer.innerHTML = "" +
			"<label for=\"" +  prefix + "_everyNday" + "\">" + that.locale.every + "</label> " +
			"<input type=\"text\"" + 
			"       class=\"ss_text\"" + 
			"       name=\"" + prefix + "_everyN" + "\"" +
			"       size=\"2\""+
			"       value=\"" + interval + "\"" +  
			"       id=\"" + prefix + "_everyNday" + "\" /> " +
			"<label for=\"" +  prefix + "_everyNday" + "\">" + that.locale.days + "</label>" +
			"";
	}
	
	function dispayWeekMask(frequencyContainer) {
		var html = "" +
			"<label for=\"" + prefix + "_everyNweek\">" + that.locale.every + "</label> " +
			"<input type=\"text\" class=\"ss_text\" name=\"" + prefix + "_everyN\" id=\"" + prefix + "_everyNweek\" size=\"2\"" + 
			"       value=\"" + interval + "\" /> " +
			"<label for=\"" + prefix + "_everyNweek\">" + that.locale.weeks + " " + that.locale.weeksOccurson + "</label> ";
		
		for (var i = 0; i <= 6; i++) {
			html += " <input type=\"checkbox\" name=\"" + prefix + "_day" + i + "\" id=\"" + prefix + "_day" + i + "\"" +
			(weekDays[i]?" checked=\"checked\" ":"") + " />" +
			"<label for=\"" + prefix + "_day" + i + "\"><span class=\"ss_week_day\">" + that.locale.dayNamesShort[i] + "</span></label> ";
		}
			
		frequencyContainer.innerHTML = html;
	}
	
	function dispayMonthMask(frequencyContainer) {
		var html = "" + 
					"<label for=\"" + prefix + "_everyNmonth\">" + that.locale.every + "</label> " + 
					"<input type=\"text\" class=\"ss_text\" size=\"2\" " +
					"       name=\"" + prefix + "_everyN\" value=\"" + interval + "\" " +
					"       id=\"" + prefix + "_everyNmonth\" /> " + 
					"<label for=\"" + prefix + "_everyNmonth\">" + that.locale.months + "</label> " +
					
					"<select name=\"" + prefix + "_onDayCard\" title=\"" + that.locale.monthOnWeeksTitle + "\">" +  
	   				
					"     <option  value=\"none\" " +
					(monthDays.dayPosition == 'none'?" selected=\"selected\" ":"") + ">" + 
					that.locale.pleaseSelect + "</option>" +
					
					"     <option  value=\"first\" " +
					(monthDays.dayPosition == 'first'?" selected=\"selected\" ":"") + ">" + 
					that.locale.weekFirst + "</option>" +
					
					"     <option  value=\"second\" " +
					(monthDays.dayPosition == 'second'?" selected=\"selected\" ":"") + ">" + 
					that.locale.weekSecond + "</option>" +
					
					"     <option  value=\"third\" " +
					(monthDays.dayPosition == 'third'?" selected=\"selected\" ":"") + ">" + 
					that.locale.weekThird + "</option>" +
					
					"     <option  value=\"fourth\" " +
					(monthDays.dayPosition == 'fourth'?" selected=\"selected\" ":"") + ">" + 
					that.locale.weekFourth + "</option>" +
					
					"     <option  value=\"last\" " +
					(monthDays.dayPosition == 'last'?" selected=\"selected\" ":"") + ">" + 
					that.locale.weekLast + "</option>" +
																								
					"</select> " + 
					
					"<select name=\"" + prefix + "_dow\" title=\"" + that.locale.monthOnDaysTitle + "\">" +  
					
					"<option  value=\"none\" " + 
					(monthDays.dayOfWeek == 'none'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.pleaseSelect + "</option>" +
					
					"<option  value=\"Sunday\" " +
					(monthDays.dayOfWeek == 'Sunday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[0] + "</option>" +
					
					"<option  value=\"Monday\" " +
					(monthDays.dayOfWeek == 'Monday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[1] + "</option>" +
					
					"<option  value=\"Tuesday\" " +
					(monthDays.dayOfWeek == 'Tuesday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[2] + "</option>" +
					
					"<option  value=\"Wednesday\" " +
					(monthDays.dayOfWeek == 'Wednesday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[3] + "</option>" +
					
					"<option  value=\"Thursday\" " +
					(monthDays.dayOfWeek == 'Thursday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[4] + "</option>" +
					
					"<option  value=\"Friday\" " +
					(monthDays.dayOfWeek == 'Friday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[5] + "</option>" +
					
					"<option  value=\"Saturday\" " +
					(monthDays.dayOfWeek == 'Saturday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.dayNamesShort[6] + "</option>" +																															

					"<option  value=\"weekday\" " +
					(monthDays.dayOfWeek == 'weekday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.weekday + "</option>" +
					
					"<option  value=\"weekendday\" " +
					(monthDays.dayOfWeek == 'weekendday'?" selected=\"selected\" ":"" ) + ">" + 
					that.locale.weekendday + "</option>" +					

					"</select>";
			
		frequencyContainer.innerHTML = html;
	}		
	
	function dispayYearMask(frequencyContainer) {
		var html = "" +
					"<label for=\"" + prefix + "repeatyear\">" + that.locale.every + "</label> " + 
					"<input type=\"text\" class=\"ss_text\" name=\""  + prefix + "_everyN\" " + 
					"       id=\"" + prefix + "repeatyear\" size=\"2\" " + 
					"       value=\"" + interval + "\" > " + 
					"<label for=\"" + prefix + "repeatyear\">" +  that.locale.years + "</label>";

		frequencyContainer.innerHTML = html;
	}
	
	function ss_show(objId){
		var obj = document.getElementById(objId);
		if (obj && obj.style) {
			obj.style.visibility="visible";
			obj.style.display="block";
		}
	}
	
	function ss_hide(objId){
		var obj = document.getElementById(objId);
		if (obj && obj.style) {
			obj.style.visibility="hidden";
			obj.style.display="none";
		}
	}	
	
}
