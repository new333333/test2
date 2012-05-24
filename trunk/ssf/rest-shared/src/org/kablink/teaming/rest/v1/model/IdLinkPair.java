package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: david
 * Date: 5/23/12
 * Time: 4:45 PM
 */
@XmlRootElement
public class IdLinkPair {
    private Long id;
    private String link;

    public IdLinkPair() {
    }

    public IdLinkPair(Long id, String link) {
        this.id = id;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name="href")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
