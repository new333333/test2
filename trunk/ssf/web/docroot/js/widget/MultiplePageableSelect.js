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
dojo.provide("ss_widget.MultiplePageableSelect");

dojo.require("ss_widget.SelectPageable");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");

dojo.widget.defineWidget(
	"ss_widget.MultiplePageableSelect",
	ss_widget.SelectPageable,
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
			ss_widget.MultiplePageableSelect.superclass.selectOption.call(this, evt);
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
			}
			this.actualizeFormValue();
			
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
