package org.kablink.teaming.module.profile.impl;

import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.domain.UserProperties;

public class GuestProperties extends UserProperties implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8802227567104522088L;
    protected GuestProperties() {
    	super();
    	//for deserializing from session
    }

	public GuestProperties(UserProperties uProps) {
    	setId(uProps.getId());
    	//session access may be concurrent, so use synchronzied map
    	Map props = java.util.Collections.synchronizedMap(new HashMap());
    	props.putAll(uProps.getProperties());
    	setProperties(props);	    	
    }
}

