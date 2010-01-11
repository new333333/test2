/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
	function ss_tasks (tableId, binderId, namespace) {
	
		var binderId = binderId; 
		
		var tasksTable = document.getElementById(tableId);
	
		var tasks = new Object();
		
		var that = this;
		
		var namespace = namespace;
		
		var completedStatusDivs = new Object();
			
		this.changeStatus = function ( entryId, newStatus) {
			var urlParams = {operation:"update_task", binderId:binderId,
							entryId:entryId, ssTaskStatus:newStatus, randomNumber:ss_random++};
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
		
			var bindArgs = {
		    	url: url,
				handleAs: "json-comment-filtered",			
				error: function(err) {
					// alert(ss_not_logged_in);
				},
				load: function(data) {
					if (data.notLoggedIn) {
						alert(ss_not_logged_in);
					} else if (data.denied) {
						alert(data.denied);
					} else if (data) {
						tasks[entryId] = data;
			    		redrawTask(tasks[entryId]);
					}
				},
				preventCache: true,
				method: "post"
			};
			dojo.xhrGet(bindArgs);
		}
	
		this.changePriority = function (entryId, newPriority) {
			var urlParams = {operation:"update_task", binderId:binderId,
							entryId:entryId, ssTaskPriority:newPriority, randomNumber:ss_random++};
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
				
			var bindArgs = {
		    	url: url,
				handleAs: "json-comment-filtered",
				error: function(err) {
					// alert(ss_not_logged_in);
				},
				load: function(data) {
					if (data.notLoggedIn) {
						alert(ss_not_logged_in);
					} else if (data.denied) {
						alert(data.denied);
					} else if (data) {
						tasks[entryId] = data;
			    		redrawTask(tasks[entryId]);
					}
				},
				preventCache: true,
				method: "post"
			};
			dojo.xhrGet(bindArgs);
		}
		
		this.changeCompleted = function (entryId, newCompleted) {
			var urlParams = new Object();
			urlParams.operation="update_task";
			urlParams.binderId=binderId;
			urlParams.entryId=entryId;
			urlParams.ssTaskCompleted=newCompleted;
			urlParams.randomNumber=ss_random++;
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
				
			var bindArgs = {
		    	url: url,
				handleAs: "json-comment-filtered",
				error: function(err) {
					// alert(ss_not_logged_in);
				},
				load: function(data) {
					if (data.notLoggedIn) {
						alert(ss_not_logged_in);
					} else if (data.denied) {
						alert(data.denied);
					} else if (data) {
						tasks[entryId] = data;
				    	redrawTask(tasks[entryId]);
					}
				},
				preventCache: true,
				method: "post"
			};
			dojo.xhrGet(bindArgs);
		}
				
		function overwrite(newTasks) {
			clearTasks();
			addTasks(newTasks);
		}
		
		function clearTasks() {
			tasks = new Object();
		}
		
		function addTasks(newTasks) {
			for (var i = 0; i < newTasks.length; i++) {
				addTask(newTasks[i]);
			}
		}
		
		this.addTask = function(task) {
			// add always so it can be refresh
			tasks[task.id] = task;
		}
	
		function redrawAll() {
			displayTasks();
		}
	
		function displayTasks () {
			clearTable();
			for (var i = 0; i < tasks.length; i++) {
				displayTask(tasks[i]);
			}
		}
		
		function getTaskTRId (taskId) {
			return "ss_tasks_list_tr_" + taskId + "_" + namespace;
		}
		
		function createTaskTR(task) {
			var trObj = document.createElement('tr');
			
			trObj.setAttribute("id", getTaskTRId(task.id));
			
			trObj.appendChild(createTitleTD(task));
			trObj.appendChild(createPriorityTD(task));
			trObj.appendChild(createDueDateTD(task));
			trObj.appendChild(createStatusTD(task));
			trObj.appendChild(createAssignedTD(task));
			trObj.appendChild(createCompletedTD(task));		
			
			return trObj;
		}
		
		function displayTask (task) {
			// alert("display task: " + task.title);
			var trObj = createTaskTR(task);
			tasksTable.tBodies[0].appendChild(trObj);
		}
		
		function redrawTask(task) {
			var oldPriorityTD = document.getElementById("ss_tasks_" + namespace +"_" + task.id + "_priority");
			var newPriorityTD = createPriorityTD(task);
			oldPriorityTD.parentNode.replaceChild(newPriorityTD, oldPriorityTD);
			
			var oldStatusTD = document.getElementById("ss_tasks_" + namespace +"_" + task.id + "_status");
			var newStatusTD = createStatusTD(task);
			oldStatusTD.parentNode.replaceChild(newStatusTD, oldStatusTD);
			
			var oldCompletedTD = document.getElementById("ss_tasks_" + namespace +"_" + task.id + "_completed");
			var newCompletedTD = createCompletedTD(task);
			oldCompletedTD.parentNode.replaceChild(newCompletedTD, oldCompletedTD);	
			var oldDueTD = document.getElementById("ss_tasks_" + namespace +"_" + task.id + "_due");
			var newDueTD = createDueDateTD(task);
			oldDueTD.parentNode.replaceChild(newDueTD, oldDueTD);	
						
			if (task.status == "s3" || task.status == "s4") {
				dojo.addClass(document.getElementById("ss_tasks_" + namespace +"_" + task.id + "_title"), "ss_task_completed");
			} else {
				dojo.removeClass(document.getElementById("ss_tasks_" + namespace +"_" + task.id + "_title"), "ss_task_completed");
			}
		}
				
		function drawInteractiveChart(task, parent) {
			if (!completedStatusDivs[task.id]) {
				completedStatusDivs[task.id] = new Object();
			}
			
			var value = task.completed;
			var label = task.completedValues[task.completed];
			
			
			completedStatusDivs[task.id][11] = document.createElement('div');
			completedStatusDivs[task.id][11].innerHTML = task.completedValues[task.completed];
			ss_setClass(completedStatusDivs[task.id][11], "ss_bar_status");
			
			var container = document.createElement('div');
			ss_setClass(container, "ss_completedContainer");
			dojo.connect(container, "onmouseout", ss_declare_changeValue(that, task, container, completedStatusDivs[task.id][11], value));
		
			var clearDiv = document.createElement('div');
			ss_setClass(clearDiv, "ss_clear");
			parent.appendChild(container);
			
			
			for (var i=0; i<=10; i++) {
				completedStatusDivs[task.id][i] = document.createElement('div');
				var tempValue = i*10;
				var realValue = "c" + (tempValue==0?"000":(tempValue==100?"100":"0"+tempValue));
				ss_setStyle(completedStatusDivs[task.id][i], tempValue, value);
				var title = task.completedValues[realValue];
				completedStatusDivs[task.id][i].title = task.completedValues[realValue];
				
				dojo.connect(completedStatusDivs[task.id][i], "onclick", ss_declare_saveValue(that, task, container, completedStatusDivs[task.id][11], realValue));
				dojo.connect(completedStatusDivs[task.id][i], "onmouseover", ss_declare_changeValue(that, task, container, completedStatusDivs[task.id][11], realValue));
		
				container.appendChild(completedStatusDivs[task.id][i]);
			}
			

			parent.appendChild(completedStatusDivs[task.id][11]);
		}
		
		function ss_setStyle(obj, tempValue, borderValue) {
			var borderValueT = borderValue.replace("c", "") * 1;
			if (tempValue <= borderValueT && borderValueT != 0 ) {
				ss_setClass(obj, "ss_bar_on");
			} else {
				ss_setClass(obj, "ss_bar_off");
			}
		}
		
		function ss_declare_changeValue(obj, task, container, statusContainer, value) {
			return function(evt) { obj.ss_changeValue(task, container, statusContainer, value);}
		}
		
		function ss_declare_saveValue(obj, task, container, statusContainer, newValue) {
			return function(evt) {obj.ss_saveValue(task, container, statusContainer, newValue);}
		}
		
		this.ss_saveValue = function(task, container, statusContainer, newValue) {
			// dojo.connect(container, "onmouseout", ss_declare_changeValue(that, task, container, statusContainer, newValue));
			// statusContainer.innerHTML = task.completedValues[newValue];
			that.changeCompleted(task.id, newValue);
		}
		
		this.ss_changeValue = function(task, container, statusContainer, newValue) {
			var counter = 0;
			for (var i = 0; i < container.childNodes.length; i++) {
				if (container.childNodes[i].tagName == "DIV") {
					var temp = counter*10;
					ss_setStyle(container.childNodes[i], temp, newValue);
					counter++;
				}
			}
			statusContainer.innerHTML = tasks[task.id].completedValues[newValue];
		}
		
		function createCompletedTD(task) {
			var tdObj = document.createElement('td');
			tdObj.setAttribute("id", "ss_tasks_" + namespace +"_" + task.id + "_completed");
			var completedDivObj = document.createElement('div');
			completedDivObj.setAttribute("id", "ss_tasks_completed_" + task.id + "_"  + namespace);
		
			tdObj.appendChild(completedDivObj);
		
			drawInteractiveChart(task, completedDivObj);
		
			return tdObj;
		}
		
		function createAssignedTD(task) {
			var tdObj = document.createElement('td');
			var ulObj = document.createElement('ul');
			tdObj.appendChild(ulObj);
			for (var i = 0; i < task.assigned.length; i++) {
				var liObj = document.createElement('li');
				liObj.appendChild(document.createTextNode(task.assigned[i]));
				ulObj.appendChild(liObj);
			}
			return tdObj;
		}
		
		function createStatusTD(task) {
			var tdObj = document.createElement('td');
			tdObj.setAttribute("id", "ss_tasks_" + namespace +"_" + task.id + "_status");
			ss_setClass(tdObj, "ss_iconsContainer");
			for (var i = 0; i < task.statuses.length; i++) {
				var hrefObj = document.createElement('a');
				hrefObj.href = "javascript: // ;";
				
				if (task.status != task.statuses[i].key) {
					ss_setClass(hrefObj, "ss_taskStatus ss_taskStatus_" + task.statuses[i].key + "_u");
				} else {
					ss_setClass(hrefObj, "ss_taskStatus");			
				}
					
				(function(taskId, statusKey) {
				dojo.connect(hrefObj, "onclick", 
						function() {
							that.changeStatus(taskId, statusKey);
						}
					);
				})(task.id, task.statuses[i].key);
				
				var imgObj= document.createElement('img');
				if (task.status == task.statuses[i].key) {
					var src = ss_imagesPath + "icons/status_" + task.statuses[i].key + ".gif"; 
				} else {
					var src = ss_imagesPath + "pics/1pix.gif";
				}
				imgObj.src = src;
				imgObj.setAttribute("alt", task.statuses[i].value);
				imgObj.setAttribute("title", task.statuses[i].value);
				
				hrefObj.appendChild(imgObj);
				
				
				tdObj.appendChild(hrefObj);
			}
			return tdObj;
		}
		
		function createPriorityTD(task) {
			var tdObj = document.createElement('td');
			tdObj.setAttribute("id", "ss_tasks_" + namespace +"_" + task.id + "_priority");
			ss_setClass(tdObj, "ss_iconsContainer");
			
			for (var i = 0; i < task.priorities.length; i++) {
				var hrefObj = document.createElement('a');
				hrefObj.href = "javascript: // ;";
		    
		    	if (task.priority != task.priorities[i].key) {
		    		ss_setClass(hrefObj, "ss_taskPriority ss_taskPriority_" + task.priorities[i].key + "_u");
				} else {
					ss_setClass(hrefObj, "ss_taskPriority");			
				}
				
				dojo.connect(hrefObj, "onclick", function(obj, taskId, newPriority) {
					return function() {
						obj.changePriority(taskId, newPriority);
					}					
				} (that, task.id, task.priorities[i].key));
				
				var imgObj= document.createElement('img');
				
				if (task.priority == task.priorities[i].key) {
					var src = ss_imagesPath + "icons/prio_" + task.priorities[i].key + ".gif"; 
				} else {
					var src = ss_imagesPath + "pics/1pix.gif";
				}
				imgObj.src = src;
				imgObj.setAttribute("alt", task.priorities[i].value);
				imgObj.setAttribute("title", task.priorities[i].value);
	
				
				hrefObj.appendChild(imgObj);
				tdObj.appendChild(hrefObj);
			
			}
					
			return tdObj;
		}
		
		function createDueDateTD(task) {
			var tdObj = document.createElement('td');
			tdObj.setAttribute("id", "ss_tasks_" + namespace +"_" + task.id + "_due");
			var txt = task.dueDate;
			if (task.overdue && (task.status != "s3" && task.status != "s4")) {
				dojo.addClass(tdObj, "ss_overdue");
				txt += ss_tasks.overdueLabel ? " " + ss_tasks.overdueLabel : "";
			}
			tdObj.appendChild(document.createTextNode(txt));
			return tdObj;
		}		
		
		function createTitleTD(task) {
			var tdObj = document.createElement('td');
			if (task.status == "s3" || task.status == "s4") {
				ss_setClass(tdObj, "ss_task_completed");
			}
			
			var hrefObj = document.createElement('a');
			hrefObj.href = "javascript: // ;";
		    
			var callViewTask = declareViewTask(that, task.id);
			dojo.connect(hrefObj, "onclick", function(obj, taskId){
				return function() {
					obj.viewTask(taskId);
				}				
			}(that, task.id));
			
			
			if (!task.title || task.title == "") {
				task.title = ss_noEntryTitleLabel;
			}
			hrefObj.appendChild(document.createTextNode(task.title));
			
			tdObj.appendChild(hrefObj);
			return tdObj;
		}
		
		function clearTable() {
			// alert("clear table");
			while (tasksTable.tBodies[0].rows.length > 0) {
				tasksTable.tBodies[0].deleteRow(0);
			}
		}
		
		function removeAllChildren(obj) {
			while (obj.firstChild) {
				obj.removeChild(obj.firstChild);
			}
		}
		
		this.viewTask = function(entryId) {
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, entryId:entryId}, "view_folder_entry");
			ss_loadEntryUrl(url, entryId);
		}
	
	}
	
	ss_tasks.adjustFormAttributes = function (changed) {
		var statusesObj = document.getElementsByName("status");
		var statusObj = statusesObj && statusesObj.length? statusesObj[0] : null;
		
		var completediesObj = document.getElementsByName("completed");
		var completedObj = completediesObj && completediesObj.length? completediesObj[0] : null;
		
		if (!(statusObj && completedObj)) {
			return;
		}
		
		// status completed => completed 100%
		if (changed == "status") {
			if (statusObj.options[statusObj.selectedIndex].value == "s3") {
				completedObj.selectedIndex = 10;
			}
		}
		
		// new status needs action or in process and current completed 100% => completed 90%
		if (changed == "status") {
			if ((statusObj.options[statusObj.selectedIndex].value == "s1" || statusObj.options[statusObj.selectedIndex].value == "s2") &&
					completedObj.options[completedObj.selectedIndex].value == "c100") {
				completedObj.selectedIndex = 9;
			}
		}		
		
		// completed 0% => status needs action
		if (changed == "completed") {
			if (completedObj.options[completedObj.selectedIndex].value == "c000") {
				statusObj.selectedIndex = 0;
			}
		}		
		
		// completed 10% - 90% => status in process
		if (changed == "completed") {
			var completedValue = completedObj.options[completedObj.selectedIndex].value;
			if (completedValue == "c010" || completedValue == "c020" || completedValue == "c030" || 
					completedValue == "c040" || completedValue == "c050" || completedValue == "c060" || 
					completedValue == "c070" || completedValue == "c080" || completedValue == "c090") {
				statusObj.selectedIndex = 1;
			}
		}
		
		// completed 100% => status completed
		if (changed == "completed") {
			var completedValue = completedObj.options[completedObj.selectedIndex].value;
			if (completedValue == "c100") {
				statusObj.selectedIndex = 2;
			}
		}		
	}
	
