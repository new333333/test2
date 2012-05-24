package org.kablink.teaming.remoting.rest.v1.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.IdLinkPair;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:46 PM
 */
public class SearchResultBuilderUtil {
    public static <T> void buildSearchResultsTree(SearchResultTree<T> tree, Object rootId, SearchResultBuilder<T> builder, Map resultMap) {
        Map<Object, SearchResultTreeNode<T>> nodesById = new HashMap<Object, SearchResultTreeNode<T>>();
        nodesById.put(rootId, tree);
        List<Map> entries = (List<Map>)resultMap.get(ObjectKeys.SEARCH_ENTRIES);
        if (entries!=null) {
            for (Map entry : entries) {
                T obj = builder.build(entry);
                if (obj!=null) {
                    Object id = builder.getId(obj);
                    if (id.equals(rootId)) {
                        tree.setItem(obj);
                    } else {
                        nodesById.put(id, builder.factoryTreeNode(obj));
                    }
                }
            }
        }
        for (SearchResultTreeNode<T> node : nodesById.values()) {
            T item = node.getItem();
            if (item!=null) {
                SearchResultTreeNode<T> parentNode = nodesById.get(builder.getParentId(item));
                if (parentNode!=null) {
                    parentNode.addChild(node);
                }
            }
        }
    }

    public static <T> void buildSearchResults(SearchResultList<T> results, SearchResultBuilder<T> builder, Map resultMap) {
        buildSearchResults(results, builder, resultMap, null, 0);
    }

    public static <T> void buildSearchResults(SearchResultList<T> results, SearchResultBuilder<T> builder, Map resultMap, String nextUrl, int offset) {
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
}
