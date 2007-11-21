/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
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
			try {
				var diff;
				if (this.startDateWidget && this.endDateWidget) {
					diff = dojo.date.compare(this.startDateWidget.getDate(), this.endDateWidget.getDate(), dojo.date.compareTypes.DATE);
					if (diff > 0) {
						if (this.widgetId == this.startDateWidgetId) {
							this.endDateWidget.setValue(this.startDateWidget.getValue());
						} else {
							this.startDateWidget.setValue(this.endDateWidget.getValue());
						}
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
			} catch (e) {
				// it's notpossible to adjust dates, ignore it (probably date is empty)
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
