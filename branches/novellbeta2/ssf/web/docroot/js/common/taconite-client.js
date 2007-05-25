/**
    @fileoverview
    This JavaScript file represents the core browser-side functionality
    supplied by Taconite. In general, the tools in this file wrap an instance
    of XMLHttpRequest object and provide utility methods for gather data from
    form elements to be sent to the server as par of an Ajax request.
*/

/**
    Constructor for the AjaxRequest class. 

    <br><br>
    Example:

    <br><br>
    var ajaxRequest = new ss_AjaxRequest("YOUR_URL");

    @class The AjaxRequest object wraps an instance of XMLHttpRequest and provides 
    facilities for setting functions that are called before a request is made
    and after a request returns. By default, AjaxRequest handles the server
    response by simply calling eval(), passing to it the responseText from 
    the XMLHttpRequestObject, of course assuming that the response was 
    generated by Taconite on the server side and that running eval() will 
    update the web page.<br><br>Example Usage:<br><br>var ajaxRequest = new ss_AjaxRequest("YOUR_URL");
    <br>ajaxRequest.addFormElements("form_element_id_attribute_value");
    <br>ajaxRequest.sendRequest();

    @constructor
    @param {String} a String repesenting the URL to which the Ajax request
    will be sent.
*/
function ss_AjaxRequest(url) {
    /** @private */
    var self = this;

    /** @private */
    var xmlHttp = ss_createXMLHttpRequest();
    
    /** @private */
    var queryString = "";

    /** @private */
    var requestURL = url;

    /** @private */
    var method = "GET";

    /** @private */
    var preRequest = null;

    /** @private */
    var postRequest = null;

    /** @private - local edit by SiteScape */
    var objectData = new Array();

    /** @private */
    var debugResponse = false;
	
    /** @private */
    var async = true;

    /** @private errorHandler*/ 
    var errorHandler = null;


    /**
        Return the instance of the XMLHttpRequest object wrapped by this object.
        @return XMLHttpRequest
    */
    this.getXMLHttpRequestObject = function() {
        return xmlHttp;
    }

    /**
        Set the pre-request function. This function will be called prior to 
        sending the Ajax request. The pre-request function is passed a reference
        to this object.
        @param {Function} The function to be called prior to sending the Ajax
        request. The function is passed a refernce of this object.
    */
    this.setPreRequest = function(func) {
        preRequest = func;
    }

    /**
        Set the post-request function. This function will be called after the
        response has been received and after eval() has been called using the 
        XMLHttpRequest object's responseText. The post-request function is passed 
        a reference to this object.
        @param {Function} The function to be called after receiving the Ajax
        response. The function is passed a refernce of this object.
    */
    this.setPostRequest = function(func) {
        postRequest = func;
    }

    /**
        Local edit by SiteScape (setData and getData)
        Set and get object local data. These routines are used to store information 
        inside the ajaxRequest object such that it can be used by the 
        pre and post processor routines.
    */
    this.setData = function(key, value) {
        objectData[key] = value;
    }
    this.getData = function(key) {
        return objectData[key];
    }

    /**
        Send the Ajax request using the POST method. Use with caution -- some
        browsers do not support the POST method with the XMLHttpRequest object.
    */
    this.setUsePOST = function() {
        method = "POST";
    }

    /**
        Send the Ajax request using the GET method, where parameters are sent
        as a query string appended to the URL. This is the default behavior.
    */
    this.setUseGET = function() {
        method = "GET";
    }

    /**
        Enable client-side debugging.  The server's response will be written
        to a text area appended to the bottom of the page.  If parsing is
        performed on the client side, then the results of the parsing operations
        are shown in their own text areas.
    */
    this.setEchoDebugInfo = function() {
        debugResponse = true;
    }

    /**
        Indicate if debugging is enabled.
        @return boolean
    */
    this.isEchoDebugInfo = function() {
        return debugResponse;
    }

    /**
        Set the query string that will be sent to the server. For GET
        requests, the query string is appended to the URL. For POST
        requests, the query string is sent in the request body. This 
        method is useful, for example, if you want to send an XML string
        or JSON string to the server.
        @param {String} qa, the new query string value.
    */
    this.setQueryString = function(qs) {
        queryString = qs;
    }

    /**
        Return the query string.
        @return The query string.
    */
    this.getQueryString = function() {
        return queryString;
    }

    /** 
        @param {Boolean} asyncBoolean, set to true if asynchronous request, false synchronous request. 
    */
    this.setAsync = function(asyncBoolean){
            async = asyncBoolean;
    }

    /** 
        @param {Function} Set the error handler function that is called if the 
        server's HTTP response code is something other than 200.
    */	
    this.setErrorHandler = function(func){
        errorHandler = func;
    }
	
    /**
        Add all of the form elements under the specified form to the query
        string to be sent to the server as part of the Ajax request. The values
        are automatically encoded.
        @param {String} formID, the value of the id attribute of the form from
        which you wish to accumulate the form values.
    */
    this.addFormElements = function(formID) {
        var formElements = document.getElementById(formID).elements;
        var values = toQueryString(formElements);
        accumulateQueryString(values);
    }

    /** @private */
    function accumulateQueryString(newValues) {
        if(queryString == "") {
            queryString = newValues; 
        }
        else {
            queryString = queryString + "&" +  newValues;
        }
    }


    

   /**
        Same as addNamedFormElements, except it will filter form elements by form's id.
        For example, these are all valid uses:<br>
        <br>ajaxRequest.addNamedFormElements("form-id""element-name-1");
        <br>ajaxRequest.addNamedFormElements("form-id","element-name-1",
        "element-name-2", "element-name-3");
    */
    this.addNamedFormElementsByFormID = function() {
        var elementName = "";
        var namedElements = null;

        for(var i = 1; i < arguments.length; i++) {
            elementName = arguments[i];
            namedElements = document.getElementsByName(elementName);
            var arNamedElements = new Array();
            for(j = 0; j < namedElements.length; j++) {
                if(namedElements[j].form  && namedElements[j].form.getAttribute("id") == arguments[0]){
                    arNamedElements.push(namedElements[j]);				
                }
            }
            if(arNamedElements.length > 0){
                elementValues = toQueryString(arNamedElements);
	        accumulateQueryString(elementValues);
            }
        }
    }








    /**
        Add the values of the named form elements to the query string to be
        sent to the server as part of the Ajax request. This method takes any 
        number of Strings representing the form elements for wish you wish to 
        accumulate the values. The Strings must be the value of the element's 
        name attribute.<br><br>For example, these are all valid uses:<br>
        <br>ajaxRequest.addNamedFormElements("element-name-1");
        <br>ajaxRequest.addNamedFormElements("element-name-1", "element-name-2", "element-name-3");
    */
    this.addNamedFormElements = function() {
        var elementName = "";
        var namedElements = null;

        for(var i = 0; i < arguments.length; i++) {
            elementName = arguments[i];
            namedElements = document.getElementsByName(elementName);

            elementValues = toQueryString(namedElements);

            accumulateQueryString(elementValues);
        }

    }

    /**
        Add the values of the id'd form elements to the query string to be
        sent to the server as part of the Ajax request. This method takes any 
        number of Strings representing the ids of the form elements for wish you wish to 
        accumulate the values. The Strings must be the value of the element's 
        name attribute.<br><br>For example, these are all valid uses:<br>
        <br>ajaxRequest.addFormElementsById("element-id-1");
        <br>ajaxRequest.addFormElementsById("element-id-1", "element-id-2", "element-id-3");
    */
    this.addFormElementsById = function() {
        var id = "";
        var element = null;
        var elements = new Array();

        for(var h = 0; h < arguments.length; h++) {
            element = document.getElementById(arguments[h]);
            if(element != null) {
                elements[h] = element;
            }
        }

        elementValues = toQueryString(elements);
        accumulateQueryString(elementValues);
    }
    
    /**
        Add a key/value pair to the query string to be
        sent to the server as part of the Ajax request.
    */
    this.addKeyValue = function(key, value) {
    	var tempString = key + "=" + encodeURIComponent(value)
    	accumulateQueryString(tempString);
    }

    /**
        Send the Ajax request.
    */
    this.sendRequest = function() {
        if(preRequest) {
            preRequest(self);
        }

        var obj = this;

        xmlHttp.onreadystatechange = function () { handleStateChange(self) };

        if(requestURL.indexOf("?") > 0) {
            requestURL = requestURL + "&ts=" + new Date().getTime();
        }
        else {
            requestURL = requestURL + "?ts=" + new Date().getTime();
        }

        if(method == "GET") {
            if(queryString.length > 0) {
                requestURL = requestURL + "&" + queryString;
            }
            xmlHttp.open(method, requestURL, true);
            xmlHttp.send(null);
        }
        else {
            //Fix a bug in Firefox when posting
            if (xmlHttp.overrideMimeType) {
                //SiteScape turned this off. It appears to break FireFox, not fix it.
                //xmlHttp.setRequestHeader("Connection", "close");
            }
            xmlHttp.open(method, requestURL, true);
            xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded"); 
            xmlHttp.send(queryString);
        }

        if(!async) {  //synchronous request, handle the state change
            handleStateChange(self);
        }

        if(self.isEchoDebugInfo()) {
            echoRequestParams();
        }
    }

    handleStateChange = function(ajaxRequest) {
        if(ajaxRequest.getXMLHttpRequestObject().readyState != 4) {
            return;
        }
        try {
        	//Guard against errors if the page was unloaded in the middle of this operation
        	var status = ajaxRequest.getXMLHttpRequestObject().status
        }
        catch(e) {
        	return;
        }
        if(ajaxRequest.getXMLHttpRequestObject().status == 200) {

            var debug = ajaxRequest.isEchoDebugInfo();
            if(debug) {
                echoResponse(ajaxRequest);
            }

            var nodes = null;
            try {
            	nodes = ajaxRequest.getXMLHttpRequestObject().responseXML.documentElement.childNodes;
            }
        	catch(e) {}
            
            var parser = null;
            var parseInBrowser = "";
            if (nodes != null) {
	            for(var i = 0; i < nodes.length; i++) {
	                if(nodes[i].nodeType != 1 || !isTaconiteTag(nodes[i])) {
	                    continue;
	                }
	
	                parseInBrowser = nodes[i].getAttribute("parseInBrowser");
	                if(parseInBrowser == "true") {
	                    parser = new XhtmlToDOMParser(nodes[i]);
	                    parser.startParsing();
	                    var js = parser.getJavaScript();
	                    if(debug) {
	                        echoParsedJavaScript(js);
	                    }
	                    eval(parser.getJavaScript());
	                }
	                else {
	                    eval(nodes[i].firstChild.nodeValue);
	                }
	            }
	        }

            if(postRequest) {
                postRequest(ajaxRequest);
            }
        }
        else {
            if(errorHandler) {
                errorHandler(self);
            }
        }
    }

    /** @private */
    function isTaconiteTag(node) {
        return node.tagName.substring(0, 9) == "taconite-";
    }

    /** @private */
    function toQueryString(elements) {
        var node = null;
        var qs = "";
        var name = "";

        var tempString = "";
        for(var i = 0; i < elements.length; i++) {
            tempString = "";
            node = elements[i];
            name = node.getAttribute("name");

            //use id if name is null
            if (!name) {
            	name = node.getAttribute("id");
            }

            if(node.tagName.toLowerCase() == "input") {
                if(node.type.toLowerCase() == "radio" || node.type.toLowerCase() == "checkbox") {
                    if(node.checked) {
                        tempString = name + "=" + node.value;
                    }
                }

                if(node.type.toLowerCase() == "text" || node.type.toLowerCase() == "hidden") {
                    tempString = name + "=" + encodeURIComponent(node.value);
                }
            }
            else if(node.tagName.toLowerCase() == "select") {
                tempString = getSelectedOptions(node);
            }

            else if(node.tagName.toLowerCase() == "textarea") {
                tempString = name + "=" + encodeURIComponent(node.value);
            }

            if(tempString != "") {
                if(qs == "") {
                    qs = tempString;
                }
                else {
                    qs = qs + "&" + tempString;
                }
            }

        }

        return qs;

    }

    /** @private */
    function getSelectedOptions(select) {
        var options = select.options;
        var option = null;
        var qs = "";
        var tempString = "";

        for(var x = 0; x < options.length; x++) {
            tempString = "";
            option = options[x];

            if(option.selected) {
                tempString = select.name + "=" + option.value;
            }

            if(tempString != "") {
                if(qs == "") {
                    qs = tempString;
                }
                else {
                    qs = qs + "&" + tempString;
                }
            }
        }

        return qs;
    }

    /** @private */
    function echoResponse(ajaxRequest) {
        var echoTextArea = document.getElementById("debugResponse");
        if(echoTextArea == null) {
            echoTextArea = createDebugTextArea("Server Response:", "debugResponse");
        }
        var debugText = ajaxRequest.getXMLHttpRequestObject().status 
            + " " + ajaxRequest.getXMLHttpRequestObject().statusText + "\n\n\n";
        echoTextArea.value = debugText + ajaxRequest.getXMLHttpRequestObject().responseText;
    }

    /** @private */
    function echoParsedJavaScript(js) {
        var echoTextArea = document.getElementById("debugParsedJavaScript");
        if(echoTextArea == null) {
            var echoTextArea = createDebugTextArea("Parsed JavaScript (by JavaScript Parser):", "debugParsedJavaScript");
        }
        echoTextArea.value = js;
    }

    /** @private */
    function createDebugTextArea(label, id) {
        echoTextArea = document.createElement("textarea");
        echoTextArea.setAttribute("id", id);
        echoTextArea.setAttribute("rows", "15");
        echoTextArea.setAttribute("style", "width:100%");
        echoTextArea.style.cssText = "width:100%";
        echoTextArea.className = "ss_style"

        document.getElementsByTagName("body")[0].appendChild(document.createTextNode(label));
        document.getElementsByTagName("body")[0].appendChild(echoTextArea);
        return echoTextArea;
    }


    /** @private */
    function echoRequestParams() {
        var qsTextBox = document.getElementById("qsTextBox");
        if(qsTextBox == null) {
            qsTextBox = createDebugTextBox("Query String:", "qsTextBox");
        }
        qsTextBox.value = queryString;

        var urlTextBox = document.getElementById("urlTextBox");
        if(urlTextBox == null) {
            urlTextBox = createDebugTextBox("URL (Includes query string if GET request):", "urlTextBox");
        }
        urlTextBox.value = requestURL;
    }

    /** @private */
    function createDebugTextBox(label, id) {
        textBox = document.createElement("input");
        textBox.setAttribute("type", "text");
        textBox.setAttribute("id", id);
        textBox.setAttribute("style", "width:100%");
        textBox.style.cssText = "width:100%";
        textBox.className = "ss_style";

        document.getElementsByTagName("body")[0].appendChild(document.createTextNode(label));
        document.getElementsByTagName("body")[0].appendChild(textBox);
        return textBox;
    }


}

/**
    Create an instance of the XMLHttpRequest object, using the appropriate
    method for the type of browser in which this script is running. For Internet
    Explorer, it's an ActiveX object, for all others it's a native JavaScript
    object.
    @return an instance of the XMLHttpRequest object.
*/
function ss_createXMLHttpRequest() {
    var req = false;
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    }
    else if (window.ActiveXObject) {
       	try {
            req = new ActiveXObject("Msxml2.XMLHTTP");
      	}
        catch(e) {
            try {
                req = new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch(e) {
                req = false;
            }
        }
    }
    return req;
}
