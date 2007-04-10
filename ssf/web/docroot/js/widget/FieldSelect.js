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
					this.nextNodeRef = document.createElement('select');
					this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
					this.nodeObj.appendChild(this.nextNodeRef);
					break;
				case "event":
					this.nextNodeRef = document.createElement('select');
					this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
					this.nodeObj.appendChild(this.nextNodeRef);
					break;
				case "radio":
					// TODO checkboxes
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					break;
				case "user_list":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					break;
				case "checkbox":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					break;
				case "selectbox":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					break;
				case "radio":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					break;
				default: /* input for title, text, description, attachFiles, textarea... */
					this.nextNodeRef = document.createElement('input');
					this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
					this.nodeObj.appendChild(this.nextNodeRef);
			}
		},
		initializeKids: function(fieldType, userValue) {
			switch (fieldType) {
				case "date":
					this.nextNodeRef = document.createElement('select');
					this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
					this.nodeObj.appendChild(this.nextNodeRef);
					break;
				case "event":
					this.nextNodeRef = document.createElement('select');
					this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
					this.nodeObj.appendChild(this.nextNodeRef);
					break;
				case "radio":
					// TODO checkboxes
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					this.nextNodeRef.setValue(userValue);
					this.nextNodeRef.setLabel(userValue);
					break;
				case "user_list":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					this.nextNodeRef.setValue(userValue);
					this.nextNodeRef.setLabel(userValue);
					break;
				case "checkbox":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					this.nextNodeRef.setValue(userValue);
					this.nextNodeRef.setLabel(userValue);
					break;
				case "selectbox":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					this.nextNodeRef.setValue(userValue);
					this.nextNodeRef.setLabel(userValue);
					break;
				case "radio":
					var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+this.selectedResult[1], id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
					this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
					this.nextNodeRef.setValue(userValue);
					this.nextNodeRef.setLabel(userValue);
					break;
				default: /* input for title, text, description, attachFiles, textarea... */
					this.nextNodeRef = document.createElement('input');
					this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
					this.nextNodeRef.value = userValue;
					this.nodeObj.appendChild(this.nextNodeRef);
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
		setDefaultValues: function(fieldId, fieldLabel, userValues, type){
			this.setValue(fieldId);
			this.setLabel(fieldLabel);
			// initialize userValueInput;
			this.initializeKids(type, userValues);
		}
	}
);

