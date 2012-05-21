package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:36 PM
 */
@XmlRootElement
public class FolderEntryBrief extends EntryBrief {
   	private String docNumber;
   	private int docLevel;
    private String[] fileNames;

    @XmlElement(name = "doc_level")
    public int getDocLevel() {
        return docLevel;
    }

    public void setDocLevel(int docLevel) {
        this.docLevel = docLevel;
    }

    @XmlElement(name = "doc_number")
    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    @XmlElement(name = "file_names")
    public String[] getFileNames() {
        return fileNames;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }
}
