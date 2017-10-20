package com.rf.data.enums;

/**
 * Keys para almacenar los datos del usuario de manera más ordenada
 * @author mortas
 */
public enum EnumConscientUserDataTypes {
    PARTICIPACION_PROMOCIONES(1, "ParticipacionPromociones"),
    JUEGOS_FAVORITOS(2, "JuegosFavoritos");

    private final Integer value;
    private final String description;

    EnumConscientUserDataTypes(Integer val, String description) {
        this.value = val;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getValue() {
        return this.value;
    }

}
