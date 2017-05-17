package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Base class for FolderEntryBriefs, ReplyBriefs and PrincipalBriefs.  Brief objects
 * are typically returned in list results because they are more efficient to build than their full counterparts.
 */
public abstract class EntryBrief extends DefinableEntityBrief {
    protected String entryType;

    protected EntryBrief() {
        setDocType("entry");
    }

    protected EntryBrief(String link) {
        super(link);
        setDocType("entry");
    }

    protected EntryBrief(EntryBrief orig) {
        super(orig);
        this.entryType = orig.entryType;
    }

    /**
     * The type of entry.  Possible values are:
     * <ul>
     *     <li><code>entry</code></li>
     *     <li><code>reply</code></li>
     *     <li><code>user</code></li>
     *     <li><code>group</code></li>
     * </ul>
     */
    @XmlElement(name = "entry_type")
    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }
}
