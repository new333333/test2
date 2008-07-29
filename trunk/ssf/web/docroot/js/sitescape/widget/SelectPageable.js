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
dojo.provide("sitescape.widget.SelectPageable");

dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");

sitescape.widget.incrementalPagableComboBoxDataProvider = function(/*String*/ url, /*Number*/ limit, /*Number*/ timeout){
	this.searchUrl = url;
	this.inFlight = false;
	this.activeRequest = null;
	this.allowCache = false;

	this.cache = {};

	this.init = function(/*Widget*/ cbox){
		this.searchUrl = cbox.dataUrl;
	};

	this.addToCache = function(/*String*/ keyword, /*Array*/ data){
		if(this.allowCache){
			this.cache[keyword] = data;
		}
	};

	this.startSearch = function(/*String*/ searchStr, /*String*/ type, /*Boolean*/ ignoreLimit, /*String*/ pagerStr, /*String*/ pagerType){
		if(this.inFlight){
			// FIXME: implement backoff!
		}
		var tss = encodeURIComponent(searchStr);
		
		var pager = pagerStr || "";
		var pgTp = pagerType || "";
		// TODO ?? var wasArrow = (type=="a"); 
		
		var realUrl = dojo.string.substituteParams(this.searchUrl, {"searchString": tss, "pagerString": pager});
		var _this = this;
		var request = dojo.xhrGet({
			url: realUrl,
			method: "get",
			mimetype: "text/json",
			load: function(data){
			try {
				_this.inFlight = false;
				if(!dojo.isArray(data)){
					var arrData = [];
					for(var key in data){
						if (typeof(data[key]) == 'function') continue;
						if (key=="_prev") {
							arrData.unshift(data[key]);
						}
						else if (key=="_next") {
							arrData.push(data[key]);
						}
						else {
							arrData.push([data[key], key]);
						}
					}
					data = arrData;
				}
				_this.addToCache(searchStr, data);
				
				var activeIndex = -7;

				// process pager object. Definition:
				// data = [  {pagerObject} , [text,value] , [text,value] , ... ]
				// pagerObject = {  _prev: { text: "<prev>", pgStr: "10:19" },   // optional
				//                  _next: { text: "<next>", pgStr: "20:29" },   // optional
				//                  _idx:  1 }   // optional
				// _idx parameter:   n/a   use default logic
				//                   -7    use default logic
				//                   -2    display data if arrow, otherwise hide data				
				//                   -ve   these wont match an index, nothing selected
				//                   else  if there is matching index, selects it   
				if (!dojo.isArray(data[0])) {
					//console.debug(data[0]._prev);
					var pagerObj = data.shift();
					
					
					if (typeof pagerObj != "undefined") { 

						if (typeof pagerObj._prev != "undefined")
						{
							pagerObj._prev.pgTp = "p";
							data.unshift(pagerObj._prev); // previous as first item
						}
						if (typeof pagerObj._next != "undefined") 
						{
							pagerObj._next.pgTp = "n";
							data.push(pagerObj._next);    // next as last item
						}
						if (typeof pagerObj._idx != "undefined") 
							activeIndex = pagerObj._idx;

					}

					// server returned only a pager, no data
					if (data.length==0)
					{
						_this.provideSearchResults(data);
						return;
					}
				}

				// special case, display data if arrow, otherwise hide data
				if (activeIndex==-2) {
					if (wasArrow) {
						activeIndex = -1;    // don't autocomplete (i.e. click arrow, no match)
					} else {
						_this.provideSearchResults([]); // keyboard entry, not found
						return;
					}
				}

				// default selection
				if (activeIndex==-7 || activeIndex==-2) {
					if (pgTp=="p")       // previous was pressed, select last
						activeIndex = -11; 
					else if (pgTp=="n")  // next was pressed, select first
						activeIndex = -10; 
					else 
						activeIndex = (!dojo.isArray(data[0]) ? 1 : 0); // select first, but skip <prev>
				}

				_this.provideSearchResults(data, activeIndex);
			} catch (e) {alert(e);}
			}
		});
		this.inFlight = true;
	};
}

