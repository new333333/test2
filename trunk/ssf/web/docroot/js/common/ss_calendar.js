//
//   Copyright (c) 2007 / SiteScape, Inc.  All Rights Reserved.
//
//  This information in this document is subject to change without notice 
//  and should not be construed as a commitment by SiteScape, Inc.  
//  SiteScape, Inc. assumes no responsibility for any errors that may appear 
//  in this document.
//
//  Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
//  is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
//  Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
//
//  SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
//
//

dojo.require("dojo.html");
dojo.require("dojo.html.util");
dojo.require("dojo.html.selection");
dojo.require("dojo.event");
dojo.require("dojo.lfx");


function ss_fadeAndDestroy(e, t) {
    dojo.lfx.fadeHide(e, t, dojo.lfx.easeIn, function(nodes) {
        dojo.lang.forEach(nodes, dojo.dom.removeNode);
    }).play();
}


Date.hourTickList = ["12am","1am","2am","3am","4am","5am","6am","7am","8am","9am","10am","11am",
                   			"12pm","1pm","2pm","3pm","4pm","5pm","6pm","7pm","8pm","9pm","10pm","11pm"];
Date.dayNamesShort = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
Date.monthNamesShort = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
Date.monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
Date.shortMeridianA = 'a';
Date.shortMeridianP = 'p';
Date.meridianTime = 12;
Date.userTimeZoneOffset = 0;

Date.prototype.getMonthName = function() {
	return Date.monthNames[this.getMonth()];
}

Date.prototype.getMonthNameShort = function() {
	return Date.monthNamesShort[this.getMonth()];
}

Date.prototype.getDaysInMonth = function() {
	return 32 - new Date(this.getFullYear(), this.getMonth(), 32).getDate();
}

Date.shortTime = function(t) {
    var hour = Math.floor(t);
    var min = Math.round((t - hour) * 60);
    if (min == 0) {
        min = '';
    } else {
        min = ":" + ((min < 10) ? "0" : "")+ min;
    }
    if (hour < 12) {
        return '<span class="ss_cal_eventTime">' + hour + min + this.shortMeridianA + '</span>';
    } else if (hour == 12) {
        return '<span class="ss_cal_eventTime">' + hour + min + this.shortMeridianP + '</span>';
    } else if (hour > 12) {
        return  '<span class="ss_cal_eventTime">' + (hour - this.meridianTime) + min + this.shortMeridianP + '</span>';
    }
}

Date.getDayHeader = function(date) {
	return date.getDate() + "-" + date.getMonthNameShort();
}

/* Ignores times.
 	returns:
 		-1 given date is before
 		0 dates are equals
 		1 given date is after

 */
Date.prototype.compareByDate = function(date) {
    var difference = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0) -
    					Date.UTC(this.getFullYear(), this.getMonth(), this.getDate(), 0, 0, 0);
   return difference;
}

Date.prototype.daysTillDate = function(date) {
    var difference = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0) -
    					Date.UTC(this.getFullYear(), this.getMonth(), this.getDate(), 0, 0, 0);
    
    return difference/(1000*60*60*24);
}


/*
	Result: date + offset days.
	offset - int (days) or string "month" or "-month"
*/
Date.prototype.addDays = function (days) {
	if (days == "month") {
		return new Date(this.getFullYear(), this.getMonth() + 1, this.getDate());	
	} else if (days == "-month") {
		return new Date(this.getFullYear(), this.getMonth() - 1, this.getDate());
	}
	return new Date(this.getTime() + days*24*60*60*1000);
}

// Records data about calendars (e.g., display colors, tick formats, layout data)
var ss_cal_CalData = {

	/* Presentation data */
    map: new Array(),
    setMap: function(pMap) { for (var i in pMap) { this.map[pMap[i].calsrc] = pMap[i]; } },
    box: function(src) { return this.map[src].box },
    border: function(src) { return this.map[src].border },
    
    today : null,

	monthViewInfo : new Array(),

    setMonthViewInfo : function (year, month, daysInMonth, startViewDate, endViewDate) {
    	this.monthViewInfo[year + "/" + month] = {
    		year : year,
    		month : month,
			daysInMonth : daysInMonth,
			startViewDate : new Date(startViewDate.year, startViewDate.month, startViewDate.dayOfMonth),
			endViewDate : new Date(endViewDate.year, endViewDate.month, endViewDate.dayOfMonth)};
    },
    
	getMonthViewInfo : function (date) {
		var a  = date.getFullYear() + "/" + date.getMonth();
		if (typeof this.monthViewInfo[a] == "undefined") {
			return null;
		}
		return this.monthViewInfo[a];
    },
	    
    setToday : function (date) {
    	this.today = new Date(date.year, date.month, date.dayOfMonth);
    },
    
    isToday : function (date) {
		return (this.today.getFullYear() == date.getFullYear() &&
				this.today.getMonth() == date.getMonth() &&
				this.today.getDate() == date.getDate());
    },
    
    loadEventsByDate : function (grid, date, requiredDay) {
	   	if (ss_cal_CalData.getMonthViewInfo(date) &&
	   		(!requiredDay || ss_cal_CalData.getMonthViewInfo(requiredDay))) {
   			ss_cal_Grid.setCurrentDate(date);
   			ss_cal_Grid.setFirstDayToShow(date);
    		ss_cal_Grid.activateGrid(grid);
    		ss_cal_Events.redrawAll();
    		return;
    	}

    	var url = ss_findEventsUrl;
    	
    	var dateToLoad = date;
    	if (requiredDay && !ss_cal_CalData.getMonthViewInfo(requiredDay)) {
    		dateToLoad = requiredDay;
    	}
    	var m = dateToLoad.getMonth() + 1
		url += "\&year=" + dateToLoad.getFullYear();
		url += "\&month=" + m;
		url += "\&dayOfMonth=" + dateToLoad.getDate();
		url += "\&randomNumber="+ss_random++;

		var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				alert(ss_not_logged_in);
			},
			load: function(type, data, evt) {
				ss_cal_CalData.setMonthViewInfo(data.monthViewInfo.year, data.monthViewInfo.month, data.monthViewInfo.numberOfDaysInView,
					data.monthViewInfo.startViewDate, data.monthViewInfo.endViewDate);
							
				ss_cal_Events.set(data.events);
				ss_cal_Grid.setCurrentDate(date);
				ss_cal_Grid.setFirstDayToShow(date);
				ss_cal_Grid.activateGrid(grid);
		        ss_cal_Events.redrawAll();
			},
						
			mimetype: "text/json",
			method: "get"
		};
	   
		dojo.io.bind(bindArgs);
		

    }
}

