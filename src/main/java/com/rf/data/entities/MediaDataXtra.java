package com.rf.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rf.data.enums.EnumDataType;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;

/**
 * Esta entidad almacena cada una de las propiedades que componen un mediadata
 * El tipo de dato que tiene el mediadataxtra se almacena en el campo xtraType, que es un enumerado
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class MediaDataXtra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
    private Integer id;
    //@Version
    private Integer version;
    private String xtraKey;

    @Lob
    private String xtraValue;

    private String xtraDescription;

    public String getXtraDescription() {
        return xtraDescription;
    }

    public void setXtraDescription(String xtraDescription) {
        this.xtraDescription = xtraDescription;
    }

    @Enumerated(EnumType.STRING)
    private EnumDataType xtraType;

    public EnumDataType getXtraType() {
        return xtraType;
    }

    public void setXtraType(EnumDataType xtraType) {
        this.xtraType = xtraType;
    }

    @ManyToOne
    @JsonIgnore
    private MediaData mediaData;

    private Boolean isDefaultXtraData;

    public Boolean getIsDefaultXtraData() {
        return isDefaultXtraData;
    }

    public void setIsDefaultXtraData(Boolean isDefaultXtraData) {
        this.isDefaultXtraData = isDefaultXtraData;
    }

    public MediaData getMediaData() {
        return mediaData;
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

    public String getXtraKey() {
        return xtraKey;
    }

    public void setXtraKey(String xtraKey) {
        this.xtraKey = xtraKey;
    }

    public String getXtraValue() {
        return xtraValue;
    }

    public void setXtraValue(String xtraValue) {
        this.xtraValue = xtraValue;
    }

    public void setMediaData(MediaData mediaData) {
        this.mediaData = mediaData;
    }

}
