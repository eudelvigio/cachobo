package com.rf.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Esta entidad almacena un elemento
 * Tiene una lista de elementMetadata que describe de qué ficheros y/o valores está compuesto
 * También tiene una lista de dependientes, y de dependencias, para construir una aplicación
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class Element {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    //@Version
    private Integer version;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date creation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date updated;

    private String fileManagerRoute;

    public String getFileManagerRoute() {
        return fileManagerRoute;
    }

    public void setFileManagerRoute(String fileManagerRoute) {
        this.fileManagerRoute = fileManagerRoute;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "element_dependency_info", joinColumns = {
        @JoinColumn(name = "dependent_id")}, inverseJoinColumns = {
        @JoinColumn(name = "dependency_id")})
    private List<Element> dependencies;

    @ManyToMany(mappedBy = "dependencies")
    @JsonIgnore
    private List<Element> dependents;

    public List<Element> getDependents() {
        return dependents;
    }

    public void setDependents(List<Element> dependents) {
        this.dependents = dependents;
    }

    public List<Element> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Element> dependencies) {
        this.dependencies = dependencies;
    }

    @OneToMany(mappedBy = "element", targetEntity = ElementMetaData.class, fetch = FetchType.EAGER)
    private List<ElementMetaData> metaDatas = new ArrayList<ElementMetaData>();

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public List<ElementMetaData> getMetaDatas() {
        return metaDatas;
    }

    public void setMetaDatas(List<ElementMetaData> metaDatas) {
        this.metaDatas = metaDatas;
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
