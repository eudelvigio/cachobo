package com.rf.data.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Enumerado que representa las posibles localizaciones que puede tener un metadato, ya sea de elementMetadata, pageMetadata o siteMetadata
 * @author mortas
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumWhere {
    LIBRARY(1, "El fichero será añadido a la web en la zona de librerías, la primera tras el <head>"),
    SCRIPT(2, "El fichero será añadido a la web en la zona de scripts, antes del cierre del <body>"),
    INLINE(3, "El contenido del fichero será incluido dentro del html en la zona dónde se vaya a insertar"),
    FOOTER(4, "El fichero será incluido en el footer haciendo uso de ng include"),
    HEADER(5, "El fichero será incluido en el header haciendo uso de ng include"),
    VIEW(5, "El fichero será una vista");

    private final String description;
    private final Integer value;

    public String getDescription() {
        return description;
    }

    public Integer getValue() {
        return value;
    }

    EnumWhere(Integer val, String desc) {
        this.value = val;
        this.description = desc;
    }
}
