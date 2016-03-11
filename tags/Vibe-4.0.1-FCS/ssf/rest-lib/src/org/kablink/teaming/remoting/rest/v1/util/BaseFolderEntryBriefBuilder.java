package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.rest.v1.model.BaseFolderEntryBrief;
import org.kablink.teaming.rest.v1.model.FileBrief;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.util.search.Constants;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:45 PM
 */
abstract public class BaseFolderEntryBriefBuilder extends DefinableEntityBriefBuilder {
    public BaseFolderEntryBriefBuilder() {
    }

    public BaseFolderEntryBriefBuilder(int descriptionFormat) {
        super(descriptionFormat);
    }

    protected void populateFileBrief(FolderEntryBrief model, Map entry) {
        FileBrief fileModel = new FileBrief();
        fileModel.setId((String) entry.get(Constants.PRIMARY_FILE_ID_FIELD));
        if (fileModel.getId()!=null) {
            if (SearchResultBuilderUtil.getNumValues(entry, Constants.FILE_ID_FIELD)==1) {
                fileModel.setLength(SearchResultBuilderUtil.getLong(entry, Constants.FILE_SIZE_IN_BYTES_FIELD));
                fileModel.setMd5((String) entry.get(Constants.FILE_MD5_FIELD));
                Long millis = SearchResultBuilderUtil.getLong(entry, Constants.FILE_TIME_FIELD);
                if (millis != null) {
                    Calendar cal = new GregorianCalendar();
                    cal.setTimeInMillis(millis);
                    fileModel.setModifiedDate(cal);
                }
                fileModel.setName((String) entry.get(Constants.FILENAME_FIELD));
                fileModel.setVersion(SearchResultBuilderUtil.getLong(entry, Constants.FILE_VERSION_FIELD));
            }
            fileModel.setLink(LinkUriUtil.getFilePropertiesLinkUri(fileModel.getId()));
            model.setPrimaryFile(fileModel);
        }
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
