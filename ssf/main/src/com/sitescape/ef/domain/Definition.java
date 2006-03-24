
package com.sitescape.ef.domain;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclSet;

/**
 * @hibernate.class table="SS_Definitions" dynamic-update="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Definition extends PersistentTimestampObject implements AclControlled {
    private String name="";
	private int type=COMMAND;
	private int visibility=PUBLIC;
    private byte[] xmlencoding;
    private Document doc;
    private String zoneName;
    private PersistentAclSet aclSet; //initialized by hiberate access=field
    private boolean inheritAclFromParent = false;
    private String title="";

    //type values
    public static int COMMAND=1;
	public static int WORKFLOW=2;
	public static int REPORT=3;
	public static int ENTRY_FILTER=4;
	public static int FORUM_VIEW=5;
	public static int PROFILE_VIEW=6;
	public static int PROFILE_ENTRY_VIEW=7;
	public static int WORKSPACE_VIEW=8;
	
	//visibility values
	public static int PUBLIC=1;
	public static int LOCAL=2;
	public static int PERSONAL=3;
	
	public Definition() {
		
	}    

     /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     * @hibernate.component prefix="acl_" class="com.sitescape.ef.domain.PersistentAclSet" 
     */
    public void setAclSet(AclSet aclSet) {
        this.aclSet = (PersistentAclSet)aclSet;
    }
    /**
     * Used by security manager only. Application should NEVER invoke this
     * method directly.  
     */
    public AclSet getAclSet() {
        return aclSet;
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
    public String getZoneName() {
    	return this.zoneName;
    }
    public void setZoneName(String id) {
    	this.zoneName = id;
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
    	return zoneName + ":" + name;
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
    
    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }

}
