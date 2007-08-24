dojo.provide("sitescape.widget.DropdownTimePickerActivateByInput");

dojo.require("dojo.widget.DropdownTimePicker");

dojo.widget.defineWidget(
	"sitescape.widget.DropdownTimePickerActivateByInput",
	dojo.widget.DropdownTimePicker,
	{
		templateString: '<span style="white-space:nowrap"><input type="hidden" name="" value="" dojoAttachPoint="valueNode" /><input type="text" value="" style="vertical-align:middle;" dojoAttachPoint="inputNode" autocomplete="off" dojoAttachEvent="onclick: onIconClick"/> <img src="${this.iconURL}" alt="${this.iconAlt}" dojoAttachEvent="onclick: onIconClick" dojoAttachPoint="buttonNode" style="vertical-align:middle; cursor:pointer;" /></span>'
	}

);
