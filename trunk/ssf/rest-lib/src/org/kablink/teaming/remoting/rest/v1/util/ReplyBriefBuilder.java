package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.ReplyBrief;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
public class ReplyBriefBuilder extends BaseFolderEntryBriefBuilder implements SearchResultBuilder<ReplyBrief> {
    public ReplyBriefBuilder() {
    }

    public ReplyBriefBuilder(int descriptionFormat) {
        super(descriptionFormat);
    }

    public ReplyBrief build(Map entry) {
        ReplyBrief model = new ReplyBrief();
        populateBaseFolderEntryBrief(model, entry, Constants.BINDER_ID_FIELD);

        Long topEntryId = SearchResultBuilderUtil.getLong(entry, Constants.ENTRY_TOP_ENTRY_ID_FIELD);
        if (topEntryId!=null) {
            model.setTopEntry(new LongIdLinkPair(topEntryId, LinkUriUtil.getFolderEntryLinkUri(topEntryId)));
            Long parentEntryId = SearchResultBuilderUtil.getLong(entry, Constants.ENTRY_PARENT_ID_FIELD);
            if (parentEntryId!=null) {
                if (parentEntryId.equals(topEntryId)) {
                    model.setParentEntry(model.getTopEntry());
                } else {
                    model.setParentEntry(new LongIdLinkPair(parentEntryId, LinkUriUtil.getReplyLinkUri(parentEntryId)));
                }
            }
        }

        LinkUriUtil.populateReplyLinks(model, model.getId());
        return model;
    }

    public Object getId(ReplyBrief obj) {
        return obj.getId();
    }

    public Object getParentId(ReplyBrief obj) {
        return obj.getParentBinder().getId();
    }

    public SearchResultTreeNode<ReplyBrief> factoryTreeNode(ReplyBrief obj) {
        return null;
    }

    public Date getLastModified(ReplyBrief obj) {
        return obj.getModificationDate();
    }
}
