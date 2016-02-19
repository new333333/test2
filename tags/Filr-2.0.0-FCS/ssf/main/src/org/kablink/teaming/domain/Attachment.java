/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Sep 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;

import org.dom4j.Element;


/**
 * @hibernate.class table="SS_Attachments" discriminator-value="N" dynamic-update="true" lazy="false"
 * @hibernate.discriminator column="type" type="char"  
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 */
public abstract class Attachment extends PersistentTimestampObject 
	implements UpdateAttributeSupport {
 
    protected AnyOwner owner;
    protected String name;
    protected char type;
    protected String relevanceUUID;
    
    public Attachment() {
    }
    public Attachment(String name) {
    	this.name = name;
    }
    public Attachment(Attachment source) {
    	name = source.name;
    	type = source.type;
    }
    /**
     * @hibernate.property insert="false" update="false"
     * use in queries
     *
     */
    protected char getType() {
    	return type;
    }
    protected void setType(char type) {
    	this.type = type;
    }
    /**
     * @hibernate.component class="org.kablink.teaming.domain.AnyOwner"
     * @return
     */
    public AnyOwner getOwner() {
    	return owner;
    }
    public void setOwner(AnyOwner owner) {
    	this.owner = owner;
    } 
    public void setOwner(DefinableEntity entity) {
  		owner = new AnyOwner(entity);
  	}
    /**
     * Return the name of the custom attribute this attachment is associated with. 
     * May be <code>null</code>
     * @hibernate.property length="64"
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    } 	
    /**
     * Return the relevance UUID for this attachment.  May be 
     * <code>null</code> which implies the attachment has not been
     * added to the relevance search repository yet.
     * 
     * @hibernate.property length="256"
     * @return
     */
    public String getRelevanceUUID() {
        return relevanceUUID;
    }
    public void setRelevanceUUID(String relevanceUUID) {
        this.relevanceUUID = relevanceUUID;
    } 	
    /**
     * @override
     */
    public boolean update(Object obj) {
    	return false;
    	
    }
    /**
     * Add change log elements for this attachment
     * @param parent
     * @return
     */
    public abstract Element addChangeLog(Element parent); 
}
