/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
dojo.provide("sitescape.widget.WorkflowSelect");

dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");

dojo.widget.defineWidget(
	"sitescape.widget.WorkflowSelect",
	dojo.widget.Select,
	{
		nestedUrl : '',
		stepsWidget : null,
		searchFieldName: "",
		widgetStepsRef : null,
		selectOption : function(/*Event*/ evt){
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			sitescape.widget.WorkflowSelect.superclass.selectOption.call(this, evt);
			var id = this.stepsWidget.id+this.selectedResult[1];
			var stepsProp = {dataUrl:this.nestedUrl+"&workflowId="+this.selectedResult[1], id:id, name:this.searchFieldName};
			this.widgetStepsRef = dojo.widget.createWidget("Select", stepsProp, this.stepsWidget, "last");
		},
		setDefaultValue: function(wfId, wfLabel, stepId, stepLabel) {
			this.setLabel(wfLabel);
			this.setValue(wfId);
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			
			if (!stepId || stepId == "") {
				return;
			}
			
			var id = this.stepsWidget.id+wfId;
			var stepsProp = {dataUrl:this.nestedUrl+"&workflowId="+wfId, id:id, name:this.searchFieldName};
			this.widgetStepsRef = dojo.widget.createWidget("Select", stepsProp, this.stepsWidget, "last");
			this.widgetStepsRef.setValue(stepId);
			this.widgetStepsRef.setLabel(stepLabel);
		}
	}
);
