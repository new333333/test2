/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package com.sitescape.team.domain;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.sitescape.util.Validator;
/**
 * @hibernate.class table="SS_Definitions" dynamic-update="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Definition extends PersistentTimestampObject  {
    private String name="";
	private int type=FOLDER_ENTRY;
	private int visibility=PUBLIC;
    private byte[] xmlencoding;
    private Document doc;
    private Long zoneId;
    private String title="";
    private String iId;
    //type values
    //types 5 and 9 are used in naming processorKeys.  Kep model-processor-mappings.xml up to date
    public static final int FOLDER_ENTRY=1;
	public static final int WORKFLOW=2;
	public static final int REPORT=3;
	public static final int ENTRY_FILTER=4;
	public static final int FOLDER_VIEW=5;
	public static final int PROFILE_VIEW=6;
	public static final int PROFILE_ENTRY_VIEW=7;
	public static final int WORKSPACE_VIEW=8;
	public static final int PROFILE_GROUP_VIEW=11;
	public static final int USER_WORKSPACE_VIEW=12;
	
	public static final String VIEW_STYLE_DEFAULT="folder"; 
	public static final String VIEW_STYLE_TABLE="table"; 
	public static final String VIEW_STYLE_WIKI="wiki"; 
	public static final String VIEW_STYLE_CALENDAR="event"; 
	public static final String VIEW_STYLE_BLOG="blog";
	public static final String VIEW_STYLE_GUESTBOOK="guestbook"; 
	public static final String VIEW_STYLE_PHOTO_ALBUM="photo"; 
	public static final String VIEW_STYLE_FILE="file"; 
	
	//visibility values
	public static final int PUBLIC=1;
	public static final int LOCAL=2;
	public static final int PERSONAL=3;
	
	protected static final Log logger = LogFactory.getLog(Definition.class);

	public Definition() {
		
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
    	if (Validator.isNull(title)) return name;
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
     * @hibernate.property not-null="true"
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
    
    /**
     * Internal id used to identify default definitions.  This id plus
     * the zoneId are used to locate default definitions.  If we just used the primary key id
     * the zones would need the same default and that may not be desirable.
     * @hibernate.property length="32"
     */
    public String getInternalId() {
    	return this.iId;
    }
    public void setInternalId(String iId) {
    	this.iId = iId;
    }
    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobByteArrayType"
     */
    protected byte[] getEncoding() {
        return xmlencoding;
    }
 
    protected void setEncoding(byte[] definition) {
 
        xmlencoding=definition;
    }
    /**
     * Changes to this object will not be reflected in the database until
     * a setDefinition call is made.
     * @return
     */
    public Document getDefinition() {
    	if (doc != null) return doc;
    	try {
    		InputStream ois = new ByteArrayInputStream(xmlencoding);
    		if (ois == null) return null;
    		SAXReader xIn = new SAXReader();
    		doc = xIn.read(ois);   
    		ois.close();
    	} catch (Exception fe) {
    		logger.error(fe.getLocalizedMessage(), fe);
    	}
        return doc;
    }
    public void setDefinition(Document doc) {
       	try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
       		if (baos == null) return;
    		XMLWriter xOut = new XMLWriter(baos);
    		xOut.write(doc);
    		xOut.close();
    		xmlencoding = baos.toByteArray(); 
    	} catch (Exception fe) {
    		logger.error(fe.getLocalizedMessage(), fe);
    	}
    	this.doc = doc;
    }

 
    public String toString() {
    	return name;
    }

    public Long getOwnerId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }

}
