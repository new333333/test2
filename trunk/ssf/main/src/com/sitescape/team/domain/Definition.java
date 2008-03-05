/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
 *
 */
public class Definition extends PersistentTimestampObject  {
    private String name="";
	private int type=FOLDER_ENTRY;
	private int visibility=PUBLIC;
    private byte[] xmlencoding;
    private Document doc;
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
	public static final int PROFILE_APPLICATION_VIEW=13;
	public static final int PROFILE_APPLICATION_GROUP_VIEW=14;
	
	public static final String VIEW_STYLE_DEFAULT="folder"; 
	public static final String VIEW_STYLE_TABLE="table"; 
	public static final String VIEW_STYLE_WIKI="wiki"; 
	public static final String VIEW_STYLE_CALENDAR="event"; 
	public static final String VIEW_STYLE_BLOG="blog";
	public static final String VIEW_STYLE_GUESTBOOK="guestbook"; 
	public static final String VIEW_STYLE_MILESTONE="milestone"; 
	public static final String VIEW_STYLE_TASK="task"; 
	public static final String VIEW_STYLE_PHOTO_ALBUM="photo"; 
	public static final String VIEW_STYLE_FILE="file"; 
	public static final String VIEW_STYLE_TEAM_ROOT="team_root";
	public static final String VIEW_STYLE_TEAM="team";
	//visibility values
	public static final int PUBLIC=1;
	public static final int LOCAL=2;
	public static final int PERSONAL=3;
	//Values for jsp types
	public static final String JSP_STYLE_FORM="form";
	public static final String JSP_STYLE_VIEW="view";
	public static final String JSP_STYLE_TEMPLATE="template";
	public static final String JSP_STYLE_MAIL="mail";
	public static final String JSP_STYLE_MOBILE="mobile";
	
	public static final String JSP_STYLE_DEFAULT="default"; //used only in definition_config file

	protected static final Log logger = LogFactory.getLog(Definition.class);

	public Definition() {
		
	}    


    /**
     * Unique name of definition.
     * @hibernate.property length="64"
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    /**
     * Title for definition.  
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
     * Return the definition type.
     * @hibernate.property 
     */
    public int getType() {
    	return type;
    }
    public void setType(int type) {
    	this.type = type;
    }
    
    /**
     * All are currently visible.
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
     * the zoneId are used to locate default definitions.  
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
     * Changes to the document object will not be reflected in the database until
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

    /**
     * Return the definition name.
     */
    public String toString() {
    	return name;
    }

}
