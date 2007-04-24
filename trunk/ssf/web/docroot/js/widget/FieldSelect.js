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
dojo.provide("ss_widget.FieldSelect");

dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");


dojo.declare(
  "ss_widget.FieldSelectDataProvider",
  dojo.widget.ComboBoxDataProvider,  
  function(/*Array*/ dataPairs, /*Number*/ limit, /*Number*/ timeout){
	this.data = [];
	this.dataType = [];

	this.getData = function(/*String*/ url){
		url += "\&randomNumber="+ss_random++;
		dojo.io.bind({
			url: url,
			load: dojo.lang.hitch(this, function(type, data, evt){ 
				var arrDataType = [];
				var arrData = [];
				for(i=0; i<data.length; i++){
					arrData.push([data[i][1], data[i][0]]);
					arrDataType[data[i][0]]=data[i][2];
				}
				this.setData(arrData);
				this.setDataType(arrDataType);				
			}),
			mimetype: "text/json"
		});
	};

	this.getDataType = function(){
		return this.dataType;
	};

	this.setDataType = function(/*Array*/ pdata){
		// populate this.data and initialize lookup structures
		this.dataType = pdata;
	};

  }
);


dojo.widget.defineWidget(
	"ss_widget.FieldSelect",
	dojo.widget.Select,
	{
		nestedUrl : '',
		entryTypeId:'',
		nodeObj : null,
		searchFieldIndex: "",
		nextNodeRef: null,
		widgetContainer: null,
		dataProviderClass: "ss_widget.FieldSelectDataProvider",		
		removeKids: function () {
			if (this.nextNodeRef != null) 
				try {
					this.nextNodeRef.destroy();
				} catch(e) {
					this.nodeObj.removeChild(this.nextNodeRef);
				}
		},
		destroy: function() {
			this.removeKids();
			return dojo.widget.HtmlWidget.prototype.destroy.apply(this, arguments);
		},
		addKids: function() {
			switch (this.dataProvider.dataType[this.selectedResult[1]]) {
				case "date":
					this.addDateField('');
					break;
				case "event":
					this.addEventField('');
					break;
				case "user_list":
					this.addUserListField();
					break;
				case "checkbox":
					this.addCheckboxField();
					break;
				case "radio":
					this.addRadioField();
					break;
				case "selectbox":
					this.addSelectBoxField();
					break;
				default: /* input for title, text, description, attachFiles, textarea... */
					this.addSimpleInputField();
			}
		},
		initializeKids: function(fieldType, userValue, userValueLabel) {
			switch (fieldType) {
				case "date":
					this.addDateField(userValue);
					break;
				case "event":
					this.addEventField(userValue);
					break;
				case "user_list":
					this.addUserListField(userValue, userValueLabel);
					break;
				case "checkbox":
					this.addCheckboxField(userValue, userValueLabel);
					break;
				case "radio":
					this.addRadioField(userValue, userValueLabel);
					break;					
				case "selectbox":
					this.addSelectBoxField(userValue, userValueLabel);
					break;
				default: /* input for title, text, description, attachFiles, textarea... */
					this.addSimpleInputField(userValue);
			}
		},
		fillInTemplate: function(/*Object*/ args, /*Object*/ frag) {
			ss_widget.FieldSelect.superclass.fillInTemplate.call(this, args, frag);
			this.setValue("");
		},
		selectOption : function(/*Event*/ evt){
			this.removeKids();
			ss_widget.FieldSelect.superclass.selectOption.call(this, evt);
			this.addKids();
		},
		setDefaultValues: function(fieldId, fieldLabel, userValue, type, userValueLabel){
			this.setValue(fieldId);
			this.setLabel(fieldLabel);
			this.initializeKids(type, userValue, userValueLabel);
		},
		
		addDateField: function(value) {
			var prop = {value: value, lang: ss_user_locale, id: "elementValue" + this.searchFieldIndex, 
						name: "elementValue" + this.searchFieldIndex, searchFieldIndex: this.searchFieldIndex, 
						autoComplete: false, nodeObj: this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("DropDownDatePicker", prop, this.widgetContainer, "last");
		},
		
		addEventField: function(value) {
			var prop = {value: value, lang: ss_user_locale, id: "elementValue" + this.searchFieldIndex, 
						name: "elementValue" + this.searchFieldIndex, searchFieldIndex: this.searchFieldIndex, 
						autoComplete: false, nodeObj: this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("DropDownDatePicker", prop, this.widgetContainer, "last");
		},
		
		addUserListField: function(value, label) {
			var url = ss_AjaxBaseUrl + "&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}";
			var prop = {dataUrl:url, 
						id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, maxListLength : 12, autoComplete: false};
			this.nextNodeRef = dojo.widget.createWidget("SelectPagable", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}
		},

		addCheckboxField: function(value, label) {
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+"checkbox", id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}			
		},
		
		addRadioField: function(value, label) {
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+"radio", id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}
		},
		
		addSelectBoxField: function(value, label) {
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+"selectbox", id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}
		},
		
		addSimpleInputField: function(value) {
			this.nextNodeRef = document.createElement('input');
			this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
			this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
			if (value) {
				this.nextNodeRef.value = value;
			}
			this.nodeObj.appendChild(this.nextNodeRef);
		}
				
	}
);

