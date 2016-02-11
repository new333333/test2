/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.web.tree.DomTreeBuilder;

import net.sf.json.*;
import net.sf.json.util.*;

/**
 * @author hurley
 *
 * A Favorites object contains a sorted list of favorite forums and workspaces.
 * 
 */
public class Favorites {
   	public final static String FAVORITE_BINDER = "binder";
   	public final static String FAVORITE_ENTRY = "entry";
	private Document favorites = null;
	
	public Favorites() {
	}
	
	public Favorites(Document favoritesDoc) {
		this.favorites = favoritesDoc;
	}
	
	public Favorites(String xmlEncoding) {
		if (xmlEncoding == null) return;
		try {
			favorites = XmlUtil.parseText(xmlEncoding);
		} catch (Exception ex) {};
	}

	public String toString() {
		return getFavorites().asXML();
	}
	public Document addFavorite(String name, String hover, String type, String value, String action, String categoryId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		
		List list = root.elements();
		//Make sure this list doesn't get too large
		Integer maxNumberOfFavorites = SPropsUtil.getInt("maxNumberOfFavorites", ObjectKeys.MAX_NUMBER_OF_FAVORITES);
		boolean atSizeLimit = false;
		if (list.size() >= maxNumberOfFavorites) {
			atSizeLimit = true;
		}
			
		int id = Integer.parseInt((String)root.attributeValue("nextId"));
		
		Element newFavorite = null;
		if (categoryId.equals("")) {
			
			for (int i = 0; i < list.size(); i++) {
				if (((Element)list.get(i)).attributeValue("value") != null) {
					if (((Element)list.get(i)).attributeValue("value").equals(value)) {
						newFavorite = (Element)list.get(i);
						break;
					}
				}
			}
			if (newFavorite == null) {
				if (atSizeLimit) {
					throw new FavoritesLimitExceededException(
							new Exception(NLT.get("errorcode.entry.not.imported")));
				}
				newFavorite = root.addElement("favorite");
			}
		} else {
			
			Element category = (Element)root.selectSingleNode("//category[@id='"+categoryId+"']");
			if (category == null) category = root;
			
			newFavorite = category.addElement("favorite");
		}
		newFavorite.addAttribute("id", String.valueOf(id));
		newFavorite.addAttribute("name", name);
		newFavorite.addAttribute("hover", hover);
		newFavorite.addAttribute("type", type);
		newFavorite.addAttribute("value", value);
		newFavorite.addAttribute("action", action);
		newFavorite.addAttribute("image", "page");
		root.addAttribute("nextId", String.valueOf(++id));
		
		return this.favorites;
	}

	public Document addCategory(String name, String parentCategoryId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		int categoryId = Integer.parseInt((String)root.attributeValue("nextId"));
		Element newCategory = null;
		if (parentCategoryId.equals("")) {
			newCategory = root.addElement("category");
		} else {
			Element parentCategory = (Element)root.selectSingleNode("//category[@id='"+parentCategoryId+"']");
			if (parentCategory == null) parentCategory = root;
			newCategory = parentCategory.addElement("category");
		}
		newCategory.addAttribute("id", String.valueOf(categoryId));
		newCategory.addAttribute("name", name);
		newCategory.addAttribute("image", "folder");
		newCategory.addAttribute("displayOnly", "true");
		root.addAttribute("nextId", String.valueOf(++categoryId));
		
		return this.favorites;
	}
	
	public Document saveOrder(String deletedIdList, String newItemOrder) {
		//Nodes start with "ss_favorites_"
		String[] deletedIds = deletedIdList.split(" ");
		String[] itemIds = newItemOrder.split(" ");
		getFavorites();
		Element root = this.favorites.getRootElement();
		for (int i = 0; i < deletedIds.length; i++) {
			Node favorite = root.selectSingleNode("//favorite[@id='"+deletedIds[i]+"']");
			if (favorite != null) {
				favorite.getParent().remove(favorite);
			}
		}

		for (int i = 0; i < itemIds.length; i++) {
			Node favorite = root.selectSingleNode("//favorite[@id='"+itemIds[i]+"']");
			if (favorite != null) {
				favorite.detach();
				root.add(favorite);
			}
		}
		
		return this.favorites;
	}

