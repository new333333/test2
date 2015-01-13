/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.util.cache.DefinitionCache;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @hibernate.class table="SS_Definitions" dynamic-update="true"
 * @hibernate.cache usage="nonstrict-read-write"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 *
 * @author ?
 */
public class Definition extends PersistentTimestampObject  {
    private String name="";
	private int type=FOLDER_ENTRY;
	private Integer visibility=VISIBILITY_PUBLIC;
    private byte[] xmlencoding;
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
	public static final int EXTERNAL_USER_WORKSPACE_VIEW=15;
	
	public static final String VIEW_STYLE_DEFAULT="folder"; 
	public static final String VIEW_STYLE_ACCESSIBLE="table"; 
	public static final String VIEW_STYLE_DISCUSSION="folder"; 
	public static final String VIEW_STYLE_DISCUSSION_WORKSPACE="discussion"; 
	public static final String VIEW_STYLE_PROJECT_WORKSPACE="project"; 
	public static final String VIEW_STYLE_TABLE="table"; 
	public static final String VIEW_STYLE_WIKI="wiki"; 
	public static final String VIEW_STYLE_CALENDAR="event"; 
	public static final String VIEW_STYLE_BLOG="blog";
	public static final String VIEW_STYLE_GUESTBOOK="guestbook"; 
	public static final String VIEW_STYLE_MILESTONE="milestone"; 
	public static final String VIEW_STYLE_MINIBLOG="miniblog"; 
	public static final String VIEW_STYLE_SURVEY="survey"; 
	public static final String VIEW_STYLE_TASK="task"; 
	public static final String VIEW_STYLE_PHOTO_ALBUM="photo"; 
	public static final String VIEW_STYLE_FILE="file"; 
	public static final String VIEW_STYLE_TEAM_ROOT="team_root";
	public static final String VIEW_STYLE_TEAM="team";
	
	//Family names
	public static final String FAMILY_BLOG="blog"; 
	public static final String FAMILY_CALENDAR="calendar"; 
	public static final String FAMILY_DISCUSSION="discussion"; 
	public static final String FAMILY_FILE="file"; 
	public static final String FAMILY_MILESTONE="milestone"; 
	public static final String FAMILY_MINIBLOG="miniblog"; 
	public static final String FAMILY_PHOTO="photo"; 
	public static final String FAMILY_SURVEY="survey"; 
	public static final String FAMILY_TASK="task"; 
	public static final String FAMILY_WIKI="wiki"; 
	public static final String FAMILY_WORKSPACE="workspace"; 
	public static final String FAMILY_TEAM="team"; 
	public static final String FAMILY_PROJECT="project"; 
	public static final String FAMILY_USER_WORKSPACE="user"; 
	public static final String FAMILY_USER_PROFILE="userProfile"; 
	public static final String FAMILY_EXTERNAL_USER_WORKSPACE="external_user"; 
	public static final String FAMILY_COMMENT="comment"; 
	public static final String FAMILY_FILE_COMMENT="fileComment"; 
	
	//visibility values
	public static final Integer VISIBILITY_PUBLIC=1; 
	public static final Integer VISIBILITY_DEPRECATED=3; //owning binder deleted; or marked obsolete
	//Values for jsp types
	public static final String JSP_STYLE_FORM="form";
	public static final String JSP_STYLE_VIEW="view";
	public static final String JSP_STYLE_TEMPLATE="template";
	public static final String JSP_STYLE_MOBILE="mobile";
	
	public static final String JSP_STYLE_DEFAULT="default"; //used only in definition_config file

	protected static final Log logger = LogFactory.getLog(Definition.class);
	protected Long binderId=ObjectKeys.RESERVED_BINDER_ID;
	public Definition() {
		
	}    
	/**
	 * Binder owner if binder level definition 
	 * @return
	 */
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
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
     * 
     * @hibernate.property 
     */
    public Integer getVisibility() {
    	return visibility;
    }
    public void setVisibility(Integer visibility) {
    	this.visibility = visibility;
    }    

    /**
     * @hibernate.property not-null="true"
     */
    @Override
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
    public Document getDefinition() {
    	return DefinitionCache.getDocumentWithDefinition(this);
    }
    
    /**
     * Get a document object with the intention of modifying it.
     * Unlike <code>getDefinition</code>, this method returns a private copy
     * of the definition that the caller can modify directly. Note that changes
     * to the document object will not be reflected in the database until
     * a <code>setDefinition</code> call is made.
     * @return
     */
    public Document getDefinitionForModificationPurpose() {
    	logger.debug("getDefinitionForModificationPurpose");
    	return getDocument();
    }
    
    @SuppressWarnings("unused")
	public void setDefinition(Document doc) {
		long startTime = System.nanoTime();
		if(DefinitionCache.isCachedDocument(getId(), doc))
			throw new InternalException("Bug: Application has directly modified shared cached definition document");
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
    	Utils.end(logger, startTime, "setDefinition", getTitle());
    }

    /**
     * Return the definition name.
     */
    @Override
    public String toString() {
    	return name;
    }

    /*
     * This method is specially designed only to be called from this class 
     * (and its subclasses) and the DefinitionCache class.
     * IMPORTANT: Don't ever make this method public!!!
     */
    @SuppressWarnings("unused")
	protected Document getDocument() {
		long startTime = System.nanoTime();
		Document doc = null;
    	try {
    		InputStream ois = new ByteArrayInputStream(xmlencoding);
    		if (ois == null) return null;
    		SAXReader xIn = XmlUtil.getSAXReader();
    		doc = xIn.read(ois);   
    		ois.close();
    	} catch (Exception fe) {
    		logger.error(fe.getLocalizedMessage(), fe);
    	}
    	Utils.end(logger, startTime, "getDocument", getTitle());
        return doc;
    }
}