//////////////////////////////////////////////////////////////////////////////////////////

var ss_cal_Grid = {

    // Some defaults
    gridSize: 7,
    gridIncr: 7,
    firstDayToShow: null,
    readOnly: false,
    monthGridWeeks: 5,
    currentType: 'day',
    dayGridCreated : false,
    
    currentDate: null,
    
	setCurrentDate : function (date) {
		this.currentDate = date;
	},
	
	setFirstDayToShow : function (date) {
		this.firstDayToShow = date;
	},

    activateGrid: function(gridType) {
    	if (!gridType) { gridType = this.currentType; }
        if (gridType != "") { this.currentType = gridType; }
        if (this.currentType == "day") {
	    	if (!this.firstDayToShow) {
	    		this.firstDayToShow = this.currentDate;
	    	}
	    	
            dojo.html.hide(dojo.byId("ss_cal_MonthGridMaster"));
            dojo.html.show(dojo.byId("ss_cal_DayGridMaster"));

            this.drawDayHeader("ss_cal_dayGridHeader", this.gridSize, this.firstDayToShow);
            this.drawDayGrid("ss_cal_dayGridAllDay", ss_cal_CalData.today, this.gridSize, this.firstDayToShow, "ss_cal_allDay", 1); 
            this.drawDayGrid("ss_cal_dayGridHour", ss_cal_CalData.today, this.gridSize, this.firstDayToShow, "ss_cal_hourGrid", 0); 
            this.drawHourMarkers("hourHeader", Date.hourTickList);
            if (!this.dayGridCreated && !this.readOnly) {
                dojo.event.connect(dojo.byId("ss_cal_dayGridHour"),  "onmousedown", function(evt) { ss_cal_CalEvent.mouseIsDown(evt, dojo.byId("ss_cal_dayGridHour"))});
                dojo.event.connect(dojo.byId("ss_cal_dayGridAllDay"), "onmousedown", function(evt) { ss_cal_CalAllDayEvent.mouseIsDown(evt, dojo.byId("ss_cal_dayGridAllDay"))});
                this.dayGridCreated = true;
            }
			this.showCalendarDaysDescription(this.firstDayToShow);
        } else if (this.currentType == "month") {
        	var monthViewInfo = ss_cal_CalData.getMonthViewInfo(this.currentDate);
    		this.firstDayToShow = monthViewInfo.beginView;
    		
            dojo.html.hide(dojo.byId("ss_cal_DayGridMaster"));
            dojo.html.show(dojo.byId("ss_cal_MonthGridMaster"));
            
			this.drawMonthGrid(ss_cal_CalData.today, this.currentDate, monthViewInfo);
			this.showCalendarMonthDescription();
        }
    },
    
    showCalendarMonthDescription: function(currentDate) {
    	this.showCalendarDescription(this.currentDate.getMonthName() + ", " + this.currentDate.getFullYear());
    },
    
    showCalendarDaysDescription: function(firstDayToShow) {
    	var descr = firstDayToShow.getDate() + " " + firstDayToShow.getMonthNameShort() + " " + firstDayToShow.getFullYear();

    	var lastDayToShow = firstDayToShow.addDays(this.gridSize - 1);
    	if (firstDayToShow.compareByDate(lastDayToShow) != 0) {
    		descr += " - ";
    		descr += lastDayToShow.getDate() + " " + lastDayToShow.getMonthNameShort() + " " + lastDayToShow.getFullYear();	
    	}
    	
    	this.showCalendarDescription(descr);
    },

    showCalendarDescription: function(descr) {
        var calViewDescription = document.getElementById("ss_calViewDatesDescriptions");
    	if (calViewDescription) {
    		calViewDescription.innerHTML = descr;
    	}
    },

    drawDayGrid: function(containerId, today, howManyDays, firstDayToShow, ruleId, justVertical) {
        var container = dojo.byId(containerId);
        var dayOffset = 0;
        var hourOffset = 0;
        var dayOffsetSize = (1.0 / howManyDays) * 100.0;
        var toDestroy = new Array();
        
        
        var todayMarker = dojo.byId(containerId + "_Today");
        if (!todayMarker) {
            var todayMarker = document.createElement("div");
            todayMarker.className = "ss_cal_todayMarker";
			todayMarker.setAttribute("id", containerId + "_Today");
			todayMarker.style.height =  "100%";
            todayMarker.style.display = "none";
            container.appendChild(todayMarker);
        }
        var v = dojo.dom.getFirstChildElement(container);
        while (v) {
            if (v.className == "ss_cal_dayRule") { toDestroy.push(v); };
            v = dojo.dom.getNextSiblingElement(v);
        }
        while (toDestroy.length) { dojo.dom.removeNode(toDestroy.pop()); }


		var todayOffsetInView = 0;
		var todayVisible = false;
		var currentDayToShow = firstDayToShow;
        for (var x = 0; x < howManyDays; x++) {
            currentDayToShow = currentDayToShow.addDays(x);
        	var isToday = ss_cal_CalData.isToday(currentDayToShow);
        	todayVisible = todayVisible || isToday;
        	if (isToday) {
        		todayOffsetInView = x;
        	}
        	
            var vrule = document.createElement("div");
            vrule.setAttribute("id", ruleId + x);
            vrule.className = "ss_cal_dayRule";
            vrule.style.left = dayOffset + "%";
            container.appendChild(vrule);
            dayOffset += dayOffsetSize;
        }
        
        if (todayVisible) {
			todayMarker.style.width = dayOffsetSize + "%";
            todayMarker.style.left = (todayOffsetInView * dayOffsetSize) + "%";
            dojo.html.show(todayMarker);
        } else {
        	dojo.html.hide(todayMarker);
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
        var dayOffset = 0;
        var hourOffset = 0;
        var dayOffsetSize = (1.0 / howManyDays) * 100.0;

        var toDestroy = new Array();
        var v = dojo.dom.getFirstChildElement(container);
        while (v) {
            if (dojo.html.hasClass(v, "ss_cal_gridHeaderText")) { toDestroy.push(v); };
            v = dojo.dom.getNextSiblingElement(v);
        }
        while (toDestroy.length) { dojo.dom.removeNode(toDestroy.pop()); }

		var currentDayToShow = firstDayToShow;
        for (var x = 0; x < howManyDays; x++) {
            var badge = document.createElement("div");
            badge.className = "ss_cal_gridHeaderText";

            if (ss_cal_CalData.isToday(currentDayToShow)) {
                badge.className += " ss_cal_gridHeaderTextToday";
            }
            badge.style.left = dayOffset + "%";
            badge.style.width = dayOffsetSize + "%";
               
            badge.innerHTML = '<a href="javascript: ;" onClick="ss_cal_Events.switchView(\'daydirect\', ' + currentDayToShow.getFullYear() + ', ' + currentDayToShow.getMonth() + ', ' + currentDayToShow.getDate() + ')">' + 
               Date.getDayHeader(new Date(currentDayToShow.getFullYear(), currentDayToShow.getMonth() , currentDayToShow.getDate())) + '</a>';                     
            container.appendChild(badge);
            dayOffset += dayOffsetSize;
            
            currentDayToShow = currentDayToShow.addDays(1);
        }
    },

    drawMonthGrid: function(today, currentDate, monthViewInfo) {
        var container = dojo.byId("ss_cal_monthGrid");
        var vOffset = 0;
        var hOffset = 0;
        this.monthGridWeeks = 5;
        if (monthViewInfo.daysInMonth > 35) {
        	this.monthGridWeeks = 6;
        } else if (monthViewInfo.daysInMonth < 35) {
        	this.monthGridWeeks = 4;
        }
        var vOffsetSize = (1.0 / 7) * 100.0;
        var hOffsetSize = (1.0 / this.monthGridWeeks) * 100.0;
        var header = dojo.byId("ss_cal_monthGridHeader");


		var todayMarker = dojo.byId("ss_cal_monthGridToday");
		if (!todayMarker) {
			todayMarker = document.createElement("div");
			todayMarker.className = "ss_cal_todayMarker";
			todayMarker.setAttribute("id", "ss_cal_monthGridToday");
			container.appendChild(todayMarker);
		}
        todayMarker.style.width = vOffsetSize + "%";
        todayMarker.style.height = hOffsetSize + "%";
        todayMarker.style.display = "none";
        
        var toDestroy = new Array();
        var v = dojo.dom.getFirstChildElement(container);
        while (v) {
            if (v.className != "ss_cal_todayMarker") { toDestroy.push(v); };
            v = dojo.dom.getNextSiblingElement(v);
        }
        while (toDestroy.length) { dojo.dom.removeNode(toDestroy.pop()); }


        for (var x = 0; x < 7; x++) {
            vOffset += vOffsetSize;
            var vrule = document.createElement("div");
            vrule.className = "ss_cal_monthVRule";
            vrule.style.left = vOffset + "%";
            container.appendChild(vrule);
        }
        vOffset = 0;
        for (var x = 0; x < 7; x++) {
            var badge = document.createElement("div");
            badge.className = "ss_cal_gridHeaderText";
            badge.style.left = vOffset + "%";
            badge.style.width = vOffsetSize + "%";
            var badgeText = document.createTextNode(Date.dayNamesShort[x]);
            badge.appendChild(badgeText);
            header.appendChild(badge);
            vOffset += vOffsetSize;
        }

        while (hOffset < 100) {
            hOffset += hOffsetSize;
            var hrule = document.createElement("div");
            hrule.className = "ss_cal_monthHRule";
            hrule.style.top = hOffset + "%";
            container.appendChild(hrule);
        }            

		var counter = 0;
		if (monthViewInfo.startViewDate.getMonth() != currentDate.getMonth()) {
			var nextDate = monthViewInfo.startViewDate;
			var daysInMonth = nextDate.getDaysInMonth();
			for (var x = nextDate.getDate(); x <= daysInMonth; x++) {
				this.drawMonthGridDayBadge(new Date(nextDate.getFullYear(), nextDate.getMonth(), x), counter++, container, vOffsetSize, hOffsetSize);
			}
		}
		
		var daysInMonth = currentDate.getDaysInMonth();
		for (var x = 1; x <= daysInMonth; x++) {
			this.drawMonthGridDayBadge(new Date(currentDate.getFullYear(), currentDate.getMonth(), x), counter++, container, vOffsetSize, hOffsetSize);
		}
		
		if (monthViewInfo.endViewDate.getMonth() != currentDate.getMonth()) {
			var nextDate = monthViewInfo.endViewDate;
			for (var x = 1; x <= nextDate.getDate(); x++) {
				this.drawMonthGridDayBadge(new Date(nextDate.getFullYear(), nextDate.getMonth(), x), counter++, container, vOffsetSize, hOffsetSize);
			}
		}
    },
    
    drawMonthGridDayBadge : function(date, counter, container, vOffsetSize, hOffsetSize) {
    	var isToday = ss_cal_CalData.isToday(date);
    	if (isToday) {
    		var today = dojo.byId("ss_cal_monthGridToday");
			today.style.left = (vOffsetSize * (counter % 7)) + "%";
            today.style.top = (hOffsetSize * Math.floor(counter / 7)) + "%";
            dojo.html.show(today);
    	}    
    
     	var d = (counter % 7);
        var w = Math.floor(counter / 7);
        var badge = document.createElement("div");
        badge.className = "ss_cal_monthGridDayBadge";
        if (isToday) {
            badge.className += " ss_cal_monthGridDayBadgeToday";
        }
        badge.style.left = (d * vOffsetSize) + "%";
        badge.style.top = (w * hOffsetSize) + "%";
        badge.innerHTML = '<a href="javascript: ;" onClick="ss_cal_Events.switchView(\'daydirect\', ' + date.getFullYear() + ', ' + date.getMonth() + ', ' + date.getDate() + ')">' + 
               date.getDate() + '</a>';
        container.appendChild(badge);
    },    

    drawHourMarkers: function(containerId, ticks) {
    	// TODO: creates always new divs, memory leak?
        var container = dojo.byId(containerId);
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
            var tickText = document.createTextNode(ticks[hour]);
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

    fullDayGrid: function() {
        outer = dojo.byId("ss_cal_dayGridWindowOuter");
        inner = dojo.byId("ss_cal_dayGridWindowInner");
        //outer.style.height = "1008px";
        //inner.style.top = "-3px";
        dojo.lfx.propertyAnimation(outer, [{ property: "height", start: 500, end: 1008 }], 200).play();
        dojo.lfx.propertyAnimation(inner, [{ property: "top", start: -255, end: -3 }], 200).play();
        dojo.byId("dayGridToggle").innerHTML = "Work Day";
        dojo.byId("dayGridToggle").onclick = ss_cal_Grid.workDayGrid;
    },

    workDayGrid: function() {
        outer = dojo.byId("ss_cal_dayGridWindowOuter");
        inner = dojo.byId("ss_cal_dayGridWindowInner");
        //outer.style.height = "500px";
        //inner.style.top = "-255px";
        dojo.lfx.propertyAnimation(outer, [{ property: "height", start: 1008, end: 500 }], 200).play();
        dojo.lfx.propertyAnimation(inner, [{ property: "top", start: -3, end: -255 }], 200).play();
        dojo.byId("dayGridToggle").innerHTML = "Full Day";
        dojo.byId("dayGridToggle").onclick = ss_cal_Grid.fullDayGrid;
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

var ss_cal_CalAllDayEvent = {
    currGrid: null,
    currDispId: null,
    currDay: null,

    allDayCount:  new Array(),
    calEvents: new Array(),
    currEventData: new Object(),

    mouseIsDown: function(evt, grid) {
        ss_cal_Events.cancelHover(false);
        //evt = (evt) ? evt : ((event) ? event : null);
        gridX = evt.clientX - dojo.html.abs(grid).x;
        gridWidth = grid.offsetWidth;
        // Calculate day offsets.
        dayOffset = Math.floor((gridX / gridWidth)  / (1.0 / ss_cal_Grid.gridSize));
        var firstDayOnGrid = ss_cal_Grid.firstDayToShow;
        // TODO: fix it! use addDate or something else....
		this.currDay = firstDayOnGrid.addDays(dayOffset);
        
        hourOffset = this.recordHourOffset(this.currDay.getFullYear(), this.currDay.getMonth(), this.currDay.getDate());
        this.currDispId = ss_cal_drawCalendarEvent(grid.id, ss_cal_Grid.gridSize, 1, 0, dayOffset, hourOffset, -1, "All day", "", "#CCCCCC", "#CCCCCC", "");
        this.resetGridHeight();
        dojo.event.connect(dojo.body(), "onmouseup", this, "mouseIsUp");
        this.currEventData = new Object();
        this.currEventData.day = dayOffset;
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
            dojo.byId("ss_cal_dayGridAllDay").style.height = ((maxEvents + 1) * 21) + "px";
            // Force IE to recalculate the offset of the next blocks
            dojo.byId("ss_cal_dayGridWindowOuter").style.top = "1px";
            dojo.byId("ss_cal_dayGridWindowOuter").style.top = "0px";
        } else {
            dojo.lfx.propertyAnimation("ss_cal_dayGridAllDay", [{ property: "height", end: ((maxEvents + 1) * 21) }], 100).play();
        }
    },

    mouseIsUp: function(evt) {
        if (!dojo.html.selection.isCollapsed()) { dojo.html.selection.collapse(true); }
        dojo.event.disconnect(dojo.body(), "onmouseup",   this, "mouseIsUp");
        ss_cal_newEventInfo(evt, this);
    },

    deleteEvent: function(id) {
        ss_fadeAndDestroy(dojo.byId("calevt" + id).parentNode, 200);
    },

    deleteCurrentEvent: function() {
        this.deleteEvent(this.currDispId);
        this.recordEvent(this.currDay.getFullYear(), this.currDay.getMonth() , this.currDay.getDate(), -1);
        this.resetGridHeight()
    },


    saveCurrentEvent: function() {
        this.deleteEvent(this.currDispId);
        this.currEventData.title = "New" + this.currDispId;
        this.currEventData.text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam viverra pretium nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vivamus lorem tortor, commodo vel, malesuada nec, sodales ut, ante. Duis ut leo non nisi ultricies ultrices. Vivamus vitae turpis sed justo dignissim porttitor.";
        this.currEventData.calsrc = "cal1";
        this.currEventData.eventId = "GAD" + this.currDispId;
        this.currEventData.day += ss_cal_Grid.gridOffset;// TODO: fix it! add month, year and dayOfMonth 
        ss_cal_Events.set([this.currEventData]);
        ss_cal_Events.redrawAll();
    },


    reset: function() {    
		// it doesn't work correctly for maps or objects
        // while (this.allDayCount.length) { this.allDayCount.pop() };
        this.allDayCount = new Array();
    }

}

//////////////////////////////////////////////////////////////////////////////////////////

var ss_cal_CalEvent = {

    currGrid: null,
    currDispId: null,
    calEvents: new Array(),
    currEventData: new Object(),


    mouseIsDown: function(evt, grid) {
        ss_cal_Events.cancelHover(false);
        //evt = (evt) ? evt : ((event) ? event : null);
        //console.log(dojo.html.getTotalOffset(grid, "top", 0));
        //console.log(dojo.html.getAbsoluteY(grid));
        // The offset is affected by the enclosing "window" divs, so we factor it here.
        gridX = evt.clientX - dojo.html.abs(grid).x;
        gridY = (evt.clientY + document.body.scrollTop + document.documentElement.scrollTop) - dojo.html.getTotalOffset(grid, "top", 0);

        gridWidth = grid.offsetWidth;
        currGrid = grid;
        dojo.event.connect(dojo.body(), "onmousemove", this, "whataDrag");
        dojo.event.connect(dojo.body(), "onmouseup", this, "mouseIsUp");
    
        // Calculate hour and day offsets.
        hourOffset = Math.floor(gridY / 42);
        if ((gridY % 42) > 21) { hourOffset += 0.5 }
        var dayOffset = Math.floor((gridX / gridWidth)  / (1.0 / ss_cal_Grid.gridSize));
        
		var firstDayOnGrid = ss_cal_Grid.firstDayToShow;

		var currDay = firstDayOnGrid.addDays(dayOffset);
        this.currDispId = ss_cal_drawCalendarEvent(grid.id, ss_cal_Grid.gridSize, 1, 0, dayOffset, hourOffset, 30, "", "", "#CCCCCC", "#CCCCCC", "");
        evt.cancelBubble = true;
        this.currEventData = new Object();
        this.currEventData.date = currDay;
        this.currEventData.start = hourOffset;
        this.currEventData.dur = 30;
    },

    whataDrag: function(evt) {
        //evt = (evt) ? evt : ((event) ? event : null);
        var gridQuantum = 3.5 * 6;  // 30 minutes
        var currBox = dojo.byId("calevt" + this.currDispId);
        gridX = (evt.clientX + document.body.scrollLeft);
        gridY = (evt.clientY + document.body.scrollTop + document.documentElement.scrollTop)  -  dojo.html.getTotalOffset(currGrid, "top", 0) - currBox.parentNode.offsetTop;
        if (gridY > 12) {
            gridY = gridQuantum + Math.floor(gridY / gridQuantum) * gridQuantum;
            currBox.style.height = (gridY - 4) + "px";
        }
        var duration = Math.floor((((parseInt(currBox.style.height) + 4) / 42) * 60));
        currBox.innerHTML = duration + ' min';
        this.currEventData.dur = duration;

        if (!dojo.html.selection.isCollapsed()) { dojo.html.selection.collapse(true); }
        evt.cancelBubble = true;
        return false;
    
    },

    mouseIsUp: function(evt) {
        if (!dojo.html.selection.isCollapsed()) { dojo.html.selection.collapse(true); }
        dojo.event.disconnect(dojo.body(), "onmousemove", this, "whataDrag");
        dojo.event.disconnect(dojo.body(), "onmouseup",   this, "mouseIsUp");
        ss_cal_newEventInfo(evt, this);
    },

    deleteEvent: function(id) {
        ss_fadeAndDestroy(dojo.byId("calevt" + id).parentNode, 200);
    },

    deleteCurrentEvent: function() { this.deleteEvent(this.currDispId); },


    saveCurrentEvent: function() {
        this.deleteEvent(this.currDispId);
        this.currEventData.title = "New" + this.currDispId;
        this.currEventData.text = " Pellentesque urna elit, lacinia sit amet, tempor a, consectetuer et, lacus. Nunc massa.";
        this.currEventData.calsrc = "cal1";
        this.currEventData.eventId = "GXX" + this.currDispId;
        this.currEventData.day += ss_cal_Grid.gridOffset;
        ss_cal_Events.set([this.currEventData]);
        ss_cal_Events.redrawAll();
    }


}

//////////////////////////////////////////////////////////////////////////////////////////

function ss_cal_centerDivInViewport(id) {
	var node = dojo.byId(id);
	var nBox = dojo.html.getBorderBox(node);
	var vp = dojo.html.getViewport();
	node.style.left = (Math.floor((vp.width/2) - (nBox.width/2)) +  dojo.html.getScrollLeft()) + "px";
	node.style.top = (Math.floor((vp.height/2) - (nBox.height/2)) +  dojo.html.getScrollTop()) + "px";
}

function ss_cal_newEventInfo(evt, gridControl) {
    evt = (evt) ? evt : ((event) ? event : null);
    
    var currEventData = ss_cal_CalEvent.currEventData;
    
    var url = ss_addCalendarEntryUrl;
    url += "&year=" + currEventData.date.getFullYear();
    url += "&month=" + currEventData.date.getMonth();
    url += "&dayOfMonth=" + currEventData.date.getDate();
    url += "&time=" + currEventData.start.toString().replace(".5", ":30");
    url += "&duration=" + currEventData.dur;
    
    ss_openUrlInPortlet(url, true);
}

function ss_cal_eventInfo(evt, eventId) {
    evt = (evt) ? evt : ((event) ? event : null);
    
    var event = ss_cal_Events.eventData[eventId];
    var viewHref = ss_viewEventUrl;
	viewHref += "&entryId=" + event.entryId;
    ss_loadEntryUrl(viewHref, event.entryId);
}


function ss_cal_drawCalendarEvent(containerId, gridDays, shareCount, shareSlot, day, time, duration, title, text, boxColor, borderColor, eventId) {
    var container = dojo.byId(containerId);
    var dayOffsetSize = (1.0 / gridDays) * 100.0;
    var e;
    var w;

    if (day >= gridDays) return;	
        
    ss_cal_Events.displayId += 1;

    var ebox = document.createElement("div");
    ebox.className = "ss_cal_eventBox";

    if (eventId != "") {
        dojo.event.connect(ebox, "onmousedown", function(evt) { ss_cal_eventInfo(evt, eventId); evt.cancelBubble = true; });
        dojo.event.connect(ebox, "onmouseover", function(evt) { ss_cal_Events.requestHover(evt, eventId); });
    }


    w = (dayOffsetSize / shareCount);
    ebox.style.width = (w - 0.2) + "%";
    ebox.style.left = (((day * dayOffsetSize) + 0.2) + (w * shareSlot)) + "%";
    ebox.style.top = (time * 42) + "px";
    
    e = document.createElement("div");
    e.className = "ss_smallRBoxTop2";
    e.style.backgroundColor = borderColor;
    ebox.appendChild(e);

    e = document.createElement("div");
    e.className = "ss_smallRBoxTop1";
    e.style.backgroundColor = borderColor;
    ebox.appendChild(e);

    e = document.createElement("div");
    e.className = "ss_smallRBoxBody ss_cal_eventBody";
    e.setAttribute("id", "calevt" + ss_cal_Events.displayId);
    e.style.backgroundColor = boxColor;
    e.style.borderColor = borderColor
    e.style.height = (((((duration <= 0) ? 30 : duration) / 60) * 42) - 4) + "px";
    var eHtml = "";
    if (duration >= 0) {
        eHtml = Date.shortTime(time);
    }
    
   	eHtml += '<a href="javascript: //">' + title + '</a>';
    
    eHtml += "<br/>" + text;
    e.innerHTML = eHtml;
    ebox.appendChild(e);

    e = document.createElement("div");
    e.className = "ss_smallRBoxBtm1";
    e.style.backgroundColor = borderColor;
    ebox.appendChild(e);

    e = document.createElement("div");
    e.className ="ss_smallRBoxBtm2";
    e.style.backgroundColor = borderColor;
    ebox.appendChild(e);

    container.appendChild(ebox);
    dojo.html.setOpacity(ebox,0);
    var o = (title == "") ? 0.8 : 1.0
    dojo.lfx.propertyAnimation(ebox, [{ property: "opacity", start: 0, end: o }], 200).play();
    
    return ss_cal_Events.displayId;
}


function ss_cal_drawMonthEventBlock(containerId, date, eventCount, eventList) {

	// find day number on grid
	var monthViewInfo = ss_cal_CalData.getMonthViewInfo(ss_cal_Grid.currentDate);
	
	var dayNumber = monthViewInfo.startViewDate.daysTillDate(date);
	
    var container = dojo.byId(containerId);
    var vOffsetSize = (1.0 / 7) * 100.0;
    var hOffsetSize = (1.0 / ss_cal_Grid.monthGridWeeks) * 100.0;
    var resultDisplayIds = new Array();
    var heightFactor = 19.05;
    var heightFactor1 = 19.4;
    var badgeOffset = 4.5;
    var eventOffset = 5.18;
    if (ss_cal_Grid.monthGridWeeks > 5) {
        heightFactor = 25.1;
        heightFactor1 = 25.5;
	    badgeOffset = 4.2;
	    eventOffset = 4.1;
    }

    var eventHeight = 1;
    if (eventCount > 3) {
        iterations = 2;
    } else {
        iterations = eventCount;
    }
    if (iterations == 1) { eventHeight = 3 }

    var w = Math.floor(dayNumber / 7);
    var d = dayNumber % 7;

    for (var i = 0; i < iterations; i++) {
        ss_cal_Events.displayId += 1;
        resultDisplayIds.push(ss_cal_Events.displayId);
        var ebox = document.createElement("div");
        ebox.className = "ss_cal_eventBox ss_cal_monthEventBody";

        ebox.style.left = ((d * vOffsetSize) + 0.15) + "%";
    
        ebox.style.top = ((w * hOffsetSize) + badgeOffset + (i * eventOffset)) + "%";
        ebox.style.width = "13.79%";

        ebox.setAttribute("id", "calevt" + ss_cal_Events.displayId);
        ebox.style.height = ((eventHeight/heightFactor) * 100) + "%";
        var e = ss_cal_Events.eventData[eventList[i]];
        ebox.style.backgroundColor = ss_cal_CalData.box(e.calsrc);
		var viewHref = ss_viewEventUrl;
    	viewHref += "&entryId=" + e.entryId;
		ebox.innerHTML = '<a href="'+viewHref+'" onClick="'+e.viewOnClick+' return false;">'+e.title+'</a>';
        container.appendChild(ebox);
        dojo.lfx.propertyAnimation(ebox, [{ property: "opacity", start: 0, end: 1 }], 200).play();
    }

    if (eventCount > 3) {
        var ebox = document.createElement("div");
        ss_cal_Events.displayId += 1;
        resultDisplayIds.push(ss_cal_Events.displayId);
        ebox.className = "ss_cal_eventBox ss_cal_monthEventBody";

        ebox.style.left = ((d * vOffsetSize) + 0.15) + "%";
    
        ebox.style.top = ((w * hOffsetSize) + badgeOffset + (2 * eventOffset)) + "%";
        ebox.style.width = "13.79%";

        ebox.setAttribute("id", "calevt" + ss_cal_Events.displayId);
        ebox.style.backgroundColor = "#BBBBBB";
        ebox.style.height = ((eventHeight/heightFactor1) * 100) + "%";
        ebox.innerHTML = "... plus " + (eventCount - 2) + " other events...";

        container.appendChild(ebox);
        dojo.lfx.propertyAnimation(ebox, [{ property: "opacity", start: 0, end: 1 }], 250).play();
    }


    return resultDisplayIds;
}

//////////////////////////////////////////////////////////////////////////////////////////

var ss_cal_Events = {
	eventsTypes : ["event", "creation", "activity"],
	eventsType : 0,
	
    displayId: 0,
    
    eventData: new Array(),
    
    collisions: new Array(),
    collisionI: new Array(),
    collisionM: new Array(),
    order: new Array(),
    
    monthGridEvents: new Array(),
    dayGridEvents: new Array(),
            
    set: function(newEvents) {
        for (var i in newEvents) {
            var nei = newEvents[i];
            
            // already loaded?
            if (this.eventData[nei.eventId]) {
            	continue;
            }
            
            // Normalize times
            if (nei.start.toString().indexOf(":") > 0) {
                var tarray = nei.start.split(":");
                start = parseFloat(tarray[0]);
                if (tarray.length > 1) { start += (parseFloat(tarray[1])/60) }
            } else {
                start = parseFloat(nei.start);
            }
            nei.start = start;
            nei.date = new Date(nei.year, nei.month - 1, nei.dayOfMonth);
            this.eventData[nei.eventId] = nei;
            
            this.setupDayDisplayRules(nei);
            this.setupMonthDisplayRules(nei);
            this.sortEvent(nei);
        }

		for (var i in this.eventsTypes) {
			if (this.order[this.eventsTypes[i]]) {        
        		this.order[this.eventsTypes[i]].sort();
        	}
        }
    },

    incrCollision: function(eventType, t) {
        if (typeof this.collisions[eventType] == "undefined") { 
        	this.collisions[eventType] = new Array(); 
        }
        if (typeof this.collisions[eventType][t] == "undefined") { 
        	this.collisions[eventType][t] = 0; 
        }
        this.collisions[eventType][t]++;
        return this.collisions[eventType][t];
    },
    
    setupDayDisplayRules: function(event) {
		this.incrCollision(event.eventType, event.date.getFullYear() + "/" + event.date.getMonth()  + "/" + event.date.getDate() + "/" + Math.floor(event.start)); 
    },

    setupMonthDisplayRules: function(event) {
        this.incrCollision(event.date.getFullYear() + "/" + event.date.getMonth()  + "/" + event.date.getDate());
        if (typeof this.collisionM[event.eventType] == "undefined") { 
        	this.collisionM[event.eventType] = new Array(); 
        }
        if (typeof this.collisionM[event.eventType][event.date.getFullYear() + "/" + this.fullWithZeros(event.date.getMonth() ) + "/" + event.date.getDate()] == "undefined") { 
        	this.collisionM[event.eventType][event.date.getFullYear() + "/" + this.fullWithZeros(event.date.getMonth() ) + "/" + event.date.getDate()] = new Array(); 
        }
        this.collisionM[event.eventType][event.date.getFullYear() + "/" + this.fullWithZeros(event.date.getMonth() ) + "/" + event.date.getDate()].push(event.eventId);
    },

    collisionCount: function(eventType, year, month, dayOfMonth, start) {
        return this.collisions[eventType][year + "/" + month + "/" + dayOfMonth + "/" + Math.floor(start)];
    },
    
    collisionIndex: function(eventType, year, month, dayOfMonth, start) {
        var t = year + "/" + month + "/" + dayOfMonth + "/" + Math.floor(start);
        if (typeof this.collisionI[eventType] == "undefined") { 
        	this.collisionI[eventType] = new Array(); 
        }
        if (typeof this.collisionI[eventType][t] == "undefined") { 
        	this.collisionI[eventType][t] = 0; 
        }
        return this.collisionI[eventType][t]++;
    },

    sortEvent: function(event) {
    	if (typeof this.order[event.eventType] == "undefined") {
    		this.order[event.eventType] = new Array();
    	}
		this.order[event.eventType].push(event.date.getFullYear() + "/" + this.fullWithZeros(event.date.getMonth() ) + "/" + this.fullWithZeros(event.date.getDate()) + "/" + (Math.floor(event.start * 10) + 1011) + "/" + event.eventId);
    },    
    
    undrawEvents: function() {
        this.undrawMonthEvents();
        this.undrawDayEvents();
    },

    undrawMonthEvents: function() {
        while (this.monthGridEvents.length) {
            dojo.dom.removeNode(dojo.byId("calevt" + this.monthGridEvents.pop()));
        }
    },

    undrawDayEvents: function() {
		while (this.dayGridEvents.length) {
			var eventId = this.dayGridEvents.pop();
			var event = this.eventData[eventId];
            dojo.dom.removeNode(dojo.byId("calevt" + event.displayId).parentNode);
            event.displayId = "";
        }	
    },
    
    fullWithZeros: function (c) {
    	if (c < 10) {
    		return "0" + c;
    	}
    	return c;
    },

    redrawDay: function() {
        ss_cal_CalAllDayEvent.reset();
        this.undrawEvents();
        for (var i in this.order[this.eventsTypes[this.eventsType]]) {
            var eid = this.order[this.eventsTypes[this.eventsType]][i].substr(16);
            var e = this.eventData[eid];
            
            if (ss_cal_Grid.firstDayToShow.compareByDate(e.date) < 0) {
            	// event in past
            	continue;
            }
            
            var gridDay = -1;
            if (ss_cal_Grid.firstDayToShow.getMonth()  == e.date.getMonth()) {
            	gridDay = e.date.getDate() - ss_cal_Grid.firstDayToShow.getDate();
            } else if ((ss_cal_Grid.firstDayToShow.getMonth() + 1)  == e.date.getMonth() + 1) {
            	 var monthViewInfo = ss_cal_CalData.getMonthViewInfo(ss_cal_Grid.currentDate);
            	 gridDay = e.date.getDate() + (monthViewInfo.daysInMonth - ss_cal_Grid.firstDayToShow.getDate());
            }
             
            if (gridDay < 0 || gridDay >= ss_cal_Grid.gridSize) {
                continue;
            }

            if (e.dur == 0) {
                var grid = "ss_cal_dayGridAllDay";
                this.eventData[eid].displayId = ss_cal_drawCalendarEvent(grid, ss_cal_Grid.gridSize, 1, 0,
                       gridDay, ss_cal_CalAllDayEvent.recordHourOffset(e.date.getFullYear(), e.date.getMonth(), e.date.getDate()), -1, e.title, e.text,
                       ss_cal_CalData.box(e.calsrc), ss_cal_CalData.border(e.calsrc), eid); 
				this.dayGridEvents.push(eid);
            } else {
                var grid = "ss_cal_dayGridHour";
                this.eventData[eid].displayId = ss_cal_drawCalendarEvent(grid, ss_cal_Grid.gridSize,
                       this.collisionCount(e.eventType, e.date.getFullYear(), e.date.getMonth(), e.date.getDate(), e.start),
                       this.collisionIndex(e.eventType, e.date.getFullYear(), e.date.getMonth(), e.date.getDate(), e.start),
                       gridDay, e.start, e.dur, e.title, e.text,
                       ss_cal_CalData.box(e.calsrc), ss_cal_CalData.border(e.calsrc), eid);
				this.dayGridEvents.push(eid);
            }
        }
        this.collisionI = new Array();
        ss_cal_CalAllDayEvent.resetGridHeight();
    },

    redrawMonth: function() {
        this.undrawEvents();
        for (var d in this.collisionM[this.eventsTypes[this.eventsType]]) {
            var grid = "ss_cal_monthGrid";
                        
            var monthViewInfo = ss_cal_CalData.getMonthViewInfo(ss_cal_Grid.currentDate);
            var year = 1 * d.substring(0, 4);
            var month = 1 * d.substring(5, 7);
            var dayOfMonth = 1 * d.substring(8, d.length);
            var date = new Date(year, month, dayOfMonth);
 			
			var end = new Date(year, month, dayOfMonth);
			if (monthViewInfo.startViewDate <= date && date <= monthViewInfo.endViewDate) {
	            var dids = ss_cal_drawMonthEventBlock(grid, date, this.collisionM[this.eventsTypes[this.eventsType]][d].length, this.collisionM[this.eventsTypes[this.eventsType]][d]);
	            while (dids.length) { this.monthGridEvents.push(dids.pop()); }
            }
        }
    },

    redrawAll: function() {
        if (ss_cal_Grid.currentType == "day") {
            this.redrawDay();
        } else if (ss_cal_Grid.currentType == "month") {
            this.redrawMonth();
        }
    },

    overEventId: "",
    hoverEventId: "",
    hoverTimer: 0,

    requestHover: function(evt, eventId) {
        if (this.overEventId != eventId) {
            this.overEventId = eventId;
            //console.log("requestHover", eventId);
            this.hoverTimer = setTimeout(function(){ss_cal_Events.displayHover(eventId);}, 1000);
            var e = this.eventData[eventId];
            var n = dojo.byId("calevt" + e.displayId).parentNode;
            n.onmouseout = function() { ss_cal_Events.cancelHover(true)};
        }
    },

    cancelHover: function(animate) {
        if (this.overEventId != "") {
            var hb = dojo.byId("hoverBox");
            if (animate) {
                dojo.lfx.html.fadeHide(hb, 100).play();
            } else {
                dojo.html.hide(hb);
            }
        } else {
            return;
        }
        if (this.overEventId != '') {
            clearTimeout(this.hoverTimer);
        }
        this.hoverEventId = '';
        this.overEventId = '';
    },

    displayHover: function(eventId) {
        //console.log("display?", eventId, this.hoverEventId);
        if (this.overEventId == eventId) {
            var e = this.eventData[eventId];
            var n = dojo.byId("calevt" + e.displayId).parentNode;
            //console.log("Hover: " + eventId);
            this.hoverEventId = eventId;

            var hb = dojo.byId("hoverBox");
            var ebox = dojo.html.abs(n);
            var eboxm = dojo.html.getBorderBox(n);
            hb.style.visibility = "visible";
            hb.innerHTML = dojo.byId("calevt" + e.displayId).innerHTML;
            hb.style.backgroundColor = ss_cal_CalData.box(e.calsrc);
            hb.style.borderColor = ss_cal_CalData.border(e.calsrc);
            dojo.html.setOpacity(hb,0);
            dojo.html.show(hb);
            dojo.html.placeOnScreen(hb, (ebox.left + eboxm.width), (ebox.top), 10, false, "TL");
            dojo.lfx.html.fadeIn(hb, 200).play();
        }
    },


    switchView: function(	/* String: "daydelta", "3daydelta", "week", "fortnight", "workweek", "month", "daydirect", "monthdirect", "prev", "next" */ 
    							mode, 
    							year, month, dayOfMonth) {
    	var grid;
    	var dayToShow;
    	var requiredDay;
    	switch (mode) {
			case "daydelta":
				ss_cal_Grid.gridSize = 1;
                ss_cal_Grid.gridIncr = 1;
                dayToShow = ss_cal_Grid.currentDate;
                grid = "day";
                break;
            case "3daydelta":
                ss_cal_Grid.gridSize = 3;
                ss_cal_Grid.gridIncr = 3;
                dayToShow = ss_cal_Grid.currentDate;
                grid = "day";
                break;
            case "week":
                ss_cal_Grid.gridSize = 7;
                ss_cal_Grid.gridIncr = 7;

                var firstDayToShow = ss_cal_Grid.currentDate.addDays(-(ss_cal_Grid.currentDate.getDay()));
                var lastDayToShow = firstDayToShow.addDays(ss_cal_Grid.gridIncr);
                
                dayToShow = firstDayToShow;
                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
                	requiredDay = lastDayToShow;
                }
                grid = "day";
                break;
            case "fortnight":
                ss_cal_Grid.gridSize = 14;
                ss_cal_Grid.gridIncr = 14;
                
                var firstDayToShow = ss_cal_Grid.currentDate.addDays(-(ss_cal_Grid.currentDate.getDay()));
                var lastDayToShow = firstDayToShow.addDays(14);
                
                dayToShow = firstDayToShow;
                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
                	requiredDay = lastDayToShow;
                }
                grid = "day";
                break;
            case "workweek":
                ss_cal_Grid.gridSize = 5;
                ss_cal_Grid.gridIncr = 7;
                
                var firstDayToShow = ss_cal_Grid.currentDate.addDays(-(ss_cal_Grid.currentDate.getDay()) + 1);
                var lastDayToShow = firstDayToShow.addDays(ss_cal_Grid.gridIncr);
                
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
                
            case "daydirect":
                ss_cal_Grid.gridSize = 1;
                ss_cal_Grid.gridIncr = 1;
                dayToShow = new Date(year, month, dayOfMonth);
                grid = "day";
                break;
                
            case "prev":
            	dayToShow = ss_cal_Grid.currentDate.addDays(ss_cal_Grid.currentType=="month"?("-"+ss_cal_Grid.currentType):-ss_cal_Grid.gridIncr);
            	
            	if (ss_cal_Grid.currentType != "month") {
	                var lastDayToShow = dayToShow.addDays(-ss_cal_Grid.gridIncr);
	                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
	                	requiredDay = lastDayToShow;
	                }
            	}
            	            	
	            grid = ss_cal_Grid.currentType;
                break;
            case "next":
            	dayToShow = ss_cal_Grid.currentDate.addDays(ss_cal_Grid.currentType=="month"?(ss_cal_Grid.currentType):ss_cal_Grid.gridIncr);
            	
            	if (ss_cal_Grid.currentType != "month") {
	                var lastDayToShow = dayToShow.addDays(ss_cal_Grid.gridIncr);
	                if (!ss_cal_CalData.getMonthViewInfo(lastDayToShow)) {
	                	requiredDay = lastDayToShow;
	                }
            	}
            	
	            grid = ss_cal_Grid.currentType;
                break;
        }    
			
		ss_cal_CalData.loadEventsByDate(grid, dayToShow, requiredDay);
		
    },
    
    toggleViewType: function() {
		if (this.eventsType < (this.eventsTypes.length - 1)) {
			this.eventsType++;
		} else {
			this.eventsType = 0;
		}
		var ss_calViewEntryTypes = document.getElementById("ss_calViewEntryTypes");
		if (this.eventsType == 0) {
			ss_calViewEntryTypes.innerHTML = ss_calendarEntriesViewType_Events;
		} else if (this.eventsType == 1) {
			ss_calViewEntryTypes.innerHTML = ss_calendarEntriesViewType_Creation;
		} else if (this.eventsType == 2) {
			ss_calViewEntryTypes.innerHTML = ss_calendarEntriesViewType_Activity;
		}

		this.redrawAll();
    }
    
};
