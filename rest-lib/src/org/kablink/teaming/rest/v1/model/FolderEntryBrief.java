package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Minimal information about a folder entry.  FolderEntryBrief objects are typically returned in list results because they are
 * more efficient to build than full FolderEntry objects
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

    /**
     * Information about the primary file associated with this entry.
     */
    @XmlElement(name="primary_file")
    public FileBrief getPrimaryFile() {
        return primaryFile;
    }

    public void setPrimaryFile(FileBrief primaryFile) {
        this.primaryFile = primaryFile;
    }
}
