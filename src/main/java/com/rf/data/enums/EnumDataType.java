package com.rf.data.enums;

/**
 * Distintos tipos de datos que puede tener un mediaXtra o mediaDataXtra
 * @author mortas
 */
public enum EnumDataType {

    IMAGE(1, "Imagen"),
    DATE(2, "Fecha"),
    URL(3, "URL"),
    DATETIME(4, "Fecha y hora"),
    STRING(5, "Texto"),
    BOOLEAN(6, "Booleano"),
    ID(7, "Identificador único"),
    INTEGER(8, "Entero"),
    ORDER(9, "Entero que se usará de orden"),
    PRINCIPAL_IMAGE(10, "Como imagen, pero además se usará como base para mostrar en administración"),
    PRINCIPAL_NAME(11, "Como String, pero además se usará como base para usar en administración"),
    TAGS(11, "Pues categorías y taxonomías y tal"),
    RICH_STRING(12, "Texto"),
    VIDEO(13, "Vídeo"),
    PUBLICATION_DATE(14, "Fecha de publicación de un elemento"),
    EXPIRATION_DATE(15, "Fecha de expiración de un elemento"),
    DELETION_DATE(16, "Fecha de eliminación de un elemento"),
    PDF(17, "Fichero pdf"),
    JS(18, "Código JS"),
    CSS(19, "Código CSS"),
    SELECT(20, "Selector entre varios elementos");

    private final String description;
    private final Integer value;
    //private final Boolean canBeMultiple;

    EnumDataType(Integer val, String desc) {
        this.value = val;
        this.description = desc;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDescription() {
        return description;
    }

}
