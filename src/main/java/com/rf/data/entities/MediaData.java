/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad sirve para almacenar los datos de MediaData, los cuales dependen de un media y sirven para almacenar la lista de elementos que suele componer un servicio
 * Guarda datos de publicación, despublicación, expiración y ordenación, por considerarse comunes a las listas en un servicio
 * Guarda una lista de MediaDataXtras, que almacenan los datos que componen el mediadata
 * Guarda un flag sobre si es el mediaData Default, del cual se cogerá el modelo de datos para crear el resto de la colección
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class MediaData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
    private Integer id;

    //@Version
    private Integer version;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date created;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date publication;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date expiration;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date deletion;

    private String objValue;
    private String objAction;

    @Column(name = "mediadata_order")
    private Integer orderOfMediaData;

    public Integer getOrderOfMediaData() {
        return orderOfMediaData;
    }

    public void setOrderOfMediaData(Integer orderOfMediaData) {
        this.orderOfMediaData = orderOfMediaData;
    }

    @OneToMany(mappedBy = "mediaData", targetEntity = MediaDataXtra.class, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private List<MediaDataXtra> mediaDataXtra = new ArrayList<MediaDataXtra>();

    public List<MediaDataXtra> getMediaDataXtra() {
        return mediaDataXtra;
    }

    public void setMediaDataXtra(List<MediaDataXtra> mediaDataXtra) {
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
    private Media media;

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
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
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @PrePersist
    protected void onCreate() {
        created = DateTime.now().toDate();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = DateTime.now().toDate();
    }
}
