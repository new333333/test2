/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

dojo.require("dojo.date.stamp");

function ssEventEditor(prefix, frequency, interval, weekDays, monthDays) {
	
	var prefix = prefix;
	
	var that = this;
	
	var interval = (typeof interval !== undefined&&interval != -1)?interval:1;
	
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
		weekFifth: "fifth", 
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
			"       id=\"" + prefix + "_everyNday" + "\"" +
			"       onBlur=\"intRequiredBlur(this, " + INT_MODE_GT_ZERO + ", '" + ss_escapeSQ(that.locale.integerRequired) + "');\"" +
			"       /> " +
			"<label for=\"" +  prefix + "_everyNday" + "\">" + that.locale.days + "</label>" +
			"";
	}
	
	function displayDayInWeek(i) {
			return " <input type=\"checkbox\" name=\"" + prefix + "_day" + i + "\" id=\"" + prefix + "_day" + i + "\"" +
			(weekDays[i]?" checked=\"checked\" ":"") + " />" +
			"<label for=\"" + prefix + "_day" + i + "\"><span class=\"ss_week_day\">" + that.locale.dayNamesShort[i] + "</span></label> ";
	}	
	function dispayWeekMask(frequencyContainer) {
		var html = "" +
			"<label for=\"" + prefix + "_everyNweek\">" + that.locale.every + "</label> " +
			"<input type=\"text\" class=\"ss_text\" name=\"" + prefix + "_everyN\" id=\"" + prefix + "_everyNweek\" size=\"2\"" + 
			"       value=\"" + interval + "\" /> " +
			"<label for=\"" + prefix + "_everyNweek\">" + that.locale.weeks + " " + that.locale.weeksOccurson + "</label> ";
		
		for (var i = that.locale.weekFirstDayDefault; i <= 6; i++) {
			html += displayDayInWeek(i);
 		}
		for (var i = 0; i < that.locale.weekFirstDayDefault; i++) {
			html += displayDayInWeek(i);
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
					
					"     <option  value=\"fifth\" " +
					(monthDays.dayPosition == 'fifth'?" selected=\"selected\" ":"") + ">" + 
					that.locale.weekFifth + "</option>" +
					
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
// -----
//					Bugzilla 588387:
//						Since there is no infrastructure on the server
//						side to support weekday or weekend day, and
//						they have never worked, I'm simply removing
//						them to fix this.
// -----
//					"<option  value=\"weekday\" " +
//					(monthDays.dayOfWeek == 'weekday'?" selected=\"selected\" ":"" ) + ">" + 
//					that.locale.weekday + "</option>" +
//					
//					"<option  value=\"weekendday\" " +
//					(monthDays.dayOfWeek == 'weekendday'?" selected=\"selected\" ":"" ) + ">" + 
//					that.locale.weekendday + "</option>" +					
// -----
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


if (typeof ssEventScheduler === "undefined" || !ssEventScheduler) {
    var ssEventScheduler = {};
}

ssEventScheduler.create = function(params) {
	var containerId = ("containerId" in params) ? params.containerId : null;
	var container = document.getElementById(containerId);
	
	var userListObj = ("userListObj" in params) ? params.userListObj : null;
	var eventStartObj = ("eventStartObj" in params) ? params.eventStartObj : null;
	var eventStartTimeObj = ("eventStartTimeObj" in params) ? params.eventStartTimeObj : null;	
	var eventEndObj = ("eventEndObj" in params) ? params.eventEndObj : null;
	var eventEndTimeObj = ("eventEndTimeObj" in params) ? params.eventEndTimeObj : null;
	var eventAllDayObj = ("eventAllDayObj" in params) ? params.eventAllDayObj : null;	
	var userListDataName = ("userListDataName" in params) ? params.userListDataName : null;
	var binderId = ("binderId" in params) ? params.binderId : null;
	var entryId = ("entryId" in params) ? params.entryId : null;
	
	return new ssEventScheduler.Scheduler(container, userListObj, eventStartObj, eventStartTimeObj, eventEndObj, eventEndTimeObj, eventAllDayObj, userListDataName, binderId, entryId);
}

ssEventScheduler.locale = {
	allAttendees: "All attendees",
	busy: "Busy",
	tentative: "Tentative",
	outOfOffice: "Out Of Office"
}

ssEventScheduler.Scheduler = function(container, userListObj, eventStartObj, eventStartTimeObj, eventEndObj, eventEndTimeObj, eventAllDayObj, userListDataName, binderId, entryId) {
	var that = this;
	
	this._container = container;
	this._userListObj = userListObj;
	this._eventStartObj = eventStartObj;
	this._eventStartTimeObj = eventStartTimeObj;
	this._eventEndObj = eventEndObj;
	this._eventEndTimeObj = eventEndTimeObj;	
	this._eventAllDayObj = eventAllDayObj;
	this._userListDataName = userListDataName;
	this._binderId = binderId;
	this._entryId = entryId;

	this._isDisplayed = false;
	this._ulLegend;
	this._ulObj;
	this._divObj;
	this._minStart;
	this._maxEnd;	
	this._timeLine;
	this._bandInfos;
	this._decorators;
	this._eventSource;
	this._usersList = [];
	this._startDate = new Date();
	this._eventStartDate = new Date();
	this._eventEndDate = new Date();
	
	// add listeners
	if (this._userListObj != null) {

		this._userListObj.addListener("onAdd", function(user) {
			that.addUser(user);
		});
		
		this._userListObj.addListener("onDelete", function(user) {
			that.deleteUser(user);
		});
		
	}
	
	if (this._eventStartObj != null) {
		dojo.connect(this._eventStartObj, "onChange", function(evt) {
			that.refreshEventDates();
		});
	}
	if (this._eventEndObj != null) {
		dojo.connect(this._eventEndObj, "onChange", function(evt) {
			that.refreshEventDates();
		});
	}
	
	if (this._eventStartTimeObj != null) {
		dojo.connect(this._eventStartTimeObj, "onChange", function(evt) {
			that.refreshEventDates();
		});	
	}
	if (this._eventEndTimeObj != null) {
		dojo.connect(this._eventEndTimeObj, "onChange", function(evt) {
			that.refreshEventDates();
		});	
	}
	if (this._eventAllDayObj != null) {
		dojo.connect(this._eventAllDayObj, "onclick", function() {
			that.refreshEventDates();
		});	
	}	
	
	this.display = function() {
		if (that._isDisplayed) {
			that.hide();
			return;
		}
		if (that._container) {
			that._container.style.display = "block";
			that._container.focus();
		}
		that._isDisplayed = true;
		
		if (that._userListObj) {
			that._usersList = that._userListObj.getList();
		}
		if (that._eventStartObj) {
			that._setStartDate(that._eventStartObj.getValue());
		}
		if (that._eventEndObj) {
			that._setEndDate(that._eventEndObj.getValue());
		}
		
		if (!that._eventAllDayObj || (that._eventAllDayObj && !that._eventAllDayObj.checked)) {
			if (that._eventStartTimeObj) {
				that._setStartTime(that._eventStartTimeObj.getValue());
			}			
			if (that._eventEndTimeObj) {
				that._setEndTime(that._eventEndTimeObj.getValue());
			}
		} else {
			that._setAllDayTime(true);
		}
		that._displayUsers(true);
		that._displayTimeLine(true);
		that._displayLegend();
		
		that.loadData();
	}
	
	this._displayLegend = function() {
		if (that._ulLegend) {
			return;
		}
		that._ulLegend = document.createElement("UL");
		that._ulLegend.className = "legend";
		
		
		var liBusy = document.createElement("LI");
		
		var busySquereTable = document.createElement("TABLE");
		var busySquereTBody = document.createElement("TBODY");
		var busySquereTR = document.createElement("TR");
		var busySquereTD = document.createElement("TD");
		busySquereTable.appendChild(busySquereTBody);
		busySquereTBody.appendChild(busySquereTR);
		busySquereTR.appendChild(busySquereTD);
		busySquereTD.style.backgroundColor = "#990000";


		liBusy.appendChild(busySquereTable);
		busySquereTD.appendChild(document.createTextNode(ssEventScheduler.locale.busy));
		
		that._ulLegend.appendChild(liBusy);


		var liTentative = document.createElement("LI");
		var tentativeSquereTable = document.createElement("TABLE");
		var tentativeSquereTBody = document.createElement("TBODY");
		var tentativeSquereTR = document.createElement("TR");
		var tentativeSquereTD = document.createElement("TD");
		tentativeSquereTable.appendChild(tentativeSquereTBody);
		tentativeSquereTBody.appendChild(tentativeSquereTR);
		tentativeSquereTR.appendChild(tentativeSquereTD);
		tentativeSquereTD.style.backgroundColor = "#FF3300";
		
		liTentative.appendChild(tentativeSquereTable);
		tentativeSquereTD.appendChild(document.createTextNode(ssEventScheduler.locale.tentative));
		
		that._ulLegend.appendChild(liTentative);
		
		
		var liOutOfOffice = document.createElement("LI");
		var outOfOfficeSquereTable = document.createElement("TABLE");
		var outOfOfficeSquereTBody = document.createElement("TBODY");
		var outOfOfficeSquereTR = document.createElement("TR");
		var outOfOfficeSquereTD = document.createElement("TD");
		outOfOfficeSquereTable.appendChild(outOfOfficeSquereTBody);
		outOfOfficeSquereTBody.appendChild(outOfOfficeSquereTR);
		outOfOfficeSquereTR.appendChild(outOfOfficeSquereTD);
		outOfOfficeSquereTD.style.backgroundColor = "#FFCC00";
		
		liOutOfOffice.appendChild(outOfOfficeSquereTable);
		outOfOfficeSquereTD.appendChild(document.createTextNode(ssEventScheduler.locale.outOfOffice));				
		
		that._ulLegend.appendChild(liOutOfOffice);		
		
		if (that._container) {
			that._container.appendChild(that._ulLegend);
		}
	}
	
	this._displayUsers = function(init) {
		if (!that._ulObj && !init) {
			return;
		}

		var newList = that._ulObj == null;		
		if (!that._ulObj) {
			that._ulObj = document.createElement("UL");
			that._ulObj.className = "usersList";
		}
		while (that._ulObj.hasChildNodes()) {
			that._ulObj.removeChild(that._ulObj.firstChild);
		}

		var li = document.createElement("LI");
		li.setAttribute("class", "allAttendees");
		li.appendChild(document.createTextNode(ssEventScheduler.locale.allAttendees));
		that._ulObj.appendChild(li);
			
		for (id in that._usersList) {
			var li = document.createElement("LI");
			li.appendChild(document.createTextNode(that._usersList[id]));
			that._ulObj.appendChild(li);
		}
		
		if (newList && that._container) {	
			that._container.appendChild(that._ulObj);
		}
	}
	
	this._displayTimeLine = function(init) {
		if (!that._divObj && !init) {
			return;
		}
		
		if (!that._divObj && that._container) {	
			that._divObj = document.createElement("DIV");
			that._container.appendChild(that._divObj);			
			
			that._divObj.className = "timeLine";
		}
		
		var usersListLength = 0;
		for (key in that._usersList) {
			usersListLength++;
		}	
		var height = usersListLength * 30 + 80;
		height = height * 100 / 80; // add 20% for days band
		that._divObj.style.height = height + "px";
		
		if (that._timeLine == null) {
			that._eventSource = new Timeline.DefaultEventSource(0);

			that._bandInfos = [
					Timeline.createBandInfo({
						width:          "80%", 
						date:           that._startDate,
						intervalUnit:   Timeline.DateTime.HOUR, 
						eventSource:    that._eventSource,
						intervalPixels: 30
					}),
					Timeline.createBandInfo({
						width:          "20%", 
						date:			that._startDate,
						intervalUnit:   Timeline.DateTime.DAY, 
						intervalPixels: 150
					})			
			];
			that._bandInfos[1].syncWith = 0;
			that._bandInfos[1].highlight = true;
			
			that._decorators = [
				new Timeline.SpanHighlightDecorator({
						startDate:  that._eventStartDate,
						endDate:    that._eventEndDate,
						color:      "#FFC080",
						startLabel: "",
						endLabel: "",
						opacity: 20
                    }),
				new Timeline.SpanHighlightDecorator({
						startDate:  that._eventStartDate,
						endDate:    that._eventEndDate,
						color:      "#FFC080",
						startLabel: "",
						endLabel: "",
						opacity: 20
                    })			
			];
			            
			for (var i = 0; i < that._bandInfos.length; i++) {
				that._bandInfos[i].decorators = [that._decorators[i]];
            }
			
			
			that._timeLine = Timeline.create(that._divObj, that._bandInfos);
			
			that._timeLine.getBand(0).addOnScrollListener(function(band) {
				if (band.getMinDate() < that._minStart) {
					that._minStart = dojo.date.add(band.getMinDate(), "week", -1);
					that._minStart.setUTCHours(0);
					that._minStart.setUTCMinutes(0);

					that._maxEnd = dojo.date.add(band.getMinDate(), "week", 3);
					that._maxEnd.setUTCHours(0);
					that._maxEnd.setUTCMinutes(0);
															
					that.loadData();
				}
				if (that._maxEnd < band.getMaxDate()) {
					that._minStart = dojo.date.add(band.getMaxDate(), "week", -1);
					that._minStart.setUTCHours(0);
					that._minStart.setUTCMinutes(0);
										
					that._maxEnd = dojo.date.add(band.getMaxDate(), "week", 3);
					that._maxEnd.setUTCHours(0);
					that._maxEnd.setUTCMinutes(0);
					
					that.loadData();
				}
			}); 
		} else {
			that._timeLine.layout();
		}
	}
	
	this.hide = function() {
		if (that._container) {
			that._container.style.display = "none";
		}
		that._isDisplayed = false;
	}
	
	this.addUser = function(user) {
		that._usersList[user.id] = user.name;
		that._displayUsers(false);
		that._displayTimeLine(false);
		that.loadData();
	}
	
	this.deleteUser = function(user) {
		delete that._usersList[user.id];
		that._displayUsers(false);
		that._displayTimeLine(false);
		that.loadData();		
	}
	
	this.refreshEventDates = function() {
		if (!that._timeLine) {
			return;
		}
		if (that._eventStartObj) {
			that._setStartDate(that._eventStartObj.getValue());
		}		
		if (that._eventEndObj) {
			that._setEndDate(that._eventEndObj.getValue());
		}		
		if (!that._eventAllDayObj || (that._eventAllDayObj && !that._eventAllDayObj.checked)) {
			if (that._eventStartTimeObj) {
				that._setStartTime(that._eventStartTimeObj.getValue());
			}					
			if (that._eventEndTimeObj) {
				that._setEndTime(that._eventEndTimeObj.getValue());
			}
		} else {
			that._setAllDayTime(true);
		}
		
		that._timeLine.getBand(0).scrollToCenter(that._startDate);
		that.loadData();
		
		that._repaintEventOnTimeLine();
	}
	
	this._setAllDayTime = function(checked) {
		if (checked) {
			var allDayTime = new Date();
			allDayTime.setHours(0);
			allDayTime.setMinutes(0);
			that._setStartTime(allDayTime);
			that._setEndTime(allDayTime);
			
			that._eventEndDate = dojo.date.add(that._eventEndDate, "day", 1);
		} else {
			that._setStartDate(that._eventStartObj.getValue());
			that._setEndDate(that._eventEndObj.getValue());
					
			that._setStartTime(that._eventStartTimeObj.getValue());
			that._setEndTime(that._eventEndTimeObj.getValue());
		}
	}
		
	this._setStartDate = function(date) {
		that._startDate.setUTCFullYear(date.getFullYear());
		that._startDate.setUTCMonth(date.getMonth());
		that._startDate.setUTCDate(date.getDate());
		
		that._eventStartDate.setUTCFullYear(date.getFullYear());
		that._eventStartDate.setUTCMonth(date.getMonth());
		that._eventStartDate.setUTCDate(date.getDate());
		
				
		that._minStart = dojo.date.add(that._startDate, "week", -1);
		that._minStart.setUTCHours(0);
		that._minStart.setUTCMinutes(0);
		
		that._maxEnd = dojo.date.add(that._startDate, "week", 3);
		that._maxEnd.setUTCHours(0);
		that._maxEnd.setUTCMinutes(0);
	}

	this._setStartTime = function(time) {
		that._startDate.setUTCHours(time.getHours() + 5);// band center
		that._startDate.setUTCMinutes(time.getMinutes());
		
		that._eventStartDate.setUTCHours(time.getHours());
		that._eventStartDate.setUTCMinutes(time.getMinutes());	
	}
	
	this._setEndDate = function(date) {	
		that._eventEndDate.setUTCFullYear(date.getFullYear());
		that._eventEndDate.setUTCMonth(date.getMonth());
		that._eventEndDate.setUTCDate(date.getDate());
	}

	this._setEndTime = function(time) {
		that._eventEndDate.setUTCHours(time.getHours());
		that._eventEndDate.setUTCMinutes(time.getMinutes());		
	}	
	
	this._repaintEventOnTimeLine = function() {
		if (that._decorators) {
			for (var i = 0; i < that._decorators.length; i++) {
				that._decorators[i]._startDate = that._eventStartDate;
			    that._decorators[i]._endDate = that._eventEndDate;
			    that._decorators[i].paint();
			}
		}
	}
	
	this.loadData = function() {
		if (that._eventSource == null) {
			return;
		}
		that._eventSource.clear();
		var usersIds =[];
		for (var i in that._usersList) {
			usersIds.push(i);
		}
		if (usersIds.length == 0) {
			return;
		}
		var jsonUrl = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation: "get_calendar_free_info",
														binderId: that._binderId != null ? that._binderId : -1,
														entryId: that._entryId != null ? that._entryId : -1,
														ssUsersId: usersIds,
														ssStartDate: dojo.date.stamp.toISOString(that._minStart, {selector: "date"}),
														ssEndDate: dojo.date.stamp.toISOString(that._maxEnd, {selector: "date"}),
														ssUserListName: that._userListDataName != null ? that._userListDataName : ""
														});
		that._timeLine.loadJSON(jsonUrl, function(json, url) {
			that._eventSource.clear();		
			that._eventSource.loadJSON(json, url);	
		});
	}

}

