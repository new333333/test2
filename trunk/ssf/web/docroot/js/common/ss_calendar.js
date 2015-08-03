/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

dojo.require("dojo.date");
dojo.require("dojo.date.locale");
dojo.require("dijit.dijit");

dojo.requireLocalization("ssf.cldr","custom", null, "da,de,de-de,en,en-gb,en_us,es,es-es,fi,fi-fi,fr,fr-fr,hu,it,it-it,ROOT,ja,ja-jp,ko,ko-kr,nl,nl-nl,pl,pt,pt-br,sv,zh,zh-cn,zh-hk,zh-tw");
dojo.date.locale.addCustomFormats("ssf.cldr","custom");

// Bugzilla 512176:
//    Rewrote to use dojo's locale based date formatter to construct
//    the correct format.  The following is used to which format is
//    used.
var calendarNav_DateFormat = "medium";

// Width used for the popup DIV containing the list of events when
// they all can't be displayed in the calendar grid.
var moreEventsDIVWidth = 150;

function ss_calendar_data_provider(binderId, calendarIds, stickyId, isDashboard, calendarModeType) {
	
	var binderId = binderId;
	
	var calendarIds = calendarIds;
	
	var stickyId = stickyId;
	
	var isDashboard = (typeof isDashboard != "undefined")?isDashboard:false;
	var calendarModeType = (typeof calendarModeType != "undefined")?calendarModeType:"";
	
	function mergeObj(dest, src) {
		if (typeof dest == "undefined" || typeof src == "undefined") {
			return dest;
		}
		
		for (var i in src) {
			dest[i] = src[i];
		}
		return dest;
	}
	
	this.loadEventsByDate = function(reqParams, date, calendarObj) {
		dojo.xhrGet({
	    	url: ss_buildAdapterUrl(ss_AjaxBaseUrl, mergeObj({operation: "find_calendar_events",
										binderId: binderId, 
										binderIds: calendarIds,
										ssDashboardRequest: isDashboard,
										calendarStickyId: stickyId, 
										calendarModeType: calendarModeType},
										reqParams)),
			handleAs: "json-comment-filtered",
			error: function(err) {
				alert(ss_not_logged_in);
			},
			load: function(data) {
				calendarObj.addEvents(data, date);
			},
			preventCache: true
		});
	}
	
	this.loadEntryEvents = function(reqParams, calendarObj) {
		dojo.xhrGet({
	    	url: ss_buildAdapterUrl(ss_AjaxBaseUrl, mergeObj({operation: "find_calendar_events",
											binderId: binderId, 
											binderIds: calendarIds,
											ssDashboardRequest: isDashboard}, reqParams)),
			handleAs: "json-comment-filtered",
			error: function(err) {
				alert(ss_not_logged_in);
			},
			load: function(data) {
				calendarObj.addEvents(data);
			},
			preventCache: true
		});	 
	}
	
	this.stickyCalendarDisplaySettings = function(options, refreshFunction) {
	 	if (!options) {
	 		return;
	 	}

	 	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, mergeObj({operation: "sticky_calendar_display_settings",
								binderId: binderId, calendarStickyId: stickyId}, options));
		dojo.xhrGet({
	    	url: url,
			error: function(err) { },
			load: function(data) {
				if (typeof refreshFunction == 'function') {
					refreshFunction();
				}
				return data
			},
			preventCache: true
		});
	}

}


function ss_calendarControl() {
	var instanceCounterId = 0;
	
	this.createCalendar = function(props) {
		var instanceId = instanceCounterId++;
		var props = props || {};
		return new ss_calendarEngine(instanceId, 
					(typeof props.containerId!="undefined")?props.containerId:null, 
					(typeof props.readOnly!="undefined")?props.readOnly:false, 
					(typeof props.calendarDataProvider!="undefined")?props.calendarDataProvider:null, 
					(typeof props.weekFirstDayDefault!="undefined")?props.weekFirstDayDefault:1,
					(typeof props.weekFirstDay!="undefined")?props.weekFirstDay:props.weekFirstDayDefault,
					(typeof props.workDayStartDefault!="undefined")?props.workDayStartDefault:8,
					(typeof props.workDayStart!="undefined")?props.workDayStart:props.workDayStartDefault,
					(typeof props.defaultCalendarId!="undefined")?props.defaultCalendarId:null,
					(typeof props.viewDatesDescriptionsFieldId!="undefined")?props.viewDatesDescriptionsFieldId:null,
					(typeof props.viewSelectorHrefIds!="undefined")?props.viewSelectorHrefIds:null,
					(typeof props.calendarHoursSelectorId!="undefined")?props.calendarHoursSelectorId:null,
					(typeof props.eventsTypeChooseId!="undefined")?props.eventsTypeChooseId:null,
					(typeof props.eventsTypeSelectId!="undefined")?props.eventsTypeSelectId:null,
					(typeof props.onCalendarStyleChoose!="undefined")?props.onCalendarStyleChoose:null,
					(typeof props.addEntryURL!="undefined")?props.addEntryURL:null,
					(typeof props.stickyId!="undefined")?props.stickyId:null,
					(typeof props.createEntryActive!="undefined")?props.createEntryActive:true);
	}
}

