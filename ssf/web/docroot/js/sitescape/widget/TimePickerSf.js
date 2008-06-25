/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("sitescape.widget.TimePickerSf");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.date.serialize");
dojo.require("dojo.date.format");
dojo.require("dojo.dom");
dojo.require("dojo.html.style");
dojo.requireLocalization("dojo.i18n.calendar", "gregorian", null, "ko,zh-cn,zh,sv,ja,en,zh-tw,it,hu,nl,fi,zh-hk,fr,pt,ROOT,es,de,pt-br");
dojo.requireLocalization("dojo.widget", "TimePicker", null, "ROOT");
dojo.widget.defineWidget("sitescape.widget.TimePickerSf", dojo.widget.HtmlWidget, function () {
	this.time = "";
	this.useTimeProperty = false;
	this.useDefaultTime = false;
	this.useDefaultMinutes = false;
	this.storedTime = "";
	this.hours12 = false;
	this.currentTime = {};
	this.classNames = {selectedTime:"selectedItem", highlightedTime:"highlightedItem"};
	this.any = "any";
	this.selectedTime = {hour:"", minute:"", amPm:"", anyTime:false};
	this.hourIndexMap = ["", 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, 11, 0];
	this.minuteIndexMap = [0, 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, 11];
}, {isContainer:false, templateString: "<div class=\"timePickerContainer\" dojoAttachPoint=\"timePickerContainerNode\">" +
	"<div dojoAttachPoint=\"hourContainerNode\" dojoAttachEvent=\"onClick: onSetSelectedTime;\">" +
		"<div name=\"time-00-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">0:00</div>" +
		"<div name=\"time-00-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">0:30</div>" +
		"<div name=\"time-01-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">1:00</div>" +
		"<div name=\"time-01-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">1:30</div>" +
		"<div name=\"time-02-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">2:00</div>" +
		"<div name=\"time-02-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">2:30</div>" +
		"<div name=\"time-03-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">3:00</div>" +
		"<div name=\"time-03-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">3:30</div>" +
		"<div name=\"time-04-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">4:00</div>" +
		"<div name=\"time-04-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">4:30</div>" +
		"<div name=\"time-05-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">5:00</div>" +
		"<div name=\"time-05-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">5:30</div>" +
		"<div name=\"time-06-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">6:00</div>" +
		"<div name=\"time-06-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">6:30</div>" +
		"<div name=\"time-07-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">7:00</div>" +
		"<div name=\"time-07-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">7:30</div>" +
		"<div name=\"time-08-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">8:00</div>" +
		"<div name=\"time-08-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">8:30</div>" +
		"<div name=\"time-09-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">9:00</div>" +
		"<div name=\"time-09-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">9:30</div>" +
		"<div name=\"time-10-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">10:00</div>" +
		"<div name=\"time-10-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">10:30</div>" +
		"<div name=\"time-11-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">11:00</div>" +
		"<div name=\"time-11-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">11:30</div>" +
		"<div name=\"time-12-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">12:00</div>" +
		"<div name=\"time-12-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">12:30</div>" +
		"<div name=\"time-13-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">13:00</div>" +
		"<div name=\"time-13-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">13:30</div>" +
		"<div name=\"time-14-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">14:00</div>" +
		"<div name=\"time-14-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">14:30</div>" +
		"<div name=\"time-15-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">15:00</div>" +
		"<div name=\"time-15-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">15:30</div>" +
		"<div name=\"time-16-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">16:00</div>" +
		"<div name=\"time-16-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">16:30</div>" +
		"<div name=\"time-17-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">17:00</div>" +
		"<div name=\"time-17-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">17:30</div>" +
		"<div name=\"time-18-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">18:00</div>" +
		"<div name=\"time-18-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">18:30</div>" +
		"<div name=\"time-19-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">19:00</div>" +
		"<div name=\"time-19-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">19:30</div>" +
		"<div name=\"time-20-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">20:00</div>" +
		"<div name=\"time-20-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">20:30</div>" +
		"<div name=\"time-21-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">21:00</div>" +
		"<div name=\"time-21-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">21:30</div>" +
		"<div name=\"time-22-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">22:00</div>" +
		"<div name=\"time-22-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">22:30</div>" +
		"<div name=\"time-23-00\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">23:00</div>" +
		"<div name=\"time-23-30\" dojoAttachEvent=\"onMouseOver: onOverHour; onMouseOut: onOutHour;\">23:30</div>" +
	"</div>" +
"</div>"
, 

	templateCssString: "/*Time Picker */" +
".timePickerContainer {" +
"    font-size:16px;" +
"    height:97px;" +
"    overflow-x:auto;" +
"    overflow-y:auto;" +
"    position:absolute;" +
"    width:90px;" +
"    background-color: #FFFFFF;" +
"    z-index:2;" +
"    color:#333333;" +
"    font-size:0.7em;" +
"    border: 1px solid #333333" + 
"}" +
".selectedItem, .highlightedItem {" +
"    background-color: #333333 !important;" +
"    color:#FFFFFF !important;" +
"}" +
".timePickerContainer div div {"+ 
"    cursor:pointer;" +
"    cursor:hand;" +
"    padding-left: 2px;" +
"}"
, 
	templateCssPath:dojo.uri.dojoUri("src/widget/templates/DatePicker.css"), postMixInProperties:function (localProperties, frag) {
	sitescape.widget.TimePickerSf.superclass.postMixInProperties.apply(this, arguments);
	this.calendar = dojo.i18n.getLocalization("dojo.i18n.calendar", "gregorian", this.lang);
	this.widgetStrings = dojo.i18n.getLocalization("dojo.widget", "TimePicker", this.lang);
}, fillInTemplate:function (args, frag) {
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
	if (args.value) {
		if (args.value instanceof Date) {
			this.storedTime = dojo.date.toRfc3339(args.value);
		} else {
			this.storedTime = args.value;
		}
	}
	this.initData();
	this.initUI();
	this.useTimeProperty = false;
}, initData:function () {
	if (this.storedTime.indexOf("T") != -1 && this.storedTime.split("T")[1] && this.storedTime != " " && this.storedTime.split("T")[1] != "any") {
		this.time = sitescape.widget.TimePickerSf.util.fromRfcDateTime(this.storedTime, this.useDefaultMinutes, this.selectedTime.anyTime);
	} else {
		if (this.useDefaultTime) {
			this.time = sitescape.widget.TimePickerSf.util.fromRfcDateTime("", this.useDefaultMinutes, this.selectedTime.anyTime);
		} else {
			this.selectedTime.anyTime = true;
			this.time = sitescape.widget.TimePickerSf.util.fromRfcDateTime("", 0, 1);
		}
	}
	this.useTimeProperty = true;
}, initUI:function () {
	var tmpDate = new Date();
	tmpDate.setHours(0);
	tmpDate.setMinutes(0);
	var hourNodes = this.hourContainerNode.getElementsByTagName("div");
	for (var i = 0; i < hourNodes.length; i++) {
		hourNodes.item(i).innerHTML = dojo.date.format(tmpDate, {formatLength:"short", timePattern:"", selector:"timeOnly", locale:this.lang});
		tmpDate.setMinutes(tmpDate.getMinutes() + 30);
	}

	if (!this.selectedTime.anyTime && this.time) {
 		this.onClearSelectedHour(); 
		this.setSelectedTime(this.time.getHours() * 2 + (this.time.getMinutes() >= 30 ? 1 : 0)); 

 		this.selectedTime.anyTime = false; 

 		this.onSetTime(); 
	} else {
		this.onSetSelectedAnyTime();
	}
}, setTime:function (date) {
	if (date) {
		this.selectedTime.anyTime = false;
		this.setDateTime(dojo.date.toRfc3339(date));
	} else {
		this.selectedTime.anyTime = true;
	}
	this.initData();
	this.initUI();
	this.useTimeProperty = false;
}, setDateTime:function (rfcDate) {
	this.storedTime = rfcDate;
}, onClearSelectedHour:function (evt) {
	this.clearSelectedHour();
}, clearSelectedHour:function () {
	var hourNodes = this.hourContainerNode.getElementsByTagName("div");
	for (var i = 0; i < hourNodes.length; i++) {
		dojo.html.removeClass(hourNodes.item(i), this.classNames.selectedTime);
	}
}, onSetSelectedTime:function (evt) {
	this.onClearSelectedHour();
	this.setSelectedTime(evt);
	this.onSetTime();
	this.useTimeProperty = false;
}, onOverHour:function (evt) {
	if (evt && evt.target) {
		if (evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
			var eventTarget = evt.target;
		} else {
			var eventTarget = evt.target.parentNode;
		}
		dojo.event.browser.stopEvent(evt);
		dojo.html.addClass(eventTarget, this.classNames.highlightedTime);
	}
}, onOutHour:function (evt) {
	if (evt && evt.target) {
		if (evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
			var eventTarget = evt.target;
		} else {
			var eventTarget = evt.target.parentNode;
		}
		dojo.event.browser.stopEvent(evt);
		dojo.html.removeClass(eventTarget, this.classNames.highlightedTime);
	}	
}, setSelectedTime:function (evt) {
	var hours = null;
	var minutes = null;
	if (!this.useTimeProperty) {
		var target = null;
		if (evt && evt.target) {
			if (evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
				target = evt.target;
			} else {
				target = evt.target.parentNode;
			}
			dojo.event.browser.stopEvent(evt);
		} else {
			if (!isNaN(evt)) {
				var hourNodes = this.hourContainerNode.getElementsByTagName("div");
				if (hourNodes.item(evt)) {
					target = hourNodes.item(evt);
				}
			}
		}
	
		dojo.html.addClass(target, this.classNames.selectedTime);
		var targetName = target.getAttribute("name");
		if (targetName != null) {
			hours = 1 * targetName.substr(5, 2);
			minutes = 1 * targetName.substr(8, 2);	
		}			
	} else {
		hours = this.time.getHours();
		minutes = this.time.getMinutes();		
		this.useTimeProperty = false;
		var hourNodes = this.hourContainerNode.getElementsByTagName("div");
		var index = hours * 2 + (minutes >= 30 ? 1 : 0);
		if (hourNodes.item(index)) {
			target = hourNodes.item(index);
			dojo.html.addClass(target, this.classNames.selectedTime);
		}		
	}
	this.selectedTime["hour"] = hours;
	this.selectedTime["minute"] = minutes;
	if (hours < 12) {
		this.selectedTime.amPm = "am";
	} else {
		this.selectedTime.amPm = "pm";
	}
	this.scrollContainer();
	this.selectedTime.anyTime = false;
}, scrollContainer:function() {
	var scroll = (14 * this.selectedTime["hour"] * 2) + (this.selectedTime["minute"] == 30?14:0) - 14;
	this.timePickerContainerNode.scrollTop = scroll;
}, onClick:function (evt) {
	dojo.event.browser.stopEvent(evt);
}, onSetTime:function () {
	if (this.selectedTime.anyTime) {
		this.time = new Date();
		this.useTimeProperty = true;
		var tempDateTime = sitescape.widget.TimePickerSf.util.toRfcDateTime(this.time);
		this.setDateTime(tempDateTime.split("T")[0]);
	} else {
		var hour = 12;
		var minute = 0;
		var isAm = false;
		if (this.selectedTime["hour"]) {
			hour = parseInt(this.selectedTime["hour"], 10);
		}
		if (this.selectedTime["minute"]) {
			minute = parseInt(this.selectedTime["minute"], 10);
		}
		if (this.selectedTime["amPm"]) {
			isAm = (this.selectedTime["amPm"].toLowerCase() == "am");
		}
		this.time = new Date();
		this.time.setHours(sitescape.widget.TimePickerSf.util.fromAmPmHour(hour, isAm));
		this.time.setMinutes(minute);
		this.useTimeProperty = true;
		this.setDateTime(sitescape.widget.TimePickerSf.util.toRfcDateTime(this.time));
	}
	this.onValueChanged(this.time);
}, onValueChanged:function (date) {
}});
sitescape.widget.TimePickerSf.util = new function () {
	this.toRfcDateTime = function (jsDate) {
		if (!jsDate) {
			jsDate = new Date();
		}
		jsDate.setSeconds(0);
		return dojo.date.strftime(jsDate, "%Y-%m-%dT%H:%M:00%z");
	};
	this.fromRfcDateTime = function (rfcDate, useDefaultMinutes, isAnyTime) {
		var tempDate = new Date();
		if (!rfcDate || rfcDate.indexOf("T") == -1) {
			if (useDefaultMinutes) {
				tempDate.setMinutes(Math.floor(tempDate.getMinutes() / 5) * 5);
			} else {
				tempDate.setMinutes(0);
			}
		} else {
			var tempTime = rfcDate.split("T")[1].split(":");
			var tempDate = new Date();
			tempDate.setHours(tempTime[0]);
			tempDate.setMinutes(tempTime[1]);
		}
		return tempDate;
	};
	this.toAmPmHour = function (hour) {
		var amPmHour = hour;
		var isAm = true;
		if (amPmHour == 0) {
			amPmHour = 12;
		} else {
			if (amPmHour > 12) {
				amPmHour = amPmHour - 12;
				isAm = false;
			} else {
				if (amPmHour == 12) {
					isAm = false;
				}
			}
		}
		return [amPmHour, isAm];
	};
	this.fromAmPmHour = function (amPmHour, isAm) {
		var hour = parseInt(amPmHour, 10);
		if (isAm && hour == 12) {
			hour = 0;
		} else {
			if (!isAm && hour < 12) {
				hour = hour + 12;
			}
		}
		return hour;
	};
};

