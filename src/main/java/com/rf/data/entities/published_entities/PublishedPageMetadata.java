package com.rf.data.entities.published_entities;

import com.rf.data.entities.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.rf.data.entities.filemanager.File;
import com.rf.data.enums.EnumMetadata;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad almacena los metadatos de publishedPages, generados en el momento de la publicación de un sitio
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class PublishedPageMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Version
    private Integer version;

    @Lob
    private String value;

    @ManyToOne
    private PublishedPage page;

    public PublishedPage getPage() {
        return page;
    }

    @OneToOne
    private File script;

    @OneToOne
    private ElementMetaData elementMetadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "page_key")
    private EnumMetadata key;

    public EnumMetadata getKey() {
        return key;
    }

    public void setKey(EnumMetadata key) {
        this.key = key;
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

    /*public Page getPage() {
		return page;
	}*/
    public void setPage(PublishedPage page) {
        this.page = page;
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
