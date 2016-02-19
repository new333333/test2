package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
public class FolderEntryBriefBuilder extends BaseFolderEntryBriefBuilder implements SearchResultBuilder<FolderEntryBrief> {
    public FolderEntryBriefBuilder() {
    }

    public FolderEntryBriefBuilder(int descriptionFormat) {
        super(descriptionFormat);
    }

    public FolderEntryBrief build(Map entry) {
        FolderEntryBrief model = new FolderEntryBrief();
        populateBaseFolderEntryBrief(model, entry, Constants.BINDER_ID_FIELD);
        populateFileBrief(model, entry);
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public Object getId(FolderEntryBrief obj) {
        return obj.getId();
    }

    public Object getParentId(FolderEntryBrief obj) {
        return obj.getParentBinder().getId();
    }

    public SearchResultTreeNode<FolderEntryBrief> factoryTreeNode(FolderEntryBrief obj) {
        return null;
    }

    public Date getLastModified(FolderEntryBrief obj) {
        return obj.getModificationDate();
    }
}
