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
dojo.provide("sitescape.widget.FieldSelect");

dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");


dojo.declare(
  "sitescape.widget.FieldSelectDataProvider",
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
	"sitescape.widget.FieldSelect",
	dojo.widget.Select,
	{
		nestedUrl : '',
		entryTypeId:'',
		nodeObj : null,
		searchFieldIndex: "",
		weekStartsOn: null,
		nextNodeRef: null,
		widgetContainer: null,
		dataProviderClass: "sitescape.widget.FieldSelectDataProvider",		
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
			sitescape.widget.FieldSelect.superclass.fillInTemplate.call(this, args, frag);
			this.setValue("");
		},
		selectOption : function(/*Event*/ evt){
			this.removeKids();
			sitescape.widget.FieldSelect.superclass.selectOption.call(this, evt);
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
			if (typeof this.weekStartsOn !== "undefined") {
				prop.weekStartsOn = this.weekStartsOn;
			}
			this.nextNodeRef = dojo.widget.createWidget("DropdownDatePickerActivateByInput", prop, this.widgetContainer, "last");
		},
		
		addEventField: function(value) {
			var prop = {value: value, lang: ss_user_locale, id: "elementValue" + this.searchFieldIndex, 
						name: "elementValue" + this.searchFieldIndex, searchFieldIndex: this.searchFieldIndex, 
						autoComplete: false, nodeObj: this.widgetContainer};
			if (typeof this.weekStartsOn !== "undefined") {
				prop.weekStartsOn = this.weekStartsOn;
			}
			this.nextNodeRef = dojo.widget.createWidget("DropdownDatePickerActivateByInput", prop, this.widgetContainer, "last");
		},
		
		addUserListField: function(value, label) {
			var url = ss_AjaxBaseUrl + "&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}";
			var prop = {dataUrl:url, 
						id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, maxListLength : 12, autoComplete: false};
			this.nextNodeRef = dojo.widget.createWidget("SelectPageable", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}
		},

		addCheckboxField: function(value, label) {
			var localElementName="checkbox";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+localElementName, id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}			
		},
		
		addRadioField: function(value, label) {
			var localElementName="radio";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+localElementName, id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}
		},
		
		addSelectBoxField: function(value, label) {
			var localElementName="selectbox";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+localElementName, id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			this.nextNodeRef = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			if (value && label) {
				this.nextNodeRef.setValue(value);
				this.nextNodeRef.setLabel(label);
			}
		},
		
		addSimpleInputField: function(value) {
			this.nextNodeRef = document.createElement('input');
			this.nextNodeRef.type = "text";
			this.nextNodeRef.id = "elementValue" + this.searchFieldIndex;
			this.nextNodeRef.name = "elementValue" + this.searchFieldIndex;
			if (value) {
				this.nextNodeRef.value = value;
			}
			this.nodeObj.appendChild(this.nextNodeRef);
		}
				
	}
);

