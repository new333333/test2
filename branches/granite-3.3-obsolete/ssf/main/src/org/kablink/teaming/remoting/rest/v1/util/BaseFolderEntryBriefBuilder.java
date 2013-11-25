package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.rest.v1.model.BaseFolderEntryBrief;
import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.EntryBrief;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.util.search.Constants;

import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
abstract public class BaseFolderEntryBriefBuilder extends DefinableEntityBriefBuilder {
    public BaseFolderEntryBriefBuilder() {
    }

    public BaseFolderEntryBriefBuilder(boolean textDescriptions) {
        super(textDescriptions);
    }

    protected void populateBaseFolderEntryBrief(BaseFolderEntryBrief model, Map entry, String parentBinderField) {
        super.populateDefinableEntityBrief(model, entry, parentBinderField);
        model.setEntryType((String) entry.get(Constants.ENTRY_TYPE_FIELD));
        model.setDocNumber((String) entry.get(Constants.DOCNUMBER_FIELD));
        String sortKey = (String) entry.get(Constants.SORTNUMBER_FIELD);
        if (sortKey!=null) {
            model.setDocLevel((new HKey(sortKey)).getLevel());
        }
        model.setFileNames(getValueAsStringArray(entry.get(Constants.FILENAME_FIELD)));

        model.setTotalReplyCount(SearchResultBuilderUtil.getInt(entry, Constants.TOTALREPLYCOUNT_FIELD));
    }

    private String[] getValueAsStringArray(Object value) {
   		if(value == null) {
   			return null;
   		}
   		else if(value instanceof String) {
   			if(!value.equals(""))
   				return new String[] {(String) value};
   			else
   				return null;
   		}
   		else if(value instanceof SearchFieldResult) {
   			return ((SearchFieldResult) value).getValueSet().toArray(new String[0]);
   		}
   		else {
   			return null;
   		}
   	}
}