	public Document saveOrderOld(String movedItemId, String newItemOrder) {
		//Nodes start with "ss_favorites_"
		String[] nodeData = movedItemId.substring(13).split("_");
		String movedId = nodeData[0];
		
		String[] itemIds = newItemOrder.split(" ");
		getFavorites();
		//Find the moved item, remembering its desired position (i.e., its immediate predecessor
		String predecessorId = "0";
		String nodeId = "";
		for (int i = 0; i < itemIds.length; i++) {
			//Nodes should start with "ss_favorites_" or "ss_delete"
			if (itemIds[i].equals("ss_delete")) {
				nodeId = "ss_delete";
			} else if (itemIds[i].length() <= 13) {
				continue;
			} else {
				nodeData = itemIds[i].substring(13).split("_");
				nodeId = nodeData[0];
				//String nodeType = nodeData[1];
				//String nodeValue = nodeData[2];
			}
			if (nodeId.equals(movedId)) {
				moveItem(movedId, predecessorId);
				break;
			}
			predecessorId = nodeId;
		}
		
		return this.favorites;
	}

	public Document moveItem(String id, String predecessorId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element item = (Element)root.selectSingleNode("//*[@id='"+id+"']");
		Element predecessor = null;
		if (predecessorId.equals("ss_delete")) {
			//Delete this item
			Element parentItem = item.getParent();
			parentItem.remove(item);
			return this.favorites;
		} else if (predecessorId.equals("")) {
			predecessor = root;
		} else {
			predecessor = (Element)root.selectSingleNode("//*[@id='"+predecessorId+"']");
		}
		if (item != null && predecessor != null) {
			String itemType = item.getName();
			String predecessorType = predecessor.getName();
			if (itemType.equals("favorite") && predecessorType.equals("favorite")) {
				//Moving one favorite next to another favorite
				//Get the category from the parent of the predecessor favorite
				String targetCategoryId = predecessor.getParent().attributeValue("id", "");
				//Make sure it is in the right category
				moveFavorite(id, targetCategoryId);
				//Then, get it into the right spot in the category
				shuffleItem(id, predecessorId);
			} else if (itemType.equals("favorite") && (predecessorType.equals("category") || predecessorType.equals("favorites"))) {
				//Moving a favorite into a category (placed at the top of the category)
				moveFavorite(id, predecessorId);
				shuffleItem(id, "");
			} else if (itemType.equals("category") && (predecessorType.equals("category") || predecessorType.equals("favorites"))) {
				//Moving a category
				moveCategory(id, predecessorId);
				shuffleItem(id, predecessorId);
			} else if (itemType.equals("category") && predecessorType.equals("favorite")) {
				//Moving a category (but predecessor is a favorite
				//Get the category from the parent of the predecessor favorite
				String targetCategoryId = predecessor.getParent().attributeValue("id", "");
				moveCategory(id, targetCategoryId);
				shuffleItem(id, predecessorId);
			}
		}
		return this.favorites;
	}

	public Document moveFavorite(String id, String targetCategoryId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element favorite = (Element)root.selectSingleNode("//favorite[@id='"+id+"']");
		if (favorite != null) {
			Element favoriteParent = favorite.getParent();
			Element newParentCategory = null;
			if (targetCategoryId.equals("") || targetCategoryId.equals("0")) {
				newParentCategory = root;
			} else {
				newParentCategory = (Element)root.selectSingleNode("//category[@id='"+targetCategoryId+"']");
			}
			if (newParentCategory != null && newParentCategory != favoriteParent) {
				if (favoriteParent.remove(favorite)) {
					newParentCategory.add(favorite);
				}
			}
		}
		return this.favorites;
	}

	public Document moveCategory(String id, String targetCategoryId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element category = (Element)root.selectSingleNode("//category[@id='"+id+"']");
		if (category != null) {
			Element categoryParent = category.getParent();
			Element newParentCategory = null;
			if (targetCategoryId.equals("") || targetCategoryId.equals("0")) {
				newParentCategory = root;
			} else {
				newParentCategory = (Element)root.selectSingleNode("//category[@id='"+targetCategoryId+"']");
			}
			if (newParentCategory != null && newParentCategory != category && 
					category.selectSingleNode("./category[@id='"+targetCategoryId+"']") == null) {
				if (categoryParent.remove(category)) {
					newParentCategory.add(category);
				}
			}
		}
		return this.favorites;
	}