function ss_calendarEngine(
					instanceId,
					containerId,
					readOnly, 
					calendarDataProvider, 
					weekFirstDayDefault,
					weekFirstDay,
					workDayStartDefault,
					workDayStart,
					defaultCalendarId, 
					viewDatesDescriptionsFieldId, 
					viewSelectorHrefIds,
					calendarHoursSelectorId,
					eventsTypeChooseId,
					eventsTypeSelectId,
					onCalendarStyleChoose,
					addEntryURL,
					stickyId) {

	this.locale = {
		dayNamesShort: ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
		monthNamesShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
		monthNames: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
		allDay: "All day",
		noTitle: "--no title--",
		workDayGridTitle: "Work day",
		fullDayGridTitle: "Full day",
		entriesLabel: "entries",
		minutesShortLabel:  "{0} min",
		lang: window.dojo&&dojo.locale?dojo.locale:"en"
	};
	
	this.NUMBER_OF_DEFINED_CALENDAR_STYLES = 11;
	
	this.templateHTML = null;
					
	var instanceId = instanceId;
	
	var containerId = containerId;
	
	var calendarDataProvider = calendarDataProvider;
	
	var defaultCalendarId = defaultCalendarId;
	
	var weekFirstDayDefault = typeof weekFirstDayDefault!=="undefined"?(weekFirstDayDefault - 1):0;
	
	var weekFirstDay = typeof weekFirstDay!=="undefined"?(weekFirstDay - 1):weekFirstDayDefault;
	
	var workDayStartDefault = workDayStartDefault;
	
	var workDayStart = workDayStart;
	
	var viewDatesDescriptionsFieldId = viewDatesDescriptionsFieldId;
	
	var calendarHoursSelectorId = calendarHoursSelectorId;
	
	var eventsTypeChooseId = eventsTypeChooseId;
	
	var	eventsTypeSelectId = eventsTypeSelectId;
	
	var onCalendarStyleChoose = onCalendarStyleChoose;
	
	var addEntryURL = addEntryURL;
	
	var viewSelectorHrefIds = viewSelectorHrefIds;
	
	var stickyId = stickyId;
	
	var templateInitialized = false;
	
	var hoverBox;	
	
	var that = this;
	
	var viewSelectorHrefClasses = {
		days1: "ss_calDaySelectButton", 
		days3: "ss_cal3DaysSelectButton", 
		days5: "ss_cal5DaysSelectButton", 
    	days7: "ss_cal7DaysSelectButton", 
		days14: "ss_cal14DaysSelectButton", 
		month: "ss_calMonthSelectButton"
	};
	
	dojo.addOnLoad(function() {
		hoverBox = document.createElement("div");
		hoverBox.className= "ss_cal_eventBody";
		hoverBox.style.display = "none"; 
		hoverBox.style.visibility = "hidden"; 
		hoverBox.style.position = "absolute"; 
		hoverBox.style.zIndex = "2003"; 
		hoverBox.style.border = "0"; 

		document.getElementsByTagName("body").item(0).appendChild(hoverBox);
	});
	
	this.ss_initializeCalendar = function() {
		that.loadEventsByDate();
	}
	
	this.ss_uninitializeCalendar = function() {
		templateInitialized = false;
		ss_cal_Events.removeAllEvents();
	}

	this.getTemplateHTML = function() {
		// We need to build this after we've initialized rather than
		// during construction so that the localized strings have been
		// localized.
		if (null == that.templateHTML) {
			that.templateHTML =
				"<div id=\"ss_cal_DayGridMaster" + instanceId + "\" style=\"display:none;\">" +
				"  <table class=\"ss_cal_gridTable\">" +
				"    <tbody>" +
				"      <tr>" +
				"        <td class=\"ss_cal_dayGridHourTicksColumn\" style=\"padding-right: 0px;\"><div class=\"ss_cal_gridHeader\"></td>" +
				"        <td><div width=\"100%\" id=\"ss_cal_dayGridHeader" + instanceId + "\" class=\"ss_cal_gridHeader ss_cal_reserveWidth\"></div></td>" + 
				"      </tr>" +
				"      <tr>" +
				"        <td class=\"ss_cal_dayGridHourTicksColumn\">" + this.locale.allDay + "</td>" +
				"        <td><div id=\"ss_cal_dayGridAllDay" + instanceId + "\" class=\"ss_cal_dayGridHour ss_cal_dayGridAllDay ss_cal_reserveWidth\"></div></td>" +
				"      </tr>" +
				"    </tbody>" +
				"  </table>" +
				"  <div class=\"ss_cal_dayGridDivider\"></div>" +
				"  <div id=\"ss_cal_dayGridWindowOuter" + instanceId + "\" class=\"ss_cal_dayGridWindowOuter\">" +
				"    <div id=\"ss_cal_dayGridWindowInner" + instanceId + "\" class=\"ss_cal_dayGridWindowInner\" style=\"top: " + (-3 - (workDayStart* 42)) + "px; \">" +
				"      <table class=\"ss_cal_gridTable\">" +
				"        <tbody>" +
				"          <tr>" +
				"            <td class=\"ss_cal_dayGridHourTicksColumn\"><div id=\"ss_cal_hourHeader" + instanceId + "\" class=\"ss_cal_dayGridHour\"></div></td>" +
				"            <td><div id=\"ss_cal_dayGridHour" + instanceId + "\" class=\"ss_cal_dayGridHour ss_cal_reserveWidth\"></div></td>" +
				"          </tr>" +
				"        </tbody>" +
				"      </table>" +
				"    </div>" +
				"  </div>" +
				"</div>" +
				"<div id=\"ss_cal_MonthGridMaster" + instanceId + "\" style=\"position: relative; display: none;\">" +
				"  <table style=\"width: 100%\" cellpadding=0 cellspacing=0 border=0>" +
				"    <tbody>" +
				"      <tr>" +
				"        <td id=\"ss_cal_monthGridHeader" + instanceId + "\" class=\"ss_cal_gridHeader ss_cal_reserveWidth\"></td>" +
				"      </tr>" +
				"      <tr>" +
				"        <td><div id=\"ss_cal_monthGrid" + instanceId + "\" class=\"ss_cal_monthGrid ss_cal_reserveWidth\"></div></td>" +
				"      </tr>" +
				"    </tbody>" +
				"  </table>" +
				"</div>";
		}
		return( that.templateHTML );
	}
		
	this.createTemplate = function() {
		// Bugzilla 940292:
		//    If we can't find the container, DON'T default to the
		//    document body as that ends up replacing the entire
		//    window with the content of the calendar.
		var container = dojo.byId(containerId);	// ...was || document.body;
		if (container) {
			container.innerHTML = that.getTemplateHTML();
			templateInitialized = true;
		}
	}
	
	this.removeEntryEvents = function(entryId) {
		ss_cal_Events.removeEntryEvents(entryId);		
	}
	
	this.refreshEntryEvents = function(entryId) {
		setTimeout(function(){
				ss_cal_Events.removeEntryEvents(entryId);
				that.loadEntryEvents(entryId);
			}, 2000);
	}
	
	this.switchView =  function(/* String: "daydelta", "3daydelta", "week", "fortnight", "workweek", "month", "daydirect", "monthdirect", "datedirect", "prev", "next" */ 
	    							mode, 
	    							year, month, dayOfMonth) {
		ss_cal_Events.switchView(mode, year, month, dayOfMonth);
	}
	
	this.changeEventType = function() {
		ss_cal_Events.changeEventType();
	}

	this.fullDayGrid =  function() {
    	ss_cal_Grid.fullDayGrid();
    }
	
	this.workDayGrid = function() {
		ss_cal_Grid.workDayGrid();
	}


	function clearSelection() {
		var sel;
		if (document.selection && document.selection.empty) {
			document.selection.empty();
		} else if (window.getSelection) {
			sel = window.getSelection();
			var body = document.getElementsByTagName("body")[0];
			sel.collapse(body, 0);
			if(sel && sel.removeAllRanges) {
				sel.removeAllRanges();
			}
		}
	}

	function fadeAndDestroy(e, t) {	
		dojo.fadeOut({node: e, duration: t,
				      onEnd: function() { dojo._destroyElement(e)}
			        }).play();
	}

	function getMonthName(date) {
		return that.locale.monthNames[date.getMonth()];
	}
	
	function getMonthNameShort(date) {
		return that.locale.monthNamesShort[date.getMonth()];
	}
	
	function getDayHeader(date) {
		return that.locale.dayNamesShort[date.getDay()] + " " +
		 			// The "monthAndDayOnly" is custom date format added under dojo/ssf
					dojo.date.locale.format(date, {formatLength: "monthAndDayOnly", selector:"date", locale: that.locale.lang});
	}
	
	function getMinutesOfTheDay(date) {
		return (60 * date.getHours()) + date.getMinutes();
	}
	
	function daysDiff(date1, date2) {
		var utc1 = +date1/10000 - (date1.getTimezoneOffset() * 6);
		var utc2 = +date2/10000 - (date2.getTimezoneOffset() * 6);

		utc1 = utc1 - (utc1 % 8640);
		utc2 = utc2 - (utc2 % 8640);
		return (utc2 - utc1) / 8640;
	}
	
	function getCalendarEventStyle(calendarId) {
		var calendarStyle = "";
		if (calendarId == defaultCalendarId) {
			calendarStyle = "ss_calendar_defaultCalendar";
		} else {
			calendarStyle = "ss_calendar_calendar" + getCalendarEventColorNumber(calendarId);
		}
		if (onCalendarStyleChoose) {
			onCalendarStyleChoose(calendarId, calendarStyle);
		}
		return calendarStyle;
	}
	
	function getCalendarEventColorNumber(calendarId) {
		var colorNum = calendarId % that.NUMBER_OF_DEFINED_CALENDAR_STYLES;
		for (var i = 0; i < ss_cal_Events.eventBinderIds.length; i++) {
			if (ss_cal_Events.eventBinderIds[i] == calendarId) {
				colorNum = i % that.NUMBER_OF_DEFINED_CALENDAR_STYLES;
				break;
			}
		}
		return colorNum;
	}
	
	function fullWithZeros(c) {
    	if (c < 10) {
    		return "0" + c;
    	}
    	return c;
    }
	
	function addTopCorners(ebox, calStyle) {
	    var cornerBox = document.createElement("div");
	    cornerBox.className = "ss_cal_smallRBoxTop2 " + calStyle;
	    ebox.appendChild(cornerBox);
	
	    cornerBox = document.createElement("div");
	    cornerBox.className = "ss_cal_smallRBoxTop1 " + calStyle;
	    ebox.appendChild(cornerBox);
	}
	
	function addBottomCorners(ebox, calStyle) {
	
		var cornerBox = document.createElement("div");
	    cornerBox.className = "ss_cal_smallRBoxBtm1 " + calStyle;
	    ebox.appendChild(cornerBox);
	
	    cornerBox = document.createElement("div");
	    cornerBox.className ="ss_cal_smallRBoxBtm2 " + calStyle;
	    ebox.appendChild(cornerBox);
	}
	
	this.addEvents = function(eventsData, date) {
		if (!templateInitialized) {
			that.createTemplate();
			if (!templateInitialized) {
				return;
			}
		}
		var today = new Date();
		if (eventsData.today) {
			today = new Date(eventsData.today.substr(0, 4) * 1, eventsData.today.substr(4, 2) * 1 - 1, eventsData.today.substr(6, 2) * 1);			
		}
		ss_cal_CalData.setToday(today);
		if (eventsData.monthViewInfo) {
			ss_cal_CalData.setMonthViewInfo(eventsData.monthViewInfo);
		}
		if (eventsData.events) {
			ss_cal_Events.addEvents(eventsData.events);
		}
		if (eventsData.eventBinderIds) {
			ss_cal_Events.addEventBinderIds(eventsData.eventBinderIds);
		}
		if (eventsData.gridSize) {
			ss_cal_Grid.setGridSize(eventsData.gridSize);
		}
		if (eventsData.currentDate) {
			ss_cal_Grid.setCurrentDate(new Date(eventsData.currentDate.substr(0, 4) * 1, eventsData.currentDate.substr(4, 2) * 1 - 1, eventsData.currentDate.substr(6, 2) * 1));
		}
		if (date) {
			ss_cal_Grid.setFirstDayToShow(date);
		}
		if (eventsData.gridType) {
			ss_cal_Grid.activateGrid(eventsData.gridType);
		}
		ss_cal_Grid.highlightDaySelectorMenuIcon();
		if (eventsData.eventType) {
			ss_cal_Events.setEventTypeByName(eventsData.eventType);
		}
		if (eventsData.dayViewType && eventsData.dayViewType == "fullday") {
			ss_cal_Grid.fullDayRedraw();
		}
		
        ss_cal_Events.redrawAll();
	}
	
	  
	this.loadEntryEvents = function (entryId) {
		if (calendarDataProvider) {
			calendarDataProvider.loadEntryEvents({
				ssEntryEvents : true,
				entryId : entryId
			}, that);
		}   	
	}
	    
	this.loadEventsByDate = function (grid, date, requiredDay) {
	   	if (date && ss_cal_CalData.getMonthViewInfo(date) &&
	   		(!requiredDay || ss_cal_CalData.getMonthViewInfo(requiredDay))) {
   			ss_cal_Grid.setCurrentDate(date);
   			ss_cal_Grid.setFirstDayToShow(date);
    		ss_cal_Grid.activateGrid(grid);
    		ss_cal_Grid.highlightDaySelectorMenuIcon();
    		var doResize = ((grid != "month") || (!ss_isIE));
    		ss_cal_Events.redrawAll(doResize);
    			   
    		var stickyCalendarParams = {};
    		if (grid) {
    			stickyCalendarParams.ssGridSize = ss_cal_Grid.gridSize;
    			stickyCalendarParams.ssGridType = grid;
    		}
			if (date) {
				stickyCalendarParams.year = date.getFullYear();
				stickyCalendarParams.month = (date.getMonth() + 1);
				stickyCalendarParams.dayOfMonth = date.getDate();
			}
			if (calendarDataProvider) {
    			calendarDataProvider.stickyCalendarDisplaySettings(stickyCalendarParams);
			}
    		return;
    	}
    	
    	var dateToLoad = date;
    	if (requiredDay && !ss_cal_CalData.getMonthViewInfo(requiredDay)) {
    		dateToLoad = requiredDay;
    	}
		
		var requestParams = {};
		
    	if (dateToLoad) {
			requestParams.year = dateToLoad.getFullYear();
			requestParams.month = dateToLoad.getMonth() + 1;
			requestParams.dayOfMonth = dateToLoad.getDate();
		}
		if (grid) {
			requestParams.ssGridSize = ss_cal_Grid.gridSize;
			requestParams.ssGridType = grid;
		}
		
		if (calendarDataProvider) {
			calendarDataProvider.loadEventsByDate(requestParams, date, that);
		}
    }
	
	// keeps today and month info data
	var ss_cal_CalData = {
	    
	    today : null,
	
		monthViewInfo : {},
	
	    setMonthViewInfo : function (monthViewInfo) {
			var year = monthViewInfo.year;
			var month = monthViewInfo.month;
			var daysInMonth = monthViewInfo.numberOfDaysInView;
			var startViewDate = monthViewInfo.startViewDate;
			var endViewDate = monthViewInfo.endViewDate;
				
	    	this.monthViewInfo[year + "/" + month] = {
	    		year : year,
	    		month : month,
				daysInMonth : daysInMonth,
				startViewDate : new Date(startViewDate.substr(0, 4) * 1, startViewDate.substr(4, 2) * 1 - 1, startViewDate.substr(6, 2) * 1),
				endViewDate : new Date(endViewDate.substr(0, 4) * 1, endViewDate.substr(4, 2) * 1 - 1, endViewDate.substr(6, 2) * 1)};
	    },
	    
		getMonthViewInfo : function (date) {
			var a  = date.getFullYear() + "/" + date.getMonth();
			if (typeof this.monthViewInfo[a] == "undefined") {
				return null;
			}
			return this.monthViewInfo[a];
	    },
		    
	    setToday : function (date) {
	    	this.today = new Date(date);
	    },
	    
	    isToday : function (date) {
			return daysDiff(this.today, date) == 0;
	    }
	}// end ss_cal_CalData
	
	
	var ss_cal_Grid = {

	    onClickSetForToday : false,	// This variable tells us whether we have set the onclick event handler on today.
	    
	    // Some defaults
	    gridSize: 7,
	    gridIncr: 7,
	    firstDayToShow: null,
	    readOnly: readOnly,
	    monthGridWeeks: 5, // allowed (and only possible) values are: 4, 5 and 6
	    currentType: 'day', // allowed are: day or month
	    dayGridCreated : false,
	    currentDate: null,
	    
		setCurrentDate : function (date) {
			this.currentDate = date;
		},
		
		setFirstDayToShow : function (date) {
			this.firstDayToShow = date;
		},
		
		setGrid : function (grid) {
			this.gridSize = grid.size;
			this.gridIncr = grid.incr;			
		},
		
		// sets grid incr to default value depends on size
		// for full control use setGrid
		setGridSize : function (size) {
			if (size == 5) {
				// work week
				this.setGrid({size: size, incr: 7});
			} else {
				this.setGrid({size: size, incr: size});
			}
		},
	
	    activateGrid: function(gridType) {
	    	if (!gridType) { gridType = this.currentType; }
	        if (gridType != "") { this.currentType = gridType; }
	        if (this.currentType == "day") {
				if ((7 == this.gridSize) || (14 == this.gridSize)) {
					// Bugzilla 509734:
					//    For week views, we want to show the week
					//    starting with the user's first day setting.
		        	var firstDay = this.currentDate;
					var firstDayOffset = (firstDay.getDay() - weekFirstDay);
					if        ( 6  == firstDayOffset) firstDayOffset = (-1);
					else if ((-6) == firstDayOffset) firstDayOffset =   1;
					else if  ( 5  == firstDayOffset) firstDayOffset = (-2);
					else if ((-5) == firstDayOffset) firstDayOffset =   2;
					this.firstDayToShow = firstDay;
					this.firstDayToShow.setDate(firstDay.getDate() - firstDayOffset)
					// alert("firstDayOffset:"+firstDayOffset+"\n" +"firstDay:"+firstDay+"\n"+"this.currentDate:"+this.currentDate+"\n"+"this.firstDayToShow:"+this.firstDayToShow); //!
				} else {
		 	   		if (!this.firstDayToShow) {
			    		this.firstDayToShow = this.currentDate;
				   	}
				}
		    	
	            dojo.style(dojo.byId("ss_cal_MonthGridMaster" + instanceId), "display", "none");
	            dojo.style(dojo.byId("ss_cal_DayGridMaster" + instanceId), "display", "block");
	
	            this.drawDayHeader("ss_cal_dayGridHeader" + instanceId, this.gridSize, this.firstDayToShow);
	            this.drawDayGrid("ss_cal_dayGridAllDay" + instanceId, ss_cal_CalData.today, this.gridSize, this.firstDayToShow, "ss_cal_allDay", 1); 
	            this.drawDayGrid("ss_cal_dayGridHour" + instanceId, ss_cal_CalData.today, this.gridSize, this.firstDayToShow, "ss_cal_hourGrid", 0); 
	            this.drawHourMarkers("ss_cal_hourHeader" + instanceId);
	            if (!this.dayGridCreated && !this.readOnly) {
					dojo.connect(dojo.byId("ss_cal_dayGridHour" + instanceId),  "onmousedown", function(evt) { ss_cal_CalEvent.mouseIsDown(evt, dojo.byId("ss_cal_dayGridHour" + instanceId))});
					dojo.connect(dojo.byId("ss_cal_dayGridAllDay" + instanceId), "onmousedown", function(evt) { ss_cal_CalAllDayEvent.mouseIsDown(evt, dojo.byId("ss_cal_dayGridAllDay" + instanceId))});
	                this.dayGridCreated = true;
	            }
				this.showCalendarDaysDescription(this.firstDayToShow);
	        } else if (this.currentType == "month") {
	        	var monthViewInfo = ss_cal_CalData.getMonthViewInfo(this.currentDate);
	    		this.firstDayToShow = monthViewInfo.beginView;
				if (dojo.byId("ss_cal_DayGridMaster" + instanceId)) {
	            	dojo.style(dojo.byId("ss_cal_DayGridMaster" + instanceId), "display", "none");
				}
	            dojo.style(dojo.byId("ss_cal_MonthGridMaster" + instanceId), "display", "block");
	            
				this.drawMonthGrid(ss_cal_CalData.today, this.currentDate, monthViewInfo);
				this.showCalendarMonthDescription();
	        }
	    },
	    
	    showCalendarMonthDescription: function(currentDate) {
	    	this.showCalendarDescription(getMonthName(this.currentDate) + ", " + this.currentDate.getFullYear());
	    },
	    
	    showCalendarDaysDescription: function(firstDayToShow) {
	    	// Bugzilla 512176:
	    	//    Rewrote to use dojo's locale based date formatter to
	    	//    construct the correct format.
// Was:    	var descr = firstDayToShow.getDate() + " " + getMonthNameShort(firstDayToShow) + " " + firstDayToShow.getFullYear();
			var descr = dojo.date.locale.format(firstDayToShow, {formatLength: calendarNav_DateFormat, selector:"date", locale: that.locale.lang});
	
	    	var lastDayToShow = dojo.date.add(firstDayToShow, "day", this.gridSize - 1);
	    	if (daysDiff(firstDayToShow, lastDayToShow) != 0) {
	    		descr += " - ";
// Was:    		descr += lastDayToShow.getDate() + " " + getMonthNameShort(lastDayToShow) + " " + lastDayToShow.getFullYear();	
				descr += dojo.date.locale.format(lastDayToShow, {formatLength: calendarNav_DateFormat, selector:"date", locale: that.locale.lang});
	    	}
	    	
	    	this.showCalendarDescription(descr);
	    },
	
	    showCalendarDescription: function(descr) {
	        var calViewDescription = dojo.byId(viewDatesDescriptionsFieldId);
	    	if (calViewDescription) {
	    		calViewDescription.innerHTML = descr;
	    	}
	    },
		
		dayOffsetSizes: {
			1: 100,
			3: 33.3333,
			5: 20,
			7: 14.2857,
			14: 7.1428
		},
		
		dayGridPositions: {
			1: [0],
			3: [0, 33.3333, 66.6666],
			5: [0, 20, 40, 60, 80],
			7: [0, 14.2857, 28.5714, 42.8571, 57.1428, 71.4285, 85.7142],
			14: [0, 7.1428, 14.2857, 21.42857, 28.5714, 35.7142, 42.8571, 49.9999, 57.1428, 64.2857, 71.4285, 78.5714, 85.7142, 92.8571]
		},
	
	    drawDayGrid: function(containerId, today, howManyDays, firstDayToShow, ruleId, justVertical) {
	        var container = dojo.byId(containerId);
	        var hourOffset = 0;
	        var dayOffsetSize = this.dayOffsetSizes[howManyDays];
	        
	        var todayMarker = dojo.byId(containerId + "_Today");
	        if (!todayMarker) {
	            var todayMarker = document.createElement("div");
	            todayMarker.className = "ss_cal_todayMarker";
				todayMarker.setAttribute("id", containerId + "_Today");
				todayMarker.style.height =  "100%";
	            todayMarker.style.display = "none";
	            container.appendChild(todayMarker);
	        }
	        
	        dojo.query(".ss_cal_dayRule", container).forEach("dojo._destroyElement(item);");	
	
			var todayOffsetInView = 0;
			var todayVisible = false;
			var currentDayToShow = firstDayToShow;
	        for (var x = 0; x < howManyDays; x++) {
	        	var isToday = ss_cal_CalData.isToday(currentDayToShow);
	        	if (!todayVisible) {
	        	   	todayVisible = todayVisible || isToday;
	        	}
	        	if (isToday) {
	        		todayOffsetInView = x;
	        	}
	        	
	            var vrule = document.createElement("div");
	            vrule.setAttribute("id", ruleId + x);
	            vrule.className = "ss_cal_dayRule";
	            vrule.style.left = this.dayGridPositions[howManyDays][x] + "%";
	            container.appendChild(vrule);
	            currentDayToShow = dojo.date.add(currentDayToShow, "day", 1);
	        }
	        
	        if (todayVisible) {
				todayMarker.style.width = dayOffsetSize + "%";
	            todayMarker.style.left = (todayOffsetInView * dayOffsetSize) + "%";
	            dojo.style(todayMarker, "display", "block");
	        } else {
	        	dojo.style(todayMarker, "display", "none");
	        }
	
	        if (!justVertical) {
	            while (hourOffset < 1000) {
	                hourOffset += 21;
	                var hrule = document.createElement("div");
	                hrule.className ="ss_cal_hr ss_cal_hrHalfHour";
	                hrule.style.top = hourOffset + "px";
	                container.appendChild(hrule);
	                hourOffset += 21;
	                hrule = document.createElement("div");
	                hrule.className = "ss_cal_hr ss_cal_hrHour";
	                hrule.style.top = hourOffset + "px";
	                container.appendChild(hrule);
	            }
	        }
	    },
	
	
	    drawDayHeader: function(containerId, howManyDays, firstDayToShow) {
	        var container = dojo.byId(containerId);
	        var hourOffset = 0;
	        var dayOffsetSize = this.dayOffsetSizes[howManyDays];
	
	        dojo.query(".ss_cal_gridHeaderText", container).forEach("dojo._destroyElement(item);");	

			var currentDayToShow = firstDayToShow;
	        for (var x = 0; x < howManyDays; x++) {
	            var badge = document.createElement("div");
	            badge.className = "ss_cal_gridHeaderText";
	
	            if (ss_cal_CalData.isToday(currentDayToShow)) {
	                badge.className += " ss_cal_gridHeaderTextToday";
	            }
	            badge.style.left = this.dayGridPositions[howManyDays][x] + "%";
	            badge.style.width = dayOffsetSize + "%";
	               
	            var changeViewLink = document.createElement("a");
	            changeViewLink.href = "javascript: // ;";
	            changeViewLink.innerHTML = getDayHeader(new Date(currentDayToShow.getFullYear(), currentDayToShow.getMonth() , currentDayToShow.getDate()));
	        	var yyyy = currentDayToShow.getFullYear();
	        	var mm = currentDayToShow.getMonth();
	        	var dd = currentDayToShow.getDate();
	        	(function(yyyy, mm, dd) {
					dojo.connect(changeViewLink, "onclick", function(evt) {
							ss_cal_Events.switchView('daydirect', yyyy, mm, dd);
	        			});
	        	})(yyyy, mm, dd);
			    badge.appendChild(changeViewLink);
	            
	                                    
	            container.appendChild(badge);
	            
	            currentDayToShow = dojo.date.add(currentDayToShow, "day", 1);
	        }
	    },
		
		monthGridDayBadgeVOffsets: [0, 14.285714285714285, 28.57142857142857, 42.857142857142854, 57.14285714285714, 71.42857142857142, 85.7142857142857],
		
		monthGridDayBadgeHOffsets: {
							4: [0, 25, 50, 75],
							5: [0, 20, 40, 60, 80],
							6: [0, 16.666666666666664, 33.33333333333333, 49.99999999999999, 66.66666666666666, 83.33333333333331]
						},		
						
		monthVOffsets: [14.285714285714285, 28.57142857142857, 42.857142857142854, 57.14285714285714, 71.42857142857142, 85.7142857142857, 99.99999999999997],
		
		monthHOffsets: {
							4: [25, 50, 75],
							5: [20, 40, 60, 80],
							6: [16.666666666666664, 33.33333333333333, 49.99999999999999, 66.66666666666666, 83.33333333333331]
						},// hOffsetSize*i
	
	    drawMonthGrid: function(today, currentDate, monthViewInfo) {
	        var container = dojo.byId("ss_cal_monthGrid" + instanceId);
	        this.monthGridWeeks = 5;
	        var hOffsetSize = 20;   // (1.0 / this.monthGridWeeks) * 100.0;
	        if (monthViewInfo.daysInMonth > 35) {
	        	this.monthGridWeeks = 6;
				hOffsetSize = 16.666666666666664; // (1.0 / this.monthGridWeeks) * 100.0;
	        } else if (monthViewInfo.daysInMonth < 35) {
	        	this.monthGridWeeks = 4;
				hOffsetSize = 25; // (1.0 / this.monthGridWeeks) * 100.0;
	        }
	        var vOffsetSize = 14.285714285714285; // (1.0 / 7) * 100.0;
	        var header = dojo.byId("ss_cal_monthGridHeader" + instanceId);
	
	
			var todayMarker = dojo.byId("ss_cal_monthGridToday" + instanceId);
			if (!todayMarker) {
				todayMarker = document.createElement("div");
				todayMarker.className = "ss_cal_todayMarker";
				todayMarker.setAttribute("id", "ss_cal_monthGridToday" + instanceId);
				container.appendChild(todayMarker);
			}
	        todayMarker.style.width = vOffsetSize + "%";
	        todayMarker.style.height = hOffsetSize + "%";
	        todayMarker.style.display = "none";
	        
	
	        var toDestroy = [];
	        var v = container.firstChild;
	        while (v) {
	            if (v.className != "ss_cal_todayMarker") { toDestroy.push(v); };
	            v = v.nextSibling;
	        }
	        while (toDestroy.length) { dojo._destroyElement(toDestroy.pop()); }
	
	        var v = dojo.byId("ss_cal_monthGridHeader" + instanceId).firstChild;
	        while (v) {
	            if (dojo.hasClass(v, "ss_cal_gridHeaderText")) { toDestroy.push(v); };
	            v = v.nextSibling;
	        }
	        while (toDestroy.length) { dojo._destroyElement(toDestroy.pop()); }
	
	
	        for (var x = 0; x < 7; x++) {
	            var vrule = document.createElement("div");
	            vrule.className = "ss_cal_monthVRule";
	            vrule.style.left = this.monthVOffsets[x] + "%";
	            container.appendChild(vrule);
	        }
			
			for (var x = 0; x < 7; x++) {
	            var badge = document.createElement("div");
	            badge.className = "ss_cal_gridHeaderText";
	            badge.style.left = this.monthGridDayBadgeVOffsets[x] + "%";
	            badge.style.width = vOffsetSize + "%";
	            badge.style.top = "0%";
	            var badgeText = document.createTextNode(that.locale.dayNamesShort[(x + weekFirstDay) % 7]);
	            badge.appendChild(badgeText);
	            header.appendChild(badge);
	        }
	        
			for (var i = 0; i < this.monthHOffsets[this.monthGridWeeks].length; i++) {
	            var hrule = document.createElement("div");
	            hrule.className = "ss_cal_monthHRule";
	            hrule.style.top = this.monthHOffsets[this.monthGridWeeks][i] + "%";
	            container.appendChild(hrule);
			}

			var counter = 0;
			if (monthViewInfo.startViewDate.getMonth() != currentDate.getMonth()) {
				var nextDateY = monthViewInfo.startViewDate.getFullYear();
				var nextDateM = monthViewInfo.startViewDate.getMonth();
				var nextDateD = monthViewInfo.startViewDate.getDate();
				var daysInMonth = dojo.date.getDaysInMonth(monthViewInfo.startViewDate);
				for (var x = nextDateD; x <= daysInMonth; x++) {
					this.drawMonthGridDayBadge(new Date(nextDateY, nextDateM, x), counter++, container, vOffsetSize, hOffsetSize, "ss_cal_monthGridDayBadge");
				}
			}
			
			var daysInMonth = dojo.date.getDaysInMonth(currentDate);
			for (var x = 1; x <= daysInMonth; x++) {
				this.drawMonthGridDayBadge(new Date(currentDate.getFullYear(), currentDate.getMonth(), x), counter++, container, vOffsetSize, hOffsetSize, "ss_cal_monthGridDayBadgeCurrent");
			}
			
			if (monthViewInfo.endViewDate.getMonth() != currentDate.getMonth()) {
				var nextDateY = monthViewInfo.endViewDate.getFullYear();
				var nextDateM = monthViewInfo.endViewDate.getMonth();
				var nextDateD = monthViewInfo.endViewDate.getDate();
				for (var x = 1; x <= nextDateD; x++) {
					this.drawMonthGridDayBadge(new Date(nextDateY, nextDateM, x), counter++, container, vOffsetSize, hOffsetSize, "ss_cal_monthGridDayBadge");
				}
			}
	    },
	    
	    drawMonthGridDayBadge : function(date, counter, container, vOffsetSize, hOffsetSize, className) {
	     	var d = (counter % 7);
	        var w = Math.floor(counter / 7);
			
			var isToday = ss_cal_CalData.isToday(date);
	    	if (isToday) {
	    		var allDayBadge = dojo.byId("ss_cal_monthGridToday" + instanceId);
				allDayBadge.style.left = this.monthGridDayBadgeVOffsets[d] + "%";
	            allDayBadge.style.top = this.monthGridDayBadgeHOffsets[this.monthGridWeeks][w] + "%";
	            dojo.style(allDayBadge, "display", "block");
	    	} else {
				var allDayBadge = document.createElement("div");
		        allDayBadge.style.top = this.monthGridDayBadgeHOffsets[this.monthGridWeeks][w] + "%";
		        allDayBadge.style.left = this.monthGridDayBadgeVOffsets[d] + "%";
		        allDayBadge.style.width = this.monthGridDayBadgeVOffsets[1] + "%";
		        allDayBadge.style.height = this.monthGridDayBadgeHOffsets[this.monthGridWeeks][1] + "%";	
				
				allDayBadge.style.position = "absolute";
						
				container.appendChild(allDayBadge);
			}
			
			(function() {
				dojo.connect(allDayBadge, "onmouseover", function(evt) {
					if (!readOnly) {
						dojo.addClass(evt.target, "ss_cal_monthGridDayBadgeOverHighlight");
					}
       			});
            })();
			
			(function() {
				dojo.connect(allDayBadge, "onmouseout", function(evt) {
					dojo.removeClass(evt.target, "ss_cal_monthGridDayBadgeOverHighlight");
       			});
            })();
			
		    var url = addEntryURL;
		    var haveAddEntryURL = ((null != url) && (0 < url.length));
		    url += "&year=" + date.getFullYear();
		    url += "&month=" + date.getMonth();
		    url += "&dayOfMonth=" + date.getDate();
            (function() {
            	var setOnclickHandler;
            	
            	setOnclickHandler = true;
        		// Have we already set the onclick handler for the "today" object?
            	if ( isToday && ss_cal_Grid.onClickSetForToday )
            	{
           			// Yes, we don't need to set the onclick handler again.
           			setOnclickHandler = false;
            	}
            	
            	// Do we need to set the onclick handler?
            	if ( setOnclickHandler )
            	{
            		// Yes
            		if ( isToday )
            		{
            			// Remember that we have set the onclick handler for the "today" object so we don't do it again.
            			ss_cal_Grid.onClickSetForToday = true;
            		}
            		
            		dojo.connect(allDayBadge, "onclick", function(evt) {
            			if (haveAddEntryURL) {		
            				ss_openUrlInPortlet(url, true);
            			}
            			return false;
            		});
            	}
            })();							


	        var badge = document.createElement("div");
	        badge.className = className;
	        if (isToday) {
	            badge.className += " ss_cal_monthGridDayBadgeToday";
	        }

	        badge.style.left = this.monthGridDayBadgeVOffsets[d] + "%";
	        badge.style.top = this.monthGridDayBadgeHOffsets[this.monthGridWeeks][w] + "%";

			if (haveAddEntryURL) {
	            var dateAddEntry = document.createElement("div");
	            dateAddEntry.className = "ss_cal_monthGridDayBadgeDateAdd";
			    dateAddEntry.innerHTML = "\u00A0";	// nbsp
	            (function() {
					dojo.connect(dateAddEntry, "onclick", function(evt) {
				    	ss_openUrlInPortlet(url, true);
						return false;
	       			});
	            })();							
			    badge.appendChild(dateAddEntry);
			    
				(function() {
					dojo.connect(dateAddEntry, "onmouseover", function(evt) {
						if (!readOnly) {
							dojo.addClass(evt.target, "ss_cal_monthGridDayBadgeOverHighlight");
						}
	       			});
	            })();
				
				(function() {
					dojo.connect(dateAddEntry, "onmouseout", function(evt) {
						dojo.removeClass(evt.target, "ss_cal_monthGridDayBadgeOverHighlight");
	       			});
	            })();
			}

            var changeViewLink = document.createElement("a");
            changeViewLink.href = "javascript: // ;";
            changeViewLink.innerHTML = date.getDate();
            (function(date) {
				dojo.connect(changeViewLink, "onclick", function(evt) {
							ss_cal_Events.switchView('daydirect', date.getFullYear(), date.getMonth(), date.getDate());
	        			});
            })(date);
		    badge.appendChild(changeViewLink);	               
	               
	        container.appendChild(badge);
	    },    
	
	    drawHourMarkers: function(containerId) {
	        var container = dojo.byId(containerId);
			
			if (container.childNodes.length > 0) {
				// draw only once
				return;
			}
			
	        var hour = 0;
	        while (hour < 24) {
	            var hmark = document.createElement("div");
	            if (hour > 0) {
	                hmark.className = "ss_cal_hr ss_cal_hrHour";
	            } else {
	                hmark.className = "ss_cal_hr";
	            }
	            hmark.style.top = (hour * 42) + "px";
	            var tick = document.createElement("div");
	            tick.className = "ss_cal_timeHead";
				var tickText = document.createTextNode(ss_calendar_formatHour(hour, that.locale.lang));
	            tick.appendChild(tickText);
	            hmark.appendChild(tick);
	            container.appendChild(hmark);
	            hour += 1;
	        }
	    },
	
	
	    realignVRules: function(fixupId, alignToId) {
	        for (var x = 0; x < 6; x++) {
	            var fixup = dojo.byId(fixupId + x);
	            if (fixup) {
	                var alignTo = dojo.byId(alignToId + x);
	                fixup.style.left = alignTo.offsetLeft + "px";
	            }
	        }
	    },
	    
	    fullDayRedraw: function() {
	        outer = dojo.byId("ss_cal_dayGridWindowOuter" + instanceId);
	        inner = dojo.byId("ss_cal_dayGridWindowInner" + instanceId);
	
	        dojo.animateProperty({node: outer,
	              				  properties: { height: {start: 500, end: 1008} },
	              				  duration: 200
	              				 }).play();
	   	    dojo.animateProperty({node: inner, 
	   	    					  properties: {top: {start: -3 - (workDayStart * 42), end: -3} },
	   	    					  duration: 200
	   	    					 }).play();	   	    
	        
	        var celendarHoursSelector = dojo.byId(calendarHoursSelectorId);
			if (celendarHoursSelector) {
		        var children = celendarHoursSelector.childNodes;
		        var arrow;
		        for (var i = 0; i < children.length; i++) {
		        	var a = children[i].nodeValue;
		        	if (children[i].nodeValue && children[i].nodeValue.indexOf(that.locale.workDayGridTitle) > -1) {
		        		children[i].nodeValue = that.locale.fullDayGridTitle;
		        		break;
		        	}
		        }
			}
	    },
	
	    fullDayGrid: function() {
	    	this.fullDayRedraw();
	        
			ss_cal_Grid.activateGrid(ss_cal_Grid.currentType);
	    	ss_cal_Events.redrawAll();
	    	
			if (calendarDataProvider) {
    			calendarDataProvider.stickyCalendarDisplaySettings({dayViewType : "fullday"});
			}
	    },
	
	    workDayGrid: function() {
	        var outer = dojo.byId("ss_cal_dayGridWindowOuter" + instanceId);
	        var inner = dojo.byId("ss_cal_dayGridWindowInner" + instanceId);
			
	       	dojo.animateProperty({node: outer, properties: { height: {start: 1008, end: 500} }, duration: 200}).play();
	       	dojo.animateProperty({node: inner, properties: { top: {start: -3, end: -3 - (workDayStart* 42)} }, duration: 200}).play();
	        
	        var celendarHoursSelector = dojo.byId(calendarHoursSelectorId);
			if (celendarHoursSelector) {
		        var children = celendarHoursSelector.childNodes;
		        var arrow;
		        for (var i = 0; i < children.length; i++) {
		        	if (children[i].nodeValue && children[i].nodeValue.indexOf(that.locale.fullDayGridTitle) > -1) {
		        		children[i].nodeValue = that.locale.workDayGridTitle;
		        		break;
		        	}
		        }
			}
			ss_cal_Grid.activateGrid(ss_cal_Grid.currentType); // couse of IE... 
	    	ss_cal_Events.redrawAll();
	    	
			if (calendarDataProvider) {
	    		calendarDataProvider.stickyCalendarDisplaySettings({dayViewType : "workday"});
			}
	    },
	    
	    highlightDaySelectorMenuIcon: function() {
			// Return if selector isn't being used
			if (dojo.byId(viewSelectorHrefIds["days1"]) == null) return;

	    	// all to inactive							
	    	for (var i in viewSelectorHrefIds) {
	    		dojo.byId(viewSelectorHrefIds[i]).className = viewSelectorHrefClasses[i];
	    	}
	    	
	    	// set current to active state
	    	var hrefIndex = "month";
	    	if (ss_cal_Grid.currentType == "day") {
	    		hrefIndex = "days" + ss_cal_Grid.gridSize;
	    	}
	     	dojo.byId(viewSelectorHrefIds[hrefIndex]).className = viewSelectorHrefClasses[hrefIndex] + "Active";
	    }
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	var ss_cal_CalAllDayEvent = {
	    currGrid: null,
	    currDispId: null,
	    currDay: null,
	
	    allDayCount:  {},
	    currEventData: {},
	    mouseUpHanle: null,
	
	    mouseIsDown: function(evt, grid) {
	        ss_cal_Events.cancelHover(false);
	        //evt = (evt) ? evt : ((event) ? event : null);
	        gridX = evt.clientX - dojo.coords(grid).x;
	        gridWidth = grid.offsetWidth;
	        // Calculate day offsets.
	        dayOffset = Math.floor((gridX / gridWidth)  / (1.0 / ss_cal_Grid.gridSize));
	        var firstDayOnGrid = ss_cal_Grid.firstDayToShow;
			this.currDay = dojo.date.add(firstDayOnGrid, "day", dayOffset);
	        
	        hourOffset = this.recordHourOffset(this.currDay.getFullYear(), this.currDay.getMonth(), this.currDay.getDate());
	        this.currDispId = ss_cal_drawCalendarEvent(grid.id, ss_cal_Grid.gridSize, 1, 0, dayOffset, hourOffset, -1, that.locale.allDay, "", "", false, {calendarId: defaultCalendarId});
	        this.resetGridHeight();
	        mouseUpHandle = dojo.connect(dojo.body(), "onmouseup", this, "mouseIsUp");       
	        this.currEventData = {};
	        this.currEventData.startDate = this.currDay;
	        this.currEventData.start = -1;
	        this.currEventData.dur = 0;
	    },
	
	    recordHourOffset: function(year, month, dayOfMonth) {
	        return (this.recordEvent(year, month, dayOfMonth, 1) - 1) * 0.5;
	    },
	
	    recordEvent: function(year, month, dayOfMonth, incr) {
	        if (typeof this.allDayCount[year + "/" + month + "/" + dayOfMonth] == "undefined") { this.allDayCount[year + "/" + month + "/" + dayOfMonth] = 0; }
	        this.allDayCount[year + "/" + month + "/" + dayOfMonth] += incr;
	        return this.allDayCount[year + "/" + month + "/" + dayOfMonth];
	    },
	
	    resetGridHeight: function() {
	        var maxEvents = 0;
	        for (var i in this.allDayCount) {
	            if (this.allDayCount[i] > maxEvents) { maxEvents = this.allDayCount[i]; }
	        }
	        if (isIE) {
	            dojo.byId("ss_cal_dayGridAllDay" + instanceId).style.height = ((maxEvents + 1) * 21) + "px";
	            // Force IE to recalculate the offset of the next blocks
	            dojo.byId("ss_cal_dayGridWindowOuter" + instanceId).style.top = "1px";
	            dojo.byId("ss_cal_dayGridWindowOuter" + instanceId).style.top = "0px";
	        } else {
	            dojo.animateProperty({node: "ss_cal_dayGridAllDay" + instanceId, properties: { height: {end: ((maxEvents + 1) * 21)} }, duration: 100}).play();
	        }
	    },
	
	    mouseIsUp: function(evt) {
	        if (!dijit.isCollapsed()) { clearSelection(); }
	        dojo.disconnect(mouseUpHandle);
	        
	        ss_cal_newEventInfo(evt, this);
	    },
	
	    deleteEvent: function(id) {
	        fadeAndDestroy(dojo.byId("calevt" + instanceId + id).parentNode, 200);
	    },
	
	    deleteCurrentEvent: function() {
	        this.deleteEvent(this.currDispId);
	        this.recordEvent(this.currDay.getFullYear(), this.currDay.getMonth() , this.currDay.getDate(), -1);
	        this.resetGridHeight()
	    },	
	
	
	    reset: function() {    
	        for (var i in this.allDayCount) {
	        	delete this.allDayCount[i];
	        }
	    }
	    
	
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	var ss_cal_CalEvent = {
	
	    currGrid: null,
	    currDispId: null,
	    currEventData: {},
	    dragHandle: null,
	    mouseUpHandle: null,
	
	
	    mouseIsDown: function(evt, grid) {
	        ss_cal_Events.cancelHover(false);
	        //evt = (evt) ? evt : ((event) ? event : null);
	        // The offset is affected by the enclosing "window" divs, so we factor it here.
	        gridX = evt.clientX - dojo.coords(grid).x;
	        gridY = evt.clientY - dojo.coords(grid).y;
	
	        gridWidth = grid.offsetWidth;
	        //console.log("mouseIsDown ex:%d ey: %d", evt.clientX, evt.clientY);
	        //console.log("mouseIsDown  x:%d y:%d w:%d", gridX, gridY, gridWidth);
	        //console.log(dojo.coords(grid));
	        //console.log(dojo.coords(grid,true));
	 
	        currGrid = grid;
	        dragHandle = dojo.connect(dojo.body(), "onmousemove", this, "whataDrag");
	        mouseUpHandle = dojo.connect(dojo.body(), "onmouseup", this, "mouseIsUp");
	    
	        // Calculate hour and day offsets.
	        hourOffset = Math.floor(gridY / 42);
	        if ((gridY % 42) > 21) { hourOffset += 0.5 }
	        var dayOffset = Math.floor((gridX / gridWidth)  / (1.0 / ss_cal_Grid.gridSize));
	        
			var firstDayOnGrid = ss_cal_Grid.firstDayToShow;
	
			var currDay = dojo.date.add(firstDayOnGrid, "day", dayOffset);
			currDay.setHours(Math.floor(hourOffset));
			currDay.setMinutes(0);
			if ((gridY % 42) > 21) {
				currDay.setMinutes(30);
			}
	        this.currDispId = ss_cal_drawCalendarEvent(grid.id, ss_cal_Grid.gridSize, 1, 0, dayOffset, hourOffset, 30, "", "", "", false, {calendarId: defaultCalendarId});
	        evt.cancelBubble = true;
	        this.currEventData = {};
	        this.currEventData.startDate = currDay;
	        this.currEventData.start = hourOffset;
	        this.currEventData.dur = 30;
	    },
	
	    whataDrag: function(evt) {
	        //evt = (evt) ? evt : ((event) ? event : null);
	        var gridQuantum = 3.5 * 6;  // 30 minutes
	        //console.log("whataDrag: ", this.currDispId); 
	        var currBox = dojo.byId("calevt" + instanceId + this.currDispId);
			var currBoxOut = dojo.byId("calevt_out" + instanceId + this.currDispId);
			
	        gridX = evt.clientX + document.body.scrollLeft;
	        gridY = evt.clientY - dojo.coords(currGrid).y - currBox.parentNode.offsetTop;
	        if (gridY > 12) {
	            gridY = gridQuantum + Math.floor(gridY / gridQuantum) * gridQuantum;
	            currBox.style.height = (gridY - 4) + "px";
				currBoxOut.style.height = gridY + "px";
	        }
	        var duration = Math.floor((((parseInt(currBox.style.height) + 4) / 42) * 60));
	        var durationLabel = that.locale.minutesShortLabel;
	        durationLabel = durationLabel.replace("{0}",String(duration));
	        currBox.innerHTML = durationLabel;
	        this.currEventData.dur = duration;
	
	        if (!dijit.isCollapsed()) { clearSelection(); }
	        evt.cancelBubble = true;
	        return false;
	    
	    },
	
	    mouseIsUp: function(evt) {
	        if (!dijit.isCollapsed()) { clearSelection(); }
	        dojo.disconnect(dragHandle);
	        dojo.disconnect(mouseUpHandle);
	        ss_cal_newEventInfo(evt, this);
	    },
	
	    deleteEvent: function(id) {
	        fadeAndDestroy(dojo.byId("calevt" + instanceId + id).parentNode, 200);
	    },
	
	    deleteCurrentEvent: function() { 
			this.deleteEvent(this.currDispId); 
		}
	
	
	}
	
	var ss_cal_Events = {
		eventsTypes : ["event", "creation", "activity", "virtual"],
		CONTINUES_LEFT : 0,
		CONTINUES_RIGHT : 1,
		CONTINUES_LEFT_AND_RIGHT : 2,
		
		eventsType : 0,
		
	    displayId: 0,
	    
	    eventData: {},
	    eventIdsByEntryId: {},
	    eventBinderIds: [],
	    
	    collisions: {"event": {}, "creation": {}, "activity": {}, "virtual": {}},
	    collisionI: {},
	    order: {"event": {}, "creation": {}, "activity": {}, "virtual": {}},	// eventType -> date(YYYY/MM/DD) -> events key
	    
	    monthGridEvents: [],
		monthEventIds: [],
	    dayGridEvents: {},
		
	    addEvents: function(newEvents) {
			var year, month, day, hours, minutes; 
	        for (var i = 0; i < newEvents.length; i++) {
	            var nei = newEvents[i];
	            // already loaded?
	            if (this.eventData[nei.eventId]) {
	            	continue;
	            }
				
				year =  nei.startDate.substr(0, 4) * 1;
				month =  nei.startDate.substr(4, 2) * 1;
				day =  nei.startDate.substr(6, 2) * 1;
				hours =  nei.startDate.substr(9, 2) * 1;
				minutes =  nei.startDate.substr(11, 2) * 1;
	            nei.start = hours + (minutes/60);
	            nei.startDate = new Date(year, month - 1, day, hours, minutes);

				year =  nei.endDate.substr(0, 4) * 1;
				month =  nei.endDate.substr(4, 2) * 1;
				day =  nei.endDate.substr(6, 2) * 1;
				hours =  nei.endDate.substr(9, 2) * 1;
				minutes =  nei.endDate.substr(11, 2) * 1;
	            nei.endDate = new Date(year, month - 1, day, hours, minutes);
				if (nei.endDate.getHours() === 0 && nei.endDate.getMinutes() === 0 && +nei.startDate != +nei.endDate && 
					!nei.allDay) {
					nei.endDate = new Date(nei.endDate - 1);
				}
	
	           	this.eventData[nei.eventId] = nei;
	            if (typeof this.eventIdsByEntryId[nei.entryId] == "undefined") {
	            	this.eventIdsByEntryId[nei.entryId] = [];
	            }
	            this.eventIdsByEntryId[nei.entryId].push(nei.eventId);
	            
	            this.setupDisplayRulesAndOrder(nei);
	        }
	
			this.reorderEvent();
	    },
		
	    addEventBinderIds: function(newEventBinderIds) {
	        for (var i = 0; i < newEventBinderIds.length; i++) {
	            var nebi = newEventBinderIds[i];
	            // already loaded?
	            if (!this.eventBinderIds[nebi.binderId]) {
	            	this.eventBinderIds.push(nebi.binderId);
	            }
	        }
	    },
		
	    removeAllEvents: function() {
			this.eventsType = 0;
			
		    this.displayId =  0;
		    
		    this.eventData =  {};
		    this.eventIdsByEntryId = {};
		    this.eventBinderIds = [];
		    
		    this.collisions =  {"event": {}, "creation": {}, "activity": {}, "virtual": {}};
		    this.collisionI =  {};
		    this.order =  {"event": {}, "creation": {}, "activity": {}, "virtual": {}};
		    
		    this.monthGridEvents = [];
			this.monthEventIds = [];
		    this.dayGridEvents = {};
	    },
	    
	    normalizedEventsType:  function() {
	    	var et = this.eventsType;
			if (3 == et) {	// Treat "virtual"...
				et = 0;		// ...like "event".
			}
	    	return( et );
	    },		
	    
	    removeEntryEvents: function(entryId) {
	    	if (!this.eventIdsByEntryId[entryId]) {
	    		return false;
	    	}
	    	for (var i = 0; i < this.eventIdsByEntryId[entryId].length; i++) {
	    		this.removeEvent(this.eventData[this.eventIdsByEntryId[entryId][i]]);
	    		delete this.eventData[this.eventIdsByEntryId[entryId][i]];
	    	}
	    	this.reorderEvent();
	    },
	    
	    removeEvent: function(event) {
	    	if (!event) {
	    		return;
	    	}
			this.removeDayDisplayRules(event);
        	this.removeMonthDisplayRules(event);
        	this.unsortEvent(event);
	    },
	    
		reorderEvent: function() {
			for (var i in this.order) {
				if (typeof this.order[i] != "undefined") {
					for (var j in this.order[i]) {
						if (typeof this.order[i][j] != "undefined") {	
		    				this.order[i][j].sort();
		    			}
		    		}
	    		}
	        }
	    },
	
	    incrCollision: function(eventType, t) {
	        if (typeof this.collisions[eventType][t] == "undefined") { 
	        	this.collisions[eventType][t] = 0; 
	        }
	        this.collisions[eventType][t]++;
	        return this.collisions[eventType][t];
	    },
	    
		decrCollision: function(eventType, t) {
	        if (typeof this.collisions[eventType] == "undefined") { 
	        	return 0;
	        }
	        if (typeof this.collisions[eventType][t] == "undefined") { 
	        	return 0; 
	        }
	        this.collisions[eventType][t]--;
	        return this.collisions[eventType][t];
	    },
		
		setOrder: function(eventType, key, v) {
	    	if (typeof this.order[eventType][key] == "undefined") {
	    		this.order[eventType][key] = [];
	    	}    		
	    	this.order[eventType][key].push(v);
	    },
		
		setupDisplayRulesAndOrder: function(event) {
	    	var date = event.startDate;
	    	
			var fullMonthKey = date.getFullYear() + "/" + fullWithZeros(date.getMonth() ) + "/" + fullWithZeros(date.getDate());
			var key = date.getFullYear() + "/" + date.getMonth()  + "/" + date.getDate();
			
			this.incrCollision(event.eventType, key);
	    	this.incrCollision(event.eventType, key + "/" + date.getHours());
			// this.setMCollision(event.eventType, fullMonthKey, event.eventId);
			this.setOrder(event.eventType, key, (Math.floor(event.start * 10) + 1011) + "/" + event.eventId);
			
			date = dojo.date.add(date, "day", 1);
			var daysToEndOfEvent = daysDiff(date, event.endDate);
			while (daysToEndOfEvent >= 0) {
				fullMonthKey = date.getFullYear() + "/" + fullWithZeros(date.getMonth() ) + "/" + fullWithZeros(date.getDate());
				key = date.getFullYear() + "/" + date.getMonth()  + "/" + date.getDate();
					
				this.incrCollision(event.eventType, key);
	    		this.incrCollision(event.eventType, key + "/0");
				// this.setMCollision(event.eventType, fullMonthKey, event.eventId);
				this.setOrder(event.eventType, key, (0 + 1011) + "/" + event.eventId);
				
	    		date = dojo.date.add(date, "day", 1);
				daysToEndOfEvent--;
	    	}
	    },
	    
	    removeDayDisplayRules: function(event) {
	    	var date = event.startDate;
	    	
	    	this.decrCollision(event.eventType, date.getFullYear() + "/" + date.getMonth()  + "/" + date.getDate() + "/" + date.getHours());
	    	date = dojo.date.add(date, "day", 1);
			var daysToEndOfEvent = daysDiff(date, event.endDate);
	    	while (daysToEndOfEvent >= 0) {
	    		this.decrCollision(event.eventType, date.getFullYear() + "/" + date.getMonth()  + "/" + date.getDate() + "/0");
	    		date = dojo.date.add(date, "day", 1);
				daysToEndOfEvent--;
	    	}
	    },
	    
	    removeMonthDisplayRules: function(event) {
	        var date = event.startDate;
			var daysToEndOfEvent = daysDiff(date, event.endDate);
	    	while (daysToEndOfEvent >= 0) {
				var key = date.getFullYear() + "/" + fullWithZeros(date.getMonth() ) + "/" + fullWithZeros(date.getDate());
		        this.decrCollision(event.eventType, date.getFullYear() + "/" + date.getMonth()  + "/" + date.getDate());
				date = dojo.date.add(date, "day", 1);
				daysToEndOfEvent--;
	    	}
	    },

	    collisionCount: function(eventType, year, month, dayOfMonth, start) {
	        return this.collisions[eventType][year + "/" + month + "/" + dayOfMonth + "/" + Math.floor(start)];
	    },

	    collisionIndex: function(eventType, year, month, dayOfMonth, start) {
	        var t = year + "/" + month + "/" + dayOfMonth + "/" + Math.floor(start);
	        if (typeof this.collisionI[eventType] == "undefined") { 
	        	this.collisionI[eventType] = {}; 
	        }
	        if (typeof this.collisionI[eventType][t] == "undefined") { 
	        	this.collisionI[eventType][t] = 0; 
	        }
	        return this.collisionI[eventType][t]++;
	    },
	    
		unsortEvent: function(event) {
	    	if (typeof this.order[event.eventType] == "undefined") {
	    		return;
	    	}
	    	
	    	var date = event.startDate;
	    	var key = date.getFullYear() + "/" + date.getMonth() + "/" + date.getDate();
	    	if (typeof this.order[event.eventType][key] == "undefined") {
	    		return;
	    	}
	    	
	    	for (var i = 0; i < this.order[event.eventType][key].length; i++) {
	    		var value = (Math.floor(event.start * 10) + 1011) + "/" + event.eventId;
	    		if (this.order[event.eventType][key][i] == value) {
	    			this.order[event.eventType][key].splice(i, 1);
	    		}
	    	}
	    	
	    	date = dojo.date.add(date, "day", 1);
			var daysToEndOfEvent = daysDiff(date, event.endDate);
	    	while (daysToEndOfEvent >= 0) {
	    		key = date.getFullYear() + "/" + date.getMonth() + "/" + date.getDate();
	    		if (typeof this.order[event.eventType][key] != "undefined") {
	    			for (var i = 0; i < this.order[event.eventType][key].length; i++) {
			    		var value = (0 + 1011) + "/" + event.eventId;
			    		if (this.order[event.eventType][key][i] == value) {
			    			this.order[event.eventType][key].splice(i, 1);
			    		}
			    	}
	    		}  
				date = dojo.date.add(date, "day", 1);
				daysToEndOfEvent--;
	    	}
	    },
	    
	    undrawEvents: function() {
	        this.undrawMonthEvents();
	        this.undrawDayEvents();
	    },
	
	    undrawMonthEvents: function() {
	        while (this.monthGridEvents.length) {
	            dojo._destroyElement(dojo.byId("calevt" + instanceId + this.monthGridEvents.pop()));
	        }
			for (var i = 0; i < this.monthEventIds.length; i++) {
				if (this.eventData[this.monthEventIds[i]]) {
					this.eventData[this.monthEventIds[i]].position = undefined;
				}
			}
			this.monthEventIds = [];
	    },
	
	    undrawDayEvents: function() {
	        for (var eventId in this.dayGridEvents) {
	        	var eventDisplayIds = this.dayGridEvents[eventId];
	        	for (var i in eventDisplayIds) {
					if (eventDisplayIds[i]) {
		            	dojo._destroyElement(dojo.byId("calevt" + instanceId + eventDisplayIds[i]).parentNode);
		            	delete eventDisplayIds[i];
		           	}
	            }	        	
	        }
	    },
		
		getDayEventsInMonthView: function(date) {
			var key = date.getFullYear() + "/" + date.getMonth() + "/" + date.getDate();
			return this.order[this.eventsTypes[this.normalizedEventsType()]][key];
		},
	    
	    redrawDay: function() {
	        ss_cal_CalAllDayEvent.reset();
	        this.undrawEvents();
			
			ss_entryList = new Array();
			ss_entriesSeen = new Array();
			ss_entryCount = 0;
	        
			var key, fullMonthKey;
	        var lastDayToShow = dojo.date.add(ss_cal_Grid.firstDayToShow, "day", ss_cal_Grid.gridSize - 1);
	        var date = ss_cal_Grid.firstDayToShow;
			for (var gridDay = 0; gridDay < ss_cal_Grid.gridSize; gridDay++) {
				key = date.getFullYear() + "/" + date.getMonth() + "/" + date.getDate();
				fullMonthKey = date.getFullYear() + "/" + fullWithZeros(date.getMonth() ) + "/" + fullWithZeros(date.getDate());
				
				if (typeof this.order[this.eventsTypes[this.normalizedEventsType()]] != "undefined" &&
					typeof this.order[this.eventsTypes[this.normalizedEventsType()]][key] != "undefined") {
					for (var i = 0; i< this.order[this.eventsTypes[this.normalizedEventsType()]][key].length; i++) {
		            	var eid = this.order[this.eventsTypes[this.normalizedEventsType()]][key][i].substr(5);
		            	var e = this.eventData[eid];
					
						var start;
						var duration;
						var continues = null;
						
						var daysToEndOfEvent = daysDiff(date, e.endDate);
						var daysToStartOfEvent = daysDiff(date, e.startDate);
							
						if (daysToEndOfEvent < 0) {
							// event finshed, go to next event
							break;
						} else if (daysToStartOfEvent == 0) {
							// event begins
							start = e.startDate.getHours() +  (e.startDate.getMinutes() / 60);
							if (daysToEndOfEvent == 0) {
								// one day event
								duration = e.dur;
							} else {
								// duration to the end of the day
								duration = 1440 - getMinutesOfTheDay(e.startDate);
								continues = this.CONTINUES_RIGHT;
							}
						} else if (daysToEndOfEvent > 0 &&
								daysToStartOfEvent < 0) {
							// event continues
							start = 0;
							duration = 1440;
							continues = this.CONTINUES_LEFT_AND_RIGHT;
						} else if (daysToStartOfEvent != 0 &&
								daysToEndOfEvent == 0) {
							// event ends
							start = 0;
							duration = getMinutesOfTheDay(e.endDate);
							continues = this.CONTINUES_LEFT;
						}
								
						var eventDisplayId;
			            if (e.eventType == "event" && e.allDay) {
			                var grid = "ss_cal_dayGridAllDay" + instanceId;
			                eventDisplayId = ss_cal_drawCalendarEvent(grid, ss_cal_Grid.gridSize, 1, 0,
			                       gridDay, ss_cal_CalAllDayEvent.recordHourOffset(date.getFullYear(), date.getMonth(), date.getDate()), -1, e.title, e.text,
			                       eid, continues, e, date);
			            } else {
			                var grid = "ss_cal_dayGridHour" + instanceId;
			                if (duration == 0) duration = 30;
			                eventDisplayId = ss_cal_drawCalendarEvent(grid, ss_cal_Grid.gridSize,
			                       this.collisionCount(e.eventType, date.getFullYear(), date.getMonth(), date.getDate(), start),
			                       this.collisionIndex(e.eventType, date.getFullYear(), date.getMonth(), date.getDate(), start),
			                       gridDay, start, duration, e.title, e.text,
			                       eid, false, e, date);
			            }
			            if (!this.dayGridEvents[eid]) {
			            	this.dayGridEvents[eid] = {};
			            }
			            this.dayGridEvents[eid][gridDay] = eventDisplayId;
					
						if (typeof ss_entriesSeen['docId' + e.entryId] == "undefined") {
							ss_entryCount++;
							ss_entryList.unshift({ 
								index : e.entryId,
								entryId : e.entryId,
								binderId : e.binderId,
								entityType : e.entityType
							});
					    	ss_entriesSeen['docId' + e.entryId] = 1;
					    }
					
					}
				}
			
				date = dojo.date.add(date, "day", 1);
	        }
	        this.collisionI = {};
	        ss_cal_CalAllDayEvent.resetGridHeight();
	    },
	
	    redrawMonth: function() {
	        this.undrawEvents();
			
			ss_entryList = new Array();
			ss_entriesSeen = new Array();
			ss_entryCount = 0;
			
			var grid = "ss_cal_monthGrid" + instanceId;
			var monthViewInfo = ss_cal_CalData.getMonthViewInfo(ss_cal_Grid.currentDate);
			var date = monthViewInfo.startViewDate;
			var daysToEndOfMonthView = daysDiff(date, monthViewInfo.endViewDate);
			var fullMonthKey;
			var eventList;
			while (daysToEndOfMonthView >= 0) {
				eventList = ss_cal_Events.getDayEventsInMonthView(date);
				if (eventList) {
					drawMonthEventBlock(grid, date, eventList);
					
					for (var i = 0; i < eventList.length; i++) {
						var eventId = eventList[i].substr(5);
						var event = ss_cal_Events.eventData[eventId];
						if (typeof ss_entriesSeen['docId' + event.entryId] == "undefined") {
							ss_entryCount++;
							ss_entryList.unshift({ 
								index : event.entryId,
								entryId : event.entryId,
								binderId : event.binderId,
								entityType : event.entityType
							});
					    	ss_entriesSeen['docId' + event.entryId] = 1;
					    }
					}
				}
				
				
				date = dojo.date.add(date, "day", 1);
				daysToEndOfMonthView--;
			}		
	    },
	
	    redrawAll: function(doResize) {
	        if (ss_cal_Grid.currentType == "day") {
	            this.redrawDay();
	        } else if (ss_cal_Grid.currentType == "month") {
	            this.redrawMonth();
	        }
	        if ((typeof doResize == "undefined") || doResize) {
	        	resizeGwtContent("calendar::redrawAll()");
	        }
	    },
	
	    overEventId: "",
	    hoverEventId: "",
	    hoverTimer: 0,
	
	    requestHover: function(evt, eventId, gridDay) {
	        if (this.overEventId != (eventId + "-" + gridDay)) {
	            this.overEventId = eventId + "-" + gridDay;
	            //console.log("requestHover", eventId);
	            this.hoverTimer = setTimeout(function(){ss_cal_Events.displayHover(eventId, gridDay);}, 1000);
	            var e = this.eventData[eventId];
	            var n = dojo.byId("calevt" + instanceId + ss_cal_Events.dayGridEvents[e.eventId][gridDay]).parentNode;
	            n.onmouseout = function() { ss_cal_Events.cancelHover(true)};
	        }
	    },
	
	    cancelHover: function(animate) {
	        if (this.overEventId != "") {
	            if (animate) {
	                dojo.fadeOut({node: hoverBox, duration: 100,
	                               onEnd: function() {dojo.style(hoverBox, "display", "none")}
	                            }).play();
	            } else {
	                dojo.style(hoverBox, "display", "none");
	            }
				hoverBox.innerHTML = "";
	        } else {
	            return;
	        }
	        if (this.overEventId != '') {
	            clearTimeout(this.hoverTimer);
	        }
	        this.hoverEventId = '';
	        this.overEventId = '';
	    },
	
	    displayHover: function(eventId, gridDay) {
	        //console.log("display?", eventId, this.hoverEventId);
	        if (this.overEventId == eventId + "-" + gridDay) {
	            var e = this.eventData[eventId];
	            var n = dojo.byId("calevt" + instanceId + ss_cal_Events.dayGridEvents[e.eventId][gridDay]).parentNode;
	            //console.log("Hover: " + eventId);
	            this.hoverEventId = eventId + "-" + gridDay;
	
	            var ebox = dojo.coords(n, true);
	            //console.log("ebox coords: ");
	            //console.log(ebox);
	            hoverBox.style.visibility = "visible";
				var calEvtObj = dojo.byId("calevt" + instanceId + ss_cal_Events.dayGridEvents[e.eventId][gridDay]);
				
				var calStyle = getCalendarEventStyle(e.calendarId);
				addTopCorners(hoverBox, calStyle);
				
				var hoverContentBox = document.createElement("div");
				hoverContentBox.style.borderLeftStyle = "solid";
				hoverContentBox.style.borderLeftWidth = "1px";
				hoverContentBox.style.borderRightStyle = "solid";
				hoverContentBox.style.borderRightWidth = "1px";
				hoverContentBox.style.borderTop = "0";
				hoverContentBox.style.borderBottom = "0";
				hoverContentBox.style.padding = "10px"; 
				
	            hoverContentBox.innerHTML = calEvtObj.innerHTML;
				
				hoverBox.appendChild(hoverContentBox);
				addBottomCorners(hoverBox, calStyle);
				
				hoverContentBox.className = calStyle;
				
	            dojo.style(hoverBox, "opacity", 0);
	            dojo.style(hoverBox, "display", "block");
	            //console.log("Hover at: %d, %d", (ebox.x+30), (ebox.y - 20));
	            dijit.placeOnScreen(hoverBox, { x: (ebox.x + 30), y: (ebox.y - hoverBox.offsetHeight - 20)}, "TL", false);
	            dojo.fadeIn({node: hoverBox, duration: 200}).play();
	        }
	    },
	
	
	    switchView: function(	/* String: "daydelta", "3daydelta", "week", "fortnight", "workweek", "month", "daydirect", "monthdirect", "datedirect", "prev", "next" */ 
	    							mode, 
	    							year, month, dayOfMonth) {
	    	var grid;
	    	var dayToShow;
	    	var requiredDay;
	    	switch (mode) {
				case "daydelta":
					ss_cal_Grid.setGrid({size : 1, incr : 1});
	                dayToShow = ss_cal_Grid.currentDate;
	                grid = "day";
	                break;
	            case "3daydelta":
					ss_cal_Grid.setGrid({size : 3, incr : 3});
	                dayToShow = ss_cal_Grid.currentDate;
	                grid = "day";
	                break;
	            case "week":
					ss_cal_Grid.setGrid({size : 7, incr : 7});
	
					var firstDayOffset = (ss_cal_Grid.currentDate.getDay() - weekFirstDay);
	                var firstDayToShow = dojo.date.add(ss_cal_Grid.currentDate, "day", -firstDayOffset);
	                var lastDayToShow = dojo.date.add(firstDayToShow, "day", ss_cal_Grid.gridIncr);
	                dayToShow = firstDayToShow;
	                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
	                	requiredDay = lastDayToShow;
	                }
	                grid = "day";
	                break;
	            case "fortnight":
					ss_cal_Grid.setGrid({size : 14, incr : 14});
	                
	                var firstDayToShow = dojo.date.add(ss_cal_Grid.currentDate, "day", -(ss_cal_Grid.currentDate.getDay()));
	                var lastDayToShow = dojo.date.add(firstDayToShow, "day", 14);
	                
	                dayToShow = firstDayToShow;
	                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
	                	requiredDay = lastDayToShow;
	                }
	                grid = "day";
	                break;
	            case "workweek":
					ss_cal_Grid.setGrid({size : 5, incr : 7});
	                
	                var firstDayToShow = dojo.date.add(ss_cal_Grid.currentDate, "day", -(ss_cal_Grid.currentDate.getDay()) + 1);
	                var lastDayToShow = dojo.date.add(firstDayToShow, "day", ss_cal_Grid.gridIncr);
	                
	                dayToShow = firstDayToShow;
	                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
	                	requiredDay = lastDayToShow;
	                }
	                grid = "day";
	                break;                
				case "month":
					dayToShow = ss_cal_Grid.currentDate;
					grid = "month";
					break;
					
	            case "monthdirect":
	                dayToShow = new Date(year, month, dayOfMonth);
	                grid = "month";
	                break;
	                
	            case "datedirect":
	                dayToShow = new Date(year, month, dayOfMonth);
	                break;	                
	                
	            case "daydirect":
					ss_cal_Grid.setGrid({size : 1, incr : 1});
	                dayToShow = new Date(year, month, dayOfMonth);
	                grid = "day";
	                break;
	                
	            case "prev":
					if (ss_cal_Grid.currentType == "month") {
	            		dayToShow = dojo.date.add(ss_cal_Grid.currentDate, "month", -1);
	            	} else {
						dayToShow = dojo.date.add(ss_cal_Grid.currentDate, "day", -ss_cal_Grid.gridIncr);
					}
	            	if (ss_cal_Grid.currentType != "month") {
		                var lastDayToShow = dojo.date.add(dayToShow, "day", ss_cal_Grid.gridIncr);
		                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
		                	requiredDay = lastDayToShow;
		                }
	            	}
	            	            	
		            grid = ss_cal_Grid.currentType;
	                break;
	            case "next":
					if (ss_cal_Grid.currentType == "month") {
	            		dayToShow = dojo.date.add(ss_cal_Grid.currentDate, "month", 1);
	            	} else {
						dayToShow = dojo.date.add(ss_cal_Grid.currentDate, "day", ss_cal_Grid.gridIncr);
					}
	            	if (ss_cal_Grid.currentType != "month") {
		                var lastDayToShow = dojo.date.add(dayToShow, "day", -ss_cal_Grid.gridIncr);
		                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
		                	requiredDay = lastDayToShow;
		                }
	            	}
	            	
		            grid = ss_cal_Grid.currentType;
	                break;
	        }
			that.loadEventsByDate(grid, dayToShow, requiredDay);
	    },
	    
	    setEventTypeByName: function(newEventTypeName) {
	    	for (var i = 0; i < this.eventsTypes.length; i++) {
	    		if (this.eventsTypes[i] == newEventTypeName) {
	    			this.eventsType = i;
	    		}
	    	}
	    	
	    	var ss_calChooseEntryTypes = document.getElementById(eventsTypeChooseId);
			var ss_calSelectEntryTypes = document.getElementById(eventsTypeSelectId);
			if (ss_calChooseEntryTypes && ss_calSelectEntryTypes) {
				var iSel = ss_calFindOptionByValue(ss_calSelectEntryTypes, newEventTypeName);
				if ((-1) == iSel) {
					if ("virtual" == newEventType) {
						newEventType = "event";
						iSel = ss_calFindOptionByValue(ss_calSelectEntryTypes, newEventTypeName);
						if ((-1) == iSel) {
							iSel = 0;
						}
					}
				}
				ss_calSelectEntryTypes.selectedIndex = iSel;
				
				var mode = ss_calSelectEntryTypes.options[iSel].value;				
				var bChecked = (("creation" == mode) || ("activity" == mode));
				ss_calChooseEntryTypes.checked = bChecked;
			}
	    },
	    
	    changeEventType: function() {
	    	var oldEventType = this.eventsType;
			
			var ss_calChooseEntryTypes = document.getElementById(eventsTypeChooseId);
			var ss_calSelectEntryTypes = document.getElementById(eventsTypeSelectId);
			if (!ss_calChooseEntryTypes || !ss_calSelectEntryTypes) {
				return;
			}
			
			if (ss_calChooseEntryTypes.checked) {
				if (ss_calSelectEntryTypes.options[ss_calSelectEntryTypes.selectedIndex].value == this.eventsTypes[1]) {
					this.eventsType = 1;	// "creation"
				} else if (ss_calSelectEntryTypes.options[ss_calSelectEntryTypes.selectedIndex].value == this.eventsTypes[2]) {
					this.eventsType = 2;	// "activity"
				}
			} else {
				if (ss_calSelectEntryTypes.options[ss_calSelectEntryTypes.selectedIndex].value == this.eventsTypes[3]) {
					this.eventsType = 3;	// "virtual"
				} else {
					this.eventsType = 0;	// "event"
				}
			}
	
			if (oldEventType != this.eventsType) {
				// Bugzilla 694339:
				//    When changing the show type, we always need to
				//    reload the list so that things show on the
				//    correct day.
				if (true) // ((3 == oldEventType) || (3 == this.eventsType))
				     refreshFunction = function(){document.location.reload();};
				else refreshFunction = function(){this.redrawAll();};			

				if (calendarDataProvider) {
					calendarDataProvider.stickyCalendarDisplaySettings(
						{eventType : this.eventsTypes[this.eventsType]},
						refreshFunction);
				}
				
				else {
					refreshFunction();
				}
			}
	    }
	    
	};
	
	//////////////////////////////////////////////////////////////////////////////////////////

	function ss_cal_newEventInfo(evt, gridControl) {
	    evt = (evt) ? evt : ((event) ? event : null);
	    
	    var currEventData = gridControl.currEventData;
	    
	    var url = addEntryURL;
	    var haveAddEntryURL = ((null != url) && (0 < url.length));
	    if (haveAddEntryURL) {
	    	url += "&year=" + currEventData.startDate.getFullYear();
	    	url += "&month=" + currEventData.startDate.getMonth();
	    	url += "&dayOfMonth=" + currEventData.startDate.getDate();
	    	url += "&time=" + currEventData.start.toString().replace(".5", ":30");
	    	url += "&duration=" + currEventData.dur;
	        
	    	ss_openUrlInPortlet(url, true);
	    }
	    
	    gridControl.deleteCurrentEvent();
	}
	
	function ss_cal_drawCalendarEvent(containerId, gridDays, shareCount, shareSlot, day, time, duration, title, text, eventId, continues, event, date) {
		if (day != 0 && event.allDay && (continues == ss_cal_Events.CONTINUES_LEFT || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT)) {
			return;
		}

	    var container = dojo.byId(containerId);
	    var dayOffsetSize = (1.0 / gridDays) * 100.0;
	
	    if (day >= gridDays) return;
	        
	    ss_cal_Events.displayId++;
	
	    var ebox = document.createElement("div");
	    ebox.className = "ss_cal_eventBox";
	
	    if (eventId != "") {
			dojo.connect(ebox, "onmousedown", function(e) {
	        	// prevent new events creation
	        	if (!e) var e = window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
	        });
			dojo.connect(ebox, "onmouseover", function(evt) { ss_cal_Events.requestHover(evt, eventId, day); });
	    }
	
		var eventLength = 1;
		if (event.allDay && typeof date != "undefined") {
			var daysToEndOfEvent = daysDiff(date, event.endDate) + 1;
			if (daysToEndOfEvent > gridDays - day) {
				eventLength = gridDays - day;
			} else {
				eventLength = daysToEndOfEvent;
			}
		}
	    var w = (dayOffsetSize / shareCount);
	    ebox.style.width = (w * eventLength - 0.2) + "%";
	    ebox.style.left = (((day * dayOffsetSize) + 0.2) + (w * shareSlot)) + "%";
	    ebox.style.top = ((time * 42) + 1) + "px";
		ebox.style.height = (((((duration <= 30) ? 30 : duration) / 60) * 42) - 1) + "px";
	    ebox.setAttribute("id", "calevt_out" + instanceId + ss_cal_Events.displayId);
		
		var calStyle = getCalendarEventStyle(event.calendarId);
		addTopCorners(ebox, calStyle);
		
	    var eboxInner = document.createElement("div");
		eboxInner.className = "ss_cal_eventBody " + calStyle;
	    eboxInner.setAttribute("id", "calevt" + instanceId + ss_cal_Events.displayId);
	    eboxInner.style.height = (((((duration <= 30) ? 30 : duration) / 60) * 42) - 5) + "px";
		
	    var eHtml = "";
	   	if (day == 0 && event.allDay && (continues == ss_cal_Events.CONTINUES_LEFT || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT)) {
			eHtml += '<img src="'+ss_imagesPath + 'pics/sym_s_prev.png'+'" style="float: left; border: 0;" />';			
		}
	   	if (event) {
			var viewHref = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:event.binderId, entryId:event.entryId}, "view_folder_entry");
			eHtml += '<a href="'+viewHref+'" onClick="try {' + event.viewOnClick + '; ss_currentEntryId = ' + event.entryId + ';} catch(e) {return true;} return false;" style="padding-left: 4px; float: left;">'+(event.title?event.title:that.locale.noTitle)+'</a>';
	   	} else {
	   		// new event creation
			eHtml += '<a href="javascript: //"  style="padding-left: 4px; float: left;">' + (title?title:that.locale.noTitle) + '</a>';
	   	}
   		if (daysToEndOfEvent > eventLength && event.allDay && (continues == ss_cal_Events.CONTINUES_RIGHT || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT)) {
			eHtml += '<img src="'+ss_imagesPath + 'pics/sym_s_next.png'+'" style="float: right; border: 0; " />';			
		}
		
		if (text != "") {
	    	eHtml += "<br style='clear: both;'/>" + text;
		}
	    eboxInner.innerHTML =  eHtml; 

			
	    ebox.appendChild(eboxInner);
	
		addBottomCorners(ebox, calStyle);

	    container.appendChild(ebox);
	    dojo.style(ebox, "opacity", 0);
	    var o = (title == "") ? 0.8 : 1.0;

	    dojo.animateProperty({node: ebox, properties: { opacity: {start: 0, end: o} }, duration: 200}).play();
	    
	    return ss_cal_Events.displayId;
	}
	
	function makeDayPositionFree(i, dayPositions) {
			if (!dayPositions[i]) { // position is free
				return;
			}
			if (i == 0) {
				if (!dayPositions[i + 1]) {
					dayPositions[i + 1] = dayPositions[i];
				} else if (!dayPositions[i + 1].reserved) {
					if (!dayPositions[i + 2] || !dayPositions[i + 2].reserved) {
						dayPositions[i + 2] = dayPositions[i + 1];
					}
					dayPositions[i + 1] = dayPositions[i];
				} else {
					if (!dayPositions[i + 2] || !dayPositions[i + 2].reserved) {
						dayPositions[i + 2] = dayPositions[i];
					}
				}
			} else if (i == 1) {
				if (!dayPositions[i + 1] || !dayPositions[i + 1].reserved) {
					dayPositions[i + 1] = dayPositions[i];
				}
			}
		}
		
		function fitsBetterThen(newEvent, oldEvent, monthViewInfo) {
			var newEventStartDateInMonth = newEvent.startDate;
			if (daysDiff(newEventStartDateInMonth, monthViewInfo.startViewDate) > 0) {
				newEventStartDateInMonth =monthViewInfo.startViewDate;
			}
			
			var oldEventStartDateInMonth = oldEvent.startDate;
			if (daysDiff(oldEventStartDateInMonth, monthViewInfo.startViewDate) > 0) {
				oldEventStartDateInMonth = monthViewInfo.startViewDate;
			}
			
			var diff = daysDiff(newEventStartDateInMonth, oldEventStartDateInMonth);
			if (diff > 0) {
				return true;
			} else if (diff < 0) {
				return false;
			}
			
			var newEventEndDateInMonth = newEvent.endDate;
			if (daysDiff(monthViewInfo.endViewDate, newEventEndDateInMonth) > 0) {
				newEventEndDateInMonth = monthViewInfo.endViewDate;
			}			
			
			
			var oldEventEndDateInMonth = oldEvent.endDate;
			if (daysDiff(monthViewInfo.endViewDate, oldEventEndDateInMonth) > 0) {
				oldEventEndDateInMonth = monthViewInfo.endViewDate;
			}
			
			var newEventLength = daysDiff(newEventStartDateInMonth, newEventEndDateInMonth);
			var oldEventLength = daysDiff(oldEventStartDateInMonth, oldEventEndDateInMonth);
			if (newEventLength > oldEventLength) {
				return true;
			} else if (newEventLength < oldEventLength) {
				return false;
			}
			
			if (+newEvent.startDate > +oldEvent.startDate) {
				return false;
			} else if (+newEvent.startDate < +oldEvent.startDate) {
				return true;
			}
			
			if (newEvent.entryId * 1 < oldEvent.entryId * 1) {
				return true;
			} else if (newEvent.entryId * 1 > oldEvent.entryId * 1) {
				return false;
			}
			
			if (newEvent.dur > oldEvent.dur) {
				return true;
			} else {
				return false;
			}
		}
		
	var monthGridEventVOffsets = [0, 14.4, 28.6, 43, 57.3, 71.5, 85.9];
	
	var eventWidths = [
		[14.3, 28.5, 42.9, 57.2, 71.3, 85.7, 100], 
		[14.1, 28.4, 42.9, 56.8, 71.3, 85.7],
		[14.2, 28.4, 42.9, 56.8, 71.3],
		[14.2, 28.4, 42.9, 57],
		[14.1, 28.4, 42.9],
		[14.2, 28.4],
		[14.1]
	];	

	var monthGridEventHOffsets = {
		4: {
			0: [3.9, 10.9, 18.1],
			1: [28.9, 35.9, 43.1],
			2: [53.9, 60.9, 68.1],
			3: [78.9, 85.9, 93.1]
			},
		5: {
			0: [3.9, 9.2, 14.5],
			1: [23.9, 29.2, 34.5],
			2: [43.9, 49.2, 54.5],
			3: [63.9, 69.2, 74.5],
			4: [83.9, 89.2, 94.5]
		},
		6: {
			0: [3.9, 8, 12.4],
			1: [20.4, 24.6, 29],
			2: [37.3, 41.5, 45.8],
			3: [53.9, 58, 62.4],
			4: [70.4, 74.6, 79],
			5: [87.3, 91.7, 96.1]
		}
	};

	var eventHeights = {
		4: {
			1: [9, 9, 9],
			3: [9]
		},
		5: {
			1: [6.5, 6.5, 6.5],
			3: [6.5]
		},
		6: {
			1: [5, 5, 4.6],
			3: [5]
		}
	};
	
	function drawMonthEventBlock(containerId, date, eventList) {
		// Bugzilla 557401:
		//    Removed the vertical fudge used to display day entries in
		//    a month view where the month displays 6 weeks.
		var hFudge6Weeks = "0";	// ...was "-3px"

		var monthViewInfo = ss_cal_CalData.getMonthViewInfo(ss_cal_Grid.currentDate);
		
		var dayPositions = [];

		var eventCount = eventList.length;
		
		var dayInMonthViewOffset = daysDiff(monthViewInfo.startViewDate, date);
		
	    var container = dojo.byId(containerId);
		
		var hOffsetSize = 20;
        if (ss_cal_Grid.monthGridWeeks == 6) {
			hOffsetSize = 16.666666666666664;
        } else if (ss_cal_Grid.monthGridWeeks == 4) {
			hOffsetSize = 25;
        }
        var vOffsetSize = 14.285714285714285; // (1.0 / 7) * 100.0;
	
	    var eventHeight = 1;
	    if (eventCount > 3) {
	        iterations = 2;
	    } else {
	        iterations = eventCount;
	    }
	
	    var week = Math.floor(dayInMonthViewOffset / 7);
	    var dayOfWeek = dayInMonthViewOffset % 7;
		
		var firstDayInCurrentWeekWithMoreThen3Events;
		if (eventCount >= 3) {
			var c = 0;
			firstDayInCurrentWeekWithMoreThen3Events = dojo.date.add(date, "day", 1);
			while (c < (6 - dayOfWeek)) {
				var tEventList = ss_cal_Events.getDayEventsInMonthView(firstDayInCurrentWeekWithMoreThen3Events);
				if (tEventList && tEventList.length > 3) {
					break;
				}
				
				firstDayInCurrentWeekWithMoreThen3Events = dojo.date.add(firstDayInCurrentWeekWithMoreThen3Events, "day", 1);
				c++;
			}
			if (daysDiff(firstDayInCurrentWeekWithMoreThen3Events, dojo.date.add(date, "day", 6 - dayOfWeek)) < 0) {
				 firstDayInCurrentWeekWithMoreThen3Events = undefined;
			}
		}
		
		var maxEventLenghtToShow;
		if (firstDayInCurrentWeekWithMoreThen3Events) {
			maxEventLenghtToShow = daysDiff(date, firstDayInCurrentWeekWithMoreThen3Events);
		}
		
		for (var i = 0; i < eventCount; i++) {
			var eventId = eventList[i].substr(5);
			var e = ss_cal_Events.eventData[eventId];
			
			// has reserved position
			if (typeof e.position != "undefined") {
				makeDayPositionFree(e.position.pos, dayPositions);
				dayPositions[e.position.pos] = {reserved: true, eventId: eventId};
				continue;
			}
			// try position 0
			if (!dayPositions[0]) {
				dayPositions[0] = {reserved: false, eventId: eventId};
			} else if (!dayPositions[0]["reserved"] &&
						fitsBetterThen(e, ss_cal_Events.eventData[dayPositions[0].eventId], monthViewInfo)) {
				makeDayPositionFree(0, dayPositions);
				dayPositions[0] = {reserved: false, eventId: eventId};
			} else {
				// try position 1
				if (!dayPositions[1]) {
					dayPositions[1] = {reserved: false, eventId: eventId};
				} else if (!dayPositions[1]["reserved"] &&
							fitsBetterThen(e, ss_cal_Events.eventData[dayPositions[1].eventId], monthViewInfo)) {
					makeDayPositionFree(1, dayPositions);
					dayPositions[1] = {reserved: false, eventId: eventId};
				} else {
					// try position 2
					if (!dayPositions[2]) {
						dayPositions[2] = {reserved: false, eventId: eventId};
					} else if (!dayPositions[2]["reserved"] &&
								fitsBetterThen(e, ss_cal_Events.eventData[dayPositions[2].eventId], monthViewInfo)) {
						makeDayPositionFree(2, dayPositions);
						dayPositions[2] = {reserved: false, eventId: eventId};
					}
				}
			}
		}
		
		// leave place for "more..."
		if (eventCount > 3 && dayPositions.length == 3) {
			dayPositions.pop();
		}
		
		// show full height event only if there is one event with one day length
		if (dayPositions.length == 1) {
			var e = ss_cal_Events.eventData[dayPositions[0].eventId];
			var diff = daysDiff(e.startDate, e.endDate);
			if (diff == 0) {
				eventHeight = 3;
			}
		}
	
		for (var i = 0; i < dayPositions.length; i++) {
			if (typeof dayPositions[i] == "undefined") {
				continue;
			}
	        ss_cal_Events.displayId += 1;
			ss_cal_Events.monthGridEvents.push(ss_cal_Events.displayId);
			ss_cal_Events.monthEventIds.push(dayPositions[i].eventId);
	        var e = ss_cal_Events.eventData[dayPositions[i].eventId];
			var diffDateEventStartDate = daysDiff(date, e.startDate);
			var diffDateEventEndDate = daysDiff(date, e.endDate);
			
			var continues = undefined;
			if (diffDateEventStartDate == 0 && diffDateEventEndDate != 0) {
				continues = ss_cal_Events.CONTINUES_RIGHT;
			} else if (diffDateEventEndDate > 0 &&	diffDateEventStartDate < 0) {
				continues = ss_cal_Events.CONTINUES_LEFT_AND_RIGHT;
			} else if (diffDateEventStartDate != 0 && diffDateEventEndDate == 0) {
				continues = ss_cal_Events.CONTINUES_LEFT;
			}
			
			var takesDaysToTheEndOfEvent = diffDateEventEndDate + 1;
			var takesDaysToTheEndOfWeek = takesDaysToTheEndOfEvent;
			if (takesDaysToTheEndOfEvent > (6 - dayOfWeek + 1)) {
				takesDaysToTheEndOfWeek = 6 - dayOfWeek + 1;
			}
			var eventDaysLength = takesDaysToTheEndOfWeek;
			if (i == 2 && takesDaysToTheEndOfWeek > maxEventLenghtToShow) {
				eventDaysLength = maxEventLenghtToShow;
			}
			
			if (typeof e.position != "undefined" && e.position.week == week && daysDiff(dojo.date.add(e.position.date, "day", e.position.length - 1), date) <= 0 && (continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT || continues == ss_cal_Events.CONTINUES_LEFT) && dayOfWeek != 0) {
				continue;
			}
			e.position = {pos: i, week: week, date: date, length: eventDaysLength};
			
		
	        var ebox = document.createElement("div");
	
			ebox.className = "ss_cal_eventBox";
			ebox.id =  "calevt" + instanceId + ss_cal_Events.displayId;
	
			ebox.style.left = monthGridEventVOffsets[dayOfWeek] + "%";
			ebox.style.top = monthGridEventHOffsets[ss_cal_Grid.monthGridWeeks][week][i] + "%";
			ebox.style.height = eventHeights[ss_cal_Grid.monthGridWeeks][eventHeight][i] + "%";
	        ebox.style.width = eventWidths[dayOfWeek][eventDaysLength - 1] + "%";
			
			var calStyle = getCalendarEventStyle(e.calendarId);
			addTopCorners(ebox, calStyle);
			
			var viewHref = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:e.binderId, entryId:e.entryId}, "view_folder_entry");

			var eboxInner = document.createElement("div");
			var boxHtml = '';
			
			if ((continues == ss_cal_Events.CONTINUES_LEFT || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT)) {
				boxHtml += '<img src="'+ss_imagesPath + 'pics/sym_s_prev.png'+'" style="float: left; border: 0;" />';			
			}
			boxHtml += '<a href="'+viewHref+'" onClick="try{' + e.viewOnClick + '; ss_currentEntryId = ' + e.entryId + ';} catch(e) {return true;} return false;" ' + (ss_cal_Grid.monthGridWeeks > 5? 'style="position: relative; top: ' + hFudge6Weeks + '; margin-left: 4px; float: left;"':'style="margin-left: 4px; float: left;"') + '>'+(e.title?e.title:that.locale.noTitle)+'</a>';			
			if (takesDaysToTheEndOfEvent > eventDaysLength && (continues == ss_cal_Events.CONTINUES_RIGHT || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT)) {
				boxHtml += '<img src="'+ss_imagesPath + 'pics/sym_s_next.png'+'" style="float: right; border: 0;" />';			
			}
			
			eboxInner.innerHTML = boxHtml;
			eboxInner.style.height = "65%";
			eboxInner.className = "ss_cal_monthEventBody " + getCalendarEventStyle(e.calendarId);
			if (ss_cal_Grid.monthGridWeeks > 5) {
				eboxInner.style.fontSize = "9px";
			}
			
			ebox.appendChild(eboxInner);
			
			addBottomCorners(ebox, calStyle);
			
	        container.appendChild(ebox);

	        dojo.animateProperty({node: ebox, properties: { opacity: {start: 0, end: 1} }, duration: 200}).play();     
	    }
	
	    if (eventCount > dayPositions.length) {
	        var ebox = document.createElement("div");
	        ss_cal_Events.displayId += 1;
			ss_cal_Events.monthGridEvents.push(ss_cal_Events.displayId);
	        ebox.className = "ss_cal_eventBox";
			ebox.id = "calevt" + instanceId + ss_cal_Events.displayId;
	
			ebox.style.left = monthGridEventVOffsets[dayOfWeek] + "%";
			ebox.style.top = monthGridEventHOffsets[ss_cal_Grid.monthGridWeeks][week][2] + "%";
			ebox.style.height = eventHeights[ss_cal_Grid.monthGridWeeks][1][2] + "%";
	        ebox.style.width = eventWidths[dayOfWeek][0] + "%";

			addTopCorners(ebox, "ss_cal_moreBox");
			
			var eboxInner = document.createElement("div");
			eboxInner.className = "ss_cal_monthEventBody ss_cal_moreBox";
			eboxInner.innerHTML = '<a href="javascript: //" ' + (ss_cal_Grid.monthGridWeeks > 5?'style="position: relative; top: -2px; "':'') + '>+' + (eventCount - 2) + ' ' + that.locale.entriesLabel + '</a>';
			eboxInner.style.height = ss_cal_Grid.monthGridWeeks > 5?"65%":"65%";
			eboxInner.style.textAlign = "center";
			
			(function(eboxInner, dayOfWeek, container, vOffsetSize, week, hOffsetSize, eventCount, eventList, date) {
				dojo.connect(eboxInner, "onclick", function(evt) {
					// create all events pane
					var moreEventsDiv = document.createElement("div");
					moreEventsDiv.className = "ss_calendar_more_box";
					moreEventsDiv.style.left = monthGridEventVOffsets[dayOfWeek] + "%";
					moreEventsDiv.style.width = (String(moreEventsDIVWidth) + "px");
					moreEventsDiv.style.height = ((18 * (eventCount<=10?eventCount:10)) + 19) + "px";
					moreEventsDiv.style.top = (monthGridEventHOffsets[ss_cal_Grid.monthGridWeeks][week][0] - 5) + "%";
					
					var moreEventsDivHeader = document.createElement("div");
					moreEventsDivHeader.className = "ss_calendar_more_box_header";
					
					var moreEventsDivClose = document.createElement("div");
					moreEventsDivClose.className = "ss_calendar_more_box_close";
					
					moreEventsDivHeader.appendChild(moreEventsDivClose);
				
					moreEventsDiv.appendChild(moreEventsDivHeader);

					container.appendChild(moreEventsDiv);

					var moreEventsDivList = document.createElement("div");
					moreEventsDivList.style.height = (18 * (eventCount<=10?eventCount:10)) + "px";
					moreEventsDiv.appendChild(moreEventsDivList);
					if (eventCount > 10) {
						moreEventsDivList.style.overflowY = "scroll";
					}

					for (var i = 0; i < eventCount; i++) {
						var eventId = eventList[i].substr(5);
				        var e = ss_cal_Events.eventData[eventId];
						var diffDateEventStartDate = daysDiff(date, e.startDate);
						var diffDateEventEndDate = daysDiff(date, e.endDate);
			
						var continues = undefined;
						
						if (diffDateEventStartDate == 0 && diffDateEventEndDate != 0) {
							continues = ss_cal_Events.CONTINUES_RIGHT;
						} else if (diffDateEventEndDate > 0 &&	diffDateEventStartDate < 0) {
							continues = ss_cal_Events.CONTINUES_LEFT_AND_RIGHT;
						} else if (diffDateEventStartDate != 0 && diffDateEventEndDate == 0) {
							continues = ss_cal_Events.CONTINUES_LEFT;
						}
						
				        var ebox = document.createElement("div");
						ebox.style.width = "100%";
						ebox.style.height = "18px";
											
						var calStyle = getCalendarEventStyle(e.calendarId);
						addTopCorners(ebox, calStyle);
						
						var viewHref = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:e.binderId, entryId:e.entryId}, "view_folder_entry");
						
						var eboxInner = document.createElement("div");
						eboxInner.style.height = "14px";
						
						var hasPrev =                                                (continues == ss_cal_Events.CONTINUES_LEFT  || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT);
						var hasNext = (takesDaysToTheEndOfEvent > eventDaysLength && (continues == ss_cal_Events.CONTINUES_RIGHT || continues == ss_cal_Events.CONTINUES_LEFT_AND_RIGHT));
						var titleDisp;
						var titleStyle = "margin-left: 4px; float: left;";
						if (e.title) {
							titleDisp = e.title;
							if (hasPrev || hasNext) {
								// If we're displaying a previous or
								// next arrow, we need to put a style
								// on the title's anchor so that
								// everything displays as it should.
								var titleWidth = Number(moreEventsDIVWidth);	// Overall width of menu.
								titleWidth -= 4;								// Title's margin-left.
								if (hasPrev) titleWidth -= 10;					// Width of left  arrow.
								if (hasNext) titleWidth -= 10;					// Width of right arrow.
								titleWidth -= 4;								// Allow for the same margin on the right too.
								titleStyle += (" width: " + String(titleWidth) + "px;");
							}
						}
						else {
							titleDisp = that.locale.noTitle;
						}
						
						var boxHtml = '';
						if (hasPrev) {
							boxHtml += '<img src="'+ss_imagesPath + 'pics/sym_s_prev.png'+'" style="float: left; border: 0;" />';			
						}
						boxHtml += '<a href="'+viewHref+'" onClick="try{' + e.viewOnClick + '; ss_currentEntryId = ' + e.entryId + ';} catch(e) {return true;} return false;" ' + (ss_cal_Grid.monthGridWeeks > 5? 'style="position: relative; top: ' + hFudge6Weeks + '; ' + titleStyle + '"':'style="' + titleStyle + '"') + '>'+titleDisp+'</a>';			
						if (hasNext) {
							boxHtml += '<img src="'+ss_imagesPath + 'pics/sym_s_next.png'+'" style="float: right; border: 0;" />';			
						}
						
						eboxInner.innerHTML = boxHtml;
						eboxInner.className = ("ss_cal_monthEventBody " + getCalendarEventStyle(e.calendarId));
						ebox.appendChild(eboxInner);						
						addBottomCorners(ebox, calStyle);						
				        moreEventsDivList.appendChild(ebox);
					}
					
					
					var lightBox = ss_showLightbox(null, null, null, "ss_lightBox_transparent", container);
					dojo.connect(lightBox, "onclick", function(evt) {
						moreEventsDiv.parentNode.removeChild(moreEventsDiv);
						lightBox.parentNode.removeChild(lightBox);
					});
					
					dojo.connect(moreEventsDiv, "onclick", function(evt) {
						moreEventsDiv.parentNode.removeChild(moreEventsDiv);
						lightBox.parentNode.removeChild(lightBox);
					});
			    });
			})(eboxInner, dayOfWeek, container, vOffsetSize, week, hOffsetSize, eventCount, eventList, date);
			
			ebox.appendChild(eboxInner);
			
			addBottomCorners(ebox, "ss_cal_moreBox");
			
	        container.appendChild(ebox);
					
	        dojo.animateProperty({node: ebox, properties: { opacity: {start: 0, end: 1} }, duration: 250}).play();
	    }
	
	
	    // return resultDisplayIds;
	}
	
	this.ss_cal_Events = ss_cal_Events;

}

