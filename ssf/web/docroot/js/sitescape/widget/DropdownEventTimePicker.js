dojo.provide("sitescape.widget.DropdownEventTimePicker");

dojo.require("sitescape.widget.DropdownTimePickerActivateByInput");

dojo.widget.defineWidget(
	"sitescape.widget.DropdownEventTimePicker",
	sitescape.widget.DropdownTimePickerActivateByInput,
	{
		startDateWidgetId: "",
		startTimeWidgetId: "",
		endDateWidgetId: "",
		endTimeWidgetId: "",
		
		startDateWidget: "",
		startTimeWidget: "",
		endDateWidget: "",
		endTimeWidget: "",
				
		onValueChanged: function(/*Date*/dateObj) {
			this.getWidgets();
			try {			
				if (this.startDateWidget && this.endDateWidget && this.startTimeWidget && this.endTimeWidget &&
					this.endDateWidget.getDate()) {
					var diff = dojo.date.compare(this.startDateWidget.getDate(), this.endDateWidget.getDate(), dojo.date.compareTypes.DATE);
					if (diff == 0) {
						if (dojo.date.compare(dojo.widget.TimePicker.util.fromRfcDateTime(this.startTimeWidget.getTime()), dojo.widget.TimePicker.util.fromRfcDateTime(this.endTimeWidget.getTime()), dojo.date.compareTypes.TIME) > 0) {
							this.endTimeWidget.setTime(dojo.date.add(dojo.widget.TimePicker.util.fromRfcDateTime(this.startTimeWidget.getTime()), dojo.date.dateParts.MINUTE, 30));
						}
					}
				}
			} catch (e) {
				// it's not possible to adjust dates, ignore it (probably date is empty)
			}	
		},
		
		getWidgets: function() {
			if (!this.startDateWidget || this.startDateWidget == "") {
				this.startDateWidget = dojo.widget.manager.getWidgetById(this.startDateWidgetId);
			}
			if (!this.startTimeWidget || this.startTimeWidget == "") {
				this.startTimeWidget = dojo.widget.manager.getWidgetById(this.startTimeWidgetId);
			}
			if (!this.endDateWidget || this.endDateWidget == "") {				
				this.endDateWidget = dojo.widget.manager.getWidgetById(this.endDateWidgetId);
			}
			if (!this.endTimeWidget || this.endTimeWidget == "") {				
				this.endTimeWidget = dojo.widget.manager.getWidgetById(this.endTimeWidgetId);
			}
		}
	}

);