dojo.widget.defineWidget(
	"sitescape.widget.SelectPageable",
	dojo.widget.Select,
	{
	
		dataProviderClass: "sitescape.widget.incrementalPagableComboBoxDataProvider",
	
		openResultList: function(/*Array*/ results){
			if (!this.isEnabled){
				return;
			}
			this.clearResultList();
			if(!results.length){
				this.hideResultList();
			}
		
			if(	(this.autoComplete)&&
				(results.length)&&
				(!this._prev_key_backspace)&&
				(this.textInputNode.value.length > 0)){
				var cpos = this.getCaretPos(this.textInputNode);
				// only try to extend if we added the last character at the end of the input
				if((cpos+1) > this.textInputNode.value.length){
					// only add to input node as we would overwrite Capitalisation of chars
					this.textInputNode.value += results[0][0].substr(cpos);
					// build a new range that has the distance from the earlier
					// caret position to the end of the first string selected
					this.setSelectedRange(this.textInputNode, cpos, this.textInputNode.value.length);
				}
			}
		
			var even = true;
			while(results.length){
				var tr = results.shift();
				if(tr){
					var td = document.createElement("div");
					// if not array, it is a pager object. process this
					if(!dojo.isArray(tr)){			
						td.appendChild(document.createTextNode(tr.text));
						td.setAttribute("resultName", tr.text);
						td.setAttribute("resultValue", tr.pgStr);
						td.setAttribute("pagerType", tr.pgTp);  // either "n" or "p"
						td.setAttribute("isPager", true);
					}
					else
					{
						td.appendChild(document.createTextNode(tr[0]));
						td.setAttribute("resultName", tr[0]);
						td.setAttribute("resultValue", tr[1]);
					}					
					td.className = "dojoComboBoxItem "+((even) ? "dojoComboBoxItemEven" : "dojoComboBoxItemOdd");
					even = (!even);
					this.optionsListNode.appendChild(td);
				}
			}
		
			// show our list (only if we have content, else nothing)
			this.showResultList();
		},
		
		selectOption: function(/*Event*/ evt){
			var tgt = null;
			if(!evt){
				evt = { target: this._highlighted_option };
			}
	
			if(!dojo.html.isDescendantOf(evt.target, this.optionsListNode)){
				// handle autocompletion where the the user has hit ENTER or TAB
	
				// if the input is empty do nothing
				if(!this.textInputNode.value.length){
					return;
				}
				tgt = dojo.html.firstElement(this.optionsListNode);
	
				// user has input value not in option list
				if(!tgt || !this._isInputEqualToResult(tgt.getAttribute("resultName"))){
					return;
				}
				// otherwise the user has accepted the autocompleted value
			}else{
				tgt = evt.target; 
			}
	
			while((tgt.nodeType!=1)||(!tgt.getAttribute("resultName"))){
				tgt = tgt.parentNode;
				if(tgt === dojo.body()){
					return false;
				}
			}
			
			if (tgt.getAttribute("isPager")) {
				this._highlighted_option = null;
				this._handleBlurTimer(true, 100);
				//var pgTp = evt.keyboardSelect ? tgt.getAttribute("pagerType") : null; // send pager info when using keyboard
				this.startSearch("", null, null, tgt.getAttribute("resultValue"),  tgt.getAttribute("pagerType"));
				this.tryFocus();
			}
			// sucessful selection
			else {
	
				this.selectedResult = [tgt.getAttribute("resultName"), tgt.getAttribute("resultValue")];
				this.setAllValues(tgt.getAttribute("resultName"), tgt.getAttribute("resultValue"));
				if(!evt.noHide){
					this.hideResultList();
					this.setSelectedRange(this.textInputNode, 0, null);
				}
				this.tryFocus();
			}
		}
	}	
)
