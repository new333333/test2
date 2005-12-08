
package com.sitescape.ef.domain;

import java.sql.Blob;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
/**
 * @author Janet McCann
 * This is an immutable object.  Its value cannot be changed.  This allows
 * lazy value loading to work.  If not, the deepcopy method of a user type, would have
 * to read the value for later comparision. 
 *  
 * Create new instancs to change values 
 */
public class SSBlobSerializable implements Serializable {
	   transient Blob blob;
	   Object value=null;
	    
	    public SSBlobSerializable(Blob blob) {
	        this.blob = blob;
	    }
	    public SSBlobSerializable(Object value) {
	        this.value = value;
	    }
	    
	   public Object getValue() {
            if (value != null) return value;
            if (blob == null) {return null;}
 
    	    try {
    	    	 InputStream is = blob.getBinaryStream();
    	    	 if (is == null) return null;
    	    	 ObjectInputStream ois = new ObjectInputStream(is);
    	    	 try {
    	    	 	return ois.readObject();
    	    	 }  catch (ClassNotFoundException ex) {
    	    	 	throw new IOException("Could not deserialize BLOB contents: " + ex.getMessage());
    	    	 } finally {
    	    	 	ois.close();
    	    	 }
    	    } catch (Exception e) {
    	    	return null;
    	    }

    }
    public String toString() {
        return getValue().toString();
    }
}
