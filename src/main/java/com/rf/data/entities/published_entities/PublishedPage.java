package com.rf.data.entities.published_entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

/**
 * Esta entidad almacena las publishedPages, generados en el momento de la publicación de un sitio
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class PublishedPage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Version
    private Integer version;

    private String name;
    private String route;

    @Lob
    private String content;

    @Lob
    private String js;

    @Lob
    private String css;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @CreationTimestamp
    private Date creation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @UpdateTimestamp
    private Date updatation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date publication;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date despublication;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date elimination;

    @OneToMany(mappedBy = "page", targetEntity = PublishedPageMetadata.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PublishedPageMetadata> metaDatas = new ArrayList<PublishedPageMetadata>();

    @ManyToOne
    @JoinColumn(name = "fkIdSite")
    @JsonIgnore
    private PublishedSite site;

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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Date getPublication() {
        return publication;
    }

    public void setPublication(Date publication) {
        this.publication = publication;
    }

    public Date getDespublication() {
        return despublication;
    }

    public void setDespublication(Date despublication) {
        this.despublication = despublication;
    }

    public Date getElimination() {
        return elimination;
    }

    public void setElimination(Date elimination) {
        this.elimination = elimination;
    }

    public PublishedSite getSite() {
        return site;
    }

    public void setSite(PublishedSite site) {
        this.site = site;
    }

    public Date getUpdatation() {
        return updatation;
    }

    public void setUpdatation(Date updatation) {
        this.updatation = updatation;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public List<PublishedPageMetadata> getMetaDatas() {
        return metaDatas;
    }

    public void setMetaDatas(List<PublishedPageMetadata> metaDatas) {
        for (PublishedPageMetadata ppmd : metaDatas) {
            ppmd.setPage(this);
        }
        this.metaDatas = metaDatas;
    }

    public Date getUpdated() {
        return updatation;
    }

    public void setUpdated(Date updatation) {
        this.updatation = updatation;
    }
}
