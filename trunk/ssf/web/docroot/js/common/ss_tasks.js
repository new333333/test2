if (!window.ss_tasks) {
	
	function ss_tasks (tableId, binderId, namespace) {
	
		var filterType = "WEEK"; 	// CLOSED - status: (cancelled + completed)
									// DAY - start date until today + status: (need action + in progress)
									// WEEK - start date current week end day + status: (need action + in progress)
									// MONTH - start date until current month end day + status: (need action + in progress)
									// ACTIVE - status: (need action + in progress)
		
		var binderId = binderId; 
		
		var tasksTable = document.getElementById(tableId);
	
		var tasks = new Array();
		
		var that = this;
		
		var statuses;
		
		var priorities;
		
		var completed;
		
		var namespace = namespace;
		
		var completedStatusDivs = new Array();
			
		loadExtendedInfo();
	
		this.changeStatus = function ( entryId, newStatus) {
			var url = ss_AjaxBaseUrl + "&operation=update_task";
			url += "\&binderId=" + binderId;
			url += "\&entryId=" + entryId;
			url += "\&ssTaskStatus=" + newStatus;
			url += "\&randomNumber=" + ss_random++;
				
			var bindArgs = {
		    	url: url,
				error: function(type, data, evt) {
					// alert(ss_not_logged_in);
				},
				load: function(type, data, evt) {
					try {
						tasks[entryId] = data;
				    	redrawTask(tasks[entryId]);
					} catch (e) {alert(e);}
				},
							
				mimetype: "text/json",
				method: "post"
			};
			dojo.io.bind(bindArgs);
		}
	
		this.changePriority = function (entryId, newPriority) {
			var url = ss_AjaxBaseUrl + "&operation=update_task";
			url += "\&binderId=" + binderId;
			url += "\&entryId=" + entryId;
			url += "\&ssTaskPriority=" + newPriority;
			url += "\&randomNumber=" + ss_random++;
				
			var bindArgs = {
		    	url: url,
				error: function(type, data, evt) {
					// alert(ss_not_logged_in);
				},
				load: function(type, data, evt) {
					try {
						tasks[entryId] = data;
				    	redrawTask(tasks[entryId]);
					} catch (e) {alert(e);}
				},
							
				mimetype: "text/json",
				method: "post"
			};
			dojo.io.bind(bindArgs);
		}
		
		this.changeCompleted = function (entryId, newCompleted) {
			var url = ss_AjaxBaseUrl + "&operation=update_task";
			url += "\&binderId=" + binderId;
			url += "\&entryId=" + entryId;
			url += "\&ssTaskCompleted=" + newCompleted;
			url += "\&randomNumber=" + ss_random++;
				
			var bindArgs = {
		    	url: url,
				error: function(type, data, evt) {
					// alert(ss_not_logged_in);
				},
				load: function(type, data, evt) {
					try {
						tasks[entryId] = data;
				    	redrawTask(tasks[entryId]);
					} catch (e) {alert(e);}
				},
							
				mimetype: "text/json",
				method: "post"
			};
			dojo.io.bind(bindArgs);
		}


		function loadExtendedInfo () {
			if (!binderId || binderId == "") {
				return;
			}

			var url = ss_AjaxBaseUrl + "&operation=get_tasks_extended_info";
			url += "\&binderId=" + binderId;
			url += "\&randomNumber=" + ss_random++;
				
			var bindArgs = {
		    	url: url,
				error: function(type, data, evt) {
					// alert(ss_not_logged_in);
				},
				load: function(type, data, evt) {
					try {
				    	statuses = data.statuses;
				    	priorities = data.priorities;
				    	completed = data.completed;
				    	loadTasks();
					} catch (e) {alert(e);}
				},
							
				mimetype: "text/json",
				method: "get"
			};
			dojo.io.bind(bindArgs);
		}
		
		function loadTasks() {
			if (!binderId || binderId == "") {
				return;
			}
			
			var url = ss_AjaxBaseUrl + "&operation=find_tasks";
			url += "\&binderId=" + binderId;
			url += "\&ssTaskFilterType=" + filterType;
			url += "\&randomNumber=" + ss_random++;
				
			var bindArgs = {
		    	url: url,
				error: function(type, data, evt) {
					// alert(ss_not_logged_in);
				},
				load: function(type, data, evt) {
					try {
				    	overwrite(data.tasks);
				    	redrawAll();
					} catch (e) {alert(e);}
				},
							
				mimetype: "text/json",
				method: "get"
			};
			dojo.io.bind(bindArgs);
		}
		
		function overwrite(newTasks) {
			clearTasks();
			addTasks(newTasks);
		}
		
		function clearTasks() {
			tasks = new Array();
		}
		
		function addTasks(newTasks) {
			for (var i = 0; i < newTasks.length; i++) {
				addTask(newTasks[i]);
			}
		}
		
		function addTask(task) {
			// add always so it can be refresh
			tasks[task.id] = task;
		}
	
		function redrawAll() {
			displayTasks();
		}
	
		function displayTasks () {
			clearTable();
			for (var i in tasks) {
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
			var taskTRId = getTaskTRId(task.id);
			var taskTRObjOld = document.getElementById(taskTRId);
			var taskTRObjNew = createTaskTR(task);
			
			taskTRObjOld.parentNode.replaceChild(taskTRObjNew, taskTRObjOld);
		}
		
		function drawInteractiveChart(task, parent) {
			if (!completedStatusDivs[task.id]) {
				completedStatusDivs[task.id] = new Array();
			}
			
			var value = task.completed;
			var label = completed[task.completed];
			
			var container = document.createElement('div');
			dojo.html.setClass(container, "ss_completedContainer");
			dojo.event.connect(container, "onmouseout", ss_declare_changeValue(that, task, value));
		
			var clearDiv = document.createElement('div');
			dojo.html.setClass(clearDiv, "ss_clear");
			parent.appendChild(container);
			
			
			for (var i=0; i<=10; i++) {
				completedStatusDivs[task.id][i] = document.createElement('div');
				var tempValue = i*10;
				ss_setStyle(completedStatusDivs[task.id][i], tempValue, value);
	
				completedStatusDivs[task.id][i].title = completed["c" + tempValue];
				
				dojo.event.connect(completedStatusDivs[task.id][i], "onclick", ss_declare_saveValue(that, task, container, "c" + tempValue));
				dojo.event.connect(completedStatusDivs[task.id][i], "onmouseover", ss_declare_changeValue(that, task, "c" + tempValue));
		
				container.appendChild(completedStatusDivs[task.id][i]);
			}
			
			completedStatusDivs[task.id][11] = document.createElement('div');
			completedStatusDivs[task.id][11].innerHTML = completed[task.completed];
			dojo.html.setClass(completedStatusDivs[task.id][11], "ss_bar_status");
			parent.appendChild(completedStatusDivs[task.id][11]);
		}
		
		function ss_setStyle(obj, tempValue, borderValue) {
			borderValueT = borderValue.replace("c", "");
			if (tempValue <= borderValueT && borderValueT != 0 ) {
				dojo.html.setClass(obj, "ss_bar_on");
			} else {
				dojo.html.setClass(obj, "ss_bar_off");
			}
		}
		
		function ss_declare_changeValue(obj, task, value) {
			return function(evt) { obj.ss_changeValue(task, value);}
		}
		
		function ss_declare_saveValue(obj, task, container, newValue) {
			return function(evt) {obj.ss_saveValue(task, container, newValue);}
		}
		
		this.ss_saveValue = function(task, container, newValue) {
			dojo.event.connect(container, "onmouseout", ss_declare_changeValue(that, task, newValue));
			completedStatusDivs[task.id][11].innerHTML = completed[newValue];
			that.changeCompleted(task.id, newValue);
		}
		
		this.ss_changeValue = function(task, newValue) {
			for (var i=0; i<=10; i++) {
				var temp = i*10;
				ss_setStyle(completedStatusDivs[task.id][i], temp, newValue);
			}
			completedStatusDivs[task.id][11].innerHTML = completed[newValue];
		}
		
		function createCompletedTD(task) {
			var tdObj = document.createElement('td');
			
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
			for (var i in task.assigned) {
				var liObj = document.createElement('li');
				liObj.appendChild(document.createTextNode(task.assigned[i]));
				ulObj.appendChild(liObj);
			}
			return tdObj;
		}
		
		function createStatusTD(task) {
			var tdObj = document.createElement('td');
			dojo.html.setClass(tdObj, "iconsContainer");
			for (var i in statuses) {
				var hrefObj = document.createElement('a');
				hrefObj.href = "javascript: // ;";
				
				if (task.status != statuses[i].key) {
					dojo.html.setClass(hrefObj, "ss_taskStatus ss_taskStatus_" + statuses[i].key + "_u");
				} else {
					dojo.html.setClass(hrefObj, "ss_taskStatus");			
				}
					    
			    function declareChangeStatus(obj, taskId, newStatus) {		
					return function() {
						obj.changeStatus(taskId, newStatus);
					}
				}
				var callChangeStatus = declareChangeStatus(that, task.id, statuses[i].key);
				dojo.event.connect(hrefObj, "onclick", callChangeStatus);
				
				var imgObj= document.createElement('img');
				if (task.status == statuses[i].key) {
					var src = ss_imagesPath + "icons/status_" + statuses[i].key + ".gif"; 
				} else {
					var src = ss_imagesPath + "pics/1pix.gif";
				}
				imgObj.src = src;
				imgObj.setAttribute("alt", statuses[i].value);
				imgObj.setAttribute("title", statuses[i].value);
				
				hrefObj.appendChild(imgObj);
				
				
				tdObj.appendChild(hrefObj);
			}
			return tdObj;
		}
		
		function createPriorityTD(task) {
			var tdObj = document.createElement('td');
			dojo.html.setClass(tdObj, "iconsContainer");
				
			for (var i in priorities) {
				var hrefObj = document.createElement('a');
				hrefObj.href = "javascript: // ;";
		    
		    	if (task.priority != priorities[i].key) {
		    		dojo.html.setClass(hrefObj, "ss_taskPriority ss_taskPriority_" + priorities[i].key + "_u");
				} else {
					dojo.html.setClass(hrefObj, "ss_taskPriority");			
				}
				
			    function declareChangePriority(obj, taskId, newPriority) {		
					return function() {
						obj.changePriority(taskId, newPriority);
					}
				}
				var callChangePriority = declareChangePriority(that, task.id, priorities[i].key);
				dojo.event.connect(hrefObj, "onclick", callChangePriority);
				
				var imgObj= document.createElement('img');
				
				if (task.priority == priorities[i].key) {
					var src = ss_imagesPath + "icons/prio_" + priorities[i].key + ".gif"; 
				} else {
					var src = ss_imagesPath + "pics/1pix.gif";
				}
				imgObj.src = src;
				imgObj.setAttribute("alt", priorities[i].value);
				imgObj.setAttribute("title", priorities[i].value);
	
				
				hrefObj.appendChild(imgObj);
				tdObj.appendChild(hrefObj);
			
			}
					
			return tdObj;
		}
		
		function createDueDateTD(task) {
			var tdObj = document.createElement('td');
			if (task.status == "completed" || task.status == "cancelled") {
				dojo.html.setClass(tdObj, "ss_task_completed");
			}
			
			tdObj.appendChild(document.createTextNode(task.dueDate));
			return tdObj;
		}
		
		function createTitleTD(task) {
			var tdObj = document.createElement('td');
			if (task.status == "completed" || task.status == "cancelled") {
				dojo.html.setClass(tdObj, "ss_task_completed");
			}
			
			var hrefObj = document.createElement('a');
			hrefObj.href = "javascript: // ;";
		    
		    function declareViewTask(obj, taskId) {		
				return function() {
					obj.viewTask(taskId);
				}
			}
			var callViewTask = declareViewTask(that, task.id);
			dojo.event.connect(hrefObj, "onclick", callViewTask);
			
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
			var url = ss_viewEntryURL + "&binderId=" + binderId;
			url += "&entryId=" + entryId;
			ss_loadEntryUrl(url, entryId);
		}
	
		this.filterTasks = function(type) {
			filterType = type;
			loadTasks();
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
			if (statusObj.options[statusObj.selectedIndex].value == "completed") {
				completedObj.selectedIndex = 10;
			}
		}
		
		// new status needs action or in process and current completed 100% => completed 90%
		if (changed == "status") {
			if ((statusObj.options[statusObj.selectedIndex].value == "needsAction" || statusObj.options[statusObj.selectedIndex].value == "inProcess") &&
					completedObj.options[completedObj.selectedIndex].value == "c100") {
				completedObj.selectedIndex = 9;
			}
		}		
		
		// completed 0% => status needs action
		if (changed == "completed") {
			if (completedObj.options[completedObj.selectedIndex].value == "c0") {
				statusObj.selectedIndex = 0;
			}
		}		
		
		// completed 10% - 90% => status in process
		if (changed == "completed") {
			var completedValue = completedObj.options[completedObj.selectedIndex].value;
			if (completedValue == "c10" || completedValue == "c20" || completedValue == "c30" || 
					completedValue == "c40" || completedValue == "c50" || completedValue == "c60" || 
					completedValue == "c70" || completedValue == "c80" || completedValue == "c90") {
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
	
}
