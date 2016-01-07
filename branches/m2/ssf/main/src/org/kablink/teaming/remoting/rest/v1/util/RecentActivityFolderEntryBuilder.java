package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.rest.v1.model.RecentActivityEntry;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
public class RecentActivityFolderEntryBuilder extends BaseFolderEntryBriefBuilder implements SearchResultBuilder<RecentActivityEntry> {
    public RecentActivityFolderEntryBuilder() {
    }

    public RecentActivityFolderEntryBuilder(int descriptionFormat) {
        super(descriptionFormat);
    }

    public RecentActivityEntry build(Map entry) {
        RecentActivityEntry model = new RecentActivityEntry();
        populateBaseFolderEntryBrief(model, entry, Constants.BINDER_ID_FIELD);
        populateFileBrief(model, entry);
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public Object getId(RecentActivityEntry obj) {
        return obj.getId();
    }

    public Object getParentId(RecentActivityEntry obj) {
        return obj.getParentBinder().getId();
    }

    public SearchResultTreeNode<RecentActivityEntry> factoryTreeNode(RecentActivityEntry obj) {
        return null;
    }

    public Date getLastModified(RecentActivityEntry obj) {
        return obj.getModificationDate();
    }
}
