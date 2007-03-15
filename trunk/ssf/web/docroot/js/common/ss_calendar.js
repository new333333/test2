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


// Records data about calendars (e.g., display colors, tick formats, layout data)
var ss_cal_CalData = {
    map: new Array(),
    setMap: function(pMap) { for (var i in pMap) { this.map[pMap[i].calsrc] = pMap[i]; } },
    box: function(src) { return this.map[src].box },
    border: function(src) { return this.map[src].border },

    todayIndex: 0,

    hourTickList: ["12am","1am","2am","3am","4am","5am","6am","7am","8am","9am","10am","11am",
                   "12pm","1pm","2pm","3pm","4pm","5pm","6pm","7pm","8pm","9pm","10pm","11pm"],

    monthTickList: [31,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,1,2,3],

    dayNamesShort: ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],
    dayHeaders: new Array(),
    shortMeridianA: 'a',
    shortMeridianP:  'p',
    meridianTime: 12,

    shortTime: function(t) {
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
    },

    dayHeader: function(day) {
        if (typeof this.dayHeaders[day] == "undefined") { return "n/a" }
        return this.dayHeaders[day];
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

var ss_cal_Grid = {

    // Some defaults
    gridSize: 7,
    gridIncr: 7,
    gridOffset: 0,
    readOnly: false,
    dayGridDrawn: false,
    monthGridDrawn: false,
    currentType: 'day',

    activateGrid: function(gridType) {
        if (gridType != "") { this.currentType = gridType; }
        if (this.currentType == "day") {
            dojo.html.hide(dojo.byId("ss_cal_MonthGridMaster"));
            dojo.html.show(dojo.byId("ss_cal_DayGridMaster"));
            if (!this.dayGridDrawn) {
                this.drawDayHeader("ss_cal_dayGridHeader", this.gridSize, this.gridOffset);
                this.drawDayGrid("ss_cal_dayGridAllDay", this.dayGridDrawn, this.gridSize, this.gridOffset, "ss_cal_allDay",   1); 
                this.drawDayGrid("ss_cal_dayGridHour",   this.dayGridDrawn, this.gridSize, this.gridOffset, "ss_cal_hourGrid", 0); 
                this.drawHourMarkers("hourHeader", ss_cal_CalData.hourTickList);
                if (!this.readOnly) {
                    dojo.event.connect(dojo.byId("ss_cal_dayGridHour"),  "onmousedown", function(evt) { ss_cal_CalEvent.mouseIsDown(evt, dojo.byId("ss_cal_dayGridHour"))});
                    dojo.event.connect(dojo.byId("ss_cal_dayGridAllDay"), "onmousedown", function(evt) { ss_cal_CalAllDayEvent.mouseIsDown(evt, dojo.byId("ss_cal_dayGridAllDay"))});
                }
                this.dayGridDrawn = true;
            } else {
                this.drawDayHeader("ss_cal_dayGridHeader", this.gridSize, this.gridOffset);
                this.drawDayGrid("ss_cal_dayGridAllDay", this.dayGridDrawn, this.gridSize, this.gridOffset, "ss_cal_allDay",   1); 
                this.drawDayGrid("ss_cal_dayGridHour",   this.dayGridDrawn, this.gridSize, this.gridOffset, "ss_cal_hourGrid", 1); 
            }
        } else if (this.currentType == "month") {
            dojo.html.hide(dojo.byId("ss_cal_DayGridMaster"));
            dojo.html.show(dojo.byId("ss_cal_MonthGridMaster"));
            if (!this.monthGridDrawn) {
                this.drawMonthGrid(true, ss_cal_CalData.todayIndex, ss_cal_CalData.monthTickList);
                this.monthGridDrawn = true;
            } else {
                this.drawMonthGrid(false, ss_cal_CalData.todayIndex, ss_cal_CalData.monthTickList);
            }
        }
    },

    drawDayGrid: function(containerId, dayGridDrawn, days, gridOffset, ruleId, justVertical) {
        var container = dojo.byId(containerId);
        var dayOffset = 0;
        var hourOffset = 0;
        var dayOffsetSize = (1.0 / days) * 100.0;
        var toDestroy = new Array();
        if (!dayGridDrawn) {
            var todayMarker = document.createElement("div");
            todayMarker.className = "ss_cal_todayMarker";
            todayMarker.setAttribute("id", containerId + "_Today");
            todayMarker.style.height =  "100%";
            todayMarker.style.display = "none";
            container.appendChild(todayMarker);
        }

        var today = dojo.byId(containerId + "_Today")
        if ((ss_cal_CalData.todayIndex - gridOffset) < 0 || (ss_cal_CalData.todayIndex - gridOffset) >= days) {
            dojo.html.hide(today);
        } else {
            today.style.width = dayOffsetSize + "%";
            today.style.left = ((ss_cal_CalData.todayIndex - gridOffset) * dayOffsetSize) + "%";
            dojo.html.show(today);
        }

        var v = dojo.dom.getFirstChildElement(container);
        while (v) {
            if (v.className == "ss_cal_dayRule") { toDestroy.push(v); };
            v = dojo.dom.getNextSiblingElement(v);
        }
        while (toDestroy.length) { dojo.dom.removeNode(toDestroy.pop()); }

        for (var x = 0; x < ss_cal_Grid.gridSize; x++) {
            var vrule = document.createElement("div");
            vrule.setAttribute("id", ruleId + x);
            vrule.className = "ss_cal_dayRule";
            vrule.style.left = dayOffset + "%";
            container.appendChild(vrule);
            dayOffset += dayOffsetSize;
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


    drawDayHeader: function(containerId, days, gridOffset) {
        var container = dojo.byId(containerId);
        var dayOffset = 0;
        var hourOffset = 0;
        var dayOffsetSize = (1.0 / days) * 100.0;

        var toDestroy = new Array();
        var v = dojo.dom.getFirstChildElement(container);
        while (v) {
            if (dojo.html.hasClass(v, "ss_cal_gridHeaderText")) { toDestroy.push(v); };
            v = dojo.dom.getNextSiblingElement(v);
        }
        while (toDestroy.length) { dojo.dom.removeNode(toDestroy.pop()); }

        for (var x = 0; x < days; x++) {
            var badge = document.createElement("div");
            badge.className = "ss_cal_gridHeaderText";
            if ((x+gridOffset) == ss_cal_CalData.todayIndex) {
                badge.className += " ss_cal_gridHeaderTextToday";
            }
            badge.style.left = dayOffset + "%";
            badge.style.width = dayOffsetSize + "%";
            badge.innerHTML = '<a href="javascript: ;" onClick="ss_cal_Events.switchDayView(' + "'daydelta', " + x + ')">' + 
                   ss_cal_CalData.dayHeader(x+gridOffset) + '</a>';
            container.appendChild(badge);
            dayOffset += dayOffsetSize;
        }

    },

    drawMonthGrid: function(firstTime, todayIndex, ticks) {
        var container = dojo.byId("ss_cal_monthGrid");
        var vOffset = 0;
        var hOffset = 0;
        var vOffsetSize = (1.0 / 7) * 100.0;
        var hOffsetSize = (1.0 / 5) * 100.0;
        var t = 0;
        var header = dojo.byId("ss_cal_monthGridHeader");

        if (firstTime) {
            var todayMarker = document.createElement("div");
            todayMarker.className = "ss_cal_todayMarker";
            todayMarker.setAttribute("id", "ss_cal_monthGridToday");
            todayMarker.style.width = vOffsetSize + "%";
            todayMarker.style.height = hOffsetSize + "%";
            todayMarker.style.display = "none";
            container.appendChild(todayMarker);

            for (var x = 0; x < 6; x++) {
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
                var badgeText = document.createTextNode(ss_cal_CalData.dayNamesShort[x]);
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

        }

        var today = dojo.byId("ss_cal_monthGridToday");
        if (todayIndex > 0) {
            today.style.left = (vOffsetSize * (todayIndex % 7)) + "%";
            today.style.top = (hOffsetSize * Math.floor(todayIndex / 7)) + "%";
            dojo.html.show(today);
        } else {
            dojo.html.hide(today);
        }

        var toDestroy = new Array();
        var v = dojo.dom.getFirstChildElement(container);
        while (v) {
            if (v.className == "ss_cal_monthGridDayBadge") { toDestroy.push(v); };
            v = dojo.dom.getNextSiblingElement(v);
        }
        while (toDestroy.length) { dojo.dom.removeNode(toDestroy.pop()); }


        for (var x = 0; x < ticks.length; x++) {// 35 is not enougth: 1 Sep 2007 is last day of week, so there are: 6 + 30 + 6 = 42 days
            var d = (x % 7);
            var w = Math.floor(x / 7);
            var badge = document.createElement("div");
            badge.className = "ss_cal_monthGridDayBadge";
            if (x == todayIndex) {
                badge.className += " ss_cal_monthGridDayBadgeToday";
            }
            badge.style.left = (d * vOffsetSize) + "%";
            badge.style.top = (w * hOffsetSize) + "%";
            badge.innerHTML = '<a href="javascript: ;" onClick="ss_cal_Events.switchDayView(' + "'daydirect', " + x + ')">' + 
                   ticks[t++] + '</a>';
            container.appendChild(badge);
        }

    },

    drawHourMarkers: function(containerId, ticks) {
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
        this.currDay = dayOffset;
        hourOffset = this.recordHourOffset(this.currDay);
        this.currDispId = ss_cal_drawCalendarEvent(grid.id, ss_cal_Grid.gridSize, 1, 0, dayOffset, hourOffset, -1, "All day", "", "#CCCCCC", "#CCCCCC", "");
        this.resetGridHeight();
        dojo.event.connect(dojo.body(), "onmouseup", this, "mouseIsUp");
        this.currEventData = new Object();
        this.currEventData.day = dayOffset;
        this.currEventData.start = -1;
        this.currEventData.dur = 0;
    },

    recordHourOffset: function(day) {
        return (this.recordEvent(day, 1) - 1) * 0.5;
    },

    recordEvent: function(day, incr) {
        if (typeof this.allDayCount[day] == "undefined") { this.allDayCount[day] = 0; }
        this.allDayCount[day] += incr;
        return this.allDayCount[day];
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
        this.recordEvent(this.currDay, -1);
        this.resetGridHeight()
    },


    saveCurrentEvent: function() {
        this.deleteEvent(this.currDispId);
        this.currEventData.title = "New" + this.currDispId;
        this.currEventData.text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam viverra pretium nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vivamus lorem tortor, commodo vel, malesuada nec, sodales ut, ante. Duis ut leo non nisi ultricies ultrices. Vivamus vitae turpis sed justo dignissim porttitor.";
        this.currEventData.calsrc = "cal1";
        this.currEventData.eventId = "GAD" + this.currDispId;
        this.currEventData.day += ss_cal_Grid.gridOffset;
        ss_cal_Events.set([this.currEventData]);
        ss_cal_Events.redrawAll();
    },


    reset: function() {
        while (this.allDayCount.length) { this.allDayCount.pop() };
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
        dayOffset = Math.floor((gridX / gridWidth)  / (1.0 / ss_cal_Grid.gridSize));
        
        this.currDispId = ss_cal_drawCalendarEvent(grid.id, ss_cal_Grid.gridSize, 1, 0, dayOffset, hourOffset, 30, "", "", "#CCCCCC", "#CCCCCC", "");
        evt.cancelBubble = true;
        this.currEventData = new Object();
        this.currEventData.day = dayOffset;
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
    var lb = ss_showLightbox();
    ss_showDiv("infoBox");
    ss_cal_centerDivInViewport("infoBox");
    var ibox = dojo.byId("infoBox");
    dojo.html.setOpacity(ibox,0);
    dojo.lfx.html.fadeIn(ibox, 500).play();
    ss_ActiveGrid = gridControl;
    evt.cancelBubble = true;
}

function ss_cal_eventInfo(evt, eventId) {
    evt = (evt) ? evt : ((event) ? event : null);
    dojo.byId("ib2eid").innerHTML = eventId;
    var e = ss_cal_Events.eventData[eventId];
	dojo.byId("ib2view").innerHTML = '<a href="' + e.viewHref + '" onClick="ss_cancelPopupDiv(' +
	              "'infoBox2');" + e.viewOnClick + '">' + e.title + '</a>';
    ss_cal_Events.cancelHover(false);
    var lb = ss_showLightbox();
    ss_showDiv("infoBox2");
    ss_cal_centerDivInViewport("infoBox2");
    var ibox = dojo.byId("infoBox2");
    dojo.html.setOpacity(ibox,0);
    dojo.lfx.html.fadeIn(ibox, 500).play();
    evt.cancelBubble = true;
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
        eHtml = ss_cal_CalData.shortTime(time);
    }
    eHtml += '<a href="#">' + title + '</a>' + "<br/>" + text;
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


function ss_cal_drawMonthEventBlock(containerId, dayNumber, eventCount, eventList) {
    var container = dojo.byId(containerId);
    var vOffsetSize = (1.0 / 7) * 100.0;
    var hOffsetSize = (1.0 / 5) * 100.0;
    var resultDisplayIds = new Array();

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
    
        ebox.style.top = ((w * hOffsetSize) + 4.5 + (i * 5.18)) + "%";
        ebox.style.width = "13.79%";

        ebox.setAttribute("id", "calevt" + ss_cal_Events.displayId);
        ebox.style.height = ((eventHeight/19.05) * 100) + "%";
        var e = ss_cal_Events.eventData[eventList[i]];
        ebox.style.backgroundColor = ss_cal_CalData.box(e.calsrc);
		ebox.innerHTML = '<a href="'+e.viewHref+'" onClick="'+e.viewOnClick+'">'+e.title+'</a>';
        container.appendChild(ebox);
        dojo.lfx.propertyAnimation(ebox, [{ property: "opacity", start: 0, end: 1 }], 200).play();
    }

    if (eventCount > 3) {
        var ebox = document.createElement("div");
        ss_cal_Events.displayId += 1;
        resultDisplayIds.push(ss_cal_Events.displayId);
        ebox.className = "ss_cal_eventBox ss_cal_monthEventBody";

        ebox.style.left = ((d * vOffsetSize) + 0.15) + "%";
    
        ebox.style.top = ((w * hOffsetSize) + 4.5 + (2 * 5.18)) + "%";
        ebox.style.width = "13.79%";

        ebox.setAttribute("id", "calevt" + ss_cal_Events.displayId);
        ebox.style.backgroundColor = "#BBBBBB";
        ebox.style.height = ((eventHeight/19.4) * 100) + "%";
        ebox.innerHTML = "... plus " + (eventCount - 2) + " other events...";

        container.appendChild(ebox);
        dojo.lfx.propertyAnimation(ebox, [{ property: "opacity", start: 0, end: 1 }], 250).play();
    }


    return resultDisplayIds;
}

//////////////////////////////////////////////////////////////////////////////////////////

var ss_cal_Events = {
    displayId: 0,
    eventData: new Array(),
    collisions: new Array(),
    collisionI: new Array(),
    collisionM: new Array(),
    order: new Array(),
    monthGridEvents: new Array(),
        
    set: function(newEvents) {
        for (var i in newEvents) {
            var nei = newEvents[i];
            // Normalize times
            if (nei.start.toString().indexOf(":") > 0) {
                var tarray = nei.start.split(":");
                start = parseFloat(tarray[0]);
                if (tarray.length > 1) { start += (parseFloat(tarray[1])/60) }
            } else {
                start = parseFloat(nei.start);
            }
            nei.start = start;
            this.eventData[nei.eventId] = nei;
        }
    },


    clear: function() {
        this.sortAndUndrawEvents();
        this.eventData = new Array();
    },

    resetCollisions: function() {
        for (var i in this.collisions) { this.collisions[i] = 0; }
        for (var i in this.collisionI) { this.collisionI[i] = 0; }
    },

    incrCollision: function(t) {
        if (typeof this.collisions[t] == "undefined") { this.collisions[t] = 0; }
        this.collisions[t]++;
        return this.collisions[t];
    },

    applyDayDisplayRules: function() {
        this.resetCollisions();
        for (var i in this.eventData) {
            var e = this.eventData[i];
            this.incrCollision(e.day + "/" + Math.floor(e.start)); 
        }
    },

    applyMonthDisplayRules: function() {
        while (this.collisionM.length) { this.collisionM.pop(); }
        for (var i in this.eventData) {
            var e = this.eventData[i];
            this.incrCollision(e.day);
            if (typeof this.collisionM[e.day] == "undefined") { this.collisionM[e.day] = new Array(); }
            this.collisionM[e.day].push(i);
        }
    },

    collisionCount: function(day, start) {
        return this.collisions[day + "/" + Math.floor(start)];
    },

    collisionIndex: function(day, start) {
        var t = day + "/" + Math.floor(start);
        if (typeof this.collisionI[t] == "undefined") { this.collisionI[t] = 0; }
        return this.collisionI[t]++;
    },

    sortAndUndrawEvents: function() {
        while (this.order.length) { this.order.pop(); }
        for (var i in this.eventData) {
            var e = this.eventData[i];
            this.order[this.order.length] = (e.day + 101) + "/" + (Math.floor(e.start * 10) + 1011) + "/" + e.eventId;
            if (typeof e.displayId != "undefined" && e.displayId != "") {
                dojo.dom.removeNode(dojo.byId("calevt" + e.displayId).parentNode);
            }
            e.displayId = "";
        }
        while (this.monthGridEvents.length) {
            dojo.dom.removeNode(dojo.byId("calevt" + this.monthGridEvents.pop()));
        }
        this.order.sort();
    },

    redrawDay: function() {
        this.applyDayDisplayRules();
        ss_cal_CalAllDayEvent.reset();
        this.sortAndUndrawEvents();
        while (this.order.length) {
            var eid = this.order.shift().substr(9);
            var e = this.eventData[eid];
            // We filter and shift the days based on the gridSize and gridOffset
            var gridDay = e.day - ss_cal_Grid.gridOffset;
            if (gridDay < 0 || gridDay >= ss_cal_Grid.gridSize) {
                continue;
            }            
            if (e.start < 0) {
                var grid = "ss_cal_dayGridAllDay";
                this.eventData[eid].displayId = ss_cal_drawCalendarEvent(grid, ss_cal_Grid.gridSize, 1, 0,
                       gridDay, ss_cal_CalAllDayEvent.recordHourOffset(e.day), -1, e.title, e.text,
                       ss_cal_CalData.box(e.calsrc), ss_cal_CalData.border(e.calsrc), eid); 
            } else {
                var grid = "ss_cal_dayGridHour";
                this.eventData[eid].displayId = ss_cal_drawCalendarEvent(grid, ss_cal_Grid.gridSize,
                       this.collisionCount(e.day, e.start),
                       this.collisionIndex(e.day, e.start),
                       gridDay, e.start, e.dur, e.title, e.text,
                       ss_cal_CalData.box(e.calsrc), ss_cal_CalData.border(e.calsrc), eid); 
            }
        }
        ss_cal_CalAllDayEvent.resetGridHeight();
    },

    redrawMonth: function() {
        this.applyMonthDisplayRules();
        this.sortAndUndrawEvents();
        for (var d in this.collisionM) {
            var grid = "ss_cal_monthGrid";
            var dids = ss_cal_drawMonthEventBlock(grid, d, this.collisionM[d].length, this.collisionM[d]);
            while (dids.length) { this.monthGridEvents.push(dids.pop()); }
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


    switchDayView: function(dayMode, tweak) {
        switch (dayMode) {
            case "daydelta":
                ss_cal_Grid.gridSize = 1;
                ss_cal_Grid.gridIncr = 1;
                ss_cal_Grid.gridOffset += tweak;
                break;
            case "daydirect":
                ss_cal_Grid.gridSize = 1;
                ss_cal_Grid.gridIncr = 1;
                ss_cal_Grid.gridOffset = tweak;
                break;
            case "3daydelta":
                ss_cal_Grid.gridSize = 3;
                ss_cal_Grid.gridIncr = 3;
                ss_cal_Grid.gridOffset += tweak;
                break;
            case "week":
                ss_cal_Grid.gridSize = 7;
                ss_cal_Grid.gridIncr = 7;
                ss_cal_Grid.gridOffset -= (ss_cal_Grid.gridOffset % 7);
                break;
            case "fortnight":
                ss_cal_Grid.gridSize = 14;
                ss_cal_Grid.gridIncr = 14;
                ss_cal_Grid.gridOffset -= (ss_cal_Grid.gridOffset % 7);
                break;
            case "workweek":
                ss_cal_Grid.gridSize = 5;
                ss_cal_Grid.gridIncr = 7;
                ss_cal_Grid.gridOffset -= (ss_cal_Grid.gridOffset % 7);
                ss_cal_Grid.gridOffset++;
                break;
        }
        ss_cal_Grid.activateGrid('day');
        this.redrawAll();
    }

};


