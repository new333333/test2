
package com.sitescape.ef.domain;
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
    	setOwner(owner.getEntry());
    } 
 	public void setOwner(Entry entry) {
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
}
