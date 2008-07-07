/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/** 
		FIXME: Write better docs.

		@author Brad Neuberg, bkn3@columbia.edu 
*/
dojo.provide("dojo.storage");

dojo.require("dojo.lang.*");
dojo.require("dojo.event.*");

// create an empty "StorageProvider", which was being created as a side-effect
//	of the erroneous dojo.provide("dojo.storage.StorageProvider")
dojo.storage.StorageProvider = {}


/** The base class for all storage providers. */

/** 
	 The constructor for a storage provider. You should avoid initialization
	 in the constructor; instead, define initialization in your initialize()
	 method. 
*/
dojo.declare("dojo.storage", null, {
	/** A put() call to a storage provider was succesful. */
	SUCCESS: "success",
	
	/** A put() call to a storage provider failed. */
	FAILED: "failed",
	
	/** A put() call to a storage provider is pending user approval. */
	PENDING: "pending",
	
	/** 
	  Returned by getMaximumSize() if this storage provider can not determine
	  the maximum amount of data it can support. 
	*/
	SIZE_NOT_AVAILABLE: "Size not available",
	
	/**
	  Returned by getMaximumSize() if this storage provider has no theoretical
	  limit on the amount of data it can store. 
	*/
	SIZE_NO_LIMIT: "No size limit",
	
	/** 
		The namespace for all storage operations. This is useful if several
		applications want access to the storage system from the same domain but
		want different storage silos. 
	*/
	"namespace": "dojoStorage",
	
	/**  
		If a function is assigned to this property, then when the settings
		provider's UI is closed this function is called. Useful, for example,
		if the user has just cleared out all storage for this provider using
		the settings UI, and you want to update your UI.
	*/
	onHideSettingsUI: null,

	initialize: function(){
		// summary: 
		//		Allows this storage provider to initialize itself. This is
		//		called after the page has finished loading, so you can not do
		//		document.writes(). 
		dojo.unimplemented("dojo.storage.initialize");
	},
	
	isAvailable: function(){ /*Boolean*/
		// summary: 
		//		Returns whether this storage provider is available on this
		//		platform. 
		dojo.unimplemented("dojo.storage.isAvailable");
	},
	
	/**

	*/
	put: function(	/*string*/ key,
					/*object*/ value, 
					/*function*/ resultsHandler){
		// summary:
		//		Puts a key and value into this storage system.
		// key:
		//		A string key to use when retrieving this value in the future.
		// value:
		//		A value to store; this can be any JavaScript type.
		// resultsHandler:
		//		A callback function that will receive three arguments. The
		//		first argument is one of three values: dojo.storage.SUCCESS,
		//		dojo.storage.FAILED, or dojo.storage.PENDING; these values
		//		determine how the put request went. In some storage systems
		//		users can deny a storage request, resulting in a
		//		dojo.storage.FAILED, while in other storage systems a storage
		//		request must wait for user approval, resulting in a
		//		dojo.storage.PENDING status until the request is either
		//		approved or denied, resulting in another call back with
		//		dojo.storage.SUCCESS. 
		//		The second argument in the call back is the key name that was being stored.
		//		The third argument in the call back is an optional message that
		//		details possible error messages that might have occurred during
		//		the storage process.

//	  Example:
//		var resultsHandler = function(status, key, message){
//		  alert("status="+status+", key="+key+", message="+message);
//		};
//		dojo.storage.put("test", "hello world", resultsHandler);
		dojo.unimplemented("dojo.storage.put");
	},

	get: function(/*string*/ key){ /*Object*/
		// summary:
		//		Gets the value with the given key. Returns null if this key is
		//		not in the storage system.
		// key:
		//		A string key to get the value of.
		// return: Returns any JavaScript object type; null if the key is not present
		dojo.unimplemented("dojo.storage.get");
	},

	hasKey: function(/*string*/ key){ /*Boolean*/
		// summary: Determines whether the storage has the given key. 
		return (this.get(key) != null);
	},

	/**
	
	getKeys: function(){ //Array
		// summary: Enumerates all of the available keys in this storage system.
		dojo.unimplemented("dojo.storage.getKeys");
	},

	*/
	clear: function(){
		// summary: 
		//		Completely clears this storage system of all of it's values and
		//		keys. 
		dojo.unimplemented("dojo.storage.clear");
	},
  
	/** Removes the given key from the storage system. */
	remove: function(key){
		dojo.unimplemented("dojo.storage.remove");
	},

	isPermanent: function(){ /*Boolean*/
		// summary:
		//		Returns whether this storage provider's values are persisted
		//		when this platform is shutdown. 
		dojo.unimplemented("dojo.storage.isPermanent");
	},

	/**
	  The maximum storage allowed by this provider.
	
	  @returns Returns the maximum storage size 
	           supported by this provider, in 
	           thousands of bytes (i.e., if it 
	           returns 60 then this means that 60K 
	           of storage is supported).
	    
	           If this provider can not determine 
	           it's maximum size, then 
	           dojo.storage.SIZE_NOT_AVAILABLE is 
	           returned; if there is no theoretical
	           limit on the amount of storage 
	           this provider can return, then
	           dojo.storage.SIZE_NO_LIMIT is 
	           returned
	*/
	getMaximumSize: function(){
		dojo.unimplemented("dojo.storage.getMaximumSize");
	},

	hasSettingsUI: function(){ /*Boolean*/
		// summary: Determines whether this provider has a settings UI.
		return false;
	},

	showSettingsUI: function(){
		// summary: If this provider has a settings UI, it is shown. 
		dojo.unimplemented("dojo.storage.showSettingsUI");
	},

	hideSettingsUI: function(){
		// summary: If this provider has a settings UI, hides it.
		dojo.unimplemented("dojo.storage.hideSettingsUI");
	},
	
	getType: function(){ /*String*/
		// summary:
		//		The provider name as a string, such as
		//		"dojo.storage.FlashStorageProvider". 
		dojo.unimplemented("dojo.storage.getType");
	},
	
	isValidKey: function(/*string*/ keyName){ /*Boolean*/
		// summary:
		//		Subclasses can call this to ensure that the key given is valid
		//		in a consistent way across different storage providers. We use
		//		the lowest common denominator for key values allowed: only
		//		letters, numbers, and underscores are allowed. No spaces. 
		if((keyName == null)||(typeof keyName == "undefined")){
			return false;
		}
			
		return /^[0-9A-Za-z_]*$/.test(keyName);
	}
});




