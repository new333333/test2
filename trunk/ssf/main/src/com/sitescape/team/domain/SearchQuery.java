/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.text.ParseException;

import com.sitescape.ef.dao.IXmlPersistence;

import org.jdom.Element;
/**
 * @author Janet McCann
 * 
 * Search expression
 *
 */
public class SearchQuery implements IXmlPersistence {
    protected static final String xmlTag = "searchQuery";
    private int type=CONTENT;
    	public static final int CONTENT= 1;
    	public static final int UNSEEN= 2;
    	public static final int DATE= 3;
    private int sortType=DEFAULTSORT;
    	public static final int DEFAULTSORT=1;
    	public static final int RELEVANCE=2;
    private Date createdBeforeDate;
    private Date createdAfterDate;
    private String searchText="";
    private Date searchDate;
    private int maxResults=1000;
    private int startPosition=0;
    private boolean advanced=false;
    private Set searchFields;
    private boolean satisfiedWhenNotFound=false;
    private boolean dirty=true;
    // TODO: customized options
    private Map options;
    public SearchQuery() {       
    }
    protected SearchQuery(SearchQuery orig) {
    }
    protected SearchQuery(Element sEle) {
        String val;
        Iterator iter;
        Element ele;
        List children;
        
        DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL, new Locale("en", "us"));
        type = Integer.parseInt(sEle.getAttributeValue("type"));
        maxResults = Integer.parseInt(sEle.getAttributeValue("maxResults"));
        startPosition = Integer.parseInt(sEle.getAttributeValue("startPosition"));
        searchText = sEle.getChildText("searchText");
        ele = sEle.getChild("createdBeforeDate"); 
        if (ele != null) {
            try {
                createdBeforeDate = df.parse(ele.getText());
            } catch (ParseException ex) {
                createdBeforeDate = null;
            }
    	}
        ele = sEle.getChild("createdAfterDate"); 
        if (ele != null) {
            try {
                createdBeforeDate = df.parse(ele.getText());
            } catch (ParseException ex) {
                createdBeforeDate = null;
            }
    	}
        ele = sEle.getChild("searchDate"); 
        if (ele != null) {
            try {
                searchDate = df.parse(ele.getText());
            } catch (ParseException ex) {
                createdBeforeDate = null;
            }
    	}
        children = sEle.getChildren(SearchField.xmlTag);
        iter = children.iterator();
        while (iter.hasNext()) {
            new SearchField((Element)iter.next());
        }
        //instantiated from XML - clean
        dirty=false;
    }
    public boolean isDirty() {
        return dirty;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
        dirty=true;
    }
    public String getSearchText() {
        return this.searchText;
    }
    public void setSearchText(String searchText) {
        dirty=true;
        this.searchText = searchText;
    }
    public Date getSearchDate() {
        return this.searchDate;
    }
    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }
    public int getMaxResults() {
        return this.maxResults;
    }
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    public int getStartPosition() {
        return this.startPosition;
    }
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }    
    public Date getCreatedBeforeDate() {
        return this.createdBeforeDate;
    }
    public void setCreatedBeforeDate(Date createdBeforeDate) {
        dirty=true;
        this.createdBeforeDate = createdBeforeDate;
    }
    public Date getCreatedAfterDate() {
        return createdAfterDate;
    }
    public void setCreatedAfterDate(Date createdAfterDate) {
        dirty=true;
        this.createdAfterDate = createdAfterDate;
    }
    public boolean isAdvanced() {
        return this.advanced;
    }
    public void setAdvanced(boolean advanced) {
        dirty=true;
        this.advanced = advanced;
    }
 
    public Set getSearchFields() {
        if (this.searchFields == null) return new HashSet();
        return new HashSet(this.searchFields);
    }
    public void removeSearchField(SearchField searchField) {
        if (searchFields == null) return;
        dirty=true;
        searchFields.remove(searchField);
    }
    public void removeAllSearchFields() {
        dirty=true;
        searchFields.clear();
    }
    public SearchField createSearchField() {
        return new SearchField();
    }
    public String encodeAsXmlString() {
        
        StringBuffer buf = new StringBuffer(256);
        
        buf.append("<" + xmlTag + " type=\"" + type + "\" maxResults=\"" + maxResults + "\" startPosition=\"" +
                startPosition + "\">");
        buf.append("<advanced>" + advanced + "</advanced>");
        if (createdAfterDate != null) {
            buf.append("<createdAfterDate>" + createdAfterDate + "</createdAfterDate>");
        }
        if (createdBeforeDate != null) {
            buf.append("<createdBeforeDate>" + createdBeforeDate + "</createdBeforeDate>");
        }
        if (searchText != null) {
            buf.append("<searchText>" + searchText + "</searchText>");
        }
        if (searchDate != null) {
            buf.append("<searchDate>" + searchDate + "</searchDate>");
        }
                       
        if (searchFields != null) {
            Iterator iter = searchFields.iterator();
            while (iter.hasNext()) {
                SearchField fld = (SearchField)iter.next();
                buf.append(fld.encodeAsXmlString());
            }
        }
        buf.append("/" + xmlTag + ">");
        return buf.toString();
    } 
    
    public class SearchField {
        private static final String xmlTag="searchField";
        private String name;
       //values to be OR'd
        private Set values;        
    
        private SearchField() {
            searchFields.add(this);
            dirty=true;
            
        }
        private SearchField(Element sEle) {
            name = sEle.getAttributeValue("name");
            List children = sEle.getChildren("fieldValue");
            Iterator iter = children.iterator();
            values = new HashSet();
            while (iter.hasNext()) {
                Element ele = (Element)iter.next();
                values.add(ele.getText());
            }
        }
        public String getName() {
            return this.name;
        }
        public void setName(String name) {
            dirty=true;
            this.name = name;
        }
        public Set getValues() {
            return this.values;
        }
        public void setValues(Set values) {
            this.values = values;
            dirty=true;
        }
        private String encodeAsXmlString() {
            StringBuffer buf = new StringBuffer(512);
            
            buf.append("<" + xmlTag + " name=\"" + name + "\">");
            if (values != null) {
                Iterator iter = values.iterator();
                while (iter.hasNext()) {
                    buf.append("<fieldValue>" + iter.next() + "</fieldValue>");
                }
            }
            buf.append("</" + xmlTag + ">");
            return buf.toString();
        }
    }    
}

