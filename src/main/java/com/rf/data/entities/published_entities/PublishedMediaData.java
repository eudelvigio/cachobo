/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.data.entities.published_entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad almacena los publishedMediaDatas generados al publicar un Media
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class PublishedMediaData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Integer id;

    //@Version
    @JsonIgnore
    private Integer version;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @CreationTimestamp
    @JsonIgnore
    private Date created;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date publication;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date expiration;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date deletion;

    @JsonIgnore
    private String objValue;
    @JsonIgnore
    private String objAction;

    @OneToMany(mappedBy = "mediaData", targetEntity = PublishedMediaDataXtra.class, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private List<PublishedMediaDataXtra> mediaDataXtra = new ArrayList<PublishedMediaDataXtra>();

    public List<PublishedMediaDataXtra> getMediaDataXtra() {
        return mediaDataXtra;
    }

    public void setMediaDataXtra(List<PublishedMediaDataXtra> mediaDataXtra) {
        this.mediaDataXtra = mediaDataXtra;
    }

    private Boolean isDefaultMediaData;

    public Boolean getIsDefaultMediaData() {
        return isDefaultMediaData;
    }

    public void setIsDefaultMediaData(Boolean isDefaultMediaData) {
        this.isDefaultMediaData = isDefaultMediaData;
    }

    @ManyToOne
    @JsonIgnore
    private PublishedMedia media;

    public PublishedMedia getMedia() {
        return media;
    }

    public void setMedia(PublishedMedia media) {
        this.media = media;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getPublication() {
        return publication;
    }

    public void setPublication(Date publication) {
        this.publication = publication;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getDeletion() {
        return deletion;
    }

    public void setDeletion(Date deletion) {
        this.deletion = deletion;
    }

    public String getObjValue() {
        return objValue;
    }

    public void setObjValue(String objValue) {
        this.objValue = objValue;
    }

    public String getObjAction() {
        return objAction;
    }

    public void setObjAction(String objAction) {
        this.objAction = objAction;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonIgnore
    @UpdateTimestamp
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