if (!window.ss_calendar) {
	var ss_calendar = new ss_calendarControl();
}

if (!window["ss_calendar_import"]) {
	var ss_calendar_import = {
		FROM_FILE : "FROM_FILE",
		
		BY_URL : "BY_URL",

		divId : "ss_calendar_import_div",
		
		importFormFromFile : function(props) {
			ss_calendar_import.importForm(props, ss_calendar_import.FROM_FILE);
		},
		
		importFormByURL : function(props) {
			ss_calendar_import.importForm(props, ss_calendar_import.BY_URL);
		},

		/*
			forumId, namespace, title, legend, btn 
		 */
		importForm : function(props, formType) {
			
			// Build the import form
			var calImportDiv = document.getElementById(this.divId);
			if (calImportDiv != null) calImportDiv.parentNode.removeChild(calImportDiv);
			
			//Build a new calendar_import div
			calImportDiv = document.createElement("div");
		    calImportDiv.setAttribute("id", this.divId);
		    calImportDiv.setAttribute("align", "left");
		    calImportDiv.style.visibility = "hidden";
		    calImportDiv.className = "ss_calendar_popup_div";
		    calImportDiv.style.display = "none";
			calImportDiv.innerHTML = '<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: 220px;">' +
	         '<tbody><tr class="ss_base_title_bar"><td width="30px"><div class="ss_popup_topleft"></td><td width="100%"><div class="ss_popup_topcenter"><div id="ss_calendar_import_title"></div></div></td><td width="40px"><div class="ss_popup_topright"><div id="ss_calendar_import_close" class="ss_popup_close"></div></div>' +
	         '</td></tr><tr><td colspan="3"><div id="ss_calendar_import_inner" style="padding: 3px 10px;" class="ss_popup_body"></div></td></tr><tr><td width="30px"><div class="ss_popup_bottomleft"></div></td><td width="100%"><div class="ss_popup_bottomcenter"></div></td>' +
	         '<td width="40px"><div class="ss_popup_bottomright"></div></td></tr></tbody></table>';
			
		
			// Link into the document tree
			document.getElementsByTagName("body").item(0).appendChild(calImportDiv);
			
			dojo.byId("ss_calendar_import_title").appendChild(document.createTextNode(props.title));
	
			dojo.connect(dojo.byId("ss_calendar_import_close"), "onclick", function(evt) {
				ss_calendar_import.cancel();
		    });

		    if (formType == ss_calendar_import.FROM_FILE) {
			    dojo.byId("ss_calendar_import_inner").innerHTML = 
					"<form id=\"ss_calendar_import_form\" method=\"post\" enctype=\"multipart/form-data\" name=\"ss_calendar_import_form\">" +
						"<div style=\"text-align: left; width: 100%; margin-bottom: 10px; margin-top: 10px; \">" +
							"<input type=\"file\" name=\"iCalFile\" />" +
							"<p class=\"ss_smallprint ss_light\">" + props.legend + "</p>" + 
						"</div>" +
						"<input type=\"button\" value=\"" + props.btn + "\" onclick=\"ss_calendar_import.uploadFile('" + props.namespace + "');\"/>" +
						"<input type=\"hidden\" name=\"folderId\" value=\"" + props.forumId + "\">" +
					"</form>";
			}
			
			if (formType == ss_calendar_import.BY_URL) {
				dojo.byId("ss_calendar_import_inner").innerHTML = 
					"<form id=\"ss_calendar_importURL_form\" method=\"post\"  name=\"ss_calendar_importURL_form\">" +
						"<div style=\"text-align: left; width: 100%; margin-bottom: 10px; margin-top: 10px; \">" +
							"<input type=\"text\" name=\"iCalURL\" style=\"width: 250px;\"/>" +
							"<p class=\"ss_smallprint ss_light\">" + props.legend + "</p>" + 
						"</div>" +
						"<input type=\"button\" value=\"" + props.btn + "\" onclick=\"ss_calendar_import.loadURL('" + props.namespace + "');\"/>" +
						"<input type=\"hidden\" name=\"folderId\" value=\"" + props.forumId + "\">" +
					"</form>";
		    }
			ss_showPopupDivCentered(this.divId);
		},
		
		uploadFile : function (prefix) {
			var box = dojo.byId("ss_calendar_import_form");
			ss_toggleAjaxLoadingIndicator(box, true);			
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"uploadICalendarFile"});
			//console.log("uploadFile");
			dojo.io.iframe.send({
		    	url: url,
		    	method: "post",
		    	handleAs: "text/html",
				error: function(err) {
					alert(err);
					ss_calendar_import.cancel();
				},
				load: function(textData) {
					var refreshContent = false;
				    var data = dojo.fromJson(textData);
					if (data && data.notLoggedIn) {
						alert(ss_not_logged_in);
					} else if (data && data.parseExceptionMsg) {
						alert(data.parseExceptionMsg);												
					} else if (data && data.entriesAmountMsg) {
						var added = data.entryAddedIds.length;
						var modified = data.entryModifiedIds.length;
						if (window["ss_calendar_" + prefix]) {
							for (var i = 0; i < added; i++) {
								window["ss_calendar_" + prefix].refreshEntryEvents(data.entryAddedIds[i]);
							}
							for (var i = 0; i < modified; i++) {
								window["ss_calendar_" + prefix].refreshEntryEvents(data.entryModifiedIds[i]);
							}							
						} else if (0 < (added + modified)) {
							refreshContent = true;
						}
						alert(data.entriesAmountMsg);
					} else {
						throw "Wrong server response.";
					}
					
					ss_calendar_import.cancel();
					if (refreshContent) {
						document.location.reload();
					}
				},
				preventCache: true,
				form: "ss_calendar_import_form"
			});
	
		},
		
		loadURL : function (prefix) {
			var box = dojo.byId("ss_calendar_importURL_form");
			ss_toggleAjaxLoadingIndicator(box, true);
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"loadICalendarByURL"});
			dojo.xhrGet({
		    	url: url,
		    	handleAs: "json",
				error: function(err) {
					alert(ss_not_logged_in);
					ss_calendar_import.cancel();
				},
				load: function(data) {
					var refreshContent = false;
					if (data && data.notLoggedIn) {
						alert(ss_not_logged_in);
					} else if (data && data.parseExceptionMsg) {
						alert(data.parseExceptionMsg);						
					} else if (data && data.entriesAmountMsg) {
						var added = data.entryAddedIds.length;
						var modified = data.entryModifiedIds.length;
						if (window["ss_calendar_" + prefix]) {
							for (var i = 0; i < added; i++) {
								window["ss_calendar_" + prefix].refreshEntryEvents(data.entryAddedIds[i]);
							}
							for (var i = 0; i < modified; i++) {
								window["ss_calendar_" + prefix].refreshEntryEvents(data.entryModifiedIds[i]);
							}							
						} else if (0 < (added + modified)) {
							refreshContent = true;
						}
						alert(data.entriesAmountMsg);
					} else {
						throw "Wrong server response.";
					}
					
					ss_calendar_import.cancel();
					if (refreshContent) {
						document.location.reload();
					}
				},
				preventCache: true,
				form: "ss_calendar_importURL_form"
			});
	
		},
		
		cancel : function () {
			ss_cancelPopupDiv(this.divId);
			return false;
		}	
	}

}

