dojo.provide("sitescape.widget.DropdownDatePickerActivateByInput");

dojo.require("dojo.widget.DropdownDatePicker");

dojo.widget.defineWidget(
	"sitescape.widget.DropdownDatePickerActivateByInput",
	dojo.widget.DropdownDatePicker,
	{
		templateString: '<span style="white-space:nowrap"><input type="hidden" name="" value="" dojoAttachPoint="valueNode" /><input type="text" value="" style="vertical-align:middle;" dojoAttachPoint="inputNode" autocomplete="off" dojoAttachEvent="onclick: onIconClick"/> <img src="${this.iconURL}" alt="${this.iconAlt}" dojoAttachEvent="onclick: onIconClick" dojoAttachPoint="buttonNode" style="vertical-align:middle; cursor:pointer;" /></span>',
		
		startDateWidgetId: "",
		startTimeWidgetId: "",
		endDateWidgetId: "",
		endTimeWidgetId: "",
	
		fillInTemplate: function(args, frag){
			// summary: see dojo.widget.DomWidget
			dojo.widget.DropdownDatePicker.superclass.fillInTemplate.call(this, args, frag);
			//attributes to be passed on to DatePicker
			var dpArgs = {widgetContainerId: this.widgetId, lang: this.lang, value: this.value,
				startDate: this.startDate, endDate: this.endDate, displayWeeks: this.displayWeeks,
				weekStartsOn: this.weekStartsOn, adjustWeeks: this.adjustWeeks, staticDisplay: this.staticDisplay,
				templateCssPath: ss_urlBase + ss_rootPath + "js/sitescape/widget/templates/DatePicker.css"};
	
			//build the args for DatePicker based on the public attributes of DropdownDatePicker
			this.datePicker = dojo.widget.createWidget("DatePicker", dpArgs, this.containerNode, "child");
			dojo.event.connect(this.datePicker, "onValueChanged", this, "onSetDate");
			
			if(this.value){
				this.onSetDate();
			}
			this.containerNode.style.zIndex = this.zIndex;
			this.containerNode.explodeClassName = "calendarBodyContainer";
			this.valueNode.name=this.name;
		}
	}
	

);