	public Document shuffleItem(String id, String predecessorId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element item = (Element)root.selectSingleNode("//*[@id='"+id+"']");
		Element itemParent = item.getParent();
		if (item != null && predecessorId.equals("")) {
			//Put this item at the top of the parent category
			//Remove the item 
			itemParent.remove(item);
			List elements = itemParent.elements();
			//Put the item into the list at the proper place (just after the predecessor)
			elements.add(0, item);
			//Shuffle the elements into place by removing each then adding it back to the end of the list
			for (int j = 0; j < elements.size(); j++) {
				itemParent.remove((Element)elements.get(j));
				itemParent.add((Element)elements.get(j));
			}
		} else {
			Element predecessor = (Element)root.selectSingleNode("//*[@id='"+predecessorId+"']");
			if (item != null && predecessor != null) {
				Element predecessorParent = predecessor.getParent();
				//Make sure the two items share the same parent
				if (itemParent == predecessorParent) {
					//Remove the item 
					itemParent.remove(item);
					List elements = itemParent.elements();
					int i = elements.indexOf(predecessor);
					//Put the item into the list at the proper place (just after the predecessor)
					elements.add(i+1, item);
					//Shuffle the elements into place by removing each then adding it back to the end of the list
					for (int j = 0; j < elements.size(); j++) {
						itemParent.remove((Element)elements.get(j));
						itemParent.add((Element)elements.get(j));
					}
				}
			}
		}
		return this.favorites;
	}

