package com.rf.data.entities.userdata;

import com.rf.data.enums.EnumConscientUserDataTypes;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

/**
 * Esta entidad sirve para almacenar, en formato clave-valor datos que pueda generar un usuario
 * Podría ser el apuntado a una promoción, la lista de juegos favoritos...
 * @author mortas
 */
@Entity
@Audited
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "KEY_ALREADY_EXIST_FOR_USER", columnNames = {"user_id", "kv_key"})})
public class ConscientUserData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    @Column(length = 64, name = "user_id")
    private String userId;

    @Column(length = 128, name = "kv_key")
    @NotNull
    private String key;

    @Column(length = 4096, name = "kv_value")
    private String value;

    private Date creation;

    private Date updated;

    @Enumerated(EnumType.STRING)
    private EnumConscientUserDataTypes conscientUserDataType;

    private String site;

    public EnumConscientUserDataTypes getConscientUserDataType() {
        return conscientUserDataType;
    }

    public void setConscientUserDataType(EnumConscientUserDataTypes conscientUserDataType) {
        this.conscientUserDataType = conscientUserDataType;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    @Column(name = "not_important", nullable = false, columnDefinition = "bit default 0")
    private Boolean notImportant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Boolean getNotImportant() {
        return notImportant;
    }

    public void setNotImportant(Boolean notImportant) {
        this.notImportant = notImportant;
    }

}
