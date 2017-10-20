package com.rf.data.entities.published_entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

/**
 * Esta entidad almacena una revisión de un media, de manera que queda congelado en el momento en que se publica.
 * Guarda una referencia a su media padre, así como del número de revisión desde el que se creó
 * Guarda también una referencia al publishedSite para el que se haya publicado. Es una diferencia con el Media normal, el cual guarda la referencia a 0-n sitios
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class PublishedMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Integer id;

    //@Version
    @JsonIgnore
    private Integer version;

    private String name;

    @JsonIgnore
    private String fileManagerRoute;

    @JsonIgnore
    private Integer parentMediaId;

    public Integer getParentMediaId() {
        return parentMediaId;
    }

    public void setParentMediaId(Integer parentMediaId) {
        this.parentMediaId = parentMediaId;
    }

    public String getFileManagerRoute() {
        return fileManagerRoute;
    }

    public void setFileManagerRoute(String fileManagerRoute) {
        this.fileManagerRoute = fileManagerRoute;
    }
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @CreationTimestamp
    @JsonIgnore
    private Date creation;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @JsonIgnore
    private Date deleted;

    @OneToMany(mappedBy = "media", targetEntity = PublishedMediaData.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PublishedMediaData> mediaData = new ArrayList<PublishedMediaData>();

    public List<PublishedMediaData> getMediaData() {
        return mediaData;
    }

    public void setMediaData(List<PublishedMediaData> mediaData) {
        this.mediaData = mediaData;
    }

    @OneToMany(mappedBy = "media", targetEntity = PublishedMediaXtra.class, cascade = CascadeType.ALL)
    private List<PublishedMediaXtra> mediaXtra = new ArrayList<PublishedMediaXtra>();

    public List<PublishedMediaXtra> getMediaXtra() {
        return mediaXtra;
    }

    public void setMediaXtra(List<PublishedMediaXtra> mediaXtra) {
        this.mediaXtra = mediaXtra;
    }

    @JsonIgnore
    private Integer revisionNumber;

    public Integer getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Integer revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonIgnore
    private PublishedSite site;

    public PublishedSite getSite() {
        return site;
    }

    public void setSite(PublishedSite site) {
        this.site = site;
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

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @UpdateTimestamp
    @JsonIgnore
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

}
