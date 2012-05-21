package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResults;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:46 PM
 */
public class SearchResultBuilderUtil {
    public static <T> void buildSearchResults(SearchResults<T> results, SearchResultBuilder<T> builder, Map resultMap) {
        buildSearchResults(results, builder, resultMap, null, 0);
    }

    public static <T> void buildSearchResults(SearchResults<T> results, SearchResultBuilder<T> builder, Map resultMap, String nextUrl, int offset) {
        results.setFirst(offset);
        results.setCount((Integer)resultMap.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED));
        results.setTotal((Integer)resultMap.get(ObjectKeys.TOTAL_SEARCH_COUNT));
        List<Map> entries = (List<Map>)resultMap.get(ObjectKeys.SEARCH_ENTRIES);
        if (entries!=null) {
            for (Map entry : entries) {
                T obj = builder.build(entry);
                if (obj!=null) {
                    results.append(obj);
                }
            }
        }
        if (nextUrl!=null) {
            offset = offset + results.getCount();
            if (offset<results.getTotal()) {
                results.setNext(nextUrl + "?first=" + offset + "&count=" + results.getCount());
            }
        }
    }

    public static void populateDefinableEntityBrief(DefinableEntityBrief model, Map entry) {
        String binderIdStr = (String) entry.get(Constants.DOCID_FIELD);
        Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;

        model.setId(binderId);
        model.setTitle((String) entry.get(Constants.TITLE_FIELD));
        model.setEntityType((String) entry.get(Constants.ENTITY_FIELD));
        model.setFamily((String) entry.get(Constants.FAMILY_FIELD));
        model.setDefinitionId((String) entry.get(Constants.COMMAND_DEFINITION_FIELD));
        model.setDefinitionType(Integer.valueOf((String) entry.get(Constants.DEFINITION_TYPE_FIELD)));

        Long creator = Long.valueOf((String) entry.get(Constants.CREATORID_FIELD));
        model.setCreation(
                new HistoryStamp(new PrincipalBrief(creator, ResourceUtil.getUserLinkUri(creator)),
                        (Date) entry.get(Constants.CREATION_DATE_FIELD)));

        Long modifier = Long.valueOf((String) entry.get(Constants.MODIFICATIONID_FIELD));
        model.setModification(
                new HistoryStamp(new PrincipalBrief(modifier, ResourceUtil.getUserLinkUri(modifier)),
                        (Date) entry.get(Constants.MODIFICATION_DATE_FIELD)));
    }

    public static Boolean getBoolean(Map entry, String fieldName) {
        Boolean value = null;
        String libraryStr = (String) entry.get(fieldName);
        if(Constants.TRUE.equals(libraryStr))
            value = Boolean.TRUE;
        else if(Constants.FALSE.equals(libraryStr))
            value = Boolean.FALSE;
        return value;
    }

    public static Long getLong(Map entry, String fieldName) {
        Long parentBinderId = null;
        String parentBinderIdStr = (String) entry.get(fieldName);
        if(Validator.isNotNull(parentBinderIdStr))
            parentBinderId = Long.valueOf(parentBinderIdStr);
        return parentBinderId;
    }
}
