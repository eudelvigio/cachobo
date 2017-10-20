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
 * Esta entidad almacena los mediaXtras, que son una serie de datos que puede tener un media
 * El tipo de dato que representa se almacena en el campo xtraType
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class MediaXtra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
    private Integer id;
    //@Version
    private Integer version;
    private String xtraKey;

    @Lob
    private String xtraValue;

    @Enumerated(EnumType.STRING)
    private EnumDataType xtraType;

    public EnumDataType getXtraType() {
        return xtraType;
    }

    public void setXtraType(EnumDataType xtraType) {
        this.xtraType = xtraType;
    }
    private String xtraDescription;

    public String getXtraDescription() {
        return xtraDescription;
    }

    public void setXtraDescription(String xtraDescription) {
        this.xtraDescription = xtraDescription;
    }

    @ManyToOne
    @JsonIgnore
    private Media media;

    public Media getMedia() {
        return media;
    }

    private Boolean isDefaultXtraData;

    public Boolean getIsDefaultXtraData() {
        return isDefaultXtraData;
    }

    public void setIsDefaultXtraData(Boolean isDefaultXtraData) {
        this.isDefaultXtraData = isDefaultXtraData;
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

    public void setMedia(Media media) {
        this.media = media;
    }

}
