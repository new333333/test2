package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.util.search.Constants;

import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
public class FolderEntryBriefBuilder implements SearchResultBuilder<FolderEntryBrief> {
    public FolderEntryBrief build(Map entry) {
        FolderEntryBrief model = new FolderEntryBrief();
        SearchResultBuilderUtil.populateDefinableEntityBrief(model, entry);
        model.setParentBinderId(SearchResultBuilderUtil.getLong(entry, Constants.BINDER_ID_FIELD));
        model.setDocNumber((String) entry.get(Constants.DOCNUMBER_FIELD));
        model.setDocLevel((new HKey((String) entry.get(Constants.SORTNUMBER_FIELD))).getLevel());
        model.setFileNames(getValueAsStringArray(entry.get(Constants.FILENAME_FIELD)));
        model.setLink(ResourceUtil.getFolderEntryLinkUri(model));
        return model;
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
