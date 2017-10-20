package com.rf.data.entities.published_entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad almacena una instantánea de una revisión de un sitio, que se publica de manera que sea el referente a la hora del envío por ftp
 * Almacena una referencia al sitio desde el que se generó el publishedSite, así como al número de revisión
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class PublishedSite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Version
    private Integer version;

    private String name;
    private String domain;

    @OneToMany(mappedBy = "site", targetEntity = PublishedPage.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PublishedPage> pages = new ArrayList<PublishedPage>();

    private Boolean has_header;
    private Boolean has_footer;

    @OneToMany(mappedBy = "site", targetEntity = PublishedSiteMetadata.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PublishedSiteMetadata> siteMetadatas = new ArrayList<PublishedSiteMetadata>();

    private Integer revisionNumber;

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    private Integer parentSiteId;

    public Integer getParentSiteId() {
        return parentSiteId;
    }

    public void setParentSiteId(Integer parentSiteId) {
        this.parentSiteId = parentSiteId;
    }

    @Lob
    private String global_css;

    @UpdateTimestamp
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<PublishedPage> getPages() {
        return pages;
    }

    public void setPages(List<PublishedPage> pages) {
        for (PublishedPage p : pages) {
            p.setSite(this);
        }
        this.pages = pages;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getHas_header() {
        return has_header;
    }

    public void setHas_header(Boolean has_header) {
        this.has_header = has_header;
    }

    public List<PublishedSiteMetadata> getSiteMetadatas() {
        return siteMetadatas;
    }

    public void setSiteMetadatas(List<PublishedSiteMetadata> siteMetadatas) {
        for (PublishedSiteMetadata smd : siteMetadatas) {
            smd.setSite(this);
        }
        this.siteMetadatas = siteMetadatas;
    }

    public Boolean getHas_footer() {
        return has_footer;
    }

    public void setHas_footer(Boolean has_footer) {
        this.has_footer = has_footer;
    }

    public String getGlobal_css() {
        return global_css;
    }

    public void setGlobal_css(String global_css) {
        this.global_css = global_css;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @CreationTimestamp
    private Date creation;

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

}
