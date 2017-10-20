package com.rf.data.enums;

/**
 * No ha sido usado nunca, deprecado
 * @author mortas
 * @deprecated
 */
@Deprecated
public enum EnumPublishedStatus {
    NOT_APPROVED(new Integer(1), "SIN_APROBAR"),
    APPROVED(new Integer(2), "APROBADO");

    private final Integer value;
    private final String description;

    EnumPublishedStatus(Integer val, String description) {
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
