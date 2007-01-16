
package com.sitescape.ef.domain;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.shared.ChangeLogUtils;
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
    	if(owner != null)
    		setOwner(owner.getEntity());
    	else
    		super.setOwner(owner);
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
     * @hibernate.many-to-one class="com.sitescape.ef.domain.FileAttachment"
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
		Element element = parent.addElement("versionAttachment");
		element.addAttribute(ObjectKeys.XTAG_ID, getId());
		element.addAttribute(ObjectKeys.XTAG_FILE_PARENT, getParentAttachment().getId());

		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FILE_VERSION_NUMBER, Long.toString(getVersionNumber()));
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FILE_VERSION_NAME, getVersionName());
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
	
		if (creation != null) creation.addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (modification != null) modification.addChangeLog(element, ObjectKeys.XTAG_ENTITY_MODIFICATION);
		if (!parent.getName().equals("fileAttachment")) {
			//add additional information if logged along
			if (!Validator.isNull(getName())) element.addAttribute(ObjectKeys.XTAG_NAME, getName());
			
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FILE_NAME, getFileItem().getName());
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FILE_LENGTH, Long.toString(getFileItem().getLength()));
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_FILE_REPOSITORY, getRepositoryName());
			
		}
		return element;
    	
    }
}
