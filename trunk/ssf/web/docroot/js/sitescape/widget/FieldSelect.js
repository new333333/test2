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
			mimetype: "text/json",
			preventCache: true
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
		nextNodeRefs: new Array(),
		widgetContainer: null,
		widgetContainer2: null,
		dataProviderClass: "sitescape.widget.FieldSelectDataProvider",		
		removeKids: function () {
			for (var i = 0; i < this.nextNodeRefs.length; i++) {
				try {
					this.nextNodeRefs[i].destroy();
				} catch(e) {
					try {
						this.nodeObj.removeChild(this.nextNodeRefs[i]);
					} catch (e) {}
				}
			}
			this.nextNodeRefs = new Array();
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
				case "date_time":
					this.addDateTimeField('');
					break;					
				case "event":
					this.addEventField('');
					break;
				case "user_list":
					this.addUserListField();
					break;
				case "group_list":
					this.addGroupListField();
					break;
				case "team_list":
					this.addTeamListField();
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
				case "entryAttributes":
					this.addEntryAttributesField();
					break;
				default: /* input for title, text, description, attachFiles, textarea... */
					this.addSimpleInputField();
			}
		},
		initializeKids: function(fieldType, userValue, userValueLabel) {
			switch (fieldType) {
				case "date":
					this.addDateField(userValueLabel);
					break;
				case "date_time":
					this.addDateTimeField(userValueLabel);
					break;					
				case "event":
					this.addEventField(userValue);
					break;
				case "user_list":
					this.addUserListField(userValue, userValueLabel);
					break;
				case "group_list":
					this.addGroupListField(userValue, userValueLabel);
					break;
				case "team_list":
					this.addTeamListField(userValue, userValueLabel);
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
				case "entryAttributes":
					this.addEntryAttributesField(userValue, userValueLabel);
					break;
				default: /* input for title, text, description, attachFiles, textarea... */
					this.addSimpleInputField(userValue);
			}
		},
		fillInTemplate: function(/*Object*/ args, /*Object*/ frag) {
			sitescape.widget.FieldSelect.superclass.fillInTemplate.call(this, args, frag);
			this.setValue("");
			this.nextNodeRefs = new Array();
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
			var prop = {value: value, lang: djConfig&&djConfig["locale"]?djConfig["locale"]:"en", id: "elementValue" + this.searchFieldIndex, 
						name: "elementValue" + this.searchFieldIndex, searchFieldIndex: this.searchFieldIndex, 
						autoComplete: false, nodeObj: this.widgetContainer};
			if (typeof this.weekStartsOn !== "undefined") {
				prop.weekStartsOn = this.weekStartsOn;
			}
			this.nextNodeRefs.push(dojo.widget.createWidget("DropdownDatePickerActivateByInput", prop, this.widgetContainer, "last"));
		},
		
		addDateTimeField: function(value) {
			var dateValue = "";
			var timeValue = "";
			if (value && value.length >= 10) {
				dateValue = value.substring(0, 10);
				if (value.length > 10) {
					timeValue = value.substring(11);
				}
			}
			var prop = {value: dateValue, 
						lang: djConfig&&djConfig["locale"]?djConfig["locale"]:"en", 
						id: "elementValue" + this.searchFieldIndex, 
						name: "elementValue" + this.searchFieldIndex, 
						searchFieldIndex: this.searchFieldIndex, 
						autoComplete: false, 
						nodeObj: this.widgetContainer};
			if (typeof this.weekStartsOn !== "undefined") {
				prop.weekStartsOn = this.weekStartsOn;
			}
			this.nextNodeRefs.push(dojo.widget.createWidget("DropdownDatePickerActivateByInput", prop, this.widgetContainer, "last"));
			prop = {value: timeValue, 
					lang: djConfig&&djConfig["locale"]?djConfig["locale"]:"en", 
					id: "elementValue" + this.searchFieldIndex + "0", 
					name: "elementValue" + this.searchFieldIndex + "0", 
					searchFieldIndex: this.searchFieldIndex, 
					autoComplete: false, 
					nodeObj: this.widgetContainer};
			this.nextNodeRefs.push(dojo.widget.createWidget("DropdownTimePickerActivateByInput", prop, this.widgetContainer, "last"));
		},		
		
		addEventField: function(value) {
			var prop = {value: value, lang: djConfig&&djConfig["locale"]?djConfig["locale"]:"en", id: "elementValue" + this.searchFieldIndex, 
						name: "elementValue" + this.searchFieldIndex, searchFieldIndex: this.searchFieldIndex, 
						autoComplete: false, nodeObj: this.widgetContainer};
			if (typeof this.weekStartsOn !== "undefined") {
				prop.weekStartsOn = this.weekStartsOn;
			}
			this.nextNodeRefs.push(dojo.widget.createWidget("DropdownDatePickerActivateByInput", prop, this.widgetContainer, "last"));
		},
		
		addUserListField: function(value, label) {
			var url = ss_AjaxBaseUrl + "&action=advanced_search&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}";
			var prop = {dataUrl:url, 
						id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, maxListLength : 12, autoComplete: false};
			var userListWidgt = dojo.widget.createWidget("SelectPageable", prop, this.widgetContainer, "last");
			this.nextNodeRefs.push(userListWidgt);
			if (value && label) {
				userListWidgt.setValue(value);
				userListWidgt.setLabel(label);
			}
		},
		
		addGroupListField: function(value, label) {
			var url = ss_AjaxBaseUrl + "&action=advanced_search&operation=get_groups_widget&searchText=%{searchString}&pager=%{pagerString}";
			var prop = {dataUrl:url, 
						id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, maxListLength : 12, autoComplete: false};
			var groupListWidgt = dojo.widget.createWidget("SelectPageable", prop, this.widgetContainer, "last");
			this.nextNodeRefs.push(groupListWidgt);
			if (value && label) {
				groupListWidgt.setValue(value);
				groupListWidgt.setLabel(label);
			}
		},
		
		addTeamListField: function(value, label) {
			var url = ss_AjaxBaseUrl + "&action=advanced_search&operation=get_teams_widget&searchText=%{searchString}&pager=%{pagerString}";
			var prop = {dataUrl:url, 
						id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, maxListLength : 12, autoComplete: false};
			var teamListWidgt = dojo.widget.createWidget("SelectPageable", prop, this.widgetContainer, "last");
			this.nextNodeRefs.push(teamListWidgt);
			if (value && label) {
				teamListWidgt.setValue(value);
				teamListWidgt.setLabel(label);
			}
		},			

		addEntryAttributesField: function(value, label) {
			var localElementName="";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var url = ss_searchBinderUrl + "&action=advanced_search&operation=get_entry_attributes_widget&searchText=&pager=&elementName="+localElementName+"";
			if (localElementName.indexOf(",") == -1) {
				var prop = {dataUrl:url, id:"elementValue" + this.searchFieldIndex, 
					name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, 
					nodeObj:this.widgetContainer, widgetContainer:this.widgetContainer, widgetContainer2:this.widgetContainer2, 
					maxListLength : 12, autoComplete: false};
				var entryAttributesWidgt = dojo.widget.createWidget("FieldSelect", prop, this.widgetContainer, "last");
				this.nextNodeRefs.push(entryAttributesWidgt);
				if (value && label) {
					entryAttributesWidgt.setValue(value);
					entryAttributesWidgt.setLabel(label);
				}
			} else {
				var prop = {dataUrl:url, id:"elementValueValue" + this.searchFieldIndex, 
					name:"elementValueValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, 
					nodeObj:this.widgetContainer, maxListLength : 12, autoComplete: false};
				var entryAttributesValueWidgt = dojo.widget.createWidget("SelectPageable", prop, this.widgetContainer2, "last");
				this.nextNodeRefs.push(entryAttributesValueWidgt);
				if (value && label) {
					entryAttributesValueWidgt.setValue(value);
					entryAttributesValueWidgt.setLabel(label);
				}
			}
		},			

		addCheckboxField: function(value, label) {
			var localElementName="checkbox";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+localElementName, id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			var widget = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			this.nextNodeRefs.push(widget);
			if (value && label) {
				widget.setValue(value);
				widget.setLabel(label);
			}			
		},
		
		addRadioField: function(value, label) {
			var localElementName="radio";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+localElementName, id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			var widget = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			this.nextNodeRefs.push(widget);
			if (value && label) {
				widget.setValue(value);
				widget.setLabel(label);
			}
		},
		
		addSelectBoxField: function(value, label) {
			var localElementName="selectbox";
			if (this.selectedResult && this.selectedResult[1]) localElementName=this.selectedResult[1];
			var prop = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.entryTypeId+"&elementName="+localElementName, id:"elementValue" + this.searchFieldIndex, name:"elementValue" + this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer};
			var widget = dojo.widget.createWidget("Select", prop, this.widgetContainer, "last");
			this.nextNodeRefs.push(widget);
			if (value && label) {
				widget.setValue(value);
				widget.setLabel(label);
			}
		},
		
		addSimpleInputField: function(value) {
			var inpt = document.createElement('input');
			inpt.type = "text";
			inpt.id = "elementValue" + this.searchFieldIndex;
			inpt.name = "elementValue" + this.searchFieldIndex;
			if (value) {
				inpt.value = value;
			}
			this.nextNodeRefs.push(inpt);
			this.nodeObj.appendChild(inpt);
		}
				
	}
);

