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
dojo.provide("sitescape.widget.WorkflowSelect");

// dojo_xxx		dojo.require("dojo.widget.Select");
// dojo_xxx		dojo.require("dojo.widget.*");
// dojo_xxx		dojo.require("dojo.html.*");
// dojo_xxx		dojo.require("dojo.widget.html.stabile");

dojo.widget.defineWidget(
	"sitescape.widget.WorkflowSelect",
	dojo.widget.Select,
	{
		nestedUrl : '',
		stepsWidget : null,
		searchFieldName: "",
		widgetStepsRef : null,
	    getSubSearchString: function() {return ""},
		selectOption : function(/*Event*/ evt){
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			sitescape.widget.WorkflowSelect.superclass.selectOption.call(this, evt);
			this.loadWorkflowSteps(this.selectedResult[1]);
		},
		setDefaultValue: function(wfId, wfLabel, stepIds) {
			this.setLabel(wfLabel);
			this.setValue(wfId);
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			
			if (!stepIds) {
				return;
			}
			this.loadWorkflowSteps(wfId, stepIds);
		},
		handleArrowClick: function(){
			this._handleBlurTimer(true, 0);
			this.tryFocus();
			if(this.popupWidget.isShowingNow){
				this.hideResultList();
			}else{
				// forces full population of results, if they click
				// on the arrow it means they want to see more options
				this.startSearch(this.getSubSearchString());
			}
		},
		loadWorkflowSteps: function(workflowId, stepsIds) {
			stepsIds = stepsIds||[];
			var stepsS = "|" + stepsIds.join("|") + "|";
			this.stepsWidget.innerHTML = "";
			dojo.xhrGet({
				url: this.nestedUrl+"&workflowId="+workflowId,
				load: dojo.lang.hitch(this, function(type, data, evt){ 
					for (var i in data) {
						var liObj = document.createElement("li");
						this.stepsWidget.appendChild(liObj);
						var chckboxId = this.stepsWidget.id+workflowId+i;
						var chkbox = document.createElement("input");
						chkbox.type = "checkbox";
						chkbox.value = i;
						chkbox.id = chckboxId;
						chkbox.name = this.searchFieldName;
						liObj.appendChild(chkbox);
						if (stepsS.indexOf("|" + i + "|") > -1) {
							chkbox.checked = true;
						}		
						var label = document.createElement("label");
						label.setAttribute("style", "padding-left: 5px;");
						label.appendChild(document.createTextNode(data[i]));
						liObj.appendChild(label);
						label.htmlFor =  chckboxId;
					}
				}),
				mimetype: "text/json",
				preventCache: true
			});
		}
	}
);
