/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */
package org.kablink.teaming.rest.v1.model.admin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * The current sync status of a Net Folder.
 */
@XmlRootElement(name="net_folder_sync_status")
public class NetFolderSyncStatus {
    private String status;
    private String nodeIPAddress;
    private Date startDate;
    private Date endDate;
    private Boolean directoryOnly;
    private Boolean directoryEnumerationFailure;
    private Integer filesFound;
    private Integer filesAdded;
    private Integer filesExpunged;
    private Integer filesModified;
    private Integer filesWithModifiedACL;
    private Integer filesWithModifiedOwner;
    private Integer foldersFound;
    private Integer foldersAdded;
    private Integer foldersExpunged;
    private Integer foldersWithModifiedACL;
    private Integer foldersWithModifiedOwner;
    private Integer entriesExpunged;
    private Integer failures;
    private Integer foldersProcessed;

    /**
     * The status of the most recent Net Folder sync job.
     *
     * <p>Possible values are:
     * <ul>
     *     <li>none</li>
     *     <li>ready</li>
     *     <li>taken</li>
     *     <li>started</li>
     *     <li>stopped</li>
     *     <li>finished</li>
     *     <li>aborted</li>
     *     <li>canceled</li>
     *     <li>deleting</li>
     * </ul>
     * </p>
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * IP Address of the server node executing the most recent sync of the Net Folder.
     */
    @XmlElement(name="node_ip_address")
    public String getNodeIPAddress() {
        return nodeIPAddress;
    }

    public void setNodeIPAddress(String nodeIPAddress) {
        this.nodeIPAddress = nodeIPAddress;
    }

    /**
     * Date/time when the most recent sync began.
     */
    @XmlElement(name="start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Date/time when the most recent sync ended.
     */
    @XmlElement(name="end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Flag indicating whether the most recent sync only scanned directories or directories and files.
     */
    @XmlElement(name="directory_only")
    public Boolean getDirectoryOnly() {
        return directoryOnly;
    }

    public void setDirectoryOnly(Boolean directoryOnly) {
        this.directoryOnly = directoryOnly;
    }

    /**
     * Flag indicating whether an error occurred attempting to enumerate a directory.
     */
    @XmlElement(name="directory_enumeration_failure")
    public Boolean getDirectoryEnumerationFailure() {
        return directoryEnumerationFailure;
    }

    public void setDirectoryEnumerationFailure(Boolean directoryEnumerationFailure) {
        this.directoryEnumerationFailure = directoryEnumerationFailure;
    }

    /**
     * The total number of files found.
     */
    @XmlElement(name="files_found")
    public Integer getFilesFound() {
        return filesFound;
    }

    public void setFilesFound(Integer filesFound) {
        this.filesFound = filesFound;
    }

    /**
     * The number of new files found.
     */
    @XmlElement(name="files_added")
    public Integer getFilesAdded() {
        return filesAdded;
    }

    public void setFilesAdded(Integer filesAdded) {
        this.filesAdded = filesAdded;
    }

    /**
     * The number of files that were removed because they no longer exist.
     */
    @XmlElement(name="files_expunged")
    public Integer getFilesExpunged() {
        return filesExpunged;
    }

    public void setFilesExpunged(Integer filesExpunged) {
        this.filesExpunged = filesExpunged;
    }

    /**
     * The number of updated files found.
     */
    @XmlElement(name="files_modified")
    public Integer getFilesModified() {
        return filesModified;
    }

    public void setFilesModified(Integer filesModified) {
        this.filesModified = filesModified;
    }

    /**
     * The number of files with modified ACLs.
     */
    @XmlElement(name="files_with_modified_acl")
    public Integer getFilesWithModifiedACL() {
        return filesWithModifiedACL;
    }

    public void setFilesWithModifiedACL(Integer filesWithModifiedACL) {
        this.filesWithModifiedACL = filesWithModifiedACL;
    }

    /**
     * The number of files with a different owner.
     */
    @XmlElement(name="files_with_modified_owner")
    public Integer getFilesWithModifiedOwner() {
        return filesWithModifiedOwner;
    }

    public void setFilesWithModifiedOwner(Integer filesWithModifiedOwner) {
        this.filesWithModifiedOwner = filesWithModifiedOwner;
    }

    /**
     * The total number of folders found.
     */
    @XmlElement(name="folders_found")
    public Integer getFoldersFound() {
        return foldersFound;
    }

    public void setFoldersFound(Integer foldersFound) {
        this.foldersFound = foldersFound;
    }

    /**
     * The number of new folders found.
     */
    @XmlElement(name="folders_added")
    public Integer getFoldersAdded() {
        return foldersAdded;
    }

    public void setFoldersAdded(Integer foldersAdded) {
        this.foldersAdded = foldersAdded;
    }

    /**
     * The number of folders that were removed because they no longer exist.
     */
    @XmlElement(name="folders_expunged")
    public Integer getFoldersExpunged() {
        return foldersExpunged;
    }

    public void setFoldersExpunged(Integer foldersExpunged) {
        this.foldersExpunged = foldersExpunged;
    }

    /**
     * The number of folders with modified ACLs.
     */
    @XmlElement(name="folders_with_modified_acl")
    public Integer getFoldersWithModifiedACL() {
        return foldersWithModifiedACL;
    }

    public void setFoldersWithModifiedACL(Integer foldersWithModifiedACL) {
        this.foldersWithModifiedACL = foldersWithModifiedACL;
    }

    /**
     * The number of folders with a different owner.
     */
    @XmlElement(name="folders_with_modified_owner")
    public Integer getFoldersWithModifiedOwner() {
        return foldersWithModifiedOwner;
    }

    public void setFoldersWithModifiedOwner(Integer foldersWithModifiedOwner) {
        this.foldersWithModifiedOwner = foldersWithModifiedOwner;
    }

    /**
     * The number of folder entries that were removed.
     */
    @XmlElement(name="entries_expunged")
    public Integer getEntriesExpunged() {
        return entriesExpunged;
    }

    public void setEntriesExpunged(Integer entriesExpunged) {
        this.entriesExpunged = entriesExpunged;
    }

    /**
     * The number of failures that occurred.
     */
    @XmlElement(name="failures")
    public Integer getFailures() {
        return failures;
    }

    public void setFailures(Integer failures) {
        this.failures = failures;
    }

    /**
     * The number of folders that were processed.
     */
    @XmlElement(name="folders_processed")
    public Integer getFoldersProcessed() {
        return foldersProcessed;
    }

    public void setFoldersProcessed(Integer foldersProcessed) {
        this.foldersProcessed = foldersProcessed;
    }
}
