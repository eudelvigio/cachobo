package com.rf.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.rf.data.entities.filemanager.File;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumScope;
import com.rf.data.enums.EnumStorage;
import com.rf.data.enums.EnumWhere;
import java.util.Date;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.joda.time.DateTime;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad almacena cada uno de los metadatos que componen un elemento
 * Puede tener como valor una cadena de texto, y/o un fichero de la entidad del filemanager
 * También almacena información sobre qué tipo de fichero o valor se trata, de dónde debe ir colocado en la página
 * y de a qué elemento pertenece
 * El valor de scope no se utiliza
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class ElementMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Version
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "fkIdElement")
    @JsonIgnore
    private Element element;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_key")
    private EnumMetadata key;

    @Deprecated
    @Enumerated(EnumType.STRING)
    @Column(name = "element_scope")
    private EnumScope scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_storage")
    private EnumStorage storage;

    @Enumerated(EnumType.STRING)
    @Column(name = "element_where")
    private EnumWhere where;

    public EnumStorage getStorage() {
        return storage;
    }

    public void setStorage(EnumStorage storage) {
        this.storage = storage;
    }

    public EnumWhere getWhere() {
        return where;
    }

    public void setWhere(EnumWhere where) {
        this.where = where;
    }

    private String description;

    private Boolean isEditable;

    @OneToOne
    private File script;

    @Lob
    @Column(name = "element_value")
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public EnumMetadata getKey() {
        return key;
    }

    public void setKey(EnumMetadata key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public EnumScope getScope() {
        return scope;
    }

    public void setScope(EnumScope scope) {
        this.scope = scope;
    }

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
