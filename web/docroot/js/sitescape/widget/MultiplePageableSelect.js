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
dojo.provide("sitescape.widget.MultiplePageableSelect");

dojo.require("sitescape.widget.SelectPageable");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");

dojo.widget.defineWidget(
	"sitescape.widget.MultiplePageableSelect",
	sitescape.widget.SelectPageable,
	{
		allowMultiple: false,
		collectedValues: new Array(),
		collectedLabels: new Array(),
		collectedDiv: null,
		collectedUl : null,
		formElement : null,
		imgRootPath: "",
		hiddenFormElementName: "",
		selectOption : function(/*Event*/ evt){
			sitescape.widget.MultiplePageableSelect.superclass.selectOption.call(this, evt);
			this.addSelectedToCollection(this.selectedResult[1], this.selectedResult[0]);
		},
		createCollectedDiv : function(){
			this.collectedDiv = document.createElement('div');
			this.collectedDiv.id = "collectorContainer_"+this.id;
			this.collectedDiv.setAttribute("class", "ss_selectedItemsContainer");
			this.domNode.appendChild(this.collectedDiv);
		},
		createCollectedUl : function() {
			if (this.collectedUl == null) {
				this.collectedUl = document.createElement('ul');
				this.collectedUl.id = "collectorContainerList_"+this.id;
				this.collectedDiv.appendChild(this.collectedUl);
			}
		},
		addSelectedToCollection : function(value, label){
			if (this.collectedDiv == null) this.createCollectedDiv();
			if (this.collectedUl == null) this.createCollectedUl();
			
			var addValue = true;
			if (!this.allowMultiple) {
				for (var i=0; i<this.collectedValues.length; i++){
					if (this.collectedValues[i] == value) {
						addValue = false;
						break;
					}
				}
			} 
			if (addValue) {
				var currentIndex = this.collectedValues.length;
				this.collectedValues[currentIndex] = value;
				this.collectedLabels[currentIndex] = label;
				var li = document.createElement('li');
				li.appendChild(document.createTextNode(label));
				var removeIcon = document.createElement('img');
				removeIcon.setAttribute("src", this.imgRootPath+"pics/delete.gif");
				function declareRemove(parentObj, objToRemove, currentIndex) {
					
					return function() {
						parentObj.remove(objToRemove, currentIndex);
					}
				}
				var callRemove = declareRemove(this, li, currentIndex);
				dojo.event.connect(removeIcon, "onclick", callRemove);
				li.appendChild(removeIcon);
				this.collectedUl.appendChild(li); 
				this.actualizeFormValue();
			}
		},
		remove : function(liObj, ndx) {
			this.collectedUl.removeChild(liObj);
			this.collectedValues[ndx] = null;
			this.collectedLabels[ndx] = null;
			this.actualizeFormValue();
		},
		actualizeFormValue: function() {
			if (this.formElement == null) {
				this.formElement = document.createElement("input");
				this.formElement.setAttribute("type", "hidden");
				this.formElement.name = this.hiddenFormElementName;
				this.collectedDiv.appendChild(this.formElement);
			}
			this.formElement.value = this.collectedValues.join(" ");
		}
	}
);