	public Document deleteFavorite(String id) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element favorite = (Element)root.selectSingleNode("//favorite[@id='"+id+"']");
		if (favorite != null) {
			favorite.getParent().remove(favorite);
		}
		return this.favorites;
	}

	public Document deleteCategory(String id) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element category = (Element)root.selectSingleNode("//category[@id='"+id+"']");
		if (category != null) {
			category.getParent().remove(category);
		}
		return this.favorites;
	}

	public Document getFavorites() {
		if (this.favorites == null) {
			this.favorites = createFavoritesRootDocument();
		}
		return this.favorites; 	//this.favorites.asXML();
	}
	
	private Document createFavoritesRootDocument() {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("favorites");
		root.addAttribute("nextId", "1");
		root.addAttribute("name", NLT.get("favorites"));
		root.addAttribute("id", "0");
		root.addAttribute("image", "folder");
		root.addAttribute("displayOnly", "true");
		return doc;
	}
	
	public Document getFavoritesTree() {
		getFavorites();
		Document favTree = DocumentHelper.createDocument();
    	Element destRoot = favTree.addElement(DomTreeBuilder.NODE_ROOT);
    	destRoot.addAttribute("title", NLT.get("favorites"));
    	destRoot.addAttribute("image", "folder");
    	destRoot.addAttribute("id", "0");
    	destRoot.addAttribute("listStyle", "ss_bold");
    	Element srcRoot = this.favorites.getRootElement();
    	FavoritesTreeHelper treeHelper = new FavoritesTreeHelper();
    	treeHelper.setupDomElement(DomTreeBuilder.TYPE_FAVORITES, srcRoot, destRoot);
    	buildFavoritesDomTree(srcRoot, destRoot, treeHelper);
    	//favTree.asXML();
    	return favTree;
	}
	
	public Document getFavoritesTreeDelete() {
		getFavorites();
		Document favTreeDelete = DocumentHelper.createDocument();
    	Element destRoot = favTreeDelete.addElement(DomTreeBuilder.NODE_ROOT);
    	destRoot.addAttribute("title", NLT.get("favorites.delete"));
    	destRoot.addAttribute("image", "folder");
    	destRoot.addAttribute("id", "ss_delete");
    	destRoot.addAttribute("listStyle", "ss_sortable ss_bold");
    	destRoot.addAttribute("displayOnly", "true");
    	//favTree.asXML();
    	return favTreeDelete;
	}
	
	public List<Long> getFavoritesIdList() {
		getFavorites();
		List ids = new ArrayList();
    	Element favRoot = this.favorites.getRootElement();
    	Iterator itFavorites = favRoot.selectNodes("favorite").iterator();
       	while (itFavorites.hasNext()) {
       		Element srcFavorite = (Element) itFavorites.next();
       		String id = srcFavorite.attributeValue("id");
       		if (id != null) {
       			ids.add(Long.valueOf(id));
       		}
       	}
    	return ids;
	}
	
	public List<Long> getFavoritesBinderIdList() {
		getFavorites();
		List ids = new ArrayList();
    	Element favRoot = this.favorites.getRootElement();
    	Iterator itFavorites = favRoot.selectNodes("favorite").iterator();
       	while (itFavorites.hasNext()) {
       		Element srcFavorite = (Element) itFavorites.next();
       		if (srcFavorite.attributeValue("type", "").equals(Favorites.FAVORITE_BINDER)) {
       			String id = srcFavorite.attributeValue("value");
	       		if (id != null) {
	       			ids.add(Long.valueOf(id));
	       		}
       		}
       	}
    	return ids;
	}
	
	private void buildFavoritesDomTree(Element srcElement, Element destElement, 
			DomTreeBuilder treeHelper) {
       	Iterator itFavorites = srcElement.selectNodes("favorite|category").iterator();
       	while (itFavorites.hasNext()) {
       		Element srcFavorite = (Element) itFavorites.next();
       		Element destFavorite = destElement.addElement(DomTreeBuilder.NODE_CHILD);
       		treeHelper.setupDomElement(DomTreeBuilder.TYPE_FAVORITES, srcFavorite, destFavorite);
       		if (srcFavorite.getName().equals("category")) {
       			buildFavoritesDomTree(srcFavorite, destFavorite, treeHelper);
       		}
    	}
	}

	private class FavoritesTreeHelper implements DomTreeBuilder {

		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_FAVORITES) {return true;}
			return false;
		}
		public boolean supportsWorkspace() {return false;}
		public boolean supportsFavorites(){return true;}
		public boolean supportsPeople() {return false;}
		public String getPage() {return "";}
		public void setPage(String page) {}
		public List getTuple() {return new ArrayList(Arrays.asList("", ""));}
		public Element setupDomElement(int type, Object source, Element element) {
			if (type == DomTreeBuilder.TYPE_FAVORITES) {
				Element e = (Element)source;
				element.addAttribute("title", e.attributeValue("name", "???"));
				element.addAttribute("image", e.attributeValue("image", "page"));
				element.addAttribute("action", e.attributeValue("action", ""));
				//The id contains the favorite Id, the favorite type and the binderId
				//  ss_favorites_xxx_type_yyy where xxx=the favorite id and yyy=the binderId
				//  type is "b" for binder and "e" for entry
				String id = "ss_favorites_" + e.attributeValue("id");
				id += "_";
				if (e.attributeValue("type", "").equals(Favorites.FAVORITE_BINDER)) {
					id += "b_";
					id += e.attributeValue("value", "");
				} else if (e.attributeValue("type", "").equals(Favorites.FAVORITE_ENTRY)) {
					id += "e_";
					id += e.attributeValue("value", "");
				} else {
					id += "u_";
				}
				
				element.addAttribute("id", id);
				String parentId = "";
				Element parentNode = e.getParent();
				if (parentNode == null) {
					parentId = "0";
				} else {
					parentId = parentNode.attributeValue("id", "0");
				}
				if (e.getName().equals("category")) parentId = e.attributeValue("id");
				element.addAttribute("parentId", parentId);
				if (!e.attributeValue("displayOnly", "").equals("")) 
					element.addAttribute("displayOnly", e.attributeValue("displayOnly"));
				if (e.getName().equals("category")) {
					element.addAttribute("listStyle", "ss_sortable ss_bold");
				} else {
					element.addAttribute("listStyle", "ss_sortable");
				}
			} else return null;
			return element;
		}
	}

	public JSONArray getFavoritesTreeJson() {
		getFavorites();
		JSONArray favData = new JSONArray();
		Document favTree = DocumentHelper.createDocument();
    	Element srcRoot = this.favorites.getRootElement();
    	buildFavoritesJson(srcRoot, favData);
    	return favData;
	}
	
	private void buildFavoritesJson(Element srcElement, JSONArray favData) {
       	Iterator itFavorites = srcElement.selectNodes("favorite|category").iterator();
       	while (itFavorites.hasNext()) {
       		Element e = (Element) itFavorites.next();

       		Map map = new HashMap();
       		map.put("eletype", e.getName());
       		map.put("id", e.attributeValue("id"));
       		map.put("name", e.attributeValue("name"));
       		if (!e.attributeValue("hover", "").equals("")) 
				map.put("hover", e.attributeValue("hover"));
       		if (!e.attributeValue("type", "").equals("")) 
				map.put("type", e.attributeValue("type"));
       		if (!e.attributeValue("value", "").equals("")) 
				map.put("value", e.attributeValue("value"));
       		if (!e.attributeValue("action", "").equals("")) 
				map.put("action", e.attributeValue("action"));
       		favData.put(map);
       		if (e.getName().equals("category")) {
       			buildFavoritesJson(e, favData);
       		}
    	}
	}

	public List<Map> getFavoritesList() {
		List idList = new ArrayList();
		JSONArray favs = getFavoritesTreeJson();
		for (int i = 0; i < favs.length(); i++) {
			idList.add(favs.getJSONObject(i));
		}
		return idList;
	}
}
