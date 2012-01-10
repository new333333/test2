if(!dojo._hasResource["dijit.form.DateTextBoxEventEditor"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.DateTextBoxEventEditor"] = true;
dojo.provide("dijit.form.DateTextBoxEventEditor");

dojo.require("dijit.form.DateTextBox");

dojo.declare(
	"dijit.form.DateTextBoxEventEditor",
	dijit.form.DateTextBox,
	{
		widgetId: "",
		startDateWidgetId: "",
		startTimeWidgetId: "",
		endDateWidgetId: "",
		endTimeWidgetId: "",

		widget: "",
		startDateWidget: "",
		startTimeWidget: "",
		endDateWidget: "",
		endTimeWidget: "",
		
		onChange: function(/*Date*/dateObj) {
			this.getWidgets();
			try {
				var diff;
				if (this.startDateWidget && this.endDateWidget) {
					diff = dojo.date.compare(this.startDateWidget.getValue(), this.endDateWidget.getValue(), "date");
					if (diff > 0) {
						if (this.widgetId == this.startDateWidgetId) {
							this.endDateWidget.setValue(this.startDateWidget.getValue());
						} else {
							this.startDateWidget.setValue(this.endDateWidget.getValue());
						}
					}				
				}
				
				if (this.startDateWidget && this.endDateWidget) {
					diff = dojo.date.compare(this.startDateWidget.getValue(), this.endDateWidget.getValue(), "date");
					if (this.startTimeWidget && this.endTimeWidget && diff == 0) {
						if (dojo.date.compare(this.startTimeWidget.getValue(), this.endTimeWidget.getValue(), "time") > 0) {
							this.endTimeWidget.setValue(dojo.date.add(this.startTimeWidget.getValue(), "minute", 30));
						}
					}
				}
			} catch (err) {
				// it's notpossible to adjust dates, ignore it (probably date is empty)
			}		
		},
		
		getWidgets: function() {
			if (!this.widget || this.widget == "") {
				this.widget = dijit.byId(this.widgetId);
			}
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
