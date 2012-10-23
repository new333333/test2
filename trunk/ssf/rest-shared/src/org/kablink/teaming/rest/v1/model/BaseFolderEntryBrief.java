package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:39 PM
 */
public abstract class BaseFolderEntryBrief extends EntryBrief {
    private String docNumber;
    private Integer docLevel;
    private String[] fileNames;
    private Integer totalReplyCount;

    protected BaseFolderEntryBrief() {
    }

    protected BaseFolderEntryBrief(String link) {
        super(link);
    }

    protected BaseFolderEntryBrief(BaseFolderEntryBrief orig) {
        super(orig);
        this.docNumber = orig.docNumber;
        this.docLevel = orig.docLevel;
        this.fileNames = orig.fileNames;
        this.totalReplyCount = orig.totalReplyCount;
    }

    @XmlElement(name = "doc_level")
    public Integer getDocLevel() {
        return docLevel;
    }

    public void setDocLevel(Integer docLevel) {
        this.docLevel = docLevel;
    }

    @XmlElement(name = "doc_number")
    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    @XmlElementWrapper(name="file_names")
    @XmlElement(name="file_name")
    public String[] getFileNames() {
        return fileNames;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }

    @XmlElement(name = "total_reply_count")
    public Integer getTotalReplyCount() {
        return totalReplyCount;
    }

    public void setTotalReplyCount(Integer totalReplyCount) {
        this.totalReplyCount = totalReplyCount;
    }
}
