package com.rf.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.rf.data.entities.filemanager.File;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumWhere;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * Esta entidad almacena los metadatos que tiene un sitio
 * Normalmente se crea a partir de la inserción de un elemento en el header o el footer, lo cual provoca que se copie el valor resultante de ciertos metadatos del elemento a los sitemetadatas, y se almacena una referencia al elementmetadata desde el que se generó
 * Dispone del campo key para almacenar la vista para el header, el footer o la css, lo cual representa un sitemetadata sin correlación con ningún elementmetadata
 * Dispone del campo where para almacenar, en caso de header o footer, si es el header o el footer, lo cual representa un sitemetadata sin correlación con ningún elementmetadata
 * El valor guardado en el campo value siempre es una url
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class SiteMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
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
    @JsonIgnore
    private Site site;

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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
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
    private Date creation;

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
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
        creation = DateTime.now().toDate();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = DateTime.now().toDate();
    }

}
