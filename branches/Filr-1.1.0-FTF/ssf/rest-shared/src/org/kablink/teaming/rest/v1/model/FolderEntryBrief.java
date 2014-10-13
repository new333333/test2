package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:36 PM
 */
@XmlRootElement
public class FolderEntryBrief extends BaseFolderEntryBrief {
    private FileBrief primaryFile;

    public FolderEntryBrief() {
        super();
    }

    protected FolderEntryBrief(FolderEntryBrief orig) {
        super(orig);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new FolderEntryBrief(this);
    }

    @XmlElement(name="primary_file")
    public FileBrief getPrimaryFile() {
        return primaryFile;
    }

    public void setPrimaryFile(FileBrief primaryFile) {
        this.primaryFile = primaryFile;
    }
}
