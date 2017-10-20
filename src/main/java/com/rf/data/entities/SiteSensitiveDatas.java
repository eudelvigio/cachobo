package com.rf.data.entities;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.envers.Audited;

/**
 * En esta tabla se almacenan los datos sensibles del sitio, de momento únicamente la cadena de conexión del ftp para el camel
 * @author mortas
 */
@Entity
@Audited
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class SiteSensitiveDatas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XStreamOmitField
    private Integer id;

    @OneToOne
    private Site site;

    private String connectionStringFtp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getConnectionStringFtp() {
        return connectionStringFtp;
    }

    public void setConnectionStringFtp(String connectionStringFtp) {
        this.connectionStringFtp = connectionStringFtp;
    }

}
