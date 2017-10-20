package com.rf.data.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Enumerado que representa las distintas maneras que tiene un elementMetadata de almacenar su valor
 * @author mortas
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumStorage {
    FILEMANAGER_NOT_CREATE(1, "El fichero será copiado del existente en el filemanager integrado"),
    FILEMANAGER_CREATE(2, "El fichero será creado en el filemanager automáticamente a partir del value insertado"),
    EXTERNAL(3, "El fichero será insertado en la página con la url proporcionada"),
    INLINE(4, "El texto del value será insertado a pelo en la página");

    private final String description;
    private final Integer value;

    public String getDescription() {
        return description;
    }

    public Integer getValue() {
        return value;
    }

    EnumStorage(Integer val, String desc) {
        this.value = val;
        this.description = desc;
    }
}
