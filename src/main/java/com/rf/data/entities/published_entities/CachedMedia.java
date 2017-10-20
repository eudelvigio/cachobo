package com.rf.data.entities.published_entities;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Esta entidad almacena la instantánea de los servicios de un sitio en el momento en que se publican
 * @author mortas
 */
@Entity
public class CachedMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @Lob
    private String fullData;

    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    public Integer getRevNumber() {
        return revNumber;
    }

    public void setRevNumber(Integer revNumber) {
        this.revNumber = revNumber;
    }

    private Integer revNumber;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullData() {
        return fullData;
    }

    public void setFullData(String fullData) {
        this.fullData = fullData;
    }
}
