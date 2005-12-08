
package com.sitescape.ef.domain;
import java.io.FileWriter;
import java.io.FileInputStream;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclSet;

/**
 * @hibernate.class table="SS_Definitions" dynamic-update="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @author Janet McCann
 *
 */
public class Definition extends PersistentTimestampObject implements AclControlled {
    private String name="";
	private int type=COMMAND;
	private int visibility=PUBLIC;
    private byte[] xmlencoding;
    private boolean dirty=true;
    private Document doc;
    private String zoneId;
    private AclSet aclSet;
    private boolean inheritAclFromParent = false;
    private String title="";

    //type values
    public static int COMMAND=1;
	public static int WORKFLOW=2;
	public static int REPORT=3;
	public static int ENTRY_FILTER=4;
	public static int FORUM_VIEW=5;
	
	//visibility values
	public static int PUBLIC=1;
	public static int LOCAL=2;
	public static int PERSONAL=3;
	
	public Definition() {
		
	}
    
    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly. 
     * 
     * @hibernate.component prefix="acl_"
     */
    public AclSet getAclSet() {
        return aclSet;
    }

    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     */
    public void setAclSet(AclSet aclSet) {
        this.aclSet = aclSet;
    }	
    /**
     * @hibernate.property length="64"
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    /**
     * @hibernate.property length="128"
     */
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }
    /**
     * @hibernate.property 
     */
    public int getType() {
    	return type;
    }
    public void setType(int type) {
    	this.type = type;
    }
    
    /**
     * @hibernate.property 
     */
    public int getVisibility() {
    	return visibility;
    }
    public void setVisibility(int visibility) {
    	this.visibility = visibility;
    }    
    /**
     * @hibernate.property length="100" not-null="true"
     */
    public String getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(String id) {
    	this.zoneId = id;
    }
    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobByteArrayType"
     */
    protected byte[] getEncoding() {
        return xmlencoding;
    }
 
    protected void setEncoding(byte[] definition) {
 
   //     doc = ForumUtils.XmlDecodeByteArrayToDocument(definition);

        xmlencoding=definition;
    }
    /**
     * Changes to this object will not be reflected in the database until
     * a setDefinition call is made.
     * @return
     */
    public Document getDefinition() {
    	//temp - store in file till db settles down
    	try {
    		FileInputStream fIn = new FileInputStream("c:/ss/" + getName());
    		SAXReader xIn = new SAXReader();
    		doc = xIn.read(fIn);   
    		fIn.close();
    	} catch (Exception fe) {
    		fe.printStackTrace();
    	}
    	return doc;
    }
    public void setDefintion(Document doc) {
       	try {
    		FileWriter fOut = new FileWriter("c:/ss/" + getName());
    		XMLWriter xOut = new XMLWriter(fOut, OutputFormat.createPrettyPrint());
    		xOut.write(doc);
    		xOut.close();
    	
    	} catch (Exception fe) {
    		fe.printStackTrace();
    	}
    	this.doc = doc;
 //   	xmlencoding = ForumUtils.XmlEncodeObjectToByteArray(doc);
    }

 
    public String toString() {
    	return zoneId + ":" + name;
    }

    /**
     * @hibernate.property column="acl_inheritFromParent" not-null="true"
     */
    public boolean getInheritAclFromParent() {
        return inheritAclFromParent;
    }

    public void setInheritAclFromParent(boolean inherit) {
        this.inheritAclFromParent = inherit;
    }
}
