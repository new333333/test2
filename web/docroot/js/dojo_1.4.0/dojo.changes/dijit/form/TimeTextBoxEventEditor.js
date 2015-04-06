if(!dojo._hasResource["dijit.form.TimeTextBoxEventEditor"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.TimeTextBoxEventEditor"] = true;
dojo.provide("dijit.form.TimeTextBoxEventEditor");

dojo.require("dijit.form.TimeTextBox");

dojo.declare(
	"dijit.form.TimeTextBoxEventEditor",
	dijit.form.TimeTextBox,
	{
		startDateWidgetId: "",
		startTimeWidgetId: "",
		endDateWidgetId: "",
		endTimeWidgetId: "",
		
		startDateWidget: "",
		startTimeWidget: "",
		endDateWidget: "",
		endTimeWidget: "",
				
		onChange: function(/*Date*/dateObj) {
			this.getWidgets();
			try {			
				if (this.startDateWidget && this.endDateWidget && this.startTimeWidget && this.endTimeWidget &&
					this.endDateWidget.getValue() && this.startDateWidget.getValue()) {
					var diff = dojo.date.compare(this.startDateWidget.getValue(), this.endDateWidget.getValue(), "date");
					if (diff == 0) {
						var startTime = this.startTimeWidget.getValue();
						var endTime = this.endTimeWidget.getValue();
						if (dojo.date.compare(startTime, endTime, "time") > 0) {
							if (startTime.getHours() == 23 && startTime.getMinutes() >= 30) {
								this.endDateWidget.setValue(dojo.date.add(this.endDateWidget.getValue(), "day", 1));
							}
							this.endTimeWidget.setValue(dojo.date.add(this.startTimeWidget.getValue(), "minute", 30));
						}
					}
				}
			} catch (err) {
				// it's not possible to adjust dates, ignore it (probably date is empty)
			}	
		},
		
		getWidgets: function() {
			if (!this.startDateWidget || this.startDateWidget == "") {
				this.startDateWidget = dijit.byId(this.startDateWidgetId);
			}
			if (!this.startTimeWidget || this.startTimeWidget == "") {
				this.startTimeWidget = dijit.byId(this.startTimeWidgetId);
			}
			if (!this.endDateWidget || this.endDateWidget == "") {				
				this.endDateWidget = dijit.byId(this.endDateWidgetId);
			}
			if (!this.endTimeWidget || this.endTimeWidget == "") {				
				this.endTimeWidget = dijit.byId(this.endTimeWidgetId);
			}
		}
	}


);

}
