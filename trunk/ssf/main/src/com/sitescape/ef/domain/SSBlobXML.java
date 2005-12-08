
package com.sitescape.ef.domain;

import java.beans.XMLDecoder;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;

/**
* @author Janet McCann
* This is an immutable object.  Its value cannot be changed.  This allows
* lazy value loading to work.  If not, the deepcopy method of a user type, would have
* to read the value for later comparision. 
*  
* Create new instancs to change values 
*/
public class SSBlobXML implements Serializable {
	   transient Blob blob;
	   Object value=null;
	    
	    public SSBlobXML(Blob blob) {
	        this.blob = blob;
	    }
	    public SSBlobXML(Object value) {
	        this.value = value;
	    }
	    
	   public Object getValue() {
            if (value != null) return value;
            if (blob == null) {return null;}
 
    	    try {
    		    InputStream ois = blob.getBinaryStream();
    		    if (ois == null) return null;
    		    XMLDecoder d = new XMLDecoder(ois);
    		    try {
    		        return d.readObject();
    		    }
    		    finally {
    		        d.close();
    		    }

     	    } catch (Exception e) {
    	    	return null;
    	    }

    }
    public String toString() {
        return getValue().toString();
    }
}
