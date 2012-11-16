package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:46 PM
 */
public class SearchResultBuilderUtil {
    private static class MapObjectPair<T> {
        private Map entry;
        private T obj;

        private MapObjectPair(Map entry, T obj) {
            this.entry = entry;
            this.obj = obj;
        }
    }

    public static <T> void buildSearchResultsTree(AllModulesInjected ami, SearchResultTree<T> tree, Object rootId, ContainerSearchResultBuilder<T> builder, Map resultMap) {
        Map<Object, List<MapObjectPair<T>>> parentIds = new HashMap<Object, List<MapObjectPair<T>>>();
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
                        Object parentId = builder.getParentId(obj);
                        if (parentId!=null) {
                            List<MapObjectPair<T>> children = parentIds.get(parentId);
                            if (children==null) {
                                children = new ArrayList<MapObjectPair<T>>();
                                parentIds.put(parentId, children);
                            }
                            children.add(new MapObjectPair<T>(entry, obj));
                        }
                        nodesById.put(id, builder.factoryTreeNode(obj));
                    }
                }
            }
        }
        for (Object id : nodesById.keySet()) {
            parentIds.remove(id);
        }
        for (List<MapObjectPair<T>> children : parentIds.values()) {
            // Only get the first object...if there are multiple children in the list, their ancestry is the same
            // because they share the same parent.
            MapObjectPair<T> first = children.iterator().next();
            T [] parents = getAncestryFromEntryMap(ami, builder, builder.getId(first.obj), builder.getType(first.obj), first.entry);
            boolean foundRoot = false;
            for (T parent : parents) {
                Object id = builder.getId(parent);
                if (foundRoot && !nodesById.containsKey(id)) {
                    nodesById.put(id, builder.factoryTreeNode(parent));
                } else if (id.equals(rootId)) {
                    foundRoot = true;
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

    public static <T> void buildSearchResultsTree(SearchResultTree<T> tree, T [] rootObjects, ContainerSearchResultBuilder<T> builder, Map resultMap) {
        Map<Object, SearchResultTreeNode<T>> nodesById = new HashMap<Object, SearchResultTreeNode<T>>();
        for (T rootObj : rootObjects) {
            SearchResultTreeNode<T> node = builder.factoryTreeNode(rootObj);
            nodesById.put(builder.getId(rootObj), node);
            tree.addChild(node);
        }
        List<Map> entries = (List<Map>)resultMap.get(ObjectKeys.SEARCH_ENTRIES);
        if (entries!=null) {
            for (Map entry : entries) {
                T obj = builder.build(entry);
                if (obj!=null) {
                    Object id = builder.getId(obj);
                    if (!nodesById.containsKey(id)) {
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
        tree.cloneItems();
    }

    public static <T> void buildSearchResults(SearchResultList<T> results, SearchResultBuilder<T> builder, Map resultMap) {
        buildSearchResults(results, builder, resultMap, null, null, 0);
    }

    public static <T> void buildSearchResults(SearchResultList<T> results, SearchResultBuilder<T> builder, Map resultMap, String nextUrl, Map<String, Object> nextParams, int offset) {
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
        results.setNextIfNecessary(nextUrl, nextParams);
    }

    public static <T> T[] getAncestryFromEntryMap(AllModulesInjected ami, ContainerSearchResultBuilder<T> builder, Object id, EntityIdentifier.EntityType type, Map entry) {
        String idStr = id.toString();
        T[] binders = null;
        Criteria crit;
        Map resultMap;
        Set<String> binderIds = null;
        Object value = entry.get(Constants.ENTRY_ANCESTRY);
        if (value instanceof SearchFieldResult) {
            SearchFieldResult ancestry = (SearchFieldResult) entry.get(Constants.ENTRY_ANCESTRY);
            binderIds = ancestry.getValueSet();
        } else {
            binderIds = new HashSet<String>();
            binderIds.add((String)value);
        }
        int idCount = 0;
        Set<Long> longIds = new HashSet<Long>();
        Junction idJunction = Restrictions.disjunction();
        for (String binderId : binderIds) {
            if (!(type.isBinder() && binderId.equals(idStr))) {
                idJunction.add(Restrictions.eq(Constants.DOCID_FIELD, binderId));
                idCount++;
                try {
                    longIds.add(Long.parseLong(binderId));
                } catch (NumberFormatException e) {
                    // Ignore?
                }
            }
        }
        if (idCount>0) {
            Junction outer = Restrictions.conjunction();
            outer.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
            outer.add(idJunction);
            crit = new Criteria();
            crit.add(outer);
            resultMap = ami.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, -1);
            List<Map> entries = (List<Map>)resultMap.get(ObjectKeys.SEARCH_ENTRIES);
            List<T> binderList = new ArrayList<T>(entries.size());
            for (Map binderEntry : entries) {
                T binderBrief = builder.build(binderEntry);
                binderList.add(binderBrief);
                longIds.remove(builder.getId(binderBrief));
            }
            // There may be intermediate folders that the user doesn't have rights to view, so they are missing
            // from the search results.
            if (longIds.size()>0) {
                for (Long missingId : longIds) {
                    T missingObj = builder.lookup(ami, missingId);
                    if (missingObj!=null) {
                        binderList.add(missingObj);
                    }
                }
            }

            builder.sort(binderList);
            binders = binderList.toArray(builder.factoryArray(binderList.size()));
        }

        return binders;
    }

}
