package com.rf.data.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;


/**
 * Esta entidad sirve para almacenar los datos de Medias, los cuales sirven para generar los distintos servicios que tendrá el sitio
 * Guarda una lista de MediaXtras, los cuales sirven para almacenar normalmente la configuración de un elemento. Datos tales como el width que ocupará un elemento, el height, checks para configuraciones...
 * Guarda una lista de MediaDatas, los cuales sirven para aglutinar los diferentes elementos que dependen de un media. Podrían ser cada una de las slides que conforman un slider, lista de promociones...
 * Guarda una referencia a una lista de sitios a los que debe servir el media, que se usa en el momento de la publicación
 * Guarda una referencia a la ruta que tiene en el filemanager virtual
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "MEDIA_NAME_EXIST", columnNames = {"name"})})
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
    private Integer id;

    // @Version
    private Integer version;

    private String name;

    private String fileManagerRoute;

    @Lob
    private String definitionJson;

    public String getDefinitionJson() {
        return definitionJson;
    }

    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getFileManagerRoute() {
        return fileManagerRoute;
    }

    public void setFileManagerRoute(String fileManagerRoute) {
        this.fileManagerRoute = fileManagerRoute;
    }

    public ElementMetaData getEmd() {
        return emd;
    }

    public void setEmd(ElementMetaData emd) {
        this.emd = emd;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private ElementMetaData emd;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @CreationTimestamp
    private Date creation;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date deleted;

    @OneToMany(mappedBy = "media", targetEntity = MediaData.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<MediaData> mediaData = new ArrayList<MediaData>();

    public List<MediaData> getMediaData() {
        return mediaData;
    }

    public void setMediaData(List<MediaData> mediaData) {
        this.mediaData = mediaData;
    }

    @OneToMany(mappedBy = "media", targetEntity = MediaXtra.class, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaXtra> mediaXtra = new ArrayList<MediaXtra>();

    public List<MediaXtra> getMediaXtra() {
        return mediaXtra;
    }

    public void setMediaXtra(List<MediaXtra> mediaXtra) {
        this.mediaXtra = mediaXtra;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @Deprecated
    private Site site;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "medias_of_site", joinColumns = @JoinColumn(name = "media_id"), inverseJoinColumns = @JoinColumn(name = "site_id"))
    private List<Site> sitesOfMedia;

    public List<Site> getSitesOfMedia() {
        return sitesOfMedia;
    }

    public void setSitesOfMedia(List<Site> sitesOfMedia) {
        this.sitesOfMedia = sitesOfMedia;
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
    private Date updated;

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
