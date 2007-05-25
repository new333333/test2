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
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.util.Validator;
/**
 * @hibernate.subclass discriminator-value="V" dynamic-update="true"
 * @author janet
 */
public class VersionAttachment extends FileAttachment {
    private int versionNumber=1;
    private String versionName;
    private FileAttachment parentAttachment;
    
    public VersionAttachment() {
    } 

    public void setOwner(AnyOwner owner) {
    	//this need to be included here since we are overloading.
    	//Hibernate calls the wrong one, it isn't present
    	this.owner = owner;
    } 
    
 	public void setOwner(DefinableEntity entry) {
		//don't set foreign key, so not read in to entry
  		owner = new AnyOwner(entry, false);
  	}

    /**
     * @hibernate.property 
     * @return
     */
    public int getVersionNumber() {
        return versionNumber;
    }
    public void setVersionNumber(int versionNumber) {
    	if (versionNumber == 0) throw new IllegalArgumentException("0 versionNumber");
    	this.versionNumber = versionNumber;
    }

    /**
     * @hibernate.property length="256"
     * @return
     */
    public String getVersionName() {
        return this.versionName;
    }
    public void setVersionName(String versionName) {
    	if (Validator.isNull(versionName)) throw new IllegalArgumentException("null versionName");
    	this.versionName = versionName;
    }
    /**
     * @hibernate.many-to-one class="com.sitescape.team.domain.FileAttachment"
     * @hibernate.column name="parentAttachment" sql-type="char(32)"
     */
    public FileAttachment getParentAttachment() {
        return parentAttachment;
    }
    public void setParentAttachment(FileAttachment parentAttachment) {
        this.parentAttachment = parentAttachment;
    }
    public String toString() {
    	return parentAttachment.toString() + ":" + versionNumber;
    }
	public Element addChangeLog(Element parent) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_VERSIONATTACHMENT);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_ID, getId());
		element.addAttribute(ObjectKeys.XTAG_FILE_PARENT, getParentAttachment().getId());

		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_VERSION_NUMBER, Long.toString(getVersionNumber()));
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_VERSION_NAME, getVersionName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
	
		if (creation != null) creation.addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (modification != null) modification.addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		if (!parent.getName().equals(ObjectKeys.XTAG_ELEMENT_TYPE_FILEATTACHMENT)) {
			//add additional information if logged along
			if (!Validator.isNull(getName())) element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getName());
			
			XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_NAME, getFileItem().getName());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_LENGTH, Long.toString(getFileItem().getLength()));
			XmlUtils.addProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
			
		}
		return element;
    	
    }
}