if (!window.ss_calendar_settings) {
	var ss_calendar_settings = {
		divId : "ss_calendar_configure_div",
		
		locale: {
			title: "Calendar Settings",
			weekStartsOnLabel: "Week starts on",
			workDayStartsAtLabel: "Work day starts at",
			dayNames: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
			submitLabel: "Save",
			lang: window.dojo&&dojo.locale?dojo.locale:"en"
		},
		
		configure : function(defWeekFirstDay, defWorkDayStart, weekFirstDay, workDayStart) {
			if (typeof weekFirstDay === "undefined") weekFirstDay = "";
			if (typeof workDayStart === "undefined") workDayStart = "";
			weekFirstDay = ((weekFirstDay != "") ? weekFirstDay : defWeekFirstDay);
			workDayStart = ((workDayStart != "") ? workDayStart : defWorkDayStart);
			
			// Build the configuration form
			var calConfigureDiv = document.getElementById(this.divId);
			if (calConfigureDiv != null) calConfigureDiv.parentNode.removeChild(calConfigureDiv);
			
			//Build a new calendar_import div
			calConfigureDiv = document.createElement("div");
		    calConfigureDiv.setAttribute("id", this.divId);
		    calConfigureDiv.setAttribute("align", "left");
		    calConfigureDiv.style.visibility = "hidden";
		    calConfigureDiv.className = "ss_calendar_popup_div";
		    calConfigureDiv.style.display = "none";
			var htmlCode = '<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: 220px;">' +
	         '<tbody><tr class="ss_base_title_bar"><td width="30px"><div class="ss_popup_topleft"></td><td width="100%"><div class="ss_popup_topcenter"><div id="ss_calendar_import_title">' + this.locale.title + '</div></div></td><td width="40px"><div class="ss_popup_topright"><div id="ss_calendar_configure_close" class="ss_popup_close"></div></div>' +
	         '</td></tr><tr><td colspan="3"><div class="ss_popup_body"><form id="ss_calendar_settings_form">'+
			 '<div class="marginleft2">' +
			 '<div class="margintop2"><label class="ss_event_editor">' + this.locale.weekStartsOnLabel + ':</label><div><select name="weekFirstDay"><option value="7"' + (weekFirstDay==7?'selected="true"':'') + '>' + this.locale.dayNames[6] + '</option><option value="1"' + (weekFirstDay==1?'selected="true"':'') + '>' + this.locale.dayNames[0] + '</option><option value="2" ' + (weekFirstDay==2?'selected="true"':'') + '>' + this.locale.dayNames[1] + '</option></select></div></div>' +
			 '<div class="margintop2"><label class="ss_event_editor">' + this.locale.workDayStartsAtLabel + ':</label><div><select name="workDayStart">';
			
			for (var hour = 0; hour <=12; hour++) {
				htmlCode += ('<option value="' + hour + '" ' + (workDayStart==hour?'selected="true"':'') + '>' + ss_calendar_formatHour(hour, this.locale.lang) + '</option>');
			}
			
			htmlCode += '</select></div></div>' +
			 '</div>' +
			 '<div class="margintop3" style="text-align: right;"><input type="button" value="' + this.locale.submitLabel + '" onclick="ss_calendar_settings.save();"/>' +
			 '</form></div></td></tr><tr><td width="30px"><div class="ss_popup_bottomleft"></div></td><td width="100%"><div class="ss_popup_bottomcenter"></div></td>' +
	         '<td width="40px"><div class="ss_popup_bottomright"></div></td></tr></tbody></table>';
			
			calConfigureDiv.innerHTML = htmlCode;
			
			// Link into the document tree
			document.getElementsByTagName("body").item(0).appendChild(calConfigureDiv);

			dojo.connect(dojo.byId("ss_calendar_configure_close"), "onclick", function(evt) {
				ss_calendar_settings.cancel();
		    });
		    
			ss_showPopupDivCentered(this.divId);	
		},
		
		save : function () {
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"saveCalendarConfiguration"});
			//console.log("save calendar configuration");
			dojo.xhrGet({
		    	url: url,
		    	handleAs: "json-comment-filtered",		    	
				error: function(err) {
					alert(ss_not_logged_in);
					ss_calendar_import.cancel();
				},
				load: function(data) {
					if (data && data.notLoggedIn) {
						alert(ss_not_logged_in);
					} else if (data && data.status == "ok") {
						// ok...
					} else {
						alert("Wrong server response.");
					}
					var doDocReload = true;
					if (window.top.ss_getUrlFromContentHistory) {
						var url = window.top.ss_getUrlFromContentHistory(0);
						if (url && (0 < url.length)) {
							doDocReload = false;
							ss_cancelPopupDiv(this.divId);
							window.top.ss_gotoContentUrl(url);
						}
					}
					if (doDocReload) {
						document.location.reload();
					}
				},
				preventCache: true,
				form: "ss_calendar_settings_form"
			});
	
		},
		
		cancel : function () {
			ss_cancelPopupDiv(this.divId);
			return false;
		}	
	}
}

function ss_calendar_formatHour(hour, lang) {
	var d = new Date();
	d.setHours(hour);
	// The "hourOnly" is custom date format added under dojo/ssf
	var hourS = dojo.date.locale.format(d, {formatLength: 'hourOnly', locale: lang});
	// next line because dojo.date.format requires to use separators in pattern 
	return hourS.replace(" ", "").toLowerCase();
}

function ss_calFindOptionByValue(eSel, value) {
	for (var i = 0; i < eSel.options.length; i += 1) {
		if (value == eSel.options[i].value) {
			return( i );
		}
	}
	return( -1 );
}
