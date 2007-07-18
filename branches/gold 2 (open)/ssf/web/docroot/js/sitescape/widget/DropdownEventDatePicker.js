dojo.provide("sitescape.widget.DropdownEventDatePicker");

dojo.require("sitescape.widget.DropdownDatePickerActivateByInput");

dojo.widget.defineWidget(
	"sitescape.widget.DropdownEventDatePicker",
	sitescape.widget.DropdownDatePickerActivateByInput,
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

			var diff;
			if (this.startDateWidget && this.endDateWidget) {
				diff = dojo.date.compare(this.startDateWidget.getDate(), this.endDateWidget.getDate(), dojo.date.compareTypes.DATE);
				if (diff > 0) {
					this.endDateWidget.setValue(this.startDateWidget.getValue());
				}				
			}
			
			if (this.startDateWidget && this.endDateWidget) {
				diff = dojo.date.compare(this.startDateWidget.getDate(), this.endDateWidget.getDate(), dojo.date.compareTypes.DATE);
				if (this.startTimeWidget && this.endTimeWidget && diff == 0) {
					if (dojo.date.compare(dojo.widget.TimePicker.util.fromRfcDateTime(this.startTimeWidget.getTime()), dojo.widget.TimePicker.util.fromRfcDateTime(this.endTimeWidget.getTime()), dojo.date.compareTypes.TIME) > 0) {
						this.endTimeWidget.setTime(dojo.date.add(dojo.widget.TimePicker.util.fromRfcDateTime(this.startTimeWidget.getTime()), dojo.date.dateParts.MINUTE, 30));
					}
				}
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
