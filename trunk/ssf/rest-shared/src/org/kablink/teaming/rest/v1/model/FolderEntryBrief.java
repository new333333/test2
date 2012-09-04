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
public class FolderEntryBrief extends EntryBrief {
   	private String docNumber;
   	private Integer docLevel;
    private String[] fileNames;
    private LongIdLinkPair parentEntry;
    private LongIdLinkPair topEntry;

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

    @XmlElement(name="parent_entry")
    public LongIdLinkPair getParentEntry() {
        return parentEntry;
    }

    public void setParentEntry(LongIdLinkPair parentEntry) {
        this.parentEntry = parentEntry;
    }

    @XmlElement(name="top_entry")
    public LongIdLinkPair getTopEntry() {
        return topEntry;
    }

    public void setTopEntry(LongIdLinkPair topEntry) {
        this.topEntry = topEntry;
    }
}
