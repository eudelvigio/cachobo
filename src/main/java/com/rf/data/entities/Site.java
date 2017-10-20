package com.rf.data.entities;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta tabla guarda un sitio
 * Almacena el valor del dominio base del sitio, y el nombre del mismo
 * Almacena una lista de páginas que compondrán la estructura del sitio
 * Almacena una lista de sitemetadatas, que se generan normalmente al insertar algún elemento en el header o el footer, como librerías, sliders, iframe MT, los cuales guardan una referencia al elementmetadata desde el que fueron generados
 * La CSS global del sitio, el header y el footer se almacenan en un sitemetadata especial
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
    private Integer id;

    //@Version
    private Integer version;

    private String name;
    private String domain;

    @OneToMany(mappedBy = "site", targetEntity = Page.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Page> pages = new ArrayList<Page>();

    @Deprecated
    private Boolean has_header;
    
    @Deprecated
    private Boolean has_footer;

    @OneToMany(mappedBy = "site", targetEntity = SiteMetadata.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<SiteMetadata> siteMetadatas = new ArrayList<SiteMetadata>();

    @Deprecated
    @Lob
    private String global_css;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "sitesOfMedia")
    private List<Media> mediasOfSite;

    public List<Media> getMediasOfSite() {
        return mediasOfSite;
    }

    public void setMediasOfSite(List<Media> mediasOfSite) {
        this.mediasOfSite = mediasOfSite;
    }

    @UpdateTimestamp
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
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

    public List<SiteMetadata> getSiteMetadatas() {
        return siteMetadatas;
    }

    public void setSiteMetadatas(List<SiteMetadata> siteMetadatas) {
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
    private Date creation;

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    @PrePersist
    protected void onCreate() {
        creation = DateTime.now().toDate();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = DateTime.now().toDate();
    }
}
