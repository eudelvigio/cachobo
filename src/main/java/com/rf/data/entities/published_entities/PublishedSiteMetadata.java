package com.rf.data.entities.published_entities;

import com.rf.data.entities.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.rf.data.entities.filemanager.File;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumWhere;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad almacena los metadatos de los publishedSites, generados en el momento de la publicación de un sitio
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class PublishedSiteMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Version
    private Integer version;

    @Lob
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "site_key")
    private EnumMetadata key;

    public EnumMetadata getKey() {
        return key;
    }

    public void setKey(EnumMetadata key) {
        this.key = key;
    }

    @Column(name = "site_metadata_order")
    private Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
    @ManyToOne
    private PublishedSite site;

    @OneToOne
    private File script;

    @OneToOne
    private ElementMetaData elementMetadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "sitemetadata_where")
    private EnumWhere where;

    public EnumWhere getWhere() {
        return where;
    }

    public void setWhere(EnumWhere where) {
        this.where = where;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PublishedSite getSite() {
        return site;
    }

    public void setSite(PublishedSite site) {
        this.site = site;
    }

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public ElementMetaData getElementMetadata() {
        return elementMetadata;
    }

    public void setElementMetadata(ElementMetaData elementMetadata) {
        this.elementMetadata = elementMetadata;
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

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @UpdateTimestamp
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

}
