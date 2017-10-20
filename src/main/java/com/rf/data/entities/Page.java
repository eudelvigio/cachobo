package com.rf.data.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;

/**
 * Esta entidad almacena las páginas que tiene un sitio
 * Pese a la existencia de campos como css, js o content, no son usados 
 * Almacena una lista de pagemetadatas, que se generan normalmente al insertar algún elemento en el header o el footer, como librerías, sliders, iframe MT, los cuales guardan una referencia al elementmetadata desde el que fueron generados
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
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
    private Date creation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date updatation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date publication;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date despublication;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date elimination;

    @OneToMany(mappedBy = "page", targetEntity = PageMetadata.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<PageMetadata> metaDatas = new ArrayList<PageMetadata>();

    @ManyToOne
    @JoinColumn(name = "fkIdSite")
    @JsonIgnore
    private Site site;

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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
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

    public List<PageMetadata> getMetaDatas() {
        return metaDatas;
    }

    public void setMetaDatas(List<PageMetadata> metaDatas) {
        this.metaDatas = metaDatas;
    }

    public Date getUpdated() {
        return updatation;
    }

    public void setUpdated(Date updatation) {
        this.updatation = updatation;
    }

    @PrePersist
    protected void onCreate() {
        creation = DateTime.now().toDate();
    }

    @PreUpdate
    protected void onUpdate() {
        updatation = DateTime.now().toDate();
    }
}
