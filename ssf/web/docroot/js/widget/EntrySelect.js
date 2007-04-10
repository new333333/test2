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
dojo.provide("ss_widget.EntrySelect");

dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");

dojo.widget.defineWidget(
	"ss_widget.EntrySelect",
	dojo.widget.Select,
	{
		nestedUrl : '',
		widgetContainer : null,
		searchFieldIndex: '',
		widgetStepsRef : null,
		selectOption : function(/*Event*/ evt){
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			ss_widget.EntrySelect.superclass.selectOption.call(this, evt);
			var id = this.widgetContainer.id+this.selectedResult[1];
			var stepsProp = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.selectedResult[1], id:id, name:"elementName"+this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, nestedUrl:this.nestedUrl, entryTypeId:this.selectedResult[1], widgetContainer:this.widgetContainer};
			this.widgetStepsRef = dojo.widget.createWidget("FieldSelect", stepsProp, this.widgetContainer, "last");
		},
		setDefaultValue: function(entryId, entryLabel, fieldId, fieldLabel, userValues, fieldType) {
			this.setValue(entryId);
			this.setLabel(entryLabel);
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			var id = this.widgetContainer.id+entryId;
			var stepsProp = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+entryId, id:id, name:"elementName"+this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, nestedUrl:this.nestedUrl, entryTypeId:entryId, widgetContainer:this.widgetContainer};
			this.widgetStepsRef = dojo.widget.createWidget("FieldSelect", stepsProp, this.widgetContainer, "last");
			this.widgetStepsRef.setDefaultValues(fieldId, fieldLabel, userValues, fieldType);
		}
	}
);
