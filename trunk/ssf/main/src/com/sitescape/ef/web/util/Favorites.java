package com.sitescape.ef.web.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.util.NLT;
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
	
	public Document addFavorite(String name, String type, String value, String categoryId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		int id = Integer.parseInt((String)root.attributeValue("nextId"));
		Element newFavorite = null;
		if (categoryId.equals("")) {
			newFavorite = root.addElement("favorite");
		} else {
			Element category = (Element)root.selectSingleNode("//category[@id='"+categoryId+"']");
			if (category == null) category = root;
			newFavorite = category.addElement("favorite");
		}
		newFavorite.addAttribute("id", String.valueOf(id));
		newFavorite.addAttribute("name", name);
		newFavorite.addAttribute("type", type);
		newFavorite.addAttribute("value", value);
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
	
	public Document saveOrder(String movedItemId, String newItemOrder) {
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
		public Element setupDomElement(String type, Object source, Element element) {
			if (type.equals(DomTreeBuilder.TYPE_FAVORITES)) {
				Element e = (Element)source;
				element.addAttribute("title", e.attributeValue("name", "???"));
				element.addAttribute("image", e.attributeValue("image", "page"));
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

}
