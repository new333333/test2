package com.sitescape.ef.web.util;

import java.util.Iterator;

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
		newCategory.addAttribute("listStyle", "ss_sortableListCircle");
		root.addAttribute("nextId", String.valueOf(++categoryId));
		
		return this.favorites;
	}
	
	public Document moveFavorite(String id, String targetCategoryId) {
		getFavorites();
		Element root = this.favorites.getRootElement();
		Element favorite = (Element)root.selectSingleNode("//favorite[@id='"+id+"']");
		if (favorite != null) {
			Element favoriteParent = favorite.getParent();
			Element newParentCategory = null;
			if (targetCategoryId.equals("")) {
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
			Element newParentCategory = (Element)root.selectSingleNode("//category[@id='"+targetCategoryId+"']");
			if (newParentCategory != null && newParentCategory != category && 
					category.selectSingleNode("//category[@id='"+targetCategoryId+"']") == null) {
				if (categoryParent.remove(category)) {
					newParentCategory.add(category);
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
			this.favorites = DocumentHelper.createDocument();
			Element root = this.favorites.addElement("favorites");
			root.addAttribute("nextId", "1");
			root.addAttribute("name", NLT.get("favorites"));
			root.addAttribute("id", "0");
			root.addAttribute("image", "folder");
			root.addAttribute("displayOnly", "true");
			root.addAttribute("listStyle", "ss_sortableListCircle");
		}
		return this.favorites;
	}
	
	public Document getFavoritesTree() {
		getFavorites();
		Document favTree = DocumentHelper.createDocument();
    	Element destRoot = favTree.addElement(DomTreeBuilder.NODE_ROOT);
    	destRoot.addAttribute("title", NLT.get("favorites"));
    	destRoot.addAttribute("image", "folder");
    	destRoot.addAttribute("id", "0");
    	Element srcRoot = this.favorites.getRootElement();
    	FavoritesTreeHelper treeHelper = new FavoritesTreeHelper();
    	treeHelper.setupDomElement(DomTreeBuilder.TYPE_FAVORITES, srcRoot, destRoot);
    	buildFavoritesDomTree(srcRoot, destRoot, treeHelper);
    	//favTree.asXML();
    	return favTree;
	}
	
	private void buildFavoritesDomTree(Element srcElement, Element destElement, 
			DomTreeBuilder treeHelper) {
       	Iterator itFavorites = srcElement.selectNodes("favorite").iterator();
       	while (itFavorites.hasNext()) {
       		Element srcFavorite = (Element) itFavorites.next();
       		Element destFavorite = destElement.addElement(DomTreeBuilder.NODE_CHILD);
       		treeHelper.setupDomElement(DomTreeBuilder.TYPE_FAVORITES, srcFavorite, destFavorite);
       	}
       	Iterator itCategories = srcElement.selectNodes("category").iterator();
       	while (itCategories.hasNext()) {
       		Element srcCategory = (Element) itCategories.next();
       		Element destCategory = destElement.addElement(DomTreeBuilder.NODE_CHILD);
       		treeHelper.setupDomElement(DomTreeBuilder.TYPE_FAVORITES, srcCategory, destCategory);
       		buildFavoritesDomTree(srcCategory, destCategory, treeHelper);
       	}
	}

	private class FavoritesTreeHelper implements DomTreeBuilder {
		public Element setupDomElement(String type, Object source, Element element) {
			if (type.equals(DomTreeBuilder.TYPE_FAVORITES)) {
				Element e = (Element)source;
				element.addAttribute("title", e.attributeValue("name", "???"));
				element.addAttribute("image", e.attributeValue("image", "page"));
				element.addAttribute("id", e.attributeValue("id"));
				if (!e.attributeValue("displayOnly", "").equals("")) 
					element.addAttribute("displayOnly", e.attributeValue("displayOnly"));
				if (!e.attributeValue("listStyle", "").equals("")) 
					element.addAttribute("listStyle", e.attributeValue("listStyle"));
			} else return null;
			return element;
		}
	}

}
