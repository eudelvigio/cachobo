
package com.rf.data.entities.live_config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rf.data.entities.Site;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Esta entidad guarda los liveconfigs, datos de configuración del sitio que se sirven desde wanaba de manera instantánea. Cortinillas
 * @author mortas
 */
@Entity
public class LiveConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(length = 128, name = "kv_key")
    @NotNull
    private String key;

    @Column(length = 4096, name = "kv_value")
    private String value;

    @OneToOne(fetch = FetchType.LAZY)
    private Site site;

    @Transient
    private Integer site_id;

    //@JsonIgnore
    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

}