/**
	Initializes the storage systems and figures out the best available 
	storage options on this platform.
*/
dojo.storage.manager = new function(){
	this.currentProvider = null;
	this.available = false;
	this.initialized = false;
	this.providers = [];
	
	// TODO: Provide a way for applications to override the default namespace
	this["namespace"] = "dojo.storage";
	
	this.initialize = function(){
		// summary: 
		//		Initializes the storage system and autodetects the best storage
		//		provider we can provide on this platform
		this.autodetect();
	};
	
	/**
	
	*/
	this.register = function(/*string*/ name, /*Object*/ instance) {
		// summary:
		//		Registers the existence of a new storage provider; used by
		//		subclasses to inform the manager of their existence. 
		// name:
		//		The full class name of this provider, such as
		//		"dojo.storage.browser.Flash6StorageProvider".
		// instance:
		//		An instance of this provider, which we will use to call
		//		isAvailable() on. 
		this.providers[this.providers.length] = instance;
		this.providers[name] = instance;
	};
	
	/**
	    
	*/
	this.setProvider = function(storageClass){
		// summary:
		//		Instructs the storageManager to use the given storage class for
		//		all storage requests.
		// description:
		//		Example:
		//			dojo.storage.setProvider(
		//				dojo.storage.browser.IEStorageProvider)
	
	};
	
	this.autodetect = function(){
		// summary:
		//		Autodetects the best possible persistent storage provider
		//		available on this platform. 
		if(this.initialized == true) // already finished
			return;
			
		// go through each provider, seeing if it can be used
		var providerToUse = null;
		for(var i = 0; i < this.providers.length; i++) {
			providerToUse = this.providers[i];
			if(providerToUse.isAvailable()){
				break;
			}
		}	
		
		if(providerToUse == null){ // no provider available
			this.initialized = true;
			this.available = false;
			this.currentProvider = null;
			dojo.raise("No storage provider found for this platform");
		}
			
		// create this provider and copy over it's properties
		this.currentProvider = providerToUse;
	  	for(var i in providerToUse){
	  		dojo.storage[i] = providerToUse[i];
		}
		dojo.storage.manager = this;
		
		// have the provider initialize itself
		dojo.storage.initialize();
		
		this.initialized = true;
		this.available = true;
	};
	
	this.isAvailable = function(){ /*Boolean*/
		// summary: Returns whether any storage options are available.
		return this.available;
	};
	
	this.isInitialized = function(){ /*Boolean*/
	 	// summary:
		//		Returns whether the storage system is initialized and ready to
		//		be used. 

		// FIXME: This should _really_ not be in here, but it fixes a bug
		if(dojo.flash.ready == false){
			return false;
		}else{
			return this.initialized;
		}
	};

	this.supportsProvider = function(/*string*/ storageClass){
		// summary: Determines if this platform supports the given storage provider.
		// description:
		//		Example:
		//			dojo.storage.manager.supportsProvider(
		//				"dojo.storage.browser.InternetExplorerStorageProvider");

		// construct this class dynamically
		try{
			// dynamically call the given providers class level isAvailable()
			// method
			var provider = eval("new " + storageClass + "()");
			var results = provider.isAvailable();
			if(results == null || typeof results == "undefined")
				return false;
			return results;
		}catch (exception){
			dojo.debug("exception="+exception);
			return false;
		}
	};

	/** Gets the current provider. */
	this.getProvider = function(){
		return this.currentProvider;
	};
	
	this.loaded = function(){
		// summary:
		//		The storage provider should call this method when it is loaded
		//		and ready to be used. Clients who will use the provider will
		//		connect to this method to know when they can use the storage
		//		system:
	};
};
