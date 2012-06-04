package org.kablink.teaming.remoting.rest.v1.util;

import org.dom4j.Element;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.util.search.Constants;

import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
public class FolderEntryBriefBuilder extends DefinableEntityBriefBuilder implements SearchResultBuilder<FolderEntryBrief> {
    public FolderEntryBrief build(Map entry) {
        FolderEntryBrief model = new FolderEntryBrief();
        populateDefinableEntityBrief(model, entry, Constants.BINDER_ID_FIELD);
        model.setDocNumber((String) entry.get(Constants.DOCNUMBER_FIELD));
        String sortKey = (String) entry.get(Constants.SORTNUMBER_FIELD);
        if (sortKey!=null) {
            model.setDocLevel((new HKey(sortKey)).getLevel());
        }
        model.setFileNames(getValueAsStringArray(entry.get(Constants.FILENAME_FIELD)));
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
